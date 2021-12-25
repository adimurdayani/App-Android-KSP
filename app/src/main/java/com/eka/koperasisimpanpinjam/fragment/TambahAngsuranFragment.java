package com.eka.koperasisimpanpinjam.fragment;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.eka.koperasisimpanpinjam.R;
import com.eka.koperasisimpanpinjam.activity.DetailAngsuranActivity;
import com.eka.koperasisimpanpinjam.activity.MenuUtamaActivity;
import com.eka.koperasisimpanpinjam.activity.PembayaranActivity;
import com.eka.koperasisimpanpinjam.network.api.URLServer;
import com.eka.koperasisimpanpinjam.network.model.DataAngsuran;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class TambahAngsuranFragment extends Fragment {
    private View view;
    private CardView btn_tambah_pinjaman, btn_bayar;
    private EditText no_member, jml_angsuran, keterangan;
    private SharedPreferences preferences;
    private TextView txt_id;
    private ImageView btn_kembali;
    private ProgressDialog dialog;
    private ArrayList<DataAngsuran> dataAngsuranArrayList;
    private StringRequest getTotalAngsuran, kirimData, getTotal;
    int m_id, jumlahangsuran, id;
    private SharedPreferences.Editor editor;

    public TambahAngsuranFragment() {

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_tambahangsuran, container, false);
        init();
        return view;
    }

    public void init() {
        preferences = requireContext().getSharedPreferences("user", Context.MODE_PRIVATE);
        btn_tambah_pinjaman = view.findViewById(R.id.btn_pinjam);
        btn_kembali = view.findViewById(R.id.btn_kembali);
        no_member = view.findViewById(R.id.no_member);
        jml_angsuran = view.findViewById(R.id.jml_angsuran);
        keterangan = view.findViewById(R.id.keterangan);
        btn_bayar = view.findViewById(R.id.btn_bayar);
        txt_id = view.findViewById(R.id.id);

        dialog = new ProgressDialog(getContext());
        dialog.setCancelable(false);

        no_member.setText(preferences.getString("member_id", ""));
        m_id = preferences.getInt("id_m", 0);
        txt_id.setText(String.valueOf(preferences.getInt("id_m", 0)));


        btn_bayar.setOnClickListener(v -> {
            if (validasi()) {
                setKirimData();
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

        btn_kembali.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager manager = getFragmentManager();
                assert manager != null;
                manager.beginTransaction()
                        .replace(R.id.frm_menu_utama, new AngsuranFragment())
                        .commit();
            }
        });
    }

    public void setKirimData() {

        dataAngsuranArrayList = new ArrayList<>();
        dialog.setMessage("Loading...");
        dialog.show();

        kirimData = new StringRequest(Request.Method.POST, URLServer.POSTANGSURAN, response -> {
            try {
                JSONObject object = new JSONObject(response);
                if (object.equals("null")) {
                    try {
                        if (object.getBoolean("status")) {
                            JSONObject data = object.getJSONObject("data");

                            DataAngsuran angsuran = new DataAngsuran();
                            angsuran.setId_a(data.getInt("id_a"));
                            angsuran.setMember(data.getInt("member"));
                            angsuran.setJumlah(data.getInt("jumlah"));
                            angsuran.setKet(data.getString("ket"));

                            editor = preferences.edit();
                            editor.putInt("id_a", object.getInt("id_a"));
                            editor.apply();

                            AngsuranFragment.dataAngsurans.add(0, angsuran);
                            AngsuranFragment.rc_data.getAdapter().notifyItemInserted(0);
                            AngsuranFragment.rc_data.getAdapter().notifyDataSetChanged();

                        }
                    } catch (NullPointerException e) {
                        e.printStackTrace();
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            dialog.dismiss();
            Toast.makeText(getContext(), "Data tersimpan", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(getContext(), PembayaranActivity.class);
            intent.putExtra("id_a", String.valueOf(id));
            startActivity(intent);
            ((MenuUtamaActivity) getContext()).finish();
        }, error -> {
            error.printStackTrace();
            dialog.dismiss();
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                String token = preferences.getString("token", "");
                Map<String, String> map = new HashMap<>();
                map.put("Authorization", "Bearer " + token);
                return map;
            }

            @Nullable
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                String ket = keterangan.getText().toString().trim();
                String m_id = txt_id.getText().toString().trim();
                Map<String, String> map = new HashMap<>();
                map.put("id", String.valueOf(id));
                map.put("member", m_id);
                map.put("jumlah", String.valueOf(jumlahangsuran));
                map.put("ket", ket);
                return map;
            }
        };
        RequestQueue koneksi = Volley.newRequestQueue(requireContext());
        koneksi.add(kirimData);
    }

    public void setGetTotalAngsuran() {
        getTotalAngsuran = new StringRequest(Request.Method.GET,
                URLServer.GETJUMLAHANGSURAN + m_id, response -> {

            try {
                JSONObject object = new JSONObject(response);
                if (!object.equals("null")) {
                    try {
                        if (object.getBoolean("status")) {
                            JSONObject data = new JSONObject(object.getString("data"));
                            String format = formatRupiah(Double.parseDouble(data.getString("angsuran")));
                            jml_angsuran.setText(format);
                            jumlahangsuran = data.getInt("angsuran");
                        } else {
                            Toast.makeText(getContext(), "Error: " + object.getString("message"), Toast.LENGTH_SHORT).show();
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
        koneksi.add(getTotalAngsuran);
    }

    public void setGetTotal() {
        getTotal = new StringRequest(Request.Method.GET, URLServer.GETTOTALANGSURAN + m_id, response -> {

            try {
                JSONObject object = new JSONObject(response);
                if (!object.equals("null")) {
                    try {
                        if (object.getBoolean("status")) {
                            id = object.getInt("data");
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
        setGetTotal();
    }

    private boolean validasi() {
        String jumlah = jml_angsuran.getText().toString().trim();

        if (jumlah.isEmpty()) {
            jml_angsuran.setError("Kolom jumlah tidak boleh kosong!");
        }
        return true;
    }

    private String formatRupiah(Double number) {
        Locale localeID = new Locale("in", "ID");
        NumberFormat formatRupiah = NumberFormat.getCurrencyInstance(localeID);
        return formatRupiah.format(number);
    }
}
