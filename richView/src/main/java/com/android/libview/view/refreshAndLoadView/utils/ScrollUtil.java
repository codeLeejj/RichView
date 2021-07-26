package com.android.libview.view.refreshAndLoadView.utils;

import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.android.libview.view.refreshAndLoadView.contract.ALoadView;
import com.android.libview.view.refreshAndLoadView.contract.ARefreshView;

/**
 * author:Lee
 * date:2021/7/22
 * Describe:
 */
public class ScrollUtil {
    /**
     * 获取 ViewGroup中的列表视图
     *
     * @param viewGroup
     * @return
     */
    public static View getScrollView(@NonNull ViewGroup viewGroup) {
        for (int i = 0; i < viewGroup.getChildCount(); i++) {
            View child = viewGroup.getChildAt(i);
            if (child instanceof ARefreshView || child instanceof ALoadView) {
                continue;
            }
            if (child instanceof AdapterView || child instanceof RecyclerView) {
                return child;
            } else {
                if (child instanceof ViewGroup) {
                    return getScrollView((ViewGroup) child);
                }
            }
        }
        return null;
    }

    /**
     * 判断 列表视图 是否发生滑动
     *
     * @param scroller
     * @return
     */
    public static boolean hadScrolled(@NonNull View scroller) {
        if (scroller instanceof RecyclerView) {
            RecyclerView rv = (RecyclerView) scroller;
            View childAt = rv.getChildAt(0);
            int position = rv.getChildAdapterPosition(childAt);
            return position != 0 || childAt.getTop() != 0;
        } else if (scroller instanceof AdapterView) {
            AdapterView adapterView = (AdapterView) scroller;
            View childAt1 = adapterView.getChildAt(0);
            if (adapterView.getFirstVisiblePosition() != 0 || childAt1 != null && childAt1.getTop() <= 0) {
                return true;
            }
        } else {
            if (scroller.getScrollY() > 0) {
                return true;
            }
        }
        return false;
    }

    /**
     * 判断是否滑动到了底部
     *
     * @param scroller
     * @return
     */
    public static boolean hadScrolledBottom(@NonNull View scroller) {
        if (scroller instanceof RecyclerView) {
            RecyclerView rv = (RecyclerView) scroller;
            int viewCount = rv.getChildCount();
            if (viewCount == 0) {
                return true;//这里改false,还是true呢
            }
            int dataCount = rv.getAdapter().getItemCount();

            View childAt = rv.getChildAt(viewCount - 1);
            int position = rv.getChildAdapterPosition(childAt);
            if ((position + 1) != dataCount) {
                return false;
            }
            int rvTop = rv.getTop();
            //子视图的bottom 是相对于父视图的,所有  父子视图的bottom 本事就不能作比较
            int rvHeight = rv.getMeasuredHeight();
            int lastChildBottom = childAt.getBottom();
            return rvTop <= 0 && rvHeight >= lastChildBottom;
        } else if (scroller instanceof AdapterView) {
            AdapterView adapterView = (AdapterView) scroller;
            int count = adapterView.getCount();
            if (count == 0) {
                return true;//这里改false,还是true呢
            }
            View childAt1 = adapterView.getChildAt(count - 1);
            if (adapterView.getLastVisiblePosition() == count - 1 && childAt1.getBottom() <= scroller.getBottom()) {
                return true;
            }
        } else {
            return false;
        }
        return false;
    }
}
