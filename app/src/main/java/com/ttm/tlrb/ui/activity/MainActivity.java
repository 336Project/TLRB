package com.ttm.tlrb.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
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
import com.ttm.tlrb.ui.application.RBApplication;
import com.ttm.tlrb.ui.entity.Account;
import com.ttm.tlrb.ui.entity.BmobFile;
import com.ttm.tlrb.ui.entity.VersionInfo;
import com.ttm.tlrb.ui.fragment.RedBombFragment;
import com.ttm.tlrb.ui.service.DownloadService;
import com.ttm.tlrb.view.MaterialDialog;

import rx.Subscriber;

public class MainActivity extends BaseActivity implements NavigationView.OnNavigationItemSelectedListener{
    private SimpleDraweeView mHeaderView;
    private TextView mTextUserName;
    private TextView mTextNickName;

    public static void launcher(Context context){
        context.startActivity(new Intent(context,MainActivity.class));
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
                AddRedBombActivity.launcher(MainActivity.this);
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
        mTextNickName = (TextView) header.findViewById(R.id.tv_nickname);
        initTabLayout();
        Account account = UserManager.getInstance().getAccount();
        if(account != null){
            RBApplication.getInstance().setSession(account.getSessionToken());
            mHeaderView.setImageURI(Uri.parse(account.getPortrait()));
            mTextUserName.setText(account.getUsername());
            mTextNickName.setText(account.getNickname());
        }
        checkUpdate();
    }

    private void initTabLayout(){
        RedBombPagerAdapter pagerAdapter = new RedBombPagerAdapter(getSupportFragmentManager());
        pagerAdapter.addFragment(RedBombFragment.newInstance(0),getString(R.string.action_all));
        pagerAdapter.addFragment(RedBombFragment.newInstance(2),getString(R.string.action_out));
        pagerAdapter.addFragment(RedBombFragment.newInstance(1),getString(R.string.action_in));
        ViewPager viewPager = (ViewPager) findViewById(R.id.view_pager);
        viewPager.setAdapter(pagerAdapter);
        TabLayout tabLayout = (TabLayout) findViewById(R.id.tab_layout);
        tabLayout.setupWithViewPager(viewPager);
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
        LoginActivity.launcher(MainActivity.this);
        finish();
    }

}
