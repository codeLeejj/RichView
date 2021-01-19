package com.android.libview.utils;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;
import android.os.Build;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;

import androidx.annotation.NonNull;

import java.lang.reflect.Method;

import static android.view.View.NO_ID;

/**
 * Created by zhangyushui on 2019/3/18.
 */

public class AndroidBottomSoftBar {

    private View mViewObserved;//被监听的视图

    private int usableHeightPrevious;//视图变化前的可用高度
    private ViewGroup.LayoutParams frameLayoutParams;

    private AndroidBottomSoftBar(View viewObserving, final Activity activity) {
        mViewObserved = viewObserving;
        //给View添加全局的布局监听器
        mViewObserved.getViewTreeObserver().addOnGlobalLayoutListener(() -> resetLayoutByUsableHeight(activity));
        frameLayoutParams = mViewObserved.getLayoutParams();
    }

    /**
     * 关联要监听的视图
     */
    public static void assistActivity(View viewObserving, Activity activity) {
        new AndroidBottomSoftBar(viewObserving, activity);
    }

    private void resetLayoutByUsableHeight(Activity activity) {

        if (checkDeviceHasNavigationBar(activity)) {
            WindowManager wm = (WindowManager) activity.getSystemService(Context.WINDOW_SERVICE);
            int width = wm.getDefaultDisplay().getWidth();
            int height = wm.getDefaultDisplay().getHeight();
            // 计算视图可视高度
            /* Rect r = new Rect();
            mViewObserved.getWindowVisibleDisplayFrame(r);
            int virtualKeyHeight = 40;
            int usableHeightNow = (r.bottom - r.top)
            + CommonUtils.dip2px(WDApplication.getInstance().getApplicationContext(), virtualKeyHeight);*/

            //int usableHeightNow = height - getHasVirtualKey(activity);
            int usableHeightNow = height;

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                activity.getWindow().setNavigationBarColor(Color.BLACK);
//            activity.getWindow().setNavigationBarColor(Color.parseColor("#1bb5d7"));

                // 虚拟导航键
//            activity.getWindow().setNavigationBarColor(Color.TRANSPARENT);
            }

            //比较布局变化前后的View的可用高度
            if (usableHeightNow != usableHeightPrevious) {
                //如果两次高度不一致
                //将当前的View的可用高度设置成View的实际高度
//            frameLayoutParams.height = usableHeightNow;
                int paddingBottom = getNavigationHeight(activity);
//                int paddingBottom = getHasVirtualKey(activity);
                mViewObserved.setPadding(0, 0, 0, paddingBottom);
                mViewObserved.requestLayout();//请求重新布局
                usableHeightPrevious = usableHeightNow;
            }
        }
    }

    /**
     * dpi 通过反射，获取包含虚拟键的整体屏幕高度
     * height 获取屏幕尺寸，但是不包括虚拟功能高度
     *
     * @return
     */
    public static int getHasVirtualKey(Activity activity) {
        int dpi = 0;
        Display display = activity.getWindowManager().getDefaultDisplay();
        DisplayMetrics dm = new DisplayMetrics();
        @SuppressWarnings("rawtypes")
        Class c;
        try {
            c = Class.forName("android.view.Display");
            @SuppressWarnings("unchecked")
            Method method = c.getMethod("getRealMetrics", DisplayMetrics.class);
            method.invoke(display, dm);
            dpi = dm.heightPixels;
        } catch (Exception e) {
            e.printStackTrace();
        }

        int height = activity.getWindowManager().getDefaultDisplay().getHeight();
        return dpi - height;
    }

    private static final String NAVIGATION = "navigationBarBackground";

    // 该方法需要在View完全被绘制出来之后调用，否则判断不了
    //在比如 onWindowFocusChanged（）方法中可以得到正确的结果
    public static boolean isNavigationBarExist(@NonNull Activity activity) {
        ViewGroup vp = (ViewGroup) activity.getWindow().getDecorView();
        if (vp != null) {
            for (int i = 0; i < vp.getChildCount(); i++) {
                vp.getChildAt(i).getContext().getPackageName();
                if (vp.getChildAt(i).getId() != NO_ID && NAVIGATION.equals(activity.getResources().getResourceEntryName(vp.getChildAt(i).getId()))) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * 判断是否存在虚拟按键
     *
     * @return
     */
    public static boolean checkDeviceHasNavigationBar(Activity activity) {
        boolean hasNavigationBar = false;
        Resources rs = activity.getResources();
        int id = rs.getIdentifier("config_showNavigationBar", "bool", "android");
        if (id > 0) {
            hasNavigationBar = rs.getBoolean(id);
        }
        try {
            Class<?> systemPropertiesClass = Class.forName("android.os.SystemProperties");
            Method m = systemPropertiesClass.getMethod("get", String.class);
            String navBarOverride = (String) m.invoke(systemPropertiesClass, "qemu.hw.mainkeys");
            if ("1".equals(navBarOverride)) {
                hasNavigationBar = false;
            } else if ("0".equals(navBarOverride)) {
                hasNavigationBar = true;
            }
        } catch (Exception e) {

        }
        return hasNavigationBar;
    }

    static boolean isShowing = false;
    int bottomHeight;

    public static boolean isNavigationBarExist2(Activity activity) {
        if (activity == null) {
            return false;
        }
        final int height = getNavigationHeight(activity);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT_WATCH) {
            activity.getWindow().getDecorView().setOnApplyWindowInsetsListener((v, windowInsets) -> {
                int b = 0;
                if (windowInsets != null) {
                    b = windowInsets.getSystemWindowInsetBottom();
                    isShowing = (b == height);
                }
//                    if (onNavigationStateListener != null && b <= height) {
//                        onNavigationStateListener.onNavigationState(isShowing, b);
//                    }
                return windowInsets;
            });
        }
        return isShowing;
    }

    public static int getNavigationHeight(Context activity) {
        if (activity == null) {
            return 0;
        }
        Resources resources = activity.getResources();
        int resourceId = resources.getIdentifier("navigation_bar_height", "dimen", "android");
        int height = 0;
        if (resourceId > 0) {
            //获取NavigationBar的高度
            height = resources.getDimensionPixelSize(resourceId);
        }
        return height;
    }

}