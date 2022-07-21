package org.codeforiraq.orphanage.ui;

import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import org.codeforiraq.orphanage.Models.NetworkManager;
import org.codeforiraq.orphanage.Models.ProgressManager;
import org.codeforiraq.orphanage.R;
import org.codeforiraq.orphanage.databinding.OrphanagesFragmentBinding;
import org.codeforiraq.orphanage.pager.Orphanage;
import org.codeforiraq.orphanage.pager.OrphanageRecyclerAdapter;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class OrphanagesFragment extends Fragment {

    private static List<String> documentsIds;

    OrphanagesFragmentBinding binding;
    OrphanageRecyclerAdapter orphanageRecyclerAdapter;

    FirebaseFirestore db;

    List<Orphanage> orphanages;

    public OrphanagesFragment() {
    }

    //
    public static List<String> getDocumentsIds() {
        return documentsIds;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(getLayoutInflater(), R.layout.orphanages_fragment, container, false);

        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        //
        db = FirebaseFirestore.getInstance();
        //
        binding.orphanagesRecycler.setHasFixedSize(true);
        //
        orphanages = new ArrayList<>();
        documentsIds = new ArrayList<>();
        //
        binding.swipeToRefresh.setOnRefreshListener(this::getData);
        //
        getData();
    }

    private void getData() {
        orphanages.clear();
        documentsIds.clear();
        new Handler().post(() -> {
            ProgressManager.showProgress(requireContext());
            fetchingOrphanageData();
        });
    }

    private void fetchingOrphanageData() {
        if (NetworkManager.networkNotAvailable(requireContext())) {
            ProgressManager.dismissProgress();
            binding.swipeToRefresh.setRefreshing(false);
            return;
        }
        db.collection("root")
                .get().addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        QuerySnapshot query = task.getResult();
                        for (DocumentSnapshot snapshot : query.getDocuments()) {
                            documentsIds.add(snapshot.getId());
                            //
                            Orphanage orphanage = new Orphanage();
                            orphanage.setOrphanageName(snapshot.getString("orphanageName"));
                            orphanage.setAddress(snapshot.getString("address"));
                            orphanage.setDescription(snapshot.getString("description"));
                            orphanage.setPhotoUri(snapshot.getString("photoUri"));
                            orphanages.add(orphanage);
                        }
                        //
                        binding.swipeToRefresh.setRefreshing(false);
                    } else {
                        Log.println(Log.WARN, "fireStore", Objects.requireNonNull(task.getException()).toString());
                    }
                    //
                    ProgressManager.dismissProgress();
                    if (orphanages.isEmpty()) {
                        Toast.makeText(requireContext(), "لا يوجد بيانات حالياً", Toast.LENGTH_LONG).show();
                    }
                    orphanageRecyclerAdapter = new OrphanageRecyclerAdapter(requireContext(), orphanages, documentsIds);
                    binding.orphanagesRecycler.setAdapter(orphanageRecyclerAdapter);
                });

    }

}