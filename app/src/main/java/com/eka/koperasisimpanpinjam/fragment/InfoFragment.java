package com.eka.koperasisimpanpinjam.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.eka.koperasisimpanpinjam.R;

public class InfoFragment extends Fragment {

    private View view;
    private ImageView btn_kembali;

    public InfoFragment() {

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_info, container, false);
        init();
        return view;
    }

    private void init() {
        btn_kembali = view.findViewById(R.id.btn_kembali);
        btn_kembali.setOnClickListener(v -> {
            FragmentManager manager = getFragmentManager();
            manager.beginTransaction()
                    .replace(R.id.frm_menu_utama, new LainnyaFragment())
                    .commit();
        });
    }
}
