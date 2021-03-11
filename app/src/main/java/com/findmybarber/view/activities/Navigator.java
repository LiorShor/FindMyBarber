package com.findmybarber.view.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;

import com.findmybarber.R;

import java.security.Permissions;

public class Navigator extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.loading_screen);
        Intent permission = new Intent(this, CheckPermissions.class);
        Intent mainActivity = new Intent(this, Login.class);
        final Handler handler = new Handler();
        final Runnable r = () -> {
            if (ContextCompat.checkSelfPermission(Navigator.this,
                    Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                startActivity(permission);
            }
            else {
                startActivity(mainActivity);
            }
        };
        handler.postDelayed(r, 5000);
    }
}