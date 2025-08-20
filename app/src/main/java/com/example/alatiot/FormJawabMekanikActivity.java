package com.example.alatiot;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.FirebaseFirestore;

public class FormJawabMekanikActivity extends AppCompatActivity {

    private TextView tvNamaKendaraan, tvJenisKendaraan, tvKeterangan, tvSolusi, tvGasData;
    private Spinner spStatus;
    private Button btnSimpan;
    private String documentId;
    private FirebaseFirestore firestore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_form_jawab_mekanik);

        firestore = FirebaseFirestore.getInstance();

        // Ambil data intent
        documentId = getIntent().getStringExtra("documentId");
        String namaKendaraan = getIntent().getStringExtra("namaKendaraan");
        String jenisKendaraan = getIntent().getStringExtra("jenisKendaraan");
        String keterangan = getIntent().getStringExtra("keterangan");
        String solusi = getIntent().getStringExtra("solusi");
        double gasCO = getIntent().getDoubleExtra("gasCO", 0);
        double gasCO2 = getIntent().getDoubleExtra("gasCO2", 0);
        double temperature = getIntent().getDoubleExtra("temperature", 0);
        String status = getIntent().getStringExtra("status");

        // Init view
        tvNamaKendaraan = findViewById(R.id.tvNamaKendaraan);
        tvJenisKendaraan = findViewById(R.id.tvJenisKendaraan);
        tvKeterangan = findViewById(R.id.tvKeterangan);
        tvSolusi = findViewById(R.id.tvSolusi);
        tvGasData = findViewById(R.id.tvGasData);
        spStatus = findViewById(R.id.spStatus);
        btnSimpan = findViewById(R.id.btnSimpan);

        // Set data (disabled edit)
        tvNamaKendaraan.setText(namaKendaraan);
        tvJenisKendaraan.setText(jenisKendaraan);
        tvKeterangan.setText(keterangan);
        tvSolusi.setText(solusi);
        tvGasData.setText("Gas CO: " + gasCO + "\nGas CO₂: " + gasCO2 + "\nTemperature: " + temperature);

        // Spinner setup
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item,
                new String[]{"Solusi Berhasil", "Solusi Gagal"});
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spStatus.setAdapter(adapter);

        // Button simpan
        btnSimpan.setOnClickListener(v -> {
            String selectedStatus = spStatus.getSelectedItem().toString();

            firestore.collection("documentID")
                    .document(documentId)
                    .update("status", selectedStatus)
                    .addOnSuccessListener(aVoid -> {
                        Toast.makeText(this, "Status berhasil diperbarui", Toast.LENGTH_SHORT).show();
                        finish();
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(this, "Gagal memperbarui status", Toast.LENGTH_SHORT).show();
                    });
        });
    }
}
