package com.example.alatiot;

public class DataModel {
    private String Keterangan, Waktu;
    private double GasCo, GasCo2, GasHc;
    private double humidity, temperature;
    private String led;
    private int id;

    public DataModel() {
    }

    public DataModel(int id, double GasCo, double GasCo2, double GasHc,
                     double humidity, double temperature, String led,
                     String Keterangan, String Waktu) {
        this.id = id;
        this.GasCo = GasCo;
        this.GasCo2 = GasCo2;
        this.GasHc = GasHc;
        this.humidity = humidity;
        this.temperature = temperature;
        this.led = led;
        this.Keterangan = Keterangan;
        this.Waktu = Waktu;
    }

    // Getter
    public int getId() { return id; }
    public double getGasCo() { return GasCo; }
    public double getGasCo2() { return GasCo2; }
    public double getGasHc() { return GasHc; }
    public double getHumidity() { return humidity; }
    public double getTemperature() { return temperature; }
    public String getLed() { return led; }
    public String getKeterangan() { return Keterangan; }
    public String getWaktu() { return Waktu; }

    // Setter
    public void setId(int id) { this.id = id; }
    public void setGasCo(double gasCo) { this.GasCo = gasCo; }
    public void setGasCo2(double gasCo2) { this.GasCo2 = gasCo2; }
    public void setGasHc(double gasHc) { this.GasHc = gasHc; }
    public void setHumidity(double humidity) { this.humidity = humidity; }
    public void setTemperature(double temperature) { this.temperature = temperature; }
    public void setLed(String led) { this.led = led; }
    public void setKeterangan(String keterangan) { this.Keterangan = keterangan; }
    public void setWaktu(String waktu) { this.Waktu = waktu; }
}
