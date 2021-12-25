package com.eka.koperasisimpanpinjam.fragment;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Patterns;
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
import com.eka.koperasisimpanpinjam.network.api.URLServer;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

@SuppressLint("SetTextI18n")
public class UbahNamaFragment extends Fragment {
    private View view;
    private EditText edt_nama, edt_email, edt_username;
    private CardView btn_simpan;
    private ImageView btn_kembali;
    private ProgressDialog dialog;
    private TextView id_m;
    private StringRequest ubahNama;
    private SharedPreferences preferences;
    private SharedPreferences.Editor editor;

    public UbahNamaFragment() {

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_ubahnama, container, false);
        init();
        return view;
    }

    private void init() {
        preferences = getContext().getSharedPreferences("user", Context.MODE_PRIVATE);
        btn_simpan = view.findViewById(R.id.btn_simpan);
        edt_email = view.findViewById(R.id.edt_email);
        edt_nama = view.findViewById(R.id.edt_nama);
        edt_username = view.findViewById(R.id.edt_username);
        btn_kembali = view.findViewById(R.id.btn_kembali);
        id_m = view.findViewById(R.id.id_m);

        id_m.setText("" + preferences.getInt("id_m", 0));
        edt_email.setText(preferences.getString("email", ""));
        edt_nama.setText(preferences.getString("nama", ""));
        edt_username.setText(preferences.getString("username", ""));

        dialog = new ProgressDialog(getContext());
        dialog.setCancelable(false);

        btn_kembali.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager kembali = getFragmentManager();
                kembali.beginTransaction()
                        .replace(R.id.frm_menu_utama, new LainnyaFragment())
                        .commit();
            }
        });

        btn_simpan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (validasi()) {
                    ubahNama();
                }
            }
        });
    }

    private void ubahNama() {
        dialog.setMessage("Loading...");
        dialog.show();

        ubahNama = new StringRequest(Request.Method.POST, URLServer.UBAHNAMA, response -> {

            try {
                JSONObject object = new JSONObject(response);
                if (object.getBoolean("status")) {
                    JSONObject data = object.getJSONObject("data");
                    preferences = requireContext().getSharedPreferences("user", Context.MODE_PRIVATE);
                    editor = preferences.edit();
                    editor.putString("nama", data.getString("nama"));
                    editor.putString("username", data.getString("username"));
                    editor.putString("email", data.getString("email"));
                    editor.apply();

                    Toast.makeText(getContext(), "Data Tersimpan", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getContext(), "Message: " + object.getString("message"), Toast.LENGTH_SHORT).show();
                }
            } catch (JSONException e) {
                e.printStackTrace();
                Toast.makeText(getContext(), "Message: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
            dialog.dismiss();
        }, error -> {
            dialog.dismiss();
            error.printStackTrace();
            Toast.makeText(getContext(), "Data tidak di temukan", Toast.LENGTH_SHORT).show();
        }) {
            @Nullable
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                String nama = edt_nama.getText().toString().trim();
                String email = edt_email.getText().toString().trim();
                String username = edt_username.getText().toString().trim();
                String id = id_m.getText().toString().trim();
                HashMap<String, String> map = new HashMap<>();
                map.put("id_m", id);
                map.put("nama", nama);
                map.put("username", username);
                map.put("email", email);
                return map;
            }
        };

        RequestQueue koneksi = Volley.newRequestQueue(requireContext());
        koneksi.add(ubahNama);
    }

    private boolean validasi() {
        String nama = edt_nama.getText().toString().trim();
        String email = edt_email.getText().toString().trim();
        String username = edt_username.getText().toString().trim();

        if (nama.isEmpty()) {
            edt_nama.setError("Kolom nama tidak boleh kosong!");
        }

        if (email.isEmpty()) {
            edt_email.setError("Kolom email tidak boleh kosong!");
        } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            edt_email.setError("Email yang anda input salah. Contoh: example@example.com!");
        }

        if (username.isEmpty()) {
            edt_username.setError("Kolom username tidak boleh kosong!");
        }

        return true;
    }
}
