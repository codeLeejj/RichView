package com.lee.richview;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

import com.lee.richview.banner.BannerDemoActivity;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        findViewById(R.id.btRefreshAndLoad).setOnClickListener(v -> {
            startActivity(new Intent(this, RefreshAndLoadViewTestActivity.class));
        });
        findViewById(R.id.btBanner).setOnClickListener(v -> {
            startActivity(new Intent(this, BannerDemoActivity.class));
        });
    }
}