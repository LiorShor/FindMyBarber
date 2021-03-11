package com.findmybarber.view.activities;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import androidx.appcompat.app.AppCompatActivity;
import com.findmybarber.R;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;
import java.util.List;

public class CheckPermissions extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.location_permission);
    }
    public void popUpAllowCalendar(View view) {
        Dexter.withActivity(this).withPermissions(Manifest.permission.READ_CALENDAR,
                Manifest.permission.WRITE_CALENDAR).withListener(new MultiplePermissionsListener() {
            @Override
            public void onPermissionsChecked(MultiplePermissionsReport report) {
                if(report.areAllPermissionsGranted())
                {
                    setContentView(R.layout.phone_permission);
                }
            }

            @Override
            public void onPermissionRationaleShouldBeShown(List< PermissionRequest > permissions, PermissionToken
                    token) {
                token.continuePermissionRequest();
            }
        }).check();
    }
    public void popUpAllowPhone(View view) {
        Intent login = new Intent(this, Login.class);
        Dexter.withActivity(this).withPermissions(Manifest.permission.CALL_PHONE).withListener(new MultiplePermissionsListener() {
            @Override
            public void onPermissionsChecked(MultiplePermissionsReport report) {
                if(report.areAllPermissionsGranted())
                {
                    startActivity(login);
                }
            }

            @Override
            public void onPermissionRationaleShouldBeShown(List< PermissionRequest > permissions, PermissionToken
                    token) {
                token.continuePermissionRequest();
            }
        }).check();
    }
    public void popUpAllowLocation(View view) {
        Dexter.withActivity(this).withPermissions(Manifest.permission.ACCESS_FINE_LOCATION).withListener(new MultiplePermissionsListener() {
            @Override
            public void onPermissionsChecked(MultiplePermissionsReport report) {
                if(report.areAllPermissionsGranted())
                {
                    setContentView(R.layout.calendar_permission);
                }
            }

            @Override
            public void onPermissionRationaleShouldBeShown(List< PermissionRequest > permissions, PermissionToken
                    token) {
                token.continuePermissionRequest();
            }
        }).check();
    }
}
