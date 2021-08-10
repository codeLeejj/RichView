package com.android.libview.view.banner;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.Lifecycle;
import androidx.viewpager2.widget.ViewPager2;

import com.android.libview.R;
import com.android.libview.view.banner.core.BannerDelegate;
import com.android.libview.view.banner.core.IAdapter;
import com.android.libview.view.banner.core.IBanner;
import com.android.libview.view.banner.core.AIndicator;

import java.util.List;

/**
 * 详细介绍见{@link IBanner}
 *
 * @param <T>
 */
public class Banner<T> extends FrameLayout implements IBanner<T> {
    private BannerDelegate<T> bannerDelegate;

    public Banner(@NonNull Context context) {
        super(context);
    }

    public Banner(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public Banner(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        bannerDelegate = new BannerDelegate<>(context, this);
        initCustomAttrs(context, attrs);
    }

    private void initCustomAttrs(Context context, AttributeSet attrs) {
        TypedArray attributes = context.obtainStyledAttributes(attrs, R.styleable.Banner);
        boolean autoPlay = attributes.getBoolean(R.styleable.Banner_autoPlay, true);
        boolean loop = attributes.getBoolean(R.styleable.Banner_loop, true);
        int interval = attributes.getInteger(R.styleable.Banner_interval, 4000);
        int duration = attributes.getInteger(R.styleable.Banner_duration, 1500);
        float xRatio = attributes.getFloat(R.styleable.Banner_indicatorXRatio, -1.f);
        float yRatio = attributes.getFloat(R.styleable.Banner_indicatorYRatio, -1.f);
        int defaultColor = attributes.getColor(R.styleable.Banner_colorDefault, AIndicator.DEFAULT_COLOR);
        int selectedColor = attributes.getColor(R.styleable.Banner_colorSelected, AIndicator.SELECTED_COLOR);
        int defaultDrawable = attributes.getResourceId(R.styleable.Banner_drawableDefault, R.drawable.sp_dot_default);
        int selectedDrawable = attributes.getResourceId(R.styleable.Banner_drawableSelected, R.drawable.sp_dot_selected);

        setAutoPlay(autoPlay);
        setLoop(loop);
        setInterval(interval);
        setDuration(duration);
        /**
         * 这下面的数据该怎么给AIndicator?
         * Builder?  代码就不解耦,使用也麻烦
         * 静态? N个页面的M个Banner 导致只有一个Banner的配置生效
         * <Banner><AIndicator/></Banner>?   这样属性就可以交给AIndicator,创建config交给Banner
         */
        setIndicatorXRatio(xRatio);
        setIndicatorYRatio(yRatio);
        setDefaultColor(defaultColor);
        setSelectedColor(selectedColor);
        setDefaultDrawable(defaultDrawable);
        setSelectedDrawable(selectedDrawable);
        attributes.recycle();
    }

    @Override
    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int mWidth = MeasureSpec.getSize(widthMeasureSpec) - getPaddingRight() - getPaddingLeft();
        int mHeight = MeasureSpec.getSize(heightMeasureSpec) - getPaddingTop() - getPaddingBottom();
        gotSize(mWidth, mHeight);
    }

    @Override
    public void gotSize(int width, int height) {
        bannerDelegate.gotSize(width, height);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_CANCEL || event.getAction() == MotionEvent.ACTION_UP) {
            bannerDelegate.start();
        } else {
            bannerDelegate.stop();
        }
        return super.dispatchTouchEvent(event);
    }

    @Override
    public void setAutoPlay(boolean autoPlay) {
        bannerDelegate.setAutoPlay(autoPlay);
    }

    @Override
    public void setLoop(boolean loop) {
        bannerDelegate.setLoop(loop);
    }

    @Override
    public void setInterval(int interval) {
        bannerDelegate.setInterval(interval);
    }

    @Override
    public void setDuration(int duration) {
        bannerDelegate.setDuration(duration);
    }

    @Override
    public void setDefaultColor(int defaultColor) {
        bannerDelegate.setDefaultColor(defaultColor);
    }

    @Override
    public void setSelectedColor(int selectedColor) {
        bannerDelegate.setSelectedColor(selectedColor);
    }

    @Override
    public void setDefaultDrawable(int defaultDrawable) {
        bannerDelegate.setDefaultDrawable(defaultDrawable);
    }

    @Override
    public void setSelectedDrawable(int selectedDrawable) {
        bannerDelegate.setSelectedDrawable(selectedDrawable);
    }

    @Override
    public void setIndicatorXRatio(float xRatio) {
        bannerDelegate.setIndicatorXRatio(xRatio);
    }

    @Override
    public void setIndicatorYRatio(float yRatio) {
        bannerDelegate.setIndicatorYRatio(yRatio);
    }

    @Override
    public void setIndicator(AIndicator indicator) {
        bannerDelegate.setIndicator(indicator);
    }

    @Override
    public void setIndicator(AIndicator indicator, float xRatio, float yRatio) {
        bannerDelegate.setIndicator(indicator, xRatio, yRatio);
    }

    @Override
    public void setAdapter(IAdapter<T> adapter) {
        bannerDelegate.setAdapter(adapter);
    }

    @Override
    public void update(List<T> data) {
        bannerDelegate.update(data);
    }

    @Override
    public void setLifecycle(@NonNull Lifecycle lifecycle) {
        bannerDelegate.setLifecycle(lifecycle);
    }

    @Override
    public void registerOnPageChangeCallback(ViewPager2.OnPageChangeCallback onPageChangeCallback) {
        bannerDelegate.registerOnPageChangeCallback(onPageChangeCallback);
    }

    @Override
    public void start() {
        bannerDelegate.start();
    }

    @Override
    public void stop() {
        bannerDelegate.stop();
    }
}
