package com.learningmachine.android.app.ui.home;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.learningmachine.android.app.R;
import com.learningmachine.android.app.data.CertificateManager;
import com.learningmachine.android.app.data.IssuerManager;
import com.learningmachine.android.app.data.inject.Injector;
import com.learningmachine.android.app.data.model.IssuerRecord;
import com.learningmachine.android.app.data.preferences.SharedPreferencesManager;
import com.learningmachine.android.app.databinding.FragmentHomeBinding;
import com.learningmachine.android.app.databinding.ListIssuerHeaderBinding;
import com.learningmachine.android.app.databinding.ListItemIssuerBinding;
import com.learningmachine.android.app.ui.LMFragment;
import com.learningmachine.android.app.ui.LMIssuerBaseFragment;
import com.learningmachine.android.app.ui.cert.CertificateActivity;
import com.learningmachine.android.app.ui.issuer.AddIssuerActivity;
import com.learningmachine.android.app.ui.settings.SettingsActivity;
import com.learningmachine.android.app.util.DialogUtils;
import com.learningmachine.android.app.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import timber.log.Timber;

public class HomeFragment extends LMIssuerBaseFragment {

    @Inject CertificateManager mCertificateManager;
    @Inject SharedPreferencesManager mSharedPreferencesManager;

    private FragmentHomeBinding mBinding;
    private List<IssuerRecord> mIssuerList;

    public static HomeFragment newInstance() {
        return new HomeFragment();
    }

    public static HomeFragment newInstanceForIssuer(String issuerUrlString, String nonce) {
        Bundle args = new Bundle();
        args.putString(ARG_ISSUER_URL, issuerUrlString);
        args.putString(ARG_ISSUER_NONCE, nonce);
        args.putString(ARG_LINK_TYPE, ARG_LINK_TYPE_ISSUER);

        HomeFragment fragment = new HomeFragment();
        fragment.setArguments(args);

        return fragment;
    }

    public static HomeFragment newInstanceForCert(String certUrl) {
        Bundle args = new Bundle();
        args.putString(ARG_CERT_URL, certUrl);
        args.putString(ARG_LINK_TYPE, ARG_LINK_TYPE_CERT);

        HomeFragment fragment = new HomeFragment();
        fragment.setArguments(args);

        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        Injector.obtain(getContext())
                .inject(this);

        mIssuerList = new ArrayList<>();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_home, container, false);

        handleArgs();

        if (ARG_LINK_TYPE_ISSUER.equals(mLinkType) && !StringUtils.isEmpty(super.mIntroUrl) && !StringUtils.isEmpty(super.mNounce)) {
            startIssuerIntroduction();
        } else if (ARG_LINK_TYPE_CERT.equals(mLinkType) && !StringUtils.isEmpty(super.mCertUrl)) {
            addCertificate();
        }

        setupRecyclerView();

        return mBinding.getRoot();
    }

    @Override
    public void onResume() {
        super.onResume();

        mIssuerManager.getIssuers()
                .compose(bindToMainThread())
                .subscribe(this::updateRecyclerView, throwable -> Timber.e(throwable, "Unable to load issuers"));
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.fragment_home, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.fragment_home_settings_menu_item:
                Timber.i("Settings button tapped");
                Intent intent = SettingsActivity.newIntent(getContext());
                startActivity(intent);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void setupRecyclerView() {
        IssuerAdapter adapter = new IssuerAdapter(mIssuerList);
        mBinding.issuerRecyclerview.setAdapter(adapter);

        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);

        mBinding.issuerRecyclerview.setLayoutManager(layoutManager);
    }


    private int totalIssuersCertificateCountCalculated = 0;
    private void updateRecyclerView(List<IssuerRecord> issuerList) {
        mIssuerList.clear();
        mIssuerList.addAll(issuerList);

        Timber.d("Managed issuers list url: " + issuerList.toString());


        if (mSharedPreferencesManager.wasReturnUser()) {
            mBinding.imageView2.setVisibility(View.GONE);
            mBinding.issuerNameTitle.setVisibility(View.GONE);

            mBinding.imageView3.setVisibility(View.VISIBLE);
            mBinding.credentialNameTitleContainer.setVisibility(View.VISIBLE);


            mBinding.onboardingHomeNoIssuersTitle.setText(R.string.onboarding_home_no_issuers_title_returning_user);

            mBinding.onboardingHomeNoIssuersDesc.setText(R.string.onboarding_home_no_issuers_desc_returning_user);
        } else {
            mBinding.imageView2.setVisibility(View.VISIBLE);
            mBinding.issuerNameTitle.setVisibility(View.VISIBLE);

            mBinding.imageView3.setVisibility(View.GONE);
            mBinding.credentialNameTitleContainer.setVisibility(View.GONE);
            mBinding.onboardingHomeNoIssuersDesc.setText(R.string.onboarding_home_no_issuers_desc_new_user);
        }

        if (issuerList.isEmpty()) {
            mBinding.issuerRecyclerview.getAdapter()
                    .notifyDataSetChanged();
            mBinding.issuerMainContent.setVisibility(View.GONE);
            mBinding.issuerEmptyContent.setVisibility(View.VISIBLE);
        }

        // calculate the number of certificates per issuer
        totalIssuersCertificateCountCalculated = mIssuerList.size();
        for(IssuerRecord record : mIssuerList) {
            mCertificateManager.getCertificatesForIssuer(record.getUuid())
                    .compose(bindToMainThread())
                    .subscribe((certificateRecords) -> {
                            record.cachedNumberOfCertificatesForIssuer = certificateRecords.size();

                        totalIssuersCertificateCountCalculated--;
                        if(totalIssuersCertificateCountCalculated <= 0) {
                            mBinding.issuerRecyclerview.getAdapter()
                                    .notifyDataSetChanged();

                            mBinding.issuerMainContent.setVisibility(issuerList.isEmpty() ? View.GONE : View.VISIBLE);
                            mBinding.issuerEmptyContent.setVisibility(issuerList.isEmpty() ? View.VISIBLE : View.GONE);
                        }

                    }, throwable -> Timber.e(throwable, "Unable to load certificates"));
        }
    }

    private class IssuerAdapter extends RecyclerView.Adapter {

        private List<IssuerRecord> mIssuerList;

        IssuerAdapter(List<IssuerRecord> issuerList) {
            mIssuerList = issuerList;
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            if(viewType == 0) {
                Context context = parent.getContext();
                LayoutInflater inflater = LayoutInflater.from(context);
                ListIssuerHeaderBinding binding = DataBindingUtil.inflate(inflater,
                        R.layout.list_issuer_header,
                        parent,
                        false);
                return new GenericViewHolder(binding);
            }

            if(viewType == 1) {
                Context context = parent.getContext();
                LayoutInflater inflater = LayoutInflater.from(context);
                ListItemIssuerBinding binding = DataBindingUtil.inflate(inflater, R.layout.list_item_issuer, parent, false);

                float height = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 150, getResources().getDisplayMetrics());

                IssuerViewHolder holder = new IssuerViewHolder(binding);
                holder.itemView.setLayoutParams(new RecyclerView.LayoutParams(mBinding.issuerRecyclerview.getWidth(), (int) height));
                return holder;
            }

            return null;
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            if(position > 0) {
                IssuerRecord issuer = mIssuerList.get(position - 1);
                ((IssuerViewHolder)holder).bind(issuer);
            }
        }

        @Override
        public int getItemViewType(int position) {
            if(position == 0) {
                return 0;
            }
            return 1;
        }

        @Override
        public int getItemCount() {
            if(mIssuerList.size() == 0) {
                return 0;
            }
            return mIssuerList.size() + 1;
        }
    }

    public void updateArgsIssuer(String issuerUrlString, String issuerNonce) {
        if (!StringUtils.isEmpty(issuerUrlString)) {
            mIntroUrl = issuerUrlString;
        }
        if (!StringUtils.isEmpty(issuerNonce)) {
            mNounce = issuerNonce;
        }
        startIssuerIntroduction();
    }

    public void updateArgsCert(String certUrl) {
        if (!StringUtils.isEmpty(certUrl)) {
            mCertUrl = certUrl;
        }

        addCertificate();
    }

    private void addCertificate() {
        displayProgressDialog(R.string.fragment_add_certificate_progress_dialog_message);
        checkVersion(updateNeeded -> {
            if (!updateNeeded) {
                mCertificateManager.addCertificate(mCertUrl)
                        .compose(bindToMainThread())
                        .subscribe(uuid -> {
                            Timber.d("Added certificate from home screen.");
                            hideProgressDialog();
                            Intent intent = CertificateActivity.newIntent(getContext(), uuid);
                            startActivity(intent);
                        }, throwable -> displayErrors(throwable, DialogUtils.ErrorCategory.CERTIFICATE, R.string.error_title_message));
            } else {
                hideProgressDialog();
            }
        });
    }

    @Override
    protected void addIssuerOnSubscribe() {
        //Nothing to do here
    }

    @Override
    protected void addIssuerOnCompleted() {
        //Nothing to do here
    }

    @Override
    protected void addIssuerOnError() {
        //Nothing to do here
    }

    @Override
    protected void addIssuerOnIssuerAdded(String uuid) {
        hideProgressDialog();
        mIssuerManager.getIssuers()
                .compose(bindToMainThread())
                .subscribe(this::updateRecyclerView, throwable -> Timber.e(throwable, "Unable to load issuers"));
    }
}
