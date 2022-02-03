package com.ttm.tlrb.ui.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.text.Html;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.ttm.tlrb.BuildConfig;
import com.ttm.tlrb.R;
import com.ttm.tlrb.api.APIManager;
import com.ttm.tlrb.ui.application.Constant;
import com.ttm.tlrb.ui.application.RBApplication;
import com.ttm.tlrb.ui.entity.BmobFile;
import com.ttm.tlrb.ui.entity.VersionInfo;
import com.ttm.tlrb.utils.ToastUtil;
import com.ttm.tlrb.view.MaterialDialog;
import com.umeng.analytics.MobclickAgent;

import rx.Subscriber;

/**
 * Created by Helen on 2016/5/6.
 * 关于
 */
public class AboutActivity extends TitlebarActivity implements View.OnClickListener{

    public static void launcher(Context context){
        context.startActivity(new Intent(context,AboutActivity.class));
    }

    private TextView mNewVersion;
    private VersionInfo mVersionInfo;
    private boolean hasNew = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);
        setTitle(R.string.title_about);
        findViewById(R.id.layout_check_update).setOnClickListener(this);
        findViewById(R.id.layout_share).setOnClickListener(this);
        TextView currentVersion = (TextView) findViewById(R.id.text_current_version);
        currentVersion.setText(getString(R.string.v, BuildConfig.VERSION_NAME));
        mNewVersion = (TextView) findViewById(R.id.text_new_version);
        checkUpdate();
    }


    private void checkUpdate(){
        APIManager.getInstance().checkVersionUpdate(new Subscriber<VersionInfo>() {
            @Override
            public void onCompleted() {

            }

            @Override
            public void onError(Throwable e) {
                hasNew = false;
                mNewVersion.setVisibility(View.VISIBLE);
                mNewVersion.setText(R.string.no_new_version);
            }

            @Override
            public void onNext(VersionInfo versionInfo) {
                if(versionInfo != null){
                    mVersionInfo = versionInfo;
                    String version = BuildConfig.VERSION_NAME;
                    String newVersion = versionInfo.getVersion();
                    if(newVersion.compareTo(version) > 0){
                        hasNew = true;
                        String str = getString(R.string.new_version)+getNewVersionHtml(getString(R.string.v, newVersion));
                        mNewVersion.setText(Html.fromHtml(str));
                    }else {
                        hasNew = false;
                        mNewVersion.setText(R.string.no_new_version);
                    }
                    mNewVersion.setVisibility(View.VISIBLE);
                }
            }
        });
    }

    private String getNewVersionHtml(String newVersion){
        return "<font color='#ff3d00'>" +newVersion+ "</font>";
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1000 && resultCode == Activity.RESULT_OK){
            startDownload();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.layout_check_update:
                MobclickAgent.onEvent(this, Constant.Event.EVENT_ID_UPDATE_CHECK);
                final MaterialDialog materialDialog = new MaterialDialog(this);
                materialDialog.setTitle(getString(R.string.alert));

                if(hasNewVersion()){
                    materialDialog.setPositiveButton(getString(R.string.update), new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            materialDialog.dismiss();
                            if(hasNewVersion()) {
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                                    if (!getPackageManager().canRequestPackageInstalls()){
                                        Intent intent = new Intent(Settings.ACTION_MANAGE_UNKNOWN_APP_SOURCES,
                                                Uri.parse("package:"+getPackageName()));
                                        startActivityForResult(intent,1000);
                                        return;
                                    }
                                }
                                startDownload();
                            }
                        }
                    });
                    materialDialog.setNegativeButton(getString(R.string.cancel), new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            materialDialog.dismiss();
                        }
                    });
                    materialDialog.setMessage(Html.fromHtml(mVersionInfo.getUpdateContent()));
                }else {
                    materialDialog.setPositiveButton(getString(R.string.sure), new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            materialDialog.dismiss();
                        }
                    });
                    materialDialog.setMessage(getString(R.string.no_new_version));
                }
                materialDialog.show();
                break;
            case R.id.layout_share:
                MobclickAgent.onEvent(this, Constant.Event.EVENT_ID_SOFT_SHARE);
                String message = getString(R.string.share_tip);
                shareIntent(this,getString(R.string.app_name),message);
                break;
        }
    }

    private void startDownload(){
        BmobFile file = mVersionInfo.getFile();
        if(file != null && !TextUtils.isEmpty(file.getUrl())) {
            RBApplication.getInstance().startDownloadApk(file.getUrl());
        }
    }

    /**
     * 分享Intent
     */
    public static void shareIntent(Context context, String titleString,
                                   String content) {
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_SUBJECT, titleString);
        intent.putExtra(Intent.EXTRA_TEXT, content);
        try {
            context.startActivity(Intent.createChooser(intent, context.getString(R.string.more_share)));
        } catch (Exception e) {
            ToastUtil.showToast(context, context.getString(R.string.not_find_share_soft));
        }

    }

    private boolean hasNewVersion(){
        return mVersionInfo != null && hasNew;
    }
}
