package com.carista.ui.main.fragments;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.adapter.FragmentStateAdapter;

public class UserViewPagerAdapter extends FragmentStateAdapter {
    private static final int CARD_ITEM_SIZE = 3;

    public UserViewPagerAdapter(@NonNull UserFragment fragmentActivity) {
        super(fragmentActivity);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position) {
            case 0:
                return new UserPostsFragment();
            case 1:
                return new UserPostsFragment();
            case 2:
                return new UserSettingsFragment();
        }
        return new UserPostsFragment();
    }

    @Override
    public int getItemCount() {
        return CARD_ITEM_SIZE;
    }
}