package com.ttm.tlrb.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;

import androidx.annotation.Nullable;

import com.ttm.tlrb.R;
import com.ttm.tlrb.api.APIManager;
import com.ttm.tlrb.api.APIService;
import com.ttm.tlrb.api.BaseSubscriber;
import com.ttm.tlrb.ui.application.Constant;
import com.ttm.tlrb.ui.entity.BaseEn;
import com.ttm.tlrb.ui.entity.BmobFile;
import com.ttm.tlrb.ui.entity.Feedback;
import com.ttm.tlrb.ui.entity.FileBodyEn;
import com.ttm.tlrb.utils.EnvironmentUtil;
import com.ttm.tlrb.utils.FileUtil;
import com.ttm.tlrb.utils.ToastUtil;
import com.umeng.analytics.MobclickAgent;

import java.io.File;

import okhttp3.RequestBody;
import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

/**
 * Created by Helen on 2016/5/5.
 *
 */
public class FeedBackActivity extends TitlebarActivity implements View.OnClickListener{

    public static void launcher(Context context){
        context.startActivity(new Intent(context,FeedBackActivity.class));
    }

    private EditText mEditTextContent;
    private EditText mEditTextContact;
    private CheckBox mCheckBox;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feedback);
        setTitle(R.string.title_feedback);
        initView();
    }

    private APIService mAPIService;
    private void initView() {
        mEditTextContent = (EditText) findViewById(R.id.et_feedback_content);
        mEditTextContact = (EditText) findViewById(R.id.et_contact);
        mCheckBox = (CheckBox) findViewById(R.id.cb_upload_log);
        Button btnSend = (Button) findViewById(R.id.btn_send);
        btnSend.setOnClickListener(this);
        mAPIService = APIManager.getInstance().getAPIService();

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_send:
                MobclickAgent.onEvent(FeedBackActivity.this, Constant.Event.EVENT_ID_FEEDBACK);
                send();
                break;
            default:break;
        }
    }

    /**
     * 发送
     */
    private void send() {
        final String content = mEditTextContent.getText().toString().trim();
        final String contact = mEditTextContact.getText().toString();
        if(TextUtils.isEmpty(content)){
            ToastUtil.showToast(this,R.string.hint_input_feedback);
            return;
        }
        boolean isUploadLog = mCheckBox.isChecked();
        Feedback feedBack = new Feedback();
        feedBack.setContent(content);
        feedBack.setContact(contact);
        File logFile = null;
        if(isUploadLog){
            logFile = new File(EnvironmentUtil.getCacheFile() + File.separator + Constant.CACHE_LOG,Constant.LOG_FILE_NAME);
        }
        upload(feedBack,logFile);
    }

    private Subscriber<BaseEn> mSubscriber;

    /**
     * 上传
     */
    private void upload(final Feedback feedBack , final File logFile){
        if(mSubscriber == null || mSubscriber.isUnsubscribed()){
            mSubscriber = new BaseSubscriber<BaseEn>(this) {

                @Override
                public void atNext(BaseEn baseEn) {
                    ToastUtil.showToast(FeedBackActivity.this,getString(R.string.thanks_to_feedback));
                    if(logFile != null && logFile.exists()) {
                        FileUtil.deleteFile(logFile);
                    }
                    finish();
                }
            };
        }
        if(logFile != null && logFile.exists()) {
            RequestBody requestBody = RequestBody.create(Constant.TEXT,logFile);
            mAPIService.postFileUpload(logFile.getName(),requestBody)
                    .flatMap(new Func1<FileBodyEn, Observable<BaseEn>>() {
                        @Override
                        public Observable<BaseEn> call(FileBodyEn fileBodyEn) {
                            BmobFile bmobFile = new BmobFile(fileBodyEn.getFilename(),fileBodyEn.getCdn(),fileBodyEn.getUrl());
                            feedBack.setFileLog(bmobFile);
                            return doPostFeedback(feedBack);
                        }
                    })
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(mSubscriber);
        }else{
            feedBack.setFileLog(null);

            doPostFeedback(feedBack)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(mSubscriber);
        }
    }

    /**
     * 执行
     */
    private Observable<BaseEn> doPostFeedback(Feedback feedBack){
        RequestBody feedbackBody = RequestBody.create(Constant.JSON, feedBack.toString());
        return mAPIService.postFeedback(feedbackBody);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(mSubscriber != null && !mSubscriber.isUnsubscribed()){
            mSubscriber.unsubscribe();
        }
    }
}
