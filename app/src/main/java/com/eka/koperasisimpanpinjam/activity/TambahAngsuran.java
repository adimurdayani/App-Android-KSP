package com.eka.koperasisimpanpinjam.activity;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.FragmentManager;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.eka.koperasisimpanpinjam.R;
import com.eka.koperasisimpanpinjam.fragment.AngsuranFragment;
import com.eka.koperasisimpanpinjam.fragment.LoginFragment;
import com.eka.koperasisimpanpinjam.fragment.TambahPinjamanFragment;
import com.eka.koperasisimpanpinjam.network.api.URLServer;
import com.eka.koperasisimpanpinjam.network.model.DataAngsuran;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class TambahAngsuran extends AppCompatActivity {
    private CardView btn_bayar;
    private EditText no_member, jml_angsuran, keterangan;
    private SharedPreferences preferences;
    private TextView txt_id;
    private ImageView btn_kembali;
    private StringRequest getTotalAngsuran, kirimData, getTotal;
    int m_id, jumlahangsuran, id;
    private String id_pinjam;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tambah_angsuran);
        init();
    }

    public void init() {
        preferences = getApplication().getSharedPreferences("user", Context.MODE_PRIVATE);
        btn_kembali = findViewById(R.id.btn_kembali);
        no_member = findViewById(R.id.no_member);
        jml_angsuran = findViewById(R.id.jml_angsuran);
        keterangan = findViewById(R.id.keterangan);
        btn_bayar = findViewById(R.id.btn_bayar);
        txt_id = findViewById(R.id.id);

        no_member.setText(preferences.getString("member_id", ""));
        m_id = preferences.getInt("id_m", 0);
        id_pinjam = getIntent().getStringExtra("id");

        btn_bayar.setOnClickListener(v -> {
            if (validasi()) {
                setKirimData();
            }
        });

        btn_kembali.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    public void setKirimData() {
        SweetAlertDialog dialog = new SweetAlertDialog(this, SweetAlertDialog.PROGRESS_TYPE);
        dialog.setTitle("Loading");
        dialog.setContentText("Proses pembuatan akun sedang berjalan!");
        dialog.setCancelable(false);
        dialog.show();

        kirimData = new StringRequest(Request.Method.POST, URLServer.POSTANGSURAN, response -> {
            try {
                JSONObject object = new JSONObject(response);
                if (object.getBoolean("status")) {
                    setSukses("Pembayaran angsuran sukses, silahkan melakukan transaksi!");
                }
            } catch (JSONException e) {
                setError(e.getMessage());
            }
            dialog.dismissWithAnimation();
        }, error -> {
            error.printStackTrace();
            dialog.dismissWithAnimation();
        }) {
            @Nullable
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                String ket = keterangan.getText().toString().trim();
                Map<String, String> map = new HashMap<>();
                map.put("id", id_pinjam);
                map.put("member", String.valueOf(m_id));
                map.put("jumlah", String.valueOf(jumlahangsuran));
                map.put("ket", ket);
                return map;
            }
        };
        RequestQueue koneksi = Volley.newRequestQueue(this);
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
                            setError(object.getString("message"));
                        }
                    } catch (NumberFormatException e) {
                        setError(e.getMessage());
                    }
                }

            } catch (JSONException e) {
                setError(e.getMessage());
            }

        }, Throwable::printStackTrace);
        RequestQueue koneksi = Volley.newRequestQueue(this);
        koneksi.add(getTotalAngsuran);
    }

    @Override
    public void onResume() {
        super.onResume();
        setGetTotalAngsuran();
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

    private void setError(String pesan) {
        new SweetAlertDialog(this, SweetAlertDialog.ERROR_TYPE)
                .setTitleText("Oops...")
                .setContentText(pesan)
                .show();
    }

    private void setSukses(String pesan) {
        new SweetAlertDialog(this, SweetAlertDialog.SUCCESS_TYPE)
                .setTitleText("Sukses")
                .setContentText(pesan)
                .setConfirmText("Oke")
                .setConfirmClickListener(sweetAlertDialog -> {
                    Intent i = new Intent(getApplication(), PembayaranActivity.class);
                    i.putExtra("id", id_pinjam);
                    Log.d("Response", "ID: " + id_pinjam);
                    startActivity(i);
                    finish();
                    sweetAlertDialog.dismissWithAnimation();
                })
                .show();
    }

}