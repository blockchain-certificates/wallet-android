package com.learningmachine.android.app.ui.cert;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.learningmachine.android.app.R;
import com.learningmachine.android.app.data.CertificateManager;
import com.learningmachine.android.app.data.inject.Injector;
import com.learningmachine.android.app.data.model.Document;
import com.learningmachine.android.app.data.model.KeyRotation;
import com.learningmachine.android.app.data.model.Receipt;
import com.learningmachine.android.app.data.model.TxRecord;
import com.learningmachine.android.app.data.model.TxRecordOut;
import com.learningmachine.android.app.data.webservice.BlockchainService;
import com.learningmachine.android.app.data.webservice.IssuerService;
import com.learningmachine.android.app.data.webservice.response.IssuerResponse;
import com.learningmachine.android.app.databinding.FragmentCertificateBinding;
import com.learningmachine.android.app.ui.LMFragment;
import com.learningmachine.android.app.util.FileUtils;

import org.bitcoinj.core.Address;
import org.bitcoinj.core.ECKey;
import org.bitcoinj.params.MainNetParams;

import java.io.File;
import java.security.SignatureException;

import javax.inject.Inject;

import timber.log.Timber;

public class CertificateFragment extends LMFragment {

    private static final String ARG_CERTIFICATE_UUID = "CertificateFragment.CertificateUuid";
    private static final String INDEX_FILE_PATH = "file:///android_asset/www/index.html";

    @Inject protected CertificateManager mCertificateManager;
    @Inject protected BlockchainService mBlockchainService;
    @Inject protected IssuerService mIssuerService;

    private FragmentCertificateBinding mBinding;

    public static CertificateFragment newInstance(String certificateUuid) {
        Bundle args = new Bundle();
        args.putString(ARG_CERTIFICATE_UUID, certificateUuid);

        CertificateFragment fragment = new CertificateFragment();
        fragment.setArguments(args);

        return fragment;
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
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_certificate, container, false);

        setupWebView();

        return mBinding.getRoot();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.fragment_certificate, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.fragment_certificate_verify_menu_item:
                verifyCertificate();
                return true;
            case R.id.fragment_certificate_share_menu_item:
                return true;
            case R.id.fragment_certificate_info_menu_item:
                String certUuid = getArguments().getString(ARG_CERTIFICATE_UUID);
                Intent intent = CertificateInfoActivity.newIntent(getActivity(), certUuid);
                startActivity(intent);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void verifyCertificate() {
        Receipt receipt = mCertificate.getReceipt();
        if (receipt == null) {
            // TODO: show an error
            Timber.d("Certificate receipt missing");
            return;
        }
        String sourceId = receipt.getFirstAnchorSourceId();
        String issuerUuid = mCertificate.getIssuerUuid();
        mBlockchainService.getBlockchain(sourceId)
                .compose(bindToMainThread())
                .subscribe(this::blockchainDownloaded, e -> Timber.e(e));
        mIssuerService.getIssuer(issuerUuid)
                .compose(bindToMainThread())
                .subscribe(this::issuerDownloaded, e -> Timber.e(e));
    }

    private void blockchainDownloaded(TxRecord txRecord) {
        TxRecordOut lastOut = txRecord.getLastOut();
        int value = lastOut.getValue();
        String remoteHash = lastOut.getScript();

        if (value != 0) {
            // TODO: show an error
            Timber.d("The last transaction record out should have the value of 0");
            return;
        }

        // strip out 6a20 prefix, if present
        remoteHash = remoteHash.startsWith("6a20") ? remoteHash.substring(4) : remoteHash;

        String merkleRoot = mCertificate.getReceipt().getMerkleRoot();

        if (!remoteHash.equals(merkleRoot)) {
            // TODO: show an error
            Timber.d("The transaction record hash doesn't match the certificate's Merkle root");
            return;
        }

        Timber.d("Blockchain transaction is downloaded successfully");
    }

    private void issuerDownloaded(IssuerResponse issuerResponse) {
        if (issuerResponse.getIssuerKeys().isEmpty()) {
            // TODO: show an error
            Timber.d("Issuer is missing keys");
            return;
        }

        KeyRotation firstIssuerKey = issuerResponse.getIssuerKeys().get(0);

        Document document = mCertificate.getDocument();
        String signature = document.getSignature();
        String uuid = document.getAssertion().getUuid();

        ECKey ecKey = null;
        try {
            ecKey = ECKey.signedMessageToKey(uuid, signature);
            ecKey.verifyMessage(uuid, signature); // this is tautological
            Address address = ecKey.toAddress(MainNetParams.get());
            if (!firstIssuerKey.getKey().equals(address.toBase58())) {
                // TODO: show an error
                Timber.d("The issuer key doesn't match the certificate address");
                return;
            }
        } catch (SignatureException e) {
            Timber.e(e);
            // TODO: show an error
            Timber.d("The document signature is invalid");
            return;
        }

        Timber.d("Issuer matches certificate");
    }

    private void setupWebView() {
        WebSettings webSettings = mBinding.webView.getSettings();
        // Enable JavaScript.
        webSettings.setJavaScriptEnabled(true);
        // Enable HTML Imports to be loaded from file://.
        webSettings.setAllowFileAccessFromFileURLs(true);
        // Ensure local links/redirects in WebView, not the browser.
        mBinding.webView.setWebViewClient(new LMWebViewClient());

        mBinding.webView.loadUrl(INDEX_FILE_PATH);
    }

    public class LMWebViewClient extends WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            // Handle local URLs
            if (Uri.parse(url)
                    .getHost()
                    .length() == 0) {
                return false;
            }

            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
            startActivity(intent);
            return true;
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            String certUuid = getArguments().getString(ARG_CERTIFICATE_UUID);
            File certFile = FileUtils.getCertificateFile(getContext(), certUuid);
            String certFilePath = certFile.toString();

            String javascript = String.format(
                    "javascript:(function() { document.getElementsByTagName('blockchain-certificate')[0].href='%1$s';})()",
                    certFilePath);
            mBinding.webView.loadUrl(javascript);
        }
    }
}
