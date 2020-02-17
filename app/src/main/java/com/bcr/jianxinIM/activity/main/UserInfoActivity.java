package com.bcr.jianxinIM.activity.main;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bcr.jianxinIM.util.Dialogchoosephoto;
import com.bumptech.glide.Glide;
import com.example.zhouwei.library.CustomPopWindow;
import com.guoqi.actionsheet.ActionSheet;
import com.orhanobut.logger.Logger;
import com.bcr.jianxinIM.R;
import com.bcr.jianxinIM.im.server.SealAction;
import com.bcr.jianxinIM.im.server.network.async.AsyncTaskManager;
import com.bcr.jianxinIM.im.server.network.async.OnDataListener;
import com.bcr.jianxinIM.im.server.network.http.HttpException;
import com.bcr.jianxinIM.im.server.response.EditUserInfoResponse;
import com.bcr.jianxinIM.im.server.response.GetUserInfoByIdResponse;
import com.bcr.jianxinIM.im.server.response.SetPortraitResponse;
import com.bcr.jianxinIM.im.server.utils.NToast;
import com.bcr.jianxinIM.im.server.widget.LoadDialog;
import com.bcr.jianxinIM.im.ui.activity.BaseActivity;
import com.bcr.jianxinIM.util.AppManager;
import com.bcr.jianxinIM.util.SharedPreferencesUtil;
import com.bcr.jianxinIM.util.photo.PhotoUtils;
import com.bcr.jianxinIM.view.CircleImageView;
import com.bcr.jianxinIM.view.city.CityPickerActivity;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.List;

import io.rong.imageloader.core.ImageLoader;
import pub.devrel.easypermissions.AppSettingsDialog;
import pub.devrel.easypermissions.EasyPermissions;

import static android.Manifest.permission.CAMERA;
import static android.Manifest.permission.READ_EXTERNAL_STORAGE;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;

public class UserInfoActivity extends BaseActivity implements View.OnClickListener, ActionSheet.OnActionSheetSelected, EasyPermissions.PermissionCallbacks {
    private static final int REQUEST_CODE_PICK_CITY = 233;
    private static final int EDITINFO = 209;
    private ImageView ivBack;
    private TextView tvTitle;
    private TextView tvRight;
    private TextView tvGender,tvCity;
    private CustomPopWindow mCustomPopWindow;
    private String city;


    public static final int TAKE_PHOTO = 1;//拍照
    public static final int CHOOSE_PHOTO = 2;//从相册选择图片
    private static final int REQUEST_RESULT_CODE = 102;//裁剪后保存
    private Uri imageUri;
    private Bitmap logoBitmap;//logo图片
    private CircleImageView ivAvatar;
    private File photoFile;
    private Uri mUriPath;
    private String photoPath;
    Uri picUri;
    private String orientation = null;
    private ActionSheet actionSheet;
    private static final int REQUSERINFO = 4234;
    private static final int UPLOAD =208 ;
    private EditText edNickName;
    private TextView edId;
    private String pic;
    private String nickName,cityString;
    private int sex;
    private String base64;
    private GetUserInfoByIdResponse getUserInfoByIdResponse;
    private TextView tvTel;
    private String Tel;
    private int selectionStart;
    private int selectionEnd;
    private CharSequence temp;
    private  int code=0;
    //定义一个文件夹路径
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_info);
        setHeadVisibility(View.GONE);
        AppManager.getAppManager().addActivity(this);
        code=getIntent().getExtras().getInt("code");
        initView();
        updateUserInfo();
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
                Glide.with(UserInfoActivity.this).load(outputUri).into(ivAvatar);
            }
        });


    }
  
    private void initView() {
        ivBack=findViewById(R.id.toolbar_left_iv);
        ivBack.setVisibility(View.VISIBLE);
        tvTitle=findViewById(R.id.title);
        tvRight=findViewById(R.id.toolbar_righ_tv);
        tvGender=findViewById(R.id.tvGender);
        tvCity=findViewById(R.id.tvCity);
        ivAvatar=findViewById(R.id.ivAvatar);
        edNickName=findViewById(R.id.edNickName);
        edId=findViewById(R.id.edId);
        tvTel=findViewById(R.id.tvTel);
        tvRight.setText("保存");
        tvRight.setVisibility(View.VISIBLE);
        tvTitle.setText("个人信息");
        ivBack.setOnClickListener(this);
        tvRight.setOnClickListener(this);
        tvGender.setOnClickListener(this);
        tvCity.setOnClickListener(this);
        findViewById(R.id.rlQRCode).setOnClickListener(this);
        findViewById(R.id.rlAvatar).setOnClickListener(this);
        edNickName.setFocusable(true);
       // edNickName.setSelection(nickName.length());
        edNickName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                //获取输入框中的数据
                String edit = edNickName.getText().toString();
                //获取过滤特殊字符后的数据
               // String stringFilter = stringFilter(edit);
//                if (!edit.equals(stringFilter)) {
//                    //如果2者不等，将匹配后的数据设置给EditText显示
//                    edNickName.setText(stringFilter);
//                }
                //将光标设置到EditText最后的位置
                edNickName.setSelection(edNickName.length());
            }

            @Override
            public void afterTextChanged(Editable s) {
                //如果EditText中的数据不为空，且长度大于指定的最大长度
                if (!TextUtils.isEmpty(s) && s.length() >8) {
                    //删除指定长度之后的数据
                    NToast.shortToast(mContext,"最长不能超过8位");
                    s.delete(8, edNickName.getSelectionEnd());
                }

            }
        });
    }
    private void updateUserInfo() {
        AsyncTaskManager.getInstance(mContext).request(REQUSERINFO, new OnDataListener() {
            @Override
            public Object doInBackground(int requestCode, String parameter) throws HttpException {
                return new SealAction(mContext).getUserInfoById(SharedPreferencesUtil.getString(mContext,"userId",""));
            }

            @Override
            public void onSuccess(int requestCode, Object result) {
                if (result != null) {
                    getUserInfoByIdResponse= (GetUserInfoByIdResponse) result;
                    if(getUserInfoByIdResponse.isSuccess()==true){
                        pic=getUserInfoByIdResponse.getResultData().getUserPic();
                        if(!TextUtils.isEmpty(pic)){
                        ImageLoader.getInstance().displayImage(pic, ivAvatar);}
                        else ivAvatar.setImageDrawable(getResources().getDrawable(R.mipmap.icon_wangwang));
                        nickName=getUserInfoByIdResponse.getResultData().getUserNickName();
                        if(TextUtils.isEmpty(getUserInfoByIdResponse.getResultData().getCity())){
                            cityString="长沙";
                        }else {
                        cityString=getUserInfoByIdResponse.getResultData().getCity();}


                        edNickName.setText(nickName);
                        edNickName.setSelection(nickName.length());
                        edId.setText(getUserInfoByIdResponse.getResultData().getUserId());
                        sex=getUserInfoByIdResponse.getResultData().getSex();
                        Tel=getUserInfoByIdResponse.getResultData().getTel();
                        tvTel.setText(Tel);
                        if(sex==0){
                            tvGender.setText("女");
                        }else {
                            tvGender.setText("男");
                        }tvCity.setText(cityString);

                    }
                }
            }

            @Override
            public void onFailure(int requestCode, int state, Object result) {

            }
        });
    }

    private void editUserInfo() {
        LoadDialog.show(mContext,"loading...",false);
        AsyncTaskManager.getInstance(mContext).request(EDITINFO, new OnDataListener() {
            @Override
            public Object doInBackground(int requestCode, String parameter) throws HttpException {
                return new SealAction(mContext).EditUserInfo(SharedPreferencesUtil.getString(mContext,"userId",""),edNickName.getText().toString(),String.valueOf(sex),cityString,pic);
            }

            @Override
            public void onSuccess(int requestCode, Object result) {
                if (result != null) {
                    EditUserInfoResponse editUserInfoResponse= (EditUserInfoResponse) result;
                    if(editUserInfoResponse.isSuccess()==true){
                        LoadDialog.dismiss(mContext);
                        NToast.shortToast(mContext,editUserInfoResponse.getMessage());
                        finish();
                    }else {
                        LoadDialog.dismiss(mContext);
                        NToast.shortToast(mContext,editUserInfoResponse.getMessage());
                    }
                }
            }

            @Override
            public void onFailure(int requestCode, int state, Object result) {
                LoadDialog.dismiss(mContext);
            }
        });
    }
    private static final int BAIDU_READ_PHONE_STATE = 100;//定位权限请求
    private static final int PRIVATE_CODE = 1315;//开启GPS权限

    static final String[] LOCATIONGPS = new String[]{Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.READ_PHONE_STATE
    };
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.toolbar_left_iv:
                onBackPressed();
                break;
            case R.id.toolbar_righ_tv:
                String userName=edNickName.getText().toString().trim();
                if(!TextUtils.isEmpty(userName)||userName.length()>0){
                    editUserInfo();
                }else {
                    NToast.shortToast(mContext,"名称不能为空");
                    return;
                }
                break;
            case R.id.tvGender:
                showPop();
                break;
            case R.id.rlAvatar:
               //ActionSheet.showSheet(UserInfoActivity.this, UserInfoActivity.this, null);
                new Dialogchoosephoto(UserInfoActivity.this){

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
            case R.id.rlQRCode:
               // toActivity(MyQRCodeActivity.class);
                Bundle bundle=new Bundle();
                bundle.putString("userName",nickName);
                bundle.putString("userPic",pic);
                bundle.putInt("code",code);
                toActivity(MyQRCodeActivity.class,bundle);
                break;
            case R.id.tvCity:
                if (ContextCompat.checkSelfPermission(UserInfoActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    //ActivityCompat.requestPermissions(UserInfoActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
                    ActivityCompat.requestPermissions(this, LOCATIONGPS, BAIDU_READ_PHONE_STATE);
                } else {
                    goCity();
                }


                break;
        }
    }
    private void goCity() {
        Intent intent = new Intent(UserInfoActivity.this, CityPickerActivity.class);
        startActivityForResult(intent, REQUEST_CODE_PICK_CITY);
    }


    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private void showPop() {
        View contentView = LayoutInflater.from(this).inflate(R.layout.pop_gender,null);
        //处理popWindow 显示内容
        handleLogic(contentView);
        //创建并显示popWindow
        mCustomPopWindow= new CustomPopWindow.PopupWindowBuilder(this).enableBackgroundDark(true)
                .setView(contentView)
                .size(tvGender.getWidth(), ViewGroup.LayoutParams.WRAP_CONTENT)//显示大小
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
                        sex=1;
                        break;
                    case R.id.menu2:
                        tvGender.setText("女");
                        sex=0;
                        break;
                }
            }
        };
        contentView.findViewById(R.id.menu1).setOnClickListener(listener);
        contentView.findViewById(R.id.menu2).setOnClickListener(listener);
    }




    private String getImagePath(Uri uri, String selection) {
        String path = null;
        //通过uri和selection来获取真实的图片路径
        Cursor cursor = getContentResolver().query(uri, null, selection, null, null);
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                path = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
            }
            orientation = cursor.getString(cursor.getColumnIndex("orientation"));// 获取旋转的角度
            cursor.close();
        }
        return path;
    }

    @Override
    public Object doInBackground(int requestCode, String id) throws HttpException {
        switch (requestCode) {
            case UPLOAD:
                return  action.setPortrait(base64);
        }
        return super.doInBackground(requestCode, id);

    }

    @Override
    public void onSuccess(int requestCode, Object result) {
        if (result != null) {
            switch (requestCode) {
                case UPLOAD:
                    LoadDialog.dismiss(mContext);
                    SetPortraitResponse setPortraitResponse= (SetPortraitResponse) result;
                    if(setPortraitResponse.isSuccess()==true){
                        pic=setPortraitResponse.getResultData();
                    }else {
                        LoadDialog.dismiss(mContext);
                        NToast.shortToast(mContext, setPortraitResponse.getMessage());
                    }
            }
        }
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

        switch (requestCode) {
            case BAIDU_READ_PHONE_STATE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    goCity();
                } else {
                    Toast.makeText(this, "你拒绝了权限申请，可能无法定位获取地址！", Toast.LENGTH_SHORT).show();
                }
                break;
            default:
        }
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
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode){
            case REQUEST_CODE_PICK_CITY:
                if (data != null) {
                    cityString = data.getStringExtra("date");
                    tvCity.setText(cityString);
                    //initData(1, false);
                }
                break;
        }
        PhotoUtils.getInstance().bindForResult(requestCode, resultCode, data);
    }


}
