package com.bcr.jianxinIM.activity.LoginRegister;


import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bcr.jianxinIM.R;
import com.bcr.jianxinIM.activity.base_util.ToastUtils;
import com.bcr.jianxinIM.im.SealConst;
import com.bcr.jianxinIM.im.server.SealAction;
import com.bcr.jianxinIM.im.server.network.async.AsyncTaskManager;
import com.bcr.jianxinIM.im.server.network.async.OnDataListener;
import com.bcr.jianxinIM.im.server.network.http.HttpException;
import com.bcr.jianxinIM.im.server.response.RegisterResponse;
import com.bcr.jianxinIM.im.server.response.SendCodeResponse;
import com.bcr.jianxinIM.im.server.utils.CommonUtils;
import com.bcr.jianxinIM.im.server.utils.NToast;
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
import com.bcr.jianxinIM.im.server.widget.ClearWriteEditText;
import com.wuhenzhizao.titlebar.widget.CommonTitleBar;

public class RegisterActivity extends BaseActivity implements View.OnClickListener {
//    private static final int CHECK_PHONE = 1;
//    private static final int SEND_CODE = 2;
//    private static final int VERIFY_CODE = 3;
    private static final int REGISTER = 4;
    private static final int REGISTER_BACK = 1001;
    private static final int SENDCODE =236 ;
    private ClearWriteEditText edAccount;
    private ClearWriteEditText edCode;
    private ClearWriteEditText edPwd;
    private ClearWriteEditText edConfirmPwd;
    private TextView tvSendCode,tvLogin;
    private BorderTextView tvRegister;
    private CheckBox checkBox;
    private String mobile,password,code;
    private LinearLayout llLoadingBg;
    private AVLoadingIndicatorView albetlistLoading;
    private CommonTitleBar titlebar;
    private RelativeLayout rlBack;
    private TextSizeCheckUtil checkUtil;
    private String confirmPwd;
    private LinearLayout llBg;
    private SharedPreferences sp;
    private SharedPreferences.Editor editor;
    private int codeTime=0;
    private TimeCount time;
    private TextView protocol_tv01,protocol_tv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
       // StatusBarUtils.setTranslucent(this);
        setContentView(R.layout.activity_register);
        setHeadVisibility(View.GONE);
        AppManager.getAppManager().addActivity(this);
        sp = getSharedPreferences("config", MODE_PRIVATE);
        editor = sp.edit();
        ininView();
    }
    private void initCode(){
        mobile = edAccount.getText().toString().trim();
        if (StringUtil.isBlank(mobile)) {
            ToastUtils.showToast(RegisterActivity.this, "手机号不能为空");
            return;
        }

        AsyncTaskManager.getInstance(mContext).request(SENDCODE, new OnDataListener() {
            @Override
            public Object doInBackground(int requestCode, String parameter) throws HttpException {
                return new SealAction(mContext).sendCode(mobile,"Register");
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
    private void ininView() {
        rlBack=findViewById(R.id.rlBack);
        llLoadingBg=findViewById(R.id.llLoadingBg);
        albetlistLoading=findViewById(R.id.alLoginLoading);
        edAccount=findViewById(R.id.edAccount);
        edCode=findViewById(R.id.edCode);
        edPwd=findViewById(R.id.edPwd);
        llBg=findViewById(R.id.llBg);
        edConfirmPwd=findViewById(R.id.edConfirmPwd);
        tvSendCode=findViewById(R.id.tvCode);
        tvRegister=findViewById(R.id.tvRegister);
        tvLogin=findViewById(R.id.tvLogin);
        checkBox = findViewById(R.id.checkbox);
        protocol_tv=findViewById(R.id.protocol_tv);
        protocol_tv01=findViewById(R.id.protocol_tv01);
        WindowManager wm = (WindowManager) this
                .getSystemService(Context.WINDOW_SERVICE);
        int height = wm.getDefaultDisplay().getHeight();
        int width= wm.getDefaultDisplay().getWidth();

        LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) llBg.getLayoutParams();
        //layoutParams.height=height/5*3;
        layoutParams.width=width/5*4;
        llBg.setLayoutParams(layoutParams);
        llBg.setGravity(Gravity.CENTER);
        tvRegister.setOnClickListener(this);
        tvLogin.setOnClickListener(this);
        tvSendCode.setOnClickListener(this);
        rlBack.setOnClickListener(this);
        protocol_tv.setOnClickListener(this);
        protocol_tv01.setOnClickListener(this);
        initWatcher();
    }
    private void initWatcher() {
        checkUtil = TextSizeCheckUtil.getInstance().setBtn(tvLogin).
                addAllEditText(edAccount, edPwd,edCode,edConfirmPwd)
                .setChangeListener(new IEditTextsChangeListener() {
                    @Override
                    public void textChange(boolean isAllHasContent) {
                        //按钮颜色变化时的操作(按钮颜色变化已经在工具中设置)
                        if (isAllHasContent) {
                            tvRegister.setContentColorResource01(getResources().getColor(R.color.colorPrimary));
                            tvRegister.setStrokeColor01(getResources().getColor(R.color.colorPrimary));
                            tvRegister.setOnClickListener(RegisterActivity.this);
                        } else {
                            tvRegister.setContentColorResource01(Color.parseColor("#999999"));
                            tvRegister.setStrokeColor01(Color.parseColor("#999999"));
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
            case R.id.rlBack:
               onBackPressed();
                break;
            case R.id.tvRegister:
                toRegister();
                break;
            case R.id.tvCode:
                initCode();
                break;
            case R.id.tvLogin:
                 onBackPressed();
                break;
            case R.id.protocol_tv:
            case R.id.protocol_tv01:
                toActivity(PrivacyActivity.class);
                break;
        }
    }
    private void sendCode() {
        mobile = edAccount.getText().toString().trim();
        if (StringUtil.isBlank(mobile)) {
            ToastUtils.showToast(RegisterActivity.this, "手机号不能为空");
            return;
        }
        if (!(StringUtil.isCellPhone(mobile))) {
            ToastUtils.showToast(RegisterActivity.this, "请输入正确的手机号码");
            return;
        } else {
            // SMSSDK.getVerificationCode("86", mobile);
           // VerificationCode();
            time.start();
            //LoaddingShow();
        }
    }
    private void toRegister() {
        mobile = edAccount.getText().toString().trim();
        password = edPwd.getText().toString().trim();
        confirmPwd = edConfirmPwd.getText().toString().trim();
        code = edCode.getText().toString().trim();
        if (StringUtil.isBlank(password)&&StringUtil.isBlank(confirmPwd)) {
            ToastUtils.showToast(RegisterActivity.this,"密码不能为空");
            return;
        }
        if (StringUtil.isBlank(mobile)) {
            ToastUtils.showToast(RegisterActivity.this,"手机不能为空");
            return;
        }
        if (!confirmPwd.equals(password)) {
            ToastUtils.showToast(RegisterActivity.this,"密码不一致");
            return;
        }
        if (StringUtil.isBlank(code)) {
            ToastUtils.showToast(RegisterActivity.this,"验证码不能为空");
            return;
        }
        if (!checkBox.isChecked()) {
            ToastUtils.showToast(RegisterActivity.this,"请同意条款");
            return;
        }
        //toNext();
        LoadDialog.show(this,"loading...",false);
        request(REGISTER, true);
    }
    @Override
    public Object doInBackground(int requestCode, String id) throws HttpException {
        switch (requestCode) {
            case REGISTER:
                return action.register(mobile, code,password, confirmPwd);
        }
        return super.doInBackground(requestCode, id);
    }


    @Override
    public void onSuccess(int requestCode, Object result) {
        if (result != null) {
            switch (requestCode) {

                case REGISTER:
                    RegisterResponse rres = (RegisterResponse) result;
                    if(rres.isSuccess()==true){
                        LoadDialog.dismiss(mContext);
                        NToast.shortToast(mContext, R.string.register_success);
//                        Intent data = new Intent();
//                        data.putExtra("phone", mobile);
//                        data.putExtra("password", password);
//                        data.putExtra("id", rres.getResultData().getUserId());
//                        setResult(REGISTER_BACK, data);
                        editor.putString(SealConst.SEALTALK_LOGING_PHONE, mobile);
                        editor.putString(SealConst.SEALTALK_LOGING_PASSWORD, password);
                        editor.putString(SealConst.SEALTALK_LOGIN_ID, rres.getResultData().getUserId());
                        editor.putString(SealConst.SEALTALK_USERID, rres.getResultData().getUserId());
                        editor.commit();
                        //String rememberpwd=SharedPreferencesUtil.getString(mContext,"remember_password","");
                        SharedPreferencesUtil.putString(mContext,"remember_password","0");
                        SharedPreferencesUtil.putString(mContext,"phoneString",mobile);
                        SharedPreferencesUtil.putString(mContext,"passwordString",password);
                        toNext();
                        //this.finish();
                    }else if (rres.isSuccess()== false) {
                        LoadDialog.dismiss(mContext);
                        NToast.shortToast(mContext, rres.getMessage());
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
            case REGISTER:
                LoadDialog.dismiss(mContext);
                RegisterResponse rres = (RegisterResponse) result;
                NToast.shortToast(mContext, rres.getMessage());
                break;
        }
    }


    private void toNext() {
        Intent intent=new Intent(RegisterActivity.this,PerfectActivity.class);
        startActivity(intent);
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
