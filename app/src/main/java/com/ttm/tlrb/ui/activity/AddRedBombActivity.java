package com.ttm.tlrb.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.view.View;
import android.widget.DatePicker;
import android.widget.RadioButton;

import com.ttm.tlrb.R;
import com.ttm.tlrb.ui.entity.RedBomb;
import com.ttm.tlrb.view.DatePickerView;

import java.util.Calendar;

public class AddRedBombActivity extends TitlebarActivity implements View.OnClickListener{

    private TextInputLayout mLayoutName;//姓名
    private TextInputLayout mLayoutGift;//随礼
    private RadioButton mRbBoy;//男方
    private RadioButton mRbGirl;//女方
    private RadioButton mRbIncome;//收入
    private TextInputLayout mLayoutCategoryName;//组别
    private TextInputLayout mLayoutTime;//时间
    private TextInputLayout mLayoutNote;//备注
    private TextInputLayout mLayoutMoney;//金额

    public static void launcher(Context context){
        context.startActivity(new Intent(context,AddRedBombActivity.class));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_red_bomb);
        initView();
    }

    private void initView(){
        setTitle("添加红包信息");
        mLayoutName=(TextInputLayout)findViewById(R.id.layout_name);
        mLayoutMoney=(TextInputLayout)findViewById(R.id.layout_money);
        mLayoutGift=(TextInputLayout)findViewById(R.id.layout_gift);
        mRbBoy=(RadioButton)findViewById(R.id.rb_boy);
        mRbGirl =(RadioButton)findViewById(R.id.rb_gril);
        mRbIncome=(RadioButton)findViewById(R.id.rb_income);
        mLayoutCategoryName=(TextInputLayout)findViewById(R.id.layout_categoryName);
        mLayoutTime=(TextInputLayout)findViewById(R.id.layout_time);
        findViewById(R.id.iv_time).setOnClickListener(this);
        mLayoutNote=(TextInputLayout)findViewById(R.id.layout_remark);
        findViewById(R.id.btn_commit).setOnClickListener(this);
        findViewById(R.id.btn_show).setOnClickListener(this);
    }

    private void saveData(){
        RedBomb redBomb=new RedBomb();
        redBomb.setName(mLayoutName.getEditText().getText().toString());
        redBomb.setMoney(Double.valueOf(mLayoutMoney.getEditText().getText().toString()));
        redBomb.setGift(mLayoutGift.getEditText().getText().toString());
        if (mRbBoy.isChecked()){
            redBomb.setTarget(1);
        }else if(mRbGirl.isChecked()){
            redBomb.setTarget(2);
        }else{
            redBomb.setTarget(3);
        }
        if(mRbIncome.isChecked()){
            redBomb.setType(1);
        }else{
            redBomb.setType(2);
        }
        redBomb.setCategoryName(mLayoutCategoryName.getEditText().getText().toString());
        redBomb.setTime(mLayoutTime.getEditText().getText().toString());
        redBomb.setRemark(mLayoutNote.getEditText().getText().toString());
//        redBomb.save(this, new SaveListener() {
//            @Override
//            public void onSuccess() {
//                ToastUtil.showToast(AddRedBombActivity.this,"提交成功");
//                finish();
//            }
//
//            @Override
//            public void onFailure(int i, String s) {
//                ToastUtil.showToast(AddRedBombActivity.this,"提交失败");
//            }
//        });

    }

    private void setStartDate() {
        Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);
        DatePickerView datePickerView = new DatePickerView(AddRedBombActivity.this,
                new DatePickerView.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year,
                                          int monthOfYear, int dayOfMonth) {
                        String time = year + "-" + monthOfYear + "-"
                                + dayOfMonth;
                        mLayoutTime.getEditText().setText(time);
                    }
                }, year, month, day);
        datePickerView.myShow();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_commit:
                saveData();
                break;
            case R.id.iv_time:
                setStartDate();
                break;
            case R.id.btn_show:
                /*RedBomb redBomb=DataSupport.findFirst(RedBomb.class);
                if(redBomb==null){
                    Toast.makeText(AddRedBombActivity.this,"没数据",Toast.LENGTH_SHORT).show();
                }else{
                    Log.e("addredbombactivity",""+redBomb.toString());
                }*/
                break;
            default:
                break;
        }
    }
}
