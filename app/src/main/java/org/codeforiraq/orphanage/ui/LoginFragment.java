package org.codeforiraq.orphanage.ui;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.DialogFragment;

import com.google.firebase.auth.FirebaseAuth;

import org.codeforiraq.orphanage.Models.PreferenceManager;
import org.codeforiraq.orphanage.R;
import org.codeforiraq.orphanage.databinding.LoginFragmentBinding;
import org.codeforiraq.orphanage.databinding.ResetPasswordDialogBinding;

import java.util.Objects;

public class LoginFragment extends DialogFragment {

    LoginFragmentBinding loginFragmentBinding;
    String email, password;
    private FirebaseAuth auth;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        loginFragmentBinding = DataBindingUtil.inflate(getLayoutInflater(),
                R.layout.login_fragment, container, false);
        //
        PreferenceManager.initialize(requireContext());
        //
        auth = FirebaseAuth.getInstance();
        //
        return loginFragmentBinding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        Objects.requireNonNull(getDialog()).getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        //
        loginFragmentBinding.logIn.setOnClickListener(view1 -> {
            email = loginFragmentBinding.email.getText().toString();
            password = Objects.requireNonNull(loginFragmentBinding.password.getText()).toString();
            //
            if (email.equals("") || password.equals("")) {
                Toast.makeText(getContext(), "رجاءاً قم بملئ الحقول المطلوبة", Toast.LENGTH_LONG).show();
            } else {
                if (isNetworkAvailable()) {
                    logIn(email, password);
                } else {
                    Toast.makeText(getContext(), "تأكد من اتصالك بشبكة الانترنت", Toast.LENGTH_LONG).show();
                }
            }
        });
        //
        loginFragmentBinding.forgotPassword.setOnClickListener(view1 -> viewRestDialog());
    }

    private void viewRestDialog() {
        ResetPasswordDialogBinding restBinding = DataBindingUtil.inflate(LayoutInflater.from(requireContext()),
                R.layout.reset_password_dialog, null, false);
        //
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setView(restBinding.getRoot());
        //
        AlertDialog dialog = builder.create();
        //
        restBinding.ok.setOnClickListener(view -> {
            if (restBinding.orphanageEmail.getText().toString().equals("")) {
                Toast.makeText(requireContext(), "رجاءاً قم بكتابة البريد الالكتروني", Toast.LENGTH_LONG).show();
                return;
            }
            auth.sendPasswordResetEmail(restBinding.orphanageEmail.getText().toString().trim())
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            Toast.makeText(requireContext(), "تم ارسال الرابط بنجاح", Toast.LENGTH_LONG).show();
                            dialog.dismiss();
                        }
                    });
        });
        dialog.show();
    }

    private void logIn(String email, String password) {
        auth.signInWithEmailAndPassword(email, password).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                // Sign in success, update UI with the signed-in user's information
                PreferenceManager.saveLogInDetails(email, password);
                //
                requireContext().startActivity(new Intent(getContext(), MainActivity.class));
            } else {
                // If sign in fails, display a message to the user.
                Toast.makeText(requireContext(), "فشل تسجيل الدخول، حاول مرة اخرى",
                        Toast.LENGTH_LONG).show();
                dismiss();
            }
        });
    }

    private boolean isNetworkAvailable() {
        // initialize connectivity manager
        ConnectivityManager connectivityManager = (ConnectivityManager)
                requireContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        return networkInfo != null && networkInfo.isConnectedOrConnecting();
    }
}