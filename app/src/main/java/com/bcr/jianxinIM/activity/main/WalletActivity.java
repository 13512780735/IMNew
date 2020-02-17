package com.bcr.jianxinIM.activity.main;


import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bcr.jianxinIM.R;
import com.bcr.jianxinIM.im.ui.activity.BaseActivity;
import com.bcr.jianxinIM.util.AppManager;
import com.bcr.jianxinIM.view.BorderTextView;


/**
 * 钱包
 */
public class WalletActivity extends BaseActivity implements View.OnClickListener {

    private ImageView ivBack;
    private TextView tvTitle;
    private TextView tvRight;
    private BorderTextView tvRecharge,tvWithdrawal;
    private Bundle bundle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wallet);
        setHeadVisibility(View.GONE);
        AppManager.getAppManager().addActivity(this);
        initView();
    }

    private void initView() {
        ivBack=findViewById(R.id.titlebar_iv_left);
        tvTitle=findViewById(R.id.titlebar_tv);
        tvRight=findViewById(R.id.titlebar_tv_right);
        ivBack.setVisibility(View.VISIBLE);
        ivBack.setOnClickListener(v->onBackPressed());
        tvRight.setVisibility(View.VISIBLE);
        tvTitle.setText("我的钱包");
        tvRight.setText("明细");
        tvRecharge=findViewById(R.id.tvRecharge);
        tvWithdrawal=findViewById(R.id.tvWithdrawal);
        tvRight.setOnClickListener(this);
        tvRecharge.setOnClickListener(this);
        tvWithdrawal.setOnClickListener(this);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.titlebar_tv_right:
                toActivity(WalletDetailsActivity.class);
                break;
            case R.id.tvRecharge:
                bundle=new Bundle();
                bundle.putString("flag","1");
                toActivity(RechargeActivity.class,bundle);
                break;
            case R.id.tvWithdrawal:
                bundle=new Bundle();
                bundle.putString("flag","2");
                toActivity(RechargeActivity.class,bundle);
                break;
        }
    }
}
