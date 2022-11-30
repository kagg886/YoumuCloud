package com.kagg886.youmucloud.core.adaper;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import java.util.ArrayList;

public class HomePagerAdapter extends FragmentPagerAdapter {
    private ArrayList<Structure> views;

    public CharSequence getPageTitle(int i) {
        return this.views.get(i).name;
    }

    public HomePagerAdapter(FragmentManager fragmentManager) {
        super(fragmentManager, FragmentPagerAdapter.BEHAVIOR_SET_USER_VISIBLE_HINT);
    }

    public void setViews(ArrayList<Structure> arrayList) {
        this.views = arrayList;
    }

    public Fragment getItem(int i) {
        return this.views.get(i).view;
    }

    public int getCount() {
        return this.views.size();
    }

    public static class Structure {
        public String name;
        public Fragment view;

        public Structure(String str, Fragment fragment) {
            this.name = str;
            this.view = fragment;
        }
    }
}
