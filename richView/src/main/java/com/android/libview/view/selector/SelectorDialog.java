package com.android.libview.view.selector;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.recyclerview.widget.RecyclerView;

import com.android.libview.R;
import com.android.libview.view.recyclerview.BaseViewHolder;
import com.android.libview.view.recyclerview.RvAdapter;

import java.util.List;

public class SelectorDialog<T> extends Dialog {
    int mGravity = Gravity.BOTTOM;

    public static class Builder<T> {
        Context mContext;
        String mTitle;
        int mGravity = Gravity.BOTTOM;
        List<T> mList;
        TitleBuilder<T> mCallback;

        public Builder(Context context) {
            mContext = context;
        }

        public Builder<T> setTitle(String title) {
            mTitle = title;
            return this;
        }

        public Builder<T> setGravity(int Gravity) {
            mGravity = Gravity;
            return this;
        }

        public Builder<T> setList(List<T> list, TitleBuilder<T> callback) {
            mList = list;
            mCallback = callback;
            return this;
        }

        public SelectorDialog<T> build() {
            SelectorDialog<T> selectorDialog = new SelectorDialog<>(mContext, mTitle, mCallback);
            selectorDialog.setGravity(mGravity);
            return selectorDialog;
        }
    }

    private void setGravity(int gravity) {
        mGravity = gravity;
    }

    String title;

    public SelectorDialog(@NonNull Context context, String title, TitleBuilder callback) {
        super(context);
        this.callback = callback;
        this.title = title;
        myAdapter = new MyAdapter(getContext());
    }

//    public SelectorDialog(@NonNull Context context, String title, MultiCallback callback) {
//        super(context);
//        this.callback = callback;
//        this.title = title;
//        myAdapter = new MyAdapter(getContext());
//    }

    RecyclerView rv;
    TextView tvTitle;
    MyAdapter myAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(1);
        setContentView(R.layout.dialog_selector);
//        if (DisplayUtil.isPad(getContext())) {
//            getWindow().setLayout(DisplayUtil.dpToPx(getContext(), 400), ViewGroup.LayoutParams.WRAP_CONTENT);
//        } else {

        //mmp 如果是null 还不是要崩溃,有jb必要包一层
//        int width = Objects.requireNonNull(getWindow()).getWindowManager().getDefaultDisplay().getWidth();
        WindowManager.LayoutParams attributes = getWindow().getAttributes();
        attributes.gravity = mGravity;
        int width = getWindow().getWindowManager().getDefaultDisplay().getWidth();
        if (Gravity.BOTTOM == mGravity) {
            attributes.width = (int) (width * 1.1f);
            getWindow().setAttributes(attributes);
        } else if (Gravity.CENTER == mGravity) {
            attributes.width = (int) (width * 0.9f);
            getWindow().setAttributes(attributes);
        }

        tvTitle = findViewById(R.id.tvHeader);
        if (!TextUtils.isEmpty(title)) {
            tvTitle.setText(title);
        }
        rv = findViewById(R.id.rv);
        rv.setAdapter(myAdapter);
    }

    public SelectorDialog updateData(List<T> list) {
        myAdapter.setDatas(list);
        return this;
    }

    public void showWithData(List<T> list) {
        show();
        myAdapter.setDatas(list);
    }

    TitleBuilder<T> callback;

    class MyAdapter extends RvAdapter<T> {

        public MyAdapter(Context mContext) {
            super(mContext);
        }

        @Override
        public void bindView(BaseViewHolder holder, int position, T item) {
            holder.setText(R.id.atv, callback.itemTitle(item));
            if (callback instanceof MultiBuilder) {
                holder.getView(R.id.aiv).setVisibility(View.VISIBLE);
                holder.getView(R.id.aiv_next).setVisibility(View.VISIBLE);
                holder.setImageRes(R.id.aiv, ((MultiBuilder<T>) callback).itemIcon(item));
                ((AppCompatTextView) holder.getView(R.id.atv)).setGravity(Gravity.CENTER_VERTICAL);
            } else {
                holder.getView(R.id.aiv).setVisibility(View.GONE);
                holder.getView(R.id.aiv_next).setVisibility(View.GONE);
                ((AppCompatTextView) holder.getView(R.id.atv)).setGravity(Gravity.CENTER);
            }
            holder.itemView.setOnClickListener(v -> {
                dismiss();
                callback.itemClick(position, item);
            });
        }

        @Override
        public int getLayoutId() {
            return R.layout.item_text_selector;
        }
    }
}