package com.ttm.tlrb.utils;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.Toast;

public class ToastUtil {

    public static void showToast(final Context mContext, final int resId) {
        if(Looper.myLooper() != Looper.getMainLooper()){
            new Handler(Looper.getMainLooper()).post(new Runnable() {

                @Override
                public void run() {
                    MyToast.showShort(mContext,resId);
                }
            });
        }else{
            MyToast.showShort(mContext, resId);
        }
    }

    public static void showToast(final Context mContext,final  String text) {
        if(Looper.myLooper() != Looper.getMainLooper()){
            new Handler(Looper.getMainLooper()).post(new Runnable() {

                @Override
                public void run() {
                    MyToast.showShort(mContext, text);
                }
            });
        }else{
            MyToast.showShort(mContext, text);
        }
    }

    public static void showLongToast(final Context mContext,final  int resId) {
        if(Looper.myLooper() != Looper.getMainLooper()){
            new Handler(Looper.getMainLooper()).post(new Runnable() {

                @Override
                public void run() {
                    MyToast.showLong(mContext,resId);
                }
            });
        }else{
            MyToast.showLong(mContext, resId);
        }
    }

    public static void showLongToast(final Context mContext, final String text) {
        if(Looper.myLooper() != Looper.getMainLooper()){
            new Handler(Looper.getMainLooper()).post(new Runnable() {

                @Override
                public void run() {
                    MyToast.showLong(mContext, text);
                }
            });
        }else{
            MyToast.showLong(mContext, text);
        }
    }

    public static void showViewToast(final Context mContext, final View view, final int gravity){
        if(Looper.myLooper() != Looper.getMainLooper()){
            new Handler(Looper.getMainLooper()).post(new Runnable() {

                @Override
                public void run() {
                    makeShortToast(mContext, view, gravity, 0, 0).show();
                }
            });
        }else{
            makeShortToast(mContext, view, gravity, 0, 0).show();
        }
    }

    private static Toast makeShortToast(final Context mContext, final View view, final int gravity, int x, int y){
        Toast toast = new Toast(mContext);
        toast.setView(view);
        toast.setGravity(gravity, x, y);
        toast.setDuration(Toast.LENGTH_SHORT);
        return  toast;
    }

}
