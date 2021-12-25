package com.eka.koperasisimpanpinjam.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.eka.koperasisimpanpinjam.R;
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

public class DetailSimpananActivity extends AppCompatActivity {

    private ImageView btn_kembali;
    private SharedPreferences preferences;
    private StringRequest getSimpanan;
    private TextView txt_tanggal, txt_id, txt_nosimpanan, txt_nohp, txt_nama, txt_jumlahsimpanan, txt_nomember,
            txt_catatan, txt_jumlahsimpanan2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_simpanan);
        init();
    }

    public void init() {
        preferences = getApplicationContext().getSharedPreferences("user", MODE_PRIVATE);
        btn_kembali = findViewById(R.id.btn_kembali);
        txt_tanggal = findViewById(R.id.tanggal);
        txt_id = findViewById(R.id.id);
        txt_nama = findViewById(R.id.nama);
        txt_nosimpanan = findViewById(R.id.no_simpanan);
        txt_jumlahsimpanan = findViewById(R.id.jumlah_simpanan);
        txt_jumlahsimpanan2 = findViewById(R.id.jumlah_simpanan2);
        txt_nohp = findViewById(R.id.no_hp);
        txt_nomember = findViewById(R.id.no_member);
        txt_catatan = findViewById(R.id.catatan);

        txt_nama.setText(preferences.getString("nama", ""));
        txt_nohp.setText(preferences.getString("no_hp", ""));
        txt_id.setText("" + getIntent().getStringExtra("id"));


        btn_kembali.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), MenuUtamaActivity.class));
                finish();
            }
        });
    }

    public void setGetSimpanan() {
        getSimpanan = new StringRequest(Request.Method.GET,
                URLServer.GETSIMPANANID + txt_id.getText().toString().trim(), response -> {
            try {
                JSONObject object = new JSONObject(response);
                if (object.getBoolean("status")) {
                    JSONObject data = new JSONObject(object.getString("data"));

                    txt_tanggal.setText(data.getString("created_at"));
                    txt_nosimpanan.setText(data.getString("no_simpanan"));
                    String format = formatRupiah(Double.parseDouble(data.getString("jumlah")));
                    txt_jumlahsimpanan.setText(format);
                    txt_jumlahsimpanan2.setText(format);
                    txt_nomember.setText(data.getString("member_id"));
                    txt_catatan.setText(data.getString("catatan"));
                } else {
                    Toast.makeText(this, "Error: " + object.getString("message"), Toast.LENGTH_SHORT).show();
                }
            } catch (JSONException e) {
                e.printStackTrace();
                Toast.makeText(this, "Terjadi masalah!", Toast.LENGTH_SHORT).show();
            }
        }, error -> {
            error.printStackTrace();
            Toast.makeText(this, "Error: " + error.getMessage(), Toast.LENGTH_LONG).show();
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                String token = preferences.getString("token", "");
                HashMap<String, String> map = new HashMap<>();
                map.put("Authorization", "Bearer " + token);
                return map;
            }

        };
        RequestQueue koneksi = Volley.newRequestQueue(this);
        koneksi.add(getSimpanan);
    }

    @Override
    protected void onResume() {
        super.onResume();
        setGetSimpanan();
    }

    private String formatRupiah(Double number) {
        Locale localeID = new Locale("in", "ID");
        NumberFormat formatRupiah = NumberFormat.getCurrencyInstance(localeID);
        return formatRupiah.format(number);
    }
}