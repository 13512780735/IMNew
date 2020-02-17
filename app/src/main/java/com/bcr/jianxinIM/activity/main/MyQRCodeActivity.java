package com.bcr.jianxinIM.activity.main;


import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.orhanobut.logger.Logger;
import com.bcr.jianxinIM.R;
import com.bcr.jianxinIM.im.server.network.http.HttpException;
import com.bcr.jianxinIM.im.server.response.getCodeResponse;
import com.bcr.jianxinIM.im.server.utils.NToast;
import com.bcr.jianxinIM.im.server.widget.LoadDialog;
import com.bcr.jianxinIM.im.ui.activity.BaseActivity;
import com.bcr.jianxinIM.util.SharedPreferencesUtil;
import com.bcr.jianxinIM.view.CircleImageView;

import io.rong.imageloader.core.ImageLoader;


public class MyQRCodeActivity extends BaseActivity implements View.OnClickListener {
    private static final int GETCODE =213 ;
    private ImageView ivBack;
    private TextView tvTitle;
    private ImageView qrCodeIv;
    private TextView qrCodeDescribeTv;
    private LinearLayout qrCodeCardLl;
    private String content;
    private Bitmap qrcode_bitmap;
    private String url;
    String pic,nickName;
    private TextView tvName;
    private CircleImageView ivAvatar;
    private int code=0;
    private RelativeLayout rl_nodata;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_qrcode);
        Intent intent=getIntent();
        pic=intent.getExtras().getString("userPic");
        nickName=intent.getExtras().getString("userName");
        code=intent.getExtras().getInt("code");
        initView();
        initData();
        initViewModel();
    }

    private void initData() {
        LoadDialog.show(mContext,"loading...",false);
        request(GETCODE,true);
    }

    @Override
    public Object doInBackground(int requestCode, String id) throws HttpException {
        switch (requestCode) {
            case GETCODE:
                return action.getIamgeCode("user", SharedPreferencesUtil.getString(mContext,"userId",""));
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
                        if(code==0){
                            rl_nodata.setVisibility(View.VISIBLE);
                            qrCodeIv.setVisibility(View.GONE);
                        }else {
                            qrCodeIv.setVisibility(View.VISIBLE);
                            rl_nodata.setVisibility(View.GONE);
                        url=codeResponse.getResultData();
                        Logger.d("url;-->"+codeResponse.getResultData());
                        ImageLoader.getInstance().displayImage(url, qrCodeIv);}
                    }else {
                        LoadDialog.dismiss(mContext);
                        NToast.shortToast(mContext,codeResponse.getMessage());
                    }
                    break;
            }
        }
    }

    private void initView() {
        setTitle("二维码");
        qrCodeDescribeTv = findViewById(R.id.profile_tv_qr_card_info_describe);
        qrCodeDescribeTv.setText("扫一扫二维码，加我为好友");
        qrCodeIv = findViewById(R.id.profile_iv_qr_code);
        tvName = findViewById(R.id.tvName);
        rl_nodata = findViewById(R.id.rl_nodata);
        ivAvatar = findViewById(R.id.ivAvatar);
        qrCodeCardLl = findViewById(R.id.profile_fl_card_capture_area_container);
        findViewById(R.id.tvSave).setOnClickListener(this);
        tvName.setText(nickName);
        ImageLoader.getInstance().displayImage(pic, ivAvatar);
    }
    private void initViewModel() {
//        qrCodeViewModel = ViewModelProviders.of(this).get(DisplayQRCodeViewModel.class);
//        content = "123木头人";
//        int width = 650;
//        int height = 650;
//        qrcode_bitmap = QRCodeUtil.createQRCodeBitmap(content, width, height, "UTF-8",
//                null, null,  Color.BLACK, Color.WHITE, BitmapFactory.decodeResource(this.getContext().getResources(), R.mipmap.icon_logo01), 0.2F,null);
//        qrCodeIv.setImageBitmap(qrcode_bitmap);
//        // 获取 QRCode 结果
//        qrCodeViewModel.getQRCode().observe(this, resource -> {
//            if (resource.data != null) {
//                qrCodeIv.setImageBitmap(resource.data);
//            }
//        });
//
//        ViewGroup.LayoutParams qrCodeLayoutParams = qrCodeIv.getLayoutParams();
////
////         if (qrType == QrCodeDisplayType.PRIVATE) {
////            // 获取用户信息结果
////            qrCodeViewModel.getUserInfo().observe(this, resource -> {
////                if (resource.data != null) {
////                    updateUserInfo(resource.data);
////                }
////            });
////
////            // 请求用户信息
////            qrCodeViewModel.requestUserInfo(targetId);
////            // 获取用户二维码
////            qrCodeViewModel.requestUserQRCode(targetId, qrCodeLayoutParams.width, qrCodeLayoutParams.height);
////        }
//        qrCodeViewModel.requestUserInfo("");
//        // 获取用户二维码
//        qrCodeViewModel.requestUserQRCode("123木头人", qrCodeLayoutParams.width, qrCodeLayoutParams.height);
//        // 保存图片到本地
//        qrCodeViewModel.getSaveLocalBitmapResult().observe(this, resource -> {
//            if (resource.status == Status.SUCCESS) {
//                // 保存成功后加入媒体扫描中，使相册中可以显示此图片
//                MediaScannerConnection.scanFile(MyQRCodeActivity.this.getApplicationContext(), new String[]{resource.data}, null, null);
//
//                String msg = "图片保存成功" + ":" + resource.data;
//               // ToastUtils.showToast(msg, Toast.LENGTH_LONG);
//                com.king.base.util.ToastUtils.showToast(this,msg);
//            }
//        });

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
