package org.codeforiraq.orphanage.ui;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import com.bumptech.glide.Glide;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import org.codeforiraq.orphanage.Models.OurApps;
import org.codeforiraq.orphanage.Models.PreferenceManager;
import org.codeforiraq.orphanage.R;
import org.codeforiraq.orphanage.databinding.ActivitySettingsBinding;

public class SettingsActivity extends AppCompatActivity {

    ActivitySettingsBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_settings);
        //
        PreferenceManager.initialize(this);
        //
        if (PreferenceManager.isLoggedIn()) {
            gettingOrphanageData();
        }
        //
        binding.aboutApp.setOnClickListener(view -> startActivity(new Intent(this, AboutAppActivity.class)));
        //
        binding.aboutTeam.setOnClickListener(view -> startActivity(new Intent(this, AboutTeamActivity.class)));
        //
        binding.aboutCode4Iraq.setOnClickListener(view ->
                startActivity(new Intent(this, CodeForIraqActivity.class)));
        //
        binding.ourApps.setOnClickListener(view -> OurApps.showApps(this));
    }

    private void gettingOrphanageData() {
        //
        binding.orphanageEmail.setText(PreferenceManager.getOrphanageEMail());
        //
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("root").document(PreferenceManager.getOrphanageEMail())
                .get().addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        DocumentSnapshot snapshot = task.getResult();
                        binding.orphanageName.setText(snapshot.getString("orphanageName"));
                        String photoUri = snapshot.getString("photoUri");
                        if (photoUri != null) {
                            if (!photoUri.isEmpty()) {
                                Uri uri = Uri.parse(photoUri);
                                Glide.with(SettingsActivity.this).load(uri)
                                        .into(binding.orphanagePhoto);
                            }
                        }

                    }
                });
    }
}