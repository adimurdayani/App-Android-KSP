package com.eka.koperasisimpanpinjam.fragment;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
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
import com.eka.koperasisimpanpinjam.activity.LoginActivity;
import com.eka.koperasisimpanpinjam.activity.MenuUtamaActivity;
import com.eka.koperasisimpanpinjam.network.api.URLServer;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class LainnyaFragment extends Fragment {
    private View view;
    private TextView nama, email, no_hp;
    private LinearLayout btn_facebook, btn_instagram, btn_whatsapp;
    private RelativeLayout btn_keluar, btn_pengaturan, btn_info;
    private CardView btn_ubah;
    SharedPreferences preferences;
    StringRequest prosesLogout;

    public LainnyaFragment() {

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_lainnya, container, false);
        init();
        return view;
    }

    private void init() {
        preferences = requireContext().getSharedPreferences("user", Context.MODE_PRIVATE);
        nama = view.findViewById(R.id.nama);
        email = view.findViewById(R.id.email);
        btn_keluar = view.findViewById(R.id.btn_keluar);
        btn_pengaturan = view.findViewById(R.id.btn_pengatuarn);
        btn_info = view.findViewById(R.id.btn_info);
        no_hp = view.findViewById(R.id.no_hp);
        btn_ubah = view.findViewById(R.id.btn_ubah);
        btn_facebook = view.findViewById(R.id.btn_facebook);
        btn_instagram = view.findViewById(R.id.btn_instagram);
        btn_whatsapp = view.findViewById(R.id.btn_whatsapp);

        nama.setText(preferences.getString("nama", ""));
        email.setText(preferences.getString("email", ""));
        no_hp.setText(preferences.getString("no_hp", ""));

        btn_whatsapp.setOnClickListener(v -> {
            Intent wa = new Intent();
            wa.setAction(Intent.ACTION_VIEW);
            wa.addCategory(Intent.CATEGORY_BROWSABLE);
            wa.setData(Uri.parse("https://api.whatsapp.com/send?phone="));
            startActivity(wa);
        });

        btn_facebook.setOnClickListener(v -> {
            Intent fb = new Intent();
            fb.setAction(Intent.ACTION_VIEW);
            fb.addCategory(Intent.CATEGORY_BROWSABLE);
            fb.setData(Uri.parse("https://web.facebook.com/?_rdc=1&_rdr/"));
            startActivity(fb);
        });

        btn_instagram.setOnClickListener(v -> {
            Intent ig = new Intent();
            ig.setAction(Intent.ACTION_VIEW);
            ig.addCategory(Intent.CATEGORY_BROWSABLE);
            ig.setData(Uri.parse("https://www.instagram.com/"));
            startActivity(ig);
        });

        btn_keluar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new SweetAlertDialog(requireActivity(), SweetAlertDialog.WARNING_TYPE)
                        .setTitleText("Apakah anda yakin ingin logout?")
                        .setConfirmText("Iya")
                        .setConfirmClickListener(sweetAlertDialog -> {
                            logout();
                        })
                        .setCancelText("Tidak")
                        .setCancelClickListener(SweetAlertDialog::dismissWithAnimation)
                        .show();
            }
        });

        btn_pengaturan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager manager = getFragmentManager();
                assert manager != null;
                manager.beginTransaction()
                        .replace(R.id.frm_menu_utama, new EditProfileFragment())
                        .commit();
            }
        });
        btn_info.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager manager = getFragmentManager();
                assert manager != null;
                manager.beginTransaction()
                        .replace(R.id.frm_menu_utama, new InfoFragment())
                        .commit();
            }
        });
        btn_ubah.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager ubahnama = getFragmentManager();
                assert ubahnama != null;
                ubahnama.beginTransaction()
                        .replace(R.id.frm_menu_utama, new UbahNamaFragment())
                        .commit();
            }
        });
    }

    private void logout() {
        SweetAlertDialog dialog = new SweetAlertDialog(requireActivity(), SweetAlertDialog.PROGRESS_TYPE);
        dialog.setTitle("Loading");
        dialog.setContentText("Proses pembuatan akun sedang berjalan!");
        dialog.setCancelable(false);
        dialog.show();
        prosesLogout = new StringRequest(Request.Method.GET, URLServer.LOGOUT, response -> {
            try {
                JSONObject object = new JSONObject(response);
                if (object.getBoolean("status")) {
                    SharedPreferences.Editor editor = preferences.edit();
                    editor.remove(preferences.getString("token", ""));
                    editor.putBoolean("isLoggedIn", false);
                    editor.clear();
                    editor.apply();
                    new SweetAlertDialog(requireActivity(), SweetAlertDialog.SUCCESS_TYPE)
                            .setTitleText("Logout Sukses")
                            .setConfirmText("Oke")
                            .setConfirmClickListener(sweetAlertDialog -> {
                                Intent i = new Intent(requireActivity(),  LoginActivity.class);
                                i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                startActivity(i);
                                requireActivity().finish();
                            })
                            .show();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            dialog.dismissWithAnimation();
        }, error -> {
            dialog.dismissWithAnimation();
            error.printStackTrace();
            Toast.makeText(getContext(), "Terjadi Masalah Koneksi", Toast.LENGTH_SHORT).show();
        });
        RequestQueue queue = Volley.newRequestQueue(requireContext());
        queue.add(prosesLogout);
    }
}
