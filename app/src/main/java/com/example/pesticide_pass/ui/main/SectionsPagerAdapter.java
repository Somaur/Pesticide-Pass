package com.example.pesticide_pass.ui.main;

import static com.example.pesticide_pass.running_state.RunningState.logged_in;

import android.content.Context;

import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.example.pesticide_pass.LocaleModelsFragment;
import com.example.pesticide_pass.NeedLoginAlertFragment;
import com.example.pesticide_pass.R;
import com.example.pesticide_pass.RemoteModelsFragment;

/**
 * A [FragmentPagerAdapter] that returns a fragment corresponding to
 * one of the sections/tabs/pages.
 */
public class SectionsPagerAdapter extends FragmentPagerAdapter {

    @StringRes
    private static final int[]   TAB_TITLES = new int[]{R.string.tab_text_1, R.string.tab_text_2};
    private final        Context mContext;

    public SectionsPagerAdapter(Context context, FragmentManager fm) {
        super(fm);
        mContext = context;
    }

    @Override
    public Fragment getItem(int position) {
        Fragment fragment;
        if (position == 0) {
            fragment = LocaleModelsFragment.newInstance("AAA", "[BBB]");
        }
        else {
            if (logged_in) fragment = RemoteModelsFragment.newInstance("AAA", "[BBB]");
            else fragment = NeedLoginAlertFragment.newInstance("AAA", "[BBB]");
        }
        return fragment;
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        return mContext.getResources().getString(TAB_TITLES[position]);
    }

    @Override
    public int getCount() {
        // Show 2 total pages.
        return 2;
    }
}