package com.example.alatiot;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class SolusiActivity extends AppCompatActivity {
    private List<DataModel> dataList;
    private TableLayout stk;
    private TextView no, gasco, gasco2, temp, ket;
    private EditText searchText;
    private Button searchButton;
    private List<DataModel> data, originalData;
    private boolean isAscending = true;

    private FirebaseFirestore firestore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.solusi_activity);

        initializeData();
        initializeViews();
        fetchHistoryFromFirestore();
        setClickListener();
    }

    private void initializeData() {
        dataList = new ArrayList<>();
        firestore = FirebaseFirestore.getInstance();
    }

    private void initializeViews() {
        stk = findViewById(R.id.tableAbsensi);
        no = findViewById(R.id.headerNo);
        gasco = findViewById(R.id.headerGas);
        gasco2 = findViewById(R.id.headerGas2);
        temp = findViewById(R.id.headerTemp);
        ket = findViewById(R.id.headerKet);

        searchButton = findViewById(R.id.searchButton);
        searchText = findViewById(R.id.searchEdit);
    }

    private void setClickListener() {
        no.setOnClickListener(v -> history("no"));
        gasco.setOnClickListener(v -> history("gasco"));
        gasco2.setOnClickListener(v -> history("gasco2"));
        temp.setOnClickListener(v -> history("temp"));
        ket.setOnClickListener(v -> history("ket"));
        searchButton.setOnClickListener(v -> searchHistory(searchText.getText().toString()));
        searchText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                searchHistory(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });
    }

    private void fetchHistoryFromFirestore() {
        firestore.collection("documentID")
                .whereNotEqualTo("status", "Solusi Berhasil")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    dataList.clear();
                    data = new ArrayList<>();
                    originalData = new ArrayList<>();
                    int idCounter = 1;

                    for (QueryDocumentSnapshot document : queryDocumentSnapshots) {

                        double gasCO = document.getDouble("gasCO") != null ? document.getDouble("gasCO") : 0;
                        double gasCO2 = document.getDouble("gasCO2") != null ? document.getDouble("gasCO2") : 0;
                        double temperature = document.getDouble("temperature") != null ? document.getDouble("temperature") : 0;
                        String keterangan = document.getString("keterangan");
                        String namaKendaraan = document.getString("namaKendaraan");
                        String jenisKendaraan = document.getString("jenisKendaraan");
                        String solusi = document.getString("solusi");
                        String status = document.getString("status");

                        double gasHc = 0;
                        String led = "";
                        String waktu = "";

                        DataModel dataItem = new DataModel(
                                idCounter++,
                                gasCO,
                                gasCO2,
                                gasHc,
                                temperature,
                                led,
                                keterangan != null ? keterangan : "",
                                waktu,
                                namaKendaraan != null ? namaKendaraan : "",
                                jenisKendaraan != null ? jenisKendaraan : "",
                                status != null ? status : "",
                                solusi != null ? solusi : ""
                        );

                        dataItem.setDocumentId(document.getId());

                        dataList.add(dataItem);
                        data.add(dataItem);
                    }

                    originalData.addAll(data);

                    Toast.makeText(this, "Berhasil memuat data dari Firestore", Toast.LENGTH_SHORT).show();
                    initTable();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Gagal memuat data dari Firestore", Toast.LENGTH_SHORT).show();
                    Log.e("Firestore", "Gagal membaca data", e);
                });
    }


    private void showEmissionDialog(DataModel data) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Detail Data Emisi");
        String message = "Gas CO: " + data.getGasCo() +
                "\nGas CO₂: " + data.getGasCo2() +
                "\nTemperature: " + data.getTemperature();
        builder.setMessage(message);
        builder.setPositiveButton("OK", (dialog, which) -> dialog.dismiss());
        builder.show();
    }

    private void initTable() {
        clearTableExceptHeader();
        for (int i = 0; i < dataList.size(); i++) {
            DataModel data = dataList.get(i);
            TableRow tbrow = new TableRow(this);

            tbrow.addView(createTextView(String.valueOf(i + 1)));

            TextView btnEmisi = createSmallButton("Lihat");
            btnEmisi.setOnClickListener(v -> showEmissionDialog(data));
            tbrow.addView(btnEmisi);

            TextView btnSolusi = createSmallButton("Jawab");
            btnSolusi.setOnClickListener(v -> jawabMasalah(data)); // ✅ Panggil method

            tbrow.addView(createTextView(data.getNama()));
            tbrow.addView(createTextView(data.getJenis()));
            tbrow.addView(createTextView(data.getSolusi()));
            tbrow.addView(createTextView(data.getStatus()));
            tbrow.addView(btnSolusi);

            stk.addView(tbrow);
        }
    }

    private void jawabMasalah(DataModel model) {
        Intent intent = new Intent(SolusiActivity.this, FormJawabActivity.class);

        intent.putExtra("documentId", model.getDocumentId());
        intent.putExtra("namaKendaraan", model.getNama());
        intent.putExtra("jenisKendaraan", model.getJenis());
        intent.putExtra("keterangan", model.getKeterangan());
        intent.putExtra("gasCO", model.getStatus());
        intent.putExtra("gasCO2", model.getStatus());
        intent.putExtra("temperature", model.getStatus());
        intent.putExtra("solusi", model.getSolusi());
        intent.putExtra("getStatus", model.getStatus());
        startActivity(intent);
    }


    private TextView createSmallButton(String text) {
        TextView tv = new TextView(this);
        tv.setText(text);
        tv.setGravity(Gravity.CENTER);
        tv.setPadding(16, 8, 16, 8);
        tv.setTextColor(getResources().getColor(R.color.white));
        tv.setBackgroundResource(R.drawable.squared_box);
        tv.setBackgroundColor(getResources().getColor(R.color.red));

        TableRow.LayoutParams params = new TableRow.LayoutParams(
                TableRow.LayoutParams.WRAP_CONTENT,
                TableRow.LayoutParams.WRAP_CONTENT
        );
        tv.setLayoutParams(params);
        return tv;
    }

    private TextView createTextView(String text) {
        TextView tv = new TextView(this);
        tv.setText(text);
        tv.setGravity(Gravity.CENTER);
        tv.setPadding(16, 8, 16, 8);
        tv.setTextColor(getResources().getColor(R.color.white));
        tv.setBackgroundResource(R.drawable.squared_box);

        TableRow.LayoutParams params = new TableRow.LayoutParams(
                TableRow.LayoutParams.WRAP_CONTENT,
                TableRow.LayoutParams.WRAP_CONTENT
        );
        tv.setLayoutParams(params);
        return tv;
    }

    private void clearTableExceptHeader() {
        while (stk.getChildCount() > 1) {
            stk.removeViewAt(1);
        }
    }

    private void history(String column) {
        if (data == null || data.isEmpty()) return;

        Comparator<DataModel> comparator;
        switch (column) {
            case "no":
                comparator = Comparator.comparing(DataModel::getId);
                break;
            case "gasco":
                comparator = Comparator.comparing(DataModel::getGasCo);
                break;
            case "gasco2":
                comparator = Comparator.comparing(DataModel::getGasCo2);
                break;
            case "temp":
                comparator = Comparator.comparing(DataModel::getTemperature);
                break;
            case "ket":
                comparator = Comparator.comparing(DataModel::getKeterangan);
                break;
            default:
                return;
        }

        isAscending = !isAscending;

        if (isAscending) {
            Collections.sort(data, comparator);
        } else {
            Collections.sort(data, comparator.reversed());
        }

        dataList.clear();
        dataList.addAll(data);
        initTable();
    }

    private void searchHistory(String query) {
        if (originalData == null) {
            originalData = new ArrayList<>(data);
        }

        List<DataModel> filteredData = new ArrayList<>();

        if (query.isEmpty()) {
            updateTable(originalData);
            return;
        }

        for (DataModel datas : originalData) {
            if (String.valueOf(datas.getId()).contains(query) ||
                    String.valueOf(datas.getGasCo()).contains(query) ||
                    String.valueOf(datas.getGasCo2()).contains(query) ||
                    String.valueOf(datas.getTemperature()).contains(query) ||
                    datas.getKeterangan().toLowerCase().contains(query)) {
                filteredData.add(datas);
            }
        }

        updateTable(filteredData);
    }

    private void updateTable(List<DataModel> data) {
        clearTableExceptHeader();

        for (int i = 0; i < data.size(); i++) {
            DataModel datas = data.get(i);
            TableRow tbrow = new TableRow(this);

            tbrow.addView(createTextView(String.valueOf(i + 1)));
            TextView btnEmisi = createSmallButton("Lihat");
            btnEmisi.setOnClickListener(v -> showEmissionDialog(datas));
            tbrow.addView(btnEmisi);

            tbrow.addView(createTextView(datas.getNama()));
            tbrow.addView(createTextView(datas.getJenis()));
            tbrow.addView(createTextView(datas.getKeterangan()));
            tbrow.addView(createTextView(datas.getStatus()));

            stk.addView(tbrow);
        }
    }

}
