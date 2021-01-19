package com.android.libview.view.selector;

public interface TitleBuilder<T> extends ItemClick<T> {
    String itemTitle(T t);
}
