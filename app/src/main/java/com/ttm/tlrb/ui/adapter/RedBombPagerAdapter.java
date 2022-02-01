package com.ttm.tlrb.ui.adapter;


import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.fragment.app.FragmentTransaction;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Helen on 2016/4/29.
 *
 */
public class RedBombPagerAdapter extends FragmentPagerAdapter {
    private final List<Fragment> mFragments = new ArrayList<>();//添加的Fragment的集合
    private final List<String> mFragmentsTitles = new ArrayList<>();//每个Fragment对应的title的集合
    private final FragmentManager fm;

    public RedBombPagerAdapter(FragmentManager fm) {
        super(fm);
        this.fm = fm;
    }

    public void addFragment(Fragment fragment, String fragmentTitle) {
        mFragments.add(fragment);
        mFragmentsTitles.add(fragmentTitle);
    }

    public void clear(){
        if (mFragments.isEmpty()){
            return;
        }
        FragmentTransaction ft = fm.beginTransaction();
        for (Fragment fragment:mFragments) {
            ft.remove(fragment);
        }
        ft.commitNow();
        mFragments.clear();
        mFragmentsTitles.clear();
    }

    @Override
    public Fragment getItem(int position) {
        return mFragments.get(position);
    }

    @Override
    public int getCount() {
        return mFragments.size();
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return mFragmentsTitles.get(position);
    }

    @Override
    public int getItemPosition(@NonNull Object object) {
        return POSITION_NONE;
    }
}
