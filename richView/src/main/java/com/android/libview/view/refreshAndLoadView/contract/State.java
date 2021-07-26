package com.android.libview.view.refreshAndLoadView.contract;

/**
 * author:Lee
 * date:2021/7/21
 * Describe: 对下拉刷新,上拉加载两个视图的状态定义
 */
public enum State {
    /**
     * 初始状态
     */
    STATE_INITIAL,
    /**
     * 正在被下拉
     */
    STATE_PULLING,
    /**
     * 正在刷新
     */
    STATE_REFRESH_OR_LOAD,
    /**
     * 正在收起
     */
    STATE_PACK_UP,
}