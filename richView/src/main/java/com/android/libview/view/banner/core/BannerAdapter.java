package com.android.libview.view.banner.core;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.IdRes;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class BannerAdapter<T> extends RecyclerView.Adapter<BannerAdapter.BannerViewHolder> {
    /**
     * 自动播放
     */
    private boolean autoPlay;
    /**
     * 循环播放
     */
    private boolean loop;

    public BannerAdapter() {
    }

    public void setData(List<T> data) {
        if (mAppearanceAdapter == null) {
            throw new IllegalStateException("You should invoke Banner.setAdapter(IAdapter) to initialize layout first!");
        }
        mAppearanceAdapter.setData(data);
        notifyDataSetChanged();
    }

    IAdapter<T> mAppearanceAdapter;

    public void setAppearanceAdapter(IAdapter<T> adapter) {
        mAppearanceAdapter = adapter;
    }

    public void setAutoPlay(boolean autoPlay) {
        this.autoPlay = autoPlay;
    }

    public void setLoop(boolean loop) {
        this.loop = loop;
    }

    @NonNull
    @Override
    public BannerAdapter.BannerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (mAppearanceAdapter == null)
            return new BannerAdapter.BannerViewHolder(new View(parent.getContext()));
        View view = LayoutInflater.from(parent.getContext()).inflate(mAppearanceAdapter.getViewId(viewType), parent, false);
        return new BannerAdapter.BannerViewHolder(view);
    }

    @Override
    public int getItemViewType(int position) {
        if (mAppearanceAdapter == null) {
            return 0;
        }
        return mAppearanceAdapter.getViewType(mAppearanceAdapter.getData().get(getRealIndex(position)));
    }

    @Override
    public void onBindViewHolder(@NonNull BannerAdapter.BannerViewHolder holder, int position) {
        if (mAppearanceAdapter != null) {
            mAppearanceAdapter.fillView(holder, mAppearanceAdapter.getData().get(getRealIndex(position)));
        }
    }

    @Override
    public int getItemCount() {
        if (mAppearanceAdapter == null || getRealCount() == 0)
            return 0;
        if (!loop)
            return getRealCount();
        //无限轮播关键所在
        return mAppearanceAdapter.getData() == null ? 0 : (autoPlay ? Integer.MAX_VALUE : (loop ? Integer.MAX_VALUE : getRealCount()));
    }

    private int getRealCount() {
        if (mAppearanceAdapter == null)
            return 0;
        return mAppearanceAdapter.getData() == null ? 0 : mAppearanceAdapter.getData().size();
    }

    public int getRealIndex(int position) {
        if (mAppearanceAdapter == null || getRealCount() == 0)
            return position;
        int offset = position - getFirstItem();
        int index = offset % getRealCount();
        if (index < 0) {
            index += getRealCount();
        }
        return index;
    }

    /**
     * 获取初次展示的item位置
     *
     * @return
     */
    public int getFirstItem() {
        if (mAppearanceAdapter == null || getRealCount() == 0 || !loop)
            return 0;
        return Integer.MAX_VALUE / 2 - (Integer.MAX_VALUE / 2) % getRealCount();
    }

    public static class BannerViewHolder extends RecyclerView.ViewHolder {

        public BannerViewHolder(@NonNull View itemView) {
            super(itemView);
        }

        public <T> T getView(@IdRes int viewId) {
            return (T) itemView.findViewById(viewId);
        }
    }
}
