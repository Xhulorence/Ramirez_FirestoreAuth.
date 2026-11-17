package com.example.myapplication;

import android.os.Bundle;
import android.view.View;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.button.MaterialButton;

public class ThirdActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_third);

        MaterialButton btnBackTopLeft = findViewById(R.id.btnBackThirdTopLeft);
        if (btnBackTopLeft != null) {
            btnBackTopLeft.setOnClickListener(v -> finish());
        }

        MaterialButton btnBack = findViewById(R.id.btnBackThird);
        if (btnBack != null) {
            btnBack.setOnClickListener(v -> finish());
        }
    }
}
