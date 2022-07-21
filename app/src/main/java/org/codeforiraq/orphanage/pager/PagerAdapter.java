package org.codeforiraq.orphanage.pager;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import org.codeforiraq.orphanage.ui.OrphanagesFragment;
import org.codeforiraq.orphanage.ui.RequirementsFragment;

public class PagerAdapter extends FragmentStateAdapter {

    private final int length;

    public PagerAdapter(@NonNull FragmentActivity fragmentActivity, int length) {
        super(fragmentActivity);
        this.length = length;
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        if (position == 0)
            return new OrphanagesFragment();
        else
            return new RequirementsFragment();
    }

    @Override
    public int getItemCount() {
        return length;
    }
}