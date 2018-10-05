package com.learningmachine.android.app.ui.cert;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v4.widget.TextViewCompat;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewParent;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.learningmachine.android.app.R;
import com.learningmachine.android.app.data.verifier.VerifierStatus;

public class VerificationCustomItem extends RelativeLayout {
    public static final int FIRST_ITEM_MARGIN_TOP = -14;
    public static final int LAST_SUB_ITEM_MARGIN_BOTTOM = 20;
    public static final int EXTRA_HEIGHT_FOR_STATUS_BAR = 28;
    private TextView mItemTitle;
    private LinearLayout mSubItemsContainer;
    private Context mContext;
    private View mItemStatusBar;
    private View mPlaceholderStatusBar;
    private int mSubItemHeight = 0;
    private int mSubItemTotalCount = 0;
    private int mSubItemCount = 0;
    private ImageView mItemStatusIconBackground;
    private boolean mIsFirstItem;
    private boolean mHasStarted;
    private View mProgress;
    private View mVerifiedInfo;
    private boolean mIsLastItem;
    private VerificationCustomView.OnVerificationFinish mOnVerificationFinishListener;
    private ScrollView mParentScrollView;

    /**
     * Notifies when the bar animation finishes.
     */
    public interface OnFinishAnimation {
        /**
         * Called when bar animation finishes.
         */
        void animationFinished();
    }

    /**
     * Set a listener for the animation finish event.
     * @param onVerificationFinishListener The listener.
     */
    public void setOnVerificationFinishListener(VerificationCustomView.OnVerificationFinish onVerificationFinishListener) {
        mOnVerificationFinishListener = onVerificationFinishListener;
    }

    public VerificationCustomItem(Context context) {
        super(context);
        mContext = context;
        inflateItem();
    }

    public VerificationCustomItem(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        inflateItem();
    }

    public VerificationCustomItem(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext = context;
        inflateItem();
    }

    /**
     * Set if this is the first item in the list. Needed to set margins.
     * @param isFirstItem True if first item.
     */
    public void setIsFirstItem(boolean isFirstItem) {
        mIsFirstItem = isFirstItem;
    }

    /**
     * Set if this is the last item in the list. Needed to show completion info.
     * @param isLastItem True if last item.
     */
    public void setIsLastItem(boolean isLastItem) {
        mIsLastItem = isLastItem;
    }

    /**
     * Will request layout of sub item mark. Needed to show all elements correctly.
     */
    public void reconstructView() {
        mSubItemsContainer.getChildCount();
        for (int i = 0; i < mSubItemsContainer.getChildCount(); i++) {
            View subItem = mSubItemsContainer.getChildAt(i);
            View subItemMark = subItem.findViewById(R.id.sub_item_mark);
            subItemMark.requestLayout();
        }
    }

    /**
     * Inflates all items in this view.
     */
    private void inflateItem() {
        View item = inflate(mContext, R.layout.list_item_verifier, this);
        mItemTitle = item.findViewById(R.id.verifier_item_title);
        mSubItemsContainer = item.findViewById(R.id.verifier_sub_item_container);
        mItemStatusBar = item.findViewById(R.id.item_status_bar);
        mVerifiedInfo = item.findViewById(R.id.verified_info);
        mPlaceholderStatusBar = item.findViewById(R.id.placeholder_status_bar);
        mProgress = item.findViewById(R.id.item_status_progress);
        mItemStatusIconBackground = item.findViewById(R.id.item_status_icon);
        View itemStatusIconContainer = item.findViewById(R.id.status_icon_container);
        itemStatusIconContainer.bringToFront();
    }

    /**
     * Returns the scroll view. Can be used to scroll to an item in the list.
     * @return The Container Scroll View.
     */
    private ScrollView getParentScrollView() {
        ViewParent parent = getParent(); //VerificationCustomView
        parent = parent.getParent();//LinearLayout
        if (mParentScrollView == null) {
            mParentScrollView = (ScrollView) parent.getParent();
        }
        return mParentScrollView;
    }

    /**
     * Set the top margin for this item.
     * @param marginTop The value for the top margin. Can be negative.
     */
    private void setItemMarginTop(int marginTop) {
        LinearLayout.LayoutParams linearParams = new LinearLayout.LayoutParams(
                new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT));
        linearParams.setMargins(0, fromDpToPx(marginTop), 0, 0);
        setLayoutParams(linearParams);
        requestLayout();
    }

    /**
     * Set the bottom margin of a sub item.
     * @param subItem The sub item to set the bottom margin.
     * @param marginBottom The bottom margin value. Can be negative.
     */
    private void setSubItemMarginBottom(View subItem, int marginBottom) {
        LinearLayout.LayoutParams linearParams = new LinearLayout.LayoutParams(
                new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT));
        linearParams.setMargins(0, 0, 0, fromDpToPx(marginBottom));
        subItem.setLayoutParams(linearParams);
        subItem.requestLayout();
    }

    /**
     * Set the title of this item.
     * @param title The title.
     */
    public void setItemTitle(String title) {
        mItemTitle.setText(title);
    }

    /**
     * Add a sub item to this item.
     * @param title The sub item title.
     * @param code The code to be set as a tag.
     * @param totalSubItemCount The total count of sub items. Needed for view construction.
     */
    public void addSubItem(String title, String code, int totalSubItemCount) {
        mSubItemTotalCount = totalSubItemCount;
        View subItem = inflate(mContext, R.layout.list_sub_item_verifier, null);
        subItem.setTag(code);
        TextView subItemTitle = subItem.findViewById(R.id.verifier_sub_item_title);
        subItemTitle.setText(title);
        mSubItemsContainer.addView(subItem);

        if (!mIsFirstItem) {
            setItemMarginTop(FIRST_ITEM_MARGIN_TOP);
        }

        if (mSubItemTotalCount == mSubItemsContainer.getChildCount()) {
            setSubItemMarginBottom(subItem, LAST_SUB_ITEM_MARGIN_BOTTOM);
            subItem.post(() -> {
                mSubItemHeight = subItem.getHeight();
                mPlaceholderStatusBar.getLayoutParams().height += mSubItemHeight * mSubItemTotalCount;
                mPlaceholderStatusBar.requestLayout();
            });
            
            mPlaceholderStatusBar.getLayoutParams().height += fromDpToPx(EXTRA_HEIGHT_FOR_STATUS_BAR);
        }

        requestLayout();
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);
        if (changed) {
            if (mSubItemsContainer.getChildCount() > 0) {
                View subView = mSubItemsContainer.getChildAt(0);
                mSubItemHeight = subView.getHeight();
            }
        }
    }

    /**
     * Activates a sub item bu changing text color and changing icons.
     * @param status The status of the sub item.
     * @param onFinishAnimation Listener to get the animation finish events.
     */
    public void activateSubItem(VerifierStatus status, OnFinishAnimation onFinishAnimation) {
        if (mSubItemHeight == 0) {
            View subItem = mSubItemsContainer.findViewWithTag(status.code);
            subItem.post(() -> {
                mSubItemHeight = subItem.getHeight();
                activateSubItemWithHeight(status, onFinishAnimation);
            });
        } else {
            activateSubItemWithHeight(status, onFinishAnimation);
        }
    }

    /**
     * Show a progress indicator inside the status icon.
     */
    private void showProgressIcon() {
        mItemStatusIconBackground.setImageResource(R.drawable.ic_verification_status_item_bg);
        mProgress.setVisibility(VISIBLE);
    }

    /**
     * Show the success icon.
     */
    private void showSuccessIcon() {
        mItemStatusIconBackground.setImageResource(R.drawable.ic_verification_status_item_ok);
        mProgress.setVisibility(GONE);
    }

    /**
     * Show the error icon.
     */
    private void showErrorIcon() {
        mItemStatusIconBackground.setImageResource(R.drawable.ic_verification_status_item_error);
        mProgress.setVisibility(GONE);
    }


    /**
     * Activate a sub item, also changing the status bar height.
     * @param status The status of the sub item.
     * @param onFinishAnimation The animation listener.
     */
    private void activateSubItemWithHeight(VerifierStatus status, OnFinishAnimation onFinishAnimation) {
        if (!mHasStarted) {
            mHasStarted = true;
            showProgressIcon();
            mItemStatusBar.setVisibility(VISIBLE);
        }

        View subItem = mSubItemsContainer.findViewWithTag(status.code);
        TextView subItemTitle = subItem.findViewById(R.id.verifier_sub_item_title);

        if (status.isSuccess()) {
            getParentScrollView().smoothScrollTo(0, subItem.getTop() + getTop());
            mSubItemCount += 1;
            if (mSubItemCount == mSubItemTotalCount) {
                mSubItemHeight += fromDpToPx(24);
                showSuccessIcon();
            }

            int itemHeight = mItemStatusBar.getLayoutParams().height;
            ValueAnimator anim = ValueAnimator.ofInt(itemHeight, itemHeight + mSubItemHeight).setDuration(200);
            anim.addUpdateListener(animation -> {
                mItemStatusBar.getLayoutParams().height = (Integer) animation.getAnimatedValue();
                mItemStatusBar.requestLayout();
            });
            anim.start();
            anim.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    super.onAnimationEnd(animation);
                    onFinishAnimation.animationFinished();
                    if (mSubItemCount == mSubItemTotalCount && mIsLastItem) {
                        showVerifiedInfo();
                        if (mOnVerificationFinishListener != null) {
                            mOnVerificationFinishListener.verificationFinish(false);
                        }
                    }
                }
            });

            TextViewCompat.setTextAppearance(subItemTitle, R.style.Text_VerifierSubItem_Active);
            TextViewCompat.setTextAppearance(mItemTitle, R.style.Text_VerifierItem_Active);

        } else if(status.isFailure()) {
            mItemStatusBar.getLayoutParams().height += mSubItemHeight;
            TextViewCompat.setTextAppearance(subItemTitle, R.style.Text_VerifierSubItem_Active_Error);
            TextViewCompat.setTextAppearance(mItemTitle, R.style.Text_VerifierItem_Active);

            TextView subItemError = subItem.findViewById(R.id.verifier_sub_item_error);
            subItemError.setText(status.errorMessage);
            subItemError.setVisibility(INVISIBLE);

            subItemError.post(() -> {
                int subItemErrorHeight = subItemError.getHeight();
                mPlaceholderStatusBar.getLayoutParams().height += subItemErrorHeight;
                mPlaceholderStatusBar.getLayoutParams().height += fromDpToPx(8);
                mPlaceholderStatusBar.requestLayout();
                View subItemIcon = subItem.findViewById(R.id.sub_item_status);
                View subItemMark = subItem.findViewById(R.id.sub_item_mark);
                subItemIcon.setVisibility(VISIBLE);
                subItemMark.setVisibility(INVISIBLE);
                subItemError.setVisibility(VISIBLE);
                showErrorIcon();
                if (mOnVerificationFinishListener != null) {
                    mOnVerificationFinishListener.verificationFinish(true);
                }
            });
        }

    }

    private void showVerifiedInfo() {
        mVerifiedInfo.setVisibility(VISIBLE);
    }

    private int fromDpToPx(float dp) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, getResources().getDisplayMetrics());
    }
}
