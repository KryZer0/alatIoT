package com.example.alatiot;

import android.util.Log;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class firebaseHelper {

    public static void kirimDataRandomKeFirebase() {
        DatabaseReference ref = FirebaseDatabase
                .getInstance("https://alatiot-dc957-default-rtdb.asia-southeast1.firebasedatabase.app")
                .getReference("FirebaseIOT");

        Random random = new Random();

        double humidity = 30 + (random.nextDouble() * 50); // 30 - 80
        double temperature = 20 + (random.nextDouble() * 15); // 20 - 35
        String led = random.nextBoolean() ? "1" : "0";

        Map<String, Object> data = new HashMap<>();
        data.put("humidity", humidity);
        data.put("temperature", temperature);
        data.put("led", led);

        ref.setValue(data)
                .addOnSuccessListener(aVoid -> {
                    Log.d("FIREBASE", "Data random berhasil dikirim");
                })
                .addOnFailureListener(e -> {
                    Log.e("FIREBASE", "Gagal kirim data", e);
                });
    }

}
