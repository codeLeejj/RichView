package com.android.libview.view;

import android.app.Activity;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.DrawableRes;
import androidx.annotation.IntDef;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.Toolbar;

import com.android.libview.R;
import com.android.libview.utils.DisplayUtil;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

public class MyToolbar extends Toolbar {

    public MyToolbar(Context context) {
        super(context);
        init(context);
    }

    public MyToolbar(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        getAtt(context, attrs);
        init(context);
    }

    public MyToolbar(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        getAtt(context, attrs);
        init(context);
    }

    private void getAtt(Context context, @Nullable AttributeSet attrs) {
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.MyToolbar);
        titleStr = typedArray.getString(R.styleable.MyToolbar_title);

        float dimension = context.getResources().getDimension(R.dimen.textSizeBig);
        titleSize = typedArray.getDimension(R.styleable.MyToolbar_title_size, dimension);
        titleSize = DisplayUtil.pxToDp(context, titleSize);
        titleColor = typedArray.getColor(R.styleable.MyToolbar_title_color, Color.BLACK);

        rightIcon = typedArray.getInteger(R.styleable.MyToolbar_right_icon, 0);
        leftText = typedArray.getString(R.styleable.MyToolbar_left_text);
        rightText = typedArray.getString(R.styleable.MyToolbar_right_text);

        bottomLine = typedArray.getBoolean(R.styleable.MyToolbar_bottom_line, false);

        typedArray.recycle();
    }

    /*******  xml  中的值*********/
    String titleStr;
    String leftText, rightText;
    float titleSize;
    int titleColor;
    int rightIcon;
    boolean bottomLine;
    /*******  xml  中的View*********/
    View content;
    TextView tvTitle;
    LinearLayout llLeft, llRight;
    AppCompatImageView aivRight;
    TextView atvRight;
    TextView atvLeft;

    private void init(Context context) {
        //添加自定义 布局
        content = LayoutInflater.from(context).inflate(R.layout.part_toolbar, null);
        ViewGroup.LayoutParams params
                = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        this.addView(content, params);
        setPadding(0, 0, 0, 2);

        //findView
        llLeft = content.findViewById(R.id.tb_llBack);
        atvLeft = content.findViewById(R.id.tb_tvBack);

        tvTitle = content.findViewById(R.id.tb_tvTitle);

        llRight = content.findViewById(R.id.tb_llRight);
        aivRight = content.findViewById(R.id.tb_aivRight);
        atvRight = content.findViewById(R.id.tb_atvRight);

        //从 左 -> 右 去设置
        if (!TextUtils.isEmpty(leftText)) {
            atvLeft.setText(leftText);
        } else {
            atvLeft.setText(null);
        }

        tvTitle.setText(titleStr);
        tvTitle.setTextSize(titleSize);
        tvTitle.setTextColor(titleColor);

        //右边的文字图片显示其一
        if (rightIcon != 0) {
            aivRight.setVisibility(VISIBLE);
            aivRight.setImageResource(rightIcon);
        } else if (!TextUtils.isEmpty(rightText)) {
            atvRight.setVisibility(VISIBLE);
            atvRight.setText(rightText);
        }

        setBottomLine(bottomLine);
        /**
         * 左边 返回
         */
        llLeft.setOnClickListener(v -> {
            if (backListener != null) {
                backListener.onClick(v);
            } else if (context instanceof Activity) {
                ((Activity) context).finish();
            }
        });
        /**
         * 右边更多
         */
        llRight.setOnClickListener(v -> {
            if (rightListener != null) {
                rightListener.onClick(v);
            }
        });
    }

    public void title(String title) {
        tvTitle.setText(title);
    }

    public void leftText(String leftText) {
        atvLeft.setText(leftText);
    }

    public void rightText(String rightText) {
        atvRight.setText(rightText);
        aivRight.setImageBitmap(null);
    }

    public void rightIcon(@DrawableRes int icon_home) {
        atvRight.setText("");
        aivRight.setImageResource(icon_home);
    }

    public void rightVisible(@Visibility int visibility) {
        llRight.setVisibility(visibility);
    }

    OnClickListener backListener, rightListener;

    public void setBackClick(OnClickListener clickListener) {
        this.backListener = clickListener;
    }

    public void setRightClick(OnClickListener clickListener) {
        this.rightListener = clickListener;
    }

    public void setBottomLine(boolean bottomLine) {
        if (bottomLine) {
            setBackgroundResource(R.drawable.sp_bg_bottom_line);
        }
    }

    /**
     * @hide
     */
    @IntDef({VISIBLE, INVISIBLE, GONE})
    @Retention(RetentionPolicy.SOURCE)
    public @interface Visibility {
    }
}