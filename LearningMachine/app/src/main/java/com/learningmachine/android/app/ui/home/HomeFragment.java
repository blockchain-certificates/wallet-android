package com.learningmachine.android.app.ui.home;

import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.learningmachine.android.app.R;
import com.learningmachine.android.app.data.IssuerManager;
import com.learningmachine.android.app.data.inject.Injector;
import com.learningmachine.android.app.data.model.Issuer;
import com.learningmachine.android.app.databinding.FragmentHomeBinding;
import com.learningmachine.android.app.databinding.ListItemIssuerBinding;
import com.learningmachine.android.app.ui.LMFragment;
import com.learningmachine.android.app.ui.issuer.AddIssuerActivity;
import com.learningmachine.android.app.ui.settings.SettingsActivity;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import timber.log.Timber;

public class HomeFragment extends LMFragment {

    @Inject IssuerManager mIssuerManager;

    private FragmentHomeBinding mBinding;

    public static HomeFragment newInstance() {
        return new HomeFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        Injector.obtain(getContext())
                .inject(this);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_home, container, false);

        mIssuerManager.getIssuers()
                .compose(bindToMainThread())
                .subscribe(this::setupRecyclerView, throwable -> {
                    Timber.e(throwable, "Unable to load issuers");
                });

        mBinding.issuerFloatingActionButton.setOnClickListener(v -> {
            Intent intent = AddIssuerActivity.newIntent(getContext());
            startActivity(intent);
        });

        return mBinding.getRoot();
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
                Intent intent = SettingsActivity.newIntent(getContext());
                startActivity(intent);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void setupRecyclerView(List<Issuer> issuerList) {
        final IssuerAdapter adapter = new IssuerAdapter(issuerList);
        mBinding.issuerRecyclerview.setAdapter(adapter);

        int gridSize = getResources().getInteger(R.integer.fragment_home_issuer_grid_size);
        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(getActivity(), gridSize);
        mBinding.issuerRecyclerview.setLayoutManager(layoutManager);
        mBinding.issuerRecyclerview.setHasFixedSize(true);
    }

    private class IssuerAdapter extends RecyclerView.Adapter<IssuerViewHolder> {

        private List<Issuer> mIssuerList;

        IssuerAdapter(List<Issuer> issuerList) {
            mIssuerList = issuerList;
        }

        @Override
        public IssuerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            Context context = parent.getContext();
            LayoutInflater inflater = LayoutInflater.from(context);
            ListItemIssuerBinding binding = DataBindingUtil.inflate(inflater, R.layout.list_item_issuer, parent, false);
            return new IssuerViewHolder(binding);
        }

        @Override
        public void onBindViewHolder(IssuerViewHolder holder, int position) {
            Issuer issuer = mIssuerList.get(position);
            holder.bind(issuer);
        }

        @Override
        public int getItemCount() {
            return mIssuerList.size();
        }
    }
}
