package com.android.libview.view.banner.core;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

/**
 * 定义指示器
 */
public abstract class AIndicator extends FrameLayout {
    /**
     * 当前位置
     */
    private int index;
    /**
     * 分页数
     */
    private int total;

    public AIndicator(@NonNull Context context) {
        super(context);
        init(context);
    }

    public AIndicator(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public AIndicator(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    protected void setTotal(int total) {
        this.total = total;

    }

    public int getTotal() {
        return total;
    }

    public abstract void init(Context context);

    /**
     * banner 的位置已经发生变化
     * @param index 变化后的位置
     */
    public abstract void changeIndex(int index);

    /**
     * 页面正在滑动
     * @param positionOffset 滑动的偏移量
     * @param toRight true:向右滑动,false:向左滑动
     */
    public abstract void pageScrolled(float positionOffset, boolean toRight);

}
