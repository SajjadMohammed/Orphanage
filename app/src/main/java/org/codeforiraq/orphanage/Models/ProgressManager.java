package org.codeforiraq.orphanage.Models;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;

import androidx.databinding.DataBindingUtil;

import org.codeforiraq.orphanage.R;
import org.codeforiraq.orphanage.databinding.ProgressLayoutBinding;

public class ProgressManager {

    private static AlertDialog dialog;

    public static void showProgress(Context context) {
        ProgressLayoutBinding progressLayoutBinding =
                DataBindingUtil.inflate(((Activity) context).getLayoutInflater(),
                        R.layout.progress_layout, null, false);
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setView(progressLayoutBinding.getRoot());
        dialog = builder.create();
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setCanceledOnTouchOutside(false);
        dialog.setCancelable(false);
        dialog.show();
    }

    public static void dismissProgress() {
        dialog.dismiss();
    }
}