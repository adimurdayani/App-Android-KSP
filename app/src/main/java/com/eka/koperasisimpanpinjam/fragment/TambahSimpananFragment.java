package com.eka.koperasisimpanpinjam.fragment;

import android.app.ProgressDialog;
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
import com.eka.koperasisimpanpinjam.activity.MenuUtamaActivity;
import com.eka.koperasisimpanpinjam.network.adapter.SimpananAdapter;
import com.eka.koperasisimpanpinjam.network.api.URLServer;
import com.eka.koperasisimpanpinjam.network.model.DataMember;
import com.eka.koperasisimpanpinjam.network.model.DataSimpanan;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static android.content.Context.MODE_PRIVATE;

public class TambahSimpananFragment extends Fragment {

    private View view;
    private EditText edt_nomember, edt_catatan, edt_jumlah;
    private Spinner jenis_simpanan;
    private ImageView btn_kembali;
    private TextView getjenissimpanan, txt_id;
    private SharedPreferences preferences;
    private CardView btn_simpan;
    private ProgressDialog dialog;
    private StringRequest kirimData;
    private ArrayList<DataSimpanan> dataSimpanans;

    public TambahSimpananFragment() {

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_tambahsimpanan, container, false);
        init();
        return view;

    }

    public void init() {
        preferences = getContext().getSharedPreferences("user", MODE_PRIVATE);

        edt_nomember = view.findViewById(R.id.no_member);
        edt_jumlah = view.findViewById(R.id.jml_pinjaman);
        edt_catatan = view.findViewById(R.id.catatan);
        btn_simpan = view.findViewById(R.id.btn_simpan);
        jenis_simpanan = view.findViewById(R.id.jenis_simpanan);
        getjenissimpanan = view.findViewById(R.id.getjenissimpanan);
        txt_id = view.findViewById(R.id.m_id);
        btn_kembali = view.findViewById(R.id.btn_kembali);

        dialog = new ProgressDialog(getContext());
        dialog.setCancelable(false);

        edt_nomember.setText(preferences.getString("member_id", ""));
        txt_id.setText(String.valueOf(preferences.getInt("id_m", 0)));

        btn_kembali.setOnClickListener(v -> {
            FragmentManager manager = getFragmentManager();
            assert manager != null;
            manager.beginTransaction().replace(R.id.frm_menu_utama, new SimpananFragment())
                    .commit();
        });

        btn_simpan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (validasi()) {
                    simpanData();

                }

            }
        });

        jenis_simpanan.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                getjenissimpanan.setText(jenis_simpanan.getSelectedItem().toString());
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    private void simpanData() {
        dataSimpanans = new ArrayList<>();
        dialog.setMessage("Loading...");
        dialog.show();

        kirimData = new StringRequest(Request.Method.POST, URLServer.POSTSIMPANAN, response -> {
            try {
                JSONObject object = new JSONObject(response);
                if (object.equals("null")) {
                    try {
                        if (object.getBoolean("status")) {
                            JSONObject data = object.getJSONObject("data");
                            DataSimpanan simpanan = new DataSimpanan();
                            simpanan.setM_id(data.getInt("m_id"));
                            simpanan.setNo_simpanan(data.getString("no_simpanan"));
                            simpanan.setJumlah(data.getInt("jumlah"));
                            simpanan.setJ_simpanan(data.getString("j_simpanan"));
                            simpanan.setCatatan(data.getString("catatan"));

                            SimpananFragment.dataSimpanans.add(0, simpanan);
                            SimpananFragment.rc_data.getAdapter().notifyItemInserted(0);
                            SimpananFragment.rc_data.getAdapter().notifyDataSetChanged();

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
            manager.beginTransaction().replace(R.id.frm_menu_utama, new SimpananFragment())
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
                String jumlah = edt_jumlah.getText().toString().trim();
                String catatan = edt_catatan.getText().toString().trim();
                String jenis_simp = getjenissimpanan.getText().toString().trim();
                String m_id = txt_id.getText().toString().trim();
                HashMap<String, String> map = new HashMap<>();
                map.put("m_id", m_id);
                map.put("jumlah", jumlah);
                map.put("j_simpanan", jenis_simp);
                map.put("catatan", catatan);
                return map;
            }
        };

        RequestQueue koneksi = Volley.newRequestQueue(requireContext());
        koneksi.add(kirimData);
    }

    private boolean validasi() {
        String jumlah = edt_jumlah.getText().toString().trim();

        if (jumlah.isEmpty()) {
            edt_jumlah.setError("Kolom jumlah tidak boleh kosong!");
        }
        return true;
    }
}
