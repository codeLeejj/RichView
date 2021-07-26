package com.android.libview.view.refreshAndLoadView.contract;

/**
 * author:Lee
 * date:2021/7/21
 * Describe:
 */
public interface DynamicView {
    void init();

    /**
     * 接受状态的改变以便开发者扩展
     *
     * @param state
     */
    void setState(State state);
}
