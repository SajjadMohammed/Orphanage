package org.codeforiraq.orphanage.Models;

import android.content.Context;
import android.content.Intent;

public class OurApps {

    public static void showApps(Context context) {

        Intent intent = new Intent(Intent.ACTION_VIEW, android.net.Uri.parse("https://play.google.com/store/apps/developer?id=Code+For+Iraq"));
        // we need to add this, because the activity is in a new context.
        // Otherwise the runtime will block the execution and throw an exception
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        context.startActivity(intent);
        //}
    }
}