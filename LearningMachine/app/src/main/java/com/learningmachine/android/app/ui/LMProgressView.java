package com.learningmachine.android.app.ui;

import android.content.Context;
import android.support.constraint.ConstraintLayout;
import android.util.AttributeSet;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.learningmachine.android.app.R;
import com.smallplanet.labalib.Laba;

public class LMProgressView extends ConstraintLayout {

    // Default constructor override
    public LMProgressView(Context context) {
        this(context, null);
    }

    // Default constructor when inflating from XML file
    public LMProgressView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    // Default constructor override
    public LMProgressView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

        ConstraintLayout.LayoutParams lp = new ConstraintLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);

        ImageView background = new ImageView(context);
        background.setLayoutParams(lp);
        background.setImageResource(R.drawable.ic_progress_background);
        this.addView(background);

        ImageView bar1 = new ImageView(context);
        bar1.setLayoutParams(lp);
        bar1.setImageResource(R.drawable.ic_progress_bar);
        this.addView(bar1);

        ImageView bar2 = new ImageView(context);
        bar2.setLayoutParams(lp);
        bar2.setImageResource(R.drawable.ic_progress_bar);
        this.addView(bar2);


        Laba.Animate(bar1, "d2r1024l-1", null);
        Laba.Animate(bar2, "d1.3r360l-1", null);
    }

}