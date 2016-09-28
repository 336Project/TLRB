package com.ttm.tlrb.ui.service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.ttm.tlrb.ui.activity.AdActivity;
import com.ttm.tlrb.utils.HLog;

/**
 * Created by Helen on 2016/9/28.
 *
 */

public class AdBroadcastReceiver extends BroadcastReceiver{
    private static final String TAG = "AdBroadcastReceiver";
    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        System.out.println(action);
        HLog.d(TAG,action);
        switch (action){
            case Intent.ACTION_PACKAGE_ADDED:
                String uri = intent.getDataString();
                HLog.d(TAG,uri);
                /*ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
                try {
                    Method method = ActivityManager.class.getMethod("forceStopPackage",String.class);
                    method.invoke(am,uri);
                } catch (NoSuchMethodException e) {
                    e.printStackTrace();
                } catch (InvocationTargetException e) {
                    e.printStackTrace();
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }*/
                Intent i = new Intent(context, AdActivity.class);
                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(i);
                break;
            case Intent.ACTION_PACKAGE_REMOVED:
                uri = intent.getDataString();
                System.out.println(uri);
                break;
        }
    }
}
