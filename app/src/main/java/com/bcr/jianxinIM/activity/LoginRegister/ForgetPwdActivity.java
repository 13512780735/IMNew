package com.bcr.jianxinIM.activity.LoginRegister;

import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bcr.jianxinIM.R;
import com.bcr.jianxinIM.activity.base_util.ToastUtils;
import com.bcr.jianxinIM.im.SealConst;
import com.bcr.jianxinIM.im.server.SealAction;
import com.bcr.jianxinIM.im.server.network.async.AsyncTaskManager;
import com.bcr.jianxinIM.im.server.network.async.OnDataListener;
import com.bcr.jianxinIM.im.server.network.http.HttpException;
import com.bcr.jianxinIM.im.server.response.RestPasswordResponse;
import com.bcr.jianxinIM.im.server.response.SendCodeResponse;
import com.bcr.jianxinIM.im.server.utils.CommonUtils;
import com.bcr.jianxinIM.im.server.utils.NToast;
import com.bcr.jianxinIM.im.server.widget.ClearWriteEditText;
import com.bcr.jianxinIM.im.server.widget.LoadDialog;
import com.bcr.jianxinIM.im.ui.activity.BaseActivity;
import com.bcr.jianxinIM.listener.IEditTextsChangeListener;
import com.bcr.jianxinIM.util.AppManager;
import com.bcr.jianxinIM.util.SharedPreferencesUtil;
import com.bcr.jianxinIM.util.StringUtil;
import com.bcr.jianxinIM.util.TextSizeCheckUtil;
import com.bcr.jianxinIM.view.BorderTextView;
import com.orhanobut.logger.Logger;
import com.wang.avi.AVLoadingIndicatorView;

public class ForgetPwdActivity extends BaseActivity implements View.OnClickListener {
    private static final int CHANGE_PASSWORD = 33;
    private static final int SENDCODE =236 ;
    private ClearWriteEditText edAccount,edCode,edPwd;
    private TextView tvSendCode;
    private BorderTextView tvFinish;
    private ImageView titlebar_iv_left;
    private TextView titlebar_tv;
    private TextSizeCheckUtil checkUtil;
    private LinearLayout llLoadingBg;
    private AVLoadingIndicatorView albetlistLoading;
    private String mobile;

    private String password,code;
    private ClearWriteEditText edConfirmPwd;
    private String confirmPwd;
    private SharedPreferences sp;
    private SharedPreferences.Editor editor;
    private int codeTime=0;
    private TimeCount time;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forget_pwd);
        AppManager.getAppManager().addActivity(this);
        setTitle("找回密码");
        sp = getSharedPreferences("config", MODE_PRIVATE);
        editor = sp.edit();
        initView();
    }
    private void initCode(){
        mobile = edAccount.getText().toString().trim();
        if (StringUtil.isBlank(mobile)) {
            ToastUtils.showToast(ForgetPwdActivity.this, "手机号不能为空");
            return;
        }

        AsyncTaskManager.getInstance(mContext).request(SENDCODE, new OnDataListener() {
            @Override
            public Object doInBackground(int requestCode, String parameter) throws HttpException {
                return new SealAction(mContext).sendCode(mobile,"Update");
            }

            @Override
            public void onSuccess(int requestCode, Object result) {
                if (result != null) {
                    SendCodeResponse sendCodeResponse= (SendCodeResponse) result;
                    if(sendCodeResponse.isSuccess()==true){
                        codeTime=sendCodeResponse.getResultData();
                        Logger.d("codeTime:-->"+codeTime);
                        if(codeTime==0){
                            time = new TimeCount(180000, 1000);
                        }else {
                            time=new TimeCount(codeTime*1000, 1000);
                        }
                        time.start();
                    }
                }
            }

            @Override
            public void onFailure(int requestCode, int state, Object result) {

            }
        });
    }
    private void initView() {
        llLoadingBg=findViewById(R.id.llLoadingBg);
        albetlistLoading=findViewById(R.id.alLoginLoading);
//        titlebar_iv_left=findViewById(R.id.titlebar_iv_left);
//        titlebar_tv=findViewById(R.id.titlebar_tv);
        edAccount=findViewById(R.id.edAccount);
        edCode=findViewById(R.id.edCode);
        edPwd=findViewById(R.id.edPwd);
        edConfirmPwd=findViewById(R.id.edConfirmPwd);
        tvSendCode=findViewById(R.id.tvCode);
        tvFinish=findViewById(R.id.tvFinish);
        tvFinish.setOnClickListener(this);
        tvSendCode.setOnClickListener(this);
//        titlebar_iv_left.setOnClickListener(this);
//        titlebar_iv_left.setVisibility(View.VISIBLE);
//        titlebar_tv.setText("找回密码");
        initWatcher();
    }
    private void initWatcher() {
        checkUtil = TextSizeCheckUtil.getInstance().setBtn(tvFinish).
                addAllEditText(edAccount, edPwd)
                .setChangeListener(new IEditTextsChangeListener() {
                    @Override
                    public void textChange(boolean isAllHasContent) {
                        //按钮颜色变化时的操作(按钮颜色变化已经在工具中设置)
                        if (isAllHasContent) {
                            tvFinish.setContentColorResource01(getResources().getColor(R.color.colorPrimary));
                            tvFinish.setStrokeColor01(getResources().getColor(R.color.colorPrimary));
                            tvFinish.setOnClickListener(ForgetPwdActivity.this);
                        } else {
                            tvFinish.setContentColorResource01(Color.parseColor("#999999"));
                            tvFinish.setStrokeColor01(Color.parseColor("#999999"));
                        }
                    }
                });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (checkUtil != null)
            checkUtil.removeWatcher();
    }


    public void LoadingDisplay(boolean isShow) {
        if (isShow) {
            llLoadingBg.setVisibility(View.VISIBLE);
            albetlistLoading.setVisibility(View.VISIBLE);
        } else {
            llLoadingBg.setVisibility(View.GONE);
            albetlistLoading.setVisibility(View.GONE);
        }
    }
    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.tvFinish:
              toFinishto();
                break;
            case R.id.tvCode:
                initCode();
                break;
        }
    }

    private void toFinishto() {
        mobile = edAccount.getText().toString().trim();
        password = edPwd.getText().toString().trim();
        confirmPwd = edConfirmPwd.getText().toString().trim();
        code = edCode.getText().toString().trim();
        if (StringUtil.isBlank(mobile)) {
            ToastUtils.showToast(ForgetPwdActivity.this,"手机不能为空");
            return;
        }
        if (StringUtil.isBlank(password)&&StringUtil.isBlank(confirmPwd)) {
            ToastUtils.showToast(ForgetPwdActivity.this,"密码不能为空");
            return;
        }
        if(!confirmPwd.equals(password)){
            ToastUtils.showToast(ForgetPwdActivity.this,"密码不一致");
            return;
        }
        if (StringUtil.isBlank(code)) {
            ToastUtils.showToast(ForgetPwdActivity.this,"验证码不能为空");
            return;
        }
        LoadDialog.show(this,"loading...",false);
        request(CHANGE_PASSWORD,true);
        //toNext();
    }

    @Override
    public Object doInBackground(int requestCode, String id) throws HttpException {
        switch (requestCode) {
//            case CHECK_PHONE:
//                return action.checkPhoneAvailable("86", phone);
//            case SEND_CODE:
//                return action.sendCode("86", phone);
            case CHANGE_PASSWORD:
                return action.restPassword(mobile,code,password,confirmPwd);
//            case VERIFY_CODE:
//                return action.verifyCode("86", phone, mCode.getText().toString());
        }
        return super.doInBackground(requestCode, id);
    }

    @Override
    public void onSuccess(int requestCode, Object result) {
        if (result != null) {
              switch (requestCode){
                  case CHANGE_PASSWORD:
                      RestPasswordResponse restPasswordResponse= (RestPasswordResponse) result;
                        if(restPasswordResponse.isSuccess()==true){
                            LoadDialog.dismiss(this);
                            editor.putString(SealConst.SEALTALK_LOGING_PHONE, mobile);
                            editor.putString(SealConst.SEALTALK_LOGING_PASSWORD, password);
                            editor.putString(SealConst.SEALTALK_LOGIN_ID, restPasswordResponse.getResultData().getUserId());
                            editor.putString(SealConst.SEALTALK_USERID, restPasswordResponse.getResultData().getUserId());
                            editor.commit();
                            String rememberpwd=SharedPreferencesUtil.getString(mContext,"remember_password","");
                            SharedPreferencesUtil.putString(mContext,"remember_password",rememberpwd);
                            SharedPreferencesUtil.putString(mContext,"phoneString",mobile);
                            SharedPreferencesUtil.putString(mContext,"passwordString",password);
                            toNext();
                        }else {
                            LoadDialog.dismiss(this);
                            NToast.shortToast(this,restPasswordResponse.getMessage());
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
        switch (requestCode) {
            case CHANGE_PASSWORD:
                RestPasswordResponse restPasswordResponse= (RestPasswordResponse) result;
                LoadDialog.dismiss(this);
                NToast.shortToast(this,restPasswordResponse.getMessage());
                break;
        }
    }

    private void toNext() {
        Bundle bundle1=new Bundle();
        bundle1.putString("flag","0");
        toActivity(Login01Activity.class,bundle1);
        finish();
    }
    private void sendCode() {

    }
    class TimeCount extends CountDownTimer {
        public TimeCount(long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval);
        }

        @Override
        public void onFinish() {// 计时完毕
            tvSendCode.setText("获取验证码");
            tvSendCode.setClickable(true);
        }

        @Override
        public void onTick(long millisUntilFinished) {// 计时过程
            tvSendCode.setClickable(false);//防止重复点击
            tvSendCode.setText(millisUntilFinished / 1000 + "s");
        }
    }
}
