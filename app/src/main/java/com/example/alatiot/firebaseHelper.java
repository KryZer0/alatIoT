package com.example.alatiot;

import android.util.Log;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class firebaseHelper {

    private static final String DB_URL = "https://alatiot-dc957-default-rtdb.asia-southeast1.firebasedatabase.app";
    private static final String NODE = "FirebaseIOT";

    public interface DataListener {
        void onDataReceived(Double temperature, Double gasCO, Double gasCO2);
        void onError(DatabaseError error);
    }
    public static void kirimDataRandomKeFirebase() {
        DatabaseReference ref = FirebaseDatabase
                .getInstance(DB_URL)
                .getReference(NODE);

        Random random = new Random();

        double humidity = 30 + (random.nextDouble() * 50); // 30 - 80
        double temperature = 20 + (random.nextDouble() * 15); // 20 - 35
        double gasCO = 3 + (random.nextDouble() * 5); // 3 - 8
        double gasCO2 = 5 + (random.nextDouble() * 10); // 5 - 15
        String led = random.nextBoolean() ? "1" : "0";

        Map<String, Object> data = new HashMap<>();
        data.put("humidity", humidity);
        data.put("temperature", temperature);
        data.put("gasCO", gasCO);
        data.put("gasCO2", gasCO2);
        data.put("led", led);

        ref.setValue(data)
                .addOnSuccessListener(aVoid -> Log.d("FIREBASE", "Data random berhasil dikirim"))
                .addOnFailureListener(e -> Log.e("FIREBASE", "Gagal kirim data", e));
    }

    public static void ambilDataRealtime(final DataListener listener) {
        DatabaseReference ref = FirebaseDatabase
                .getInstance(DB_URL)
                .getReference(NODE);

        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    Double temperature = snapshot.child("temperature").getValue(Double.class);
                    Double gasCO = snapshot.child("gasCO").getValue(Double.class);
                    Double gasCO2 = snapshot.child("gasCO2").getValue(Double.class);

                    listener.onDataReceived(temperature, gasCO, gasCO2);
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
                listener.onError(error);
            }
        });
    }

    public static void ambilDataSekali(final DataListener listener) {
        DatabaseReference ref = FirebaseDatabase
                .getInstance(DB_URL)
                .getReference(NODE);

        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    Double temperature = snapshot.child("temperature").getValue(Double.class);
                    Double gasCO = snapshot.child("gasCO").getValue(Double.class);
                    Double gasCO2 = snapshot.child("gasCO2").getValue(Double.class);

                    listener.onDataReceived(temperature, gasCO, gasCO2);
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
                listener.onError(error);
            }
        });
    }

}
