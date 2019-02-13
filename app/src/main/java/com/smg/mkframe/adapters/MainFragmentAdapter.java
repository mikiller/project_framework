package com.smg.mkframe.adapters;

import android.content.Intent;

import com.smg.mkframe.base.BaseFragment;

import java.util.ArrayList;
import java.util.List;

import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

/**
 * Created by Mikiller on 2017/3/15.
 */

public class MainFragmentAdapter extends FragmentStatePagerAdapter {

    private List<BaseFragment> fragments;
    private String[] titles = new String[]{};

    public MainFragmentAdapter(FragmentManager fm) {
        super(fm);
        fragments = new ArrayList<>();
    }

    @Override
    public BaseFragment getItem(int position) {
        return fragments.get(position);
    }

    public void fragmentCallback(int pos, int type, Intent data){
        getItem(pos).fragmentCallback(type, data);
    }

    @Override
    public int getCount() {
        return fragments == null ? 0 : fragments.size();
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return titles[position];
    }
}
