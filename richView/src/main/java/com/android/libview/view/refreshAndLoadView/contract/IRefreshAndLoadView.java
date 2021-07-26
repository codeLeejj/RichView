package com.android.libview.view.refreshAndLoadView.contract;

import com.android.libview.view.refreshAndLoadView.RefreshAndLoadView;

/**
 * author:Lee
 * date:2021/7/21
 * Describe: 定义下拉刷新.上拉加载 视图
 */
public interface IRefreshAndLoadView {
    /**
     * 设置 刷新视图
     */
    void setRefreshView(ARefreshView refreshView);

    /**
     * 设置 刷新视图 并设置刷新高度
     *
     * @param refreshView
     * @param refreshHeight 设置刷新高度,单位:像素
     */
    void setRefreshView(ARefreshView refreshView, int refreshHeight);

    void setRefreshListener(RefreshAndLoadView.RefreshListener refreshListener);

    /**
     * 设置是否可以刷新
     *
     * @param refreshAble
     */
    void setRefreshAble(boolean refreshAble);

    /**
     * 通知 刷新执行完毕
     */
    void refreshComplete();


    /*******************************************加载更多**************************************
     * 设置 加载视图
     */
    void setLoadView(ALoadView loadView);

    /**
     * 设置 加载视图 并设置刷新高度
     *
     * @param loadView
     * @param loadHeight 设置加载高度,单位:
     */
    void setLoadView(ALoadView loadView, int loadHeight);

    void setLoadListener(RefreshAndLoadView.LoadListener loadListener);

    /**
     * 设置是否可以刷新
     *
     * @param refreshAble
     */
    void setLoadAble(boolean refreshAble);

    /**
     * 通知 刷新执行完毕
     */
    void loadComplete();
}
