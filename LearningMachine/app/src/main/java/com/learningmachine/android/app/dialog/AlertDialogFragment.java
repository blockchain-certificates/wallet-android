package com.learningmachine.android.app.dialog;

import android.app.Dialog;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Point;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.StringRes;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.learningmachine.android.app.R;
import com.learningmachine.android.app.util.StringUtils;

public class AlertDialogFragment extends DialogFragment {

    public static final int RESULT_POSITIVE = 1;
    public static final int RESULT_NEGATIVE = 0;

    private static final String ARG_ICON = "AlertDialogFragment.Icon";
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

    public static AlertDialogFragment newInstance(int iconID, String title, String message, String positiveButtonMessage, String negativeButtonMessage) {
        Bundle args = new Bundle();
        AlertDialogFragment fragment = new AlertDialogFragment();
        args.putString(ARG_MESSAGE, message);
        args.putString(ARG_TITLE, title);
        args.putInt(ARG_ICON, iconID);
        args.putString(ARG_POSITIVE_BUTTON_MESSAGE, positiveButtonMessage);
        args.putString(ARG_NEGATIVE_BUTTON_MESSAGE, negativeButtonMessage);
        fragment.setArguments(args);
        return fragment;
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

    private float dp2px(float dp) {
        Resources resources = getContext().getResources();
        DisplayMetrics metrics = resources.getDisplayMetrics();
        return dp * ((float) metrics.densityDpi / DisplayMetrics.DENSITY_DEFAULT);
    }

    private void relayoutChildren(View view) {
        view.measure(
                View.MeasureSpec.makeMeasureSpec(view.getMeasuredWidth(), View.MeasureSpec.EXACTLY),
                View.MeasureSpec.makeMeasureSpec(view.getMeasuredHeight(), View.MeasureSpec.EXACTLY));
        view.layout(view.getLeft(), view.getTop(), view.getRight(), view.getBottom());
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Bundle args = getArguments();

        String title = args.getString(ARG_TITLE);
        String message = args.getString(ARG_MESSAGE);
        String positiveButtonMessage = args.getString(ARG_POSITIVE_BUTTON_MESSAGE);
        String negativeButtonMessage = args.getString(ARG_NEGATIVE_BUTTON_MESSAGE);

        int dialogIcon = 0;
        if (args.containsKey(ARG_ICON)) {
            dialogIcon = args.getInt(ARG_ICON);
        }

        if (StringUtils.isEmpty(positiveButtonMessage)) {
            positiveButtonMessage = getString(android.R.string.ok);
        }

        final Dialog dialog = new Dialog(getActivity());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);

        LayoutInflater factory = LayoutInflater.from(getContext());
        View dialogContent = null;
        if (negativeButtonMessage == null) {
            dialogContent = factory.inflate(R.layout.dialog_1, null);
        } else {
            dialogContent = factory.inflate(R.layout.dialog_2, null);
        }

        dialog.setContentView(dialogContent);


        TextView titleView = (TextView) dialogContent.findViewById(R.id.titleView);
        TextView messageView = (TextView) dialogContent.findViewById(R.id.messageView);
        Button positiveButtonView = (Button) dialogContent.findViewById(R.id.dialog_positive_button);
        Button negativeButtonView = (Button) dialogContent.findViewById(R.id.dialog_negative_button);

        titleView.setText(title);
        messageView.setText(message);
        positiveButtonView.setText(positiveButtonMessage);

        if (negativeButtonView != null) {
            negativeButtonView.setText(negativeButtonMessage);
        }

        // 1) Dialog width should be 80% of the width of the screen
        Display display = getActivity().getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);

        float idealDialogWidth = size.x * 0.8f;
        float maxDialogHeight = size.y * 0.8f;

        // 1) Size the dialog to the max
        dialogContent.setLayoutParams(new FrameLayout.LayoutParams((int) idealDialogWidth, (int) maxDialogHeight));

        // 2) tell all views to remeasure themselves given the new max width
        relayoutChildren(dialogContent);


        // 3) Telling the views to measure is apparently not enough to get the
        // ConstraintLayout to have the proper width and height. So let's run through
        // all of the subviews and find the max extents and use those
        float totalHeight = 0.0f;
        ViewGroup vg = (ViewGroup) dialogContent;

        for (int i = 0; i < vg.getChildCount(); i++) {
            final View child = vg.getChildAt(i);

            child.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);

            float vX = child.getMeasuredWidth();
            float vY = child.getMeasuredHeight();

            //Log.d("LM", String.format("child[%d] = %f,%f  %s", i, vX, vY, child.toString()));

            totalHeight += vY + dp2px(16.0f);
        }

        //Log.d("LM", String.format("totalHeight = %f", totalHeight));
        //Log.d("LM", String.format("dialogContent width/height= %d,%d", dialogContent.getMeasuredWidth(), dialogContent.getMeasuredHeight()));

        // 4) Dialog height should be a little larger than the measured height
        float idealDialogHeight = totalHeight + dp2px(80.0f);

        dialogContent.setLayoutParams(new FrameLayout.LayoutParams((int) idealDialogWidth, (int) idealDialogHeight));


        // 5) instrument the buttons
        positiveButtonView.setOnClickListener(view -> {
            dismiss();
            onButtonTapped(RESULT_POSITIVE);
        });

        if (negativeButtonView != null) {
            negativeButtonView.setOnClickListener(view -> {
                dismiss();
                onButtonTapped(RESULT_NEGATIVE);
            });
        }

        return dialog;
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
