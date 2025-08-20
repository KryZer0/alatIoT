package com.example.alatiot;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.widget.Button;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class SolusiMekanikActivity extends AppCompatActivity {

    private TableLayout tableLayout;
    private FirebaseFirestore firestore;
    private List<DataModel> dataList = new ArrayList<>();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_solusi_mekanik);

        tableLayout = findViewById(R.id.tableLayout);
        firestore = FirebaseFirestore.getInstance();

        fetchSolusiMekanik();
    }

    private void fetchSolusiMekanik() {
        firestore.collection("documentID")
                .whereNotEqualTo("status", "solusi berhasil")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    dataList.clear();
                    int counter = 1;

                    for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                        double gasCO = document.getDouble("gasCO") != null ? document.getDouble("gasCO") : 0;
                        double gasCO2 = document.getDouble("gasCO2") != null ? document.getDouble("gasCO2") : 0;
                        double temperature = document.getDouble("temperature") != null ? document.getDouble("temperature") : 0;
                        String keterangan = document.getString("keterangan");
                        String namaKendaraan = document.getString("namaKendaraan");
                        String jenisKendaraan = document.getString("jenisKendaraan");
                        String solusi = document.getString("solusi");
                        String status = document.getString("status");

                        DataModel data = new DataModel(
                                counter++,
                                gasCO,
                                gasCO2,
                                0,
                                temperature,
                                "",
                                keterangan != null ? keterangan : "",
                                "",
                                namaKendaraan != null ? namaKendaraan : "",
                                jenisKendaraan != null ? jenisKendaraan : "",
                                status != null ? status : "",
                                solusi != null ? solusi : ""
                        );

                        data.setDocumentId(document.getId());
                        dataList.add(data);
                    }

                    initTable();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Gagal memuat data", Toast.LENGTH_SHORT).show();
                    Log.e("Firestore", "Error: ", e);
                });
    }

    private void initTable() {
        tableLayout.removeAllViews();

        // Header
        TableRow header = new TableRow(this);
        header.setBackgroundColor(getResources().getColor(android.R.color.darker_gray));
        header.addView(createTextView("No", true));
        header.addView(createTextView("Nama", true));
        header.addView(createTextView("Jenis", true));
        header.addView(createTextView("Solusi", true));
        header.addView(createTextView("Aksi", true));
        tableLayout.addView(header);

        // Isi tabel
        for (int i = 0; i < dataList.size(); i++) {
            DataModel model = dataList.get(i);
            TableRow row = new TableRow(this);
            row.addView(createTextView(String.valueOf(i + 1), false));
            row.addView(createTextView(model.getNama(), false));
            row.addView(createTextView(model.getJenis(), false));
            row.addView(createTextView(model.getSolusi(), false));

            Button btnJawab = new Button(this);
            btnJawab.setText("Jawab");
            btnJawab.setOnClickListener(v -> {
                Intent intent = new Intent(SolusiMekanikActivity.this, FormJawabMekanikActivity.class);
                intent.putExtra("documentId", model.getDocumentId());
                intent.putExtra("namaKendaraan", model.getNama());
                intent.putExtra("jenisKendaraan", model.getJenis());
                intent.putExtra("keterangan", model.getKeterangan());
                intent.putExtra("solusi", model.getSolusi());
                intent.putExtra("gasCO", model.getGasCo());
                intent.putExtra("gasCO2", model.getGasCo2());
                intent.putExtra("temperature", model.getTemperature());
                intent.putExtra("status", model.getStatus());
                startActivity(intent);
            });

            row.addView(btnJawab);
            tableLayout.addView(row);
        }
    }

    private TextView createTextView(String text, boolean isHeader) {
        TextView tv = new TextView(this);
        tv.setText(text);
        tv.setPadding(16, 12, 16, 12);
        tv.setGravity(Gravity.CENTER);
        if (isHeader) {
            tv.setTextColor(getResources().getColor(android.R.color.white));
        }
        return tv;
    }
}
