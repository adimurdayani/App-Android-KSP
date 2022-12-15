package com.eka.koperasisimpanpinjam.fragment;

import android.content.Context;
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
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.eka.koperasisimpanpinjam.R;
import com.eka.koperasisimpanpinjam.network.adapter.PinjamanAdapter;
import com.eka.koperasisimpanpinjam.network.adapter.SimpananAdapter;
import com.eka.koperasisimpanpinjam.network.api.URLServer;
import com.eka.koperasisimpanpinjam.network.model.DataPinjaman;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class PinjamFragment extends Fragment {
    private View view;
    SharedPreferences preferences;
    private TextView nama, txt_totalpinjaman, total_pinj;
    private CardView btn_tambah_pinjaman;
    private StringRequest getjumlahpinjaman, getDataPinjaman, getTotal;
    public static ArrayList<DataPinjaman> dataPinjamen, jumlah;
    private SwipeRefreshLayout sw_data;
    public static RecyclerView rc_data;
    private SearchView search_data;
    private RecyclerView.LayoutManager layoutManager;
    private PinjamanAdapter adapter;
    private int id;

    public PinjamFragment() {

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_pinjam, container, false);
        init();
        return view;
    }

    private void init() {
        preferences = requireContext().getSharedPreferences("user", Context.MODE_PRIVATE);
        nama = view.findViewById(R.id.teks_nama);
        txt_totalpinjaman = view.findViewById(R.id.total_pinjam);
        btn_tambah_pinjaman = view.findViewById(R.id.btn_tambah_pinjaman);
        sw_data = view.findViewById(R.id.refresh_data);
        rc_data = view.findViewById(R.id.rc_listdata);
        search_data = view.findViewById(R.id.cari_data);
        total_pinj = view.findViewById(R.id.total_pinj);

        layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        rc_data.setLayoutManager(layoutManager);
        rc_data.setHasFixedSize(true);

        nama.setText(preferences.getString("nama", ""));
        id = preferences.getInt("id_m", 0);


        sw_data.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                setGetDataPinjaman();
            }
        });

        btn_tambah_pinjaman.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager manager = getFragmentManager();
                assert manager != null;
                manager.beginTransaction()
                        .replace(R.id.frm_menu_utama, new TambahPinjamanFragment())
                        .commit();
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

    public void setGetDataPinjaman() {
        dataPinjamen = new ArrayList<>();
        sw_data.setRefreshing(true);

        getDataPinjaman = new StringRequest(Request.Method.GET, URLServer.GETPINJAMAN + id, response -> {
            try {
                JSONObject object = new JSONObject(response);
                if (object.getBoolean("status")) {
                    JSONArray data = new JSONArray(object.getString("data"));
                    for (int i = 0; i < data.length(); i++) {
                        JSONObject getData = data.getJSONObject(i);

                        DataPinjaman pinjaman = new DataPinjaman();
                        pinjaman.setId(getData.getInt("id"));
                        pinjaman.setNo_pinjaman(getData.getString("no_pinjaman"));
                        pinjaman.setMember_id(getData.getString("member_id"));
                        pinjaman.setJumlah(getData.getInt("jumlah"));
                        pinjaman.setTenor(getData.getInt("tenor"));
                        pinjaman.setKeterangan(getData.getString("keterangan"));
                        pinjaman.setCreated_at(getData.getString("created_at"));
                        dataPinjamen.add(pinjaman);
                    }
                    adapter = new PinjamanAdapter(getContext(), dataPinjamen);
                    rc_data.setAdapter(adapter);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            sw_data.setRefreshing(false);
        }, error -> {
            error.printStackTrace();
            sw_data.setRefreshing(false);
        });
        getDataPinjaman.setRetryPolicy(new RetryPolicy() {
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
        koneksi.add(getDataPinjaman);
    }

    public void setGetjumlahpinjaman() {
        sw_data.setRefreshing(true);
        getjumlahpinjaman = new StringRequest(Request.Method.GET,
                URLServer.GETJUMLAHPINJAMAN + id, response -> {
            try {
                JSONObject object = new JSONObject(response);
                if (!object.equals("null")) {
                    try {
                        if (object.getBoolean("status")) {
                            JSONObject data = new JSONObject(object.getString("data"));
                            if (!data.getString("tenor").equals("null")) {
                                int jml_tenor = Integer.parseInt(data.getString("tenor"));
                                String format = formatRupiah(Double.parseDouble(data.getString("jumlah")));
                                if (jml_tenor <= 0) {
                                    btn_tambah_pinjaman.setVisibility(View.GONE);
                                    txt_totalpinjaman.setText(formatRupiah(Double.parseDouble("0")));
                                } else {
                                    btn_tambah_pinjaman.setVisibility(View.GONE);
                                    txt_totalpinjaman.setText(format);
                                }
                            }else{
                                txt_totalpinjaman.setText(formatRupiah(Double.parseDouble("0")));
                            }
                        }
                    } catch (NumberFormatException e) {
                        e.printStackTrace();
                    }
                }
                sw_data.setRefreshing(false);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }, error -> {
            sw_data.setRefreshing(false);
        });
        getjumlahpinjaman.setRetryPolicy(new RetryPolicy() {
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
        koneksi.add(getjumlahpinjaman);
    }

    public void setGetTotal() {
        sw_data.setRefreshing(true);
        getTotal = new StringRequest(Request.Method.GET,
                URLServer.GETTOTALPINJAMAN + id, response -> {
            try {
                JSONObject object = new JSONObject(response);
                if (!object.equals("null")) {
                    try {
                        if (object.getBoolean("status")) {
                            total_pinj.setText(object.getString("data"));
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
        setGetjumlahpinjaman();
        setGetDataPinjaman();
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
