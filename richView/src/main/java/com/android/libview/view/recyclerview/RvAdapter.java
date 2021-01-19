package com.android.libview.view.recyclerview;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public abstract class RvAdapter<T> extends RecyclerView.Adapter<BaseViewHolder> {
    /**
     * 设置视图
     *
     * @param holder
     * @param position
     */
    public abstract void bindView(BaseViewHolder holder, int position, T item);

    /**
     * 获取Item view 的布局文件
     *
     * @return
     */
    public abstract int getLayoutId();

    protected Context mContext;
    List<T> mDatas;
    protected ListItemClickListener<T> itemClickListener;

    public RvAdapter(Context mContext, ListItemClickListener<T> itemClickListener) {
        if (mContext == null) throw new RuntimeException("RvAdapter 上下文不能为空。");
        this.mContext = mContext;
        this.itemClickListener = itemClickListener;
        mDatas = new ArrayList<>();
    }

    public RvAdapter(Context mContext) {
        this.mContext = mContext;
        mDatas = new ArrayList<>();
    }

    public T getItem(int position) {
        return mDatas.get(position);
    }

    public void setDatas(List<T> mDatas) {
        this.mDatas.clear();
        if (mDatas == null) {
            notifyDataSetChanged();
            return;
        }
        this.mDatas.addAll(mDatas);
        notifyDataSetChanged();
    }

    public void removeData(int position) {
        if (mDatas == null) return;
        this.mDatas.remove(position);
        notifyDataSetChanged();
    }

    public void clearItems() {
        if (mDatas == null) return;
        this.mDatas.clear();
        notifyDataSetChanged();
    }

    public void addData(T t) {
        if (mDatas == null) return;
        this.mDatas.add(t);
        notifyDataSetChanged();
    }

    public void addDatas(List<T> list) {
        if (mDatas == null) return;
        this.mDatas.addAll(list);
        notifyDataSetChanged();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @NonNull
    @Override
    public BaseViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        getLayoutId();
        View view = LayoutInflater.from(mContext).inflate(getLayoutId(), parent, false);
        BaseViewHolder holder = new BaseViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull BaseViewHolder holder, int position) {
        if (itemClickListener != null) {
            holder.itemView.setOnClickListener(v -> itemClickListener.onItemClick(getItem(position), position));
        }
        bindView(holder, position, getItem(position));
    }

    @Override
    public int getItemCount() {
        return mDatas == null ? 0 : mDatas.size();
    }

    public List<T> getItems() {
        return mDatas;
    }

}
