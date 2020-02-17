package com.bcr.jianxinIM.activity.LoginRegister;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.preference.PreferenceManager;
import android.support.design.widget.TabLayout;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bcr.jianxinIM.app.MyApplication;
import com.bcr.jianxinIM.im.server.SealAction;
import com.bcr.jianxinIM.im.server.network.async.AsyncTaskManager;
import com.bcr.jianxinIM.im.server.network.async.OnDataListener;
import com.bcr.jianxinIM.im.server.response.SendCodeResponse;
import com.bcr.jianxinIM.push.ExampleUtil;
import com.orhanobut.logger.Logger;
import com.bcr.jianxinIM.activity.base_util.ToastUtils;
import com.bcr.jianxinIM.activity.main.MainActivity;
import com.bcr.jianxinIM.R;
import com.bcr.jianxinIM.im.SealConst;
import com.bcr.jianxinIM.im.SealUserInfoManager;
import com.bcr.jianxinIM.im.server.network.http.HttpException;
import com.bcr.jianxinIM.im.server.response.GetTokenResponse;
import com.bcr.jianxinIM.im.server.response.GetUserInfoByIdResponse;
import com.bcr.jianxinIM.im.server.response.LoginResponse;
import com.bcr.jianxinIM.im.server.response.PhoneLoginResponse;
import com.bcr.jianxinIM.im.server.utils.CommonUtils;
import com.bcr.jianxinIM.im.server.utils.NLog;
import com.bcr.jianxinIM.im.server.utils.NToast;
import com.bcr.jianxinIM.im.server.utils.RongGenerate;
import com.bcr.jianxinIM.im.server.widget.LoadDialog;
import com.bcr.jianxinIM.im.ui.activity.BaseActivity;
import com.bcr.jianxinIM.listener.IEditTextsChangeListener;
import com.bcr.jianxinIM.model.userLoginInfo;
import com.bcr.jianxinIM.util.AppManager;
import com.bcr.jianxinIM.util.SharedPreferencesUtil;
import com.bcr.jianxinIM.util.SpUtils;
import com.bcr.jianxinIM.util.StringUtil;
import com.bcr.jianxinIM.util.TextSizeCheckUtil;
import com.bcr.jianxinIM.view.BorderTextView;
import com.bcr.jianxinIM.view.ClearWriteEditText;
import com.bcr.jianxinIM.view.CommonDialog;
import com.bcr.jianxinIM.view.NoScrollViewPager;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import cn.jpush.android.api.JPushInterface;
import cn.jpush.android.api.TagAliasCallback;
import io.rong.imkit.RongIM;
import io.rong.imlib.RongIMClient;
import io.rong.imlib.model.UserInfo;

public class Login01Activity extends BaseActivity implements View.OnClickListener{
    private final static String TAG = "Login01Activity";
    private static final int LOGIN = 5;
    private static final int GET_TOKEN = 6;
    private static final int SYNC_USER_INFO = 9;
    private static final int PHONELOGIN =203 ;
    private static final int SENDCODE =236 ;
    private ArrayList<String> mTitles;
    private TabLayout mTabLayout;
    private NoScrollViewPager mViewPager;
    private LinearLayout updateLayout;
    private LinearLayout account_login;
    private AlphaAnimation alphaAnimation;
    private ClearWriteEditText mPhoneEdit,mPasswordEdit;
    private TextView tvForgetPwd;
    private BorderTextView tvLogin2,tvLogin1;
    private LinearLayout phoneLogin;
    private ClearWriteEditText edPhone,edCode;
    private TextView tvRegister2,tvRegister1,tvSendCode;
    private LinearLayout llbg01;

    private String phoneString,passwordString;
    private SharedPreferences sp;
    private SharedPreferences.Editor editor;
    private Intent intent;
    private String connectResultId;
    private String loginToken;
    private String userId;
    private TextSizeCheckUtil checkUtil;


    /**
     * 手机快捷登录
     * @param savedInstanceState
     */
   // TimeCount time = new TimeCount(60000, 1000);
    private int codeTime=0;
    private TimeCount time;
    private String phone;
    private String phone1,code1;

    private List<userLoginInfo> loginInfos;
    private ImageView ivBack;
    private String flag;
    private LoginResponse loginResponse;
    private ImageView ivQQ,ivWeChat;
    private String mobile;
    private String myEncode;
    private String PublicEncode;
    private CheckBox cbRemeberPwd;
    private int flags;
  //  private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login01);
      //notchScreenManager.setDisplayInNotch(this);
        flag=getIntent().getExtras().getString("flag"); //1从切换帐号过来。0为 其他地方过来
        setHeadVisibility(View.GONE);
//        Sofia.with(this)
//                .invasionStatusBar()
//                .statusBarBackground(Color.TRANSPARENT);
        SpUtils.getSp(this);
        AppManager.getAppManager().addActivity(this);
        sp = getSharedPreferences("config", MODE_PRIVATE);
        editor = sp.edit();
       // SharedPreferencesUtil.putString(mContext,"remember_password","0");

       // MyApplication.mContext.getAppKey((MyApplication) mContext);
        initView();
    }
    public void initData(){

    }
    private void initView() {
        mTitles = new ArrayList<>(Arrays.asList("帐号登录", "手机快捷登录"));
        mTabLayout=findViewById(R.id.xTablayout);
        ivBack=findViewById(R.id.ivBack);
        for (int i = 0; i < mTitles.size(); i++) {
            TabLayout.Tab tab = mTabLayout.newTab();
            tab.setText(mTitles.get(i));
            mTabLayout.addTab(tab);
        }
        mTabLayout.setTabMode(TabLayout.MODE_FIXED);
        updateLayout=findViewById(R.id.llBg);
        llbg01=findViewById(R.id.llbg01);
        WindowManager wm = (WindowManager) this
                .getSystemService(Context.WINDOW_SERVICE);
        int height = wm.getDefaultDisplay().getHeight();
        int width= wm.getDefaultDisplay().getWidth();

        LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) updateLayout.getLayoutParams();
        //layoutParams.height=height/5*3;
        layoutParams.width=width/5*4;
        updateLayout.setLayoutParams(layoutParams);
        updateLayout.setGravity(Gravity.CENTER);

        //帐号登录
        account_login=findViewById(R.id.account_login);
        ivQQ=findViewById(R.id.ivQQ);
        ivWeChat=findViewById(R.id.ivWeChat);
        mPhoneEdit=findViewById(R.id.edAccount);
        mPasswordEdit=findViewById(R.id.edPwd);
        tvForgetPwd=findViewById(R.id.tvForgetPwd);
        tvLogin2=findViewById(R.id.tvLogin2);
        tvRegister2=findViewById(R.id.tvRegister2);
        cbRemeberPwd = findViewById(R.id.cbRemeberPwd);
        tvRegister2.setOnClickListener(this);
        tvForgetPwd.setOnClickListener(this);
        ivQQ.setOnClickListener(this);
        ivWeChat.setOnClickListener(this);
        initWatcher();

        //手机登录
        phoneLogin=findViewById(R.id.phone_login);
        edPhone=findViewById(R.id.edPhone);
        edCode=findViewById(R.id.edCode);
        tvLogin1=findViewById(R.id.tvLogin1);
        tvSendCode=findViewById(R.id.tvCode);
        tvRegister1=findViewById(R.id.tvRegister1);
        tvSendCode.setOnClickListener(this);
        tvRegister1.setOnClickListener(this);
        initWatcher1();


//        if (!TextUtils.isEmpty(oldPhone) && !TextUtils.isEmpty(oldPassword)) {
//            mPhoneEdit.setText(oldPhone);
//            mPasswordEdit.setText(oldPassword);
//        }
        mTabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                llbg01.setVisibility(View.INVISIBLE);
                switch (tab.getPosition()){
                    case 0:
                        phoneLogin.setVisibility(View.GONE);
                        account_login.setVisibility(View.VISIBLE);
                        break;
                    case 1:
                        account_login.setVisibility(View.GONE);
                        phoneLogin.setVisibility(View.VISIBLE);
                        break;

                }
                llbg01.setAlpha(0.0f);
                alphaAnimation = new AlphaAnimation(0.0f, 1.0f);
                alphaAnimation.setDuration(500);    //深浅动画持续时间
                alphaAnimation.setFillAfter(true);   //动画结束时保持结束的画面

                llbg01.setVisibility(View.VISIBLE);
                llbg01.setAlpha(1.0f);
                llbg01.setAnimation(alphaAnimation);
                alphaAnimation.start();
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
        if (getIntent().getBooleanExtra("kickedByOtherClient", false)) {
            CommonDialog.Builder builder = new CommonDialog.Builder();
            builder.setContentMessage("您的帐号在别的设备上登录，您被迫下线!");
            builder.setIsOnlyConfirm(true);
            builder.isCancelable(false);
            CommonDialog dialog = builder.build();
            dialog.show(getSupportFragmentManager(), null);
        }

        if("1".equals(flag)){
            ivBack.setVisibility(View.VISIBLE);
                ivBack.setOnClickListener(v -> {
                onBackPressed();
            });
        }else {
            ivBack.setVisibility(View.GONE);
        }

        GetRememberPwd();
    }

    private void GetRememberPwd() {
        String rememberpwd = SharedPreferencesUtil.getString(mContext,"remember_password","");
        String oldPhone = SharedPreferencesUtil.getString(mContext,"phoneString","");
        String oldPassword = SharedPreferencesUtil.getString(mContext,"passwordString","");
         if(TextUtils.isEmpty(rememberpwd)){
             cbRemeberPwd.setChecked(false);
             mPhoneEdit.setText("");
             mPasswordEdit.setText("");
         }  else {
        if ("1".equals(rememberpwd)) {
           // Logger.d("rememberpwd",rememberpwd);
            cbRemeberPwd.setChecked(true);
            mPhoneEdit.setText(oldPhone);
            mPasswordEdit.setText(oldPassword);
        }else {
            cbRemeberPwd.setChecked(false);
            mPhoneEdit.setText(oldPhone);
            mPasswordEdit.setText("");
        }}
    }
    private void SetRememberPwd() {
        //editor = sharedPreferences.edit();
        if (cbRemeberPwd.isChecked()) {
            SharedPreferencesUtil.putString(mContext,"passwordString",passwordString);
            SharedPreferencesUtil.putString(mContext,"remember_password","1");
        }else {
        SharedPreferencesUtil.putString(mContext,"remember_password","0");}
    }
    private void initWatcher() {
        checkUtil = TextSizeCheckUtil.getInstance().setBtn(tvLogin2).
                addAllEditText(mPhoneEdit,mPasswordEdit )
                .setChangeListener(isAllHasContent -> {
                    //按钮颜色变化时的操作(按钮颜色变化已经在工具中设置)
                    if (isAllHasContent) {
                        tvLogin2.setContentColorResource01(getResources().getColor(R.color.colorPrimary));
                        tvLogin2.setStrokeColor01(getResources().getColor(R.color.colorPrimary));
                        tvLogin2.setOnClickListener(this);
                    } else {
                        tvLogin2.setContentColorResource01(Color.parseColor("#999999"));
                        tvLogin2.setStrokeColor01(Color.parseColor("#999999"));
                    }
                });
    }
    private void initWatcher1() {
        checkUtil = TextSizeCheckUtil.getInstance().setBtn(tvLogin1).
                addAllEditText(edPhone, edCode)
                .setChangeListener(new IEditTextsChangeListener() {
                    @Override
                    public void textChange(boolean isAllHasContent) {
                        //按钮颜色变化时的操作(按钮颜色变化已经在工具中设置)
                        if (isAllHasContent) {
                            tvLogin1.setContentColorResource01(getResources().getColor(R.color.colorPrimary));
                            tvLogin1.setStrokeColor01(getResources().getColor(R.color.colorPrimary));
                            tvLogin1.setOnClickListener(Login01Activity.this);
                        } else {
                            tvLogin1.setContentColorResource01(Color.parseColor("#999999"));
                            tvLogin1.setStrokeColor01(Color.parseColor("#999999"));
                        }
                    }
                });
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tvLogin2:
                phoneString = mPhoneEdit.getText().toString().trim();
                passwordString = mPasswordEdit.getText().toString().trim();
                //   String countryCodeStr = countryCodeTv.getText().toString().trim();
                if (TextUtils.isEmpty(phoneString)) {
                    NToast.shortToast(mContext, R.string.phone_number_is_null);
                    mPhoneEdit.setShakeAnimation();
                    return;
                }
                if (TextUtils.isEmpty(passwordString)) {
                    NToast.shortToast(mContext, R.string.password_is_null);
                    mPasswordEdit.setShakeAnimation();
                    return;
                }
                if (passwordString.contains(" ")) {
                    NToast.shortToast(mContext, R.string.password_cannot_contain_spaces);
                    mPasswordEdit.setShakeAnimation();
                    return;
                }
                LoadDialog.show(this,"loading...",false);
                editor = mContext.getSharedPreferences("config", Context.MODE_PRIVATE).edit();
                String oldPhone = sp.getString(SealConst.SEALTALK_LOGING_PHONE, "");
                editor.putString("loginToken", "");
                editor.putBoolean("exit", true);
                editor.putString(SealConst.SEALTALK_LOGIN_ID, "");
                editor.putInt("getAllUserInfoState", 0);
                editor.commit();
                SealUserInfoManager.getInstance().deleteAllUserInfo();
                SealUserInfoManager.getInstance().closeDB();
                //RongIM.getInstance().disconnect();
                RongIM.getInstance().logout();
                request(LOGIN, true);
                Logger.d("执行了1");
                break;
            case R.id.tvRegister2:
                intent = new Intent(this, RegisterActivity.class);
                startActivity(intent);
//                intent = new Intent(this, PerfectActivity.class);
//                startActivity(intent);
                break;
            case R.id.tvForgetPwd:
                intent = new Intent(this, ForgetPwdActivity.class);
                startActivity(intent);
                //startActivityForResult(new Intent(this, ForgetPwdActivity.class), 2);
                break;
            case R.id.tvCode:
                initCode();
                break;
            case R.id.tvRegister1:
                intent=new Intent(this, RegisterActivity.class);
                startActivity(intent);
                break;
            case R.id.tvLogin1:
                phoneString=edPhone.getText().toString();
                passwordString = "";

                code1=edCode.getText().toString();
                RongIM.getInstance().logout();
                LoadDialog.show(this,"loading...",false);
                editor = mContext.getSharedPreferences("config", Context.MODE_PRIVATE).edit();
                editor.putString("loginToken", "");
                editor.putBoolean("exit", true);
                editor.putString(SealConst.SEALTALK_LOGIN_ID, "");
                editor.putInt("getAllUserInfoState", 0);
                editor.commit();
                SealUserInfoManager.getInstance().deleteAllUserInfo();
                SealUserInfoManager.getInstance().closeDB();
                //RongIM.getInstance().disconnect();
                RongIM.getInstance().logout();

                request(PHONELOGIN, true);
//                intent = new Intent(this, MainActivity.class);
//                startActivity(intent);
                break;
            case R.id.ivQQ:
                break;
            case R.id.ivWeChat:
                //toActivity(BindPhoneActivity.class);
                break;

        }}

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 2 && data != null) {
            String phone = data.getStringExtra("phone");
            String password = data.getStringExtra("password");
            mPhoneEdit.setText(phone);
            mPasswordEdit.setText(password);
        }
//        else if (data != null && requestCode == 1) {
//            String phone = data.getStringExtra("phone");
//            String password = data.getStringExtra("password");
//            String id = data.getStringExtra("id");
//            if (!TextUtils.isEmpty(phone) && !TextUtils.isEmpty(password) && !TextUtils.isEmpty(id) && !TextUtils.isEmpty(nickname)) {
//                mPhoneEdit.setText(phone);
//                mPasswordEdit.setText(password);
//                editor.putString(SealConst.SEALTALK_LOGING_PHONE, phone);
//                editor.putString(SealConst.SEALTALK_LOGING_PASSWORD, password);
//                editor.putString(SealConst.SEALTALK_LOGIN_ID, id);
//                editor.commit();
//            }
//
//        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void initCode(){
        mobile = edPhone.getText().toString().trim();
        if (StringUtil.isBlank(mobile)) {
            ToastUtils.showToast(Login01Activity.this, "手机号不能为空");
            return;
        }

        AsyncTaskManager.getInstance(mContext).request(SENDCODE, new OnDataListener() {
            @Override
            public Object doInBackground(int requestCode, String parameter) throws HttpException {
                return new SealAction(mContext).sendCode(mobile,"Login");
            }

            @Override
            public void onSuccess(int requestCode, Object result) {
                if (result != null) {
                    SendCodeResponse sendCodeResponse= (SendCodeResponse) result;
                    if(sendCodeResponse.isSuccess()==true){
                        codeTime=sendCodeResponse.getResultData();
                        Logger.d("codeTime:-->"+codeTime);
                        if(codeTime==0){
                            time = new TimeCount(180000, 1000);
                        }else {
                            time=new TimeCount(codeTime*1000, 1000);
                        }
                        time.start();
                    }
                }
            }

            @Override
            public void onFailure(int requestCode, int state, Object result) {

            }
        });
    }
    @Override
    public Object doInBackground(int requestCode, String id) throws HttpException {
        switch (requestCode) {
            case LOGIN:
                return action.login("86", phoneString, passwordString);
            case GET_TOKEN:
                return action.getToken(userId);
            case SYNC_USER_INFO:
                return action.getUserInfoById(connectResultId);
               // return action.getUserInfoById(userId);
            case PHONELOGIN:
                return action.PhoneLogin(phoneString,code1);
        }
        return null;
    }

    @Override
    public void onSuccess(int requestCode, Object result) {
        Logger.d("loginResponse",result);
        if (result != null) {
            switch (requestCode) {
                case LOGIN:
                    flags=0;
                    SharedPreferencesUtil.putInt(mContext,"PhoneFlags",flags);
                    Logger.d("loginResponse",result);
                    loginResponse = (LoginResponse) result;
                    if (loginResponse.isSuccess() == true) {
                        loginToken = loginResponse.getResultData().getRtoken();
                        userId = loginResponse.getResultData().getUserId();
                        myEncode=loginResponse.getResultData().getEncode();
                        PublicEncode=loginResponse.getResultData().getPublicEncode();
                        SharedPreferencesUtil.putString(Login01Activity.this,"userId",userId);
                        SharedPreferencesUtil.putString(mContext,"myEncode",myEncode);
                        SharedPreferencesUtil.putString(mContext,"PublicEncode","fcI50A7s");
                        SharedPreferencesUtil.getString(mContext,"userName",loginResponse.getResultData().getUserNickName());
                        JPushInterface.setTags(mContext, getInPutTags(), new TagAliasCallback() {
                            @Override
                            public void gotResult(int i, String s, Set<String> set) {
                                Iterator<String> iterator = set.iterator();
// 从while循环中读取key
                                while(iterator.hasNext()){
                                    String key = iterator.next();
                                    Logger.d("Tagsssss888"+key);
                                }

                            }
                        });

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
                    SealUserInfoManager.getInstance().getAllUserInfo(SharedPreferencesUtil.getString(mContext,"userId",""));
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
                                    SharedPreferencesUtil.putString(Login01Activity.this,"userId",s);
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
                case PHONELOGIN:
                    Logger.d("loginResponse",result);
                    flags=1;
                    SharedPreferencesUtil.putInt(mContext,"PhoneFlags",flags);
                    loginResponse = (LoginResponse) result;
                    if (loginResponse.isSuccess() == true) {
                        loginToken = loginResponse.getResultData().getRtoken();
                        userId = loginResponse.getResultData().getUserId();
                        myEncode=loginResponse.getResultData().getEncode();
                        PublicEncode=loginResponse.getResultData().getPublicEncode();
                        SharedPreferencesUtil.putString(Login01Activity.this,"userId",userId);
                        SharedPreferencesUtil.putString(mContext,"myEncode",myEncode);
                        SharedPreferencesUtil.putString(mContext,"PublicEncode","fcI50A7s");
                        SharedPreferencesUtil.getString(mContext,"userName",loginResponse.getResultData().getUserNickName());
                        JPushInterface.setTags(mContext, getInPutTags(), new TagAliasCallback() {
                            @Override
                            public void gotResult(int i, String s, Set<String> set) {
                                Iterator<String> iterator = set.iterator();
// 从while循环中读取key
                                while(iterator.hasNext()){
                                    String key = iterator.next();
                                    Logger.d("Tagsssss888"+key);
                                }

                            }
                        });

                        if (!TextUtils.isEmpty(loginToken)) {
                            //TODO CONNECT
                            rongConnect(loginToken);
                        }
                    } else if (loginResponse.isSuccess() == false) {
                        LoadDialog.dismiss(mContext);
                        NToast.shortToast(mContext, loginResponse.getMessage());
                    }
                    break;
            }
        }
    }
    /**
     * 获取输入的tags
     * */
    private Set<String> getInPutTags(){
        String tag= SharedPreferencesUtil.getString(mContext,"userId","");
        // 检查 tag 的有效性
        if (TextUtils.isEmpty(tag)) {
            Toast.makeText(getApplicationContext(), R.string.error_tag_empty, Toast.LENGTH_SHORT).show();
            return null;
        }
        // ","隔开的多个 转换成 Set
        String[] sArray = tag.split(",");
        Set<String> tagSet = new LinkedHashSet<String>();
        for (String sTagItme : sArray) {
            if (!ExampleUtil.isValidTagAndAlias(sTagItme)) {
                Toast.makeText(getApplicationContext(), R.string.error_tag_gs_empty, Toast.LENGTH_SHORT).show();
                return null;
            }
            tagSet.add(sTagItme);
        }
        if(tagSet.isEmpty()){
            Toast.makeText(getApplicationContext(), R.string.error_tag_empty, Toast.LENGTH_SHORT).show();
            return null;
        }
        return tagSet;
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
            case PHONELOGIN:
                LoginResponse loginResponse1= (LoginResponse) result;
                LoadDialog.dismiss(mContext);
                NToast.shortToast(mContext, loginResponse1.getMessage());
                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    private void goToMain() {
        editor.putString("loginToken", loginToken);
        editor.putString(SealConst.SEALTALK_USERID, SharedPreferencesUtil.getString(mContext,"userId",""));
        editor.putString(SealConst.SEALTALK_LOGING_PHONE, phoneString);
//        editor.putString(SealConst.SEALTALK_LOGING_PASSWORD, passwordString);
        editor.commit();
        SharedPreferencesUtil.putString(mContext,"phoneString",phoneString);
        LoadDialog.dismiss(mContext);
        NToast.shortToast(mContext, R.string.login_success);


        //if(flags==0){
        loginInfos=SpUtils.getDataList("listBean",userLoginInfo.class);
        if(loginInfos.size()==0){
            addAccount(loginInfos,SharedPreferencesUtil.getString(mContext,"userId",""));
            SpUtils.setDataList("listBean", loginInfos);
        }else if(loginInfos.size()==1){
            for(userLoginInfo user:loginInfos){
                if(loginResponse.getResultData().getUserId().equals(loginInfos.get(loginInfos.size()-1).getUserId())){
                    deleAccount(loginInfos,loginInfos.get(loginInfos.size()-1).getUserId());
                    addAccount(loginInfos,SharedPreferencesUtil.getString(mContext,"userId",""));
                }else {
                    addAccount(loginInfos,SharedPreferencesUtil.getString(mContext,"userId",""));
                }
            }

        }else if(loginInfos.size()==2){
            deleAccount(loginInfos,loginInfos.get(loginInfos.size()-2).getUserId());
            addAccount(loginInfos,SharedPreferencesUtil.getString(mContext,"userId",""));
        }
    //}
        SetRememberPwd();
        //if(flags==0){ }
        Intent intent=new Intent(mContext,MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);

       // finish();
    }

    private void addAccount(List<userLoginInfo> loginInfos, String userId){
        userLoginInfo userLoginInfo = new userLoginInfo();
        userLoginInfo.setUserId(userId);
        userLoginInfo.setPhone(phoneString);
        userLoginInfo.setPwd(passwordString);
        userLoginInfo.setImageUrl(loginResponse.getResultData().getUserPic());
        loginInfos.add(userLoginInfo);
        SpUtils.setDataList("listBean", loginInfos);
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

    private void sendCode() {
        phone = edPhone.getText().toString().trim();
        if (StringUtil.isBlank(phone)) {
            ToastUtils.showToast(Login01Activity.this, "手机号不能为空");
            return;
        }
        if (!(StringUtil.isCellPhone(phone))) {
            ToastUtils.showToast(Login01Activity.this, "请输入正确的手机号码");
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
            tvSendCode.setText("获取验证码");
            tvSendCode.setClickable(true);
        }

        @Override
        public void onTick(long millisUntilFinished) {// 计时过程
            tvSendCode.setClickable(false);//防止重复点击
            tvSendCode.setText(millisUntilFinished / 1000 + "s");
        }
    }

}
