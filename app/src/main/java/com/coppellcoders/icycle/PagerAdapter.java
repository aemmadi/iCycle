package com.coppellcoders.icycle;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

public class PagerAdapter extends FragmentStatePagerAdapter {
    int mNumOfTabs;

    public PagerAdapter(FragmentManager fm, int NumOfTabs) {
        super(fm);
        this.mNumOfTabs = NumOfTabs;
    }

    @Override
    public Fragment getItem(int position) {

        switch (position) {
            case 0:
                MainActivity tab2 = new MainActivity();
                return tab2;


            case 1:
                LeaderboardFragment tab1 = new LeaderboardFragment();
                return tab1;
            case 2:
                CarbonActivity tab3 = new CarbonActivity();
                return tab3;
            case 3:
                SearchFragment tab4 = new SearchFragment();
                return tab4;
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return mNumOfTabs;
    }
}
