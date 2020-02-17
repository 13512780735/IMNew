package com.bcr.jianxinIM.im.ui.activity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.orhanobut.logger.Logger;
import com.bcr.jianxinIM.R;
import com.bcr.jianxinIM.im.server.SealAction;
import com.bcr.jianxinIM.im.server.network.async.AsyncTaskManager;
import com.bcr.jianxinIM.im.server.network.async.OnDataListener;
import com.bcr.jianxinIM.im.server.network.http.HttpException;
import com.bcr.jianxinIM.im.server.response.GetGroupInfoResponse;
import com.bcr.jianxinIM.im.server.response.getCodeResponse;
import com.bcr.jianxinIM.im.server.utils.NToast;
import com.bcr.jianxinIM.im.server.widget.LoadDialog;
import com.bcr.jianxinIM.view.CircleImageView;

import io.rong.imageloader.core.ImageLoader;

public class GroupQRCodeActivity extends BaseActivity implements View.OnClickListener {

    private static final int GETCODE =213 ;
    private static final int GETGROUPINFO =214 ;
    private ImageView ivBack;
    private TextView tvTitle;
    private ImageView qrCodeIv;
    private TextView qrCodeDescribeTv;
    String groupId,groupPic,groupName;
    private TextView tvName;
    private CircleImageView ivAvatar;
    private String url;
    private LinearLayout qrCodeCardLl;
    private GetGroupInfoResponse getGroupInfoResponse;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_qrcode);
        setTitle("二维码");
        Intent intent=getIntent();
        groupId=intent.getExtras().getString("groupId");
        groupPic=intent.getExtras().getString("groupPic");
        groupName=intent.getExtras().getString("groupName");

        Logger.d("groupId:-->"+groupId);
        Logger.d("groupPic:-->"+groupPic);
        Logger.d("groupName:-->"+groupName);



        initView();
        initGroupInfo();

        initData();
    }

    private void initGroupInfo() {
        AsyncTaskManager.getInstance(mContext).request(GETGROUPINFO, new OnDataListener() {
            @Override
            public Object doInBackground(int requestCode, String parameter) throws HttpException {
                return new SealAction(mContext).getGroupInfo(groupId);
            }

            @Override
            public void onSuccess(int requestCode, Object result) {
                if (result != null) {
                    getGroupInfoResponse= (GetGroupInfoResponse) result;
                    if(getGroupInfoResponse.isSuccess()==true){
                        //checkIsInGroup(groupId, getGroupInfoResponse.getResultData().getName());
                        tvName.setText(getGroupInfoResponse.getResultData().getName());
                        ImageLoader.getInstance().displayImage(getGroupInfoResponse.getResultData().getPortraitUri(), ivAvatar);

                    }else {
                        NToast.shortToast(mContext,"群组不存在");
                        finish();

                    }
                }
            }

            @Override
            public void onFailure(int requestCode, int state, Object result) {
                NToast.shortToast(mContext,"群组不存在");
                finish();
            }
        });
    }

    private void initData() {
        LoadDialog.show(mContext,"loading...",false);
        request(GETCODE,true);
    }

    @Override
    public Object doInBackground(int requestCode, String id) throws HttpException {
        switch (requestCode) {
            case GETCODE:
                return action.getIamgeCode("group", groupId);
        }
        return super.doInBackground(requestCode, id);
    }

    @Override
    public void onSuccess(int requestCode, Object result) {
        if (result != null) {
            switch (requestCode){
                case GETCODE:
                    getCodeResponse codeResponse= (getCodeResponse) result;
                    if(codeResponse.isSuccess()==true){
                        LoadDialog.dismiss(mContext);
                        NToast.shortToast(mContext,codeResponse.getMessage());
                        url=codeResponse.getResultData();
                        Logger.d("url;-->"+codeResponse.getResultData());
                        ImageLoader.getInstance().displayImage(url, qrCodeIv);
                    }else {
                        LoadDialog.dismiss(mContext);
                        NToast.shortToast(mContext,codeResponse.getMessage());
                    }
                    break;
            }
        }
    }

    private void initView() {
//        ivBack=findViewById(R.id.titlebar_iv_left);
//        tvTitle=findViewById(R.id.titlebar_tv);
//        ivBack.setVisibility(View.VISIBLE);
//        ivBack.setOnClickListener(v->onBackPressed());
//        tvTitle.setText("二维码");
        qrCodeDescribeTv = findViewById(R.id.profile_tv_qr_card_info_describe);
        qrCodeDescribeTv.setText("扫一扫二维码，加入群聊");
        qrCodeIv = findViewById(R.id.profile_iv_qr_code);
        tvName = findViewById(R.id.tvName);
        ivAvatar = findViewById(R.id.ivAvatar);
        qrCodeCardLl = findViewById(R.id.profile_fl_card_capture_area_container);
        findViewById(R.id.tvSave).setOnClickListener(this);

    }
    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.tvSave:
                saveQRCodeToLocal();
                break;
        }
    }
    /**
     * 保存二维码到本地
     */
    private void saveQRCodeToLocal() {
        if (!checkHasStoragePermission()) {
            return;
        }
        //  qrCodeViewModel.saveQRCodeToLocal(ViewCapture.getViewBitmap(qrCodeCardLl));
    }
    private boolean checkHasStoragePermission() {
        // 从6.0系统(API 23)开始，访问外置存储需要动态申请权限
        if (Build.VERSION.SDK_INT >= 23) {
            int checkPermission = checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE);
            if (checkPermission != PackageManager.PERMISSION_GRANTED) {
                //  requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_CODE_ASK_PERMISSIONS);
                return false;
            }
        }
        return true;
    }
}
