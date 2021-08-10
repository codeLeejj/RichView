package com.android.libview.view.banner.indicator;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.android.libview.view.banner.core.AIndicator;

public class DotIndicator extends AIndicator {

    public DotIndicator(@NonNull Context context) {
        super(context);
    }

    public DotIndicator(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public DotIndicator(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public void init(Context context) {
        mResources = context.getResources();
        defaultDrawable = mResources.getDrawable(getDefaultDrawable());
        selectedDrawable = mResources.getDrawable(getSelectedDrawable());

        defaultPaint = new Paint();
        defaultPaint.setColor(Color.WHITE);
        defaultPaint.setAntiAlias(true);
        defaultPaint.setStyle(Paint.Style.FILL);
        defaultPaint.setTextSize(16);

        selectedPaint = new Paint();

        if (getBackground() == null) {
            setBackgroundColor(Color.parseColor("#00000000"));
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        int hMode = MeasureSpec.getMode(heightMeasureSpec);
        int hSize;
        if (hMode == MeasureSpec.AT_MOST || hMode == MeasureSpec.EXACTLY) {
            hSize = itemWidth + itemInterval * 2;
        } else {
            hSize = MeasureSpec.getSize(heightMeasureSpec);
        }

        int wMode = MeasureSpec.getMode(widthMeasureSpec);
        int wSize;
        if (wMode == MeasureSpec.AT_MOST || wMode == MeasureSpec.EXACTLY) {
            int total = getTotal();
            if (0 == total) {
                wSize = 0;
            } else {
                wSize = (itemWidth + itemInterval) * total + itemInterval;
            }
        } else {
            wSize = MeasureSpec.getSize(heightMeasureSpec);
        }
        mWidth = wSize - getPaddingRight() - getPaddingLeft();
        mHeight = hSize - getPaddingTop() - getPaddingBottom();
        setMeasuredDimension(wSize, hSize);
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
    }

    @Override
    public void changeIndex(int index) {
        invalidate();
    }

    @Override
    public void updateCount() {
        requestLayout();
        invalidate();
    }

    @Override
    public void pageScrolled(int position, float positionOffset) {
        this.positionOffset = positionOffset;
        this.position = position;
        invalidate();
    }

    @Override
    public void updateDefault() {
        defaultDrawable = mResources.getDrawable(getDefaultDrawable());
        invalidate();
    }

    @Override
    public void updateSelected() {
        selectedDrawable = mResources.getDrawable(getSelectedDrawable());
        invalidate();
    }

    Resources mResources;
    Drawable defaultDrawable, selectedDrawable;
    Paint defaultPaint, selectedPaint;

    /**
     * 可用空间
     */
    int mWidth, mHeight;

    int itemInterval = 15;
    int itemWidth = 20;


    float positionOffset;
    int position;

    /***
     *计算的中间过程不要取整,最后使用时取整,让动效更平顺
     * @param canvas
     */
    @Override
    protected void onDraw(Canvas canvas) {
//        super.onDraw(canvas);
        //1.根据总数画出默认dot
        for (int i = 0; i < getTotal(); i++) {
            int left = itemInterval + (itemInterval + itemWidth) * i;
            defaultDrawable.setBounds(left + 1, itemInterval + 1,
                    left + itemWidth - 1, itemInterval + itemWidth - 1);
            defaultDrawable.draw(canvas);
        }
        //2.画出选中的dot
        float abs = 0.5f - Math.abs(0.5f - positionOffset);//abs-> (0.5 - 0 - 0.5)    * 2
        float dotScaleWidth = itemWidth * abs / 2;
        float xOffset = (itemWidth + itemInterval) * positionOffset;


        int rectLeft = itemInterval + (itemInterval + itemWidth) * position;
        float realLeft = rectLeft + xOffset + dotScaleWidth;
        float realRight = rectLeft + itemWidth + (int) (xOffset - dotScaleWidth);


        if (realLeft + itemWidth / 2 > mWidth) {
            realLeft -= mWidth;
            realRight -= mWidth;
        }

        selectedDrawable.setBounds((int) realLeft, itemInterval + (int) dotScaleWidth,
                (int) realRight, itemInterval + itemWidth - (int) dotScaleWidth);
        selectedDrawable.draw(canvas);
    }
}
