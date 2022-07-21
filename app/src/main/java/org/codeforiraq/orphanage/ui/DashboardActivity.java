package org.codeforiraq.orphanage.ui;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import org.codeforiraq.orphanage.R;
import org.codeforiraq.orphanage.databinding.ActivityDashboardBinding;

public class DashboardActivity extends AppCompatActivity {

    ActivityDashboardBinding dashboardBinding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        dashboardBinding = DataBindingUtil.setContentView(this, R.layout.activity_dashboard);
        //
        dashboardBinding.toolbar.setNavigationOnClickListener(v -> onBackPressed());
        //
        dashboardBinding.orphanageDetails.setOnClickListener(view ->
                startActivity(new Intent(this, OrphanageManagementActivity.class)));
        //
        dashboardBinding.orphanageRequirements.setOnClickListener(view ->
                startActivity(new Intent(this, RequirementsActivity.class)));
    }
}