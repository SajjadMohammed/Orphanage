package org.codeforiraq.orphanage.ui;

import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import com.bumptech.glide.Glide;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import org.codeforiraq.orphanage.Models.NetworkManager;
import org.codeforiraq.orphanage.Models.PreferenceManager;
import org.codeforiraq.orphanage.Models.ProgressManager;
import org.codeforiraq.orphanage.Models.RequirementDocIds;
import org.codeforiraq.orphanage.R;
import org.codeforiraq.orphanage.databinding.ActivityOrphanageBinding;
import org.codeforiraq.orphanage.pager.Requirement;
import org.codeforiraq.orphanage.pager.RequirementRecyclerAdapter;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class OrphanageActivity extends AppCompatActivity {

    public static WeakReference<RequirementRecyclerAdapter> requirementRecyclerAdapter;
    FirebaseFirestore db;
    ActivityOrphanageBinding orphanageBinding;
    String documentId;
    List<Requirement> requirements;
    boolean isAdmin = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        orphanageBinding = DataBindingUtil.setContentView(this, R.layout.activity_orphanage);
        //
        PreferenceManager.initialize(this);
        //
        db = FirebaseFirestore.getInstance();
        //
        documentId = getIntent().getStringExtra("docId");
        //
        requirements = new ArrayList<>();
        RequirementDocIds.initialize();
        //
        orphanageBinding.toolbar.setNavigationOnClickListener(v -> onBackPressed());
        //
        new Handler().post(() -> {
            ProgressManager.showProgress(this);
            fetchingOrphanageData(documentId);
            fetchingRequirementsData(documentId);
        });
    }

    private void fetchingOrphanageData(String documentId) {
        if (NetworkManager.networkNotAvailable(this)) return;
        db.collection("root")
                .document(documentId).get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        DocumentSnapshot snapshot = task.getResult();
                        orphanageBinding.collapsingToolbar.setTitle(snapshot.getString("orphanageName"));
                        orphanageBinding.orphanageOwner.setText(snapshot.getString("fullName"));
                        orphanageBinding.orphanageAddress.setText(snapshot.getString("address"));
                        orphanageBinding.orphanagePhone.setText(snapshot.getString("phone"));
                        orphanageBinding.description.setText(snapshot.getString("description"));
                        String photoUri = snapshot.getString("photoUri");
                        if (photoUri != null) {
                            Uri uri = Uri.parse(photoUri);
                            Glide.with(this)
                                    .load(uri)
                                    .into(orphanageBinding.orphanagePhoto);
                        } else {
                            orphanageBinding.orphanagePhoto.setVisibility(View.GONE);
                        }
                    }
                    ProgressManager.dismissProgress();
                });
    }

    private void fetchingRequirementsData(String documentId) {
        if (NetworkManager.networkNotAvailable(this)) return;
        RequirementDocIds.reqDocIds.clear();
        db.collection("root").document(documentId)
                .collection("requirements")
                .get().addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        QuerySnapshot query = task.getResult();
                        for (DocumentSnapshot snapshot : query.getDocuments()) {
                            requirements.add(new Requirement(snapshot.getString("orphanageName")
                                    , snapshot.getString("requirementDate")
                                    , snapshot.getString("description")
                                    , snapshot.getString("email")
                                    , snapshot.getString("phone")));
                            RequirementDocIds.reqDocIds.add(snapshot.getId());
                        }
                    } else {
                        Log.println(Log.INFO, "fireStore", Objects.requireNonNull(task.getException()).toString());
                    }
                    //
                    ProgressManager.dismissProgress();
                    //
                    isAdmin = documentId.equals(PreferenceManager.getOrphanageEMail().trim());
                    //
                    requirementRecyclerAdapter = new WeakReference<>
                            (new RequirementRecyclerAdapter(this, requirements, isAdmin));
                    orphanageBinding.requirementsRecycler.setAdapter(requirementRecyclerAdapter.get());
                });
    }
}