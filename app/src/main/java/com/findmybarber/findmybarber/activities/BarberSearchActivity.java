package com.findmybarber.findmybarber.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.findmybarber.findmybarber.R;

public class BarberSearchActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        String[] res;
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_barber_search);
        String KEY = "MainActivityKey";
        if(getIntent().hasExtra(KEY)) {
            res = getIntent().getStringArrayExtra(KEY);
            Toast.makeText(this, res[0], Toast.LENGTH_SHORT).show();
        }
    }

    public void searchForBarber(View view) {
        TextView textView = (TextView) view;
        textView.setText("");
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_HOME);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

}