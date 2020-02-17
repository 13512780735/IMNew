package com.bcr.jianxinIM.activity.LoginRegister;

import android.graphics.Color;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.widget.TextView;
import com.bcr.jianxinIM.R;
import com.bcr.jianxinIM.activity.base_util.ToastUtils;
import com.bcr.jianxinIM.im.server.widget.ClearWriteEditText;
import com.bcr.jianxinIM.im.ui.activity.BaseActivity;
import com.bcr.jianxinIM.listener.IEditTextsChangeListener;
import com.bcr.jianxinIM.util.StringUtil;
import com.bcr.jianxinIM.util.TextSizeCheckUtil;
import com.bcr.jianxinIM.view.BorderTextView;


public class BindPhoneActivity extends BaseActivity implements View.OnClickListener{

    ClearWriteEditText edAccount;
    ClearWriteEditText edCode;
    TextView tvCode;
    ClearWriteEditText edPwd;
    BorderTextView tvFinish;
    private TextSizeCheckUtil checkUtil;
    private String mobile;
   TimeCount time = new TimeCount(60000, 1000);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bind_phone);
        setTitle("绑定手机");
        initView();

    }
    private void initView(){
        edAccount=findViewById(R.id.edAccount);
        edCode=findViewById(R.id.edCode);
        tvCode=findViewById(R.id.tvCode);
        edPwd=findViewById(R.id.edPwd);
        tvFinish=findViewById(R.id.tvFinish);

        tvCode.setOnClickListener(this);
        tvFinish.setOnClickListener(this);
        initWatcher();
    }
    private void initWatcher() {
        checkUtil = TextSizeCheckUtil.getInstance().setBtn(tvFinish).
                addAllEditText(edAccount, edPwd,edCode)
                .setChangeListener(new IEditTextsChangeListener() {
                    @Override
                    public void textChange(boolean isAllHasContent) {
                        //按钮颜色变化时的操作(按钮颜色变化已经在工具中设置)
                        if (isAllHasContent) {
                            tvFinish.setContentColorResource01(getResources().getColor(R.color.colorPrimary));
                            tvFinish.setStrokeColor01(getResources().getColor(R.color.colorPrimary));
                            tvFinish.setOnClickListener(BindPhoneActivity.this);
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

    private void sendCode() {
        mobile = edAccount.getText().toString().trim();
        if (StringUtil.isBlank(mobile)) {
            ToastUtils.showToast(BindPhoneActivity.this, "手机号不能为空");
            return;
        }
        if (!(StringUtil.isCellPhone(mobile))) {
            ToastUtils.showToast(BindPhoneActivity.this, "请输入正确的手机号码");
            return;
        } else {
            // SMSSDK.getVerificationCode("86", mobile);
            // VerificationCode();
            time.start();
            //LoaddingShow();
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tvCode:
                sendCode();
                break;
            case R.id.tvFinish:
                break;
        }
    }

    class TimeCount extends CountDownTimer {
        public TimeCount(long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval);
        }

        @Override
        public void onFinish() {// 计时完毕
            tvCode.setText("获取验证码");
            tvCode.setClickable(true);
        }

        @Override
        public void onTick(long millisUntilFinished) {// 计时过程
            tvCode.setClickable(false);//防止重复点击
            tvCode.setText(millisUntilFinished / 1000 + "s");
        }
    }
}
