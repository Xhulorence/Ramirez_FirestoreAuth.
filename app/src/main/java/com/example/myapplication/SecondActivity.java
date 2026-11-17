package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;

public class SecondActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_second);

        MaterialButton btnBackTopLeft = findViewById(R.id.btnBackTopLeft);
        if (btnBackTopLeft != null) {
            btnBackTopLeft.setOnClickListener(v -> finish());
        }

        MaterialButton btnSuperStart = findViewById(R.id.btnSuperStart);
        if (btnSuperStart != null) {
            btnSuperStart.setOnClickListener(v -> {
                Intent intent = new Intent(SecondActivity.this, ThirdActivity.class);
                startActivity(intent);
            });
        }
    }
}
