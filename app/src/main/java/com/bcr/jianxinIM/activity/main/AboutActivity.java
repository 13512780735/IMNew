package com.bcr.jianxinIM.activity.main;


import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bcr.jianxinIM.R;
import com.bcr.jianxinIM.im.ui.activity.BaseActivity;
import com.bcr.jianxinIM.im.utils.Base64Utils;
import com.bcr.jianxinIM.util.AppManager;
import com.orhanobut.logger.Logger;


public class AboutActivity extends BaseActivity {

    private ImageView ivBack;
    private TextView tvTitle;
    private TextView tvPublisher,tvVersion;
    private String version;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);
        //setHeadVisibility(View.GONE);
        setTitle("关于我们");
        AppManager.getAppManager().addActivity(this);
        getVersionName();
        initView();
//        try {
//            Logger.d("消息sw:-->"+ AndroidDes3Util.decode("Zm91bmRlcjEyMw=="));
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        HashMap<String, Object> map = null;
//        try {
//            map = RSAUtils.getKeys();
//        } catch (NoSuchAlgorithmException e) {
//            e.printStackTrace();
//        }
//        // 生成公钥和私钥
//        RSAPublicKey publicKey = (RSAPublicKey) map.get("public");
//        RSAPrivateKey privateKey = (RSAPrivateKey) map.get("private");
//
//        // 模
//        String modulus = publicKey.getModulus().toString();
//        System.out.println("pubkey modulus=" + modulus);
//        // 公钥指数
//        String public_exponent = publicKey.getPublicExponent().toString();
//        System.out.println("pubkey exponent=" + public_exponent);
//        // 私钥指数
//        String private_exponent = privateKey.getPrivateExponent().toString();
//        System.out.println("private exponent=" + private_exponent);
//        // 明文
//        String ming = "founder123";
//        // 使用模和指数生成公钥和私钥
//        RSAPublicKey pubKey = RSAUtils.getPublicKey(modulus, public_exponent);
//        RSAPrivateKey priKey = RSAUtils.getPrivateKey(modulus,private_exponent);
//
//        // 加密后的密文
//        String mi = null;
//        try {
//            mi = RSAUtils.encryptByPublicKey(ming, pubKey);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        System.err.println("mi=" + mi);
//        Logger.d("加密1："+ mi);
//        // 解密后的明文
//        String ming2 = null;
//        try {
//            ming2 = RSAUtils.decryptByPrivateKey(mi, priKey);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        System.err.println("ming2=" + ming2);
//        Logger.d("加密2："+ ming2);
        String ming = "founder123";
        Logger.e("消息：--》"+Base64Utils.getBase64(ming));
        Logger.e("消息11：--》"+  Base64Utils.getFromBase64(Base64Utils.getBase64(ming)));
    }

    private void initView() {
//        ivBack=findViewById(R.id.titlebar_iv_left);
//        ivBack.setVisibility(View.VISIBLE);
//        ivBack.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                onBackPressed();
//            }
//        });
//        tvTitle=findViewById(R.id.titlebar_tv);
//        tvTitle.setText("关于我们");
        tvPublisher=findViewById(R.id.tv_publisher);
        tvVersion=findViewById(R.id.tv_version);
        tvVersion.setText(version+"");
        tvPublisher.setText("湖南大栩企业管理有限公司");
    }
    private String getVersionName() {
        // 获取packagemanager的实例
        PackageManager packageManager = getPackageManager();
        // getPackageName()是你当前类的包名，0代表是获取版本信息
        PackageInfo packInfo = null;
        try {
            packInfo = packageManager.getPackageInfo(getPackageName(), 0);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        version = packInfo.versionName;
        return version;
    }
}
