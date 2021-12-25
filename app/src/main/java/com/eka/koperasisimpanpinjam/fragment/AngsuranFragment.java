package com.eka.koperasisimpanpinjam.fragment;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
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
import com.eka.koperasisimpanpinjam.network.adapter.AngsuranAdapter;
import com.eka.koperasisimpanpinjam.network.adapter.SimpananAdapter;
import com.eka.koperasisimpanpinjam.network.api.URLServer;
import com.eka.koperasisimpanpinjam.network.model.DataAngsuran;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class AngsuranFragment extends Fragment {
    private View view;
    private TextView nama, txt_jumlahangsuran, txt_angsuran, total_angsur;
    SharedPreferences preferences;
    private StringRequest getTotalAngsuran, getAllAngsuran, getTotal;
    private AngsuranAdapter adapter;
    public static ArrayList<DataAngsuran> dataAngsurans;
    private SwipeRefreshLayout sw_data;
    public static RecyclerView rc_data;
    private SearchView search_data;
    private RecyclerView.LayoutManager layoutManager;
    int m_id;

    public AngsuranFragment() {

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_angsuran, container, false);
        init();
        return view;
    }

    private void init() {
        preferences = requireContext().getSharedPreferences("user", Context.MODE_PRIVATE);
        nama = view.findViewById(R.id.teks_nama);
        txt_angsuran = view.findViewById(R.id.jmlh_angsuran);
        txt_jumlahangsuran = view.findViewById(R.id.total_angsuran);
        sw_data = view.findViewById(R.id.refresh_data);
        rc_data = view.findViewById(R.id.rc_listdata);
        search_data = view.findViewById(R.id.cari_data);
        total_angsur = view.findViewById(R.id.total_angsur);

        layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        rc_data.setLayoutManager(layoutManager);
        rc_data.setHasFixedSize(true);

        nama.setText(preferences.getString("nama", ""));
        m_id = preferences.getInt("id_m", 0);

        sw_data.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                setGetAllAngsuran();
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

    @SuppressLint("SetTextI18n")
    public void setGetTotalAngsuran() {
        sw_data.setRefreshing(true);
        getTotalAngsuran = new StringRequest(Request.Method.GET,
                URLServer.GETJUMLAHANGSURAN + m_id, response -> {
            try {
                JSONObject object = new JSONObject(response);
                if (!object.equals("null")) {
                    try {
                        if (object.getBoolean("status")) {
                            JSONObject data = new JSONObject(object.getString("data"));
                            if (!data.getString("tenor").equals("null")) {
                                txt_angsuran.setText(data.getString("tenor") + " Bulan");
                                String format = formatRupiah(Double.parseDouble(data.getString("angsuran")));
                                int jml_tenor = Integer.parseInt(data.getString("tenor"));
                                if (jml_tenor == 0) {
                                    txt_jumlahangsuran.setText(formatRupiah(Double.parseDouble("0")));
                                } else {
                                    txt_jumlahangsuran.setText(format);
                                }
                            } else {
                                txt_angsuran.setText("0 Bulan");
                                txt_jumlahangsuran.setText(formatRupiah(Double.parseDouble("0")));
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
        getTotalAngsuran.setRetryPolicy(new RetryPolicy() {
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
        koneksi.add(getTotalAngsuran);
    }

    public void setGetAllAngsuran() {
        dataAngsurans = new ArrayList<>();
        sw_data.setRefreshing(true);

        getAllAngsuran = new StringRequest(Request.Method.GET, URLServer.GETANGSURAN + m_id, response -> {

            try {
                JSONObject object = new JSONObject(response);
                if (!object.equals("null")) {
                    try {
                        if (object.getBoolean("status")) {
                            JSONArray data = new JSONArray(object.getString("data"));
                            if (!data.equals("null")) {
                                try {
                                    for (int i = 0; i < data.length(); i++) {
                                        JSONObject getData = data.getJSONObject(i);

                                        DataAngsuran angsuran = new DataAngsuran();
                                        angsuran.setId_a(getData.getInt("id_a"));
                                        angsuran.setJumlah(getData.getInt("jumlah"));
                                        angsuran.setMember_id(getData.getString("member_id"));
                                        angsuran.setCreated_at(getData.getString("created_at"));
                                        dataAngsurans.add(angsuran);
                                    }
                                    adapter = new AngsuranAdapter(getContext(), dataAngsurans);
                                    rc_data.setAdapter(adapter);
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
        koneksi.add(getAllAngsuran);
    }

    public void setGetTotal() {
        getTotal = new StringRequest(Request.Method.GET, URLServer.GETTOTALANGSURAN + m_id, response -> {

            try {
                JSONObject object = new JSONObject(response);
                if (!object.equals("null")) {
                    try {
                        if (object.getBoolean("status")) {
                            total_angsur.setText(object.getString("data"));
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

    @Override
    public void onResume() {
        super.onResume();
        setGetTotalAngsuran();
        setGetAllAngsuran();
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
