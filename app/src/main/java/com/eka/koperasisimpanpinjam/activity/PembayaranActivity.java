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
import com.eka.koperasisimpanpinjam.network.api.URLServer;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.NumberFormat;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class PembayaranActivity extends AppCompatActivity {

    private ImageView btn_kembali;
    private TextView txt_id, txt_tanggal, txt_jumlah_angsuran,
            txt_no_hp, txt_no_member, txt_angsuran_ke, nama;
    private SharedPreferences preferences;
    private StringRequest getAngsuranId, getTotal;
    int member;
    String id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pembayaran);
        init();
    }

    public void init() {
        preferences = getApplicationContext().getSharedPreferences("user", MODE_PRIVATE);
        btn_kembali = findViewById(R.id.btn_kembali);
        txt_id = findViewById(R.id.id);
        txt_tanggal = findViewById(R.id.tanggal);
        txt_jumlah_angsuran = findViewById(R.id.jumlah_angsuran);
        txt_no_hp = findViewById(R.id.no_hp);
        txt_no_member = findViewById(R.id.no_member);
        txt_angsuran_ke = findViewById(R.id.angsuran_ke);
        nama = findViewById(R.id.nama);

        id = getIntent().getStringExtra("id");
        txt_no_hp.setText(preferences.getString("no_hp", ""));
        txt_no_member.setText(preferences.getString("member_id", ""));
        member = preferences.getInt("id_m", 0);
        nama.setText(preferences.getString("nama", ""));

        btn_kembali.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplication(), MenuUtamaActivity.class));
                finish();
            }
        });
    }

    public void setGetAngsuranId() {
        getAngsuranId = new StringRequest(Request.Method.GET,
                URLServer.GETPINJAMANID + id, response -> {
            try {
                JSONObject object = new JSONObject(response);
                if (object.getBoolean("status")) {
                    JSONObject data = new JSONObject(object.getString("data"));

                    txt_tanggal.setText("");
                    String format = formatRupiah(Double.parseDouble(data.getString("angsuran")));
                    txt_jumlah_angsuran.setText(format);
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
        });
        RequestQueue koneksi = Volley.newRequestQueue(this);
        koneksi.add(getAngsuranId);
    }

    public void setGetTotal() {
        getTotal = new StringRequest(Request.Method.GET, URLServer.GETTOTALANGSURAN + member, response -> {

            try {
                JSONObject object = new JSONObject(response);
                if (!object.equals("null")) {
                    try {
                        if (object.getBoolean("status")) {
                            txt_angsuran_ke.setText(object.getString("data"));
                        } else {
                            Toast.makeText(this, "Error: " + object.getString("message"), Toast.LENGTH_SHORT).show();
                        }
                    } catch (NumberFormatException e) {
                        e.printStackTrace();
                    }
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }

        }, error -> {
            error.printStackTrace();
            Toast.makeText(this, "Error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
        });
        RequestQueue koneksi = Volley.newRequestQueue(this);
        koneksi.add(getTotal);
    }

    @Override
    protected void onResume() {
        super.onResume();
        setGetAngsuranId();
        setGetTotal();
    }

    private String formatRupiah(Double number) {
        Locale localeID = new Locale("in", "ID");
        NumberFormat formatRupiah = NumberFormat.getCurrencyInstance(localeID);
        return formatRupiah.format(number);
    }
}