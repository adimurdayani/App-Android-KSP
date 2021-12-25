package com.eka.koperasisimpanpinjam.network.model;

public class DataPinjaman{
    private int id, user_id, tenor, status, biaya_admin, jumlah;
    private double bunga;
    private String no_pinjaman, keterangan, created_at, member_id;

    public double getBunga() {
        return bunga;
    }

    public void setBunga(double bunga) {
        this.bunga = bunga;
    }

    public String getMember_id() {
        return member_id;
    }

    public void setMember_id(String member_id) {
        this.member_id = member_id;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getUser_id() {
        return user_id;
    }

    public void setUser_id(int user_id) {
        this.user_id = user_id;
    }

    public int getTenor() {
        return tenor;
    }

    public void setTenor(int tenor) {
        this.tenor = tenor;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public int getBiaya_admin() {
        return biaya_admin;
    }

    public void setBiaya_admin(int biaya_admin) {
        this.biaya_admin = biaya_admin;
    }

    public int getJumlah() {
        return jumlah;
    }

    public void setJumlah(int jumlah) {
        this.jumlah = jumlah;
    }

    public String getNo_pinjaman() {
        return no_pinjaman;
    }

    public void setNo_pinjaman(String no_pinjaman) {
        this.no_pinjaman = no_pinjaman;
    }

    public String getKeterangan() {
        return keterangan;
    }

    public void setKeterangan(String keterangan) {
        this.keterangan = keterangan;
    }

    public String getCreated_at() {
        return created_at;
    }

    public void setCreated_at(String created_at) {
        this.created_at = created_at;
    }
}
