package org.codeforiraq.orphanage.ui;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import org.codeforiraq.orphanage.Models.NetworkManager;
import org.codeforiraq.orphanage.Models.PreferenceManager;
import org.codeforiraq.orphanage.Models.ProgressManager;
import org.codeforiraq.orphanage.Models.RequirementDocIds;
import org.codeforiraq.orphanage.R;
import org.codeforiraq.orphanage.databinding.ActivityRequirementsBinding;
import org.codeforiraq.orphanage.pager.Requirement;
import org.codeforiraq.orphanage.pager.RequirementRecyclerAdapter;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class RequirementsActivity extends AppCompatActivity {

    public static WeakReference<RequirementRecyclerAdapter> requirementRecyclerAdapter;
    FirebaseFirestore db;
    List<Requirement> requirements;
    ActivityRequirementsBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_requirements);
        //
        db = FirebaseFirestore.getInstance();
        //
        PreferenceManager.initialize(this);
        //
        binding.requirementsRecycler.setHasFixedSize(true);
        //
        binding.toolbar.setNavigationOnClickListener(view -> onBackPressed());
        //
        //
        binding.addRequirement.setOnClickListener(v ->
                startActivity(new Intent(this, RequirementManagementActivity.class))
        );
        //
        requirements = new ArrayList<>();
        RequirementDocIds.initialize();
        //
        new Handler().post(() -> {
            ProgressManager.showProgress(this);
            fetchingRequirementsData();

        });
    }


    private void fetchingRequirementsData() {
        if (NetworkManager.networkNotAvailable(this)) return;
        requirements.clear();
        RequirementDocIds.reqDocIds.clear();
        db.collection("root").document(PreferenceManager.getOrphanageEMail())
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
//                            reqDocIds.add(snapshot.getId());
                            RequirementDocIds.setReqDocIds(snapshot.getId());
                        }
                    } else {
                        Log.println(Log.INFO, "fireStore", Objects.requireNonNull(task.getException()).toString());
                    }
                    //
                    ProgressManager.dismissProgress();
                    requirementRecyclerAdapter = new WeakReference<>
                            (new RequirementRecyclerAdapter(this, requirements, true));

                    binding.requirementsRecycler.setAdapter(requirementRecyclerAdapter.get());

                });
    }
}