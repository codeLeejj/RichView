package com.android.libview.view.refreshAndLoadView.wapper;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.android.libview.R;
import com.android.libview.view.refreshAndLoadView.contract.ARefreshView;
import com.android.libview.view.refreshAndLoadView.contract.State;

/**
 * author:Lee
 * date:2021/7/21
 * Describe:
 */
public class SimpleRefreshView extends ARefreshView {
    public SimpleRefreshView(@NonNull Context context) {
        super(context);
    }

    public SimpleRefreshView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public SimpleRefreshView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    TextView tvContent;

    @Override
    public void init() {
        LayoutInflater.from(getContext()).inflate(R.layout.simple_refresh_header, this, true);

        tvContent = findViewById(R.id.tvContent);
    }

    @Override
    public void setState(State state,int expandHeight) {
        tvContent.setText(state +": "+ expandHeight);
    }
}
