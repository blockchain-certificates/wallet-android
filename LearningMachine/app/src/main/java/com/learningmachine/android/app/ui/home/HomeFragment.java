package com.learningmachine.android.app.ui.home;

import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.databinding.ViewDataBinding;
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
import com.learningmachine.android.app.data.model.Issuer;
import com.learningmachine.android.app.databinding.FragmentHomeBinding;
import com.learningmachine.android.app.ui.LearningMachineFragment;
import com.learningmachine.android.app.ui.settings.SettingsActivity;

import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends LearningMachineFragment {

    private FragmentHomeBinding mBinding;

    public static HomeFragment newInstance() {
        return new HomeFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_home, container, false);

        setupRecyclerView();

        return mBinding.getRoot();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.issuer_settings, menu);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.issuer_settings_button:
                Intent intent = new Intent(getContext(), SettingsActivity.class);
                startActivity(intent);
        }
        return super.onOptionsItemSelected(item);
    }

    private void setupRecyclerView() {
        // build list
        List<Issuer> issuerList = new ArrayList<>();

        Issuer issuer = new Issuer("Issuer 1", R.drawable.issuer_baratheon);
        issuerList.add(issuer);
        issuer = new Issuer("Issuer 2", R.drawable.issuer_stark);
        issuerList.add(issuer);
        issuer = new Issuer("Issuer 3", R.drawable.issuer_targaryen);
        issuerList.add(issuer);

        IssuerAdapter adapter = new IssuerAdapter(issuerList);
        mBinding.issuerRecyclerview.setAdapter(adapter);

        int gridSize = getResources().getInteger(R.integer.fragment_home_issuer_grid_size);
        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(getActivity(), gridSize);
        mBinding.issuerRecyclerview.setLayoutManager(layoutManager);
        mBinding.issuerRecyclerview.setHasFixedSize(true);
    }

    private class IssuerAdapter extends RecyclerView.Adapter<IssuerViewHolder> {

        private List<Issuer> mIssuerList;

        public IssuerAdapter(List<Issuer> contactList) {
            mIssuerList = contactList;
        }

        @Override
        public IssuerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            Context context = parent.getContext();
            LayoutInflater inflater = LayoutInflater.from(context);
            ViewDataBinding binding = DataBindingUtil.inflate(inflater, R.layout.list_item_issuer, parent, false);
            return new IssuerViewHolder(binding);
        }

        @Override
        public void onBindViewHolder(IssuerViewHolder holder, int position) {
            Issuer contact = mIssuerList.get(position);
            holder.getViewModel()
                    .bindIssuer(contact);
        }

        @Override
        public int getItemCount() {
            return mIssuerList.size();
        }
    }
}
