package com.smg.mkframe.base;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * Created by Mikiller on 2017/3/15.
 */

public abstract class BaseFragment extends Fragment {
    protected Unbinder unbinder;
    protected int layoutRes;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        setLayoutRes();
        View view = inflater.inflate(layoutRes, container, false);
        unbinder = ButterKnife.bind(this, view);

        initView();
        return view;
    }

    protected abstract void setLayoutRes();
    protected abstract void initView();

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }
}
