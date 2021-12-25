package com.eka.koperasisimpanpinjam.fragment;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
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
import com.eka.koperasisimpanpinjam.network.adapter.PinjamanAdapter;
import com.eka.koperasisimpanpinjam.network.api.URLServer;
import com.eka.koperasisimpanpinjam.network.model.DataPinjaman;
import com.eka.koperasisimpanpinjam.network.model.DataSimpanan;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class TambahPinjamanFragment extends Fragment {
    private View view;
    private TextView txt_id, gettenor;
    private EditText edt_nomember, edt_jmlpinjaman,
            edt_biayaadmin, edt_bunga, edt_keterangan;
    private CardView btn_simpan;
    private Spinner sp_tenor;
    SharedPreferences preferences;
    private ProgressDialog dialog;
    private StringRequest kirimData;
    private ArrayList<DataPinjaman> dataPinjamen;
    int biayaadmin = 2000;
    double persen = 0.1;

    public TambahPinjamanFragment() {

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_tambahpinjaman, container, false);
        init();
        return view;
    }

    private void init() {
        preferences = requireContext().getSharedPreferences("user", Context.MODE_PRIVATE);
        edt_biayaadmin = view.findViewById(R.id.biaya_admin);
        edt_bunga = view.findViewById(R.id.bunga);
        edt_jmlpinjaman = view.findViewById(R.id.jml_pinjaman);
        edt_nomember = view.findViewById(R.id.no_member);
        edt_keterangan = view.findViewById(R.id.keterangan);
        txt_id = view.findViewById(R.id.id);
        sp_tenor = view.findViewById(R.id.tenor);
        gettenor = view.findViewById(R.id.gettenor);
        btn_simpan = view.findViewById(R.id.btn_simpan);

        dialog = new ProgressDialog(getContext());
        dialog.setCancelable(false);

        edt_nomember.setText(preferences.getString("member_id", ""));
        String format = formatRupiah(Double.parseDouble(String.valueOf(biayaadmin)));
        txt_id.setText(String.valueOf(preferences.getInt("id_m", 0)));
        edt_biayaadmin.setText(format);
        edt_bunga.setText(String.valueOf(persen));

        sp_tenor.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                gettenor.setText(sp_tenor.getSelectedItem().toString());
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        btn_simpan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (validasi()) {
                    setKirimData();
                }
            }
        });
    }

    public void setKirimData() {
        dataPinjamen = new ArrayList<>();
        dialog.setMessage("Loading...");
        dialog.show();

        kirimData = new StringRequest(Request.Method.POST, URLServer.POSTPINJAMAN, response -> {
            try {
                JSONObject object = new JSONObject(response);
                if (object.equals("null")) {
                    try {
                        if (object.getBoolean("status")) {
                            JSONObject data = object.getJSONObject("data");

                            DataPinjaman pinjaman = new DataPinjaman();
                            pinjaman.setUser_id(data.getInt("user_id"));
                            pinjaman.setJumlah(data.getInt("jumlah"));
                            pinjaman.setBunga(data.getDouble("bunga"));
                            pinjaman.setTenor(data.getInt("tenor"));
                            pinjaman.setBiaya_admin(data.getInt("biaya_admin"));
                            pinjaman.setKeterangan(data.getString("keterangan"));

                            PinjamFragment.dataPinjamen.add(0, pinjaman);
                            PinjamFragment.rc_data.getAdapter().notifyItemInserted(0);
                            PinjamFragment.rc_data.getAdapter().notifyDataSetChanged();

                        }
                    } catch (NullPointerException e) {
                        e.printStackTrace();
                    }
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
            dialog.dismiss();
            Toast.makeText(getContext(), "Data Tersimpan", Toast.LENGTH_SHORT).show();
            FragmentManager manager = getFragmentManager();
            assert manager != null;
            manager.beginTransaction().replace(R.id.frm_menu_utama, new PinjamFragment())
                    .commit();
        }, error -> {
            dialog.dismiss();
            error.printStackTrace();
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                String token = preferences.getString("token", "");
                HashMap<String, String> map = new HashMap<>();
                map.put("Authorization", "Bearer " + token);
                return map;
            }

            @Nullable
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                String keterangan = edt_keterangan.getText().toString().trim();
                String jumlah_pinjaman = edt_jmlpinjaman.getText().toString().trim();
                String m_id = txt_id.getText().toString().trim();
                String tenor = gettenor.getText().toString().trim();
                HashMap<String, String> map = new HashMap<>();
                map.put("user_id", m_id);
                map.put("jumlah", jumlah_pinjaman);
                map.put("tenor", tenor);
                map.put("keterangan", keterangan);
                return map;
            }
        };
        RequestQueue koneksi = Volley.newRequestQueue(requireContext());
        koneksi.add(kirimData);
    }

    private String formatRupiah(Double number) {
        Locale localeID = new Locale("in", "ID");
        NumberFormat formatRupiah = NumberFormat.getCurrencyInstance(localeID);
        return formatRupiah.format(number);
    }

    private boolean validasi() {
        String jumlah = edt_jmlpinjaman.getText().toString().trim();

        if (jumlah.isEmpty()) {
            edt_jmlpinjaman.setError("Kolom jumlah tidak boleh kosong!");
        }
        return true;
    }
}
