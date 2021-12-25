package com.eka.koperasisimpanpinjam.network.model;

public class DataSimpanan {
    private int id, jumlah, m_id;
    private String no_simpanan, j_simpanan, member_id, created_at, catatan;

    public String getCatatan() {
        return catatan;
    }

    public void setCatatan(String catatan) {
        this.catatan = catatan;
    }

    public String getCreated_at() {
        return created_at;
    }

    public void setCreated_at(String created_at) {
        this.created_at = created_at;
    }

    public String getMember_id() {
        return member_id;
    }

    public void setMember_id(String member_id) {
        this.member_id = member_id;
    }

    public int getM_id() {
        return m_id;
    }

    public void setM_id(int m_id) {
        this.m_id = m_id;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getJumlah() {
        return jumlah;
    }

    public void setJumlah(int jumlah) {
        this.jumlah = jumlah;
    }

    public String getNo_simpanan() {
        return no_simpanan;
    }

    public void setNo_simpanan(String no_simpanan) {
        this.no_simpanan = no_simpanan;
    }

    public String getJ_simpanan() {
        return j_simpanan;
    }

    public void setJ_simpanan(String j_simpanan) {
        this.j_simpanan = j_simpanan;
    }
}
