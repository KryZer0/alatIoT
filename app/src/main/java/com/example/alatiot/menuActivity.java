package com.example.alatiot;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class menuActivity extends AppCompatActivity {

    private Button monitor, data, solusi;
    private String role, loginStatus;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dashboard_activity);
        Intent intent = getIntent();
        loginStatus = intent.getStringExtra("login");
        role = intent.getStringExtra("role");
        findViewById();
        setClickListener();
    }

    private void setClickListener()
    {
        monitor.setOnClickListener(v -> monitor());
        solusi.setOnClickListener(v -> solusiMekanik());

        if (isRoleOwner()){
            data.setOnClickListener(v -> solusiTable());
        } else {
            data.setOnClickListener(v -> data());
        }

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

    private void solusiTable()
    {
        Intent intent = new Intent(menuActivity.this,SolusiActivity.class);
        startActivity(intent);
    }

    private void solusiMekanik() {
        Intent intent = new Intent(menuActivity.this, SolusiMekanikActivity.class);
        startActivity(intent);
    }

    private void findViewById() {
        monitor = findViewById(R.id.check_in_btn);
        data = findViewById(R.id.check_out_btn);
        solusi = findViewById(R.id.solusi_btn);

        if ("user".equals(loginStatus)) {
            data.setVisibility(View.VISIBLE);
        } else if ("guess".equals(loginStatus)) {
            data.setVisibility(View.GONE);
        }

        if (isRoleOwner()){
            data.setText("solusi");
            solusi.setVisibility(View.GONE);
        } else {

        }
    }

    private boolean isRoleOwner(){
        return "owner".equals(role);
    }
}
