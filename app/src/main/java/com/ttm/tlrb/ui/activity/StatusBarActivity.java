package com.ttm.tlrb.ui.activity;

import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;

import androidx.annotation.ColorInt;
import androidx.annotation.ColorRes;
import androidx.annotation.Nullable;
import androidx.core.graphics.ColorUtils;

public class StatusBarActivity extends BaseActivity{
    private View mStatusBarView;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Window window = getWindow();
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {//6.0 全透明实现
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.TRANSPARENT);
            int navBarColorInt = getNavigationBarColorInt();
            if(navBarColorInt != 0) {
                window.setNavigationBarColor(navBarColorInt);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    boolean isLightNavBarMode = isLightColor(navBarColorInt);
                    int vis = window.getDecorView().getSystemUiVisibility();
                    if (isLightNavBarMode) {
                        vis |= View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR;     // 黑色
                    } else {
                        //白色
                        vis &= ~View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR;
                    }
                    window.getDecorView().setSystemUiVisibility(vis);
                }
            }
        }else if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT){//4.4 全透明状态栏
            window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }
        ViewGroup contentView = (ViewGroup) findViewById(android.R.id.content);
        mStatusBarView = new View(this);
        ViewGroup.LayoutParams lp = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                getStatusBarHeight());
        setStatusBarColor(getStatusBarColorInt());
        contentView.addView(mStatusBarView, lp);
    }

    public void setStatusBarColor(@ColorInt int colorInt){
        if(colorInt != 0 && mStatusBarView != null){
            mStatusBarView.setBackgroundColor(colorInt);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                boolean isLightStatusBarMode = isLightColor(colorInt);
                setStatusBarTextMode(!isLightStatusBarMode);
            }
        }
    }

    private @ColorInt int getStatusBarColorInt(){
        int colorRes = getStatusBarColor();
        if(colorRes != 0){
            return getResources().getColor(colorRes);
        }
        return 0;
    }

    /**
     * 获取状态栏高度
     *
     * @return 状态栏高度
     */
    private int getStatusBarHeight() {
        try {
            // 获得状态栏高度
            int resourceId = getResources().getIdentifier("status_bar_height", "dimen", "android");
            return getResources().getDimensionPixelSize(resourceId);
        }catch (Exception e){
            return 0;
        }
    }

    protected @ColorRes int getStatusBarColor(){
        return 0;
    }

    protected @ColorRes int getNavigationBarColor(){
        return 0;
    }

    private @ColorInt int getNavigationBarColorInt() {
        int colorRes = getNavigationBarColor();
        if (colorRes != 0) {
            return getResources().getColor(colorRes);
        }
        return 0;
    }

    /**
     * https://stackoverflow.com/questions/24260853/check-if-color-is-dark-or-light-in-android
     */
    private boolean isLightColor(@ColorInt int color){
        return ColorUtils.calculateLuminance(color) >= 0.5;
    }
}
