package com.lee.richview;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        findViewById(R.id.btRefreshAndLoad).setOnClickListener(v -> {
            startActivity(new Intent(this, RefreshAndLoadViewTestActivity.class));
        });
    }
}