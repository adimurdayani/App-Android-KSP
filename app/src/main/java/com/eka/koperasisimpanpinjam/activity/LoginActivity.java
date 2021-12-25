package com.eka.koperasisimpanpinjam.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;

import android.os.Bundle;

import com.eka.koperasisimpanpinjam.R;
import com.eka.koperasisimpanpinjam.fragment.LoginFragment;

public class LoginActivity extends AppCompatActivity {

    private FragmentManager manager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        manager = getSupportFragmentManager();
        manager.beginTransaction().replace(R.id.frm_login, new LoginFragment()).commit();
        init();
    }

    public void init() {

    }
}