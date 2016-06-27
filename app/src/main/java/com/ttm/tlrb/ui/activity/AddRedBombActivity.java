package com.ttm.tlrb.ui.activity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.Spinner;
import android.widget.TextView;

import com.ttm.tlrb.R;
import com.ttm.tlrb.api.APIManager;
import com.ttm.tlrb.ui.application.Constant;
import com.ttm.tlrb.ui.entity.BmobObject;
import com.ttm.tlrb.ui.entity.Category;
import com.ttm.tlrb.ui.entity.RedBomb;
import com.ttm.tlrb.ui.fragment.RedBombFragment;
import com.ttm.tlrb.utils.ToastUtil;
import com.ttm.tlrb.view.DatePickerView;
import com.ttm.tlrb.view.MaterialDialog;
import com.umeng.analytics.MobclickAgent;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import rx.Subscriber;

public class AddRedBombActivity extends TitlebarActivity implements View.OnClickListener{
    public static final int GO_GROUP=1001;
    public static final int REFRESH_REDBOMBFRAGMENT=2001;

    private TextInputLayout mLayoutName;//姓名
    private TextInputLayout mLayoutGift;//随礼
    private TextInputLayout mLayoutTime;//时间
    private TextInputLayout mLayoutNote;//备注
    private TextInputLayout mLayoutMoney;//金额
    private Spinner mSpAddType;//组别
    private String type="";//选择的组别
    private TextView mTvMen;//男方
    private TextView mTvWomen;//女方
    private TextView mTvAll;//所有
    private TextView mTvIncome;//收入
    private TextView mTvSpending;//支出
    private int target=1;//属于哪方
    private int IntOutType=1;//收入支出类型
    private RedBomb redBomb;//列表传递过来的参数

    public static void launcher(Context context){
        context.startActivity(new Intent(context, AddRedBombActivity.class));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_red_bomb);
        initView();
    }

    private void initView(){
        setTitle("红包信息");
        redBomb= (RedBomb) getIntent().getSerializableExtra("redBomb");
        mLayoutName=(TextInputLayout)findViewById(R.id.layout_name);
        mLayoutMoney=(TextInputLayout)findViewById(R.id.layout_money);
        mLayoutGift=(TextInputLayout)findViewById(R.id.layout_gift);
        mTvMen=(TextView)findViewById(R.id.tv_men);
        mTvMen.setOnClickListener(this);
        mTvWomen=(TextView)findViewById(R.id.tv_women);
        mTvWomen.setOnClickListener(this);
        mTvAll=(TextView)findViewById(R.id.tv_all);
        mTvAll.setOnClickListener(this);
        mTvIncome=(TextView)findViewById(R.id.tv_income);
        mTvIncome.setOnClickListener(this);
        mTvSpending=(TextView)findViewById(R.id.tv_spending);
        mTvSpending.setOnClickListener(this);
        mLayoutTime=(TextInputLayout)findViewById(R.id.layout_time);
        mLayoutTime.setFocusable(false);
        findViewById(R.id.iv_time).setOnClickListener(this);
        mLayoutNote=(TextInputLayout)findViewById(R.id.layout_remark);
        findViewById(R.id.btn_commit).setOnClickListener(this);
        findViewById(R.id.btn_update).setOnClickListener(this);
        findViewById(R.id.btn_delete).setOnClickListener(this);
        findViewById(R.id.tv_addType).setOnClickListener(this);
        mSpAddType=(Spinner)findViewById(R.id.sp_addType);
        if(redBomb!=null){
            findViewById(R.id.btn_commit).setVisibility(View.GONE);
            findViewById(R.id.layout_updateAndDelete).setVisibility(View.VISIBLE);
            mLayoutName.getEditText().setText(redBomb.getName());
            mLayoutMoney.getEditText().setText(redBomb.getMoney()+"");
            mLayoutGift.getEditText().setText(redBomb.getGift());
            mLayoutTime.getEditText().setText(redBomb.getTime());
            mLayoutNote.getEditText().setText(redBomb.getRemark());
            if(redBomb.getTarget()==2){
                mTvWomen.performClick();
            }else if(redBomb.getTarget()==3){
                mTvAll.performClick();
            }
            if(redBomb.getType()==2){
                mTvSpending.performClick();
            }
        }
        findAddType();
    }

    //查询组别
    private void findAddType(){
        APIManager.getInstance().getCategoryList(new Subscriber<List<Category>>() {
            @Override
            public void onCompleted() {

            }

            @Override
            public void onError(Throwable e) {
//                ToastUtil.showToast(AddRedBombActivity.this,"获取组别失败");
            }

            @Override
            public void onNext(List<Category> categories) {
                int selection=0;
                List<String> list = new ArrayList<String>();
                for (int i=0;i<categories.size();i++){
                    if(redBomb!=null&&redBomb.getCategoryName().equals(categories.get(i).getName())){
                        type=categories.get(i).getName();
                        selection=i;
                    }else if(i==0){
                        type=categories.get(0).getName();
                    }
                    list.add(categories.get(i).getName());
                }
                final ArrayAdapter<String> adapter = new ArrayAdapter<String>(AddRedBombActivity.this, android.R.layout.simple_spinner_item, list);
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                mSpAddType.setAdapter(adapter);
                mSpAddType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        type = adapter.getItem(position);
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {

                    }
                });
                mSpAddType.setSelection(selection);
            }
        });
    }

    //保存数据
    private void saveData(){
        RedBomb redBomb=new RedBomb();
        if(checkInput(redBomb)==null){
            return;
        }
        APIManager.getInstance().addRedBomb(redBomb, new Subscriber<BmobObject>() {
            @Override
            public void onCompleted() {
                setResult(REFRESH_REDBOMBFRAGMENT);
                ToastUtil.showToast(AddRedBombActivity.this, "添加成功");
                finish();
            }

            @Override
            public void onError(Throwable e) {
                ToastUtil.showToast(AddRedBombActivity.this, "保存数据失败，请重试");
            }

            @Override
            public void onNext(BmobObject bmobObject) {

            }
        });
    }

    //更新数据
    private void updateData(){
        if(checkInput(redBomb)==null){
            return;
        }
        APIManager.getInstance().updateRedBomb(redBomb, new Subscriber<BmobObject>() {
            @Override
            public void onCompleted() {
                setResult(REFRESH_REDBOMBFRAGMENT);
                ToastUtil.showToast(AddRedBombActivity.this, "修改数据成功");
                finish();
            }

            @Override
            public void onError(Throwable e) {
                ToastUtil.showToast(AddRedBombActivity.this, "修改数据失败，请重试");
            }

            @Override
            public void onNext(BmobObject bmobObject) {

            }
        });
    }

    //删除数据
    private void deleteData(){
        APIManager.getInstance().deleteRedBomb(redBomb.getObjectId(), new Subscriber<BmobObject>() {
            @Override
            public void onCompleted() {
                setResult(REFRESH_REDBOMBFRAGMENT);
                ToastUtil.showToast(AddRedBombActivity.this, "删除成功");
                finish();
            }

            @Override
            public void onError(Throwable e) {
                ToastUtil.showToast(AddRedBombActivity.this, "删除数据失败，请重试");
            }

            @Override
            public void onNext(BmobObject bmobObject) {

            }
        });
    }

    //检查用户输入是否正确，并赋值redBomb
    private RedBomb checkInput(RedBomb redBomb){
        if(mLayoutMoney.getEditText().getText().toString().equals("")){
            ToastUtil.showToast(AddRedBombActivity.this,"金额不能为空");
            return null;
        }else if(Double.valueOf(mLayoutMoney.getEditText().getText().toString())<=0){
            ToastUtil.showToast(AddRedBombActivity.this,"金额要大于0元");
            return null;
        }
        redBomb.setName(mLayoutName.getEditText().getText().toString());
        redBomb.setMoney(Double.valueOf(mLayoutMoney.getEditText().getText().toString()));
        redBomb.setGift(mLayoutGift.getEditText().getText().toString());
        redBomb.setTarget(target);
        redBomb.setType(IntOutType);
        redBomb.setCategoryName(type);
        redBomb.setTime(mLayoutTime.getEditText().getText().toString());
        redBomb.setRemark(mLayoutNote.getEditText().getText().toString());
        return redBomb;
    }

    //设置开始时间
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

    //设置点击
    private void textSet(int nowTarget){
        if(nowTarget!=target){
            if(target==1){
                mTvMen.setTextColor(getResources().getColor(R.color.black_de));
                mTvMen.setBackgroundDrawable(getResources().getDrawable(R.drawable.shape_rectangular_white_radiusleft));
            }else if(target==2){
                mTvWomen.setTextColor(getResources().getColor(R.color.black_de));
                mTvWomen.setBackgroundColor(getResources().getColor(R.color.white));
            }else{
                mTvAll.setTextColor(getResources().getColor(R.color.black_de));
                mTvAll.setBackgroundDrawable(getResources().getDrawable(R.drawable.shape_rectangular_white_radiusright));
            }
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_commit:
                MobclickAgent.onEvent(AddRedBombActivity.this, Constant.Event.EVENT_ID_BOMB_ADD);
                saveData();
                break;
            case R.id.btn_update:
                MobclickAgent.onEvent(AddRedBombActivity.this, Constant.Event.EVENT_ID_BOMB_UPDATE);
                final MaterialDialog mMaterialDialog = new MaterialDialog(this);
                mMaterialDialog.setTitle("提示");
                mMaterialDialog.setMessage("您确定更新红包信息？");
                mMaterialDialog.setPositiveButton("确定",new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        updateData();
                    }
                });
                mMaterialDialog.setNegativeButton("取消", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mMaterialDialog.dismiss();
                    }
                });
                mMaterialDialog.show();
                break;
            case R.id.btn_delete:
                MobclickAgent.onEvent(AddRedBombActivity.this, Constant.Event.EVENT_ID_BOMB_DELETE);
                final MaterialDialog deleteBuilder = new MaterialDialog(this);
                deleteBuilder.setTitle("提示");
                deleteBuilder.setMessage("您确定删除红包信息？");
                deleteBuilder.setPositiveButton("确定", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        deleteData();
                    }
                });
                deleteBuilder.setNegativeButton("取消", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        deleteBuilder.dismiss();
                    }
                });
                deleteBuilder.show();
                break;
            case R.id.iv_time:
                setStartDate();
                break;
            case R.id.tv_men:
                textSet(1);
                mTvMen.setTextColor(getResources().getColor(R.color.white));
                mTvMen.setBackgroundDrawable(getResources().getDrawable(R.drawable.shape_rectangular_red_radiusleft));
                target=1;
                break;
            case R.id.tv_women:
                textSet(2);
                mTvWomen.setTextColor(getResources().getColor(R.color.white));
                mTvWomen.setBackgroundColor(getResources().getColor(R.color.Red_400));
                target=2;
                break;
            case R.id.tv_all:
                textSet(3);
                mTvAll.setTextColor(getResources().getColor(R.color.white));
                mTvAll.setBackgroundDrawable(getResources().getDrawable(R.drawable.shape_rectangular_red_radiusright));
                target=3;
                break;
            case R.id.tv_income:
                IntOutType=1;
                mTvIncome.setBackgroundDrawable(getResources().getDrawable(R.drawable.shape_rectangular_red_radiusleft));
                mTvIncome.setTextColor(getResources().getColor(R.color.white));
                mTvSpending.setBackgroundDrawable(getResources().getDrawable(R.drawable.shape_rectangular_white_radiusright));
                mTvSpending.setTextColor(getResources().getColor(R.color.black_de));
                break;
            case R.id.tv_spending:
                IntOutType=2;
                mTvSpending.setBackgroundDrawable(getResources().getDrawable(R.drawable.shape_rectangular_red_radiusright));
                mTvSpending.setTextColor(getResources().getColor(R.color.white));
                mTvIncome.setBackgroundDrawable(getResources().getDrawable(R.drawable.shape_rectangular_white_radiusleft));
                mTvIncome.setTextColor(getResources().getColor(R.color.black_de));
                break;
            case R.id.tv_addType:
                Intent intent =new Intent(AddRedBombActivity.this,GroupActivity.class);
                startActivityForResult(intent,GO_GROUP);
                break;
            default:
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==GO_GROUP){
            findAddType();
        }
    }
}
