package com.example.alatiot;

public class DataModel {
    private String Keterangan, Waktu;
    private double GasCo, GasCo2;
    private double temperature;
    private String led, nama, jenis, status, solusi;
    private int id;
    private String documentId;

    public DataModel() {
    }

    public DataModel(int id, double GasCo, double GasCo2, double GasHc, double temperature, String led,
                     String Keterangan, String Waktu, String nama, String jenis, String status, String solusi) {
        this.id = id;
        this.GasCo = GasCo;
        this.GasCo2 = GasCo2;
//        this.humidity = humidity;
        this.temperature = temperature;
        this.led = led;
        this.Keterangan = Keterangan;
        this.Waktu = Waktu;
        this.nama = nama;
        this.jenis = jenis;
        this.status = status;
        this.solusi = solusi;
    }

    // Getter
    public int getId() { return id; }
    public double getGasCo() { return GasCo; }
    public double getGasCo2() { return GasCo2; }
//    public double getHumidity() { return humidity; }
    public double getTemperature() { return temperature; }
    public String getLed() { return led; }
    public String getKeterangan() { return Keterangan; }
    public String getWaktu() { return Waktu; }
    public String getNama() { return nama; }
    public String getJenis() { return jenis; }
    public String getStatus() { return status; }
    public String getSolusi() { return solusi; }
    public String getDocumentId() {
        return documentId;
    }

    public void setDocumentId(String documentId) {
        this.documentId = documentId;
    }
    // Setter
    public void setId(int id) { this.id = id; }
    public void setGasCo(double gasCo) { this.GasCo = gasCo; }
    public void setGasCo2(double gasCo2) { this.GasCo2 = gasCo2; }
//    public void setHumidity(double humidity) { this.humidity = humidity; }
    public void setTemperature(double temperature) { this.temperature = temperature; }
    public void setLed(String led) { this.led = led; }
    public void setKeterangan(String keterangan) { this.Keterangan = keterangan; }
    public void setWaktu(String waktu) { this.Waktu = waktu; }
}
