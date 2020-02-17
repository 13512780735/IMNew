package com.bcr.jianxinIM.app;

import android.app.ActivityManager;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;

import com.bcr.jianxinIM.im.RongCloudEvent;
import com.bcr.jianxinIM.im.message.ContactNotificationMessage;
import com.bcr.jianxinIM.im.message.CustomizeMessage;
import com.bcr.jianxinIM.im.message.provider.CustomizeMessageItemProvider;
import com.bcr.jianxinIM.service.LocalService;
import com.bcr.jianxinIM.service.RomoteService;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.orhanobut.logger.AndroidLogAdapter;
import com.orhanobut.logger.Logger;
import com.bcr.jianxinIM.R;
import com.bcr.jianxinIM.activity.LoginRegister.Login01Activity;
import com.bcr.jianxinIM.im.SealAppContext;
import com.bcr.jianxinIM.im.SealConst;
import com.bcr.jianxinIM.im.SealUserInfoManager;
import com.bcr.jianxinIM.im.message.provider.ContactNotificationMessageProvider;
import com.bcr.jianxinIM.im.server.SealAction;
import com.bcr.jianxinIM.im.server.network.async.AsyncTaskManager;
import com.bcr.jianxinIM.im.server.network.async.OnDataListener;
import com.bcr.jianxinIM.im.server.network.http.HttpException;
import com.bcr.jianxinIM.im.server.response.AppKeyRespone;
import com.bcr.jianxinIM.im.server.utils.NLog;
import com.bcr.jianxinIM.im.utils.SharedPreferencesContext;
import com.bcr.jianxinIM.util.SharedPreferencesUtil;
import com.tencent.bugly.Bugly;
import com.tencent.bugly.crashreport.CrashReport;
import com.tencent.smtt.sdk.QbSdk;


import cn.jpush.android.api.JPushInterface;
import io.rong.imkit.RongExtensionManager;
import io.rong.imkit.RongIM;
import io.rong.imkit.widget.provider.RealTimeLocationMessageProvider;
import io.rong.imkit.widget.provider.SightMessageItemProvider;
import io.rong.imlib.RongIMClient;
import io.rong.imlib.ipc.RongExceptionHandler;
import io.rong.message.SightMessage;
import io.rong.push.RongPushClient;
import io.rong.sight.SightExtensionModule;

public class MyApplication extends Application {
    private static final int GETKEY = 225;
    private static DisplayImageOptions options;
    public static MyApplication mContext;
    private String key;
    public   static String Appkey="3argexb63sade";
    private String key1;

    public static MyApplication getInstance() {
        return mContext;
    }
    //全局Context
    private static Context sContext;
    @Override
    public void onCreate() {
        super.onCreate();
        mContext = this;
        sContext = getApplicationContext();
        //实例化全局的请求队列
        Logger.addLogAdapter(new AndroidLogAdapter());
        key1=SharedPreferencesUtil.getString(this,"AppKey","");
        initImageLoad();
        getAppKey(mContext);
       initQbSdk();//初始化腾讯X5
       // CrashReport.initCrashReport(getApplicationContext());

        //bugly初始化
        Bugly.init(getApplicationContext(),"460b9cedc9",false);
        CrashReport.initCrashReport(getApplicationContext(), "460b9cedc9", false);
        JPushInterface.setDebugMode(true); 	// 设置开启日志,发布时请关闭日志
        JPushInterface.init(this);     		// 初始化 JPush
        if (getApplicationInfo().packageName.equals(getCurProcessName(getApplicationContext()))) {

//            LeakCanary.install(this);//内存泄露检测
           RongPushClient.registerHWPush(this);
             RongPushClient.registerMiPush(this, "2882303761517473625", "5451747338625");
            RongPushClient.registerMZPush(this, "112988", "2fa951a802ac4bd5843d694517307896");
            try {
                RongPushClient.registerFCM(this);
            } catch (Exception e) {
                e.printStackTrace();
            }

            /**
             * 注意：
             *
             * IMKit SDK调用第一步 初始化
             *
             * context上下文
             *
             * 只有两个进程需要初始化，主进程和 push 进程
             */
            //initRongIM();


//            RongExtensionManager.getInstance().registerExtensionModule(new PTTExtensionModule(this, true, 1000 * 60));
//            RongExtensionManager.getInstance().registerExtensionModule(new ContactCardExtensionModule(new IContactCardInfoProvider() {
//                @Override
//                public void getContactAllInfoProvider(final IContactCardInfoCallback contactInfoCallback) {
//                    SealUserInfoManager.getInstance().getFriends(new SealUserInfoManager.ResultCallback<List<Friend>>() {
//                        @Override
//                        public void onSuccess(List<Friend> friendList) {
//                            contactInfoCallback.getContactCardInfoCallback(friendList);
//                        }
//
//                        @Override
//                        public void onError(String errString) {
//                            contactInfoCallback.getContactCardInfoCallback(null);
//                        }
//                    });
//                }
//
//                @Override
//                public void getContactAppointedInfoProvider(String userId, String name, String portrait, final IContactCardInfoCallback contactInfoCallback) {
//                    SealUserInfoManager.getInstance().getFriendByID(userId, new SealUserInfoManager.ResultCallback<Friend>() {
//                        @Override
//                        public void onSuccess(Friend friend) {
//                            List<UserInfo> list = new ArrayList<>();
//                            list.add(friend);
//                            contactInfoCallback.getContactCardInfoCallback(list);
//                        }
//
//                        @Override
//                        public void onError(String errString) {
//                            contactInfoCallback.getContactCardInfoCallback(null);
//                        }
//                    });
//                }
//
//            }, new IContactCardClickListener() {
//                @Override
//                public void onContactCardClick(View view, ContactMessage content) {
//                    Intent intent = new Intent(view.getContext(), UserDetailActivity.class);
//                    Friend friend = SealUserInfoManager.getInstance().getFriendByID(content.getId());
//                    if (friend == null) {
//                        UserInfo userInfo = new UserInfo(content.getId(), content.getName(),
//                                Uri.parse(TextUtils.isEmpty(content.getImgUrl()) ? RongGenerate.generateDefaultAvatar(content.getName(), content.getId()) : content.getImgUrl()));
//                        friend = CharacterParser.getInstance().generateFriendFromUserInfo(userInfo);
//                    }
//                    intent.putExtra("friend", friend);
//                    view.getContext().startActivity(intent);
//                }
//            }));
//            RongExtensionManager.getInstance().registerExtensionModule(new RecognizeExtensionModule());
        }
    }

    private void initQbSdk() {
        QbSdk.setDownloadWithoutWifi(true);
        //搜集本地tbs内核信息并上报服务器，服务器返回结果决定使用哪个内核。
        QbSdk.PreInitCallback cb = new QbSdk.PreInitCallback() {
            @Override
            public void onViewInitFinished(boolean arg0) {
                //x5內核初始化完成的回调，为true表示x5内核加载成功，否则表示x5内核加载失败，会自动切换到系统内核。
            }

            @Override
            public void onCoreInitFinished() {

            }
        };
        //x5内核初始化接口
        QbSdk.initX5Environment(getApplicationContext(), cb);
    }

    private void initRongIM() {
        RongIM.setServerInfo("nav.cn.ronghub.com", "up.qbox.me");
        Logger.d("Appkey999:-->"+SharedPreferencesUtil.getString(mContext,"AppKey",""));
         RongIM.init(this,SharedPreferencesUtil.getString(mContext,"AppKey",""));
        //RongIM.init(this,Appkey);
        // RongIM.init(this);
        NLog.setDebug(true);//Seal Module Log 开关
        SealAppContext.init(this);
        SharedPreferencesContext.init(this);
        //RongIM.setConnectionStatusListener(new MyConnectionStatusListener());
        Thread.setDefaultUncaughtExceptionHandler(new RongExceptionHandler(this));

        try {
            RongIM.registerMessageTemplate(new ContactNotificationMessageProvider());
            RongIM.registerMessageTemplate(new RealTimeLocationMessageProvider());
            RongExtensionManager.getInstance().registerExtensionModule(new SightExtensionModule());
            RongIM.registerMessageType(SightMessage.class);
            RongIM.registerMessageTemplate(new SightMessageItemProvider());
            RongIM.registerMessageType(CustomizeMessage.class);
            RongIM.registerMessageType(ContactNotificationMessage.class);
            RongIM.registerMessageTemplate(new CustomizeMessageItemProvider());
        } catch (Exception e) {
            e.printStackTrace();
        }

        openSealDBIfHasCachedToken();
        RongIM.setConnectionStatusListener(new MyConnectionStatusListener());

        options = new DisplayImageOptions.Builder()
                .showImageForEmptyUri(R.drawable.de_default_portrait)
                .showImageOnFail(R.drawable.de_default_portrait)
                .showImageOnLoading(R.drawable.de_default_portrait)
                .cacheInMemory(true)
                .cacheOnDisk(true)
                .build();
      //  RongCloudEvent.init(mContext);
//            startService(new Intent(this, LocalService.class));
//            startService(new Intent(this, RomoteService.class));
    }

    public String getAppKey(MyApplication mContext) {
        AsyncTaskManager.getInstance(mContext).request(GETKEY, new OnDataListener() {
            @Override
            public Object doInBackground(int requestCode, String parameter) throws HttpException {
                return new SealAction(mContext).GetAppKey();
            }

            @Override
            public void onSuccess(int requestCode, Object result) {

                if (result != null) {
                    AppKeyRespone appKeyRespone= (AppKeyRespone) result;
                    if(appKeyRespone.isSuccess()==true){
                         key=appKeyRespone.getResultData();
                         if(!key.equals(key1)){
                             SharedPreferences.Editor editor = mContext.getSharedPreferences("config", Context.MODE_PRIVATE).edit();
                             editor.putString("loginToken", "");
                             editor.commit();
                         }
                         SharedPreferencesUtil.putString(mContext,"AppKey",key);
                        //  SharedPreferencesUtil.putString(mContext,"AppKey","");
                        Logger.d("AppKey:-->"+key);
                        initRongIM();
                    }else{
                        SharedPreferencesUtil.putString(mContext,"AppKey",null);
                    }
                }
            }

            @Override
            public void onFailure(int requestCode, int state, Object result) {
                SharedPreferencesUtil.putString(mContext,"AppKey",null);
            }
        });
        return null;
    }

    private class MyConnectionStatusListener implements RongIMClient.ConnectionStatusListener {

        @Override
        public void onChanged(ConnectionStatus connectionStatus) {
            Logger.d("connectionStatus:->"+connectionStatus);
            switch (connectionStatus){

                case CONNECTED://连接成功。

                    break;
                case DISCONNECTED://断开连接。

                    break;
                case CONNECTING://连接中。

                    break;
                case NETWORK_UNAVAILABLE://网络不可用。

                    break;
                case KICKED_OFFLINE_BY_OTHER_CLIENT://用户帐户在其他设备登录，本机会被踢掉线
                    quit(true);
                    break;
                case TOKEN_INCORRECT:
                    SharedPreferences sp = getSharedPreferences("config", MODE_PRIVATE);
                        final String cacheToken = sp.getString("loginToken", "");
                        if (!TextUtils.isEmpty(cacheToken)) {
                            RongIM.connect(cacheToken, SealAppContext.getInstance().getConnectCallback());
                        } else {
                            Log.e("seal", "token is empty, can not reconnect");
                        }
                    break;
            }
        }
    }
    public static DisplayImageOptions getOptions() {
        return options;
    }

    private void openSealDBIfHasCachedToken() {
        SharedPreferences sp = getSharedPreferences("config", MODE_PRIVATE);
        String cachedToken = sp.getString("loginToken", "");
        if (!TextUtils.isEmpty(cachedToken)) {
            String current = getCurProcessName(this);
            String mainProcessName = getPackageName();
            if (mainProcessName.equals(current)) {
                SealUserInfoManager.getInstance().openDB();
            }
        }
    }
    private void quit(boolean isKicked) {
        //Log.d(TAG, "quit isKicked " + isKicked);
        SharedPreferences.Editor editor = mContext.getSharedPreferences("config", Context.MODE_PRIVATE).edit();
        if (!isKicked) {
            editor.putBoolean("exit", true);
        }
        editor.putString("loginToken", "");
        editor.putString(SealConst.SEALTALK_LOGIN_ID, "");
        editor.putInt("getAllUserInfoState", 0);
        editor.commit();
        /*//这些数据清除操作之前一直是在login界面,因为app的数据库改为按照userID存储,退出登录时先直接删除
        //这种方式是很不友好的方式,未来需要修改同app server的数据同步方式
        //SealUserInfoManager.getInstance().deleteAllUserInfo();*/
        SealUserInfoManager.getInstance().closeDB();
        RongIM.getInstance().logout();
        Intent loginActivityIntent = new Intent();
        Bundle bundle=new Bundle();
        bundle.putString("flag","0");
        loginActivityIntent.putExtras(bundle);
        loginActivityIntent.setClass(mContext, Login01Activity.class);
        loginActivityIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        if (isKicked) {
            loginActivityIntent.putExtra("kickedByOtherClient", true);
        }
        mContext.startActivity(loginActivityIntent);
    }

    public static String getCurProcessName(Context context) {
        int pid = android.os.Process.myPid();
        ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningAppProcessInfo appProcess : activityManager.getRunningAppProcesses()) {
            if (appProcess.pid == pid) {
                return appProcess.processName;
            }
        }
        return null;
    }



    public static Context getContext() {
        return sContext;
    }
    private void initImageLoad() {
        DisplayImageOptions defaultOptions = new DisplayImageOptions.Builder()
                .showImageForEmptyUri(R.mipmap.default_pic)
                .showImageOnFail(R.mipmap.default_pic)
                .cacheInMemory(true).cacheOnDisc(false).build();
        // 图片加载工具配置
        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(
                getApplicationContext())
                .defaultDisplayImageOptions(defaultOptions)
                .discCacheSize(500 * 1024 * 1024)//
                .discCacheFileCount(300)// 缓存一百张图片
                .writeDebugLogs().build();
        ImageLoader.getInstance().init(config);
    }
}
