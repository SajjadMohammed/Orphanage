package org.codeforiraq.orphanage.ui;

import android.os.Bundle;
import android.os.Handler;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import org.codeforiraq.orphanage.Models.NetworkManager;
import org.codeforiraq.orphanage.Models.PreferenceManager;
import org.codeforiraq.orphanage.Models.ProgressManager;
import org.codeforiraq.orphanage.R;
import org.codeforiraq.orphanage.databinding.ActivityRequirementManagementBinding;
import org.codeforiraq.orphanage.pager.Requirement;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class RequirementManagementActivity extends AppCompatActivity {

    ActivityRequirementManagementBinding binding;

    FirebaseFirestore db;
    String orphanageName, description, requirementDate, phone, email;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_requirement_management);
        //
        PreferenceManager.initialize(this);
        //
        db = FirebaseFirestore.getInstance();
        //
        binding.toolbar.setNavigationOnClickListener(view -> onBackPressed());
        //
        new Handler().post(() -> {
            ProgressManager.showProgress(this);
            fetchingData();
        });
        //
        binding.save.setOnClickListener(view -> {
            if (areTheFieldsEmpty()) return;
            //
            if (NetworkManager.networkNotAvailable(this)) {
                ProgressManager.dismissProgress();
                return;
            }
            //
            Requirement requirement = new Requirement(orphanageName
                    , requirementDate
                    , description
                    , email,
                    phone);
            //
            //adding requirement manager details
            db.collection("root")
                    .document(PreferenceManager.getOrphanageEMail())
                    .collection("req")
                    .document()// this will generate auto document id
                    .set(requirement)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            clearDescription();
                            Toast.makeText(this, "عملية الاضافة تمت بنجاح", Toast.LENGTH_LONG).show();
                        }
                    });
        });
    }

    private void fetchingData() {
        if (NetworkManager.networkNotAvailable(this)) {
            ProgressManager.dismissProgress();
            return;
        }
        //
        db.collection("root").document(PreferenceManager.getOrphanageEMail())
                .get().addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        DocumentSnapshot snapshot = task.getResult();
                        orphanageName = snapshot.getString("orphanageName");
                    }
                    ProgressManager.dismissProgress();
                });

    }

    private boolean areTheFieldsEmpty() {
        SimpleDateFormat dtf = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss EEEE", Locale.getDefault());
        Date now = new Date();
        requirementDate = dtf.format(now);
        email = binding.email.getText().toString();
        phone = binding.orphanagePhone.getText().toString();
        description = binding.description.getText().toString();
        //
        if (description.isEmpty()) {
            Toast.makeText(this, "رجاءاً قم بملئ جميع الحقول المطلوبة", Toast.LENGTH_LONG).show();
            return true;
        }
        return false;
    }

    private void clearDescription() {
        binding.description.setText("");
    }
}