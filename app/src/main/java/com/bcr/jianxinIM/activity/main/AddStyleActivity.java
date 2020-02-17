package com.bcr.jianxinIM.activity.main;

import android.os.Bundle;
import android.widget.CompoundButton;

import com.bcr.jianxinIM.R;
import com.bcr.jianxinIM.im.server.SealAction;
import com.bcr.jianxinIM.im.server.network.async.AsyncTaskManager;
import com.bcr.jianxinIM.im.server.network.async.OnDataListener;
import com.bcr.jianxinIM.im.server.network.http.HttpException;
import com.bcr.jianxinIM.im.server.response.GetUserAddWayResponse;
import com.bcr.jianxinIM.im.server.response.SetUserAddWayResponse;
import com.bcr.jianxinIM.im.server.utils.NToast;
import com.bcr.jianxinIM.im.server.widget.LoadDialog;
import com.bcr.jianxinIM.im.ui.activity.BaseActivity;
import com.bcr.jianxinIM.im.ui.widget.switchbutton.SwitchButton;
import com.bcr.jianxinIM.util.SharedPreferencesUtil;


public class AddStyleActivity extends BaseActivity implements CompoundButton.OnCheckedChangeListener {
    private static final int SETSTYLE =252 ;
    SwitchButton sw_phone, sw_Id, sw_code, sw_group;
    private static final int GETUSETADDWAY = 251;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_style);
        setTitle("添加我的方式");
        initView();
        initData();
    }

    public void initData() {
        AsyncTaskManager.getInstance(mContext).request(GETUSETADDWAY, new OnDataListener() {
            @Override
            public Object doInBackground(int requestCode, String parameter) throws HttpException {
                return new SealAction(mContext).getUserAddWay(SharedPreferencesUtil.getString(mContext,"userId",""));
            }

            @Override
            public void onSuccess(int requestCode, Object result) {
                if (result != null) {
                    GetUserAddWayResponse getUserAddWayResponse= (GetUserAddWayResponse) result;
                    if(getUserAddWayResponse.isSuccess()==true){
                        LoadDialog.dismiss(mContext);
                        if(getUserAddWayResponse.getResultData().getTel()==0){
                            sw_phone.setChecked(false);
                        }else {
                            sw_phone.setChecked(true);
                        }
                        if(getUserAddWayResponse.getResultData().getUserId()==0){
                            sw_Id.setChecked(false);
                        }else {
                            sw_Id.setChecked(true);
                        }
                        if(getUserAddWayResponse.getResultData().getCode()==0){
                            sw_code.setChecked(false);
                        }else {
                            sw_code.setChecked(true);
                        }
                        if(getUserAddWayResponse.getResultData().getGroup()==0){
                            sw_group.setChecked(false);
                        }else {
                            sw_group.setChecked(true);
                        }

                       // NToast.shortToast(mContext,getUserAddWayResponse.getMessage());
                    }else {
                        LoadDialog.dismiss(mContext);
                        NToast.shortToast(mContext,getUserAddWayResponse.getMessage());
                    }
                }
            }

            @Override
            public void onFailure(int requestCode, int state, Object result) {
                LoadDialog.dismiss(mContext);
            }
        });
    }

    public void initView() {
        sw_phone = findViewById(R.id.sw_phone);
        sw_Id = findViewById(R.id.sw_Id);
        sw_code = findViewById(R.id.sw_code);
        sw_group = findViewById(R.id.sw_group);
        sw_phone.setOnCheckedChangeListener(this);
        sw_Id.setOnCheckedChangeListener(this);
        sw_code.setOnCheckedChangeListener(this);
        sw_group.setOnCheckedChangeListener(this);
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        switch (buttonView.getId()) {
            case R.id.sw_phone://设置会话置顶
                String AddWay;
                if (isChecked) {
                    AddWay="1";
                    setStyle(AddWay,"1");
                }else {
                    AddWay="0";
                    setStyle(AddWay,"1");
                }
                break;
            case R.id.sw_Id://设置免打扰
                String AddWays;
                if (isChecked) {
                    AddWays="1";
                    setStyle(AddWays,"4");
                }else {
                    AddWays="0";
                    setStyle(AddWays,"4");
                }
                break;

            case R.id.sw_code://设置群禁言
                String AddWays1;
                if (isChecked) {
                    AddWays1="1";
                    setStyle(AddWays1,"2");
                }else {
                    AddWays1="0";
                    setStyle(AddWays1,"2");
                }
                break;

            case R.id.sw_group://设置是否可以查看好友
                String AddWays2;
                if (isChecked) {
                    AddWays2="1";
                    setStyle(AddWays2,"3");
                }else {
                    AddWays2="0";
                    setStyle(AddWays2,"3");
                }

                break;
        }
    }

    private void setStyle(String addWay, String s) {

        AsyncTaskManager.getInstance(mContext).request(SETSTYLE, new OnDataListener() {
            @Override
            public Object doInBackground(int requestCode, String parameter) throws HttpException {
                return new SealAction(mContext).setUserAddWay(SharedPreferencesUtil.getString(mContext,"userId",""),s,addWay);
            }

            @Override
            public void onSuccess(int requestCode, Object result) {
                if (result != null) {
                    SetUserAddWayResponse setUserAddWayResponse= (SetUserAddWayResponse) result;
                    if(setUserAddWayResponse.isSuccess()==true){
                        LoadDialog.dismiss(mContext);
                        NToast.shortToast(mContext,setUserAddWayResponse.getMessage());
                        initData();
                    }else {
                        LoadDialog.dismiss(mContext);
                        NToast.shortToast(mContext,setUserAddWayResponse.getMessage());
                    }
                }
            }

            @Override
            public void onFailure(int requestCode, int state, Object result) {
                LoadDialog.dismiss(mContext);
            }
        });
    }
}
