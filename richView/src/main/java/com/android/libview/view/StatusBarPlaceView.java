package com.android.libview.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.os.Build;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;

import com.android.libview.R;
import com.android.libview.utils.WindowHelper;

public class StatusBarPlaceView extends View {
    public StatusBarPlaceView(Context context) {
        super(context);
        initView(context);
    }

    int mColor;

    public StatusBarPlaceView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.StatusBarPlaceView);
        mColor = typedArray.getColor(R.styleable.StatusBarPlaceView_backgroundColor, -1);
        typedArray.recycle();
        initView(context);
    }

    private void initView(Context context) {
        if (mColor != -1) {
            setBackgroundColor(mColor);
        } else {
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {//6.0
                setBackgroundColor(Color.BLACK);
            } else {
                setBackgroundColor(Color.WHITE);
            }
        }
    }

    @Override
    protected void onAttachedToWindow() {
        setMyHeight();
        super.onAttachedToWindow();
    }

    private void setMyHeight() {
        int barHeight = WindowHelper.getStatusBarHeight(getContext());
        if (barHeight != 0) {
            ViewGroup.LayoutParams layoutParams = getLayoutParams();
            if (layoutParams == null) {
                layoutParams = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, barHeight);
            } else {
                layoutParams.height = barHeight;
            }
            setLayoutParams(layoutParams);
        }
    }
}
