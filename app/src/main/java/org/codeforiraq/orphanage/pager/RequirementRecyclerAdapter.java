package org.codeforiraq.orphanage.pager;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import org.codeforiraq.orphanage.R;
import org.codeforiraq.orphanage.databinding.RequirementItemBinding;

import java.util.List;

public class RequirementRecyclerAdapter extends RecyclerView.Adapter<RequirementHolder> {

    private final List<Requirement> requirements;
    private final Context context;
    boolean isAdmin;

    public RequirementRecyclerAdapter(Context context, List<Requirement> requirements, boolean isAdmin) {
        this.context = context;
        this.requirements = requirements;
        this.isAdmin = isAdmin;
    }

    @NonNull
    @Override
    public RequirementHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        RequirementItemBinding itemBinding =
                DataBindingUtil.inflate(LayoutInflater.from(context), R.layout.requirement_item, parent, false);
        return new RequirementHolder(itemBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull RequirementHolder holder, int position) {
        holder.setData(requirements.get(position));
        //
        if (isAdmin) {
            holder.itemBinding.call.setVisibility(View.GONE);
            holder.itemBinding.mail.setVisibility(View.GONE);
            holder.itemBinding.callTitle.setVisibility(View.GONE);
            holder.manageRequirement(context);
        } else {
            holder.itemBinding.more.setVisibility(View.GONE);
            holder.makeACall(requirements.get(position).getPhone());
            holder.sendAnEMail(requirements.get(position).getEmail());
        }
        holder.share(requirements.get(position));
        holder.itemBinding.executePendingBindings();
    }

    @Override
    public int getItemCount() {
        return requirements.size();
    }

    public void removeItem(int position) {
        requirements.remove(position);
        notifyItemRemoved(position);
    }
}