package com.eka.koperasisimpanpinjam.fragment;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
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
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.eka.koperasisimpanpinjam.R;
import com.eka.koperasisimpanpinjam.network.adapter.ListDataSimpanAdapter;
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

public class SimpananFragment extends Fragment {
    private View view;
    private TextView nama, txt_total_simpan, total_simp;
    private StringRequest getSimpanan, getjmlSimpanan, getTotal;
    private CardView btn_tambah_simpanan;
    public static ArrayList<DataSimpanan> dataSimpanans, jumlah;
    private SwipeRefreshLayout sw_data;
    public static RecyclerView rc_data;
    private SearchView search_data;
    private RecyclerView.LayoutManager layoutManager;
    private ListDataSimpanAdapter adapter;
    int m_id;
    SharedPreferences preferences;

    public SimpananFragment() {

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_simpanan, container, false);
        init();
        return view;
    }

    private void init() {
        preferences = requireContext().getSharedPreferences("user", Context.MODE_PRIVATE);
        nama = view.findViewById(R.id.teks_nama);
        sw_data = view.findViewById(R.id.refresh_data);
        rc_data = view.findViewById(R.id.rc_listdata);
        search_data = view.findViewById(R.id.cari_data);
        txt_total_simpan = view.findViewById(R.id.total_simpanan);
        btn_tambah_simpanan = view.findViewById(R.id.btn_tambah_simpanan);
        total_simp = view.findViewById(R.id.total_simp);

        layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        rc_data.setLayoutManager(layoutManager);
        rc_data.setHasFixedSize(true);

        nama.setText(preferences.getString("nama", ""));
        m_id = preferences.getInt("id_m", 0);

        btn_tambah_simpanan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager manager = getFragmentManager();
                manager.beginTransaction()
                        .replace(R.id.frm_menu_utama, new TambahSimpananFragment())
                        .commit();
            }
        });

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
                    if (!data.equals("null")) {
                        try {
                            for (int i = 0; i < data.length(); i++) {
                                JSONObject getData = data.getJSONObject(i);

                                DataSimpanan simpanan = new DataSimpanan();
                                simpanan.setId(getData.getInt("id"));
                                simpanan.setMember_id(getData.getString("member_id"));
                                simpanan.setNo_simpanan(getData.getString("no_simpanan"));
                                simpanan.setJumlah(getData.getInt("jumlah"));
                                simpanan.setCreated_at(getData.getString("created_at"));
                                simpanan.setCatatan(getData.getString("catatan"));
                                dataSimpanans.add(simpanan);
                            }
                            adapter = new ListDataSimpanAdapter(getContext(), dataSimpanans);
                            rc_data.setAdapter(adapter);
                        } catch (NullPointerException e) {
                            e.printStackTrace();
                        }
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            sw_data.setRefreshing(false);
        }, error -> {
            error.printStackTrace();
            sw_data.setRefreshing(false);
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                String token = preferences.getString("token", "");
                HashMap<String, String> map = new HashMap<>();
                map.put("Authorization", "Bearer " + token);
                return map;
            }
        };
        RequestQueue koneksi = Volley.newRequestQueue(requireContext());
        koneksi.add(getSimpanan);
    }

    public void getJumlahSimpanan() {
        jumlah = new ArrayList<>();

        getjmlSimpanan = new StringRequest(Request.Method.GET, URLServer.GETJUMLAHSIMPANAN + m_id, response -> {
            try {
                JSONObject object = new JSONObject(response);
                if (!object.equals("null")) {
                    try {
                        if (object.getBoolean("status")) {
                            JSONObject data = new JSONObject(object.getString("data"));
                            if (!data.equals("null")) {
                                try {
                                    if (!data.getString("jumlah").equals("null")) {
                                        String format = formatRupiah(Double.parseDouble(data.getString("jumlah")));
                                        txt_total_simpan.setText(format);
                                    } else {
                                        txt_total_simpan.setText(formatRupiah(Double.parseDouble("0")));
                                    }
                                } catch (NullPointerException e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                    } catch (NumberFormatException e) {
                        e.printStackTrace();
                    }
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }, Throwable::printStackTrace) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                String token = preferences.getString("token", "");
                HashMap<String, String> map = new HashMap<>();
                map.put("Authorization", "Bearer " + token);
                return map;
            }
        };
        RequestQueue koneksi = Volley.newRequestQueue(requireContext());
        koneksi.add(getjmlSimpanan);
    }

    public void setGetTotal() {
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
                e.printStackTrace();
            }

        }, Throwable::printStackTrace) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                String token = preferences.getString("token", "");
                HashMap<String, String> map = new HashMap<>();
                map.put("Authorization", "Bearer " + token);
                return map;
            }
        };
        RequestQueue koneksi = Volley.newRequestQueue(requireContext());
        koneksi.add(getTotal);
    }

    private String formatRupiah(Double number) {
        Locale localeID = new Locale("in", "ID");
        NumberFormat formatRupiah = NumberFormat.getCurrencyInstance(localeID);
        return formatRupiah.format(number);
    }

    @Override
    public void onResume() {
        super.onResume();
        getDataSimpanan();
        getJumlahSimpanan();
        setGetTotal();
    }
}
