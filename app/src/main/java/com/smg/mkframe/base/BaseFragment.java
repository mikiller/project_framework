package com.smg.mkframe.base;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * Created by Mikiller on 2017/3/15.
 */

public abstract class BaseFragment extends Fragment {
    protected final String TAG = this.getClass().getSimpleName();
    protected Unbinder unbinder;
    protected int layoutRes;
    protected boolean hasInit = false, isVisible = false;

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if((isVisible = isVisibleToUser) == true && hasInit){
            initData();
            hasInit = false;
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        setLayoutRes();
        View view = inflater.inflate(layoutRes, container, false);
        unbinder = ButterKnife.bind(this, view);

        initView();
        if(isVisible){
            initData();
        }
        hasInit = true;
        return view;
    }

    protected abstract void setLayoutRes();
    protected abstract void initView();
    protected abstract void initData();
    public abstract void fragmentCallback(int type, Intent data);

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
        hasInit = false;
    }
}
