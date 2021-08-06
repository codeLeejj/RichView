package com.android.libview.view.banner.core;

import androidx.annotation.LayoutRes;
import androidx.annotation.NonNull;

import java.util.List;

/**
 * 将布局,数据源,View填充交给用户,实现最大化的定制
 *
 * @param <T>
 */
public abstract class IAdapter<T> {

    List<T> data;

    public IAdapter(List<T> data) {
        this.data = data;
    }

    public List<T> getData() {
        return data;
    }

    public void setData(List<T> data) {
        this.data = data;
    }

    /**
     * 定义布局
     *
     * @return
     */
    @LayoutRes
    public abstract int getViewId(int viewType);

    /**
     * 将View回执给用户自己填充
     *
     * @param holder
     * @param t
     */
    public abstract void fillView(@NonNull BannerAdapter.BannerViewHolder holder, @NonNull T t);

    public int getViewType(T t) {
        return 0;
    }

}
