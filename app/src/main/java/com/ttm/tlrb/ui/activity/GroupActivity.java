package com.ttm.tlrb.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;

import com.baidu.mobads.AdView;
import com.baidu.mobads.AdViewListener;
import com.ttm.tlrb.R;
import com.ttm.tlrb.api.APIManager;
import com.ttm.tlrb.api.e.CategoryExistException;
import com.ttm.tlrb.api.e.CategoryOverCountException;
import com.ttm.tlrb.ui.application.Constant;
import com.ttm.tlrb.ui.entity.BmobObject;
import com.ttm.tlrb.ui.entity.Category;
import com.ttm.tlrb.utils.HLog;
import com.ttm.tlrb.utils.ToastUtil;
import com.ttm.tlrb.view.EmptyEmbeddedContainer;
import com.ttm.tlrb.view.MaterialDialog;
import com.ttm.tlrb.view.TagGroup;
import com.umeng.analytics.MobclickAgent;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import rx.Subscriber;

/**
 * Created by 李晓伟 on 2016/6/20.
 *
 */
public class GroupActivity extends TitlebarActivity implements TagGroup.OnTagChangeListener{

    private static final int DO_ADD = 1;
    private static final int DO_DELETE = 2;
    private static final int DO_UPDATE = 3;

    public static void launcher(Context context){
        context.startActivity(new Intent(context,GroupActivity.class));
    }

    private TagGroup mTagGroup;
    private List<Category> mCategoryList;
    private EmptyEmbeddedContainer mEmptyLayoutManager;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group);
        setTitle(getString(R.string.title_group));
        mTagGroup = (TagGroup) findViewById(R.id.tag_group);
        mTagGroup.setOnTagChangeListener(this);
        mTagGroup.setOnClickListener(null);
        mTagGroup.setCanUpdate(true);
        mEmptyLayoutManager = (EmptyEmbeddedContainer) findViewById(R.id.empty_container);
        mEmptyLayoutManager.setType(EmptyEmbeddedContainer.EmptyStyle.EmptyStyle_LOADING_WITH_VIEW);
        mEmptyLayoutManager.setEmptyInterface(new EmptyEmbeddedContainer.EmptyInterface() {
            @Override
            public void doRetry() {
                initData();
            }
        });
        initData();
        initAd();
    }

    private void initAd() {
        /*View adView = BannerManager.getInstance(RBApplication.getInstance()).getBanner(this);
        if(adView != null) {
            // 获取要嵌入广告条的布局
            LinearLayout adLayout = (LinearLayout) findViewById(R.id.layout_ad);
            // 将广告条加入到布局中
            adLayout.addView(adView);
        }*/
        AdView adView = new AdView(this,"2733053");
        adView.setListener(new AdViewListener() {
            @Override
            public void onAdReady(AdView adView) {

            }

            @Override
            public void onAdShow(JSONObject jsonObject) {

            }

            @Override
            public void onAdClick(JSONObject jsonObject) {
                MobclickAgent.onEvent(GroupActivity.this, Constant.Event.EVENT_ID_SPLASH_AD_CLICK);
            }

            @Override
            public void onAdFailed(String s) {

            }

            @Override
            public void onAdSwitch() {

            }
        });
        LinearLayout adLayout = (LinearLayout) findViewById(R.id.layout_ad);
        // 将广告条加入到布局中
        adLayout.addView(adView);
    }

    private Subscriber<List<Category>> mCategoryListSubscriber;

    private void initData() {
        if(mCategoryListSubscriber == null || mCategoryListSubscriber.isUnsubscribed()){
            mCategoryListSubscriber = new Subscriber<List<Category>>() {
                @Override
                public void onCompleted() {

                }

                @Override
                public void onError(Throwable e) {
                    mEmptyLayoutManager.setType(EmptyEmbeddedContainer.EmptyStyle.EmptyStyle_RETRY);
                    ToastUtil.showToast(GroupActivity.this,getString(R.string.request_fail));
                }

                @Override
                public void onNext(List<Category> categories) {
                    mEmptyLayoutManager.setType(EmptyEmbeddedContainer.EmptyStyle.EmptyStyle_NORMAL);
                    mCategoryList = categories;
                    if(categories != null && !categories.isEmpty()){
                        int size = categories.size();
                        List<String> tags = new ArrayList<>();
                        for (int i = 0;i < size;i++){
                            tags.add(categories.get(i).getName());
                        }
                        mTagGroup.setTags(tags);
                        mTagGroup.setVisibility(View.VISIBLE);

                    }
                }
            };
        }
        APIManager.getInstance().getCategoryList(mCategoryListSubscriber);
    }

    @Override
    protected void onDestroy() {
        if(mCategoryListSubscriber != null && !mCategoryListSubscriber.isUnsubscribed()){
            mCategoryListSubscriber.unsubscribe();
        }
        if(mCategoryAddSubscriber != null && !mCategoryAddSubscriber.isUnsubscribed()){
            mCategoryAddSubscriber.unsubscribe();
        }
        if(mCategoryUpdateSubscriber != null && !mCategoryUpdateSubscriber.isUnsubscribed()){
            mCategoryUpdateSubscriber.unsubscribe();
        }
        if(mCategoryDeleteSubscriber != null && !mCategoryDeleteSubscriber.isUnsubscribed()){
            mCategoryDeleteSubscriber.unsubscribe();
        }
        super.onDestroy();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //批量操作
        MenuItem item = menu.add(1,1,0, R.string.save);
        item.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == 1) {
            save();
        }
        return super.onOptionsItemSelected(item);
    }

    private void save() {
        if(!mTagGroup.hasTagUpdate()){
            doSave();
        }else {
            TagGroup.TagView updateTag = mTagGroup.getUpdateTag();
            onUpdate(mTagGroup,updateTag,updateTag.getText().toString(),mTagGroup.getUpdateTagOldText());
        }
    }


    private void doSave() {
        String tag = mTagGroup.getInputTagText().trim();
        if(!TextUtils.isEmpty(tag)){
            String[] tags = mTagGroup.getTags();
            if(tags.length >= Category.LIMIT_COUNT){
                ToastUtil.showToast(GroupActivity.this,getString(R.string.tag_count_limit,Category.LIMIT_COUNT));
                return;
            }
            for (String t : tags){
                if(t.trim().equals(tag)) {
                    ToastUtil.showToast(this,getString(R.string.tag_exist));
                    return;
                }
            }
            add(tag);
        }else {
            ToastUtil.showToast(this,getString(R.string.tag_not_empty));
        }
    }

    @Override
    public void onAppend(TagGroup tagGroup, String tag) {

    }

    @Override
    public void onDelete(TagGroup tagGroup, TagGroup.TagView tagView , String tag) {
        showConfirmDialog(DO_DELETE,tagView,tag,null);
    }

    @Override
    public void onUpdate(TagGroup tagGroup, TagGroup.TagView tagView, String newTag,String oldTag) {
        HLog.d("onUpdate","newTag = "+newTag+" oldTag = "+oldTag);
        if(!newTag.equals(oldTag)) {
            showConfirmDialog(DO_UPDATE,tagView,oldTag,newTag);
        }else {
            mTagGroup.updateTag(tagView,oldTag);
        }
    }

    private void showConfirmDialog(final int state,final TagGroup.TagView tagView,final String oldTag,final String newTag){
        final MaterialDialog dialog = new MaterialDialog(this);
        dialog.setTitle(getString(R.string.alert));
        String msg = "";
        if(state == DO_DELETE){
            msg = getString(R.string.tag_delete_tip);
        }else if(state == DO_UPDATE){
            msg = getString(R.string.tag_update_tip);
        }
        dialog.setMessage(msg);

        dialog.setPositiveButton(getString(R.string.sure), new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                if(state == DO_DELETE){
                    delete(tagView,oldTag);
                }else if(state == DO_UPDATE){
                    update(tagView,newTag,oldTag);
                }
            }
        });

        dialog.setNegativeButton(getString(R.string.cancel), new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        dialog.show();
    }

    //添加
    private Subscriber<BmobObject> mCategoryAddSubscriber;
    private void add(String tag){
        MobclickAgent.onEvent(GroupActivity.this, Constant.Event.EVENT_ID_GROUP_ADD);
        mEmptyLayoutManager.setType(EmptyEmbeddedContainer.EmptyStyle.EmptyStyle_LOADING_WITH_VIEW);
        final Category category = new Category();
        category.setName(tag);
        if(mCategoryAddSubscriber == null || mCategoryAddSubscriber.isUnsubscribed()){
            mCategoryAddSubscriber = new Subscriber<BmobObject>() {
                @Override
                public void onCompleted() {

                }

                @Override
                public void onError(Throwable e) {
                    mEmptyLayoutManager.setType(EmptyEmbeddedContainer.EmptyStyle.EmptyStyle_NORMAL);
                    if(e instanceof CategoryExistException){
                        ToastUtil.showToast(GroupActivity.this,getString(R.string.tag_exist));
                    }else if(e instanceof CategoryOverCountException){
                        ToastUtil.showToast(GroupActivity.this,getString(R.string.tag_count_limit,Category.LIMIT_COUNT));
                    }else {
                        ToastUtil.showToast(GroupActivity.this,getString(R.string.add_fail));
                    }
                }

                @Override
                public void onNext(BmobObject object) {
                    mEmptyLayoutManager.setType(EmptyEmbeddedContainer.EmptyStyle.EmptyStyle_NORMAL);
                    category.setObjectId(object.getObjectId());
                    mCategoryList.add(category);
                    ToastUtil.showToast(GroupActivity.this,getString(R.string.add_success));
                    mTagGroup.submitTag();
                }
            };
        }
        APIManager.getInstance().addCategory(category, mCategoryAddSubscriber);
    }

    //删除
    private Subscriber<BmobObject> mCategoryDeleteSubscriber;
    private void delete(final TagGroup.TagView tagView, final String tag){
        MobclickAgent.onEvent(GroupActivity.this, Constant.Event.EVENT_ID_GROUP_DELETE);
        mEmptyLayoutManager.setType(EmptyEmbeddedContainer.EmptyStyle.EmptyStyle_LOADING_WITH_VIEW);
        if(mCategoryDeleteSubscriber == null || mCategoryDeleteSubscriber.isUnsubscribed()){
            mCategoryDeleteSubscriber = new Subscriber<BmobObject>() {
                @Override
                public void onCompleted() {

                }

                @Override
                public void onError(Throwable e) {
                    mEmptyLayoutManager.setType(EmptyEmbeddedContainer.EmptyStyle.EmptyStyle_NORMAL);
                    if(getString(R.string.default_tag).equals(tag)){
                        ToastUtil.showToast(GroupActivity.this, getString(R.string.default_tag_not_delete));
                    }else {
                        ToastUtil.showToast(GroupActivity.this, getString(R.string.delete_fail));
                    }
                }

                @Override
                public void onNext(BmobObject object) {
                    mEmptyLayoutManager.setType(EmptyEmbeddedContainer.EmptyStyle.EmptyStyle_NORMAL);
                    mTagGroup.deleteTag(tagView);
                    ToastUtil.showToast(GroupActivity.this,getString(R.string.delete_success));
                }
            };
        }
        APIManager.getInstance().deleteCategory(getIdByTagName(tag),mCategoryDeleteSubscriber);
    }



    //修改
    private Subscriber<BmobObject> mCategoryUpdateSubscriber;
    private void update(final TagGroup.TagView tagView,final String newTag, final String oldTag){
        MobclickAgent.onEvent(GroupActivity.this, Constant.Event.EVENT_ID_GROUP_UPDATE);
        mEmptyLayoutManager.setType(EmptyEmbeddedContainer.EmptyStyle.EmptyStyle_LOADING_WITH_VIEW);
        if(mCategoryUpdateSubscriber == null || mCategoryUpdateSubscriber.isUnsubscribed()){
            mCategoryUpdateSubscriber = new Subscriber<BmobObject>() {
                @Override
                public void onCompleted() {

                }

                @Override
                public void onError(Throwable e) {
                    mEmptyLayoutManager.setType(EmptyEmbeddedContainer.EmptyStyle.EmptyStyle_NORMAL);
                    if(e instanceof CategoryExistException){
                        ToastUtil.showToast(GroupActivity.this,getString(R.string.tag_exist));
                    }else if(getString(R.string.default_tag).equals(oldTag)){
                        ToastUtil.showToast(GroupActivity.this, getString(R.string.default_tag_not_update));
                    }else {
                        ToastUtil.showToast(GroupActivity.this, getString(R.string.update_fail));
                    }
                }

                @Override
                public void onNext(BmobObject object) {
                    Category category = getCategoryByTagName(oldTag);
                    if(category != null){
                        category.setName(newTag);
                    }
                    mEmptyLayoutManager.setType(EmptyEmbeddedContainer.EmptyStyle.EmptyStyle_NORMAL);
                    mTagGroup.updateTag(tagView,newTag);
                    ToastUtil.showToast(GroupActivity.this,getString(R.string.update_success));
                }
            };
        }

        APIManager.getInstance().updateCategory(getIdByTagName(oldTag),newTag,mCategoryUpdateSubscriber);
    }


    /**
     * 根据标签名称获取组别id
     */
    private String getIdByTagName(String tag){
        if(mCategoryList != null){
            for (Category category:mCategoryList){
                if(category.getName().equals(tag)){
                    return category.getObjectId();
                }
            }
        }
        return null;
    }

    private Category getCategoryByTagName(String tag){
        if(mCategoryList != null){
            for (Category category:mCategoryList){
                if(category.getName().equals(tag)){
                    return category;
                }
            }
        }
        return null;
    }

}
