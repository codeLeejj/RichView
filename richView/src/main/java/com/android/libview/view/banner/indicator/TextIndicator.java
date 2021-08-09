package com.android.libview.view.banner.indicator;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.android.libview.R;
import com.android.libview.view.banner.core.AIndicator;

public class TextIndicator extends AIndicator {

    public TextIndicator(@NonNull Context context) {
        super(context);
    }

    public TextIndicator(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public TextIndicator(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    TextView textView;

    @Override
    public void init(Context context) {
        if (getBackground() == null) {
            setBackgroundResource(R.drawable.sp_bg_white);
        }

        textView = new TextView(context);
        textView.setTextColor(Color.parseColor("#333333"));
        textView.setTextSize(14f);
        textView.setGravity(Gravity.CENTER);
        textView.setPadding(25, 1, 25, 1);

        FrameLayout.LayoutParams layoutParams = new LayoutParams(120, ViewGroup.LayoutParams.WRAP_CONTENT);
        addView(textView, layoutParams);
    }

    @Override
    public void pageScrolled(float positionOffset, boolean toRight) {

    }

    @Override
    public void changeIndex(int index) {
        if (getTotal() == 0) {
            textView.setText("");
        } else {
            textView.setText((index + 1) + "/" + getTotal());
        }
    }
}
