package com.eka.koperasisimpanpinjam.network.api;

public class URLServer {
    public static final String BASE_URL = "http://koperasi-ksp.my.id/api/";
    public static final String LOGIN = BASE_URL + "auth/login";
    public static final String LOGOUT = BASE_URL + "auth/logout";
    public static final String REGISTER = BASE_URL + "auth/register";
    public static final String EDITPROFIL = BASE_URL + "member/member";
    public static final String UBAHNAMA = BASE_URL + "member/namamember";
    public static final String GETSIMPANAN = BASE_URL + "simpanan?m_id=";
    public static final String GETSIMPANANID = BASE_URL + "simpanan?id=";
    public static final String POSTSIMPANAN = BASE_URL + "simpanan/simpanan";
    public static final String GETJUMLAHSIMPANAN = BASE_URL + "simpanan/jumlahsimpanan?m_id=";
    public static final String GETJUMLAHPINJAMAN = BASE_URL + "pinjaman/jumlahpinjaman?user_id=";
    public static final String GETJUMLAHANGSURAN = BASE_URL + "angsuran/jumlahangsuran?user_id=";
    public static final String GETPINJAMAN = BASE_URL + "pinjaman/pinjaman?user_id=";
    public static final String GETPINJAMANID = BASE_URL + "pinjaman/pinjaman?id=";
    public static final String POSTPINJAMAN = BASE_URL + "pinjaman/pinjaman";
    public static final String GETTOTALPINJAMAN = BASE_URL + "pinjaman/totaldata?user_id=";
    public static final String GETANGSURAN = BASE_URL + "angsuran/angsuran?member=";
    public static final String GETANGSURANID = BASE_URL + "angsuran/angsuran?id_a=";
    public static final String POSTANGSURAN = BASE_URL + "angsuran/angsuran";
    public static final String GETTOTALANGSURAN = BASE_URL + "angsuran/totaldata?member=";
    public static final String GETTOTALSIMPANAN = BASE_URL + "simpanan/totaldata?m_id=";
}
