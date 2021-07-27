package com.lee.richview;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.android.libview.view.refreshAndLoadView.RefreshAndLoadView;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class RefreshAndLoadViewTestActivity extends AppCompatActivity {

    RefreshAndLoadView ral;
    RecyclerView rv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_refresh_and_load_view_test);
        ral = findViewById(R.id.ral);

        setRefreshAndLoad();
        initRecycleVIew();
    }

    private void setRefreshAndLoad() {
        ral.setRefreshAble(true);
        ral.setLoadAble(true);

        ral.setRefreshListener(new RefreshAndLoadView.RefreshListener() {
            @Override
            public void onRefresh() {
                Toast.makeText(RefreshAndLoadViewTestActivity.this, "收到刷新", Toast.LENGTH_SHORT).show();
                ral.postDelayed(() -> {
                    data = generateData(20);
                    adapter.notifyDataSetChanged();
                    ral.refreshComplete();
//                    ral.setRefreshAble(false);
                }, 2_000);
            }
        });
        ral.setLoadListener(new RefreshAndLoadView.LoadListener() {
            @Override
            public void onLoad() {
                Toast.makeText(RefreshAndLoadViewTestActivity.this, "收到加载", Toast.LENGTH_SHORT).show();
                ral.postDelayed(() -> {
                    data.addAll(generateData(5));
                    adapter.notifyDataSetChanged();
                    ral.loadComplete();
//                    ral.setLoadAble(false);
                }, 2_000);
            }
        });
    }

    MyAdapter adapter;
    List<String> data;

    private void initRecycleVIew() {
        rv = findViewById(R.id.rv);

        adapter = new MyAdapter();
        rv.setAdapter(adapter);
        data = generateData(10);

        adapter.notifyDataSetChanged();
    }

    class MyAdapter extends RecyclerView.Adapter<MyAdapter.ViewHolder> {

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(android.R.layout.simple_list_item_1, null));
        }

        @Override
        public void onBindViewHolder(@NonNull RefreshAndLoadViewTestActivity.MyAdapter.ViewHolder holder, int position) {
            holder.textView.setText(data.get(position));
            holder.textView.setOnClickListener(v -> {
                Toast.makeText(getBaseContext(), "点击位置:" + position, Toast.LENGTH_SHORT).show();
            });
        }

        @Override
        public int getItemCount() {
            return data.size();
        }

        class ViewHolder extends RecyclerView.ViewHolder {
            TextView textView;

            public ViewHolder(@NonNull View itemView) {
                super(itemView);
                textView = (TextView) itemView;
            }
        }
    }

    DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss.SSS");

    private List<String> generateData(int size) {
        List<String> list = new ArrayList<>();
        for (int i = 0; i < size; i++) {
            list.add(dateFormat.format(new Date()) + "     " + i);
        }
        return list;
    }
}