package com.example.alatiot;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import com.google.firebase.database.ValueEventListener;

public class monitoringActivity extends AppCompatActivity {
    private TextView humid, temp;
    private View led;

    private double lastHumidity = -1;
    private double lastTemp = -1;
    private String lastLed = "";

    private SQLiteOperations dbOperation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.monitor_activity);
        initializeViews();

        dbOperation = new SQLiteOperations(this);

        DatabaseReference ref = FirebaseDatabase.getInstance()
                .getReference("FirebaseIOT");

        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    Double humidity = snapshot.child("humidity").getValue(Double.class);
                    Double temperature = snapshot.child("temperature").getValue(Double.class);
                    String ledStatus = snapshot.child("led").getValue(String.class);

                    if (humidity != null && temperature != null && ledStatus != null) {
                        humid.setText(String.format("%.1f%%", humidity));
                        temp.setText(String.format("%.1f°C", temperature));

                        if (ledStatus.equals("0")) {
                            led.setBackgroundTintList(getResources().getColorStateList(R.color.green, null));
                        } else {
                            led.setBackgroundTintList(getResources().getColorStateList(R.color.red, null));
                        }

                        if (humidity != lastHumidity || temperature != lastTemp || !ledStatus.equals(lastLed)) {
                            dbOperation.addData(humidity, temperature, ledStatus);
                            lastHumidity = humidity;
                            lastTemp = temperature;
                            lastLed = ledStatus;
                        }
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Handle Firebase error
            }
        });
    }

    private void initializeViews() {
        humid = findViewById(R.id.humidityValue);
        temp = findViewById(R.id.tempValue);
        led = findViewById(R.id.statusIndicator);
    }
}
