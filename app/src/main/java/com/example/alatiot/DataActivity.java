package com.example.alatiot;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class DataActivity extends AppCompatActivity {
    private List<DataModel> dataList;
    private SQLiteOperations sqliteOperations;
    private TableLayout stk;
    private TextView no, gasco, gasco2, temp, ket;
    private EditText searchText;
    private Button searchButton, exportButton;
    private List<DataModel> data, originalData;
    private boolean isAscending = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.data_activity);
        initializeData();
        initializeViews();
        fetchHistoryFromSQLite();
        setClickListener();
    }

    private void initializeData() {
        dataList = new ArrayList<>();
        sqliteOperations = new SQLiteOperations(this);
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
        exportButton = findViewById(R.id.exportButton);

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
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // Tidak digunakan
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                searchHistory(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {
                // Tidak digunakan
            }
        });
        exportButton.setOnClickListener(v -> exportCSVFileIntent());
    }

    private void fetchHistoryFromSQLite() {
        List<DataModel> dataModels = sqliteOperations.getAllData();
        if (dataModels != null && !dataModels.isEmpty()) {
            dataList.addAll(dataModels);
        }
        data = dataModels;
        initTable();
    }

    private void initTable() {
        clearTableExceptHeader();
        for (int i = 0; i < dataList.size(); i++) {
            DataModel data = dataList.get(i);
            TableRow tbrow = new TableRow(this);

            tbrow.addView(createTextView(String.valueOf(data.getId())));
            tbrow.addView(createTextView(String.valueOf(data.getGasCo())));
            tbrow.addView(createTextView(String.valueOf(data.getGasCo2())));
            tbrow.addView(createTextView(String.valueOf(data.getTemperature())));
            tbrow.addView(createTextView(data.getKeterangan()));

            stk.addView(tbrow);
        }
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

        if (query.contains("=")) {
            String[] parts = query.split("=");
            if (parts.length == 2) {
                String key = parts[0].trim().toLowerCase();
                String value = parts[1].trim();

                for (DataModel datas : originalData) {
                    switch (key) {
                        case "no":
                            if (String.valueOf(datas.getId()).contains(value)) {
                                filteredData.add(datas);
                            }
                            break;
                        case "gasco":
                            if (String.valueOf(datas.getGasCo()).contains(value)) {
                                filteredData.add(datas);
                            }
                            break;
                        case "gasco2":
                            if (String.valueOf(datas.getGasCo2()).contains(value)) {
                                filteredData.add(datas);
                            }
                            break;
                        case "temp":
                            if (String.valueOf(datas.getTemperature()).contains(value)){
                                filteredData.add(datas);
                            }
                            break;
                        case "ket":
                            if (datas.getKeterangan().toLowerCase().contains(value.toLowerCase())) {
                                filteredData.add(datas);
                            }
                            break;
                        default:
                            // If the key is not recognized, show all data
                            updateTable(originalData);
                            return;
                    }
                }
            } else {
                updateTable(originalData);
                return;
            }
        } else {
            for (DataModel datas : originalData) {
                if (String.valueOf(datas.getId()).contains(query) ||
                        String.valueOf(datas.getGasCo()).contains(query) ||
                        String.valueOf(datas.getGasCo2()).contains(query) ||
                        String.valueOf(datas.getTemperature()).contains(query) ||
                        datas.getKeterangan().toLowerCase().contains(query)) {
                    filteredData.add(datas);
                }
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
            tbrow.addView(createTextView(String.valueOf(datas.getGasCo())));
            tbrow.addView(createTextView(String.valueOf(datas.getGasCo2())));
            tbrow.addView(createTextView(String.valueOf(datas.getTemperature())));
            tbrow.addView(createTextView(datas.getKeterangan()));

            stk.addView(tbrow);
        }
    }
    public void exportCSVFileIntent() {
        String fileName = "Data_Emisi.csv";
        Intent intent = new Intent(Intent.ACTION_CREATE_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("text/csv");
        intent.putExtra(Intent.EXTRA_TITLE, fileName);
        startActivityForResult(intent, 1001);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1001 && resultCode == RESULT_OK && data != null) {
            Uri uri = data.getData();
            exportDataToCSV(uri);
        }
    }

    private void exportDataToCSV(Uri uri) {
        SQLiteOperations dbOps = new SQLiteOperations(this);
        List<DataModel> dataList = dbOps.getAllData();

        if (dataList.isEmpty()) {
            Toast.makeText(this, "Tidak ada data untuk diekspor", Toast.LENGTH_SHORT).show();
            return;
        }

        StringBuilder data = new StringBuilder();
        data.append("No,Gas Co,Gas CO2,Temp,Keterangan\n");

        for (int i = 0; i < dataList.size(); i++) {
            DataModel d = dataList.get(i);
            data.append(i + 1).append(",")
                    .append(d.getGasCo()).append(",")
                    .append(d.getGasCo2()).append(",")
                    .append(d.getTemperature()).append(",")
                    .append(d.getKeterangan()).append("\n");
        }

        try {
            OutputStream out = getContentResolver().openOutputStream(uri);
            if (out == null) {
                Toast.makeText(this, "Tidak bisa membuka file", Toast.LENGTH_SHORT).show();
                return;
            }
            out.write(data.toString().getBytes());
            out.close();

            Toast.makeText(this, "Berhasil export ke file CSV", Toast.LENGTH_LONG).show();
        } catch (Exception e) {
            Toast.makeText(this, "Gagal export: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }

}
