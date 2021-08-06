package com.android.libview.view.banner.core;

import androidx.annotation.IntRange;
import androidx.annotation.NonNull;
import androidx.lifecycle.Lifecycle;
import androidx.viewpager2.widget.ViewPager2;

import java.util.List;

/**
 * 关于 duration 和 interval
 * 在Handler 执行时 就开始 执行动画,并消费 interval. 所以间隔和动画时间模型如下
 * #handler run runnable   ->  | <------执行动画,持续时间决定因素: duration------>        |run runnable   -> |<------->
 * #                           | <------等待下次执行,时间长度决定因素: interval------>     |                  |<------------->
 * 上面的条件是 : duration < interval,如果 duration == interval ,那么就可实现 连续滚动的效果 >.<
 * 这里是禁止  1.duration > interval , 2.interval < 2_000
 * ### 但是由于handler.sendMessage()本身有延迟,会导致我的interval控制的间隔将大于它的设值!!!
 *
 * @param <T>
 */
public interface IBanner<T> {
    /**
     * 设置自动播放
     * 自动播放和轮询是两个不太相关的属性,开启自动播放后 banner 会不断的播放下一个页面,
     * (关于 loop:)直到播放到最后一个页面后,如果loop==false,就停止播放,否则会循环到第一个页面继续播放
     *
     * @param autoPlay
     */
    void setAutoPlay(boolean autoPlay);

    /**
     * 设置循环播放
     *
     * @param loop true 允许 banner 循环播放,否则 autoPlay==true 时也只会播放到最后一张图
     */
    void setLoop(boolean loop);

    /**
     * 设置滚动间隔时间
     *
     * @param interval
     */
    void setInterval(@IntRange(from = 2_000) int interval);

    /**
     * 设置动画持续时间
     *
     * @param duration
     */
    void setDuration(int duration);

    /**
     * 将ViewPager 页面滚动监听暴露出来
     *
     * @param onPageChangeCallback
     */
    void registerOnPageChangeCallback(ViewPager2.OnPageChangeCallback onPageChangeCallback);

    /**
     * 设置自定义View,并将View填充交给用户
     *
     * @param adapter
     */
    void setAdapter(IAdapter<T> adapter);

    /**
     * 更新数据
     *
     * @param data
     */
    void update(List<T> data);

    /**
     * 设置生命监听
     *
     * @param lifecycle
     */
    void setLifecycle(@NonNull Lifecycle lifecycle);

    /**
     * 开始自动播放
     */
    void start();

    /**
     * 暂停自动播放
     */
    void stop();

}
