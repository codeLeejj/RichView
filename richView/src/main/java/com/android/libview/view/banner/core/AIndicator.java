package com.android.libview.view.banner.core;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.android.libview.R;

/**
 * 定义指示器
 * 注意: 指示器的颜色和图标应该是同时使用,需求特殊除外
 * <p>
 * 自定义Indicator:
 * 1.确定选中和默认的 颜色或图标
 * 2.关注一下位置的改变{@link AIndicator#changeIndex(int index)} 和 {@link AIndicator#updateCount()}
 * 3.如果您要做切换动画,应该关注{@link AIndicator#pageScrolled(int position, float positionOffset)}
 */
public abstract class AIndicator extends FrameLayout {
    /**
     * 设置默认色
     */
    public static final int DEFAULT_COLOR = Color.parseColor("#222222");

    /**
     * 选中后的颜色
     */
    public static final int SELECTED_COLOR = Color.YELLOW;

    /**
     * 当前位置
     */
    private int index;
    /**
     * 分页数
     */
    private int total;

    /**
     * 指示器默认的颜色
     */
    private int defaultColor = DEFAULT_COLOR;
    /**
     * 指示器选中后的颜色
     */
    private int selectedColor = SELECTED_COLOR;

    private int defaultDrawable = R.drawable.sp_dot_default;
    private int selectedDrawable = R.drawable.sp_dot_selected;

    public AIndicator(@NonNull Context context) {
        super(context);
        commonInitialize(context);
    }

    public AIndicator(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        commonInitialize(context);
    }

    public AIndicator(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        commonInitialize(context);
    }

    private void commonInitialize(Context context) {
        if (getBackground() == null) {
            setBackgroundColor(Color.parseColor("#00000000"));
        }
        init(context);
    }

    public abstract void init(Context context);

    public int getDefaultColor() {
        return defaultColor;
    }

    public void setDefaultColor(int defaultColor) {
        this.defaultColor = defaultColor;
        updateDefault();
    }

    public int getSelectedColor() {
        return selectedColor;
    }

    public void setSelectedColor(int selectedColor) {
        this.selectedColor = selectedColor;
        updateSelected();
    }

    public int getDefaultDrawable() {
        return defaultDrawable;
    }

    public void setDefaultDrawable(int defaultDrawable) {
        this.defaultDrawable = defaultDrawable;
        updateDefault();
    }

    public int getSelectedDrawable() {
        return selectedDrawable;
    }

    public void setSelectedDrawable(int selectedDrawable) {
        this.selectedDrawable = selectedDrawable;
        updateSelected();
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
        changeIndex(index);
    }

    protected void setTotal(int total) {
        this.total = total;
        requestLayout();
        updateCount();
    }

    public int getTotal() {
        return total;
    }

    /**
     * 更新了默认 色/图标
     */
    public abstract void updateDefault();

    /**
     * 更新了选中 色/图标
     */
    public abstract void updateSelected();

    /**
     * 实现层实现update的逻辑,不用考虑向父View申请
     */
    public abstract void updateCount();

    /**
     * banner 的位置已经发生变化
     *
     * @param index 变化后的位置
     */
    public abstract void changeIndex(int index);

    /**
     * 页面正在滑动
     *
     * @param position       当前位置
     * @param positionOffset 滑动的偏移量 百分比
     */
    public abstract void pageScrolled(int position, float positionOffset);
}
