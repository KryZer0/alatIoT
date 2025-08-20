package com.example.alatiot;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.FirebaseFirestore;

public class FormJawabActivity extends AppCompatActivity {

    private TextView tvNamaKendaraan, tvJenisKendaraan, tvKeterangan, tvGasData;
    private EditText etSolusi, etStatus;
    private Button btnSimpan;

    private String documentId;
    private FirebaseFirestore firestore;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_form_jawab);

        firestore = FirebaseFirestore.getInstance();

        // Ambil data dari Intent
        documentId = getIntent().getStringExtra("documentId");
        String namaKendaraan = getIntent().getStringExtra("namaKendaraan");
        String jenisKendaraan = getIntent().getStringExtra("jenisKendaraan");
        String keterangan = getIntent().getStringExtra("keterangan");
        String solusi = getIntent().getStringExtra("solusi");
        String status = getIntent().getStringExtra("getStatus");

        double gasCO = getIntent().getDoubleExtra("gasCO", 0);
        double gasCO2 = getIntent().getDoubleExtra("gasCO2", 0);
        double temperature = getIntent().getDoubleExtra("temperature", 0);

        // Inisialisasi view
        tvNamaKendaraan = findViewById(R.id.tvNamaKendaraan);
        tvJenisKendaraan = findViewById(R.id.tvJenisKendaraan);
        tvKeterangan = findViewById(R.id.tvKeterangan);
        tvGasData = findViewById(R.id.tvGasData);
        etSolusi = findViewById(R.id.etSolusi);
        etStatus = findViewById(R.id.etStatus);
        btnSimpan = findViewById(R.id.btnSimpan);

        // Set data ke view
        tvNamaKendaraan.setText(namaKendaraan);
        tvJenisKendaraan.setText(jenisKendaraan);
        tvKeterangan.setText(keterangan);
        tvGasData.setText("Gas CO: " + gasCO + "\nGas CO₂: " + gasCO2 + "\nTemperature: " + temperature);

        etSolusi.setText(solusi);
        etStatus.setText(status);

        btnSimpan.setOnClickListener(v -> {
            String solusiInput = etSolusi.getText().toString();
            String statusInput = "Menunggu Konfirmasi Solusi";

            if (solusiInput.isEmpty() || statusInput.isEmpty()) {
                Toast.makeText(this, "Harap isi solusi dan status!", Toast.LENGTH_SHORT).show();
                return;
            }

            firestore.collection("documentID")
                    .document(documentId)
                    .update(
                            "solusi", solusiInput,
                            "status", statusInput
                    )
                    .addOnSuccessListener(aVoid -> {
                        Toast.makeText(this, "Data berhasil diperbarui", Toast.LENGTH_SHORT).show();
                        finish();
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(this, "Gagal memperbarui data", Toast.LENGTH_SHORT).show();
                    });
        });
    }
}
