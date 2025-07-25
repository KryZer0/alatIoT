package com.example.alatiot;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class menuActivity extends AppCompatActivity {

    private Button monitor, data;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dashboard_activity);
        findViewById();
        setClickListener();
    }

    private void setClickListener()
    {
        monitor.setOnClickListener(v -> monitor());
        data.setOnClickListener(v -> data());
    }

    private void monitor()
    {
        Intent intent = new Intent(menuActivity.this,monitoringActivity.class);
        startActivity(intent);
    }

    private void data()
    {
        Intent intent = new Intent(menuActivity.this,DataActivity.class);
        startActivity(intent);
    }

    private void findViewById() {
        monitor = findViewById(R.id.check_in_btn);
        data = findViewById(R.id.check_out_btn);

        Intent intent = getIntent();
        String loginStatus = intent.getStringExtra("login");

        if ("user".equals(loginStatus)) {
            data.setVisibility(View.VISIBLE);
        } else if ("guess".equals(loginStatus)) {
            data.setVisibility(View.GONE);
        }
    }
}
