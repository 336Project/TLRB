package com.ttm.tlrb.ui.activity;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.drawee.view.SimpleDraweeView;
import com.ttm.tlrb.R;
import com.ttm.tlrb.api.APIManager;
import com.ttm.tlrb.api.UserManager;
import com.ttm.tlrb.ui.adapter.RedBombPagerAdapter;
import com.ttm.tlrb.ui.application.Constant;
import com.ttm.tlrb.ui.entity.Account;
import com.ttm.tlrb.ui.entity.BmobFile;
import com.ttm.tlrb.ui.entity.RedBomb;
import com.ttm.tlrb.ui.entity.VersionInfo;
import com.ttm.tlrb.ui.fragment.RedBombFragment;
import com.ttm.tlrb.ui.service.DownloadService;
import com.ttm.tlrb.utils.HLog;
import com.ttm.tlrb.utils.ToastUtil;
import com.ttm.tlrb.view.MaterialDialog;
import com.umeng.analytics.MobclickAgent;

import java.util.List;
import java.util.Map;

import rx.Subscriber;

public class MainActivity extends BaseActivity implements NavigationView.OnNavigationItemSelectedListener,View.OnClickListener{
    public static final int GO_ADD_RED_BOMB=1002;
    private SimpleDraweeView mHeaderView;
    private TextView mTextUserName;
    //private TextView mTextNickName;
    private TextView mTextIn;
    private TextView mTextOut;
    private RedBombFragment mAllInformFragment;
    private RedBombFragment mSpendingFragment;
    private RedBombFragment mIncomeFragment;

    public static void launcher(Context context) {
        context.startActivity(new Intent(context, MainActivity.class));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(MainActivity.this,AddRedBombActivity.class);
                startActivityForResult(intent,GO_ADD_RED_BOMB);
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        View header = navigationView.getHeaderView(0);

        mHeaderView = (SimpleDraweeView) header.findViewById(R.id.iv_portrait);
        mTextUserName = (TextView) header.findViewById(R.id.tv_username);
        //mTextNickName = (TextView) header.findViewById(R.id.tv_nickname);
        mTextIn = (TextView) header.findViewById(R.id.tv_in);
        mTextOut = (TextView) header.findViewById(R.id.tv_out);
        mHeaderView.setOnClickListener(this);
        mTextIn.setText("0");
        mTextOut.setText("0");

        initTabLayout();
        counter();
        checkUpdate();

        //location
        requestPermission();
    }

    private static final int REQ_WRITE = 2002;

    private void requestPermission() {
        if (Build.VERSION.SDK_INT >= 23) {
            boolean isAllow = PackageManager.PERMISSION_GRANTED == ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
            if (!isAllow) {
                ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},REQ_WRITE);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode == REQ_WRITE){
            boolean isAllow = PackageManager.PERMISSION_GRANTED == grantResults[0];
            if(!isAllow){
                ToastUtil.showToast(this,"权限获取失败，部分功能将不能正常使用！");
            }
        }
    }

    private void refreshAccountInfo(){
        Account account = UserManager.getInstance().getAccount();
        if (account != null && mHeaderView != null) {
            mHeaderView.setImageURI(Uri.parse(account.getPortrait()));
            String nickName  = account.getNickname();
            if(!TextUtils.isEmpty(nickName)) {
                mTextUserName.setText(nickName);
            }else {
                mTextUserName.setText(account.getUsername());
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        refreshAccountInfo();
    }

    private void initTabLayout(){
        RedBombPagerAdapter pagerAdapter = new RedBombPagerAdapter(getSupportFragmentManager());
        mAllInformFragment=RedBombFragment.newInstance(0);
        mSpendingFragment=RedBombFragment.newInstance(2);
        mIncomeFragment=RedBombFragment.newInstance(1);
        pagerAdapter.addFragment(mAllInformFragment,getString(R.string.action_all));
        pagerAdapter.addFragment(mSpendingFragment,getString(R.string.action_out));
        pagerAdapter.addFragment(mIncomeFragment,getString(R.string.action_in));
        ViewPager viewPager = (ViewPager) findViewById(R.id.view_pager);
        viewPager.setAdapter(pagerAdapter);
        TabLayout tabLayout = (TabLayout) findViewById(R.id.tab_layout);
        tabLayout.setupWithViewPager(viewPager);
    }


    Subscriber<List<Map<String,String>>> mCounterSubscriber;
    private void counter(){
        if(mCounterSubscriber == null || mCounterSubscriber.isUnsubscribed()){
            mCounterSubscriber = new Subscriber<List<Map<String, String>>>() {
                @Override
                public void onCompleted() {

                }

                @Override
                public void onError(Throwable e) {
                    HLog.e("MainActivity","mCounterSubscriber----onError",e);
                }

                @Override
                public void onNext(List<Map<String, String>> maps) {
                    if(maps != null && !maps.isEmpty()){
                        for (Map<String,String> map:maps){
                            String money = map.get("_sumMoney");
                            int type = Integer.valueOf(map.get("type"));
                            if(type == RedBomb.TYPE_IN){
                                mTextIn.setText(money);
                            }else if(type == RedBomb.TYPE_OUT){
                                mTextOut.setText(money);
                            }
                        }
                    }else {
                        mTextIn.setText("0");
                        mTextOut.setText("0");
                    }
                }
            };
        }
        APIManager.getInstance().countRedBombMoney(mCounterSubscriber);
    }

    /**
     * 登录检测更新
     */
    private Subscriber<VersionInfo> mVersionInfoSubscriber;
    private void checkUpdate(){
        if(mVersionInfoSubscriber == null || mVersionInfoSubscriber.isUnsubscribed()){
            mVersionInfoSubscriber = new Subscriber<VersionInfo>() {
                @Override
                public void onCompleted() {

                }

                @Override
                public void onError(Throwable e) {
                    HLog.e("MainActivity","mVersionInfoSubscriber----onError",e);
                }

                @Override
                public void onNext(VersionInfo versionInfo) {
                    if (versionInfo != null) {
                        if (!versionInfo.getPatch()) {//有新版本
                            showUpdateDialog(versionInfo);
                        } else {//有修复包
                            APIManager.getInstance().addPatch();
                        }
                    }
                }
            };
        }

        APIManager.getInstance().loginCheckVersion(mVersionInfoSubscriber);
    }

    private void showUpdateDialog(final VersionInfo versionInfo){
        final boolean isForce = versionInfo.getForce();
        final MaterialDialog dialog = new MaterialDialog(this);
        dialog.setCanceledOnTouchOutside(false);
        String alert = isForce ? getString(R.string.force_to_update):getString(R.string.alert);
        dialog.setTitle(alert);
        dialog.setPositiveButton(getString(R.string.update), new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BmobFile file = versionInfo.getFile();
                if(file != null && !TextUtils.isEmpty(file.getUrl())) {
                    Intent intent = new Intent(MainActivity.this, DownloadService.class);
                    intent.putExtra(DownloadService.KEY_URL, file.getUrl());
                    startService(intent);
                }
                dialog.dismiss();
                if(isForce) {
                    finishAll();
                }
            }
        });
        dialog.setNegativeButton(getString(R.string.cancel), new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                if(isForce){
                    finishAll();
                }
            }
        });
        dialog.setMessage(Html.fromHtml(versionInfo.getUpdateContent()));
        dialog.show();
    }

    @Override
    protected void onDestroy() {
        if(mVersionInfoSubscriber != null && !mVersionInfoSubscriber.isUnsubscribed()){
            mVersionInfoSubscriber.unsubscribe();
        }
        if(mCounterSubscriber != null && !mCounterSubscriber.isUnsubscribed()){
            mCounterSubscriber.unsubscribe();
        }
        super.onDestroy();
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            quit();
        }
    }

    private long exitTime = 0;
    public void quit() {
        if ((System.currentTimeMillis() - exitTime) > 2000) {
            Toast.makeText(getApplicationContext(), R.string.once_click_quit, Toast.LENGTH_SHORT).show();
            exitTime = System.currentTimeMillis();
        } else {
            finishAll();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        //getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        //noinspection SimplifiableIfStatement

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_group) {
            GroupActivity.launcher(this);
        } else if (id == R.id.nav_feedback) {
            FeedBackActivity.launcher(this);
        } else if (id == R.id.nav_about) {
            AboutActivity.launcher(this);
        } else if (id == R.id.nav_exit){
            final MaterialDialog dialog = new MaterialDialog(this);
            dialog.setTitle(getString(R.string.alert));
            dialog.setMessage(getString(R.string.confirm_to_exit));
            dialog.setPositiveButton(R.string.sure, new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                    logout();
                }
            });
            dialog.setNegativeButton(R.string.cancel, new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                }
            });
            dialog.show();
        }

        /*DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);*/
        return true;
    }

    private void logout(){
        UserManager.getInstance().logout();
        MobclickAgent.onEvent(this, Constant.Event.EVENT_ID_LOGOUT);
        LoginActivity.launcher(MainActivity.this);
        finish();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode==AddRedBombActivity.REFRESH_REDBOMBFRAGMENT){
            if(mAllInformFragment!=null&&mAllInformFragment.isAdded()){
                mAllInformFragment.onRefresh();
            }
            if(mIncomeFragment!=null&&mIncomeFragment.isAdded()){
                mIncomeFragment.onRefresh();
            }
            if(mSpendingFragment!=null&&mSpendingFragment.isAdded()){
                mSpendingFragment.onRefresh();
            }
            counter();
        }
        if(resultCode==AddRedBombActivity.ADD_INFORM){
            RedBomb redBomb=(RedBomb) data.getSerializableExtra("redBomb");
            if(mAllInformFragment!=null&&mAllInformFragment.isAdded()){
                mAllInformFragment.addNewInform(redBomb);
            }
            if(mIncomeFragment!=null&&mIncomeFragment.isAdded()){
                if(redBomb.getType()==1){
                    mIncomeFragment.addNewInform(redBomb);
                }
            }
            if(mSpendingFragment!=null&&mSpendingFragment.isAdded()){
                if(redBomb.getType()==2){
                    mSpendingFragment.addNewInform(redBomb);
                }
            }
            counter();
        }
    }

    @Override
    public void onClick(View v) {
        Intent intent = new Intent();
        switch (v.getId()){
            case R.id.iv_portrait:
                intent.setClass(MainActivity.this,UserInfoActivity.class);
                startActivity(intent);
                break;
        }
    }
}
