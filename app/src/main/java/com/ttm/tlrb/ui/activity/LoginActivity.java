package com.ttm.tlrb.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;

import com.ttm.tlrb.MainActivity;
import com.ttm.tlrb.R;

public class LoginActivity extends BaseActivity implements View.OnClickListener{
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        initView();
    }

    private void initView() {
        findViewById(R.id.button).setOnClickListener(this);
        findViewById(R.id.textView_register).setOnClickListener(this);
//        TextInputLayout layout = (TextInputLayout) findViewById(R.id.layout_name);
//        Field field = layout.getClass().getDeclaredField("mFocusedTextColor");
//        field.setAccessible(true);
//        field.set(layout, createColorStateList(color, 0, 0, 0));
    }

    @Override
    public void onClick(View v) {
        Intent intent = new Intent();
        switch (v.getId()){
            case R.id.button:
                intent.setClass(this, MainActivity.class);
                startActivity(intent);
                break;
            case R.id.textView_register:
                intent.setClass(this,RegisterActivity.class);
                startActivity(intent);
                break;

        }
    }
}
