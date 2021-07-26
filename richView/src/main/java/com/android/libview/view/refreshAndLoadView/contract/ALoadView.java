package com.android.libview.view.refreshAndLoadView.contract;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

/**
 * author:Lee
 * date:2021/7/21
 * Describe:
 */
public abstract class ALoadView  extends FrameLayout implements DynamicView {

    public ALoadView(@NonNull Context context) {
        super(context);
    }

    public ALoadView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public ALoadView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

}
