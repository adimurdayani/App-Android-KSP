package com.eka.koperasisimpanpinjam.fragment;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.eka.koperasisimpanpinjam.R;
import com.google.android.material.textfield.TextInputLayout;

import java.util.regex.Pattern;

public class LupaPasswordFragment extends Fragment {
    private View view;
    private ImageView btn_kembali;
    private TextInputLayout  l_password_baru, l_password_lama,  l_konf_pass;
    private EditText edt_password_baru, edt_password_lama, edt_konf_pass;
    private CardView btn_ubah_password;
    public static final Pattern PASSWORD_FORMAT = Pattern.compile("^" +
            "(?=.*[1-9])" + //harus menggunakan satu angka
            "(?=.*[a-z])" + //harus menggunakan abjad
            "(?=.*[A-Z])" + //harus menggunakan huruf kapital
            "(?=.*[@#$%^&+=])" + //harus menggunakan sepesial karakter
            "(?=\\S+$)" + // tidak menggunakan spasi
            ".{6,}" + //harus lebih dari 6 karakter
            "$"
    );
    public LupaPasswordFragment(){

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_lupapassword, container,false);
        init();
        return view;
    }

    private void init() {
        btn_kembali = view.findViewById(R.id.btn_kembali);
        l_password_lama = view.findViewById(R.id.l_password_lama);
        l_password_baru = view.findViewById(R.id.l_password_baru);
        l_konf_pass = view.findViewById(R.id.l_konf_pass);
        edt_password_lama = view.findViewById(R.id.edt_password_lama);
        edt_password_baru = view.findViewById(R.id.edt_password_baru);
        edt_konf_pass = view.findViewById(R.id.edt_konf_pass);
        btn_ubah_password = view.findViewById(R.id.btn_lupa_password);

        cekvalidasi();
        btn_ubah_password.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (validasi()){

                }
            }
        });

        btn_kembali.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager manager = getFragmentManager();
                manager.beginTransaction()
                        .replace(R.id.frm_login, new LoginFragment())
                        .commit();
            }
        });
    }

    public void cekvalidasi(){
        String password_lama = edt_password_lama.getText().toString().trim();
        String password_baru = edt_password_baru.getText().toString().trim();
        String konf_pass = edt_konf_pass.getText().toString().trim();

        edt_password_lama.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (password_lama.isEmpty()) {
                    l_password_lama.setErrorEnabled(false);
                } else if (password_lama.length() > 7) {
                    l_password_lama.setErrorEnabled(false);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        edt_password_baru.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (password_baru.isEmpty()) {
                    l_password_baru.setErrorEnabled(false);
                } else if (password_baru.length() > 7) {
                    l_password_baru.setErrorEnabled(false);
                } else if (PASSWORD_FORMAT.matcher(password_baru).matches()) {
                    l_password_baru.setErrorEnabled(false);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        edt_konf_pass.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (konf_pass.isEmpty()) {
                    l_konf_pass.setErrorEnabled(false);
                } else if (konf_pass.length() > 7) {
                    l_konf_pass.setErrorEnabled(false);
                } else if (PASSWORD_FORMAT.matcher(konf_pass).matches()) {
                    l_konf_pass.setErrorEnabled(false);
                } else if (konf_pass.matches(password_baru)) {
                    l_konf_pass.setErrorEnabled(false);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    private boolean validasi() {

        String password_lama = edt_password_lama.getText().toString().trim();
        String password_baru = edt_password_baru.getText().toString().trim();
        String konf_pass = edt_konf_pass.getText().toString().trim();

        if (password_lama.isEmpty()) {
            l_password_lama.setErrorEnabled(true);
            l_password_lama.setError("Kolom password lama tidak boleh kosong!");
            return false;
        } else if (password_lama.length() < 6) {
            l_password_lama.setErrorEnabled(true);
            l_password_lama.setError("Password lama tidak boleh kurang dari 6 karakter!");
            return false;
        }

        if (password_baru.isEmpty()) {
            l_password_baru.setErrorEnabled(true);
            l_password_baru.setError("Kolom password baru tidak boleh kosong!");
            return false;
        } else if (password_baru.length() < 6) {
            l_password_baru.setErrorEnabled(true);
            l_password_baru.setError("Password baru tidak boleh kurang dari 6 karakter!");
            return false;
        } else if (!PASSWORD_FORMAT.matcher(password_baru).matches()) {
            l_password_baru.setErrorEnabled(true);
            l_password_baru.setError("Password baru sangat lemah!. Contoh: @Jad123");
            return false;
        }

        if (konf_pass.isEmpty()) {
            l_konf_pass.setErrorEnabled(true);
            l_konf_pass.setError("Kolom konfirmasi password tidak boleh kosong!");
            return false;
        } else if (konf_pass.length() < 6) {
            l_konf_pass.setErrorEnabled(true);
            l_konf_pass.setError("Konfirmasi password tidak boleh kurang dari 6 karakter!");
            return false;
        } else if (!PASSWORD_FORMAT.matcher(konf_pass).matches()) {
            l_konf_pass.setErrorEnabled(true);
            l_konf_pass.setError("Konfirmasi password sangat lemah!");
            return false;
        } else if (!konf_pass.matches(password_baru)) {
            l_konf_pass.setErrorEnabled(true);
            l_konf_pass.setError("Konfirmasi password tidak sama dengan password baru!");
            return false;
        }
        return true;
    }
}
