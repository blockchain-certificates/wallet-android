package com.learningmachine.android.app.ui.settings.passphrase;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.learningmachine.android.app.R;
import com.learningmachine.android.app.databinding.FragmentReplacePassphraseBinding;
import com.learningmachine.android.app.ui.LMFragment;

public class ReplacePassphraseFragment extends LMFragment {

    public static Fragment newInstance() {
        return new ReplacePassphraseFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        FragmentReplacePassphraseBinding binding = DataBindingUtil.inflate(inflater,
                R.layout.fragment_replace_passphrase,
                container,
                false);
        return binding.getRoot();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.fragment_replace_passphrase, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.fragment_replace_passphrase_done_menu_item:
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}
