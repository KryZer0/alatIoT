package com.example.alatiot;

public class DataModel {
    private String Keterangan, Waktu;
    private double GasCo, GasCo2, GasHc;
    private int id;

    DataModel() {
    }

    public DataModel (int id,  double GasCo, double GasCo2, double GasHc,
                      String Keterangan){
        this.id = id;
        this.GasCo = GasCo;
        this.GasCo2 = GasCo2;
        this.GasHc = GasHc;
        this.Keterangan = Keterangan;
    }

    public String getKeterangan() {
        return Keterangan;
    }

    public String getWaktu() {
        return Waktu;
    }

    public double getGasCo() {
        return GasCo;
    }

    public double getGasCo2() {
        return GasCo2;
    }

    public double getGasHc() {
        return GasHc;
    }

    public int getId() {
        return id;
    }

    public void setKeterangan(String keterangan) {
        Keterangan = keterangan;
    }

    public void setWaktu(String waktu) {
        Waktu = waktu;
    }

    public void setGasCo(double gasCo) {
        GasCo = gasCo;
    }

    public void setGasCo2(double gasCo2) {
        GasCo2 = gasCo2;
    }

    public void setGasHc(double gasHc) {
        GasHc = gasHc;
    }

    public void setId(int id) {
        this.id = id;
    }
}
