package org.codeforiraq.orphanage.ui;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import org.codeforiraq.orphanage.Models.NetworkManager;
import org.codeforiraq.orphanage.Models.PreferenceManager;
import org.codeforiraq.orphanage.Models.ProgressManager;
import org.codeforiraq.orphanage.R;
import org.codeforiraq.orphanage.databinding.RequirementsFragmentBinding;
import org.codeforiraq.orphanage.pager.Requirement;
import org.codeforiraq.orphanage.pager.RequirementRecyclerAdapter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class RequirementsFragment extends Fragment {

    FirebaseFirestore db;
    RequirementsFragmentBinding binding;
    RequirementRecyclerAdapter requirementRecyclerAdapter;
    List<String> docIds;
    List<Requirement> requirements;
    int length;

    public RequirementsFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(getLayoutInflater(), R.layout.requirements_fragment, container, false);
        //
        return binding.getRoot();
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        //
        PreferenceManager.initialize(requireContext());
        if (PreferenceManager.isLoggedIn()) {
            binding.addRequirement.setVisibility(View.VISIBLE);
        }
        //
        if (PreferenceManager.isLoggedIn()) {
            //
            binding.nested.setOnScrollChangeListener((View.OnScrollChangeListener) (view1, i, i1, i2, i3) -> {
                if (i1 > i3) {
                    binding.addRequirement.hide();
                } else {
                    binding.addRequirement.show();
                }
            });
//            binding.requirementsRecycler.addOnScrollListener(new RecyclerView.OnScrollListener() {
//                @Override
//                public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
//                    super.onScrolled(recyclerView, dx, dy);
//                    if (dy > 0 && binding.addRequirement.getVisibility() == View.VISIBLE) {
//                        binding.addRequirement.hide();
//                    } else if (dy < 0 && binding.addRequirement.getVisibility() != View.VISIBLE) {
//                        binding.addRequirement.show();
//                    }
//                }
//            });
        }
        //
        binding.addRequirement.setOnClickListener(v ->
                startActivity(new Intent(requireContext(), RequirementManagementActivity.class))
        );
        //
        docIds = OrphanagesFragment.getDocumentsIds();
        //
        db = FirebaseFirestore.getInstance();
        //
        binding.requirementsRecycler.setHasFixedSize(true);
        //
        requirements = new ArrayList<>();
        //
        binding.swipeToRefresh.setOnRefreshListener(this::getData);
        //
        getData();

    }

    private void getData() {
        length = 0;
        requirements.clear();
        if (!docIds.isEmpty()) {
            new Handler().post(() -> {
                ProgressManager.showProgress(requireContext());
                for (String docId : docIds) {
                    fetchingRequirementsData(docId);
                }
            });
        } else {
            binding.swipeToRefresh.setRefreshing(false);

        }
    }

    private void fetchingRequirementsData(String documentId) {
        if (NetworkManager.networkNotAvailable(requireContext())) {
            binding.swipeToRefresh.setRefreshing(false);
            ProgressManager.dismissProgress();
            return;
        }
        db.collection("root").document(documentId)
                .collection("req")
                .get().addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        QuerySnapshot query = task.getResult();
                        for (DocumentSnapshot snapshot : query.getDocuments()) {
                            requirements.add(new Requirement(
                                    snapshot.getString("orphanageName")
                                    , snapshot.getString("requirementDate")
                                    , snapshot.getString("description")
                                    , snapshot.getString("email")
                                    , snapshot.getString("phone")));
                        }
                        //
                        binding.swipeToRefresh.setRefreshing(false);
                    } else {
                        Log.println(Log.INFO, "fireStore", Objects.requireNonNull(task.getException()).toString());
                    }
                    //
                    length++;
                    if (length == docIds.size()) {
                        ProgressManager.dismissProgress();
                        Collections.sort(requirements, (requirement, t1) -> t1.getRequirementDate().compareToIgnoreCase(requirement.getRequirementDate()));
                        //
                        if (requirements.isEmpty()) {
                            Toast.makeText(requireContext(), "لا يوجد بيانات حالياً", Toast.LENGTH_LONG).show();
                        }
                        requirementRecyclerAdapter = new RequirementRecyclerAdapter(requireContext()
                                , requirements, false);
                        binding.requirementsRecycler.setAdapter(requirementRecyclerAdapter);
                    }
                });
    }
}