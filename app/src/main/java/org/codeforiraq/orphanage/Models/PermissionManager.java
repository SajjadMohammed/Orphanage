package org.codeforiraq.orphanage.Models;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;

import androidx.core.app.ActivityCompat;

public class PermissionManager {

    private static final String readPermission = Manifest.permission.READ_EXTERNAL_STORAGE;
    private static final String callPermission = Manifest.permission.CALL_PHONE;

    public static boolean shouldAskPermission() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.M;
    }

    public static boolean canReadStorage(Context context) {
        return ActivityCompat.checkSelfPermission(context, readPermission) == PackageManager.PERMISSION_GRANTED;
    }

    public static boolean canMakeACall(Context context) {
        return ActivityCompat.checkSelfPermission(context, callPermission) == PackageManager.PERMISSION_GRANTED;
    }
}