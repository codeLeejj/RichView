package com.android.libview.view.recyclerview;


import android.util.SparseArray;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class BaseViewHolder extends RecyclerView.ViewHolder {
    SparseArray<View> views;

    public BaseViewHolder(@NonNull View itemView) {
        super(itemView);
        views = new SparseArray<>();
    }

    public <T extends View> T getView(int viewId, Class<T> tClass) {
        View view = views.get(viewId);
        if (view == null) {
            view = (T) itemView.findViewById(viewId);
        }
        return (T) view;
    }

    public <T extends View> T getView(int viewId) {
        View view = views.get(viewId);
        if (view == null) {
            view = (T) itemView.findViewById(viewId);
        }
        return (T) view;
    }

    /**
     * 给子节点上的 itemview 设置点击事件
     *
     * @param viewId
     * @param subViewClickListener
     */
    public void bindViewClickListener(int viewId, ListItemSubViewClickListener subViewClickListener) {
        View view = getView(viewId);
        view.setOnClickListener(v -> {
            if (subViewClickListener != null) {
                subViewClickListener.subViewClick(v, getAdapterPosition());
            }
        });
    }

    /**
     * 直接设置数据，后续自行添加
     *
     * @param id
     * @param visibility
     */

    public void setVisibility(int id, int visibility) {
        View view = getView(id);
        view.setVisibility(visibility);
    }

    public void setClick(int id, View.OnClickListener clickListener) {
        getView(id).setOnClickListener(clickListener);
    }

    public void setText(int id, String text) {
        TextView view = getView(id);
        view.setText(text);
    }

    public void setImageRes(int id, int resId) {
        ImageView view = getView(id);
        view.setImageResource(resId);
    }

    public void setBackgroundRes(int id, int resId) {
        View view = getView(id);
        view.setBackgroundResource(resId);
    }

    public void setBackground(int id, int resId) {
        View view = getView(id);
        view.setBackgroundResource(resId);
    }
}
