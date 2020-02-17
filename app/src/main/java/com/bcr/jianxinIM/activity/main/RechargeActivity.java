package com.bcr.jianxinIM.activity.main;


import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bcr.jianxinIM.R;
import com.bcr.jianxinIM.im.ui.activity.BaseActivity;
import com.bcr.jianxinIM.util.AppManager;


/**
 * 充值
 */
public class RechargeActivity extends BaseActivity {

    private String flag;
    private ImageView ivBack;
    private TextView tvTitle;
    private TextView tvTitle01,tvTitle02;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recharge);
        AppManager.getAppManager().addActivity(this);
        flag=getIntent().getExtras().getString("flag");
        initView();
    }

    private void initView() {
        ivBack=findViewById(R.id.titlebar_iv_left);
        ivBack.setVisibility(View.VISIBLE);
        ivBack.setOnClickListener(v->onBackPressed());
        tvTitle=findViewById(R.id.titlebar_tv);
        tvTitle01=findViewById(R.id.tvTitle);
        tvTitle02=findViewById(R.id.tvTitle01);
        if("1".equals(flag)){
            setTitle("充值");
            tvTitle01.setText("充值金额");
            tvTitle02.setText("充值方式");
        } else if("2".equals(flag)){
            setTitle("提现");
            tvTitle01.setText("提现金额");
            tvTitle02.setText("提现方式");
        }
    }

}
