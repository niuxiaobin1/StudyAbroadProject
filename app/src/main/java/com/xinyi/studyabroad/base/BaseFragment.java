package com.xinyi.studyabroad.base;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

/**
 * Created by Administrator on 2018/4/16.
 */

public abstract class BaseFragment extends Fragment {

    private Toast toast;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Log.e("nxb","onCreateView"+getClass().getCanonicalName());
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.e("nxb","onCreate"+getClass().getCanonicalName());
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.e("nxb","onPause"+getClass().getCanonicalName());
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.e("nxb","onResume"+getClass().getCanonicalName());
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        Log.e("nxb","onHiddenChanged"+getClass().getCanonicalName());
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initViews();
        initDatas();
    }

    protected void showToast(@StringRes int stringRes){
        if (toast==null){

            toast=Toast.makeText(getActivity(),stringRes,Toast.LENGTH_SHORT);
        }else{
            toast.setText(stringRes);
        }
        toast.show();
    }

    public abstract void initViews();

    public abstract void initDatas();
}
