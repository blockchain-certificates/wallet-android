package com.learningmachine.android.app.dialog;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.Resources;
import android.graphics.Point;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.StringRes;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.learningmachine.android.app.R;

public class AlertDialogFragment extends DialogFragment {

    public static final int RESULT_POSITIVE = 1;
    public static final int RESULT_NEGATIVE = 0;

    private static final String ARG_BOTTOM = "AlertDialogFragment.BOTTOM";
    private static final String ARG_ICON = "AlertDialogFragment.Icon";
    private static final String ARG_LAYOUT = "AlertDialogFragment.Layout";
    private static final String ARG_TITLE = "AlertDialogFragment.Title";
    private static final String ARG_MESSAGE = "AlertDialogFragment.Message";
    private static final String ARG_POSITIVE_BUTTON_MESSAGE = "AlertDialogFragment.Positive.Button_Message";
    private static final String ARG_NEGATIVE_BUTTON_MESSAGE = "AlertDialogFragment.Negative.Button.Message";
    private TextView mTitleView;
    private TextView mSubTitleView;
    private TextView mMessageView;

    @FunctionalInterface
    public interface Callback <A, R> {
        public R apply (A a);
    }

    public Callback onCancel = null;
    public Callback onComplete = null;
    public Callback onCreate= null;

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
        return newInstance(0, title, message, positiveButtonMessage, negativeButtonMessage);
    }


    public static AlertDialogFragment newInstance(Context context, @StringRes int messageResId, @StringRes int positiveButtonResId, @StringRes int negativeButtonResId) {
        return newInstance(context, 0, messageResId, positiveButtonResId, negativeButtonResId);
    }

    public static AlertDialogFragment newInstance(String message) {
        return newInstance(0, "", message, "", "");
    }

    public static AlertDialogFragment newInstance(Context context, String title, String message) {
        String negativeButton = context.getString(R.string.ok_button);
        return newInstance(R.drawable.ic_dialog_failure, title, message, null, negativeButton);
    }

    public static AlertDialogFragment newInstance(boolean bottomSheet, int layoutID, int iconID, String title, String message, String positiveButtonMessage, String negativeButtonMessage, Callback complete, Callback onCreate, Callback onCancel) {
        Bundle args = new Bundle();
        AlertDialogFragment fragment = new AlertDialogFragment();
        args.putString(ARG_MESSAGE, message);
        args.putString(ARG_TITLE, title);
        args.putInt(ARG_ICON, iconID);
        args.putBoolean(ARG_BOTTOM, bottomSheet);
        args.putInt(ARG_LAYOUT, layoutID);
        args.putString(ARG_POSITIVE_BUTTON_MESSAGE, positiveButtonMessage);
        args.putString(ARG_NEGATIVE_BUTTON_MESSAGE, negativeButtonMessage);
        fragment.setArguments(args);
        fragment.onComplete = complete;
        fragment.onCancel = onCancel;
        fragment.onCreate = onCreate;
        return fragment;
    }

    public static AlertDialogFragment newInstance(int iconID, String title, String message, String positiveButtonMessage, String negativeButtonMessage) {
        Bundle args = new Bundle();
        AlertDialogFragment fragment = new AlertDialogFragment();
        if(iconID > 0) {
            args.putInt(ARG_ICON, iconID);
        }
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

        boolean bottomSheet = false;
        if (args.containsKey(ARG_BOTTOM)) {
            bottomSheet = args.getBoolean(ARG_BOTTOM);
        }

        int dialogIcon = 0;
        if (args.containsKey(ARG_ICON)) {
            dialogIcon = args.getInt(ARG_ICON);
        }

        int layoutID = 0;
        if (args.containsKey(ARG_LAYOUT)) {
            layoutID = args.getInt(ARG_LAYOUT);
        }

        Dialog dialog = null;

        if (bottomSheet) {
            dialog = new Dialog(getActivity(), R.style.bottom_dialog);
        } else {
            dialog = new Dialog(getActivity());
        }
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);

        View dialogContent = null;
        LayoutInflater factory = LayoutInflater.from(getContext());
        if (layoutID > 0) {
            dialogContent = factory.inflate(layoutID, null);
        } else {
            if (negativeButtonMessage == null && positiveButtonMessage != null) {
                dialogContent = factory.inflate(R.layout.dialog_1a, null);
            } else if (negativeButtonMessage != null && positiveButtonMessage == null) {
                dialogContent = factory.inflate(R.layout.dialog_1b, null);
            } else {
                dialogContent = factory.inflate(R.layout.dialog_2, null);
            }
        }


        dialog.setContentView(dialogContent);

        ImageView iconView = (ImageView) dialogContent.findViewById(R.id.image_view);
        mTitleView = (TextView) dialogContent.findViewById(R.id.titleView);
        mSubTitleView = (TextView) dialogContent.findViewById(R.id.subTitleView);
        mMessageView = (TextView) dialogContent.findViewById(R.id.messageView);
        Button positiveButtonView = (Button) dialogContent.findViewById(R.id.dialog_positive_button);
        Button negativeButtonView = (Button) dialogContent.findViewById(R.id.dialog_negative_button);

        if (iconView != null) {
            if (dialogIcon > 0) {
                iconView.setImageResource(dialogIcon);
            } else {
                iconView.setVisibility(View.GONE);
            }
        }

        if(mTitleView != null) {
            mTitleView.setText(title);
        }
        if(mMessageView != null) {
            mMessageView.setText(message);
        }

        if (positiveButtonView != null) {
            positiveButtonView.setText(positiveButtonMessage);
        }

        if (negativeButtonView != null) {
            negativeButtonView.setText(negativeButtonMessage);
        }

        // 1) Dialog width should be 80% of the width of the screen
        Display display = getActivity().getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);

        float idealDialogWidth = size.x * 0.8f;
        float maxDialogHeight = size.y * 0.8f;

        if (bottomSheet) {
            idealDialogWidth = size.x * 1.0f;
            maxDialogHeight = size.y * 1.0f;
        }

        // 1) Size the dialog to the max
        dialogContent.setLayoutParams(new FrameLayout.LayoutParams((int) idealDialogWidth, (int) maxDialogHeight));

        // 2) tell all views to remeasure themselves given the new max width
        relayoutChildren(dialogContent);

        // 3) Calculate the total height of the dialog
        int widthMeasureSpec = View.MeasureSpec.makeMeasureSpec((int) (idealDialogWidth * 0.6f), View.MeasureSpec.EXACTLY);
        int heightMeasureSpec = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
        dialogContent.measure(widthMeasureSpec, heightMeasureSpec);
        float totalHeight = dialogContent.getMeasuredHeight();

        // 4) Dialog height should be a little larger than the measured height
        float idealDialogHeight = totalHeight + dp2px(1.0f);

        dialogContent.setLayoutParams(new FrameLayout.LayoutParams((int) idealDialogWidth, (int) idealDialogHeight));


        // 5) instrument the buttons
        if (positiveButtonView != null) {
            positiveButtonView.setOnClickListener(view -> {
                dismiss();
                onButtonTapped(RESULT_POSITIVE);
            });
        }

        if (negativeButtonView != null) {
            negativeButtonView.setOnClickListener(view -> {
                dismiss();
                onButtonTapped(RESULT_NEGATIVE);
            });
        }

        if (onCreate != null) {
            onCreate.apply(dialogContent);
        }

        if (bottomSheet) {
            Window window = dialog.getWindow();
            WindowManager.LayoutParams wlp = window.getAttributes();
            wlp.gravity = Gravity.BOTTOM;
            window.setAttributes(wlp);
        }


        //dialog.setCancelable(false);
        //this.setCancelable(false);

        return dialog;
    }

    public void setCustomTitle(String customTitle) {
        if (mTitleView != null) {
            mTitleView.setText(customTitle);
        }
    }

    public void setCustomSubTitle(String customSubTitle) {
        if (mSubTitleView != null) {
            mSubTitleView.setText(customSubTitle);
        }
    }

    public void setCustomMessage(String customMessage) {
        if (mMessageView != null) {
            mMessageView.setText(customMessage);
        }
    }

    public int getTextViewHeight(TextView textView, int maxWidth) {
        int widthMeasureSpec = View.MeasureSpec.makeMeasureSpec(maxWidth, View.MeasureSpec.AT_MOST);
        int heightMeasureSpec = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
        textView.measure(widthMeasureSpec, heightMeasureSpec);
        return textView.getMeasuredHeight();
    }

    public boolean forceFullscreen = false;
    @Override
    public void onStart() {
        super.onStart();
        if(forceFullscreen) {
            Dialog dialog = getDialog();
            if (dialog != null) {
                dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            }
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        callback = null;
    }

    @Override
    public void onCancel(DialogInterface dialog) {
        if(onCancel != null) {
            onCancel.apply(0);
        }
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

        if(onComplete != null) {
            onComplete.apply(buttonResultCode);
        }
    }
}
