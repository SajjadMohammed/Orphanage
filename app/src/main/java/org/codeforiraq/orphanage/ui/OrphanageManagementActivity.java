package org.codeforiraq.orphanage.ui;

import android.Manifest;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import org.codeforiraq.orphanage.Models.NetworkManager;
import org.codeforiraq.orphanage.Models.PermissionManager;
import org.codeforiraq.orphanage.Models.PreferenceManager;
import org.codeforiraq.orphanage.Models.ProgressManager;
import org.codeforiraq.orphanage.R;
import org.codeforiraq.orphanage.databinding.ActivityOrphanageManagementBinding;
import org.codeforiraq.orphanage.pager.Orphanage;

import java.io.ByteArrayOutputStream;
import java.util.Objects;

public class OrphanageManagementActivity extends AppCompatActivity {

    ActivityOrphanageManagementBinding managementBinding;
    FirebaseFirestore db;
    FirebaseStorage storage;
    String orphanageName, orphanageOwner, orphanagePhone, orphanageAddress,
            orphanageDescription, photoUri;
    //
    ActivityResultLauncher<String> launchForContent =
            registerForActivityResult(new ActivityResultContracts.GetContent(), uri -> {
                // Handle the returned Uri
                managementBinding.orphanagePhoto.setImageURI(uri);
                managementBinding.progressCircular.setVisibility(View.GONE);
                managementBinding.thumbnail.setVisibility(View.GONE);
            });
    ActivityResultLauncher<String> launcher =
            registerForActivityResult(new ActivityResultContracts.RequestPermission(), granted -> {
                if (granted) {
                    getImage();
                }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        managementBinding = DataBindingUtil.setContentView(this, R.layout.activity_orphanage_management);
        //
        PreferenceManager.initialize(this);
        db = FirebaseFirestore.getInstance();
        storage = FirebaseStorage.getInstance();
        //
        managementBinding.toolbar.setNavigationOnClickListener(view -> onBackPressed());
        //
        new Handler().post(() -> {
            ProgressManager.showProgress(this);
            fetchingOrphanageData();
        });
        //
        managementBinding.selectPhoto.setOnClickListener(view -> {
            if (PermissionManager.shouldAskPermission()) {
                if (PermissionManager.canReadStorage(this)) {
                    getImage();
                } else {
                    launcher.launch(Manifest.permission.READ_EXTERNAL_STORAGE);
                }
            } else {
                getImage();
            }
        });
        //
        managementBinding.save.setOnClickListener(view -> {
            if (areTheFieldsEmpty()) return;
            //
            if (NetworkManager.networkNotAvailable(this)) {
                ProgressManager.dismissProgress();
                return;
            }
            //
            //
            ProgressManager.showProgress(this);
            //
            StorageReference reference = storage.getReference().child("images/" + orphanageOwner + ".jpg");
            if (getPictureBytes() == null) {
                Toast.makeText(this, "يجب اضافة صورة للدار", Toast.LENGTH_LONG).show();
                ProgressManager.dismissProgress();
                return;
            }
            UploadTask uploadTask = reference.putBytes(getPictureBytes());
            uploadTask.continueWithTask(task -> {
                        if (task.isSuccessful())
                            return reference.getDownloadUrl();
                        return null;
                    })
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            photoUri = task.getResult().toString();
                            Orphanage orphanage = new Orphanage(orphanageOwner, orphanageAddress
                                    , orphanageDescription, orphanagePhone, orphanageName, photoUri);
                            //adding orphanage manager details
                            db.collection("root")
                                    .document(PreferenceManager.getOrphanageEMail())
                                    .set(orphanage)
                                    .addOnCompleteListener(task1 -> {
                                        if (task1.isSuccessful()) {
                                            Toast.makeText(this, "عملية الحفظ تمت بنجاح", Toast.LENGTH_LONG).show();
                                        } else {
                                            ProgressManager.dismissProgress();
                                            Log.println(Log.INFO, "task", Objects.requireNonNull(task1.getException()).toString());
                                        }
                                        ProgressManager.dismissProgress();
                                    });
                        } else {
                            ProgressManager.dismissProgress();
                            Log.println(Log.INFO, "task", Objects.requireNonNull(task.getException()).toString());
                        }
                    });
        });
    }

    private void getImage() {
        launchForContent.launch("image/*");
    }

    private byte[] getPictureBytes() {
        if (managementBinding.orphanagePhoto.getDrawable() == null) return null;
        Bitmap bitmap = ((BitmapDrawable) (managementBinding.orphanagePhoto.getDrawable())).getBitmap();
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();

        Matrix matrix = new Matrix();
        float scaleWidth = ((float) 480) / width;
        float scaleHeight = ((float) 480) / height;
        matrix.postScale(scaleWidth, scaleHeight);

        Bitmap resizedBitmap = Bitmap.createBitmap(bitmap, 0, 0, width, height, matrix, true);
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        resizedBitmap.compress(Bitmap.CompressFormat.PNG, 70, outputStream);

        return outputStream.toByteArray();
    }

    private void fetchingOrphanageData() {
        if (NetworkManager.networkNotAvailable(this)) return;
        db.collection("root")
                .document(PreferenceManager.getOrphanageEMail()).get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        DocumentSnapshot snapshot = task.getResult();
                        managementBinding.orphanageName.setText(snapshot.getString("orphanageName"));
                        managementBinding.orphanageOwner.setText(snapshot.getString("fullName"));
                        managementBinding.orphanageAddress.setText(snapshot.getString("address"));
                        managementBinding.orphanagePhone.setText(snapshot.getString("phone"));
                        managementBinding.description.setText(snapshot.getString("description"));
                        String photoUri = snapshot.getString("photoUri");
                        if (photoUri != null) {
                            if (!photoUri.isEmpty()) {
                                Uri uri = Uri.parse(photoUri);
                                managementBinding.progressCircular.setVisibility(View.VISIBLE);
                                Glide.with(this).load(uri)
                                        .listener(new RequestListener<Drawable>() {
                                            @Override
                                            public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                                                return false;
                                            }

                                            @Override
                                            public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                                                managementBinding.progressCircular.setVisibility(View.GONE);
                                                managementBinding.thumbnail.setVisibility(View.GONE);
                                                return false;
                                            }
                                        })
                                        .into(managementBinding.orphanagePhoto);
                            } else {
                                managementBinding.progressCircular.setVisibility(View.GONE);
                            }
                        }
                    }
                    ProgressManager.dismissProgress();
                });
    }

    private boolean areTheFieldsEmpty() {
        orphanageName = managementBinding.orphanageName.getText().toString();
        orphanageOwner = managementBinding.orphanageOwner.getText().toString();
        orphanagePhone = managementBinding.orphanagePhone.getText().toString();
        orphanageAddress = managementBinding.orphanageAddress.getText().toString();
        orphanageDescription = managementBinding.description.getText().toString();
        //
        if (orphanageName.isEmpty() || orphanageOwner.isEmpty()
                || orphanagePhone.isEmpty() || orphanageAddress.isEmpty()) {
            Toast.makeText(this, "رجاءاً قم بملئ جميع الحقول المطلوبة", Toast.LENGTH_LONG).show();
            return true;
        }
        return false;
    }

}