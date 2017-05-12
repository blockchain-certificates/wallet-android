package com.learningmachine.android.app.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.StringRes;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;

public class AlertDialogFragment extends DialogFragment {


    public static final int RESULT_POSITIVE = 1;
    public static final int RESULT_NEGATIVE = 0;

    private static final String ARG_TITLE = "AlertDialogFragment.Title";
    private static final String ARG_MESSAGE = "AlertDialogFragment.Message";
    private static final String ARG_POSITIVE_BUTTON_MESSAGE = "AlertDialogFragment.Positive.Button_Message";
    private static final String ARG_NEGATIVE_BUTTON_MESSAGE = "AlertDialogFragment.Negative.Button.Message";

    public interface AlertCallback {
        void onDialogPositive();

        void onDialogNegative();
    }

    public static AlertDialogFragment newInstance(Context context, @StringRes int messageResId) {
        return newInstance(context, messageResId, 0, 0);
    }


    public static AlertDialogFragment newInstance(Context context, @StringRes int titleResId, @StringRes int messageResId, @StringRes int positiveButtonResId, @StringRes int negativeButtonResId) {
        String title = titleResId == 0 ? "" : context.getString(titleResId);
        String message = messageResId == 0 ? "" : context.getString(messageResId);
        String positiveButtonMessage = positiveButtonResId == 0 ? "" : context.getString(positiveButtonResId);
        String negativeButtonMessage = negativeButtonResId == 0 ? "" : context.getString(negativeButtonResId);
        return newInstance(title, message, positiveButtonMessage, negativeButtonMessage);
    }


    public static AlertDialogFragment newInstance(Context context, @StringRes int messageResId, @StringRes int positiveButtonResId, @StringRes int negativeButtonResId) {
        return newInstance(context, 0, messageResId, positiveButtonResId, negativeButtonResId);
    }

    public static AlertDialogFragment newInstance(String message) {
        return newInstance("", message, "", "");
    }

    public static AlertDialogFragment newInstance(String title, String message) {
        return newInstance(title, message, "", "");
    }

    public static AlertDialogFragment newInstance(String title, String message, String positiveButtonMessage, String negativeButtonMessage) {
        Bundle args = new Bundle();
        AlertDialogFragment fragment = new AlertDialogFragment();
        args.putString(ARG_MESSAGE, message);
        args.putString(ARG_TITLE, title);
        args.putString(ARG_POSITIVE_BUTTON_MESSAGE, positiveButtonMessage);
        args.putString(ARG_NEGATIVE_BUTTON_MESSAGE, negativeButtonMessage);
        fragment.setArguments(args);
        return fragment;
    }

    private AlertCallback callback;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof AlertCallback) {
            callback = (AlertCallback) context;
        }
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Bundle args = getArguments();
        String title = args.getString(ARG_TITLE);
        String message = args.getString(ARG_MESSAGE);
        String positiveButtonMessage = args.getString(ARG_POSITIVE_BUTTON_MESSAGE);
        String negativeButtonMessage = args.getString(ARG_NEGATIVE_BUTTON_MESSAGE);

        if (TextUtils.isEmpty(positiveButtonMessage)) {
            positiveButtonMessage = getString(android.R.string.ok);
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity()).setMessage(message)
                .setPositiveButton(positiveButtonMessage, (dialog, which) -> onButtonTapped(RESULT_POSITIVE));
        if (!TextUtils.isEmpty(title)) {
            builder.setTitle(title);
        }
        if (!TextUtils.isEmpty(negativeButtonMessage)) {
            builder.setNegativeButton(negativeButtonMessage, (dialog, which) -> onButtonTapped(RESULT_NEGATIVE));
        }

        return builder.create();
    }

    @Override
    public void onDetach() {
        super.onDetach();
        callback = null;
    }

    private void onButtonTapped(int buttonResultCode) {
        Fragment fragment = getTargetFragment();
        if (fragment != null) {
            fragment.onActivityResult(getTargetRequestCode(), buttonResultCode, null);
        } else if (callback != null) {
            if (buttonResultCode == RESULT_POSITIVE) {
                callback.onDialogPositive();
            } else if (buttonResultCode == RESULT_NEGATIVE) {
                callback.onDialogNegative();
            }
        }

    }
}
