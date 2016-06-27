package com.ttm.tlrb.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.Spinner;
import android.widget.TextView;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.ttm.tlrb.R;
import com.ttm.tlrb.api.APIManager;
import com.ttm.tlrb.ui.application.Constant;
import com.ttm.tlrb.ui.application.RBApplication;
import com.ttm.tlrb.ui.entity.BmobGeoPoint;
import com.ttm.tlrb.ui.entity.BmobObject;
import com.ttm.tlrb.ui.entity.Category;
import com.ttm.tlrb.ui.entity.RedBomb;
import com.ttm.tlrb.utils.HLog;
import com.ttm.tlrb.utils.ToastUtil;
import com.ttm.tlrb.view.DatePickerView;
import com.ttm.tlrb.view.MaterialDialog;
import com.umeng.analytics.MobclickAgent;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import rx.Subscriber;

public class AddRedBombActivity extends TitlebarActivity implements View.OnClickListener,AMapLocationListener{
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
        initLocation();
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
        if(locationPoint != null){
            redBomb.setDistrict(district);
            redBomb.setProvince(province);
            redBomb.setLocation(locationPoint);
        }
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


    private AMapLocationClient mLocationClient = null;
    private void initLocation(){
        //初始化定位
        mLocationClient = new AMapLocationClient(RBApplication.getInstance());
        //设置定位回调监听
        mLocationClient.setLocationListener(this);
        //初始化定位参数
        AMapLocationClientOption mLocationOption = new AMapLocationClientOption();
        //设置定位模式为高精度模式，Battery_Saving为低功耗模式，Device_Sensors是仅设备模式
        mLocationOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Battery_Saving);
        //设置是否返回地址信息（默认返回地址信息）
        mLocationOption.setNeedAddress(true);
        //设置是否只定位一次,默认为false
        mLocationOption.setOnceLocation(true);
        //设置是否强制刷新WIFI，默认为强制刷新
        mLocationOption.setWifiActiveScan(true);
        //设置是否允许模拟位置,默认为false，不允许模拟位置
        mLocationOption.setMockEnable(false);
        //设置定位间隔,单位毫秒,默认为2000ms
        mLocationOption.setInterval(2000);
        //给定位客户端对象设置定位参数
        mLocationClient.setLocationOption(mLocationOption);
        //启动定位
        mLocationClient.startLocation();
    }

    private String province;
    private String city;
    private String district;
    private BmobGeoPoint locationPoint;
    @Override
    public void onLocationChanged(AMapLocation amapLocation) {
        if (amapLocation != null) {
            if (amapLocation.getErrorCode() == 0) {

                //定位成功回调信息，设置相关消息
                amapLocation.getLocationType();//获取当前定位结果来源，如网络定位结果，详见定位类型表
                double lat = amapLocation.getLatitude();//获取纬度
                double lng = amapLocation.getLongitude();//获取经度

                amapLocation.getAccuracy();//获取精度信息
                amapLocation.getAddress();//地址，如果option中设置isNeedAddress为false，则没有此结果，网络定位结果中会有地址信息，GPS定位不返回地址信息。
                amapLocation.getCountry();//国家信息
                province = amapLocation.getProvince();//省信息
                city = amapLocation.getCity();//城市信息
                district = amapLocation.getDistrict();//城区信息
                amapLocation.getStreet();//街道信息
                amapLocation.getStreetNum();//街道门牌号信息
                amapLocation.getCityCode();//城市编码
                amapLocation.getAdCode();//地区编码
                amapLocation.getAoiName();//获取当前定位点的AOI信息
                if(lat <= 90.0D && lat >= -90.0D && lng <= 180.0D && lng >= -180.0D){
                    locationPoint = new BmobGeoPoint(lng,lat);
                }
                HLog.d("onLocationChanged",amapLocation.toString());
            } else {
                //显示错误信息ErrCode是错误码，errInfo是错误信息，详见错误码表。
                HLog.e("AmapError","location Error, ErrCode:"
                        + amapLocation.getErrorCode() + ", errInfo:"
                        + amapLocation.getErrorInfo());
            }
        }
    }

    @Override
    protected void onStop() {
        if(mLocationClient != null) {
            mLocationClient.stopLocation();//停止定位
        }
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        if(mLocationClient != null){
            mLocationClient.onDestroy();//销毁定位客户端。
        }
        super.onDestroy();
    }
}
