package com.eka.koperasisimpanpinjam.fragment;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.android.volley.AuthFailureError;
import com.android.volley.ClientError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.eka.koperasisimpanpinjam.R;
import com.eka.koperasisimpanpinjam.activity.LoginActivity;
import com.eka.koperasisimpanpinjam.activity.MainActivity;
import com.eka.koperasisimpanpinjam.activity.MenuUtamaActivity;
import com.eka.koperasisimpanpinjam.network.adapter.SimpananAdapter;
import com.eka.koperasisimpanpinjam.network.api.URLServer;
import com.eka.koperasisimpanpinjam.network.model.DataSimpanan;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class HomeFragment extends Fragment {

    private View view;
    private TextView nama, txt_total_simpan, txt_total_pinjam, txt_totalangsuran, total_simp;
    private SharedPreferences preferences;
    private StringRequest getSimpanan, getjmlSimpanan, getDataJumlahpinjaman, getDatapinjaman, getTotal;
    private ArrayList<DataSimpanan> dataSimpanans;
    private SwipeRefreshLayout sw_data;
    public static RecyclerView rc_data;
    private SearchView search_data;
    private RecyclerView.LayoutManager layoutManager;
    private SimpananAdapter adapter;
    int m_id;

    public HomeFragment() {

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_home, container, false);
        init();
        return view;
    }

    private void init() {
        preferences = requireContext().getSharedPreferences("user", Context.MODE_PRIVATE);
        nama = view.findViewById(R.id.teks_nama);
        sw_data = view.findViewById(R.id.refresh_data);
        rc_data = view.findViewById(R.id.rc_listdata);
        search_data = view.findViewById(R.id.cari_data);
        txt_total_simpan = view.findViewById(R.id.total_simpan);
        txt_total_pinjam = view.findViewById(R.id.total_pinjam);
        txt_totalangsuran = view.findViewById(R.id.total_angsuran);
        total_simp = view.findViewById(R.id.total_simp);

        layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        rc_data.setLayoutManager(layoutManager);
        rc_data.setHasFixedSize(true);

        nama.setText(preferences.getString("nama", ""));
        m_id = preferences.getInt("id_m", 0);

        sw_data.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getDataSimpanan();
            }
        });

        search_data.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                adapter.getSearchData().filter(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });
    }

    public void getDataSimpanan() {
        dataSimpanans = new ArrayList<>();
        sw_data.setRefreshing(true);
        getSimpanan = new StringRequest(Request.Method.GET, URLServer.GETSIMPANAN + m_id, response -> {
            try {
                JSONObject object = new JSONObject(response);
                if (object.getBoolean("status")) {
                    JSONArray data = new JSONArray(object.getString("data"));
                    for (int i = 0; i < data.length(); i++) {

                        JSONObject getData = data.getJSONObject(i);

                        DataSimpanan simpanan = new DataSimpanan();
                        simpanan.setId(getData.getInt("id"));
                        simpanan.setMember_id(getData.getString("member_id"));
                        simpanan.setNo_simpanan(getData.getString("no_simpanan"));
                        simpanan.setJumlah(getData.getInt("jumlah"));
                        simpanan.setJ_simpanan(getData.getString("j_simpanan"));
                        simpanan.setCreated_at(getData.getString("created_at"));
                        simpanan.setCatatan(getData.getString("catatan"));
                        dataSimpanans.add(simpanan);

                    }
                    adapter = new SimpananAdapter(getContext(), dataSimpanans);
                    rc_data.setAdapter(adapter);
                } else {
                    setError(object.getString("message"));
                }
            } catch (JSONException e) {
                setError(e.getMessage());
            }
            sw_data.setRefreshing(false);
        }, error -> {
            Log.d("Response", "Error: " + error.getMessage());
            sw_data.setRefreshing(false);
        });
        getSimpanan.setRetryPolicy(new RetryPolicy() {
            @Override
            public int getCurrentTimeout() {
                return 2000;
            }

            @Override
            public int getCurrentRetryCount() {
                return 2000;
            }

            @Override
            public void retry(VolleyError error) throws VolleyError {
                if (Looper.myLooper() != null) {
                    Looper.prepare();
                    setError("Koneksi gagal!");
                }
            }
        });
        RequestQueue koneksi = Volley.newRequestQueue(requireContext());
        koneksi.add(getSimpanan);
    }

    public void getJumlahSimpanan() {
        sw_data.setRefreshing(true);
        getjmlSimpanan = new StringRequest(Request.Method.GET, URLServer.GETJUMLAHSIMPANAN + m_id, response -> {
            try {
                JSONObject object = new JSONObject(response);
                if (!object.equals("null")) {
                    try {
                        if (object.getBoolean("status")) {
                            JSONObject data = new JSONObject(object.getString("data"));
                            if (!data.getString("jumlah").equals("null")) {
                                String format = formatRupiah(Double.parseDouble(data.getString("jumlah")));
                                txt_total_simpan.setText(format);
                            } else {
                                txt_total_simpan.setText(formatRupiah(Double.parseDouble("0")));
                            }

                        }
                    } catch (NumberFormatException e) {
                        setError(e.getMessage());
                    }
                }
            } catch (JSONException e) {
                setError(e.getMessage());
            }
            sw_data.setRefreshing(false);
        }, error -> {
            sw_data.setRefreshing(false);
            Log.d("Response", "Error: " + error.getMessage());
        });
        getjmlSimpanan.setRetryPolicy(new RetryPolicy() {
            @Override
            public int getCurrentTimeout() {
                return 2000;
            }

            @Override
            public int getCurrentRetryCount() {
                return 2000;
            }

            @Override
            public void retry(VolleyError error) throws VolleyError {
                if (Looper.myLooper() != null) {
                    Looper.prepare();
                    setError("Koneksi gagal!");
                }
            }
        });
        RequestQueue koneksi = Volley.newRequestQueue(requireContext());
        koneksi.add(getjmlSimpanan);
    }

    public void setGetDataJumlahpinjaman() {
        sw_data.setRefreshing(true);
        getDataJumlahpinjaman = new StringRequest(Request.Method.GET,
                URLServer.GETJUMLAHPINJAMAN + m_id, response -> {
            try {
                JSONObject object = new JSONObject(response);
                if (!object.equals("null")) {
                    try {
                        if (object.getBoolean("status")) {
                            JSONObject data = new JSONObject(object.getString("data"));
                            if (!data.getString("tenor").equals("null")) {
                                int jml_tenor = Integer.parseInt(data.getString("tenor"));
                                String format = formatRupiah(Double.parseDouble(data.getString("jumlah")));
                                if (jml_tenor == 0) {
                                    txt_total_pinjam.setText(formatRupiah(Double.parseDouble("0")));
                                } else {
                                    txt_total_pinjam.setText(format);
                                }
                            } else {
                                txt_total_pinjam.setText(formatRupiah(Double.parseDouble("0")));
                            }

                        }
                    } catch (NumberFormatException e) {
                        Log.d("Response", "Error: " + e.getMessage());
                    }
                }

            } catch (JSONException e) {
                setError(e.getMessage());
            }
            sw_data.setRefreshing(false);
        }, error -> {
            sw_data.setRefreshing(false);
            Log.d("Response", "Error: " + error.getMessage());
        });
        getDataJumlahpinjaman.setRetryPolicy(new RetryPolicy() {
            @Override
            public int getCurrentTimeout() {
                return 2000;
            }

            @Override
            public int getCurrentRetryCount() {
                return 2000;
            }

            @Override
            public void retry(VolleyError error) throws VolleyError {
                if (Looper.myLooper() != null) {
                    Looper.prepare();
                    setError("Koneksi gagal!");
                }
            }
        });
        RequestQueue koneksi = Volley.newRequestQueue(requireContext());
        koneksi.add(getDataJumlahpinjaman);
    }

    public void setGetDatapinjaman() {
        sw_data.setRefreshing(true);
        getDatapinjaman = new StringRequest(Request.Method.GET,
                URLServer.GETJUMLAHANGSURAN + m_id, response -> {
            try {
                JSONObject object = new JSONObject(response);
                if (!object.equals("null")) {
                    try {
                        if (object.getBoolean("status")) {
                            JSONObject data = new JSONObject(object.getString("data"));
                            if (!data.getString("tenor").equals("null")) {
                                String format = formatRupiah(Double.parseDouble(data.getString("angsuran")));
                                int jml_tenor = Integer.parseInt(data.getString("tenor"));
                                if (jml_tenor == 0) {
                                    txt_totalangsuran.setText(formatRupiah(Double.parseDouble("0")));
                                } else {
                                    txt_totalangsuran.setText(format);
                                }
                            } else {
                                txt_totalangsuran.setText(formatRupiah(Double.parseDouble("0")));
                            }
                        }
                    } catch (NumberFormatException e) {
                        Log.d("Response", "Error: " + e.getMessage());
                    }
                }

            } catch (JSONException e) {
                setError(e.getMessage());
            }
            sw_data.setRefreshing(false);
        }, error -> {
            sw_data.setRefreshing(false);
            Log.d("Response", "Error: " + error.getMessage());
        });
        getDatapinjaman.setRetryPolicy(new RetryPolicy() {
            @Override
            public int getCurrentTimeout() {
                return 2000;
            }

            @Override
            public int getCurrentRetryCount() {
                return 2000;
            }

            @Override
            public void retry(VolleyError error) throws VolleyError {
                if (Looper.myLooper() != null) {
                    Looper.prepare();
                    setError("Koneksi gagal!");
                }
            }
        });
        RequestQueue koneksi = Volley.newRequestQueue(requireContext());
        koneksi.add(getDatapinjaman);
    }

    public void setGetTotal() {
        sw_data.setRefreshing(true);
        getTotal = new StringRequest(Request.Method.GET, URLServer.GETTOTALSIMPANAN + m_id, response -> {

            try {
                JSONObject object = new JSONObject(response);
                if (!object.equals("null")) {
                    try {
                        if (object.getBoolean("status")) {
                            total_simp.setText(object.getString("data"));
                        }
                    } catch (NumberFormatException e) {
                        e.printStackTrace();
                    }
                }

            } catch (JSONException e) {
                setError(e.getMessage());
            }
            sw_data.setRefreshing(false);
        }, error -> {
            sw_data.setRefreshing(false);
            Log.d("Response", "Error: " + error.getMessage());
        });
        getTotal.setRetryPolicy(new RetryPolicy() {
            @Override
            public int getCurrentTimeout() {
                return 2000;
            }

            @Override
            public int getCurrentRetryCount() {
                return 2000;
            }

            @Override
            public void retry(VolleyError error) throws VolleyError {
                if (Looper.myLooper() != null) {
                    Looper.prepare();
                    setError("Koneksi gagal!");
                }
            }
        });
        RequestQueue koneksi = Volley.newRequestQueue(requireContext());
        koneksi.add(getTotal);
    }

    @Override
    public void onResume() {
        super.onResume();
        getDataSimpanan();
        getJumlahSimpanan();
        setGetDataJumlahpinjaman();
        setGetDatapinjaman();
        setGetTotal();
    }

    private String formatRupiah(Double number) {
        Locale localeID = new Locale("in", "ID");
        NumberFormat formatRupiah = NumberFormat.getCurrencyInstance(localeID);
        return formatRupiah.format(number);
    }

    private void setError(String pesan) {
        new SweetAlertDialog(requireActivity(), SweetAlertDialog.ERROR_TYPE)
                .setTitleText("Oops...")
                .setContentText(pesan)
                .show();
    }
}
