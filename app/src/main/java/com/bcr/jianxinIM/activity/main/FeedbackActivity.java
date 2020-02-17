package com.bcr.jianxinIM.activity.main;


import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bcr.jianxinIM.R;
import com.bcr.jianxinIM.im.server.SealAction;
import com.bcr.jianxinIM.im.server.network.async.AsyncTaskManager;
import com.bcr.jianxinIM.im.server.network.async.OnDataListener;
import com.bcr.jianxinIM.im.server.network.http.HttpException;
import com.bcr.jianxinIM.im.server.response.UserFeedBackRespone;
import com.bcr.jianxinIM.im.server.utils.NToast;
import com.bcr.jianxinIM.im.ui.activity.BaseActivity;
import com.bcr.jianxinIM.util.AppManager;
import com.bcr.jianxinIM.util.SharedPreferencesUtil;
import com.bcr.jianxinIM.util.TextSizeCheckUtil;
import com.bcr.jianxinIM.view.BorderTextView;


public class FeedbackActivity extends BaseActivity implements View.OnClickListener {
    private static final int UPLOADFEEDBACK =227 ;
    private ImageView ivBack;
    private TextView tvTitle;
    private BorderTextView tvConfirm;
    private TextSizeCheckUtil checkUtil;
    private EditText edTitle,edContent;
    private TextView textView1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feedback);
        setTitle("帮助与反馈");
        AppManager.getAppManager().addActivity(this);
        initView();
    }

    private void initView() {
//        ivBack=findViewById(R.id.titlebar_iv_left);
//        ivBack.setVisibility(View.VISIBLE);
//        ivBack.setOnClickListener(v->onBackPressed());
//        tvTitle=findViewById(R.id.titlebar_tv);
//        tvTitle.setText("帮助与反馈");
        tvConfirm=findViewById(R.id.tv_confirm);
        edTitle=findViewById(R.id.edTitle);
        edContent=findViewById(R.id.edContent);
        textView1=findViewById(R.id.textView1);
        tvConfirm.setOnClickListener(this);
        initWatcher();
        edContent.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {


            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {


            }

            @Override
            public void afterTextChanged(Editable s) {
                textView1.setText(String.valueOf(s.length())+"/500");
                if(s.length()>=500){
                    Toast.makeText(FeedbackActivity.this, "上限了，亲", Toast.LENGTH_SHORT).show();
                }

            }
        });
    }


    private void initWatcher() {
        checkUtil = TextSizeCheckUtil.getInstance().setBtn(tvConfirm).
                addAllEditText(edTitle, edContent)
                .setChangeListener(isAllHasContent -> {
                    //按钮颜色变化时的操作(按钮颜色变化已经在工具中设置)
                    if (isAllHasContent) {
                        tvConfirm.setContentColorResource01(getResources().getColor(R.color.colorPrimary));
                        tvConfirm.setStrokeColor01(getResources().getColor(R.color.colorPrimary));
                        tvConfirm.setOnClickListener(FeedbackActivity.this);
                    } else {
                        tvConfirm.setContentColorResource01(Color.parseColor("#999999"));
                        tvConfirm.setStrokeColor01(Color.parseColor("#999999"));
                    }
                });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (checkUtil != null)
            checkUtil.removeWatcher();
    }
    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.tv_confirm:
               uploadData();
                break;
        }
    }

    private void uploadData() {
        AsyncTaskManager.getInstance(mContext).request(UPLOADFEEDBACK, new OnDataListener() {
            @Override
            public Object doInBackground(int requestCode, String parameter) throws HttpException {
                return new SealAction(mContext).SetUserFeedBack(SharedPreferencesUtil.getString(mContext,"userId",""),edTitle.getText().toString(),edContent.getText().toString());
            }

            @Override
            public void onSuccess(int requestCode, Object result) {
                if (result != null) {
                    UserFeedBackRespone userFeedBackRespone= (UserFeedBackRespone) result;
                    if(userFeedBackRespone.isSuccess()==true){
                        NToast.shortToast(mContext,userFeedBackRespone.getMessage());
                        finish();
                    }else {
                        NToast.shortToast(mContext,userFeedBackRespone.getMessage());
                    }
                }
            }

            @Override
            public void onFailure(int requestCode, int state, Object result) {

            }
        });
    }
}
