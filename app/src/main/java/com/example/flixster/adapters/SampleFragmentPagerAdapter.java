package com.example.flixster.adapters;

import android.content.Context;
import android.util.Log;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.example.flixster.MainActivity;
import com.example.flixster.MovieDetailsActivity;
import com.example.flixster.PageFragment;
import com.example.flixster.RelatedFragment;

public class SampleFragmentPagerAdapter extends FragmentPagerAdapter {
    int PAGE_COUNT;
    private String tabTitles[];
    private Context context;

    public SampleFragmentPagerAdapter(FragmentManager fm, Context context) {
        super(fm);
        this.context = context;
        if (context instanceof MainActivity) {
            PAGE_COUNT = 3;
            tabTitles = new String[] { "Now Playing", "Top Rated", "Upcoming" };
        } else {
            PAGE_COUNT = 2;
            tabTitles = new String[] {"Movie Details", "Similar Movies"};
        }
    }

    @Override
    public int getCount() {
        return PAGE_COUNT;
    }

    @Override
    public Fragment getItem(int position) {
        if (context instanceof MovieDetailsActivity && position == 0) {
            return PageFragment.newInstance(position + 1);
        } else if (context instanceof MovieDetailsActivity && position == 1) {
            return RelatedFragment.newInstance(position + 1);
        } else {
            return PageFragment.newInstance(position + 1);
        }

    }

    @Override
    public CharSequence getPageTitle(int position) {
        // Generate title based on item position
        return tabTitles[position];
    }
}
