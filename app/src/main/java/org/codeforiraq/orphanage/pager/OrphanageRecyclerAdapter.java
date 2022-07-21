package org.codeforiraq.orphanage.pager;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import org.codeforiraq.orphanage.R;
import org.codeforiraq.orphanage.databinding.OrphanageItemBinding;

import java.util.List;

public class OrphanageRecyclerAdapter extends RecyclerView.Adapter<OrphanageHolder> {

    private final Context context;
    private final List<Orphanage> orphanages;
    private final List<String> documentsIds;

    public OrphanageRecyclerAdapter(Context context, List<Orphanage> orphanages, List<String> documentsIds) {
        this.context = context;
        this.orphanages = orphanages;
        this.documentsIds = documentsIds;
    }

    @NonNull
    @Override
    public OrphanageHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        OrphanageItemBinding itemBinding =
                DataBindingUtil.inflate(LayoutInflater.from(context), R.layout.orphanage_item, parent, false);
        return new OrphanageHolder(itemBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull OrphanageHolder holder, int position) {
        holder.setData(orphanages.get(position));
        holder.viewOrphanage(context, documentsIds.get(position));
    }

    @Override
    public int getItemCount() {
        return orphanages.size();
    }
}
