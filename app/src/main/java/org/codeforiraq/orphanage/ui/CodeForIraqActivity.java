package org.codeforiraq.orphanage.ui;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import org.codeforiraq.orphanage.R;
import org.codeforiraq.orphanage.databinding.ActivityCodeForIraqBinding;

public class CodeForIraqActivity extends AppCompatActivity {

    ActivityCodeForIraqBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_code_for_iraq);
        //
        binding.toolbar.setNavigationOnClickListener(v -> onBackPressed());
    }
}