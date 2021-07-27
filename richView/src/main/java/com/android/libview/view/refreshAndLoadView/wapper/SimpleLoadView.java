package com.android.libview.view.refreshAndLoadView.wapper;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.airbnb.lottie.LottieAnimationView;
import com.android.libview.R;
import com.android.libview.view.refreshAndLoadView.contract.ALoadView;
import com.android.libview.view.refreshAndLoadView.contract.State;

/**
 * author:Lee
 * date:2021/7/22
 * Describe:
 */
public class SimpleLoadView extends ALoadView {
    public SimpleLoadView(@NonNull Context context) {
        super(context);
    }

    public SimpleLoadView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public SimpleLoadView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    LottieAnimationView lottieAnimationView;

    @Override
    public void init() {
        LayoutInflater.from(getContext()).inflate(R.layout.simple_load_view, this, true);

        lottieAnimationView = findViewById(R.id.lottieView);
    }

    @Override
    public void setState(State state, int expandHeight, int refreshOrLoadHeight) {
        if (State.STATE_PULLING == state || State.STATE_PACK_UP == state) {
            if (expandHeight >= getMeasuredHeight()) {
                float scale = 1 + ((expandHeight - getMeasuredHeight()) * 1.0f / refreshOrLoadHeight);
                if (scale >= 2) scale = 2;
                lottieAnimationView.setScaleX(scale);
                lottieAnimationView.setScaleY(scale);
            }
        } else if (State.STATE_REFRESH_OR_LOAD == state) {
            lottieAnimationView.playAnimation();
        } else if (State.STATE_INITIAL == state) {
            lottieAnimationView.cancelAnimation();
            lottieAnimationView.setFrame(0);
        }
    }
}
