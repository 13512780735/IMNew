package com.bcr.jianxinIM.im.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Base64;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;

import com.bcr.jianxinIM.util.Dialogchoosephoto;
import com.bcr.jianxinIM.util.ImageLoaderUtil;
import com.bumptech.glide.Glide;
import com.guoqi.actionsheet.ActionSheet;
import com.orhanobut.logger.Logger;
import com.qiniu.android.storage.UploadManager;


import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.bcr.jianxinIM.R;
import com.bcr.jianxinIM.im.SealConst;
import com.bcr.jianxinIM.im.SealUserInfoManager;
import com.bcr.jianxinIM.im.db.Friend;
import com.bcr.jianxinIM.im.db.Groups;
import com.bcr.jianxinIM.im.server.broadcast.BroadcastManager;
import com.bcr.jianxinIM.im.server.network.http.HttpException;
import com.bcr.jianxinIM.im.server.response.CreateGroupResponse;
import com.bcr.jianxinIM.im.server.response.SetPortraitResponse;
import com.bcr.jianxinIM.im.server.utils.NToast;
import com.bcr.jianxinIM.im.server.widget.BottomMenuDialog;
import com.bcr.jianxinIM.im.server.widget.ClearWriteEditText;
import com.bcr.jianxinIM.im.server.widget.LoadDialog;
import com.bcr.jianxinIM.util.photo.PhotoUtils;
import com.bcr.jianxinIM.view.RoundImageView;

import io.rong.imkit.RongIM;
import io.rong.imkit.emoticon.AndroidEmoji;
import io.rong.imlib.model.Conversation;
import pub.devrel.easypermissions.AppSettingsDialog;
import pub.devrel.easypermissions.EasyPermissions;

import static android.Manifest.permission.CAMERA;
import static android.Manifest.permission.READ_EXTERNAL_STORAGE;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;

/**
 * Created by AMing on 16/1/25.
 * Company RongCloud
 */
public class CreateGroupActivity extends BaseActivity implements View.OnClickListener, ActionSheet.OnActionSheetSelected, EasyPermissions.PermissionCallbacks{

    private static final int CREATE_GROUP = 16;
    private static final int UPLOAD =208 ;
    public static final String REFRESH_GROUP_UI = "REFRESH_GROUP_UI";
    private RoundImageView asyncImageView;
    private PhotoUtils photoUtils;
    private BottomMenuDialog dialog;
    private String mGroupName, mGroupId;
    private ClearWriteEditText mGroupNameEdit;
    private List<String> groupIds = new ArrayList<>();
    private Uri selectUri;
    private UploadManager uploadManager;
    private String imageUrl;
    private ActionSheet actionSheet;
    private String orientation = null;
    public static final int TAKE_PHOTO = 1;//拍照
    public static final int CHOOSE_PHOTO = 2;//从相册选择图片
    private static final int REQUEST_RESULT_CODE = 102;//裁剪后保存
    private String base64;
    private Uri mUriPath;
    private Uri imageUri;
    @Override
    @SuppressWarnings("unchecked")
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_group);
        setTitle(R.string.rc_item_create_group);
        List<Friend> memberList = (List<Friend>) getIntent().getSerializableExtra("GroupMember");
        initView();
        if (memberList != null && memberList.size() > 0) {
            groupIds.add(getSharedPreferences("config", MODE_PRIVATE).getString(SealConst.SEALTALK_LOGIN_ID, ""));
            for (Friend f : memberList) {
                groupIds.add(f.getUserId());
            }
        }
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
               // Glide.with(CreateGroupActivity.this).load(outputUri).into(asyncImageView);
                Logger.d("群头像：--》"+outputUri.getPath());
               ImageLoaderUtil.displayImage(mContext,asyncImageView,"file://"+outputUri.getPath(),ImageLoaderUtil.getPhotoImageOption1());
            }
        });
    }

    private void initView() {
        asyncImageView = (RoundImageView) findViewById(R.id.img_Group_portrait);
        asyncImageView.setOnClickListener(this);
        Button mButton = (Button) findViewById(R.id.create_ok);
        mButton.setOnClickListener(this);
        mGroupNameEdit = (ClearWriteEditText) findViewById(R.id.create_groupname);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.img_Group_portrait:
              //  ActionSheet.showSheet(CreateGroupActivity.this, CreateGroupActivity.this, null);

                new Dialogchoosephoto(CreateGroupActivity.this){

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
            case R.id.create_ok:
                mGroupName = mGroupNameEdit.getText().toString().trim();
                if(TextUtils.isEmpty(imageUrl)){
                    NToast.shortToast(mContext, "群头像不能为空");
                    break;
                }
                if (TextUtils.isEmpty(mGroupName)) {
                    NToast.shortToast(mContext, getString(R.string.group_name_not_is_null));
                    break;
                }
                if (mGroupName.length() == 1) {
                    NToast.shortToast(mContext, getString(R.string.group_name_size_is_one));
                    return;
                }
                if (AndroidEmoji.isEmoji(mGroupName)) {
                    if (mGroupName.length() <= 2) {
                        NToast.shortToast(mContext, getString(R.string.group_name_size_is_one));
                        return;
                    }
                }
                if (groupIds.size() > 1) {
                    LoadDialog.show(mContext,"loading...",false);
                    request(CREATE_GROUP, true);
                }

                break;
        }
    }


    @Override
    public Object doInBackground(int requestCode, String id) throws HttpException {
        switch (requestCode) {
            case CREATE_GROUP:
                Logger.d("groupIds:-->"+groupIds);
                return action.createGroup(mGroupName, groupIds,imageUrl);
            case UPLOAD:
                return  action.setPortrait(base64);
        }
        return null;
    }

    @Override
    public void onSuccess(int requestCode, Object result) {
        if (result != null) {
            switch (requestCode) {
                case CREATE_GROUP:
                    CreateGroupResponse createGroupResponse = (CreateGroupResponse) result;
                    if (createGroupResponse.isSuccess() == true) {
                        mGroupId = createGroupResponse.getResultData(); //id == null
                            SealUserInfoManager.getInstance().addGroup(new Groups(mGroupId, mGroupName, imageUrl, String.valueOf(0)));
                            BroadcastManager.getInstance(mContext).sendBroadcast(REFRESH_GROUP_UI);
                            LoadDialog.dismiss(mContext);
                            NToast.shortToast(mContext, getString(R.string.create_group_success));
                            SealUserInfoManager.getInstance().getGroupMember(mGroupId);
                            RongIM.getInstance().startConversation(mContext, Conversation.ConversationType.GROUP, mGroupId, mGroupName);
                            finish();
                    }
                    break;
                case UPLOAD:
                   // LoadDialog.dismiss(mContext);
                    SetPortraitResponse setPortraitResponse= (SetPortraitResponse) result;
                    if(setPortraitResponse.isSuccess()==true){
                        imageUrl=setPortraitResponse.getResultData();
                    }else {
                       // LoadDialog.dismiss(mContext);
                        NToast.shortToast(mContext, setPortraitResponse.getMessage());
                    }

            }
        }
    }

    @Override
    public void onFailure(int requestCode, int state, Object result) {
        switch (requestCode) {
            case CREATE_GROUP:
                LoadDialog.dismiss(mContext);
                NToast.shortToast(mContext, getString(R.string.group_create_api_fail));
                break;

        }
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        hintKbTwo();
        finish();
        return super.onOptionsItemSelected(item);
    }







    private void hintKbTwo() {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm.isActive() && getCurrentFocus() != null) {
            if (getCurrentFocus().getWindowToken() != null) {
                imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
            }
        }
    }

    String[] takePhotoPerms = {READ_EXTERNAL_STORAGE, WRITE_EXTERNAL_STORAGE, CAMERA};
    String[] selectPhotoPerms = {READ_EXTERNAL_STORAGE, WRITE_EXTERNAL_STORAGE};
    @Override
    public void onClick(int whichButton) {
        switch (whichButton) {
            case ActionSheet.CHOOSE_PICTURE:
                //相册
                checkPermission(selectPhotoPerms, 2);
                break;
            case ActionSheet.TAKE_PICTURE:
                //拍照
                checkPermission(takePhotoPerms, 1);
                break;
            case ActionSheet.CANCEL:
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
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        PhotoUtils.getInstance().bindForResult(requestCode, resultCode, data);
    }
}
