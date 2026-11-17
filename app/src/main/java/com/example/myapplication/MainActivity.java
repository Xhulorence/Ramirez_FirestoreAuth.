package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.FirebaseAuth;

public class MainActivity extends AppCompatActivity {

    private MaterialButton btnSuperStart, btnLogoutTopRight;
    private ImageView imageViewProfile, iconMe, iconBible, iconPray;
    private TextView textMe, textBible, textPray;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        btnSuperStart = findViewById(R.id.btnSuperStart);
        btnLogoutTopRight = findViewById(R.id.btnLogoutTopRight);
        imageViewProfile = findViewById(R.id.imageViewProfile);
        iconMe = findViewById(R.id.textView14);
        textMe = findViewById(R.id.textView18);

        iconBible = findViewById(R.id.textView13);
        textBible = findViewById(R.id.textView19);

        iconPray = findViewById(R.id.textView11);
        textPray = findViewById(R.id.textView16);

        if (btnLogoutTopRight != null) {
            btnLogoutTopRight.setOnClickListener(v -> showLogoutConfirmationDialog());
        }

        btnSuperStart.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, SecondActivity.class);
            startActivity(intent);
        });

        View.OnClickListener meClickListener = v -> {
            Intent intent = new Intent(MainActivity.this, ReflectionsActivity.class);
            startActivity(intent);
        };

        if (imageViewProfile != null) imageViewProfile.setOnClickListener(meClickListener);
        if (iconMe != null) iconMe.setOnClickListener(meClickListener);
        if (textMe != null) textMe.setOnClickListener(meClickListener);

        View.OnClickListener bibleClickListener = v -> {
            Intent intent = new Intent(MainActivity.this, ThirdActivity.class);
            startActivity(intent);
        };

        if (iconBible != null) iconBible.setOnClickListener(bibleClickListener);
        if (textBible != null) textBible.setOnClickListener(bibleClickListener);

        View.OnClickListener prayClickListener = v -> {
            Intent intent = new Intent(MainActivity.this, SecondActivity.class);
            startActivity(intent);
        };

        if (iconPray != null) iconPray.setOnClickListener(prayClickListener);
        if (textPray != null) textPray.setOnClickListener(prayClickListener);
    }

    private void showLogoutConfirmationDialog() {
        new AlertDialog.Builder(this)
                .setTitle("Logout Confirmation")
                .setMessage("Are you sure you want to log out?")
                .setPositiveButton("Log Out", (dialog, which) -> {
                    try {
                        FirebaseAuth mAuth = FirebaseAuth.getInstance();
                        if (mAuth != null) {
                            mAuth.signOut();
                        }
                    } catch (Exception ignored) {}
                    Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    finish();
                })
                .setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss())
                .show();
    }
}
