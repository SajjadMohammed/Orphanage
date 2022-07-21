package org.codeforiraq.orphanage.Models;

import android.content.Context;
import android.content.SharedPreferences;

public class PreferenceManager {
    private static SharedPreferences preferences;
    private static SharedPreferences.Editor editor;

    public static void initialize(Context context) {
        preferences = context.getSharedPreferences(context.getPackageName(), Context.MODE_PRIVATE);
    }

    public static boolean isLoggedIn() {
        return preferences.getBoolean("stayLoggedIn", false);
    }

    public static void saveLogInDetails(String email, String password) {
        editor = preferences.edit();
        editor.putBoolean("stayLoggedIn", true);
        editor.putString("email", email);
        editor.putString("password", password);
        editor.commit();
    }

    public static void saveLogOutDetails() {
        editor = preferences.edit();
        editor.putBoolean("stayLoggedIn", false);
        editor.putString("email", "");
        editor.putString("password", "");
        editor.commit();
    }

    public static String getOrphanageEMail() {
        return preferences.getString("email", "");
    }
}