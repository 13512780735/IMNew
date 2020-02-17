package com.bcr.jianxinIM.zxinglibrary.android;

import android.app.AlertDialog;
import android.content.Intent;
import android.content.pm.FeatureInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatDelegate;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.LinearLayoutCompat;
import android.text.TextUtils;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import com.google.zxing.Result;
import com.bcr.jianxinIM.R;
import com.bcr.jianxinIM.im.db.Friend;
import com.bcr.jianxinIM.im.server.SealAction;
import com.bcr.jianxinIM.im.server.network.async.AsyncTaskManager;
import com.bcr.jianxinIM.im.server.network.async.OnDataListener;
import com.bcr.jianxinIM.im.server.network.http.HttpException;
import com.bcr.jianxinIM.im.server.pinyin.CharacterParser;
import com.bcr.jianxinIM.im.server.response.GetGroupInfoResponse;
import com.bcr.jianxinIM.im.server.response.GetGroupMemberResponse;
import com.bcr.jianxinIM.im.server.response.GetUserInfoByIdResponse;
import com.bcr.jianxinIM.im.server.response.QRCodeResponse;
import com.bcr.jianxinIM.im.server.utils.NToast;
import com.bcr.jianxinIM.im.server.utils.json.JsonMananger;
import com.bcr.jianxinIM.im.server.widget.LoadDialog;
import com.bcr.jianxinIM.im.ui.activity.BaseActivity;
import com.bcr.jianxinIM.im.ui.activity.JoinGroupActivity;
import com.bcr.jianxinIM.im.ui.activity.UserDetailActivity;
import com.bcr.jianxinIM.zxinglibrary.bean.ZxingConfig;
import com.bcr.jianxinIM.zxinglibrary.camera.CameraManager;
import com.bcr.jianxinIM.zxinglibrary.common.Constant;
import com.bcr.jianxinIM.zxinglibrary.decode.DecodeImgCallback;
import com.bcr.jianxinIM.zxinglibrary.decode.DecodeImgThread;
import com.bcr.jianxinIM.zxinglibrary.decode.ImageUtil;
import com.bcr.jianxinIM.zxinglibrary.view.ViewfinderView;

import java.io.IOException;
import java.util.List;

import io.rong.imkit.RongIM;


/**
 * @author: yzq
 * @date: 2017/10/26 15:22
 * @declare :扫一扫
 */

public class CaptureActivity extends BaseActivity implements SurfaceHolder.Callback, View.OnClickListener {

    private static final String TAG = CaptureActivity.class.getSimpleName();
    private static final int SYN_USER_INFO = 10087;
    private static final int GETGROUPINFO =214 ;
    private static final int GETGROUPMEMBER =220 ;
    public ZxingConfig config;
    private SurfaceView previewView;
    private ViewfinderView viewfinderView;
    private AppCompatImageView flashLightIv;
    private TextView flashLightTv;
    private AppCompatImageView backIv;
    private LinearLayoutCompat flashLightLayout;
    private LinearLayoutCompat albumLayout;
    private LinearLayoutCompat bottomLayout;
    private boolean hasSurface;
    private InactivityTimer inactivityTimer;
    private BeepManager beepManager;
    private CameraManager cameraManager;
    private CaptureActivityHandler handler;
    private SurfaceHolder surfaceHolder;
    private String type;
    private String id;
    private static final int CLICK_CONTACT_FRAGMENT_FRIEND = 2;
    private GetGroupInfoResponse getGroupInfoResponse;

    public ViewfinderView getViewfinderView() {
        return viewfinderView;
    }

    public Handler getHandler() {
        return handler;
    }

    public CameraManager getCameraManager() {
        return cameraManager;
    }

    public void drawViewfinder() {
        viewfinderView.drawViewfinder();
    }


    static {
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // 保持Activity处于唤醒状态
        Window window = getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//            window.setStatusBarColor(Color.BLACK);
//        }

        /*先获取配置信息*/
        try {
            config = (ZxingConfig) getIntent().getExtras().get(Constant.INTENT_ZXING_CONFIG);
        } catch (Exception e) {

            Log.i("config", e.toString());
        }

        if (config == null) {
            config = new ZxingConfig();
        }


        setContentView(R.layout.activity_capture);
        setHeadVisibility(View.GONE);

        initView();

        hasSurface = false;

        inactivityTimer = new InactivityTimer(this);
        beepManager = new BeepManager(this);
        beepManager.setPlayBeep(config.isPlayBeep());
        beepManager.setVibrate(config.isShake());


    }


    private void initView() {
        previewView = findViewById(R.id.preview_view);
        previewView.setOnClickListener(this);

        viewfinderView = findViewById(R.id.viewfinder_view);
        viewfinderView.setZxingConfig(config);


        backIv = findViewById(R.id.backIv);
        backIv.setOnClickListener(this);

        flashLightIv = findViewById(R.id.flashLightIv);
        flashLightTv = findViewById(R.id.flashLightTv);

        flashLightLayout = findViewById(R.id.flashLightLayout);
        flashLightLayout.setOnClickListener(this);
        albumLayout = findViewById(R.id.albumLayout);
        albumLayout.setOnClickListener(this);
        bottomLayout = findViewById(R.id.bottomLayout);


        switchVisibility(bottomLayout, config.isShowbottomLayout());
        switchVisibility(flashLightLayout, config.isShowFlashLight());
        //switchVisibility(albumLayout, config.isShowAlbum());


        /*有闪光灯就显示手电筒按钮  否则不显示*/
        if (isSupportCameraLedFlash(getPackageManager())) {
            flashLightLayout.setVisibility(View.VISIBLE);
        } else {
            flashLightLayout.setVisibility(View.GONE);
        }

    }


    /**
     * @param pm
     * @return 是否有闪光灯
     */
    public static boolean isSupportCameraLedFlash(PackageManager pm) {
        if (pm != null) {
            FeatureInfo[] features = pm.getSystemAvailableFeatures();
            if (features != null) {
                for (FeatureInfo f : features) {
                    if (f != null && PackageManager.FEATURE_CAMERA_FLASH.equals(f.name)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    /**
     * @param flashState 切换闪光灯图片
     */
    public void switchFlashImg(int flashState) {

        if (flashState == Constant.FLASH_OPEN) {
            flashLightIv.setImageResource(R.drawable.ic_open);
            flashLightTv.setText(R.string.close_flash);
        } else {
            flashLightIv.setImageResource(R.drawable.ic_close);
            flashLightTv.setText(R.string.open_flash);
        }

    }

    /**
     * @param rawResult 返回的扫描结果
     */
    public void handleDecode(Result rawResult) {

        inactivityTimer.onActivity();

        beepManager.playBeepSoundAndVibrate();
        QRCodeResponse qrCodeResponse=null;
        if (!TextUtils.isEmpty(rawResult.toString())) {
            Log.e("VerifyCodeResponse", rawResult.toString());
            try {
                qrCodeResponse= JsonMananger.jsonToBean(rawResult.toString(), QRCodeResponse.class);
                System.out.println("打印结果：--》"+qrCodeResponse.getType());
                System.out.println("打印结果：--》"+qrCodeResponse.getId());
                type=qrCodeResponse.getType();
                id=qrCodeResponse.getId();
                if("group".equals(type)){
                    checkGroupIsExist(id);

                }else  if("user".equals(type)){
                    //showUserDetail(id);
                    getUserDetail(id);

                }else {
                    NToast.shortToast(CaptureActivity.this,"该二维码无法识别");
                    this.finish();
                }
            } catch (HttpException e) {
                e.printStackTrace();
            }
        }

        System.out.println("扫描结果：--》"+rawResult.getText());


//        Intent intent = getIntent();
//        intent.putExtra(Constant.CODED_CONTENT, rawResult.getText());
       // setResult(RESULT_OK, intent);



    }

    private void getUserDetail(String id) {
        request(SYN_USER_INFO);
    }

    @Override
    public Object doInBackground(int requestCode, String ids) throws HttpException {
        switch (requestCode){
            case SYN_USER_INFO:
                return action.getUserInfoById(id);
        }
        return super.doInBackground(requestCode, id);
    }

    @Override
    public void onSuccess(int requestCode, Object result) {
        if (result != null) {
            switch (requestCode){
                case SYN_USER_INFO:
                    GetUserInfoByIdResponse userInfoByIdResponse = (GetUserInfoByIdResponse) result;
                    if(userInfoByIdResponse.isSuccess()==true){
                      Friend friend=  new Friend(userInfoByIdResponse.getResultData().getUserId(),
                                userInfoByIdResponse.getResultData().getUserNickName(),
                                Uri.parse(userInfoByIdResponse.getResultData().getUserPic()),
                              userInfoByIdResponse.getResultData().getUserNickName(),
                                null, null, null, null,
                                CharacterParser.getInstance().getSpelling( userInfoByIdResponse.getResultData().getUserNickName()),
                                TextUtils.isEmpty(userInfoByIdResponse.getResultData().getUserNickName()) ?
                                        null : CharacterParser.getInstance().getSpelling(userInfoByIdResponse.getResultData().getUserNickName()));
                        showUserDetail(friend);
                    }else {
                        NToast.shortToast(mContext,userInfoByIdResponse.getMessage());
                    }
                    this.finish();
                    break;
            }
        }
    }

    /**
     *检查群组是否存在
     * @param groupId
     */
    private void checkGroupIsExist(String groupId) {
        System.out.println("检查群组是否存在");
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
                        checkIsInGroup(groupId, getGroupInfoResponse.getResultData().getName());
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

    /**
     * ji检查是否在群组中
     * @param groupId
     * @param name
     */
    private void checkIsInGroup(String groupId, String name) {
        System.out.println("检查是否在群组中");
        AsyncTaskManager.getInstance(mContext).request(GETGROUPMEMBER, new OnDataListener() {
            @Override
            public Object doInBackground(int requestCode, String parameter) throws HttpException {
                return new SealAction(mContext).getGroupMember(groupId);
            }

            @Override
            public void onSuccess(int requestCode, Object result) {
                if (result != null) {
                    GetGroupMemberResponse getGroupMemberResponse= (GetGroupMemberResponse) result;
                    if(getGroupMemberResponse.isSuccess()==true){
                        String currentUserId = RongIM.getInstance().getCurrentUserId();
                        LoadDialog.dismiss(mContext);
                        List<GetGroupMemberResponse.ResultDataBean> groupMembers=getGroupMemberResponse.getResultData();
                        if (groupMembers != null && groupMembers.size() > 0) {
                        for(GetGroupMemberResponse.ResultDataBean groupMember:groupMembers){
                            String userId = groupMember.getUserId();
                            if (currentUserId.equals(userId)) {
                                // 群组中包含自己则跳转到群聊天界面
                                toGroupChat(groupId, name);
                                finish();
                                return;
                            }
                         }
                            } showJoinGroup(groupId,groupMembers.size());
                        return;
                       // NToast.shortToast(mContext,getGroupMemberResponse.getMessage())
                    }else {
                        LoadDialog.dismiss(mContext);
                       // NToast.shortToast(mContext,getGroupMemberResponse.getMessage());
                    }
                }
            }

            @Override
            public void onFailure(int requestCode, int state, Object result) {
                LoadDialog.dismiss(mContext);
            }
        });


//        SealUserInfoManager.getInstance().getGroupMembers(groupId, new SealUserInfoManager.ResultCallback<List<GroupMember>>() {
//            @Override
//            public void onSuccess(List<GroupMember> groupMembers) {
//                String currentUserId = RongIM.getInstance().getCurrentUserId();
//                Logger.d("groupMembers:-->"+groupMembers.size());
//                LoadDialog.dismiss(mContext);
//                if (groupMembers != null && groupMembers.size() > 0) {
//                    for (GroupMember groupMember : groupMembers) {
//                        String userId = groupMember.getUserId();
//                        if (currentUserId.equals(userId)) {
//                            // 群组中包含自己则跳转到群聊天界面
//                            toGroupChat(groupId, name);
//                            finish();
//                            return;
//                        }
//
//
//                    }
//                    showJoinGroup(groupId,groupMembers.size());
//                    return;
//                }
//            }
//
//            @Override
//            public void onError(String errString) {
//                LoadDialog.dismiss(mContext);
//            }
//        });
    }

    /**
     * 加入群聊
     * @param groupId
     * @param size
     */
    private void showJoinGroup(String groupId, int size) {
        Intent intent=new Intent(mContext, JoinGroupActivity.class);
        intent.putExtra("groupName",getGroupInfoResponse.getResultData().getName());
        intent.putExtra("groupMember",String.valueOf(size));
        intent.putExtra("groupPic",getGroupInfoResponse.getResultData().getPortraitUri());
        intent.putExtra("groupId",groupId);
        startActivity(intent);
        this.finish();


    }

    /**
     * 显示群聊
     * @param groupId
     * @param name
     */
    private void toGroupChat(String groupId, String name) {
        RongIM.getInstance().startGroupChat(CaptureActivity.this, groupId, name);
    }
    /**
     * 显示用户详情
     *
     * @param friend
     */
    private void showUserDetail(Friend friend) {
        Intent intent = new Intent(mContext, UserDetailActivity.class);
        intent.putExtra("type", CLICK_CONTACT_FRAGMENT_FRIEND);
        intent.putExtra("friend", friend);
        startActivity(intent);
    }

    private void switchVisibility(View view, boolean b) {
        if (b) {
            view.setVisibility(View.VISIBLE);
        } else {
            view.setVisibility(View.GONE);
        }
    }


    @Override
    protected void onResume() {
        super.onResume();

        cameraManager = new CameraManager(getApplication(), config);

        viewfinderView.setCameraManager(cameraManager);
        handler = null;

        surfaceHolder = previewView.getHolder();
        if (hasSurface) {

            initCamera(surfaceHolder);
        } else {
            // 重置callback，等待surfaceCreated()来初始化camera
            surfaceHolder.addCallback(this);
        }

        beepManager.updatePrefs();
        inactivityTimer.onResume();

    }

    private void initCamera(SurfaceHolder surfaceHolder) {
        if (surfaceHolder == null) {
            throw new IllegalStateException("No SurfaceHolder provided");
        }
        if (cameraManager.isOpen()) {
            return;
        }
        try {
            // 打开Camera硬件设备
            cameraManager.openDriver(surfaceHolder);
            // 创建一个handler来打开预览，并抛出一个运行时异常
            if (handler == null) {
                handler = new CaptureActivityHandler(this, cameraManager);
            }
        } catch (IOException ioe) {
            Log.w(TAG, ioe);
            displayFrameworkBugMessageAndExit();
        } catch (RuntimeException e) {
            Log.w(TAG, "Unexpected error initializing camera", e);
            displayFrameworkBugMessageAndExit();
        }
    }

    private void displayFrameworkBugMessageAndExit() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("扫一扫");
        builder.setMessage(getString(R.string.msg_camera_framework_bug));
        builder.setPositiveButton(R.string.button_ok, new FinishListener(this));
        builder.setOnCancelListener(new FinishListener(this));
        builder.show();
    }

    @Override
    protected void onPause() {

        Log.i("CaptureActivity","onPause");
        if (handler != null) {
            handler.quitSynchronously();
            handler = null;
        }
        inactivityTimer.onPause();
        beepManager.close();
        cameraManager.closeDriver();

        if (!hasSurface) {

            surfaceHolder.removeCallback(this);
        }
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        inactivityTimer.shutdown();
        viewfinderView.stopAnimator();
        super.onDestroy();
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        if (!hasSurface) {
            hasSurface = true;
            initCamera(holder);
        }
    }


    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        hasSurface = false;
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width,
                               int height) {

    }

    @Override
    public void onClick(View view) {

        int id = view.getId();
        if (id == R.id.flashLightLayout) {
            /*切换闪光灯*/
            cameraManager.switchFlashLight(handler);
        } else if (id == R.id.albumLayout) {
            /*打开相册*/
            Intent intent = new Intent();
            intent.setAction(Intent.ACTION_PICK);
            intent.setType("image/*");
            startActivityForResult(intent, Constant.REQUEST_IMAGE);
        } else if (id == R.id.backIv) {
            finish();
        }


    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == Constant.REQUEST_IMAGE && resultCode == RESULT_OK) {
            String path = ImageUtil.getImageAbsolutePath(this, data.getData());


            new DecodeImgThread(path, new DecodeImgCallback() {
                @Override
                public void onImageDecodeSuccess(Result result) {
                    handleDecode(result);
                }

                @Override
                public void onImageDecodeFailed() {
                    Toast.makeText(CaptureActivity.this, R.string.scan_failed_tip, Toast.LENGTH_SHORT).show();
                }
            }).run();


        }
    }


}
