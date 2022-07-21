package org.codeforiraq.orphanage.pager;

import android.content.Context;
import android.content.Intent;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import org.codeforiraq.orphanage.databinding.OrphanageItemBinding;
import org.codeforiraq.orphanage.ui.OrphanageActivity;

public class OrphanageHolder extends RecyclerView.ViewHolder {

    OrphanageItemBinding itemBinding;

    public OrphanageHolder(@NonNull OrphanageItemBinding itemBinding) {
        super(itemBinding.getRoot());
        this.itemBinding = itemBinding;
    }

    public void setData(Orphanage orphanage) {
        itemBinding.setOrphanage(orphanage);
        itemBinding.executePendingBindings();
    }

    public void viewOrphanage(Context context, String docId) {
        itemBinding.getRoot().setOnClickListener(view ->
                context.startActivity(new Intent(context, OrphanageActivity.class).putExtra("docId", docId)));
    }
}