package com.eka.koperasisimpanpinjam.fragment;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.DatePicker;
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
import com.eka.koperasisimpanpinjam.network.api.URLServer;
import com.eka.koperasisimpanpinjam.network.model.DataMember;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class EditProfileFragment extends Fragment {

    private View view;
    private TextView getTexttgl, getkelamin, getagama, getgoldarah, txtnama, getidmember;
    private CardView btn_tgl_lahir, btn_simpan;
    private EditText edt_t_lahir, edt_no_hp, edt_pekerjaan, edt_alamat, edt_nik;
    private Spinner gol_darah, agama, kelamin;
    private SimpleDateFormat simpleDateFormat;
    private DatePickerDialog datePickerDialog;
    private ProgressDialog dialog;
    private StringRequest editProfil;
    private SharedPreferences preferences;
    private SharedPreferences.Editor editor;
    private ArrayList<DataMember> dataMembers;
    private ImageView btn_kembali;

    public EditProfileFragment() {

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_editprofile, container, false);
        init();
        return view;
    }

    private void init() {
        preferences = requireContext().getSharedPreferences("user", Context.MODE_PRIVATE);
        getTexttgl = view.findViewById(R.id.getTextTgl);
        btn_tgl_lahir = view.findViewById(R.id.btn_tgl_lahir);
        edt_t_lahir = view.findViewById(R.id.edt_tlahir);
        edt_no_hp = view.findViewById(R.id.edt_nohp);
        edt_pekerjaan = view.findViewById(R.id.edt_pekerjaan);
        edt_alamat = view.findViewById(R.id.edt_alamat);
        edt_nik = view.findViewById(R.id.edt_nik);
        gol_darah = view.findViewById(R.id.edt_goldarah);
        kelamin = view.findViewById(R.id.edt_kelamin);
        agama = view.findViewById(R.id.edt_agama);
        btn_simpan = view.findViewById(R.id.btn_simpan);
        getkelamin = view.findViewById(R.id.getkelamin);
        getagama = view.findViewById(R.id.getagama);
        getgoldarah = view.findViewById(R.id.getgolongandarah);
        txtnama = view.findViewById(R.id.nama);
        getidmember = view.findViewById(R.id.id_member);
        btn_kembali = view.findViewById(R.id.btn_kembali);

        txtnama.setText(preferences.getString("nama", ""));
        getidmember.setText("" + preferences.getInt("id_m", 0));
        edt_nik.setText(preferences.getString("nik", ""));
        edt_no_hp.setText(preferences.getString("no_hp", ""));
        edt_alamat.setText(preferences.getString("alamat", ""));
        edt_t_lahir.setText(preferences.getString("t_lahir", ""));
        edt_pekerjaan.setText(preferences.getString("pekerjaan", ""));

        dialog = new ProgressDialog(getContext());
        dialog.setCancelable(false);

        btn_simpan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (validasi()) {
                    editProfile();
                }
            }
        });

        simpleDateFormat = new SimpleDateFormat("dd MM yyyy", Locale.US);
        btn_tgl_lahir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tampilkanTanggal();
            }
        });
        kelamin.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                getkelamin.setText(kelamin.getSelectedItem().toString());
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        gol_darah.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                getgoldarah.setText(gol_darah.getSelectedItem().toString());
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        agama.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                getagama.setText(agama.getSelectedItem().toString());
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        btn_kembali.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager manager = getFragmentManager();
                assert manager != null;
                manager.beginTransaction()
                        .replace(R.id.frm_menu_utama, new LainnyaFragment())
                        .commit();
            }
        });
    }

    private void editProfile() {
        dataMembers = new ArrayList<>();
        dialog.setMessage("Loading...");
        dialog.show();

        editProfil = new StringRequest(Request.Method.POST, URLServer.EDITPROFIL, response -> {
            try {
                JSONObject object = new JSONObject(response);
                if (object.getBoolean("status")) {
                    JSONObject data = object.getJSONObject("data");

                    DataMember postMember = new DataMember();
                    postMember.setNik(data.getInt("nik"));
                    postMember.setTgl_lahir(data.getString("tgl_lahir"));
                    postMember.setKelamin(data.getString("kelamin"));
                    postMember.setGol_darah(data.getString("gol_darah"));
                    postMember.setAgama(data.getString("agama"));
                    postMember.setPekerjaan(data.getString("pekerjaan"));
                    postMember.setAlamat(data.getString("alamat"));
                    postMember.setNo_hp(data.getString("no_hp"));
                    postMember.setT_lahir(data.getString("t_lahir"));

                    preferences = requireContext().getSharedPreferences("user", Context.MODE_PRIVATE);
                    editor = preferences.edit();
                    editor.putString("nik", data.getString("nik"));
                    editor.putString("alamat", data.getString("alamat"));
                    editor.putString("t_lahir", data.getString("t_lahir"));
                    editor.putString("no_hp", data.getString("no_hp"));
                    editor.putString("pekerjaan", data.getString("pekerjaan"));
                    editor.apply();

                    Toast.makeText(getContext(), "Data Tersimpan", Toast.LENGTH_SHORT).show();
                    FragmentManager manager = getFragmentManager();
                    assert manager != null;
                    manager.beginTransaction()
                            .replace(R.id.frm_menu_utama, new LainnyaFragment())
                            .commit();
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
                String nik = edt_nik.getText().toString().trim();
                String no_hp = edt_no_hp.getText().toString().trim();
                String pekerjaan = edt_pekerjaan.getText().toString().trim();
                String alamat = edt_alamat.getText().toString().trim();
                String t_lahir = edt_t_lahir.getText().toString().trim();
                String kelamin = getkelamin.getText().toString().trim();
                String tgl = getTexttgl.getText().toString().trim();
                String darah = getgoldarah.getText().toString().trim();
                String agama = getagama.getText().toString().trim();
                String idmember = getidmember.getText().toString().trim();

                HashMap<String, String> map = new HashMap<>();
                map.put("id_m", idmember);
                map.put("nik", nik);
                map.put("tgl_lahir", tgl);
                map.put("kelamin", kelamin);
                map.put("gol_darah", darah);
                map.put("agama", agama);
                map.put("pekerjaan", pekerjaan);
                map.put("alamat", alamat);
                map.put("no_hp", no_hp);
                map.put("t_lahir", t_lahir);
                return map;
            }
        };
        RequestQueue koneksi = Volley.newRequestQueue(requireContext());
        koneksi.add(editProfil);
    }

    private boolean validasi() {
        String nik = edt_nik.getText().toString().trim();
        String no_hp = edt_no_hp.getText().toString().trim();
        String pekerjaan = edt_pekerjaan.getText().toString().trim();
        String alamat = edt_alamat.getText().toString().trim();
        String t_lahir = edt_t_lahir.getText().toString().trim();

        if (nik.isEmpty()) {
            edt_nik.setError("Kolom NIK tidak boleh kosong!");
        }
        if (no_hp.isEmpty()) {
            edt_no_hp.setError("Kolom no. telp tidak boleh kosong!");
        }
        if (pekerjaan.isEmpty()) {
            edt_pekerjaan.setError("Kolom pekerjaan tidak boleh kosong!");
        }
        if (alamat.isEmpty()) {
            edt_alamat.setError("Kolom alamat tidak boleh kosong!");
        }
        if (t_lahir.isEmpty()) {
            edt_t_lahir.setError("Kolom tempat lahir tidak boleh kosong!");
        }
        return true;
    }

    private void tampilkanTanggal() {
        Calendar calendar = Calendar.getInstance();
        datePickerDialog = new DatePickerDialog(getContext(), new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                Calendar tanggal = Calendar.getInstance();
                tanggal.set(year, month, dayOfMonth);

                getTexttgl.setText("" + simpleDateFormat.format(tanggal.getTime()));
                Toast.makeText(getContext(), simpleDateFormat.format(tanggal.getTime()), Toast.LENGTH_SHORT).show();
            }
        }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
        datePickerDialog.show();
    }

}
