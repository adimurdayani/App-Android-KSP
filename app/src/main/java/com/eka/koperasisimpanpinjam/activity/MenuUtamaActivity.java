package com.eka.koperasisimpanpinjam.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.os.Bundle;
import android.view.MenuItem;

import com.eka.koperasisimpanpinjam.R;
import com.eka.koperasisimpanpinjam.fragment.AngsuranFragment;
import com.eka.koperasisimpanpinjam.fragment.HomeFragment;
import com.eka.koperasisimpanpinjam.fragment.LainnyaFragment;
import com.eka.koperasisimpanpinjam.fragment.PinjamFragment;
import com.eka.koperasisimpanpinjam.fragment.SimpananFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MenuUtamaActivity extends AppCompatActivity {
    FragmentManager manager;
    private BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu_utama);
        manager = getSupportFragmentManager();
        manager.beginTransaction().replace(R.id.frm_menu_utama, new HomeFragment()).commit();
        init();
    }

    public void init() {
        bottomNavigationView = findViewById(R.id.navigation_button);
        BottomNavigationView.OnNavigationItemSelectedListener navigasi = new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                Fragment fragment = null;
                switch (item.getItemId()) {
                    case R.id.pinjam:
                        fragment = new PinjamFragment();
                        break;
                    case R.id.angsuran:
                        fragment = new AngsuranFragment();
                        break;
                    case R.id.home:
                        fragment = new HomeFragment();
                        break;
                    case R.id.simpanan:
                        fragment = new SimpananFragment();
                        break;
                    case R.id.lainnya:
                        fragment = new LainnyaFragment();
                        break;
                }
                getSupportFragmentManager().beginTransaction().replace(R.id.frm_menu_utama, fragment).commit();
                return true;
            }
        };

        bottomNavigationView.setOnNavigationItemSelectedListener(navigasi);
    }
}