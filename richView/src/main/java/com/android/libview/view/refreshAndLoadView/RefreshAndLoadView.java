package com.android.libview.view.refreshAndLoadView;

import android.content.Context;
import android.content.res.Resources;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.LinearInterpolator;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.Scroller;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.android.libview.view.refreshAndLoadView.contract.ALoadView;
import com.android.libview.view.refreshAndLoadView.contract.IRefreshAndLoadView;
import com.android.libview.view.refreshAndLoadView.contract.ARefreshView;
import com.android.libview.view.refreshAndLoadView.contract.State;
import com.android.libview.view.refreshAndLoadView.utils.ScrollUtil;
import com.android.libview.view.refreshAndLoadView.wapper.SimpleLoadView;
import com.android.libview.view.refreshAndLoadView.wapper.SimpleRefreshView;

/**
 * author:Lee
 * date:2021/7/21
 * Describe: 下拉刷新.上拉加载的一个容器
 */
public class RefreshAndLoadView extends FrameLayout implements IRefreshAndLoadView {
    public interface RefreshListener {
        void onRefresh();
    }

    public interface LoadListener {
        void onLoad();
    }

    private enum DYNAMIC {
        /**
         * 初始状态
         */
        DYNAMIC_TYPE_INITIAL,
        /**
         * 下拉刷新状态
         */
        DYNAMIC_TYPE_REFRESH,
        /**
         * 上拉加载状态
         */
        DYNAMIC_TYPE_LOAD;
    }

    /**
     * 默认刷新or加载的高度
     */
    private static final int REFRESH_LOAD_HEIGHT = 55;
    /**
     * 滑动阻尼
     */
    private static final float REFRESH_DAMP = 1.9f;
    private static final float LOAD_DAMP = 2.5f;
    /**
     * 最大滑动距离
     */
    private static final float MAC_SLIDE_HEIGHT = 1000f;
    /**
     * 工具中找到的列表视图
     */
    View scrollView;
    /**
     * 刷新的视图
     */
    private ARefreshView mRefreshView;
    private boolean refreshable;
    /**
     * 头部下拉刷新的触发高度
     */
    private int mRefreshHeight;
    private RefreshListener refreshListener;
    /**
     * 加载的视图
     */
    private ALoadView mLoadView;
    private boolean loadable;
    /**
     * 底部上拉加载的触发高度
     */
    private int mLoadHeight;
    private LoadListener loadListener;
    /**
     * 标记当前处于什么状态
     */
    private DYNAMIC lastDynamicType = DYNAMIC.DYNAMIC_TYPE_INITIAL;
    private DYNAMIC dynamicType = DYNAMIC.DYNAMIC_TYPE_INITIAL;

    /**
     * 当前的上拉加载or下拉刷新的状态
     */
    private State mState = State.STATE_INITIAL;

    /**
     * RefreshAndLoadView 的底部位置
     */
    private int mBottom = 0;

    public RefreshAndLoadView(@NonNull Context context) {
        super(context);
        init();
    }

    public RefreshAndLoadView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public RefreshAndLoadView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        autoScroll = new AutoScroll();
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if ((loadListener == null && refreshListener == null)) {
            return super.dispatchTouchEvent(ev);
        }
        if (mState == State.STATE_REFRESH_OR_LOAD || mState == State.STATE_PACK_UP) {
            return true;
        }
        if (ev.getAction() == MotionEvent.ACTION_CANCEL || ev.getAction() == MotionEvent.ACTION_UP
                || ev.getAction() == MotionEvent.ACTION_POINTER_INDEX_MASK) {
            if (dynamicType == DYNAMIC.DYNAMIC_TYPE_REFRESH) {
                recoverHeader();
                return true;
            } else if (dynamicType == DYNAMIC.DYNAMIC_TYPE_LOAD) {
                recoverTail();
                return true;
            }
            return false;
        }
        boolean consumed = gestureDetector.onTouchEvent(ev);
        if (!consumed) {
            if (dynamicType == DYNAMIC.DYNAMIC_TYPE_INITIAL) {
                Log.w("RefreshAndLoadView", "return false dispatchTouchEvent  2222");
                return super.dispatchTouchEvent(ev);
            }
        }
        return true;
    }

    private GestureDetector gestureDetector = new GestureDetector(getContext(), new GestureDetector.OnGestureListener() {

        @Override
        public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
            //左右滑动
            if (Math.abs(distanceX) > Math.abs(distanceY) && mState == State.STATE_INITIAL) {
                Log.w("RefreshAndLoadView", "return false  111  dynamicType:" + dynamicType);
                return false;
            }
            //正在刷新 or 正在收起  -> 拦截事件   (防止触发第二次刷新和子视图滚动)
            if (mState == State.STATE_REFRESH_OR_LOAD || mState == State.STATE_PACK_UP) {
                return true;
            }
            scrollView = ScrollUtil.getScrollView(RefreshAndLoadView.this);
            if (scrollView == null) {
                Log.w("RefreshAndLoadView", "return false scrollView == null");
                return false;
            }
            boolean hadScrolled = ScrollUtil.hadScrolled(scrollView);
            boolean hadScrolledBottom = ScrollUtil.hadScrolledBottom(scrollView);
            Log.w("RefreshAndLoadView", "onScroll -> hadScrolled:" + hadScrolled + "    hadScrolledBottom:" + hadScrolledBottom);

            boolean handle = false;
            //事件 : 1.上拉,2.下拉
            //状态: 1.初始, 2.内容滑动了,3.头部出来,4.尾部出来了,5.刷新或加载
            int scrollViewTop = scrollView.getTop();
            if (distanceY > 0) {//上拉
                //底部内容
                if (hadScrolled) {
                    if (hadScrolledBottom && dynamicType != DYNAMIC.DYNAMIC_TYPE_REFRESH) {
                        handle = moveTail(-distanceY, true);
                    } else return dynamicType != DYNAMIC.DYNAMIC_TYPE_INITIAL;
                } else if (dynamicType == DYNAMIC.DYNAMIC_TYPE_INITIAL) {
                    Log.w("RefreshAndLoadView", "return false  3333 上拉 -> dynamicType:" + dynamicType + "   scrollViewTop:" + scrollViewTop + "    hadScrolledBottom:" + hadScrolledBottom);
                    return false;
                } else if (scrollViewTop > 0) {
                    handle = moveHeader(distanceY, true);
                } else if (scrollViewTop < 0 && dynamicType != DYNAMIC.DYNAMIC_TYPE_REFRESH) {
                    handle = moveTail(-distanceY, true);
                } else {
                    Log.w("RefreshAndLoadView", "return false  4444 上拉 -> dynamicType:" + dynamicType + "   scrollViewTop:" + scrollViewTop + "    hadScrolledBottom:" + hadScrolledBottom);
                    return false;
                }
            } else {//下拉
                //头部内容
                if (hadScrolled) {
                    Log.w("RefreshAndLoadView", "onScroll false  5555 下拉 -> dynamicType:" + dynamicType + "   scrollViewTop:" + scrollViewTop + "    hadScrolledBottom:" + hadScrolledBottom);
                    if (hadScrolledBottom) {
                        handle = moveTail(-distanceY, true);
                    } else {
                        return false;
                    }
                } else if (dynamicType == DYNAMIC.DYNAMIC_TYPE_INITIAL) {
                    handle = moveHeader(distanceY, true);
                } else if (scrollViewTop > 0 && dynamicType != DYNAMIC.DYNAMIC_TYPE_LOAD) {
                    handle = moveHeader(distanceY, true);
                } else if (scrollViewTop < 0 && dynamicType != DYNAMIC.DYNAMIC_TYPE_REFRESH) {
                    handle = moveTail(-distanceY, true);
                } else {
                    Log.w("RefreshAndLoadView", "onScroll false  6666 下拉 -> dynamicType:" + dynamicType + "   scrollViewTop:" + scrollViewTop + "    hadScrolledBottom:" + hadScrolledBottom);
                    return false;
                }
            }
            if (!handle) {
                Log.w("RefreshAndLoadView", "onScroll false  7777 下拉 -> dynamicType:" + dynamicType + "   scrollViewTop:" + scrollViewTop + "    hadScrolledBottom:" + hadScrolledBottom);
            }
            return handle;
        }

        @Override
        public boolean onDown(MotionEvent e) {
            Log.w("RefreshAndLoadView", "onDown -> mState:" + mState + "     dynamicType:" + dynamicType);
            return false;
        }

        @Override
        public void onShowPress(MotionEvent e) {
        }

        @Override
        public boolean onSingleTapUp(MotionEvent e) {
            return false;
        }

        @Override
        public void onLongPress(MotionEvent e) {
        }

        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            return false;
        }
    });

    /**
     * 移动头部
     *
     * @param distanceY
     * @return
     */
    private boolean moveHeader(float distanceY, boolean userControl) {
        if (userControl && !refreshable || dynamicType == DYNAMIC.DYNAMIC_TYPE_LOAD) {
            return false;
        }
        View header = getChildAt(0);
        View scroller = getChildAt(1);
        if (!userControl && header.getBottom() <= 0 && distanceY > 0) {
            return true;
        }
        //设置最大拉伸位置
        if (header.getBottom() > MAC_SLIDE_HEIGHT) {
            setStateAndNotify(userControl);
            return true;
        }
        if (userControl && scroller.getTop() > mRefreshHeight) {
            distanceY /= REFRESH_DAMP;
        }
        Log.w("RefreshAndLoadView", "moveHeader -> header:" + header.getBottom() + "     scroller:" + scroller.getTop() + " ----- ");
        header.offsetTopAndBottom(-(int) distanceY);
        scroller.offsetTopAndBottom(-(int) distanceY);
        Log.w("RefreshAndLoadView", "moveHeader -> header:" + header.getBottom() + "     scroller:" + scroller.getTop());
        setStateAndNotify(userControl);
        return true;
    }

    /**
     * 移动底部
     *
     * @param distanceY
     * @return
     */
    private boolean moveTail(float distanceY, boolean userControl) {
        if (userControl && !loadable || dynamicType == DYNAMIC.DYNAMIC_TYPE_REFRESH)
            return false;
        Log.w("RefreshAndLoadView", "moveTail distanceY:" + distanceY);

        if (scrollView.getTop() < -MAC_SLIDE_HEIGHT) {
            setStateAndNotify(userControl);
            return true;
        }
        if (scrollView != null) {
            if (userControl && -scrollView.getTop() > mLoadHeight) {
                distanceY /= LOAD_DAMP;
            }
            Log.w("RefreshAndLoadView", "moveTail scrollView:" + scrollView.getTop() + "     mLoadView:" + mLoadView.getTop() + " ----- ");

            scrollView.offsetTopAndBottom((int) distanceY);
            mLoadView.offsetTopAndBottom((int) distanceY);
            Log.w("RefreshAndLoadView", "moveTail scrollView:" + scrollView.getTop() + "     mLoadView:" + mLoadView.getTop() + " ----- ");
        }
        setStateAndNotify(userControl);
        return true;
    }

    /**
     * 自动滑动完成
     */
    private void scrollComplete() {
        requestLayout();
    }

    /**
     * 根据位置设置状态
     *
     * @param userControl 是否是用户操作的
     */
    private void setStateAndNotify(boolean userControl) {
        scrollView = ScrollUtil.getScrollView(RefreshAndLoadView.this);
        int scrollTop = scrollView.getTop();
        if (userControl) {
            //操作类型在用户一次操作内只允许被修改一次
            if (dynamicType != DYNAMIC.DYNAMIC_TYPE_INITIAL) {
                broadcastState();
                return;
            }
            //用户操作不存在初始状态,初始状态应该有scroller来达到
            boolean hadScrolled = ScrollUtil.hadScrolled(scrollView);
            boolean hadScrolledBottom = ScrollUtil.hadScrolledBottom(scrollView);
            if (hadScrolled) {
                if (hadScrolledBottom) {
                    dynamicType = DYNAMIC.DYNAMIC_TYPE_LOAD;
                    mState = State.STATE_PULLING;
                }
            } else {
                if (scrollTop > 0) {
                    dynamicType = DYNAMIC.DYNAMIC_TYPE_REFRESH;
                    mState = State.STATE_PULLING;
                }
            }
        } else {
            if (scrollTop == 0) {
                mState = State.STATE_INITIAL;
                dynamicType = DYNAMIC.DYNAMIC_TYPE_INITIAL;
                removeCallbacks(autoScroll);
                requestLayout();
            } else if (scrollTop == mRefreshHeight && dynamicType == DYNAMIC.DYNAMIC_TYPE_REFRESH) {
                mState = State.STATE_REFRESH_OR_LOAD;
                if (refreshListener != null) {
                    refreshListener.onRefresh();
                }
            } else if (-scrollTop == mLoadHeight && dynamicType == DYNAMIC.DYNAMIC_TYPE_LOAD) {
                mState = State.STATE_REFRESH_OR_LOAD;
                if (loadListener != null) {
                    loadListener.onLoad();
                }
            } else {
                mState = State.STATE_PACK_UP;
            }
        }

        Log.w("RefreshAndLoadView", "dynamicType:" + dynamicType + "     mState:" + mState);

        Log.w("RefreshAndLoadView", "0 bottom:" + getChildAt(0).getBottom() +
                "     1 top:" + getChildAt(1).getTop()
                + "     2 top:" + getChildAt(2).getTop());
        broadcastState();
    }

    /**
     * 向头部刷新或底部加载广播事件
     */
    private void broadcastState() {
        if (dynamicType == DYNAMIC.DYNAMIC_TYPE_LOAD && mLoadView != null) {
            mLoadView.setState(mState, scrollView.getMeasuredHeight() - mLoadView.getTop(), mLoadHeight);
        } else if (dynamicType == DYNAMIC.DYNAMIC_TYPE_REFRESH && mRefreshView != null) {
            mRefreshView.setState(mState, mRefreshView.getBottom(), mRefreshHeight);
        } else if (dynamicType == DYNAMIC.DYNAMIC_TYPE_INITIAL) {
            if (lastDynamicType == DYNAMIC.DYNAMIC_TYPE_LOAD) {
                mLoadView.setState(mState, 0, mLoadHeight);
            } else if (lastDynamicType == DYNAMIC.DYNAMIC_TYPE_REFRESH) {
                mRefreshView.setState(mState, 0, mRefreshHeight);
            }
        }
        lastDynamicType = dynamicType;
    }

    /**
     * 恢复头部位置
     */
    private void recoverHeader() {
        Log.w("RefreshAndLoadView", "recoverHeader");
        int bottom = mRefreshView.getBottom();
        if (bottom > mRefreshHeight && refreshListener != null) {//准备刷新
            autoScroll.scroll(bottom - mRefreshHeight);
        } else {//恢复初始状态
            autoScroll.scroll(bottom);
        }
    }

    /**
     * 恢复尾部位置
     */
    private void recoverTail() {
        int bottom = scrollView.getBottom();
        int dY = mBottom - bottom;

        Log.w("RefreshAndLoadView", "recoverTail:" + dY + "     bottom:" + bottom);
        if (dY > mLoadHeight && loadListener != null) {
            autoScroll.scroll(dY - mLoadHeight);
        } else {
            autoScroll.scroll(dY);
        }
    }

    AutoScroll autoScroll;

    private class AutoScroll implements Runnable {
        private Scroller scroller;
        private int lastY;
        private int total;

        public AutoScroll() {
            scroller = new Scroller(getContext(), new LinearInterpolator());
        }

        @Override
        public void run() {
            if (scroller.computeScrollOffset()) {
                Log.w("RefreshAndLoadView", "0 bottom:" + getChildAt(0).getBottom() +
                        "     1 top:" + getChildAt(1).getTop()
                        + "     2 top:" + +getChildAt(2).getTop());

                int xY = scroller.getCurrY() - lastY;
                lastY = scroller.getCurrY();
                total += xY;
                Log.w("RefreshAndLoadView", "AutoScroll 移动位置:" + xY + "     共计:" + total);
                if (dynamicType == DYNAMIC.DYNAMIC_TYPE_REFRESH) {
                    moveHeader(xY, false);
                    post(this);
                } else if ((dynamicType == DYNAMIC.DYNAMIC_TYPE_LOAD)) {
                    moveTail(xY, false);
                    post(this);
                } else {
                    setStateAndNotify(false);
                    removeCallbacks(this);
                }
            } else {
                scrollComplete();
                removeCallbacks(this);
            }
        }

        public void scroll(int xY) {
            Log.w("RefreshAndLoadView", "AutoScroll 目标:" + xY + "--------------------------------------------");
            Log.w("RefreshAndLoadView", "0 bottom:" + getChildAt(0).getBottom() +
                    "     1 top:" + getChildAt(1).getTop()
                    + "     2 top:" + +getChildAt(2).getTop());

            removeCallbacks(this);
            lastY = 0;
            total = 0;
            scroller.startScroll(0, 0, 0, xY, 350);
            post(this);
        }

    }

    /**********************头部刷新的内容***************************/
    @Override
    public void setRefreshView(ARefreshView refreshView) {
        setRefreshView(refreshView, dp2px(REFRESH_LOAD_HEIGHT, getResources()));
    }

    @Override
    public void setRefreshView(ARefreshView refreshView, int refreshHeight) {
        if (mRefreshView != null) {
            removeView(mRefreshView);
        }
        mRefreshView = refreshView;
        mRefreshHeight = refreshHeight;
        FrameLayout.LayoutParams layoutParams = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
        addView(refreshView, 0, layoutParams);
    }

    @Override
    public void setRefreshListener(RefreshListener refreshListener) {
        this.refreshListener = refreshListener;
    }

    @Override
    public void setRefreshAble(boolean refreshAble) {
        this.refreshable = refreshAble;
        if (refreshable && mRefreshView == null) {
            setRefreshView(new SimpleRefreshView(getContext()));
        }
    }

    @Override
    public void refreshComplete() {
        if (dynamicType == DYNAMIC.DYNAMIC_TYPE_REFRESH) {
            recoverHeader();
        }
    }

    /**********************底部刷新的内容***************************/
    @Override
    public void setLoadView(ALoadView loadView) {
        setLoadView(loadView, dp2px(REFRESH_LOAD_HEIGHT, getResources()));
    }

    @Override
    public void setLoadView(ALoadView loadView, int loadHeight) {
        if (mLoadView != null) {
            removeView(mLoadView);
        }
        mLoadView = loadView;
        mLoadHeight = loadHeight;
        FrameLayout.LayoutParams layoutParams = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
        addView(loadView, getChildCount(), layoutParams);
    }

    @Override
    public void setLoadListener(LoadListener loadListener) {
        this.loadListener = loadListener;
    }

    @Override
    public void setLoadAble(boolean refreshAble) {
        loadable = refreshAble;
        if (loadable && mLoadView == null) {
            setLoadView(new SimpleLoadView(getContext()));
        }
    }

    @Override
    public void loadComplete() {
        if (dynamicType == DYNAMIC.DYNAMIC_TYPE_LOAD) {
            recoverTail();
        }
    }

    @Override
    public void requestLayout() {
        if (State.STATE_INITIAL == mState) {
            super.requestLayout();
        }
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        Log.w("RefreshAndLoadView", "onLayout:   >>>>>>>>>>>>>>>>");
        mBottom = bottom;
        if ((mRefreshView != null || mLoadView != null) && getChildCount() >= 2) {
            View first = getChildAt(0);
            if (first instanceof ARefreshView) {
                first.layout(left, -first.getMeasuredHeight(), right, 0);
                View content = getChildAt(1);
                content.layout(left, top, right, bottom);
                View loadView = getChildAt(2);
                if (loadView != null) {
                    int measuredHeight = loadView.getMeasuredHeight();
                    loadView.layout(left, bottom, right, bottom + measuredHeight);
                }
            } else if (first instanceof RecyclerView || first instanceof AdapterView) {
                first.layout(left, top, right, bottom);
                View load = getChildAt(1);
                if (load != null) {
                    int measuredHeight = load.getMeasuredHeight();
                    load.layout(left, bottom, right, bottom + measuredHeight);
                }
            }
        } else {
            super.onLayout(changed, left, top, right, bottom);
        }
    }

    private static int dp2px(float dp, Resources resources) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, resources.getDisplayMetrics());
    }
}
