package com.eka.koperasisimpanpinjam.fragment;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Looper;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
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
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.eka.koperasisimpanpinjam.R;
import com.eka.koperasisimpanpinjam.activity.LoginActivity;
import com.eka.koperasisimpanpinjam.activity.MenuUtamaActivity;
import com.eka.koperasisimpanpinjam.network.api.URLServer;
import com.google.android.material.textfield.TextInputLayout;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class LoginFragment extends Fragment {

    private View view;
    private TextView register, lupa_password;
    private CardView btn_login;
    private TextInputLayout l_username, l_password;
    private EditText edt_username, edt_password;
    private ProgressDialog dialog;
    private StringRequest loginUser;
    private SharedPreferences session_data;
    private SharedPreferences.Editor editor;

    public LoginFragment() {

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_login, container, false);
        init();
        return view;
    }

    private void init() {

        register = view.findViewById(R.id.btn_register);
        btn_login = view.findViewById(R.id.btn_login);
        l_password = view.findViewById(R.id.l_password);
        l_username = view.findViewById(R.id.l_username);
        edt_password = view.findViewById(R.id.edt_password);
        edt_username = view.findViewById(R.id.edt_username);
//        lupa_password = view.findViewById(R.id.btn_lupa_password);

//        lupa_password.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                FragmentManager manager = getFragmentManager();
//                manager.beginTransaction()
//                        .replace(R.id.frm_login, new LupaPasswordFragment())
//                        .commit();
//            }
//        });

        cekvalidasi();
        btn_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (validasi()) {
                    login();
                }
            }
        });
        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager manager = getFragmentManager();
                assert manager != null;
                manager.beginTransaction()
                        .replace(R.id.frm_login, new RegisterFragment())
                        .commit();
            }
        });
    }

    private void login() {
        SweetAlertDialog dialog = new SweetAlertDialog(requireActivity(), SweetAlertDialog.PROGRESS_TYPE);
        dialog.setTitle("Loading");
        dialog.setCancelable(false);
        dialog.show();

        loginUser = new StringRequest(Request.Method.POST, URLServer.LOGIN, response -> {
            try {
                JSONObject object = new JSONObject(response);
                if (object.getBoolean("status")) {
                    JSONObject data = object.getJSONObject("data");
                    session_data = requireContext().getSharedPreferences("user", Context.MODE_PRIVATE);
                    editor = session_data.edit();
                    editor.putInt("id_m", data.getInt("id_m"));
                    editor.putString("nama", data.getString("nama"));
                    editor.putString("username", data.getString("username"));
                    editor.putString("email", data.getString("email"));
                    editor.putString("no_hp", data.getString("no_hp"));
                    editor.putString("nik", data.getString("nik"));
                    editor.putString("member_id", data.getString("member_id"));
                    editor.putBoolean("isLoggedIn", true);
                    editor.apply();

                    startActivity(new Intent(getContext(), MenuUtamaActivity.class));
                    ((LoginActivity) requireContext()).finish();
                    setSukses("Login sukses!");
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
            Log.d("Response", "Error: " + error.getMessage());
            setError(error.getMessage());
        }) {
            @Nullable
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                String username = edt_username.getText().toString().trim();
                String password = edt_password.getText().toString().trim();
                HashMap<String, String> map = new HashMap<>();
                map.put("username", username);
                map.put("password", password);
                return map;
            }
        };
        setPolice();
        RequestQueue koneksi = Volley.newRequestQueue(requireContext());
        koneksi.add(loginUser);
    }

    private void setPolice() {
        loginUser.setRetryPolicy(new RetryPolicy() {
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
        String username = edt_username.getText().toString().trim();
        String password = edt_password.getText().toString().trim();
        edt_username.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (username.isEmpty()) {
                    l_username.setErrorEnabled(false);
                } else if (username.length() < 0) {
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
    }

    private boolean validasi() {
        String username = edt_username.getText().toString().trim();
        String password = edt_password.getText().toString().trim();

        if (username.isEmpty()) {
            l_username.setErrorEnabled(true);
            l_username.setError("Username tidak boleh kosong!");
            return false;
        }
        if (password.isEmpty()) {
            l_password.setErrorEnabled(true);
            l_password.setError("Password tidak boleh kosong!");
            return false;
        } else if (password.length() < 6) {
            l_password.setErrorEnabled(true);
            l_password.setError("Password tidak boleh kurang dari 6 karakter!");
            return false;
        }
        return true;
    }

    private void setSukses(String pesan) {
        new SweetAlertDialog(requireActivity(), SweetAlertDialog.SUCCESS_TYPE)
                .setTitleText("Sukses")
                .setContentText(pesan)
                .show();
    }

    private void setError(String pesan) {
        new SweetAlertDialog(requireActivity(), SweetAlertDialog.ERROR_TYPE)
                .setTitleText("Oops...")
                .setContentText(pesan)
                .show();
    }
}
