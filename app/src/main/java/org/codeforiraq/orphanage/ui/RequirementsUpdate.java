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
import org.codeforiraq.orphanage.databinding.ActivityRequirementsUpdateBinding;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class RequirementsUpdate extends AppCompatActivity {

    ActivityRequirementsUpdateBinding updateBinding;
    FirebaseFirestore db;
    String orphanageName, description, requirementDate, phone, email;
    String docId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        updateBinding = DataBindingUtil.setContentView(this, R.layout.activity_requirements_update);
        //
        updateBinding.toolbar.setNavigationOnClickListener(view -> onBackPressed());
        //
        db = FirebaseFirestore.getInstance();
        //
        if (getIntent() != null)
            docId = getIntent().getStringExtra("docId");
        //
        new Handler().post(() -> {
            ProgressManager.showProgress(this);
            fetchingData();
        });
        //
        updateBinding.save.setOnClickListener(view -> update(docId));
    }

    private void update(String docId) {
        if (areTheFieldsEmpty()) return;
        if (NetworkManager.networkNotAvailable(this)) return;
        //
        ProgressManager.showProgress(this);
        db.collection("root").document(PreferenceManager.getOrphanageEMail())
                .collection("req")
                .document(docId)
                .update("orphanageName", orphanageName, "email", email
                        , "phone", phone, "description", description
                        , "requirementDate", requirementDate)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(this, "عملية تحديث البيانات تمت بنجاح", Toast.LENGTH_LONG).show();
                        ProgressManager.dismissProgress();
                    }
                });
    }

    private void fetchingData() {
        if (NetworkManager.networkNotAvailable(this)) {
            ProgressManager.dismissProgress();
            return;
        }
        //
        db.collection("root").document(PreferenceManager.getOrphanageEMail())
                .collection("req").document(docId)
                .get().addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        DocumentSnapshot documentSnapshot = task.getResult();
                        orphanageName = documentSnapshot.getString("orphanageName");
                        updateBinding.orphanagePhone.setText(documentSnapshot.getString("phone"));
                        updateBinding.email.setText(documentSnapshot.getString("email"));
                        updateBinding.description.setText(documentSnapshot.getString("description"));
                    }
                    ProgressManager.dismissProgress();
                });

    }

    private boolean areTheFieldsEmpty() {
        SimpleDateFormat dtf = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss EEEE", Locale.getDefault());
        Date now = new Date();
        requirementDate = dtf.format(now);
        email = updateBinding.email.getText().toString();
        phone = updateBinding.orphanagePhone.getText().toString();
        description = updateBinding.description.getText().toString();
        //
        if (description.isEmpty()) {
            Toast.makeText(this, "رجاءاً قم بملئ جميع الحقول المطلوبة", Toast.LENGTH_LONG).show();
            return true;
        }
        return false;
    }
}