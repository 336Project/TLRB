package com.ttm.tlrb.ui.fragment;

import android.app.Activity;
import android.content.Context;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

/**
 * Created by Helen on 2016/7/26.
 *
 */
public class BaseFragment extends Fragment {
    protected Context mContext;
    protected Activity mActivity;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        mContext = context;
    }

    @Override
    public void onAttach(@NonNull Activity activity) {
        super.onAttach(activity);
        mActivity = activity;
    }

    public void finish(){
        mActivity.finish();
    }
}
