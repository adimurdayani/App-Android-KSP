package com.eka.koperasisimpanpinjam.fragment;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Looper;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
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

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.eka.koperasisimpanpinjam.R;
import com.eka.koperasisimpanpinjam.network.api.URLServer;
import com.google.android.material.textfield.TextInputLayout;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class RegisterFragment extends Fragment {

    private View view;
    private ImageView btn_kembali;
    private CardView btn_register;
    private TextInputLayout l_username, l_password, l_nama, l_email, l_konf_pass;
    private EditText edt_username, edt_password, edt_konf_pass, edt_email, edt_nama;
    private StringRequest registerUser;

    public RegisterFragment() {

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_register, container, false);
        init();
        return view;
    }

    private void init() {

        btn_kembali = view.findViewById(R.id.btn_kembali);
        btn_register = view.findViewById(R.id.btn_register);
        l_password = view.findViewById(R.id.l_password);
        l_username = view.findViewById(R.id.l_username);
        l_nama = view.findViewById(R.id.l_nama);
        l_email = view.findViewById(R.id.l_email);
        l_konf_pass = view.findViewById(R.id.l_konf_pass);
        edt_password = view.findViewById(R.id.edt_password);
        edt_username = view.findViewById(R.id.edt_username);
        edt_nama = view.findViewById(R.id.edt_nama);
        edt_email = view.findViewById(R.id.edt_email);
        edt_konf_pass = view.findViewById(R.id.edt_konf_pass);

        cekvalidasi();
        btn_register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (validasi()) {
                    register();
                }
            }
        });

        btn_kembali.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager manager = getFragmentManager();
                assert manager != null;
                manager.beginTransaction()
                        .replace(R.id.frm_login, new LoginFragment())
                        .commit();
            }
        });
    }

    private void register() {
       SweetAlertDialog dialog = new SweetAlertDialog(requireActivity(), SweetAlertDialog.PROGRESS_TYPE);
       dialog.setTitle("Loading");
       dialog.setContentText("Proses pembuatan akun sedang berjalan!");
       dialog.setCancelable(false);
       dialog.show();
        registerUser = new StringRequest(Request.Method.POST, URLServer.REGISTER, response -> {
            try {
                JSONObject object = new JSONObject(response);
                if (object.getBoolean("status")) {
                    setSukses("Register Sukses!");
                } else {
                    setError(object.getString("message"));
                }
            } catch (JSONException e) {
                e.printStackTrace();
                setError(e.getMessage());
            }
            dialog.dismissWithAnimation();
        }, error -> {
            dialog.dismissWithAnimation();
            Log.d("Response", "Erorr: " + error.getMessage());
            setError(error.getMessage());
        }) {
            @Nullable
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                String nama = edt_nama.getText().toString().trim();
                String username = edt_username.getText().toString().trim();
                String email = edt_email.getText().toString().trim();
                String password = edt_password.getText().toString().trim();
                HashMap<String, String> map = new HashMap<>();
                map.put("nama", nama);
                map.put("email", email);
                map.put("username", username);
                map.put("password", password);
                return map;
            }
        };
        setPolice();
        RequestQueue koneksi = Volley.newRequestQueue(requireContext());
        koneksi.add(registerUser);
    }

    private void setPolice() {
        registerUser.setRetryPolicy(new RetryPolicy() {
            @Override
            public int getCurrentTimeout() {
                return 2000;
            }

            @Override
            public int getCurrentRetryCount() {
                return 2000;
            }

            @Override
            public void retry(VolleyError error) throws VolleyError {
                if (Looper.myLooper() != null) {
                    Looper.prepare();
                    setError("Koneksi gagal!");
                }
            }
        });
    }

    public void cekvalidasi() {
        String nama = edt_nama.getText().toString().trim();
        String username = edt_username.getText().toString().trim();
        String email = edt_email.getText().toString().trim();
        String password = edt_password.getText().toString().trim();
        String konf_pass = edt_konf_pass.getText().toString().trim();

        edt_nama.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (nama.isEmpty()) {
                    l_nama.setErrorEnabled(false);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        edt_email.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (email.isEmpty()) {
                    l_email.setErrorEnabled(false);
                } else if (Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                    l_email.setErrorEnabled(false);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        edt_username.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (username.isEmpty()) {
                    l_username.setErrorEnabled(false);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        edt_password.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (password.isEmpty()) {
                    l_password.setErrorEnabled(false);
                } else if (password.length() > 7) {
                    l_password.setErrorEnabled(false);
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
                } else if (konf_pass.matches(password)) {
                    l_konf_pass.setErrorEnabled(false);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    private boolean validasi() {

        String nama = edt_nama.getText().toString().trim();
        String email = edt_email.getText().toString().trim();
        String username = edt_username.getText().toString().trim();
        String password = edt_password.getText().toString().trim();
        String konf_pass = edt_konf_pass.getText().toString().trim();

        if (nama.isEmpty()) {
            l_nama.setErrorEnabled(true);
            l_nama.setError("Kolom nama tidak boleh kosong!");
            return false;
        }
        if (email.isEmpty()) {
            l_email.setErrorEnabled(true);
            l_email.setError("Kolom email tidak boleh kosong!");
            return false;
        } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            l_email.setErrorEnabled(true);
            l_email.setError("Format email salah!. Contoh: gunakan @example.com");
            return false;
        }
        if (username.isEmpty()) {
            l_username.setErrorEnabled(true);
            l_username.setError("Kolom username tidak boleh kosong!");
            return false;
        }
        if (password.isEmpty()) {
            l_password.setErrorEnabled(true);
            l_password.setError("Kolom password tidak boleh kosong!");
            return false;
        } else if (password.length() < 6) {
            l_password.setErrorEnabled(true);
            l_password.setError("Password tidak boleh kurang dari 6 karakter!");
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
        } else if (!konf_pass.matches(password)) {
            l_konf_pass.setErrorEnabled(true);
            l_konf_pass.setError("Konfirmasi password tidak sama dengan password!");
            return false;
        }
        return true;
    }

    private void setSukses(String pesan) {
        new SweetAlertDialog(requireActivity(), SweetAlertDialog.SUCCESS_TYPE)
                .setTitleText("Sukses")
                .setContentText(pesan)
                .setConfirmText("Oke")
                .setConfirmClickListener(sweetAlertDialog -> {
                    FragmentManager manager = getFragmentManager();
                    manager.beginTransaction()
                            .replace(R.id.frm_login, new LoginFragment())
                            .commit();
                    sweetAlertDialog.dismissWithAnimation();
                })
                .show();
    }

    private void setError(String pesan) {
        new SweetAlertDialog(requireActivity(), SweetAlertDialog.ERROR_TYPE)
                .setTitleText("Oops...")
                .setContentText(pesan)
                .show();
    }
}
