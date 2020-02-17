package com.bcr.jianxinIM.activity.main;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bcr.jianxinIM.im.server.SealAction;
import com.bcr.jianxinIM.im.server.network.async.AsyncTaskManager;
import com.bcr.jianxinIM.im.server.network.async.OnDataListener;
import com.orhanobut.logger.Logger;
import com.bcr.jianxinIM.R;
import com.bcr.jianxinIM.activity.LoginRegister.Login01Activity;
import com.bcr.jianxinIM.im.SealConst;
import com.bcr.jianxinIM.im.SealUserInfoManager;
import com.bcr.jianxinIM.im.server.network.http.HttpException;
import com.bcr.jianxinIM.im.server.response.GetTokenResponse;
import com.bcr.jianxinIM.im.server.response.GetUserInfoByIdResponse;
import com.bcr.jianxinIM.im.server.response.LoginResponse;
import com.bcr.jianxinIM.im.server.utils.CommonUtils;
import com.bcr.jianxinIM.im.server.utils.NLog;
import com.bcr.jianxinIM.im.server.utils.NToast;
import com.bcr.jianxinIM.im.server.utils.RongGenerate;
import com.bcr.jianxinIM.im.server.widget.LoadDialog;
import com.bcr.jianxinIM.im.ui.activity.BaseActivity;
import com.bcr.jianxinIM.util.SharedPreferencesUtil;
import com.bcr.jianxinIM.util.SpUtils;
import com.bcr.jianxinIM.view.RoundImageView;
import com.bcr.jianxinIM.model.userLoginInfo;

import java.util.Iterator;
import java.util.List;

import io.rong.imageloader.core.ImageLoader;
import io.rong.imkit.RongIM;
import io.rong.imlib.RongIMClient;
import io.rong.imlib.model.UserInfo;

public class SwitchAcountActivity extends BaseActivity {
    private static final int REQUSERINFO = 4234;
    private final static String TAG = "SwitchAcountActivity";
    RoundImageView ivAvatar01,ivAvatar02;
    TextView tvId01,tvId02,tvDel;
    RelativeLayout rl_del;
    LinearLayout ll_add01,ll_add;
    private static final int LOGIN = 5;
    private static final int GET_TOKEN = 6;
    private static final int SYNC_USER_INFO = 9;
    private List<userLoginInfo> list;
    private LoginResponse loginResponse;
    private String loginToken;
    private String userId;
    private String connectResultId;
    private List<userLoginInfo> loginInfos;
    private SharedPreferences sp;
    private SharedPreferences.Editor editor;
    private String myEncode;
    private String PublicEncode;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_switch_acount);
        setTitle("切换帐号");
        SpUtils.getSp(this);
        sp = getSharedPreferences("config", MODE_PRIVATE);
        editor = sp.edit();

        initView();
        initData();
    }
    public void initView(){
        ivAvatar01=findViewById(R.id.ivAvatar01);
        tvId01=findViewById(R.id.tvId01);
        tvId02=findViewById(R.id.tvId02);
        tvDel=findViewById(R.id.tvDel);
        ll_add01=findViewById(R.id.ll_add01);
        ll_add=findViewById(R.id.ll_add);
        ivAvatar02=findViewById(R.id.ivAvatar02);
        rl_del=findViewById(R.id.rl_del);
        ivAvatar01.setOnClickListener(v -> onBackPressed());
        ll_add.setOnClickListener(v -> {
            Intent loginActivityIntent = new Intent();
            Bundle bundle=new Bundle();
            bundle.putString("flag","1");
            loginActivityIntent.putExtras(bundle);
            loginActivityIntent.setClass(mContext, Login01Activity.class);
            startActivity(loginActivityIntent);
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        });
        tvDel.setOnClickListener(v -> rl_del.setVisibility(View.VISIBLE));

    }
    public void initData(){

        int flags=SharedPreferencesUtil.getInt(mContext,"PhoneFlags",0);
        list= SpUtils.getDataList("listBean",userLoginInfo.class);
        Logger.d("loginInfos9898",list.size());
        for(userLoginInfo user:list ){
            Logger.d("loginInfos000:-->"+user.getUserId());
        }
        if(list.size()==1){

            initUserInfo(list.get(list.size()-1).getUserId());
//            if(TextUtils.isEmpty(list.get(list.size()-1).getImageUrl())){
//                ivAvatar01.setImageResource(R.mipmap.icon_wangwang);
//            }else {
//            ImageLoader.getInstance().displayImage(list.get(list.size()-1).getImageUrl(),ivAvatar01);}
            tvId01.setText(list.get(list.size()-1).getUserId());
        }else if(list.size()==2){
            initUserInfo(list.get(list.size()-1).getUserId());

             tvId01.setText(list.get(list.size()-1).getUserId());
            if(TextUtils.isEmpty(list.get(list.size()-2).getImageUrl())){
                ivAvatar02.setImageResource(R.mipmap.icon_wangwang);
            }else {
                ImageLoader.getInstance().displayImage(list.get(list.size()-2).getImageUrl(),ivAvatar02);
            }
            tvId02.setText(list.get(list.size()-2).getUserId());
            ll_add.setVisibility(View.GONE);
            ll_add01.setVisibility(View.VISIBLE);
            tvDel.setVisibility(View.VISIBLE);
        }
        rl_del.setOnClickListener(v -> {
         deleAccount(list,list.get(list.size()-2).getUserId());
        ll_add01.setVisibility(View.GONE);
        ll_add.setVisibility(View.VISIBLE);
        tvDel.setVisibility(View.GONE);
        });
        ll_add01.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Logger.d("賬號："+list.get(list.size()-2).getPhone());
                Logger.d("賬號："+list.get(list.size()-2).getPwd());
                if(TextUtils.isEmpty(list.get(list.size()-2).getPwd())){
                    Intent loginActivityIntent = new Intent();
                    Bundle bundle=new Bundle();
                    bundle.putString("flag","1");
                    loginActivityIntent.putExtras(bundle);
                    loginActivityIntent.setClass(mContext, Login01Activity.class);
                    startActivity(loginActivityIntent);
                    overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                }
                //RongIM.getInstance().disconnect();
                SharedPreferences.Editor editor = mContext.getSharedPreferences("config", Context.MODE_PRIVATE).edit();
                editor.putString("loginToken", "");
                editor.putBoolean("exit", true);
                editor.putString(SealConst.SEALTALK_LOGIN_ID, "");
                editor.putInt("getAllUserInfoState", 0);
                editor.commit();
                SealUserInfoManager.getInstance().closeDB();
                RongIM.getInstance().logout();
                LoadDialog.show(mContext,"loading...",false);
                request(LOGIN, true);
            }
        });
    }

    private void initUserInfo(String userId) {
        AsyncTaskManager.getInstance(mContext).request(REQUSERINFO, new OnDataListener() {
            @Override
            public Object doInBackground(int requestCode, String parameter) throws HttpException {
                return new SealAction(mContext).getUserInfoById(SharedPreferencesUtil.getString(mContext,"userId",""));
            }

            @Override
            public void onSuccess(int requestCode, Object result) {
                if (result != null) {
                    GetUserInfoByIdResponse getUserInfoByIdResponse = (GetUserInfoByIdResponse) result;
                    if(getUserInfoByIdResponse.isSuccess()==true){
                        if(TextUtils.isEmpty(getUserInfoByIdResponse.getResultData().getUserPic())){
                            ivAvatar01.setImageResource(R.mipmap.icon_wangwang);
                        }else {
                            ImageLoader.getInstance().displayImage(getUserInfoByIdResponse.getResultData().getUserPic(),ivAvatar01);
                        }

                    }
                }
            }

            @Override
            public void onFailure(int requestCode, int state, Object result) {

            }
        });

    }

    /**
     * 插入数据前要遍历下有没有有的话删除，保存最新的
     */
    private void deleAccount(List<userLoginInfo> list, String id) {
        if (list != null && list.size() > 0) {
            Iterator it = list.iterator();
            while (it.hasNext()) {
                userLoginInfo userbean = (userLoginInfo) it.next();
                if (id.equals(userbean.getUserId())) {
                    it.remove();
                }
            }
            SpUtils.setDataList("listBean", list);

        }
    }
    @Override
    public Object doInBackground(int requestCode, String id) throws HttpException {
        switch (requestCode) {
            case LOGIN:
                Logger.d("賬號："+list.get(list.size()-2).getPhone());
                Logger.d("賬號："+list.get(list.size()-2).getPwd());
                return action.login("86",list.get(list.size()-2).getPhone() , list.get(list.size()-2).getPwd());
            case GET_TOKEN:
                return action.getToken(list.get(list.size()-2).getUserId());
            case SYNC_USER_INFO:
                return action.getUserInfoById(list.get(list.size()-2).getUserId());
            // return action.getUserInfoById(userId);
        }
        return null;
    }
    @Override
    public void onSuccess(int requestCode, Object result) {
        Logger.d("loginResponse",result);
        if (result != null) {
            switch (requestCode) {
                case LOGIN:
                    Logger.d("loginResponse",result);
                    loginResponse = (LoginResponse) result;
                    if (loginResponse.isSuccess() == true) {
                        loginToken = loginResponse.getResultData().getRtoken();
                        userId = loginResponse.getResultData().getUserId();
                        myEncode=loginResponse.getResultData().getEncode();
                        PublicEncode=loginResponse.getResultData().getPublicEncode();
                        SharedPreferencesUtil.putString(mContext,"userId",userId);
                        SharedPreferencesUtil.putString(mContext,"myEncode",myEncode);
                        SharedPreferencesUtil.putString(mContext,"PublicEncode","fcI50A7s");
                        if (!TextUtils.isEmpty(loginToken)) {
                            //TODO CONNECT
                            rongConnect(loginToken);
                        }
                    } else if (loginResponse.isSuccess() == false) {
                        LoadDialog.dismiss(mContext);
                        NToast.shortToast(mContext, loginResponse.getMessage());
                    }
                    break;
                case SYNC_USER_INFO:
                    GetUserInfoByIdResponse userInfoByIdResponse = (GetUserInfoByIdResponse) result;
                    if (userInfoByIdResponse.isSuccess() == true) {
                        if (TextUtils.isEmpty(userInfoByIdResponse.getResultData().getUserPic())) {
                            userInfoByIdResponse.getResultData().setUserPic(RongGenerate.generateDefaultAvatar(userInfoByIdResponse.getResultData().getUserNickName(), userInfoByIdResponse.getResultData().getUserId()));
                        }
                        String nickName = userInfoByIdResponse.getResultData().getUserNickName();
                        String portraitUri = userInfoByIdResponse.getResultData().getUserPic();
                        editor.putString(SealConst.SEALTALK_LOGIN_NAME, nickName);
                        editor.putString(SealConst.SEALTALK_LOGING_PORTRAIT, portraitUri);
                        editor.commit();
                        RongIM.getInstance().refreshUserInfoCache(new UserInfo(connectResultId, nickName, Uri.parse(portraitUri)));
                    }
                    //不继续在login界面同步好友,群组,群组成员信息
                    SealUserInfoManager.getInstance().getAllUserInfo(userId);
                    goToMain();
                    break;
                case GET_TOKEN:
                    GetTokenResponse tokenResponse = (GetTokenResponse) result;
                    if (tokenResponse.isSuccess() == true) {
                        String token = tokenResponse.getResultData().getRtoken();
                        if (!TextUtils.isEmpty(token)) {
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
                                    SharedPreferencesUtil.putString(mContext,"userId",s);
                                    editor.putString(SealConst.SEALTALK_USERID, s);
                                    editor.commit();
                                    SealUserInfoManager.getInstance().openDB();
                                    request(SYNC_USER_INFO, true);
                                }

                                @Override
                                public void onError(RongIMClient.ErrorCode errorCode) {

                                }
                            });

                        }
                    }
                    break;
            }
        }
    }

    private void reGetToken() {
        request(GET_TOKEN);
    }

    @Override
    public void onFailure(int requestCode, int state, Object result) {
        if (!CommonUtils.isNetworkConnected(mContext)) {
            LoadDialog.dismiss(mContext);
            NToast.shortToast(mContext, getString(R.string.network_not_available));
            return;
        }
        switch (requestCode) {
            case LOGIN:
                LoginResponse loginResponse= (LoginResponse) result;
                LoadDialog.dismiss(mContext);
                NToast.shortToast(mContext,loginResponse.getMessage());
                break;
            case SYNC_USER_INFO:
                GetUserInfoByIdResponse getUserInfoByIdResponse= (GetUserInfoByIdResponse) result;
                LoadDialog.dismiss(mContext);
                NToast.shortToast(mContext, getUserInfoByIdResponse.getMessage());
                break;
            case GET_TOKEN:
                GetTokenResponse getTokenResponse= (GetTokenResponse) result;
                LoadDialog.dismiss(mContext);
                NToast.shortToast(mContext, getTokenResponse.getMessage());
                break;
        }
    }
    private void rongConnect(String token){
        RongIM.connect(token, new RongIMClient.ConnectCallback() {
            @Override
            public void onTokenIncorrect() {
                NLog.e("connect", "onTokenIncorrect");
                reGetToken();
            }

            @Override
            public void onSuccess(String s) {
                connectResultId = s;
                NLog.e("connect", "onSuccess userid:" + s);
                editor.putString(SealConst.SEALTALK_LOGIN_ID, s);
                editor.commit();
                SealUserInfoManager.getInstance().openDB();
                request(SYNC_USER_INFO, true);
            }

            @Override
            public void onError(RongIMClient.ErrorCode errorCode) {
                NLog.e("connect", "onError errorcode:" + errorCode.getValue());
//                if("31006".equals(String.valueOf(errorCode.getValue()))){
//                    goToMain();
//                }
            }
        });
    }

    private void goToMain() {
        editor.putString("loginToken", loginToken);
        editor.putString(SealConst.SEALTALK_USERID, userId);
        editor.putString(SealConst.SEALTALK_LOGING_PHONE, list.get(list.size()-2).getPhone());
        editor.putString(SealConst.SEALTALK_LOGING_PASSWORD,  list.get(list.size()-2).getPwd());
        editor.commit();
        LoadDialog.dismiss(mContext);
        NToast.shortToast(mContext, R.string.login_success);
        loginInfos=SpUtils.getDataList("listBean",userLoginInfo.class);
        if(loginInfos.size()==0){
            addAccount(loginInfos,userId);
            SpUtils.setDataList("listBean", loginInfos);
        }else if(loginInfos.size()==1){
            for(userLoginInfo user:loginInfos){
                if(loginResponse.getResultData().getUserId().equals(loginInfos.get(loginInfos.size()-1).getUserId())){
                    deleAccount(loginInfos,loginInfos.get(loginInfos.size()-1).getUserId());
                    addAccount(loginInfos,userId);
                }else {
                    addAccount(loginInfos,userId);
                }
            }

        }else if(loginInfos.size()==2){
            deleAccount(loginInfos,loginInfos.get(loginInfos.size()-2).getUserId());
            addAccount(loginInfos,userId);
        }

        Intent intent=new Intent(mContext,MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
       // AppManager.getAppManager().finishAllActivity();
    }
    private void addAccount(List<userLoginInfo> loginInfos, String userId){
        userLoginInfo userLoginInfo = new userLoginInfo();
        userLoginInfo.setUserId(userId);
        userLoginInfo.setPhone(list.get(list.size()-2).getPhone());
        userLoginInfo.setPwd(list.get(list.size()-2).getPwd());
        userLoginInfo.setImageUrl(loginResponse.getResultData().getUserPic());
        loginInfos.add(userLoginInfo);
        SpUtils.setDataList("listBean", loginInfos);
    }
}
