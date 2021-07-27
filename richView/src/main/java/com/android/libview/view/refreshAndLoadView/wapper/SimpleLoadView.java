package com.android.libview.view.refreshAndLoadView.wapper;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

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

    TextView tvContent;

    @Override
    public void init() {
        LayoutInflater.from(getContext()).inflate(R.layout.simple_load_view, this, true);

        tvContent = findViewById(R.id.tvContent);
        tvContent.setBackgroundColor(Color.MAGENTA);
    }

    @Override
    public void setState(State state, int expandHeight) {

        tvContent.setText(state + ": " + expandHeight);
    }
}
