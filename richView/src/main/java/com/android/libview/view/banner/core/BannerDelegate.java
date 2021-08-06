package com.android.libview.view.banner.core;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.os.Handler;
import android.view.animation.LinearInterpolator;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleEventObserver;
import androidx.viewpager2.widget.ViewPager2;

import com.android.libview.view.banner.Banner;

import java.util.List;

public class BannerDelegate<T> implements IBanner<T> {
    /**
     * 自动播放
     */
    private boolean autoPlay;
    /**
     * 循环播放
     */
    private boolean loop;
    /**
     * 动画间隔时间(单位: 毫秒)
     */
    private int interval;
    /**
     * 动画持续时间(单位: 毫秒)
     */
    private int duration;

    private final ViewPager2 mViewPager;

    private final BannerAdapter<T> mAdapter;
    public BannerDelegate(Context mContext, Banner<T> mBanner) {
        mViewPager = new ViewPager2(mContext);
        mViewPager.setOrientation(ViewPager2.ORIENTATION_HORIZONTAL);
        mAdapter = new BannerAdapter<>();
        mViewPager.setAdapter(mAdapter);
        mBanner.addView(mViewPager, new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT));

        mViewPager.postDelayed(() -> initAnimator(), 1_000);
    }

    public boolean isAutoPlay() {
        return autoPlay;
    }

    public void setAutoPlay(boolean autoPlay) {
        if (this.autoPlay == autoPlay) {
            return;
        }
        this.autoPlay = autoPlay;
        mAdapter.setAutoPlay(autoPlay);
        if (autoPlay) {
            start();
        } else {
            stop();
        }
    }

    public boolean isLoop() {
        return loop;
    }

    /**
     * 我考虑在设值时重置,还是在最后一张判断
     * 设值时重置 会出现页面跳动的问题,这需要解决!!!!
     *
     * @param loop true 允许 banner 循环播放,否则 autoPlay==true 时也只会播放到最后一张图
     */
    public void setLoop(boolean loop) {
        if (this.loop == loop) {
            return;
        }
        this.loop = loop;
        mAdapter.setLoop(loop);
        if (mViewPager.isFakeDragging()) {
            needReset = true;
        } else {
            int offset = mAdapter.getRealIndex(mViewPager.getCurrentItem());
            mViewPager.setCurrentItem(mAdapter.getFirstItem() + offset, false);
        }
        if(loop){
            start();
        }
        //Cannot change current item when ViewPager2 is fake dragging
    }

    public int getInterval() {
        return interval;
    }

    public void setInterval(int interval) {
        if (duration > interval) {
            throw new IllegalArgumentException("The value of duration cannot be greater than that of interval!!!");
        }
        this.interval = interval;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        if (duration > interval) {
            throw new IllegalArgumentException("The value of duration cannot be greater than that of interval!!!");
        }
        this.duration = duration;
    }

    @Override
    public void setLifecycle(@NonNull Lifecycle lifecycle) {
        lifecycle.addObserver((LifecycleEventObserver) (source, event) -> {
            if (Lifecycle.Event.ON_RESUME == event) {
                start();
            } else if (Lifecycle.Event.ON_PAUSE == event) {
                stop();
            }
        });
    }

    @Override
    public void registerOnPageChangeCallback(ViewPager2.OnPageChangeCallback onPageChangeCallback) {
//        this.mPageChangeCallback = onPageChangeCallback;
        mViewPager.registerOnPageChangeCallback(onPageChangeCallback);
    }

    @Override
    public void setAdapter(IAdapter<T> adapter) {
        mAdapter.setAppearanceAdapter(adapter);
        if (autoPlay) {
            handler.postDelayed(runnable, interval);
        }
        mViewPager.setCurrentItem(mAdapter.getFirstItem(), false);
    }

    @Override
    public void update(List<T> data) {
        mAdapter.setData(data);
        start();
        mViewPager.setCurrentItem(mAdapter.getFirstItem(), false);
    }

    private Handler handler = new Handler();
    private Runnable runnable = this::scroll2Next;
    private boolean needReset;

    /**
     * 滑动到下一个页面
     */
    private void scroll2Next() {
        if (!loop) {
            int realIndex = mAdapter.getRealIndex(mViewPager.getCurrentItem());
            if (realIndex == mAdapter.getItemCount() - 1)
                return;
        }
        previousValue = 0;
        animator.setDuration(duration);
        animator.start();
//        mViewPager.setCurrentItem(mViewPager.getCurrentItem() + 1, true);
        handler.postDelayed(runnable, interval);
    }

    /**
     * 保存前一个animatedValue
     */
    private static int previousValue;

    /**
     * 使用单例避免创建耗时,导致 duration 控制的时间实际上过多的大于这个设值!!
     */
    ValueAnimator animator;

    private void initAnimator() {
        int pagePxWidth = mViewPager.getWidth();
        animator = ValueAnimator.ofInt(0, pagePxWidth);
        animator.addUpdateListener(animation -> {
            int currentValue = (int) animation.getAnimatedValue();
            float currentPxToDrag = (float) (currentValue - previousValue);
            mViewPager.fakeDragBy(-currentPxToDrag);
            previousValue = currentValue;
        });
        animator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
                mViewPager.beginFakeDrag();
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                mViewPager.endFakeDrag();
                if (needReset) {
                    int offset = mAdapter.getRealIndex(mViewPager.getCurrentItem());
                    mViewPager.endFakeDrag();
                    mViewPager.setCurrentItem(mAdapter.getFirstItem() + offset, false);
                    needReset = false;
                }
            }

            @Override
            public void onAnimationCancel(Animator animation) {
            }

            @Override
            public void onAnimationRepeat(Animator animation) {
            }
        });
        animator.setInterpolator(new LinearInterpolator());
    }

    /**
     * 开始自动播放
     */
    @Override
    public void start() {
        if (autoPlay) {
            stop();
            handler.postDelayed(runnable, interval);
        }
    }

    /**
     * 停止自动播放
     */
    @Override
    public void stop() {
        handler.removeCallbacksAndMessages(null);
    }
}
