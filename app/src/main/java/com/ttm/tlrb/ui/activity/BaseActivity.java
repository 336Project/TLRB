package com.ttm.tlrb.ui.activity;

import android.os.Build;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.facebook.common.logging.FLog;
import com.ttm.tlrb.R;
import com.ttm.tlrb.view.MaterialDialog;
import com.umeng.analytics.MobclickAgent;

import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Helen on 2016/4/27.
 *
 */
public class BaseActivity extends AppCompatActivity {

    private static final Map<String,WeakReference<BaseActivity>> activitiesMap = new HashMap<>();
    private MaterialDialog mMaterialDialog;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activitiesMap.put(BaseActivity.this.toString(), new WeakReference<BaseActivity>(this));
        initDialog();
    }

    protected void setStatusBarTextMode(boolean isLightMode){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            int vis = getWindow().getDecorView().getSystemUiVisibility();
            if (!isLightMode) {
                vis |= View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR;
            } else {
                vis &= ~View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR;
            }
            getWindow().getDecorView().setSystemUiVisibility(vis);
        }
    }

    private void initDialog() {
        mMaterialDialog = new MaterialDialog(this).setContentView(R.layout.material_dialog_login);
        mMaterialDialog.setCanceledOnTouchOutside(true);
    }

    protected void showLoadingDialog(){
        if(mMaterialDialog != null && !mMaterialDialog.isShow()) {
            mMaterialDialog.show();
        }
    }

    protected void hideLoadingDialog(){
        if(mMaterialDialog != null && mMaterialDialog.isShow()) {
            mMaterialDialog.dismiss();
        }
    }

    @Override
    protected void onDestroy() {
        activitiesMap.remove(BaseActivity.this.toString());
        super.onDestroy();
    }

    /**
     * 关闭所有activity
     */
    public static void finishAll(){
        try {
            if(activitiesMap != null){
                for (Map.Entry<String,WeakReference<BaseActivity>> entry:activitiesMap.entrySet()){
                    WeakReference<BaseActivity> weakReferenceAct = entry.getValue();
                    if(weakReferenceAct!=null && weakReferenceAct.get()!=null){
                        BaseActivity activity = weakReferenceAct.get();
                        if(!activity.isFinishing()){
                            activity.finish();
                        }
                        weakReferenceAct.clear();
                    }
                }
                activitiesMap.clear();
            }
        }catch (Exception e){
            FLog.e("finishAll", e, null);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        MobclickAgent.onResume(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        MobclickAgent.onPause(this);
    }
}
