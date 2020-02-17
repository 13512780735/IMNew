package com.bcr.jianxinIM.activity.main;


import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.orhanobut.logger.Logger;
import com.bcr.jianxinIM.R;
import com.bcr.jianxinIM.activity.LoginRegister.Login01Activity;
import com.bcr.jianxinIM.im.SealConst;
import com.bcr.jianxinIM.im.server.broadcast.BroadcastManager;
import com.bcr.jianxinIM.im.server.network.http.HttpException;
import com.bcr.jianxinIM.im.server.response.UpdatePwdResponse;
import com.bcr.jianxinIM.im.server.utils.CommonUtils;
import com.bcr.jianxinIM.im.server.utils.NToast;
import com.bcr.jianxinIM.im.server.widget.ClearWriteEditText;
import com.bcr.jianxinIM.im.server.widget.LoadDialog;
import com.bcr.jianxinIM.im.ui.activity.BaseActivity;
import com.bcr.jianxinIM.util.AppManager;
import com.bcr.jianxinIM.util.SharedPreferencesUtil;
import com.bcr.jianxinIM.util.TextSizeCheckUtil;
import com.bcr.jianxinIM.view.BorderTextView;


public class EditPwdActivity extends BaseActivity implements View.OnClickListener {

    private static final int EDITPWD =202 ;
    private ImageView ivBack;
    private TextView tvTitle;
   // private BorderTextView tvConfirm;
    private ClearWriteEditText edOldPwd,edNewPwd,edConfirmPwd;
    private TextSizeCheckUtil checkUtil;
    private String oldPwd,pwd,confirmPwd;
    private SharedPreferences sp;
    private SharedPreferences.Editor editor;
    private TextView tvRight;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_pwd);
        setHeadVisibility(View.GONE);
        AppManager.getAppManager().addActivity(this);
        sp = getSharedPreferences("config", MODE_PRIVATE);
        editor = sp.edit();
        initView();
    }

    private void initView() {
        ivBack=findViewById(R.id.titlebar_iv_left);
        ivBack.setVisibility(View.VISIBLE);
        ivBack.setOnClickListener(v -> onBackPressed());
        tvTitle=findViewById(R.id.titlebar_tv);
        tvRight=findViewById(R.id.titlebar_tv_right);
        tvTitle.setText("修改密码");
        tvRight.setVisibility(View.VISIBLE);
        tvRight.setText("确认");
        edOldPwd=findViewById(R.id.edOldPwd);
        edNewPwd=findViewById(R.id.edNewPwd);
        edConfirmPwd=findViewById(R.id.edConfirmPwd);
       // tvConfirm=findViewById(R.id.tv_confirm);
        tvRight.setOnClickListener(this);
        //initWatcher();
    }
//    private void initWatcher() {
//        checkUtil = TextSizeCheckUtil.getInstance().setBtn(tvConfirm).
//                addAllEditText(edOldPwd, edNewPwd,edConfirmPwd)
//                .setChangeListener(isAllHasContent -> {
//                    //按钮颜色变化时的操作(按钮颜色变化已经在工具中设置)
//                    if (isAllHasContent) {
//                        tvConfirm.setContentColorResource01(getResources().getColor(R.color.colorPrimary));
//                        tvConfirm.setStrokeColor01(getResources().getColor(R.color.colorPrimary));
//                        tvConfirm.setOnClickListener(EditPwdActivity.this);
//                    } else {
//                        tvConfirm.setContentColorResource01(Color.parseColor("#999999"));
//                        tvConfirm.setStrokeColor01(Color.parseColor("#999999"));
//                    }
//                });
//    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (checkUtil != null)
            checkUtil.removeWatcher();
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.titlebar_tv_right:
                oldPwd=edOldPwd.getText().toString();
                pwd=edNewPwd.getText().toString();
                confirmPwd=edConfirmPwd.getText().toString();
                if (TextUtils.isEmpty(oldPwd)&&TextUtils.isEmpty(pwd)&&TextUtils.isEmpty(confirmPwd)) {
                    NToast.shortToast(mContext, "密码不能为空");
                    edOldPwd.setShakeAnimation();
                    return;
                }if(!confirmPwd.equals(pwd)){
                    NToast.shortToast(this,"密码不一致");
                    return;
                 }
                LoadDialog.show(this,"loading...",false);
                request(EDITPWD,true);
                break;
        }
    }

    @Override
    public Object doInBackground(int requestCode, String id) throws HttpException {
        switch (requestCode){
            case EDITPWD:
                return action.UpdatePwd(SharedPreferencesUtil.getString(EditPwdActivity.this,"userId",""),oldPwd,pwd,confirmPwd);
        }
        return super.doInBackground(requestCode, id);
    }

    @Override
    public void onSuccess(int requestCode, Object result) {
        if(result!=null){
            switch (requestCode){
                case EDITPWD:
                    UpdatePwdResponse updatePwdResponse= (UpdatePwdResponse) result;
                    if(updatePwdResponse.isSuccess()==true){
                        Logger.d("修改密码：--》"+updatePwdResponse.getMessage());
                        LoadDialog.dismiss(mContext);
                        BroadcastManager.getInstance(EditPwdActivity.this).sendBroadcast(SealConst.EXIT);
                        editor.putString(SealConst.SEALTALK_LOGING_PASSWORD, confirmPwd);
                        editor.putString(SealConst.SEALTALK_LOGIN_ID, updatePwdResponse.getResultData().getUserId());
                        editor.putString(SealConst.SEALTALK_USERID, updatePwdResponse.getResultData().getUserId());
                        editor.commit();

                        String rememberpwd=SharedPreferencesUtil.getString(mContext,"remember_password","");
                        SharedPreferencesUtil.putString(mContext,"remember_password",rememberpwd);
                        SharedPreferencesUtil.putString(mContext,"passwordString",confirmPwd);
                       // SharedPreferencesUtil.putString(mContext,"phoneString",oldPhone);
                        NToast.shortToast(mContext,updatePwdResponse.getMessage());
                        Bundle bundle1=new Bundle();
                        bundle1.putString("flag","0");
                        toActivity(Login01Activity.class,bundle1);
                        finish();
                        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);

                    }
                    else {
                        LoadDialog.dismiss(mContext);
                        NToast.shortToast(mContext,updatePwdResponse.getMessage());
                    }
                    break;
            }
        }
    }

    @Override
    public void onFailure(int requestCode, int state, Object result) {
        if (!CommonUtils.isNetworkConnected(mContext)) {
            LoadDialog.dismiss(mContext);
            NToast.shortToast(mContext, getString(R.string.network_not_available));
            return;
        }
       switch (requestCode){
           case EDITPWD:
               UpdatePwdResponse updatePwdResponse= (UpdatePwdResponse) result;
               LoadDialog.dismiss(this);
               NToast.shortToast(this,updatePwdResponse.getMessage());
               break;
       }
    }
}
