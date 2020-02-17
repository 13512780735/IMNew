package com.bcr.jianxinIM.activity.LoginRegister;


import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;

import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.RequiresApi;

import android.text.TextUtils;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;


import com.bcr.jianxinIM.im.ui.activity.CreateGroupActivity;
import com.bcr.jianxinIM.util.Dialogchoosephoto;
import com.bumptech.glide.Glide;
import com.example.zhouwei.library.CustomPopWindow;
import com.guoqi.actionsheet.ActionSheet;
import com.bcr.jianxinIM.R;
import com.bcr.jianxinIM.im.server.network.http.HttpException;
import com.bcr.jianxinIM.im.server.response.PerfectUserInfoResponse;
import com.bcr.jianxinIM.im.server.response.SetPortraitResponse;
import com.bcr.jianxinIM.im.server.utils.NToast;
import com.bcr.jianxinIM.im.server.widget.LoadDialog;
import com.bcr.jianxinIM.im.ui.activity.BaseActivity;
import com.bcr.jianxinIM.util.AppManager;
import com.bcr.jianxinIM.util.photo.PhotoUtils;
import com.bcr.jianxinIM.view.BorderTextView;
import com.bcr.jianxinIM.view.CircleImageView;
import com.bcr.jianxinIM.view.ClearWriteEditText;
import com.bcr.jianxinIM.view.city.CityPickerActivity;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.List;

import pub.devrel.easypermissions.AppSettingsDialog;
import pub.devrel.easypermissions.EasyPermissions;

import static android.Manifest.permission.CAMERA;
import static android.Manifest.permission.READ_EXTERNAL_STORAGE;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;

public class PerfectActivity extends BaseActivity implements View.OnClickListener , ActionSheet.OnActionSheetSelected, EasyPermissions.PermissionCallbacks{
    private static final int REQUEST_CODE_PICK_CITY = 233;
    public static final int TAKE_PHOTO = 1;//拍照
    public static final int CHOOSE_PHOTO = 2;//从相册选择图片
    private static final int REQUEST_RESULT_CODE = 102;//裁剪后保存
    private static final int PERFECT = 201;
    private static final int UPLOAD =208 ;
    private LinearLayout rl_titlebar;
    private ImageView ivBack;
    private TextView mTitle;
    private BorderTextView tvConfirm;
    private ClearWriteEditText edNickName;
    private TextView tvAddress,tvGender;
    private CustomPopWindow mCustomPopWindow;
    private CircleImageView ivAvatar;
    private String city;
    private  String sex;
    private ActionSheet actionSheet;
    private String orientation = null;
    private Uri imageUri;
    private Uri mUriPath;
    private String UserPic;
    private String nickName;
    private SharedPreferences sp;
    private SharedPreferences.Editor editor;
    private String base64;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_perfect);
        setHeadVisibility(View.GONE);
        AppManager.getAppManager().addActivity(this);
       // sex=1;//0是女，1是男
       // UserPic="https://p0.ssl.qhimgs1.com/bdr/400__/t01475c8e781b6c6b60.jpg";
        sp = getSharedPreferences("config", MODE_PRIVATE);
        editor = sp.edit();

        initView();

        PhotoUtils.getInstance().init(this, true, new PhotoUtils.OnSelectListener() {
            @Override
            public void onFinish(File outputFile, Uri outputUri) {
                // Bitmap bitmap = PhotoUtils.getBitmapFromUri(outputUri, UserInfoActivity.this);
                Bitmap bitmap = null;
                try {
                    bitmap = MediaStore.Images.Media.getBitmap(mContext.getContentResolver(), outputUri);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                if (bitmap != null) {
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
                    byte[] bytes = baos.toByteArray();
                    base64 = Base64.encodeToString(bytes, Base64.DEFAULT);//  编码后
                    request(UPLOAD,true);
                }
                Glide.with(PerfectActivity.this).load(outputUri).into(ivAvatar);
            }
        });
    }

    private void initView() {
        rl_titlebar=findViewById(R.id.rl_titlebar);
        ivBack=findViewById(R.id.titlebar_iv_left);
        ivBack.setVisibility(View.VISIBLE);
        mTitle=findViewById(R.id.titlebar_tv);
        tvConfirm=findViewById(R.id.tvConfirm);
        ivAvatar=findViewById(R.id.ivAvatar);
        edNickName=findViewById(R.id.edNickName);
        tvGender=findViewById(R.id.tvGender);
        tvAddress=findViewById(R.id.tvAddress);
        mTitle.setText("完善资料");
        rl_titlebar.setBackgroundResource(R.drawable.shape_titlebg_tran);
        ivBack.setOnClickListener(this);
        tvConfirm.setOnClickListener(this);
        tvGender.setOnClickListener(this);
        tvAddress.setOnClickListener(this);
        ivAvatar.setOnClickListener(this);


    }

    @Override
    public Object doInBackground(int requestCode, String id) throws HttpException {
        switch (requestCode) {
//            case CHECK_PHONE:
//                return action.checkPhoneAvailable("86", mPhone);
//            case SEND_CODE:
//                return action.sendCode("86", mPhone);
//            case VERIFY_CODE:
//                return action.verifyCode("86", mPhone, mCode);
            case PERFECT:
                return action.perfectUserInfo(sp.getString("userId",""), UserPic, nickName,Integer.valueOf(sex),city);
            case UPLOAD:
                return  action.setPortrait(base64);
        }
        return null;
    }

    @Override
    public void onSuccess(int requestCode, Object result) {
        if (result != null) {
            switch (requestCode) {
                case PERFECT:
                    LoadDialog.dismiss(mContext);
                    PerfectUserInfoResponse perfectUserInfoResponse=(PerfectUserInfoResponse) result;
                    if(perfectUserInfoResponse.isSuccess()==true){

                        Bundle bundle1=new Bundle();
                        bundle1.putString("flag","0");
                        toActivity(Login01Activity.class,bundle1);
                        finish();
                        AppManager.getAppManager().finishAllActivity();
                    }else {
                        LoadDialog.dismiss(mContext);
                        NToast.shortToast(mContext, perfectUserInfoResponse.getMessage());
                    }
                    break;
                case UPLOAD:
                    LoadDialog.dismiss(mContext);
                    SetPortraitResponse setPortraitResponse= (SetPortraitResponse) result;
                    if(setPortraitResponse.isSuccess()==true){
                        UserPic=setPortraitResponse.getResultData();
                    }else {
                        LoadDialog.dismiss(mContext);
                        NToast.shortToast(mContext, setPortraitResponse.getMessage());
                    }
            }
        }
    }

    @Override
    public void onFailure(int requestCode, int state, Object result) {
        switch (requestCode) {
            case PERFECT:
                LoadDialog.dismiss(mContext);
                PerfectUserInfoResponse perfectUserInfoResponse=(PerfectUserInfoResponse) result;
                NToast.shortToast(mContext, perfectUserInfoResponse.getMessage());
                break;
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.titlebar_iv_left:
                onBackPressed();
                break;
            case R.id.tvConfirm:
                //toActivityFinish(MainActivity.class);
                nickName=edNickName.getText().toString();
                if (TextUtils.isEmpty(UserPic)) {
                    NToast.shortToast(mContext, "请上传头像");
                    return;
                }
                if (TextUtils.isEmpty(nickName)) {
                    NToast.shortToast(mContext, "昵称不能为空");
                    edNickName.setShakeAnimation();
                    return;
                }
                if (TextUtils.isEmpty(sex)) {
                    NToast.shortToast(mContext, "请选择性别");
                    return;
                }
                if (TextUtils.isEmpty(city)) {
                    NToast.shortToast(mContext, "请选择城市");
                    return;
                }
                LoadDialog.show(mContext,"loading...",false);
                request(PERFECT, true);
                break;
            case R.id.tvGender:
                showPop();
                break;
            case R.id.ivAvatar:
               // ActionSheet.showSheet(PerfectActivity.this, PerfectActivity.this, null);

                new Dialogchoosephoto(PerfectActivity.this){

                    @Override
                    public void btnPickBySelect() {
                        //ChooseImage = true;
                        checkPermission(selectPhotoPerms, 2);
                    }

                    @Override
                    public void btnPickByTake() {
                        checkPermission(takePhotoPerms, 1);
                    }

                    @Override
                    public void CancelSelect() {
                        this.cancel();
                    }
                }.show();
                break;
            case R.id.tvAddress:
                Intent intent = new Intent(PerfectActivity.this, CityPickerActivity.class);
               // intent.putExtra("city", city);
                startActivityForResult(intent, REQUEST_CODE_PICK_CITY);
                break;
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private void showPop() {

        View contentView = LayoutInflater.from(this).inflate(R.layout.pop_gender,null);
        //处理popWindow 显示内容
        handleLogic(contentView);
        //创建并显示popWindow
        mCustomPopWindow= new CustomPopWindow.PopupWindowBuilder(this).enableBackgroundDark(true)
                .setView(contentView)
                .size(tvGender.getWidth(),ViewGroup.LayoutParams.WRAP_CONTENT)//显示大小
                .create()
                .showAsDropDown(tvGender,0,20);
        
    }
    private void handleLogic(View contentView){
        View.OnClickListener listener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mCustomPopWindow!=null){
                    mCustomPopWindow.dissmiss();
                }
                switch (v.getId()){
                    case R.id.menu:
                        break;
                    case R.id.menu1:
                        tvGender.setText("男");
                        sex="1";
                        break;
                    case R.id.menu2:
                        tvGender.setText("女");
                        sex="0";
                        break;
                }
            }
        };
        contentView.findViewById(R.id.menu1).setOnClickListener(listener);
        contentView.findViewById(R.id.menu2).setOnClickListener(listener);
    }

    String[] takePhotoPerms = {READ_EXTERNAL_STORAGE, WRITE_EXTERNAL_STORAGE, CAMERA};
    String[] selectPhotoPerms = {READ_EXTERNAL_STORAGE, WRITE_EXTERNAL_STORAGE};
    @Override
    public void onClick(int whichButton) {
        switch (whichButton) {
            case com.guoqi.actionsheet.ActionSheet.CHOOSE_PICTURE:
                //相册
                checkPermission(selectPhotoPerms, 2);
                break;
            case com.guoqi.actionsheet.ActionSheet.TAKE_PICTURE:
                //拍照
                checkPermission(takePhotoPerms, 1);
                break;
            case com.guoqi.actionsheet.ActionSheet.CANCEL:
                //取消
                break;
        }
    }
    private void checkPermission(String[] perms, int requestCode) {
        if (EasyPermissions.hasPermissions(this, perms)) {//已经有权限了
            switch (requestCode) {
                case 1:
                    PhotoUtils.getInstance().takePhoto();
                    break;
                case 2:
                    PhotoUtils.getInstance().selectPhoto();
                    break;
            }
        } else {//没有权限去请求
            EasyPermissions.requestPermissions(this, "权限", requestCode, perms);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions,
                                           int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }

    @Override
    public void onPermissionsGranted(int requestCode, List<String> perms) {//设置成功
        switch (requestCode) {
            case 1:
                PhotoUtils.getInstance().takePhoto();
                break;
            case 2:
                PhotoUtils.getInstance().selectPhoto();
                break;
        }
    }

    @Override
    public void onPermissionsDenied(int requestCode, List<String> perms) {
        if (EasyPermissions.somePermissionPermanentlyDenied(this, perms)) {
            new AppSettingsDialog.Builder(this)
                    .setTitle("权限设置")
                    .setPositiveButton("设置")
                    .setRationale("当前应用缺少必要权限,可能会造成部分功能受影响！请点击\"设置\"-\"权限\"-打开所需权限。最后点击两次后退按钮，即可返回")
                    .build()
                    .show();
        }
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode){
            case REQUEST_CODE_PICK_CITY:
                if (data != null) {
                    city = data.getStringExtra("date");
                    tvAddress.setText(city);
                    //initData(1, false);
                }
                break;

        }
        PhotoUtils.getInstance().bindForResult(requestCode, resultCode, data);
    }


}
