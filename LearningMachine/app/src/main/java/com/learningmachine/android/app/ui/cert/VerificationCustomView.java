package com.learningmachine.android.app.ui.cert;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.widget.LinearLayout;

import com.learningmachine.android.app.data.verifier.VerificationSteps;
import com.learningmachine.android.app.data.verifier.VerifierStatus;

import java.util.LinkedList;
import java.util.Queue;

public class VerificationCustomView extends LinearLayout {
    private VerificationSteps[] mVerificationSteps;
    private Queue<VerifierStatus> mStatusQueue;
    private boolean mIsAnimating;
    private OnVerificationFinish mOnVerificationFinishListener;

    public VerificationCustomView(Context context) {
        super(context);
    }

    public VerificationCustomView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public VerificationCustomView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    /**
     * Notifies caller when the verification finishes.
     */
    interface OnVerificationFinish {
        /**
         * Called when verification finishes.
         * @param withError True if there is an error in the verification.
         */
        void verificationFinish(boolean withError);
    }

    /**
     * Sets the listener for Verification finishing event.
     * @param onVerificationFinishListener The listener.
     */
    public void setOnVerificationFinishListener(OnVerificationFinish onVerificationFinishListener) {
        mOnVerificationFinishListener = onVerificationFinishListener;
    }

    /**
     * Will populate the screen with verification steps in Idle state.
     * @param verificationSteps The steps to be added.
     */
    public void addVerificationSteps(VerificationSteps[] verificationSteps) {
        removeAllViews();
        mVerificationSteps = verificationSteps;
        mStatusQueue = new LinkedList<>();
        mIsAnimating = false;
        for (int i = 0; i < verificationSteps.length; i++) {
            VerificationSteps verificationStep = verificationSteps[i];
            VerificationCustomItem verificationCustomItem = new VerificationCustomItem(getContext());
            verificationCustomItem.setItemTitle(verificationStep.label);
            verificationCustomItem.setTag(verificationStep.code);
            verificationCustomItem.setIsFirstItem(i == 0);
            verificationCustomItem.setIsLastItem(i == verificationSteps.length-1);
            verificationCustomItem.setOnVerificationFinishListener(withError -> {
                mOnVerificationFinishListener.verificationFinish(withError);
                //We need to call this to show elements correctly after verification ends.
                reconstructViews();
            });
            addView(verificationCustomItem);

            for (VerificationSteps.SubSteps verificationSubStep :
                    verificationStep.subSteps) {
                verificationCustomItem.addSubItem(verificationSubStep.label,
                        verificationSubStep.code, verificationStep.subSteps.length);
            }

        }
    }

    /**
     * Will reconstruct the sub items marks.
     */
    public void reconstructViews() {
        for (int i = 0; i < getChildCount(); i++) {
            VerificationCustomItem item = (VerificationCustomItem) getChildAt(i);
            item.reconstructView();
        }
    }

    /**
     * Will activate sub steps, making them darker and changing icons accordingly to status.
     * @param status The status to activate a step, with success or failure.
     */
    public void activateSubStep(VerifierStatus status) {
        if (status.isStarting()) {
            return;
        }
        if (mIsAnimating) {
            mStatusQueue.add(status);
        } else {
            animateItem(status);
        }
    }

    /**
     * Coordinates items animations in sequence.
     * @param status The status to activate a step, with success or failure.
     */
    private void animateItem(VerifierStatus status) {
        mIsAnimating = true;
        VerificationSteps.SubSteps subSteps = getSubStepFromCode(status.code);
        String parentStep = "";
        if (subSteps != null) {
            parentStep = subSteps.parentStep;
        }
        VerificationCustomItem itemWithTag = findViewWithTag(parentStep);
        if (itemWithTag != null) {
            itemWithTag.activateSubItem(status, () -> {
                if (mStatusQueue.size() == 0) {
                    mIsAnimating = false;
                } else {
                    animateItem(mStatusQueue.poll());
                }
            });
        }
    }

    /**
     * Helper method to get a sub status from code.
     * @param code The code for the sub status.
     * @return The sub status.
     */
    private VerificationSteps.SubSteps getSubStepFromCode(String code) {
        for (VerificationSteps step :
                mVerificationSteps) {
            for (VerificationSteps.SubSteps subStep:
                    step.subSteps) {
                if (subStep.code.equals(code)) {
                    return subStep;
                }
            }
        }
        return null;
    }

}
