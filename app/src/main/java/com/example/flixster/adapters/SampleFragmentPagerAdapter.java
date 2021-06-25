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

    // number of tabs in his PageAdapter
    int PAGE_COUNT;
    // titles of each tab
    private String tabTitles[];
    private Context context;

    public SampleFragmentPagerAdapter(FragmentManager fm, Context context) {
        super(fm);
        this.context = context;

        // appropriately sets page count and page titles based on context
        if (context instanceof MainActivity) {
            PAGE_COUNT = 3;
            tabTitles = new String[] { "Now Playing", "Top Rated", "Upcoming" };
        } else {
            PAGE_COUNT = 2;
            tabTitles = new String[] {"Movie Details", "Similar Movies"};
        }
    }

    // returns page count
    @Override
    public int getCount() {
        return PAGE_COUNT;
    }

    // returns correct fragment based on activity type and tab position
    @Override
    public Fragment getItem(int position) {
        if (context instanceof MovieDetailsActivity && position == 0) {
            return PageFragment.newInstance(position + 1);
        } else if (context instanceof MovieDetailsActivity && position == 1) {
            return RelatedFragment.newInstance(position + 1);
        } else {
            return RelatedFragment.newInstance(position + 1);
        }

    }

    // retrieves the titles of each page
    @Override
    public CharSequence getPageTitle(int position) {
        // Generate title based on item position
        return tabTitles[position];
    }
}
