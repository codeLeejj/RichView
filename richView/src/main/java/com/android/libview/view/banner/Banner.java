package com.android.libview.view.banner;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.Lifecycle;
import androidx.viewpager2.widget.ViewPager2;

import com.android.libview.R;
import com.android.libview.view.banner.core.BannerAdapter;
import com.android.libview.view.banner.core.BannerDelegate;
import com.android.libview.view.banner.core.IAdapter;
import com.android.libview.view.banner.core.IBanner;

import java.util.List;

/**
 * 详细介绍见{@link IBanner}
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
        setAutoPlay(autoPlay);
        setLoop(loop);
        setInterval(interval);
        setDuration(duration);
        attributes.recycle();
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
