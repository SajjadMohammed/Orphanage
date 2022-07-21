package org.codeforiraq.orphanage.pager;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.widget.PopupMenu;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.FirebaseFirestore;

import org.codeforiraq.orphanage.Models.PreferenceManager;
import org.codeforiraq.orphanage.Models.ProgressManager;
import org.codeforiraq.orphanage.Models.RequirementDocIds;
import org.codeforiraq.orphanage.R;
import org.codeforiraq.orphanage.databinding.RequirementItemBinding;
import org.codeforiraq.orphanage.ui.OrphanageActivity;
import org.codeforiraq.orphanage.ui.RequirementsActivity;
import org.codeforiraq.orphanage.ui.RequirementsUpdate;

public class RequirementHolder extends RecyclerView.ViewHolder {

    RequirementItemBinding itemBinding;
    FirebaseFirestore db;

    public RequirementHolder(@NonNull RequirementItemBinding itemBinding) {
        super(itemBinding.getRoot());
        this.itemBinding = itemBinding;
        //
        db = FirebaseFirestore.getInstance();
        //
        PreferenceManager.initialize(itemBinding.getRoot().getContext());
    }

    public void setData(Requirement requirement) {
        itemBinding.setRequirement(requirement);
    }

    public void sendAnEMail(String email) {
        itemBinding.mail.setOnClickListener(view -> sendEMailToOrphanage(email));
    }

    private void sendEMailToOrphanage(String email) {
        String[] TO = {email};
        String[] CC = {""};
        Intent emailIntent = new Intent(Intent.ACTION_SENDTO);
        emailIntent.setData(Uri.parse("mailto:"));
        emailIntent.putExtra(Intent.EXTRA_EMAIL, TO);
        emailIntent.putExtra(Intent.EXTRA_CC, CC);
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, "تبرع");
        emailIntent.setPackage("com.google.android.gm");

        if (emailIntent.resolveActivity(itemBinding.getRoot().getContext().getPackageManager()) != null) {
            itemBinding.getRoot().getContext().startActivity(emailIntent);
        }
    }

    public void makeACall(String phone) {
        itemBinding.call.setOnClickListener(view -> {
            Intent callIntent = new Intent(Intent.ACTION_DIAL);
            callIntent.setData(Uri.parse("tel:" + phone));
            itemBinding.getRoot().getContext().startActivity(callIntent);
        });
    }

    public void share(Requirement requirement) {
        String content = requirement.getOrphanageName() + "\n\n" +
                requirement.getDescription() + "\n\n" +
                "للتواصل:\n" +
                requirement.getEmail() + "\n" +
                requirement.getPhone();
        itemBinding.share.setOnClickListener(view -> {
            Intent sendIntent = new Intent();
            sendIntent.setAction(Intent.ACTION_SEND);
            sendIntent.putExtra(Intent.EXTRA_TEXT, content);
            sendIntent.setType("text/plain");

            Intent shareIntent = Intent.createChooser(sendIntent, null);
            view.getContext().startActivity(shareIntent);
        });
    }

    public void manageRequirement(Context context) {
//        String docId = RequirementsActivity.reqDocIds.get(getAdapterPosition());
        String docId = RequirementDocIds.reqDocIds.get(getAdapterPosition());
        //
        itemBinding.more.setOnClickListener(view -> {
            PopupMenu popupMenu = new PopupMenu(context, itemBinding.more);
            // Inflating popup menu from popup_menu.xml file
            popupMenu.getMenuInflater().inflate(R.menu.update_delete_menu, popupMenu.getMenu());
            popupMenu.setOnMenuItemClickListener(menuItem -> {
                if (menuItem.getItemId() == R.id.update) {
                    context.startActivity(
                            new Intent(context, RequirementsUpdate.class)
                                    .putExtra("docId", docId));
                } else {
                    ProgressManager.showProgress(context);
                    db.collection("الايتام").document(PreferenceManager.getOrphanageEMail())
                            .collection("requirements")
                            .document(docId).delete()
                            .addOnCompleteListener(task -> {
                                if (task.isSuccessful()) {
                                    ProgressManager.dismissProgress();
                                    try {
                                        RequirementsActivity.requirementRecyclerAdapter.get().removeItem(getAdapterPosition());
                                    } catch (Exception ignore) {
                                        OrphanageActivity.requirementRecyclerAdapter.get().removeItem(getAdapterPosition());
                                    }
                                    Toast.makeText(context, "عملية حذف البيانات تمت بنجاح", Toast.LENGTH_LONG).show();
                                }
                            });
                }
                return true;
            });
            // Showing the popup menu
            popupMenu.show();
        });

    }
}