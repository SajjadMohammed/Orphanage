package org.codeforiraq.orphanage.ui;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import com.google.android.material.tabs.TabLayoutMediator;
import com.google.firebase.auth.FirebaseAuth;

import org.codeforiraq.orphanage.Models.PreferenceManager;
import org.codeforiraq.orphanage.R;
import org.codeforiraq.orphanage.databinding.ActivityMainBinding;
import org.codeforiraq.orphanage.databinding.ConfirmLogoutBinding;
import org.codeforiraq.orphanage.databinding.RequestDialogBinding;
import org.codeforiraq.orphanage.databinding.RequestSentDialogBinding;
import org.codeforiraq.orphanage.pager.PagerAdapter;

import java.util.Objects;

public class MainActivity extends AppCompatActivity {

    ActivityMainBinding mainBinding;
    FirebaseAuth auth;
    boolean mailSent;
    //
    String[] tabsTitle = new String[]{"دور الايتام", "احتياجات دور الايتام"};
    PagerAdapter pagerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mainBinding = DataBindingUtil.setContentView(this, R.layout.activity_main);
        //
        PreferenceManager.initialize(this);
        //
        auth = FirebaseAuth.getInstance();
        //
        pagerAdapter = new PagerAdapter(this, tabsTitle.length);
        mainBinding.pager2.setAdapter(pagerAdapter);
        new TabLayoutMediator(mainBinding.tabsLayout, mainBinding.pager2,
                (tab, position) -> tab.setText(tabsTitle[position])).attach();
        //
        checkIfLoggedIn();
        //
        mainBinding.bottomNavigation.setOnItemSelectedListener(item -> {
            if (item.getItemId() == R.id.signInItem) {
                loginFragment();
                return true;
            } else if (item.getItemId() == R.id.signUpItem) {
                signUpRequestMessage();
                return true;
            } else if (item.getItemId() == R.id.dashboardItem) {
                startActivity(new Intent(this, DashboardActivity.class));
                return true;
            } else if (item.getItemId() == R.id.logOutItem) {
                logOut();
                return true;
            } else {
                startActivity(new Intent(this, SettingsActivity.class));
                return true;
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        mainBinding.bottomNavigation.getMenu().findItem(R.id.dashboardItem).setChecked(false);
        mainBinding.bottomNavigation.getMenu().findItem(R.id.logOutItem).setChecked(false);
        mainBinding.bottomNavigation.getMenu().findItem(R.id.signInItem).setChecked(false);
        mainBinding.bottomNavigation.getMenu().findItem(R.id.signUpItem).setChecked(false);
    }

    private void logOut() {
        ConfirmLogoutBinding confirmLogoutBinding =
                DataBindingUtil.inflate(getLayoutInflater(), R.layout.confirm_logout, null, false);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(confirmLogoutBinding.getRoot());
        //
        AlertDialog dialog = builder.create();
        //
        confirmLogoutBinding.ok.setOnClickListener(view -> {
            auth.signOut();
            PreferenceManager.saveLogOutDetails();
            dialog.dismiss();
            Toast.makeText(this, "تم تسجيل الخروج بنجاح", Toast.LENGTH_LONG).show();
            finish();
            startActivity(getIntent());
            overridePendingTransition(0, 0);
            Objects.requireNonNull(mainBinding.tabsLayout.getTabAt(0)).select();
        });
        //
        confirmLogoutBinding.cancel.setOnClickListener(view -> dialog.dismiss());
        dialog.show();
    }

    private void checkIfLoggedIn() {
        if (PreferenceManager.isLoggedIn()) {
            mainBinding.bottomNavigation.getMenu().findItem(R.id.dashboardItem).setVisible(true);
            mainBinding.bottomNavigation.getMenu().findItem(R.id.logOutItem).setVisible(true);
            //
            mainBinding.bottomNavigation.getMenu().findItem(R.id.signInItem).setVisible(false);
            mainBinding.bottomNavigation.getMenu().findItem(R.id.signUpItem).setVisible(false);
            mainBinding.bottomNavigation.getMenu().findItem(R.id.signUpItem).setChecked(true);
        } else {
            mainBinding.bottomNavigation.getMenu().findItem(R.id.dashboardItem).setVisible(false);
            mainBinding.bottomNavigation.getMenu().findItem(R.id.logOutItem).setVisible(false);
            mainBinding.bottomNavigation.getMenu().findItem(R.id.logOutItem).setChecked(true);
            //
            mainBinding.bottomNavigation.getMenu().findItem(R.id.signInItem).setVisible(true);
            mainBinding.bottomNavigation.getMenu().findItem(R.id.signUpItem).setVisible(true);
        }
    }

    private void signUpRequestMessage() {
        RequestDialogBinding dialogBinding = DataBindingUtil.inflate(getLayoutInflater(), R.layout.request_dialog,
                null, false);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(dialogBinding.getRoot());
        //
        AlertDialog dialog = builder.create();
        //
        dialogBinding.request.setOnClickListener(v -> {
            String fullName = dialogBinding.fullName.getText().toString();
            String homeName = dialogBinding.homeName.getText().toString();
            String phone = dialogBinding.phone.getText().toString();
            if (phone.equals("") || fullName.equals("") || homeName.equals("")) {
                Toast.makeText(this, "رجاءاً قم بملئ الحقول المطلوبة", Toast.LENGTH_LONG).show();
                return;
            }
            String[] TO = {"your_mail"};
            String[] CC = {""};
            Intent emailIntent = new Intent(Intent.ACTION_SENDTO);
            emailIntent.setData(Uri.parse("mailto:"));
            emailIntent.putExtra(Intent.EXTRA_EMAIL, TO);
            emailIntent.putExtra(Intent.EXTRA_CC, CC);
            emailIntent.putExtra(Intent.EXTRA_SUBJECT, "تقديم طلب");
            emailIntent.putExtra(Intent.EXTRA_TEXT, "اني صاحب الدار " + fullName + " ارجوا انشاء حساب لي لإدارة دار الايتام " + homeName + " \n رقم الهاتف:  " + phone);
            emailIntent.setPackage("com.google.android.gm");

            if (emailIntent.resolveActivity(getPackageManager()) != null) {
                startActivity(emailIntent);
                dialog.dismiss();
                mailSent = true;
            } else {
                mailSent = false;
                dialog.dismiss();
            }
        });
        dialog.setOnDismissListener(v -> {
                    if (mailSent)
                        successfulRequestMessage();
                }
        );
        //
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.show();

    }

    private void successfulRequestMessage() {
        RequestSentDialogBinding requestBinding = DataBindingUtil.inflate(getLayoutInflater(), R.layout.request_sent_dialog,
                null, false);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(requestBinding.getRoot());
        //
        AlertDialog dialog = builder.create();
        requestBinding.ok.setOnClickListener(v -> dialog.dismiss());
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.show();
    }

    private void loginFragment() {
        LoginFragment loginFragment = new LoginFragment();
        loginFragment.show(getSupportFragmentManager(), null);
    }
}