package com.learningmachine.android.app.util;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.StringRes;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;

import com.learningmachine.android.app.R;
import com.learningmachine.android.app.data.error.ExceptionWithResourceString;
import com.learningmachine.android.app.dialog.AlertDialogFragment;
import com.learningmachine.android.app.dialog.ProgressDialogFragment;

import java.net.UnknownHostException;

import retrofit2.HttpException;

import static java.net.HttpURLConnection.HTTP_BAD_REQUEST;
import static java.net.HttpURLConnection.HTTP_NOT_FOUND;

public class DialogUtils {

    public enum ErrorCategory {
        GENERIC, ISSUER, CERTIFICATE
    }

    public static final String TAG_DIALOG_PROGRESS = "DialogUtils.Dialog.Progress";
    private static final String TAG_DIALOG_ALERT = "DialogUtils.Dialog.Alert";

    public static void showProgressDialog(FragmentManager fragmentManager, String message) {
        ProgressDialogFragment progressDialogFragment = ProgressDialogFragment.newInstance(message);
        progressDialogFragment.setCancelable(false);
        progressDialogFragment.show(fragmentManager, TAG_DIALOG_PROGRESS);

    }

    public static void hideProgressDialog(FragmentManager fragmentManager) {
        Fragment fragment = fragmentManager.findFragmentByTag(TAG_DIALOG_PROGRESS);
        if (fragment instanceof ProgressDialogFragment) {
            ((ProgressDialogFragment) fragment).dismissAllowingStateLoss();
        }
    }

    public static void showAlertDialog(Context context, FragmentManager fragmentManager, @StringRes int messageResId) {
        AlertDialogFragment alertDialogFragment = AlertDialogFragment.newInstance(context, messageResId);
        alertDialogFragment.show(fragmentManager, TAG_DIALOG_ALERT);
    }

    public static void showAlertDialog(Context context, @NonNull Fragment targetFragment, int requestCode, @StringRes int titleResId, @StringRes int messageResId, @StringRes int positiveButtonResId, @StringRes int negativeButtonResId) {
        FragmentManager fragmentManager = targetFragment.getFragmentManager();
        AlertDialogFragment alertDialogFragment = AlertDialogFragment.newInstance(context,
                titleResId,
                messageResId,
                positiveButtonResId,
                negativeButtonResId);
        alertDialogFragment.setTargetFragment(targetFragment, requestCode);
        alertDialogFragment.show(fragmentManager, TAG_DIALOG_ALERT);
    }

    public static void showAlertDialog(Context context, @NonNull Fragment targetFragment, int iconID, String title, String message, String positiveButton, String negativeButton, AlertDialogFragment.Callback callback) {
        FragmentManager fragmentManager = targetFragment.getFragmentManager();
        AlertDialogFragment alertDialogFragment = AlertDialogFragment.newInstance(
                false,
                0,
                iconID,
                title,
                message,
                positiveButton,
                negativeButton,
                callback,
                null);
        alertDialogFragment.setTargetFragment(targetFragment, 0);
        alertDialogFragment.show(fragmentManager, TAG_DIALOG_ALERT);
    }

    public static AlertDialogFragment showCustomDialog(Context context, @NonNull Fragment targetFragment, int layoutID, int iconID, String title, String message, String positiveButton, String negativeButton, AlertDialogFragment.Callback onComplete, AlertDialogFragment.Callback onCreate) {
        FragmentManager fragmentManager = targetFragment.getFragmentManager();
        AlertDialogFragment alertDialogFragment = AlertDialogFragment.newInstance(
                false,
                layoutID,
                iconID,
                title,
                message,
                positiveButton,
                negativeButton,
                onComplete,
                onCreate);
        alertDialogFragment.setTargetFragment(targetFragment, 0);
        alertDialogFragment.show(fragmentManager, TAG_DIALOG_ALERT);
        return alertDialogFragment;
    }

    public static AlertDialogFragment showCustomSheet(Context context, @NonNull Fragment targetFragment, int layoutID, int iconID, String title, String message, String positiveButton, String negativeButton, AlertDialogFragment.Callback onComplete, AlertDialogFragment.Callback onCreate) {
        FragmentManager fragmentManager = targetFragment.getFragmentManager();
        AlertDialogFragment alertDialogFragment = AlertDialogFragment.newInstance(
                true,
                layoutID,
                iconID,
                title,
                message,
                positiveButton,
                negativeButton,
                onComplete,
                onCreate);
        alertDialogFragment.setTargetFragment(targetFragment, 0);
        alertDialogFragment.show(fragmentManager, TAG_DIALOG_ALERT);
        return alertDialogFragment;
    }


    public static void showErrorAlertDialog(Context context, FragmentManager fragmentManager, @StringRes int titleResId, Throwable throwable, ErrorCategory errorCategory) {
        String titleString = context.getString(titleResId);
        String errorString = getErrorMessageString(context, throwable, errorCategory);
        showErrorAlertDialog(context, fragmentManager, titleString, errorString, throwable);
    }

    public static void showErrorAlertDialog(Context context, FragmentManager fragmentManager, @StringRes int titleResId, int errorID, Throwable throwable, ErrorCategory errorCategory) {
        String titleString = context.getString(titleResId);
        String errorString = context.getString(errorID);
        showErrorAlertDialog(context, fragmentManager, titleString, errorString, throwable);
    }

    private static void showErrorAlertDialog(Context context, FragmentManager fragmentManager, String title, String errorMessage, Throwable throwable) {
        AlertDialogFragment dialog = AlertDialogFragment.newInstance(title, errorMessage);
        fragmentManager.beginTransaction()
                .add(dialog, TAG_DIALOG_ALERT)
                .commitAllowingStateLoss();
    }

    private static String getErrorMessageString(Context context, Throwable throwable, ErrorCategory errorCategory) {
        int resId = getErrorMessageResourceId(throwable, errorCategory);
        if (resId == 0) {
            return throwable.getMessage();
        }
        return context.getString(resId);
    }

    private static int getErrorMessageResourceId(Throwable throwable, ErrorCategory errorCategory) {
        if (throwable instanceof UnknownHostException) {
            return R.string.connection_error_message;
        } else if (throwable instanceof HttpException) {
            switch (((HttpException) throwable).code()) {
                case HTTP_NOT_FOUND:
                    switch(errorCategory){
                        case ISSUER:
                            return R.string.http_not_found_issuer;
                        case CERTIFICATE:
                            return R.string.http_not_found_certificate;
                        default:
                            return R.string.http_not_found_generic;
                    }
                default:
                case HTTP_BAD_REQUEST:
                    switch(errorCategory){
                        case ISSUER:
                            return R.string.http_bad_request_issuer;
                        case CERTIFICATE:
                            return R.string.http_bad_request_certificate;
                        default:
                            return R.string.http_bad_request_generic;
                    }
            }
        } else if (throwable instanceof ExceptionWithResourceString) {
            ExceptionWithResourceString exceptionWithResourceString = (ExceptionWithResourceString) throwable;
            return exceptionWithResourceString.getErrorMessageResId();
        } else {
            return 0;
        }
    }

}
