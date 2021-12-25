package com.eka.koperasisimpanpinjam.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.eka.koperasisimpanpinjam.R;
import com.eka.koperasisimpanpinjam.fragment.TambahAngsuranFragment;
import com.eka.koperasisimpanpinjam.network.api.URLServer;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.NumberFormat;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

@SuppressLint("SetTextI18n")
public class DetailPinjamanActivity extends AppCompatActivity {

    private ImageView btn_kembali;
    private TextView txt_tanggal, txt_id, txt_nohp, txt_nama,
            txt_jumlahpinjaman, txt_nomember, txt_nopinjaman,
            txt_keterangan, txt_nik;
    private SharedPreferences preferences;
    private LinearLayout btn_bayar;
    private StringRequest getPinjaman;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_pinjaman);
        init();
    }

    public void init() {
        preferences = getApplicationContext().getSharedPreferences("user", MODE_PRIVATE);
        btn_kembali = findViewById(R.id.btn_kembali);
        txt_id = findViewById(R.id.id);
        txt_tanggal = findViewById(R.id.tanggal);
        txt_nama = findViewById(R.id.nama);
        txt_nohp = findViewById(R.id.no_hp);
        txt_nomember = findViewById(R.id.no_member);
        txt_nopinjaman = findViewById(R.id.no_pinjaman);
        txt_jumlahpinjaman = findViewById(R.id.jumlah_pinjaman);
        txt_keterangan = findViewById(R.id.keterangan);
        txt_nik = findViewById(R.id.nik);
        btn_bayar = findViewById(R.id.btn_bayar);

        txt_id.setText("" + getIntent().getStringExtra("id"));
        txt_nama.setText(preferences.getString("nama", ""));
        txt_nohp.setText(preferences.getString("no_hp", ""));
        txt_nik.setText(preferences.getString("nik", ""));

        btn_kembali.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        btn_bayar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getApplication(), TambahAngsuran.class);
                i.putExtra("id", txt_id.getText().toString().trim());
                Log.d("Response", "ID: " + txt_id.getText().toString().trim());
                startActivity(i);
            }
        });
    }

    public void setGetPinjaman() {
        getPinjaman = new StringRequest(Request.Method.GET,
                URLServer.GETPINJAMANID + txt_id.getText().toString().trim(), response -> {
            try {
                JSONObject object = new JSONObject(response);
                if (object.getBoolean("status")) {
                    JSONObject data = new JSONObject(object.getString("data"));
                    txt_tanggal.setText(data.getString("created_at"));
                    txt_nopinjaman.setText(data.getString("no_pinjaman"));
                    String format = formatRupiah(Double.parseDouble(data.getString("jumlah")));
                    txt_jumlahpinjaman.setText(format);
                    txt_nomember.setText(data.getString("member_id"));
                    txt_keterangan.setText(data.getString("keterangan"));
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
        koneksi.add(getPinjaman);
    }

    @Override
    protected void onResume() {
        super.onResume();
        setGetPinjaman();
    }

    private String formatRupiah(Double number) {
        Locale localeID = new Locale("in", "ID");
        NumberFormat formatRupiah = NumberFormat.getCurrencyInstance(localeID);
        return formatRupiah.format(number);
    }
}