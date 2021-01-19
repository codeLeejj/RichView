package com.android.libview.utils;

import android.app.Activity;
import android.content.Context;
import android.graphics.Rect;
import android.view.Window;

public class DisplayUtil {

    private static boolean got = false;
    private static boolean isPad = false;

//    public static boolean isPad(Context context) {
//        if (!got) {
//            isPad = context.getResources().getBoolean(R.bool.isPad);
//            got = true;
//        }
//        return isPad;
//    }

    public static int dpToPx(Context context, float dpValue) {//dp转换为px
        float scale = context.getResources().getDisplayMetrics().density;//获得当前屏幕密度
        return (int) (dpValue * scale + 0.5f);
    }

    public static int pxToDp(Context context, float pxValue) {//px转换为dp
        float scale = context.getResources().getDisplayMetrics().density;//获得当前屏幕密度
        return (int) (pxValue / scale + 0.5f);
    }

    /**
     * 获取状态栏高度
     * 该方法获取需要在onWindowFocusChanged方法回调之后
     *
     * @param context
     * @return
     */
    public static int getStatusBarHeight(Context context) {
        int statusBarHeight = getStatusBarByResId(context);
        if (statusBarHeight <= 0) {
            statusBarHeight = getStatusBarByReflex(context);
        }
        return statusBarHeight;
    }

    /**
     * 通过状态栏资源id来获取状态栏高度
     *
     * @param context
     * @return
     */
    private static int getStatusBarByResId(Context context) {
        int height = 0;
        //获取状态栏资源id
        int resourceId = context.getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            try {
                height = context.getResources().getDimensionPixelSize(resourceId);
            } catch (Exception e) {
            }
        }
        return height;
    }

    /**
     * 通过Activity的内容距离顶部高度来获取状态栏高度，该方法获取需要在onWindowFocusChanged回调之后
     *
     * @param activity
     * @return
     */
    public static int getStatusBarByTop(Activity activity) {
        Rect rect;
        rect = new Rect();
        Window window = activity.getWindow();
        window.getDecorView().getWindowVisibleDisplayFrame(rect);
        return rect.top;
    }

    /**
     * 通过反射获取状态栏高度
     *
     * @param context
     * @return
     */
    private static int getStatusBarByReflex(Context context) {
        int statusBarHeight = 0;
        try {
            Class<?> clazz = Class.forName("com.android.internal.R$dimen");
            Object object = clazz.newInstance();
            int height = Integer.parseInt(clazz.getField("status_bar_height")
                    .get(object).toString());
            statusBarHeight = context.getResources().getDimensionPixelSize(height);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return statusBarHeight;
    }
}
