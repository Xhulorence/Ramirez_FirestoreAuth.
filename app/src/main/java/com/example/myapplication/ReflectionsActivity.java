package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ReflectionsActivity extends AppCompatActivity implements ReflectionAdapter.OnReflectionListener {

    private RecyclerView recyclerView;
    private TextView tvEmpty;
    private FloatingActionButton fabAdd;
    private MaterialButton btnBackToHome;

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private String userId;

    private List<Reflection> reflectionList;
    private ReflectionAdapter adapter;
    private CollectionReference reflectionsRef;

    private boolean isFirebaseAvailable() {
        try {
            if (FirebaseApp.getApps(this).isEmpty()) {
                FirebaseApp.initializeApp(this);
            }
            mAuth = FirebaseAuth.getInstance();
            db = FirebaseFirestore.getInstance();
            return mAuth != null && db != null;
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reflections);

        recyclerView = findViewById(R.id.recyclerViewReflections);
        tvEmpty = findViewById(R.id.tvEmptyReflections);
        fabAdd = findViewById(R.id.fabAddReflection);
        btnBackToHome = findViewById(R.id.btnBackToHome);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        reflectionList = new ArrayList<>();
        adapter = new ReflectionAdapter(reflectionList, this);
        recyclerView.setAdapter(adapter);

        if (btnBackToHome != null) {
            btnBackToHome.setOnClickListener(v -> finish());
        }

        if (!isFirebaseAvailable()) {
            Toast.makeText(this, "Firebase is not configured. Please add google-services.json", Toast.LENGTH_LONG).show();
            tvEmpty.setVisibility(View.VISIBLE);
            tvEmpty.setText("Firebase configuration (google-services.json) missing.");
            fabAdd.setOnClickListener(v -> Toast.makeText(ReflectionsActivity.this, "Add google-services.json to save reflections", Toast.LENGTH_SHORT).show());
            return;
        }

        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser == null) {
            goToLogin();
            return;
        }
        userId = currentUser.getUid();
        reflectionsRef = db.collection("users").document(userId).collection("reflections");

        fabAdd.setOnClickListener(v -> showAddEditDialog(null));

        loadReflections();
    }

    private void goToLogin() {
        Intent intent = new Intent(ReflectionsActivity.this, LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    private void loadReflections() {
        if (reflectionsRef == null) return;
        reflectionsRef.orderBy("createdAt", com.google.firebase.firestore.Query.Direction.DESCENDING)
                .addSnapshotListener((value, error) -> {
                    if (error != null) {
                        Toast.makeText(ReflectionsActivity.this, "Error loading reflections: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                        return;
                    }

                    if (value != null) {
                        reflectionList.clear();
                        for (DocumentSnapshot doc : value.getDocuments()) {
                            Reflection reflection = doc.toObject(Reflection.class);
                            if (reflection != null) {
                                reflection.setReflectionId(doc.getId());
                                reflectionList.add(reflection);
                            }
                        }
                        adapter.notifyDataSetChanged();

                        if (reflectionList.isEmpty()) {
                            tvEmpty.setVisibility(View.VISIBLE);
                            recyclerView.setVisibility(View.GONE);
                        } else {
                            tvEmpty.setVisibility(View.GONE);
                            recyclerView.setVisibility(View.VISIBLE);
                        }
                    }
                });
    }

    private void showAddEditDialog(Reflection existingReflection) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_add_edit_reflection, null);
        builder.setView(dialogView);

        TextView tvDialogTitle = dialogView.findViewById(R.id.tvDialogTitle);
        TextInputEditText etTitle = dialogView.findViewById(R.id.etDialogTitle);
        TextInputEditText etVerse = dialogView.findViewById(R.id.etDialogVerse);
        TextInputEditText etNotes = dialogView.findViewById(R.id.etDialogNotes);

        boolean isEdit = (existingReflection != null);
        if (isEdit) {
            tvDialogTitle.setText("Edit Reflection");
            etTitle.setText(existingReflection.getTitle());
            etVerse.setText(existingReflection.getBibleVerse());
            etNotes.setText(existingReflection.getReflectionNotes());
        } else {
            tvDialogTitle.setText("Add Reflection");
        }

        builder.setPositiveButton(isEdit ? "Update" : "Save", (dialog, which) -> {
            String title = etTitle.getText() != null ? etTitle.getText().toString().trim() : "";
            String verse = etVerse.getText() != null ? etVerse.getText().toString().trim() : "";
            String notes = etNotes.getText() != null ? etNotes.getText().toString().trim() : "";

            if (TextUtils.isEmpty(title)) {
                Toast.makeText(ReflectionsActivity.this, "Title is required", Toast.LENGTH_SHORT).show();
                return;
            }

            if (isEdit) {
                updateReflection(existingReflection.getReflectionId(), title, verse, notes);
            } else {
                createReflection(title, verse, notes);
            }
        });

        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss());
        builder.create().show();
    }

    private void createReflection(String title, String verse, String notes) {
        if (reflectionsRef == null) return;
        String reflectionId = reflectionsRef.document().getId();
        long now = System.currentTimeMillis();

        Map<String, Object> map = new HashMap<>();
        map.put("reflectionId", reflectionId);
        map.put("title", title);
        map.put("bibleVerse", verse);
        map.put("reflectionNotes", notes);
        map.put("createdAt", now);
        map.put("updatedAt", now);

        reflectionsRef.document(reflectionId).set(map)
                .addOnSuccessListener(aVoid -> Toast.makeText(ReflectionsActivity.this, "Reflection added successfully", Toast.LENGTH_SHORT).show())
                .addOnFailureListener(e -> Toast.makeText(ReflectionsActivity.this, "Error adding reflection: " + e.getMessage(), Toast.LENGTH_SHORT).show());
    }

    private void updateReflection(String reflectionId, String title, String verse, String notes) {
        if (reflectionsRef == null) return;
        Map<String, Object> map = new HashMap<>();
        map.put("title", title);
        map.put("bibleVerse", verse);
        map.put("reflectionNotes", notes);
        map.put("updatedAt", System.currentTimeMillis());

        reflectionsRef.document(reflectionId).update(map)
                .addOnSuccessListener(aVoid -> Toast.makeText(ReflectionsActivity.this, "Reflection updated successfully", Toast.LENGTH_SHORT).show())
                .addOnFailureListener(e -> Toast.makeText(ReflectionsActivity.this, "Error updating reflection: " + e.getMessage(), Toast.LENGTH_SHORT).show());
    }

    @Override
    public void onEditClick(Reflection reflection) {
        showAddEditDialog(reflection);
    }

    @Override
    public void onDeleteClick(Reflection reflection) {
        if (reflectionsRef == null) return;
        new AlertDialog.Builder(this)
                .setTitle("Delete Reflection")
                .setMessage("Are you sure you want to delete this reflection?")
                .setPositiveButton("Delete", (dialog, which) -> {
                    reflectionsRef.document(reflection.getReflectionId()).delete()
                            .addOnSuccessListener(aVoid -> Toast.makeText(ReflectionsActivity.this, "Reflection deleted", Toast.LENGTH_SHORT).show())
                            .addOnFailureListener(e -> Toast.makeText(ReflectionsActivity.this, "Error deleting reflection: " + e.getMessage(), Toast.LENGTH_SHORT).show());
                })
                .setNegativeButton("Cancel", null)
                .show();
    }
}
