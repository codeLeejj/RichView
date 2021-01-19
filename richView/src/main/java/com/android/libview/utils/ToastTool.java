package com.android.libview.utils;


import android.content.Context;
import android.widget.Toast;

import androidx.annotation.StringRes;

public final class ToastTool {

    private ToastTool() throws IllegalAccessException {
        throw new IllegalAccessException("工具类无法实例化!");
    }

    /**
     * Toast显示
     */
    static Toast toast = null;

    public static void showLong(Context ctx, String msg) {
        if (toast != null) {
            toast.cancel();
        }
        toast = Toast.makeText(ctx, msg, Toast.LENGTH_LONG);
        toast.show();
    }

    public static void showShort(Context context, String msg) {
        if (toast != null) {
            toast.cancel();
        }
        toast = Toast.makeText(context, msg, Toast.LENGTH_SHORT);
        toast.show();
    }

    public static void showLong(Context ctx, @StringRes int resId) {
        if (toast != null) {
            toast.cancel();
        }
        toast = Toast.makeText(ctx, resId, Toast.LENGTH_LONG);
        toast.show();
    }

    public static void showShort(Context ctx, @StringRes int resId) {
        if (toast != null) {
            toast.cancel();
        }
        toast = Toast.makeText(ctx, resId, Toast.LENGTH_SHORT);
        toast.show();
    }

    public static void cancel() {
        if (toast != null) {
            toast.cancel();
        }
    }
}