package com.android.libview.view.recyclerview;

public interface ListItemClickListener<T> {
    void onItemClick(T t, int position);
}