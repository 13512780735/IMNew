package com.bcr.jianxinIM.activity.LoginRegister;


import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;

import com.orhanobut.logger.Logger;
import com.bcr.jianxinIM.R;
import com.bcr.jianxinIM.activity.base.SplashActivity;
import com.bcr.jianxinIM.activity.main.MainActivity;
import com.bcr.jianxinIM.im.SealConst;
import com.bcr.jianxinIM.im.SealUserInfoManager;
import com.bcr.jianxinIM.im.server.utils.NLog;
import com.bcr.jianxinIM.util.SharedPreferencesUtil;
import com.bcr.jianxinIM.view.CommonDialog;

import io.rong.imkit.RongIM;
import io.rong.imlib.RongIMClient;


public class WelcomeActivity extends SplashActivity {

    private SharedPreferences sp;
    private SharedPreferences.Editor editor;
    private String token;
    String TAG="WelcomeActivity";
    private String connectResultId;

    @Override
    public int getContentViewId() {
        return R.layout.activity_welcome;
    }

    @Override
    public Animation.AnimationListener getAnimationListener() {
        return new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                sp = getSharedPreferences("config", MODE_PRIVATE);
                editor = sp.edit();
                token=sp.getString("loginToken","");
                String key=SharedPreferencesUtil.getString(WelcomeActivity.this,"AppKey","");
               if(key!=null&&!TextUtils.isEmpty(key)){
                if(!TextUtils.isEmpty(token)){
                    connect();
                }else{
                    Bundle bundle1=new Bundle();
                    bundle1.putString("flag","0");
                    toActivity(Login01Activity.class,bundle1);
                    finish();
                }}
               else {

                   CommonDialog dialog = new CommonDialog.Builder()
                           .setContentMessage("网络异常,确认是否退出程序重新登录！")
                           .setIsOnlyConfirm(true)
                           .setDialogButtonClickListener(new CommonDialog.OnDialogButtonClickListener() {
                               @Override
                               public void onPositiveClick(View v, Bundle bundle) {
                                   finish();
                       android.os.Process.killProcess(android.os.Process.myPid());
                       System.exit(0);


                               }



                               @Override
                               public void onNegativeClick(View v, Bundle bundle) {
                               }
                           })
                           .build();
                   dialog.show(getSupportFragmentManager(), null);



               }
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        };
    }

    private void connect() {
        RongIM.connect(token, new RongIMClient.ConnectCallback() {
            @Override
            public void onTokenIncorrect() {
                Log.e(TAG, "reToken Incorrect");
            }

            @Override
            public void onSuccess(String s) {
                Logger.d("connect:-->"+s);
                //editor.putString("userId", s);
                connectResultId = s;
                NLog.e("connect", "onSuccess userid:" + s);
                editor.putString(SealConst.SEALTALK_LOGIN_ID, s);
                SharedPreferencesUtil.putString(WelcomeActivity.this,"userId",s);
                editor.putString(SealConst.SEALTALK_USERID, s);
                editor.commit();
                SealUserInfoManager.getInstance().openDB();
                //request(SYNC_USER_INFO, true);
                SealUserInfoManager.getInstance().getAllUserInfo(connectResultId);
                goToMain();
            }
            @Override
            public void onError(RongIMClient.ErrorCode errorCode) {
                Bundle bundle1=new Bundle();
                bundle1.putString("flag","0");
                toActivity(Login01Activity.class,bundle1);
                finish();
            }
        });
    }
    private void goToMain() {
        editor.putString("loginToken", token);
        editor.commit();
        startActivity(new Intent(WelcomeActivity.this, MainActivity.class));
        finish();
    }
}
