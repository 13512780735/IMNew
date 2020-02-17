package com.bcr.jianxinIM.activity.main;


import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bcr.jianxinIM.R;
import com.bcr.jianxinIM.activity.LoginRegister.Login01Activity;
import com.bcr.jianxinIM.fragment.main.BindDialogFragment;
import com.bcr.jianxinIM.im.SealConst;
import com.bcr.jianxinIM.im.server.SealAction;
import com.bcr.jianxinIM.im.server.broadcast.BroadcastManager;
import com.bcr.jianxinIM.im.server.network.async.AsyncTaskManager;
import com.bcr.jianxinIM.im.server.network.async.OnDataListener;
import com.bcr.jianxinIM.im.server.network.http.HttpException;
import com.bcr.jianxinIM.im.server.response.getNeedValidatedRespone;
import com.bcr.jianxinIM.im.server.response.setNeedValidatedRespone;
import com.bcr.jianxinIM.im.server.utils.NToast;
import com.bcr.jianxinIM.im.ui.activity.BaseActivity;
import com.bcr.jianxinIM.im.ui.widget.switchbutton.SwitchButton;
import com.bcr.jianxinIM.model.userLoginInfo;
import com.bcr.jianxinIM.util.AppManager;
import com.bcr.jianxinIM.util.DataClearUtil;
import com.bcr.jianxinIM.util.SharedPreferencesUtil;
import com.bcr.jianxinIM.util.SpUtils;
import com.bcr.jianxinIM.view.CommonDialog;

import java.util.Iterator;
import java.util.List;


public class SettingActivity extends BaseActivity implements View.OnClickListener, CompoundButton.OnCheckedChangeListener {

    private static final int FRIENDVALIDATION = 224;
    private static final int SETFRIENDVALIDATION =226 ;
    private ImageView ivBack;
    private TextView tvTitle;
    private TextView tvClear,tvEditPwd,tvEditPhone,tvBindWechat,tvBindQQ,tvMessage,tvAdd,tvChangeAccount,tvLogout;
    private LinearLayout llClear;
    private BindDialogFragment bindDialogFragment;
    private Bundle bundle;
    private List<userLoginInfo> list;
    private SwitchButton mFriendValidation;
    private String flag;
    private getNeedValidatedRespone needValidatedRespone1;
    private LinearLayout llAdd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        setTitle("设置");
        SpUtils.getSp(this);
        AppManager.getAppManager().addActivity(this);
        initView();
        initData();
    }
    private void initData(){
        AsyncTaskManager.getInstance(mContext).request(FRIENDVALIDATION, new OnDataListener() {
            @Override
            public Object doInBackground(int requestCode, String parameter) throws HttpException {
                return new SealAction(mContext).getNeedValidated(SharedPreferencesUtil.getString(mContext,"userId",""));
            }

            @Override
            public void onSuccess(int requestCode, Object result) {
                if (result != null) {
                    needValidatedRespone1=(getNeedValidatedRespone)result;
                    if(needValidatedRespone1.isSuccess()==true){
                        flag=needValidatedRespone1.getResultData();
                        if("1".equals(flag)){
                            mFriendValidation.setChecked(true);
                        }else{ mFriendValidation.setChecked(false);}

                    }else {
                        NToast.shortToast(mContext,needValidatedRespone1.getMessage());
                    }
                }
            }

            @Override
            public void onFailure(int requestCode, int state, Object result) {

            }
        });
    }
    private void initView() {
//        ivBack=findViewById(R.id.titlebar_iv_left);
//        ivBack.setVisibility(View.VISIBLE);
//        ivBack.setOnClickListener(v -> onBackPressed());
//        tvTitle=findViewById(R.id.titlebar_tv);
//        tvTitle.setText("设置");
        tvClear=findViewById(R.id.tvClear);
        llClear=findViewById(R.id.llClear);
        tvEditPwd=findViewById(R.id.tvEditPwd);
        tvEditPhone=findViewById(R.id.tvEditPhone);
        tvBindWechat=findViewById(R.id.tvBindWechat);
        tvBindQQ=findViewById(R.id.tvBindQQ);
        tvAdd=findViewById(R.id.tvAdd);
        tvMessage=findViewById(R.id.tvMessage);
        mFriendValidation=findViewById(R.id.sw_freind_validation);
        tvChangeAccount=findViewById(R.id.tvChangeAccount);
        tvLogout=findViewById(R.id.tvLogout);
        llAdd=findViewById(R.id.llAdd);
        llClear.setOnClickListener(this);
        tvEditPwd.setOnClickListener(this);
        tvEditPhone.setOnClickListener(this);
        tvBindWechat.setOnClickListener(this);
        llAdd.setOnClickListener(this);
        tvBindQQ.setOnClickListener(this);
        tvAdd.setOnClickListener(this);
        tvMessage.setOnClickListener(this);
        tvChangeAccount.setOnClickListener(this);
        tvLogout.setOnClickListener(this);
        tvClear.setText(DataClearUtil.getTotalCacheSize(this));
        mFriendValidation.setOnCheckedChangeListener(this);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.llClear:
                DataClearUtil.cleanAllCache(this);
                Toast.makeText(this, "清除缓存成功", Toast.LENGTH_SHORT).show();
                tvClear.setText(DataClearUtil.getTotalCacheSize(this));
                break;
            case R.id.tvEditPwd:
                toActivity(EditPwdActivity.class);
                break;
            case R.id.tvEditPhone:
               bindDialogFragment=new BindDialogFragment();
               bundle=new Bundle();
               bundle.putString("id","a");
               bindDialogFragment.setArguments(bundle);
                bindDialogFragment.show(getSupportFragmentManager(),"手机");
                break;
            case R.id.tvBindWechat:
                bindDialogFragment=new BindDialogFragment();
                bundle=new Bundle();
                bundle.putString("id","b");
                bindDialogFragment.setArguments(bundle);
                bindDialogFragment.show(getSupportFragmentManager(),"微信");
                break;
            case R.id.tvBindQQ:
                bindDialogFragment=new BindDialogFragment();
                bundle=new Bundle();
                bundle.putString("id","c");
                bindDialogFragment.setArguments(bundle);
                bindDialogFragment.show(getSupportFragmentManager(),"QQ");
                break;
            case R.id.tvAdd:
                toActivity(AddStyleActivity.class);

                break;
            case R.id.tvMessage:
                break;
            case R.id.tvChangeAccount:
                toActivity(SwitchAcountActivity.class);
                break;
            case R.id.tvLogout:
                showExitDialog();
                break;
        }
    }

    private void showExitDialog() {
        CommonDialog.Builder  builder = new CommonDialog.Builder();
        builder.setContentMessage("是否退出登录?");
        builder.setDialogButtonClickListener(new CommonDialog.OnDialogButtonClickListener() {
            @Override
            public void onPositiveClick(View v, Bundle bundle) {
                //logout();
                // 通知退出
                list= SpUtils.getDataList("listBean",userLoginInfo.class);
                if(list!=null&&list.size()>0){
                deleAccount(list,list.get(list.size()-1).getUserId());
                }
                BroadcastManager.getInstance(SettingActivity.this).sendBroadcast(SealConst.EXIT);
//                Intent intent=new Intent(mContext,Login01Activity.class);
//                Bundle bundle1=new Bundle();
//                bundle1.putString("flag","0");
//                intent.putExtras(bundle1);
//              //  intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
//                startActivity(intent);
//                finish();
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            }

            @Override
            public void onNegativeClick(View v, Bundle bundle) {

            }
        });
        CommonDialog dialog = builder.build();
        dialog.show(getSupportFragmentManager(), "logout_dialog");
    }
    /**
     * 插入数据前要遍历下有没有有的话删除，保存最新的
     */
    private void deleAccount(List<userLoginInfo> list, String id) {
        if (list != null && list.size() > 0) {
            Iterator it = list.iterator();
            while (it.hasNext()) {
                userLoginInfo userbean = (userLoginInfo) it.next();
                if (id.equals(userbean.getUserId())) {
                    it.remove();
                }
            }
            SpUtils.setDataList("listBean", list);

        }
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        switch (buttonView.getId()){
            case R.id.sw_freind_validation:
                if(!isChecked){
                        mFriendValidation.setChecked(false);
                        flag="0";
                }else {
                        mFriendValidation.setChecked(true);
                        flag="1";
                    }
                    setValidation(flag);

                break;
        }
    }

    private void setValidation(final String flag) {
        AsyncTaskManager.getInstance(mContext).request(SETFRIENDVALIDATION, new OnDataListener() {
            @Override
            public Object doInBackground(int requestCode, String parameter) throws HttpException {
                return new SealAction(mContext).setNeedValidated(SharedPreferencesUtil.getString(mContext,"userId",""),flag);
            }

            @Override
            public void onSuccess(int requestCode, Object result) {
                if (result != null) {
                    setNeedValidatedRespone needValidatedRespone=(setNeedValidatedRespone)result;
                    if(needValidatedRespone.isSuccess()==true){
                       // NToast.shortToast(mContext,needValidatedRespone.getMessage());
                    }else {
                        NToast.shortToast(mContext,needValidatedRespone.getMessage());
                    }
                }
            }

            @Override
            public void onFailure(int requestCode, int state, Object result) {

            }
        });
    }
}
