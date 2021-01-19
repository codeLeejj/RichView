package com.android.libview.view;

import android.app.Activity;
import android.app.Dialog;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.ColorDrawable;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.RotateAnimation;
import android.widget.FrameLayout;
import android.widget.TextView;

import androidx.annotation.IntRange;
import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatImageView;

import com.android.libview.R;

/**
 * 带计时器的等待层
 *
 * @author hss hess@yitong.com.cn
 * @date 2015年8月29日 下午8:37:45
 */
public class TimerLoadingDialog {

    public final int DEFAULT_DELAY_TIME = 31;
//    private Timer timer;

    private Activity activity;
    private Dialog loadingDialog;

    private LoadingTimeOutCallback callBack;

    private AppCompatImageView indicationIm;

    private TextView loadTextView;

    private View loadingLayout;

    public TimerLoadingDialog() {
    }

    public static final int MSG_CODE = 1235;
    Handler mHandler;

    public TimerLoadingDialog(Activity activity) {
        this.activity = activity;
        callBack = null;
        mHandler = new Handler(){
            @Override
            public void handleMessage(@NonNull Message msg) {
                if (callBack != null) {
                    callBack.onLoadingTimeOut(TimerLoadingDialog.this);
                }
                dismiss();
            }
        };
    }

    /**
     * 显示等待层
     *
     * @param canceled       是否可取消
     * @param text           显示文字
     * @param loadingTimeOut 超时回调
     * @return
     * @author hss hess@yitong.com.cn
     * @date 2015年8月29日 下午8:38:25
     */
    public TimerLoadingDialog showLoadingDialog(final boolean canceled, final String text,
                                                final LoadingTimeOutCallback loadingTimeOut) {
        return this.showLoadingDialog(canceled, text, DEFAULT_DELAY_TIME, loadingTimeOut);
    }

    public TimerLoadingDialog showLoadingDialog(final boolean canceled, final String text,
                                                @IntRange(from = 0, to = 600) final int timeout,
                                                final LoadingTimeOutCallback loadingTimeOut) {
        if (activity == null || activity.isFinishing())
            return null;
        activity.runOnUiThread(() -> {
            // 1. 布局文件转换为View对象
            callBack = loadingTimeOut;
            // 1. 新建对话框对象
            loadingDialog = new Dialog(activity, R.style.KeyboardTheme2);
            // 1. 布局文件转换为View对象
            loadingLayout = LayoutInflater.from(activity).inflate(R.layout.load_view, null);
            FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT);
            layoutParams.gravity = Gravity.CENTER;

            indicationIm = loadingLayout.findViewById(R.id.indication);
            loadTextView = loadingLayout.findViewById(R.id.promptTV);

            loadTextView.setText(TextUtils.isEmpty(text) ? "正在加载..." : text);
            //动画
            startPageAnim();
            // 2.倒计时
            if (timeout == 0) {
                startTimer(DEFAULT_DELAY_TIME);
            } else {
                startTimer(timeout);
            }

            // 3.. 新建对话框对象
            loadingDialog.setCancelable(canceled);
            loadingDialog.setContentView(loadingLayout, new FrameLayout.LayoutParams(
                    FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.WRAP_CONTENT));
            Window window = loadingDialog.getWindow();
            window.setBackgroundDrawable(new ColorDrawable());
            //设置动画
//                window.setWindowAnimations(R.style.bri_alpha_in_out_loading_anim_style);
            WindowManager.LayoutParams wl = window.getAttributes();
            wl.width = ViewGroup.LayoutParams.WRAP_CONTENT;
            wl.height = ViewGroup.LayoutParams.WRAP_CONTENT;
            wl.flags = 8450;
            wl.dimAmount = 0.0F;//去掉了阴影
            wl.gravity = Gravity.CENTER;
            loadingDialog.onWindowAttributesChanged(wl);
            loadingDialog.setCanceledOnTouchOutside(false);
            loadingDialog.show();
            loadingDialog.setOnDismissListener(dialog -> {
                //取消计时
                cancelTimer();
                indicationIm.clearAnimation();
            });
        });
        return this;
    }

    private void startPageAnim() {
        indicationIm.setBackgroundResource(R.mipmap.loading);
        RotateAnimation rotateAnimation = new RotateAnimation(0, 360, Animation.ABSOLUTE, 0.5f, Animation.ABSOLUTE, 0.5f);
        rotateAnimation.setRepeatMode(Animation.RESTART);
        rotateAnimation.setRepeatCount(Animation.INFINITE);
        indicationIm.startAnimation(rotateAnimation);
        loadingLayout.startAnimation(AnimationUtils.loadAnimation(activity, R.anim.bri_load_alpha_enter));

//        indicationIm.setBackgroundResource(R.drawable.bank_loading_white_animlist);
////        indicationIm.setImageResource(R.drawable.bank_loading_white_animlist);
//        AnimationDrawable anim = (AnimationDrawable) indicationIm.getBackground();
//        anim.start();
//        loadingLayout.startAnimation(AnimationUtils.loadAnimation(activity, R.anim.bri_load_alpha_enter));
    }

    /**
     * 关闭等待层
     *
     * @author hss hess@yitong.com.cn
     * @date 2015年8月29日 下午8:39:00
     */
    public void dismiss() {
        try {
            if (loadingDialog != null) {
                loadingDialog.dismiss();
            }
        } catch (Exception ignore) {

        }
        cancelTimer();
    }

    private void startTimer(int secondTime) {
        if (secondTime < 1000) {
            secondTime *= 1000;
        }
        Message message = mHandler.obtainMessage();
        message.what = MSG_CODE;
        mHandler.sendMessageDelayed(message, secondTime);
    }

    /**
     * 取消等待层计时器
     *
     * @author hss hess@yitong.com.cn
     * @date 2015年10月13日 下午8:55:48
     */
    public void cancelTimer() {
        mHandler.removeMessages(MSG_CODE);
    }

    /**
     * 计时结束回调
     *
     * @author hss hess@yitong.com.cn
     * @date 2015年8月29日 下午8:39:10
     */
    public interface LoadingTimeOutCallback {
        void onLoadingTimeOut(TimerLoadingDialog timeDialog);
    }

    public void setText(final String strMsg) {
        activity.runOnUiThread(() -> {
            if (loadTextView != null)
                loadTextView.setText(TextUtils.isEmpty(strMsg) ? "正在加载中" : strMsg);
        });
    }

    /**
     * 判断当前loadingDialog 是否没有在显示
     *
     * @return
     * @author hss hess@yitong.com.cn
     * @date 2016年7月6日 下午4:51:01
     */
    public boolean isShowing() {
        return loadingDialog != null && loadingDialog.isShowing();
    }
}