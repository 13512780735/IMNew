package com.bcr.jianxinIM.fragment.main;


import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v4.app.DialogFragment;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bcr.jianxinIM.R;
import com.bcr.jianxinIM.activity.base_util.ToastUtils;
import com.bcr.jianxinIM.util.StringUtil;


public class BindDialogFragment extends DialogFragment implements View.OnClickListener {

    private View mRootView;
    private String id;
    private TextView tvTitle,tvCode,tv_cancel,tv_sure;
    private EditText edPhone,edPwd,edCode;
    private LinearLayout llCode;
    private LinearLayout linear_bottom;
TimeCount time = new TimeCount(60000, 1000);
    private String phone;

    @Override
    public void onStart() {
        super.onStart();
        DisplayMetrics dm = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(dm);
        getDialog().getWindow().setLayout(dm.widthPixels, getDialog().getWindow().getAttributes().height);
        getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if(mRootView==null){
            mRootView = inflater.inflate(R.layout.fragment_bind_dialog, container,false);
        }

        id=getArguments().getString("id");
        Log.d("id",id);
        initUI(mRootView);
        return mRootView;
    }

    private void initUI(View mView) {
        tvTitle=mView.findViewById(R.id.tvTitle);
        edPhone=mView.findViewById(R.id.edPhone);
        llCode=mView.findViewById(R.id.llCode);
        edCode=mView.findViewById(R.id.edCode);
        tvCode=mView.findViewById(R.id.tvCode);
        edPwd=mView.findViewById(R.id.edPwd);
        linear_bottom=mView.findViewById(R.id.linear_bottom);
        tv_cancel=mView.findViewById(R.id.tv_cancel);
        tv_sure=mView.findViewById(R.id.tv_sure);
        Log.d("id111",id);
        if("a".equals(id)){
            tvTitle.setText("绑定手机");
            edPhone.setVisibility(View.VISIBLE);
            llCode.setVisibility(View.VISIBLE);
            edPhone.setHint("请输入手机号");
            edCode.setHint("请输入验证码");
        }else if("b".equals(id)){
            tvTitle.setText("绑定微信");
            edPhone.setVisibility(View.VISIBLE);
            edPwd.setVisibility(View.VISIBLE);
            edPhone.setHint("请输入微信号");
            edPwd.setHint("请输入密码");
        }else  if("c".equals(id)){
            tvTitle.setText("绑定QQ");
            edPhone.setVisibility(View.VISIBLE);
            edPwd.setVisibility(View.VISIBLE);
            edPhone.setHint("请输入QQ号");
            edPwd.setHint("请输入密码");
        }
        tv_cancel.setOnClickListener(this);
        tv_sure.setOnClickListener(this);
        tvCode.setOnClickListener(BindDialogFragment.this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case  R.id.tv_cancel:
                dismiss();
                break;
            case  R.id.tv_sure:
                dismiss();
                break;
            case R.id.tvCode:
                sendCode();
                break;
        }
    }
    private void sendCode() {
        phone = edPhone.getText().toString().trim();
        if (StringUtil.isBlank(phone)) {
            ToastUtils.showToast(getActivity(), "手机号不能为空");
            return;
        }
        if (!(StringUtil.isCellPhone(phone))) {
            ToastUtils.showToast(getActivity(), "请输入正确的手机号码");
            return;
        } else {
            // SMSSDK.getVerificationCode("86", mobile);
            // VerificationCode();
            time.start();
            //  LoaddingShow();
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
