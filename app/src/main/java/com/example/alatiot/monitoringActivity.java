package com.example.alatiot;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.LinkedList;

public class monitoringActivity extends AppCompatActivity {

    private TextView temp, gasCo, gasCo2, keteranganLabel, COStatus, CO2Status;
    private TextView led;
    private Button record;
    private boolean isRecording = false;
    private boolean dangerDataSent = false;
    private String namaKendaraan = "";
    private String jenisKendaraan = "";

    private FirebaseFirestore firestore;
    private TableLayout tableLayout;
    private MediaPlayer mediaPlayer;
    private final LinkedList<HashMap<String, String>> dataList = new LinkedList<>();
    private final int MAX_ROWS = 5;
    private double lastTemp = -1;
    private double lastGasCo = -1;
    private double lastGasCo2 = -1;
    private String lastLed = "";


    private SQLiteOperations dbOperation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.monitor_activity);
        initializeViews();

        dbOperation = new SQLiteOperations(this);
        firestore = FirebaseFirestore.getInstance();

        // Data awal
        firebaseHelper.ambilDataSekali(new firebaseHelper.DataListener() {
            @Override
            public void onDataReceived(Double temperature, Double gasCO, Double gasCO2) {
                updateUIAndStore(temperature, gasCO, gasCO2);
            }

            @Override
            public void onError(DatabaseError error) {
                Log.e("FIREBASE_ERROR", "Gagal ambil data awal: " + error.getMessage());
            }
        });

        // Data realtime
        firebaseHelper.ambilDataRealtime(new firebaseHelper.DataListener() {
            @Override
            public void onDataReceived(Double temperature, Double gasCO, Double gasCO2) {
                updateUIAndStore(temperature, gasCO, gasCO2);
            }

            @Override
            public void onError(DatabaseError error) {
                Log.e("FIREBASE_ERROR", "Gagal pantau data: " + error.getMessage());
            }
        });

        // Tombol record
        record.setOnClickListener(v -> {
            if (!isRecording) {
                showRecordDialog();
            } else {
                stopRecording();
            }
        });
    }

    private void showRecordDialog() {
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_record_form, null);

        EditText edtNama = dialogView.findViewById(R.id.edtNamaKendaraan);
        Spinner spnJenis = dialogView.findViewById(R.id.spnJenisKendaraan);

        // Isi spinner contoh data
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_dropdown_item,
                new String[]{"Pilih jenis", "2 Tak", "4 Tak"});
        spnJenis.setAdapter(adapter);

        new AlertDialog.Builder(this)
                .setTitle("Mulai Rekam Data")
                .setView(dialogView)
                .setPositiveButton("Mulai", (dialog, which) -> {
                    String nama = edtNama.getText().toString().trim();
                    String jenis = spnJenis.getSelectedItem().toString();

                    if (nama.isEmpty() || jenis.equals("Pilih jenis")) {
                        Toast.makeText(this, "Lengkapi semua data!", Toast.LENGTH_SHORT).show();
                    } else {
                        namaKendaraan = nama;
                        jenisKendaraan = jenis;
                        startRecording();
                    }
                })
                .setNegativeButton("Batal", null)
                .show();
    }

    private void startRecording() {
        isRecording = true;
        dangerDataSent = false;
        record.setText("Stop");
        Toast.makeText(this, "Perekaman dimulai", Toast.LENGTH_SHORT).show();
    }

    private void stopRecording() {
        isRecording = false;
        record.setText("Record");
        Toast.makeText(this, "Perekaman dihentikan", Toast.LENGTH_SHORT).show();
    }

    private void updateUIAndStore(Double temperature, Double gasCO, Double gasCO2) {
        if (temperature != null && gasCO != null && gasCO2 != null) {
            temp.setText(String.format("%.1f°C", temperature));
            gasCo.setText(String.format("%.2f ppm", gasCO));
            gasCo2.setText(String.format("%.2f ppm", gasCO2));

            String keterangan = "";
            if (gasCO2 >= 3000) {
                CO2Status.setText("Bahaya");
                CO2Status.setTextColor(ContextCompat.getColor(this, R.color.red));
            } else {
                CO2Status.setText("Aman");
                CO2Status.setTextColor(ContextCompat.getColor(this, R.color.green));
            }

            if (gasCO >= 700) {
                COStatus.setText("Bahaya");
                COStatus.setTextColor(ContextCompat.getColor(this, R.color.red));
            } else {
                COStatus.setText("Aman");
                COStatus.setTextColor(ContextCompat.getColor(this, R.color.green));
            }

            if (gasCO2 > 3 || temperature > 50 || gasCO > 1) {
                led.setBackgroundTintList(getResources().getColorStateList(R.color.red, null));
                led.setText("Bad");
                keterangan = "Bad";
                keteranganLabel.setText("Uji Emisi Gagal (Kualitas udara buruk)");

                // Vibrate
                Vibrator vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
                if (vibrator != null && vibrator.hasVibrator()) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        vibrator.vibrate(VibrationEffect.createOneShot(1000, VibrationEffect.DEFAULT_AMPLITUDE));
                    } else {
                        vibrator.vibrate(1000);
                    }
                }

                // Sound
                playAlertSound();

                if (isRecording && !dangerDataSent) {
                    HashMap<String, Object> data = new HashMap<>();
                    data.put("gasCO", gasCO);
                    data.put("gasCO2", gasCO2);
                    data.put("temperature", temperature);
                    data.put("keterangan", keterangan);
                    data.put("namaKendaraan", namaKendaraan);
                    data.put("jenisKendaraan", jenisKendaraan);
                    data.put("solusi", "Belum Ada solusi");
                    data.put("status", "Menunggu Solusi");
                    data.put("timestamp", System.currentTimeMillis());

                    firestore.collection("documentID")
                            .add(data)
                            .addOnSuccessListener(documentReference -> {
                                Log.d("FIREBASE", "Data bahaya berhasil dikirim dengan ID: " + documentReference.getId());
                            })
                            .addOnFailureListener(e -> {
                                Log.e("FIREBASE", "Gagal kirim data bahaya: " + e.getMessage());
                            });

                    dangerDataSent = true;
                }
            } else {
                led.setBackgroundTintList(getResources().getColorStateList(R.color.green, null));
                led.setText("Good");
                keterangan = "Good";
                keteranganLabel.setText("Uji Emisi Lolos");
            }

            if (isRecording) {
                DataModel lastData = dbOperation.getLastData();
                boolean isDifferentFromDB = lastData == null ||
                        lastData.getTemperature() != temperature ||
                        lastData.getGasCo() != gasCO ||
                        lastData.getGasCo2() != gasCO2;

                if (isDifferentFromDB) {
                    dbOperation.addData(gasCO, gasCO2, temperature, keterangan, namaKendaraan, jenisKendaraan);
                    lastTemp = temperature;
                    lastGasCo = gasCO;
                    lastGasCo2 = gasCO2;
                }
            }
            tambahDataKeTabel(gasCO, gasCO2, temperature, keterangan);
        }
    }
    private void tambahDataKeTabel(Double gasCO, Double gasCO2, Double temperature, String keterangan) {
        HashMap<String, String> row = new HashMap<>();
        row.put("co", String.format("%.2f ppm", gasCO));
        row.put("co2", String.format("%.2f ppm", gasCO2));
        row.put("temp", String.format("%.1f°C", temperature));
        row.put("ket", keterangan);

        if (dataList.size() >= MAX_ROWS) {
            dataList.removeFirst();
        }
        dataList.add(row);

        tampilkanDataKeTabel();
    }

    private void tampilkanDataKeTabel() {
        tableLayout.removeViews(1, Math.max(0, tableLayout.getChildCount() - 1));

        int index = 1;
        for (HashMap<String, String> row : dataList) {
            TableRow tableRow = new TableRow(this);

            TextView no = new TextView(this);
            no.setText(String.valueOf(index));
            no.setGravity(View.TEXT_ALIGNMENT_CENTER);
            no.setBackgroundResource(R.drawable.squared_box);
            no.setTextColor(ContextCompat.getColor(this, R.color.black));
            no.setPadding(8, 8, 8, 8);

            TextView co = new TextView(this);
            co.setText(row.get("co"));
            co.setTextColor(ContextCompat.getColor(this, R.color.black));
            co.setBackgroundResource(R.drawable.squared_box);
            co.setPadding(8, 8, 8, 8);

            TextView co2 = new TextView(this);
            co2.setText(row.get("co2"));
            co2.setBackgroundResource(R.drawable.squared_box);
            co2.setTextColor(ContextCompat.getColor(this, R.color.black));
            co2.setPadding(8, 8, 8, 8);

            TextView temp = new TextView(this);
            temp.setText(row.get("temp"));
            temp.setBackgroundResource(R.drawable.squared_box);
            temp.setTextColor(ContextCompat.getColor(this, R.color.black));
            temp.setPadding(8, 8, 8, 8);

            TextView ket = new TextView(this);
            ket.setText(row.get("ket"));
            ket.setBackgroundResource(R.drawable.squared_box);
            ket.setTextColor(ContextCompat.getColor(this, R.color.black));
            ket.setPadding(8, 8, 8, 8);

            tableRow.addView(no);
            tableRow.addView(co);
            tableRow.addView(co2);
            tableRow.addView(temp);
            tableRow.addView(ket);

            tableLayout.addView(tableRow);
            index++;
        }
    }

    private void initializeViews() {
        temp = findViewById(R.id.tempValue);
        gasCo = findViewById(R.id.coValue);
        gasCo2 = findViewById(R.id.co2Value);
        led = findViewById(R.id.emisiStatus);
        COStatus = findViewById(R.id.coStatus);
        CO2Status = findViewById(R.id.co2Status);
        keteranganLabel = findViewById(R.id.kesimpulanValue);
        tableLayout = findViewById(R.id.tableAbsensi);
        record = findViewById(R.id.record);
    }

    private void playAlertSound() {
        stopAlertSound();
        mediaPlayer = MediaPlayer.create(this, R.raw.alert_sound);
        mediaPlayer.start();
    }

    @Override
    protected void onPause() {
        super.onPause();
        stopAlertSound();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        stopAlertSound();
    }

    private void stopAlertSound() {
        if (mediaPlayer != null) {
            if (mediaPlayer.isPlaying()) {
                mediaPlayer.stop();
            }
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }
}
