package com.ttm.tlrb.api;

import android.content.Context;

import com.ttm.tlrb.R;
import com.ttm.tlrb.api.e.HttpExceptionHandle;
import com.ttm.tlrb.view.MaterialDialog;

import retrofit2.adapter.rxjava.HttpException;
import rx.Subscriber;

/**
 * user:wtw
 * time: 2016/7/14 0014.
 */
public abstract class BaseSubscriber<T> extends Subscriber<T> {

    private MaterialDialog mMaterialDialog;
    private Context context;

    public BaseSubscriber(Context context){
        super();
        this.context=context;
        mMaterialDialog =new MaterialDialog(context).setContentView(R.layout.material_dialog_login);
        mMaterialDialog.setCanceledOnTouchOutside(true);
    }

    @Override
    public void onStart() {
        super.onStart();
        mMaterialDialog.show();
    }

    @Override
    public void onCompleted() {
        atCompleted();
    }

    @Override
    public void onError(Throwable e) {
        dismiss();
        if(e instanceof HttpException){
            HttpExceptionHandle handle = new HttpExceptionHandle((HttpException) e,context);
            handle.handle();
        }else {
            atError(e);
        }
    }

    @Override
    public void onNext(T t) {
        dismiss();
        atNext(t);
    }

    public void dismiss(){
        if(mMaterialDialog != null && mMaterialDialog.isShow()) {
            mMaterialDialog.dismiss();
        }
    }

    public void atCompleted(){}

    public void atError(Throwable e){}

    public abstract void atNext(T t);


}
