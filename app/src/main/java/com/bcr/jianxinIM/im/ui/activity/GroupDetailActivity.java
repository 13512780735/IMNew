package com.bcr.jianxinIM.im.ui.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.bcr.jianxinIM.im.server.response.SetAddFriendResponse;
import com.bcr.jianxinIM.util.Dialogchoosephoto;
import com.bcr.jianxinIM.util.ImageLoaderUtil;
import com.bumptech.glide.Glide;
import com.guoqi.actionsheet.ActionSheet;
import com.orhanobut.logger.Logger;
import com.qiniu.android.storage.UploadManager;
import com.bcr.jianxinIM.R;
import com.bcr.jianxinIM.im.SealAppContext;
import com.bcr.jianxinIM.im.SealConst;
import com.bcr.jianxinIM.im.SealUserInfoManager;
import com.bcr.jianxinIM.im.db.DBManager;
import com.bcr.jianxinIM.im.db.Friend;
import com.bcr.jianxinIM.im.db.GroupMember;
import com.bcr.jianxinIM.im.db.Groups;
import com.bcr.jianxinIM.im.db.GroupsDao;
import com.bcr.jianxinIM.im.model.SealSearchConversationResult;
import com.bcr.jianxinIM.im.server.broadcast.BroadcastManager;
import com.bcr.jianxinIM.im.server.network.http.HttpException;
import com.bcr.jianxinIM.im.server.pinyin.CharacterParser;
import com.bcr.jianxinIM.im.server.response.DismissGroupResponse;
import com.bcr.jianxinIM.im.server.response.GetGroupInfoResponse;
import com.bcr.jianxinIM.im.server.response.QiNiuTokenResponse;
import com.bcr.jianxinIM.im.server.response.QuitGroupResponse;
import com.bcr.jianxinIM.im.server.response.SetBannedResponse;
import com.bcr.jianxinIM.im.server.response.SetGroupDisplayNameResponse;
import com.bcr.jianxinIM.im.server.response.SetGroupNameResponse;
import com.bcr.jianxinIM.im.server.response.SetGroupPortraitResponse;
import com.bcr.jianxinIM.im.server.response.SetPortraitResponse;
import com.bcr.jianxinIM.im.server.utils.CommonUtils;
import com.bcr.jianxinIM.im.server.utils.NToast;
import com.bcr.jianxinIM.im.server.utils.OperationRong;
import com.bcr.jianxinIM.im.server.utils.RongGenerate;
import com.bcr.jianxinIM.im.server.utils.json.JsonMananger;
import com.bcr.jianxinIM.im.server.widget.BottomMenuDialog;
import com.bcr.jianxinIM.im.server.widget.LoadDialog;
import com.bcr.jianxinIM.im.ui.widget.DemoGridView;
import com.bcr.jianxinIM.im.ui.widget.switchbutton.SwitchButton;
import com.bcr.jianxinIM.util.SharedPreferencesUtil;
import com.bcr.jianxinIM.util.photo.PhotoUtils;
import com.bcr.jianxinIM.view.CircleImageView;
import com.bcr.jianxinIM.view.CommonDialog;
import com.bcr.jianxinIM.view.RoundImageView;
import com.bcr.jianxinIM.view.SimpleInputDialog;


import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import io.rong.imageloader.core.ImageLoader;
import io.rong.imkit.RongIM;
import io.rong.imkit.emoticon.AndroidEmoji;
import io.rong.imlib.RongIMClient;
import io.rong.imlib.model.Conversation;
import io.rong.imlib.model.Group;
import io.rong.imlib.model.Message;
import io.rong.imlib.model.UserInfo;
import pub.devrel.easypermissions.AppSettingsDialog;
import pub.devrel.easypermissions.EasyPermissions;

import static android.Manifest.permission.CAMERA;
import static android.Manifest.permission.READ_EXTERNAL_STORAGE;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;

/**
 * Created by AMing on 16/1/27.
 * Company RongCloud
 */
public class GroupDetailActivity extends BaseActivity implements View.OnClickListener, CompoundButton.OnCheckedChangeListener, ActionSheet.OnActionSheetSelected, EasyPermissions.PermissionCallbacks {

    private static final int CLICK_CONVERSATION_USER_PORTRAIT = 1;

    private static final int DISMISS_GROUP = 26;
    private static final int QUIT_GROUP = 27;
    private static final int SET_GROUP_NAME = 29;
    private static final int GET_GROUP_INFO = 30;
    private static final int UPDATE_GROUP_NAME = 32;
    private static final int GET_QI_NIU_TOKEN = 133;
    private static final int UPDATE_GROUP_HEADER = 25;
    private static final int SEARCH_TYPE_FLAG = 1;
    private static final int CHECKGROUPURL = 39;
    private static final int BANNED =205;
    private static final int GET_GROUP_INFOS =206;
    private static final int UPDATE_GROUP_DISNAME =207 ;
    private static final int GETGROUPMEMBER =215 ;
    private static final int ADDFRIEND = 248;


    private boolean isCreated = false;
    private DemoGridView mGridView;
    private List<GroupMember> mGroupMember;
    private TextView mTextViewMemberSize, mGroupDisplayNameText;
    private RoundImageView mGroupHeader;
    private SwitchButton messageTop, messageNotification,sw_group_banned,sw_add_friend;
    private Groups mGroup;
    private String fromConversationId;
    private Conversation.ConversationType mConversationType;
    private boolean isFromConversation;
    private LinearLayout mGroupAnnouncementDividerLinearLayout;
    private TextView mGroupName;
    private PhotoUtils photoUtils;
    private BottomMenuDialog dialog;
    private UploadManager uploadManager;
    private String imageUrl;
    private Uri selectUri;
    private String newGroupName;
    private LinearLayout mGroupNotice;
    private LinearLayout mSearchMessagesLinearLayout;
    private Button mDismissBtn;
    private Button mQuitBtn;
    private SealSearchConversationResult mResult;
    private String Statu;
    private int isMute;
    private LinearLayout llsw_group_banned;
    private LinearLayout mGroupPortL;
    private LinearLayout mGroupNameL;
    private TextView tvNotice;
    private String newGroupDisName;
    private String base64;
    private static final int UPLOAD =208 ;
    private LinearLayout group_code;
    private LinearLayout ll_add_friend;
    private int isAddFriend;
    private String Status;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_group);
        initViews();
        setTitle(R.string.group_info);

        //群组会话界面点进群组详情
        fromConversationId = getIntent().getStringExtra("TargetId");
        mConversationType = (Conversation.ConversationType) getIntent().getSerializableExtra("conversationType");
        Logger.d("fromConversationId:-->"+fromConversationId);
        if (!TextUtils.isEmpty(fromConversationId)) {
            isFromConversation = true;
        }

//        if (isFromConversation) {//群组会话页进入
//            Logger.d("isFromConversation:-->"+isFromConversation);
//            LoadDialog.show(mContext);
//            getGroups();
//            request(GET_GROUP_INFOS,true);
//            getGroupMembers();
//        }
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
              //  Glide.with(GroupDetailActivity.this).load(outputUri).into(mGroupHeader);
                ImageLoaderUtil.displayImage(mContext,mGroupHeader,outputUri.getPath(),ImageLoaderUtil.getPhotoImageOption2());

            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();

        onrefresh();

    }

    private void onrefresh() {
        if (isFromConversation) {//群组会话页进入
            Logger.d("isFromConversation:-->"+isFromConversation);
            LoadDialog.show(mContext,"loading...",false);
            getGroups();
            request(GET_GROUP_INFOS,true);
            getGroupMembers();
        }
        SealAppContext.getInstance().pushActivity(this);

        setGroupsInfoChangeListener();
    }

    private void getGroups() {
        Logger.d("getGroups:-->"+"getGroups");


        SealUserInfoManager.getInstance().getGroupsByID(fromConversationId, new SealUserInfoManager.ResultCallback<Groups>() {

            @Override
            public void onSuccess(Groups groups) {
                if (groups != null) {
                    mGroup = groups;
                    initGroupData();
                }
            }

            @Override
            public void onError(String errString) {

            }
        });
    }

    private void getGroupMembers() {
        SealUserInfoManager.getInstance().getGroupMembers(fromConversationId, new SealUserInfoManager.ResultCallback<List<GroupMember>>() {
            @Override
            public void onSuccess(List<GroupMember> groupMembers) {
              for (GroupMember u : groupMembers) {
              }
                LoadDialog.dismiss(mContext);
                if (groupMembers != null && groupMembers.size() > 0) {
                    mGroupMember = groupMembers;
                    initGroupMemberData();
                }
            }

            @Override
            public void onError(String errString) {
                LoadDialog.dismiss(mContext);
            }
        });
    }
    private void getGroupMembers1() {
        SealUserInfoManager.getInstance().getGroupMembers1(fromConversationId, new SealUserInfoManager.ResultCallback<List<GroupMember>>() {
            @Override
            public void onSuccess(List<GroupMember> groupMembers) {
                for (GroupMember u : groupMembers) {
                }
                LoadDialog.dismiss(mContext);
                if (groupMembers != null && groupMembers.size() > 0) {
                    mGroupMember = groupMembers;
                    initGroupMemberData();
                }
            }

            @Override
            public void onError(String errString) {
                LoadDialog.dismiss(mContext);
            }
        });
    }

    @Override
    protected void onDestroy() {
        BroadcastManager.getInstance(this).destroy(SealAppContext.UPDATE_GROUP_NAME);
        BroadcastManager.getInstance(this).destroy(SealAppContext.UPDATE_GROUP_MEMBER);
        BroadcastManager.getInstance(this).destroy(SealAppContext.GROUP_DISMISS);
        super.onDestroy();
    }

    private void initGroupData() {
        String portraitUri = SealUserInfoManager.getInstance().getPortraitUri(mGroup);
        //ImageLoader.getInstance().displayImage(portraitUri, mGroupHeader);
        ImageLoaderUtil.displayImage(mContext,mGroupHeader,portraitUri,ImageLoaderUtil.getPhotoImageOption2());
        mGroupName.setText(mGroup.getName());
        if (RongIM.getInstance() != null) {
            RongIM.getInstance().getConversation(Conversation.ConversationType.GROUP, mGroup.getGroupsId(), new RongIMClient.ResultCallback<Conversation>() {
                @Override
                public void onSuccess(Conversation conversation) {
                    if (conversation == null) {
                        return;
                    }
                    if (conversation.isTop()) {
                        messageTop.setChecked(true);
                    } else {
                        messageTop.setChecked(false);
                    }

                }

                @Override
                public void onError(RongIMClient.ErrorCode errorCode) {

                }
            });

            RongIM.getInstance().getConversationNotificationStatus(Conversation.ConversationType.GROUP, mGroup.getGroupsId(), new RongIMClient.ResultCallback<Conversation.ConversationNotificationStatus>() {
                @Override
                public void onSuccess(Conversation.ConversationNotificationStatus conversationNotificationStatus) {

                    if (conversationNotificationStatus == Conversation.ConversationNotificationStatus.DO_NOT_DISTURB) {
                        messageNotification.setChecked(true);
                    } else {
                        messageNotification.setChecked(false);
                    }
                }

                @Override
                public void onError(RongIMClient.ErrorCode errorCode) {

                }
            });
        }
        Logger.d("getRole();--》"+mGroup.getRole());
        if (mGroup.getRole().equals("0"))
            isCreated = true;
        if (!isCreated) {
            mGroupAnnouncementDividerLinearLayout.setVisibility(View.VISIBLE);
            mGroupNotice.setVisibility(View.VISIBLE);
            llsw_group_banned.setVisibility(View.GONE);
            ll_add_friend.setVisibility(View.GONE);
            mGroupPortL.setClickable(false);
            mGroupNameL.setClickable(false);
            mGroupNotice.setClickable(false);
        } else {
            mGroupAnnouncementDividerLinearLayout.setVisibility(View.VISIBLE);
            mDismissBtn.setVisibility(View.VISIBLE);
            mQuitBtn.setVisibility(View.GONE);
            mGroupNotice.setVisibility(View.VISIBLE);
            mGroupNotice.setClickable(true);
            mGroupPortL.setClickable(true);
            mGroupNameL.setClickable(true);
        }
        if (CommonUtils.isNetworkConnected(mContext)) {
            request(CHECKGROUPURL);
        }
    }

    private void initGroupMemberData() {

        Logger.d("initGroupMemberData;-->"+mGroupMember.size());
        if (mGroupMember != null && mGroupMember.size() > 0) {
            setTitle(getString(R.string.group_info) + "(" + mGroupMember.size() + ")");
            mTextViewMemberSize.setText(getString(R.string.group_member_size) + "(" + mGroupMember.size() + ")");
            GridAdapter gridAdapter=new GridAdapter(mContext,mGroupMember);
            mGridView.setAdapter(gridAdapter);
            gridAdapter.notifyDataSetChanged();

        } else {
            return;
        }

        for (GroupMember member : mGroupMember) {
            Logger.d("disName888:-->"+member.getDisplayName());
            if (member.getUserId().equals(getSharedPreferences("config", MODE_PRIVATE).getString(SealConst.SEALTALK_LOGIN_ID, ""))) {
                if (!TextUtils.isEmpty(member.getDisplayName())) {
                    Logger.d("disName:-->"+member.getDisplayName());
                    mGroupDisplayNameText.setText(member.getDisplayName());
                } else {
                    mGroupDisplayNameText.setText("无");
                }
            }
        }
    }

    @Override
    public Object doInBackground(int requestCode, String id) throws HttpException {
        switch (requestCode) {
            case QUIT_GROUP:
                return action.quitGroup(SharedPreferencesUtil.getString(mContext,"userId",""),fromConversationId);
            case DISMISS_GROUP:
                return action.dissmissGroup(SharedPreferencesUtil.getString(mContext,"userId",""),fromConversationId);
            case SET_GROUP_NAME:
                return action.setGroupDisplayName(fromConversationId, newGroupDisName,SharedPreferencesUtil.getString(mContext,"userId",""));
            case GET_GROUP_INFO:
                return action.getGroupInfo(fromConversationId);
            case GET_GROUP_INFOS:
                return action.getGroupInfo(fromConversationId);
            case UPDATE_GROUP_HEADER:
                return action.setGroupPortrait(SharedPreferencesUtil.getString(mContext,"userId",""),fromConversationId, imageUrl);
            case GET_QI_NIU_TOKEN:
                return action.getQiNiuToken();
            case UPDATE_GROUP_NAME:
                return action.setGroupName(fromConversationId, newGroupName,SharedPreferencesUtil.getString(mContext,"userId",""));
            case CHECKGROUPURL:
                return action.getGroupInfo(fromConversationId);
            case BANNED:
                return action.setBanned(Statu,fromConversationId);
            case UPLOAD:
                return  action.setPortrait(base64);
            case ADDFRIEND:
                return  action.setAddFriend(fromConversationId,Status);
        }
        return super.doInBackground(requestCode, id);
    }

    @Override
    public void onSuccess(int requestCode, Object result) {
        if (result != null) {
            switch (requestCode) {
                case QUIT_GROUP:
                    QuitGroupResponse response = (QuitGroupResponse) result;
                    if (response.isSuccess() == true) {

                        RongIM.getInstance().getConversation(Conversation.ConversationType.GROUP, fromConversationId, new RongIMClient.ResultCallback<Conversation>() {
                            @Override
                            public void onSuccess(Conversation conversation) {
                                RongIM.getInstance().clearMessages(Conversation.ConversationType.GROUP, fromConversationId, new RongIMClient.ResultCallback<Boolean>() {
                                    @Override
                                    public void onSuccess(Boolean aBoolean) {
                                        RongIM.getInstance().removeConversation(Conversation.ConversationType.GROUP, fromConversationId, null);
                                    }

                                    @Override
                                    public void onError(RongIMClient.ErrorCode e) {

                                    }
                                });
                            }

                            @Override
                            public void onError(RongIMClient.ErrorCode e) {

                            }
                        });
                        SealUserInfoManager.getInstance().deleteGroups(new Groups(fromConversationId));
                        SealUserInfoManager.getInstance().deleteGroupMembers(fromConversationId);
                        BroadcastManager.getInstance(mContext).sendBroadcast(SealConst.GROUP_LIST_UPDATE);
                        setResult(501, new Intent());
                        NToast.shortToast(mContext, getString(R.string.quit_success));
                        LoadDialog.dismiss(mContext);
                        Logger.d("发送信息");
                        finish();
                    }
                    break;

                case DISMISS_GROUP:
                    DismissGroupResponse response1 = (DismissGroupResponse) result;
                    if (response1.isSuccess() == true) {
                        RongIM.getInstance().getConversation(Conversation.ConversationType.GROUP, fromConversationId, new RongIMClient.ResultCallback<Conversation>() {
                            @Override
                            public void onSuccess(Conversation conversation) {
                                RongIM.getInstance().clearMessages(Conversation.ConversationType.GROUP, fromConversationId, new RongIMClient.ResultCallback<Boolean>() {
                                    @Override
                                    public void onSuccess(Boolean aBoolean) {
                                        RongIM.getInstance().removeConversation(Conversation.ConversationType.GROUP, fromConversationId, null);
                                    }

                                    @Override
                                    public void onError(RongIMClient.ErrorCode e) {

                                    }
                                });
                            }

                            @Override
                            public void onError(RongIMClient.ErrorCode e) {

                            }
                        });
                        SealUserInfoManager.getInstance().deleteGroups(new Groups(fromConversationId));
                        SealUserInfoManager.getInstance().deleteGroupMembers(fromConversationId);
                        BroadcastManager.getInstance(mContext).sendBroadcast(SealConst.GROUP_LIST_UPDATE);
                        setResult(501, new Intent());
                        NToast.shortToast(mContext, getString(R.string.dismiss_success));
                        LoadDialog.dismiss(mContext);
                        finish();
                    }
                    break;

                case SET_GROUP_NAME:
                    SetGroupDisplayNameResponse response2 = (SetGroupDisplayNameResponse) result;
                    if (response2.isSuccess() == true) {
                        //request(GET_GROUP_INFO);
                        Logger.d("newGroupDisName:-->"+newGroupDisName);
                        mGroupDisplayNameText.setText(newGroupDisName);
                        //getGroupMembers();
                        getGroupMembers1();
                        //onrefresh();
//                        RongIM.getInstance().refreshUserInfoCache(new UserInfo(SharedPreferencesUtil.getString(mContext,"userId",""), newGroupDisName, null));
//                        //RongIM.getInstance().refreshGroupInfoCache(new Group(fromConversationId, newGroupDisName, null));
//
//                        BroadcastManager.getInstance(mContext).sendBroadcast(SealAppContext.UPDATE_FRIEND);
//                        LoadDialog.dismiss(mContext);
//                        NToast.shortToast(mContext, response2.getMessage());
//                        getGroupMembers();
//                        LoadDialog.dismiss(mContext);
//                        NToast.shortToast(mContext, response2.getMessage());
//                        BroadcastManager.getInstance(mContext).sendBroadcast(SealConst.GROUP_LIST_UPDATE);
                    }
                    break;
                case GET_GROUP_INFO:
                    GetGroupInfoResponse response3 = (GetGroupInfoResponse) result;
                    if (response3.isSuccess() == true) {
                        int i;
                        if (isCreated) {
                            i = 0;
                        } else {
                            i = 1;
                        }
                        GetGroupInfoResponse.ResultDataBean bean = response3.getResultData();
                        SealUserInfoManager.getInstance().addGroup(
                                new Groups(bean.getId(), bean.getName(), bean.getPortraitUri(), newGroupName, String.valueOf(i), null)
                        );

                        mGroupDisplayNameText.setText(newGroupDisName);
                        RongIM.getInstance().refreshGroupInfoCache(new Group(fromConversationId, newGroupName, Uri.parse(bean.getPortraitUri())));
                        LoadDialog.dismiss(mContext);
                        NToast.shortToast(mContext, response3.getMessage());
                    }
                    break;
                case GET_GROUP_INFOS:
                    GetGroupInfoResponse getGroupInfoResponse = (GetGroupInfoResponse) result;
                    if(getGroupInfoResponse.isSuccess()==true){
                        isMute=getGroupInfoResponse.getResultData().getIsMute();
                        isAddFriend=getGroupInfoResponse.getResultData().getIsAddFriend();
                        Logger.d("isMute11:-->"+isMute);
                        String notice=getGroupInfoResponse.getResultData().getBulletin();
                        if(!TextUtils.isEmpty(notice)){
                            tvNotice.setText(notice);
                        }else tvNotice.setVisibility(View.GONE);

                        if(isAddFriend==0){
                            sw_add_friend.setChecked(false);
                        }else {
                            sw_add_friend.setChecked(true);
                        }

                        if(isMute==0){
                            sw_group_banned.setChecked(false);
                        }else{
                            sw_group_banned.setChecked(true);
                        }
                    }
                    break;
                case UPDATE_GROUP_HEADER:
                    SetGroupPortraitResponse response5 = (SetGroupPortraitResponse) result;
                    if (response5.isSuccess() == true) {
                        //ImageLoader.getInstance().displayImage(imageUrl, mGroupHeader);
                        ImageLoaderUtil.displayImage(mContext,mGroupHeader,imageUrl,ImageLoaderUtil.getPhotoImageOption2());
                        RongIM.getInstance().refreshGroupInfoCache(new Group(fromConversationId, mGroup.getName(), Uri.parse(imageUrl)));
                        LoadDialog.dismiss(mContext);
                        NToast.shortToast(mContext, getString(R.string.update_success));
                    }

                    break;
                case GET_QI_NIU_TOKEN:
                    QiNiuTokenResponse response6 = (QiNiuTokenResponse) result;
                    if (response6.getCode() == 200) {
                       // uploadImage(response6.getResult().getDomain(), response6.getResult().getToken(), selectUri);
                    }
                    break;
                case UPDATE_GROUP_NAME:
                    SetGroupNameResponse response7 = (SetGroupNameResponse) result;
                    if (response7.isSuccess() == true) {
                        SealUserInfoManager.getInstance().addGroup(
                                new Groups(mGroup.getGroupsId(), newGroupName, mGroup.getPortraitUri(), mGroup.getRole())
                        );
                        mGroupName.setText(newGroupName);
                        RongIM.getInstance().refreshGroupInfoCache(new Group(fromConversationId, newGroupName, TextUtils.isEmpty(mGroup.getPortraitUri()) ? Uri.parse(RongGenerate.generateDefaultAvatar(newGroupName, mGroup.getGroupsId())) : Uri.parse(mGroup.getPortraitUri())));
                        LoadDialog.dismiss(mContext);
                        NToast.shortToast(mContext, getString(R.string.update_success));
                        BroadcastManager.getInstance(mContext).sendBroadcast(SealConst.GROUP_LIST_UPDATE);
                        setGroupsInfoChangeListener();
                    }
                    break;
                case CHECKGROUPURL:
                    GetGroupInfoResponse groupInfoResponse = (GetGroupInfoResponse) result;
                    if (groupInfoResponse.isSuccess() == true) {
                        if (groupInfoResponse.getResultData() != null) {
                            mGroupName.setText(groupInfoResponse.getResultData().getName());
                            String portraitUri = SealUserInfoManager.getInstance().getPortraitUri(groupInfoResponse);
                            ImageLoaderUtil.displayImage(mContext,mGroupHeader,portraitUri,ImageLoaderUtil.getPhotoImageOption2());
                            RongIM.getInstance().refreshGroupInfoCache(new Group(fromConversationId, groupInfoResponse.getResultData().getName(), TextUtils.isEmpty(groupInfoResponse.getResultData().getPortraitUri()) ? Uri.parse(RongGenerate.generateDefaultAvatar(groupInfoResponse.getResultData().getName(), groupInfoResponse.getResultData().getId())) : Uri.parse(groupInfoResponse.getResultData().getPortraitUri())));
                        }
                    }
                    break;

                case BANNED:
                    SetBannedResponse setBannedResponse= (SetBannedResponse) result;
                    if(setBannedResponse.isSuccess()==true){
                        //NToast.shortToast(mContext, setBannedResponse.getMessage());
                        request(GET_GROUP_INFOS);
                    }else {
                        NToast.shortToast(mContext,setBannedResponse.getMessage());
                    }
                    break;
                case UPLOAD:
                    LoadDialog.dismiss(mContext);
                    SetPortraitResponse setPortraitResponse= (SetPortraitResponse) result;
                    if(setPortraitResponse.isSuccess()==true){
                        imageUrl=setPortraitResponse.getResultData();
                        request(UPDATE_GROUP_HEADER);
                    }else {
                        LoadDialog.dismiss(mContext);
                       // NToast.shortToast(mContext, setPortraitResponse.getMessage());
                    }

                case ADDFRIEND:
                    SetAddFriendResponse setAddFriendResponse= (SetAddFriendResponse) result;
                    if (setAddFriendResponse.isSuccess()==true) {
                        request(GET_GROUP_INFOS);
                    }else {
                        NToast.shortToast(mContext,setAddFriendResponse.getMessage());
                    }
                    break;
            }
        }
    }


    @Override
    public void onFailure(int requestCode, int state, Object result) {
        switch (requestCode) {
            case QUIT_GROUP:
                NToast.shortToast(mContext, "退出群组请求失败");
                LoadDialog.dismiss(mContext);
                break;
            case DISMISS_GROUP:
                NToast.shortToast(mContext, "解散群组请求失败");
                LoadDialog.dismiss(mContext);
                break;
            case BANNED:
                SetBannedResponse setBannedResponse= (SetBannedResponse) result;
                if(setBannedResponse.isSuccess()==true){
                    NToast.shortToast(mContext, setBannedResponse.getMessage());
                }
                break;
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.group_quit:
                CommonDialog commonDialog = new CommonDialog.Builder()
                        .setContentMessage(getString(R.string.confirm_quit_group))
                        .setDialogButtonClickListener(new CommonDialog.OnDialogButtonClickListener() {
                            @Override
                            public void onPositiveClick(View v, Bundle bundle) {
                                LoadDialog.show(mContext,"loading...",false);
                                request(QUIT_GROUP);
                            }

                            @Override
                            public void onNegativeClick(View v, Bundle bundle) {
                            }
                        })
                        .build();
                commonDialog.show(getSupportFragmentManager(), null);


                break;
            case R.id.group_dismiss:
                CommonDialog commonDialog1 = new CommonDialog.Builder()
                        .setContentMessage( getString(R.string.confirm_dismiss_group))
                        .setDialogButtonClickListener(new CommonDialog.OnDialogButtonClickListener() {
                            @Override
                            public void onPositiveClick(View v, Bundle bundle) {
                                LoadDialog.show(mContext,"loading...",false);
                                request(DISMISS_GROUP);
                            }

                            @Override
                            public void onNegativeClick(View v, Bundle bundle) {
                            }
                        })
                        .build();
                commonDialog1.show(getSupportFragmentManager(), null);

                break;
            case R.id.ac_ll_search_chatting_records:
                Intent searchIntent = new Intent(GroupDetailActivity.this, SealSearchChattingDetailActivity.class);
                searchIntent.putExtra("filterString", "");
                ArrayList<Message> arrayList = new ArrayList<>();
                searchIntent.putParcelableArrayListExtra("filterMessages", arrayList);
                mResult = new SealSearchConversationResult();
                Conversation conversation = new Conversation();
                conversation.setTargetId(fromConversationId);
                conversation.setConversationType(mConversationType);
                mResult.setConversation(conversation);
                Groups groupInfo = DBManager.getInstance().getDaoSession().getGroupsDao().queryBuilder().where(GroupsDao.Properties.GroupsId.eq(fromConversationId)).unique();
                if (groupInfo != null) {
                    String portraitUri = groupInfo.getPortraitUri();
                    mResult.setId(groupInfo.getGroupsId());

                    if (!TextUtils.isEmpty(portraitUri)) {
                        mResult.setPortraitUri(portraitUri);
                    }
                    if (!TextUtils.isEmpty(groupInfo.getName())) {
                        mResult.setTitle(groupInfo.getName());
                    } else {
                        mResult.setTitle(groupInfo.getGroupsId());
                    }

                    searchIntent.putExtra("searchConversationResult", mResult);
                    searchIntent.putExtra("flag", SEARCH_TYPE_FLAG);
                    startActivity(searchIntent);
                }
                break;
            case R.id.group_clean:
                CommonDialog dialog = new CommonDialog.Builder()
                        .setContentMessage( getString(R.string.clean_group_chat_history))
                        .setDialogButtonClickListener(new CommonDialog.OnDialogButtonClickListener() {
                            @Override
                            public void onPositiveClick(View v, Bundle bundle) {
                                if (RongIM.getInstance() != null) {
                                    if (mGroup != null) {
                                        RongIM.getInstance().clearMessages(Conversation.ConversationType.GROUP, mGroup.getGroupsId(), new RongIMClient.ResultCallback<Boolean>() {
                                            @Override
                                            public void onSuccess(Boolean aBoolean) {
                                                NToast.shortToast(mContext, getString(R.string.clear_success));
                                            }

                                            @Override
                                            public void onError(RongIMClient.ErrorCode errorCode) {
                                                NToast.shortToast(mContext, getString(R.string.clear_failure));
                                            }
                                        });
                                        RongIMClient.getInstance().cleanRemoteHistoryMessages(Conversation.ConversationType.GROUP, mGroup.getGroupsId(), System.currentTimeMillis(), null);
                                    }
                                }
                            }



                            @Override
                            public void onNegativeClick(View v, Bundle bundle) {
                            }
                        })
                        .build();
                dialog.show(getSupportFragmentManager(), null);


                break;
            case R.id.group_member_size_item:
                Intent intent = new Intent(mContext, TotalGroupMemberActivity.class);
//                intent.putExtra("targetId", fromConversationId);
//                startActivity(intent);
                break;
            case R.id.group_member_online_status:
                intent = new Intent(mContext, MembersOnlineStatusActivity.class);
                intent.putExtra("targetId", fromConversationId);
                startActivity(intent);
                break;
            case R.id.ll_group_port:
                if (isCreated) {
                   // ActionSheet.showSheet(GroupDetailActivity.this, GroupDetailActivity.this, null);
                    new Dialogchoosephoto(GroupDetailActivity.this){

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
                }
                break;
            case R.id.ll_group_name:
                if (isCreated) {
                    SimpleInputDialog dialog1 = new SimpleInputDialog();
                    dialog1.setInputHint(getString(R.string.new_group_name));
                    dialog1.setInputDialogListener(new SimpleInputDialog.InputDialogListener() {
                        @Override
                        public boolean onConfirmClicked(EditText input) {
                            String editText=input.getText().toString();
                            if (TextUtils.isEmpty(editText)) {
                                return false;
                            }
                            if (editText.length() < 2 && editText.length() > 10) {
                                NToast.shortToast(mContext, "群名称应为 2-10 字");
                                return false;
                            }

                            if (AndroidEmoji.isEmoji(editText) && editText.length() < 4) {
                                NToast.shortToast(mContext, "群名称表情过短");
                                return false;
                            }
                            newGroupName = editText;
                            LoadDialog.show(mContext,"loading...",false);
                            request(UPDATE_GROUP_NAME);
                            return true;
                        }
                    });
                    dialog1.show(getSupportFragmentManager(), null);
                }
                break;
            case R.id.group_announcement:
                Intent tempIntent = new Intent(mContext, GroupNoticeActivity.class);
                tempIntent.putExtra("conversationType", Conversation.ConversationType.GROUP.getValue());
                tempIntent.putExtra("targetId", fromConversationId);
                startActivityForResult(tempIntent, 1001);

                break;

            case R.id.group_displayname:
                SimpleInputDialog dialog1 = new SimpleInputDialog();
                dialog1.setInputHint("我在本群的昵称");
                dialog1.setInputDialogListener(new SimpleInputDialog.InputDialogListener() {
                    @Override
                    public boolean onConfirmClicked(EditText input) {
                        String editText=input.getText().toString();
                        if (TextUtils.isEmpty(editText)) {
                            return false;
                        }
                        if (editText.length() < 1 && editText.length() > 10) {
                            NToast.shortToast(mContext, "群昵称应为 2-10 字");
                            return false;
                        }

                        if (AndroidEmoji.isEmoji(editText) && editText.length() < 4) {
                            NToast.shortToast(mContext, "群昵称表情过短");
                            return false;
                        }
                        newGroupDisName = editText;
                        LoadDialog.show(mContext,"loading...",false);
                        request(SET_GROUP_NAME);
                        return true;
                    }
                });
                dialog1.show(getSupportFragmentManager(), null);
                break;
            case R.id.group_code:
                Bundle bundle=new Bundle();
                bundle.putString("groupId",mGroup.getGroupsId());
                bundle.putString("groupPic",mGroup.getPortraitUri());
                bundle.putString("groupName",mGroup.getName());
                toActivity(GroupQRCodeActivity.class,bundle);
                break;
        }
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        switch (buttonView.getId()) {
            case R.id.sw_group_top://设置会话置顶
                if (isChecked) {
                    if (mGroup != null) {
                        OperationRong.setConversationTop(mContext, Conversation.ConversationType.GROUP, mGroup.getGroupsId(), true);
                    }
                } else {
                    if (mGroup != null) {
                        OperationRong.setConversationTop(mContext, Conversation.ConversationType.GROUP, mGroup.getGroupsId(), false);
                    }
                }
                break;
            case R.id.sw_group_notfaction://设置免打扰
                if (isChecked) {
                    if (mGroup != null) {
                        OperationRong.setConverstionNotif(mContext, Conversation.ConversationType.GROUP, mGroup.getGroupsId(), true);
                    }
                } else {
                    if (mGroup != null) {
                        OperationRong.setConverstionNotif(mContext, Conversation.ConversationType.GROUP, mGroup.getGroupsId(), false);
                    }
                }

                break;

            case R.id.sw_group_banned://设置群禁言
                if (isChecked) {
                    if (mGroup != null) {
                        Statu="1";
                        request(BANNED,true);
                    }
                } else {
                    if (mGroup != null) {
                        Statu="0";
                        request(BANNED,true);
                    }
                }
                break;

            case R.id.sw_add_friend://设置是否可以查看好友
                if (isChecked) {
                    if (mGroup != null) {
                        Status="1";
                        request(ADDFRIEND,true);
                    }
                } else {
                    if (mGroup != null) {
                        Status="0";
                        request(ADDFRIEND,true);
                    }
                }

                break;
        }
    }


    private class GridAdapter extends BaseAdapter {

        private List<GroupMember> list;
        Context context;


        public GridAdapter(Context context, List<GroupMember> list) {
            if (list.size() >= 31) {
                this.list = list.subList(0, 30);
            } else {
                this.list = list;
            }

            this.context = context;
        }


        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = LayoutInflater.from(context).inflate(R.layout.social_chatsetting_gridview_item, parent, false);
            }
            CircleImageView iv_avatar = (CircleImageView) convertView.findViewById(R.id.iv_avatar);
            TextView tv_username = (TextView) convertView.findViewById(R.id.tv_username);
            ImageView badge_delete = (ImageView) convertView.findViewById(R.id.badge_delete);

            // 最后一个item，减人按钮
            if (position == getCount() - 1 && isCreated) {
                tv_username.setText("");
                badge_delete.setVisibility(View.GONE);
                iv_avatar.setImageResource(R.drawable.icon_btn_deleteperson);
                iv_avatar.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(GroupDetailActivity.this, SelectFriendsActivity.class);
                        intent.putExtra("isDeleteGroupMember", true);
                        intent.putExtra("GroupId", mGroup.getGroupsId());
                        startActivityForResult(intent, 101);
                    }

                });
            } else if ((isCreated && position == getCount() - 2) || (!isCreated && position == getCount() - 1)) {
                tv_username.setText("");
                badge_delete.setVisibility(View.GONE);
                iv_avatar.setImageResource(R.drawable.jy_drltsz_btn_addperson);

                iv_avatar.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(GroupDetailActivity.this, SelectFriendsActivity.class);
                        intent.putExtra("isAddGroupMember", true);
                        intent.putExtra("GroupId", mGroup.getGroupsId());
                        intent.putExtra("GroupName", mGroup.getName());
                        startActivityForResult(intent, 100);

                    }
                });
            } else { // 普通成员
                final GroupMember bean = list.get(position);
                Logger.d("userName:-->"+bean.getDisplayName());
                if(!bean.getUserId().equals(SharedPreferencesUtil.getString(mContext,"userId",""))){
                Friend friend = SealUserInfoManager.getInstance().getFriendByID(bean.getUserId());
                if (friend != null && !TextUtils.isEmpty(friend.getDisplayName())) {
                    tv_username.setText(friend.getDisplayName());
                } else {
                    tv_username.setText(bean.getName());
                }}else {
                    tv_username.setText(bean.getDisplayName());
                }

                String portraitUri = SealUserInfoManager.getInstance().getPortraitUri(bean);
                ImageLoader.getInstance().displayImage(portraitUri, iv_avatar);
                iv_avatar.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (isCreated) {
                            UserInfo userInfo = new UserInfo(bean.getUserId(), bean.getName(), TextUtils.isEmpty(bean.getPortraitUri().toString()) ? Uri.parse(RongGenerate.generateDefaultAvatar(bean.getName(), bean.getUserId())) : bean.getPortraitUri());
                            Intent intent = new Intent(context, UserDetailActivity.class);
                            Friend friend = CharacterParser.getInstance().generateFriendFromUserInfo(userInfo);
                            intent.putExtra("friend", friend);
                            //intent.putExtra("conversationType", Conversation.ConversationType.GROUP.getValue());
                            intent.putExtra("conversationType", Conversation.ConversationType.GROUP.getName());
                            Logger.d("conversationType:-->"+ Conversation.ConversationType.GROUP.getName());
                            //Groups not Serializable,just need group name
                            intent.putExtra("groupName", mGroup.getName());
                            intent.putExtra("groupId", mGroup.getGroupsId());
                            intent.putExtra("created", "1");
                            intent.putExtra("type", CLICK_CONVERSATION_USER_PORTRAIT);
                            context.startActivity(intent);
                        }else {

                        if(isAddFriend==1){
                            UserInfo userInfo = new UserInfo(bean.getUserId(), bean.getName(), TextUtils.isEmpty(bean.getPortraitUri().toString()) ? Uri.parse(RongGenerate.generateDefaultAvatar(bean.getName(), bean.getUserId())) : bean.getPortraitUri());
                            Intent intent = new Intent(context, UserDetailActivity.class);
                            Friend friend = CharacterParser.getInstance().generateFriendFromUserInfo(userInfo);
                            intent.putExtra("friend", friend);
                            //intent.putExtra("conversationType", Conversation.ConversationType.GROUP.getValue());
                            intent.putExtra("conversationType", Conversation.ConversationType.GROUP.getName());
                            Logger.d("conversationType:-->"+ Conversation.ConversationType.GROUP.getName());
                            //Groups not Serializable,just need group name
                            intent.putExtra("groupName", mGroup.getName());
                            intent.putExtra("created", "0");
                            intent.putExtra("groupId", mGroup.getGroupsId());
                            intent.putExtra("type", CLICK_CONVERSATION_USER_PORTRAIT);
                            context.startActivity(intent);
                        }else {
                            return;
                        }}
                    }
                });

            }

            return convertView;
        }

        @Override
        public int getCount() {
            if (isCreated) {
                return list.size() + 2;
            } else {
                return list.size() + 1;
            }
        }

        @Override
        public Object getItem(int position) {
            return list.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        /**
         * 传入新的数据 刷新UI的方法
         */
        public void updateListView(List<GroupMember> list) {
            this.list = list;
            notifyDataSetChanged();
        }

    }


    // 拿到新增的成员刷新adapter
    @Override
    @SuppressWarnings("unchecked")
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (data != null) {
            List<Friend> newMemberData = (List<Friend>) data.getSerializableExtra("newAddMember");
            List<Friend> deleMember = (List<Friend>) data.getSerializableExtra("deleteMember");
            if (newMemberData != null && newMemberData.size() > 0) {
                for (Friend friend : newMemberData) {
                    GroupMember member = new GroupMember(fromConversationId,
                            friend.getUserId(),
                            friend.getName(),
                            friend.getPortraitUri(),
                            null);
                    mGroupMember.add(1, member);
                }
                SealUserInfoManager.getInstance().getGroupMember(fromConversationId);
                initGroupMemberData();
//                LoadDialog.show(mContext);
//                getGroupMembers();
            } else if (deleMember != null && deleMember.size() > 0) {
                for (Friend friend : deleMember) {
                    for (GroupMember member : mGroupMember) {
                        if (member.getUserId().equals(friend.getUserId())) {
                            mGroupMember.remove(member);
                            break;
                        }
                    }
                }
                SealUserInfoManager.getInstance().getGroupMember(fromConversationId);
                initGroupMemberData();


//                LoadDialog.show(mContext);
//                getGroupMembers();
            }

        }
        switch (requestCode) {
//            case PhotoUtils.INTENT_CROP:
//            case PhotoUtils.INTENT_TAKE:
//            case PhotoUtils.INTENT_SELECT:
//                photoUtils.onActivityResult(GroupDetailActivity.this, requestCode, resultCode, data);
//                break;
            case 1001:
                if(data!=null){
                    String notice=data.getStringExtra("notice");
                    if(!TextUtils.isEmpty(notice)){
                        tvNotice.setVisibility(View.VISIBLE);
                        tvNotice.setText(data.getStringExtra("notice"));
                    }else {
                        tvNotice.setVisibility(View.GONE);
                    }

                }
                break;
        }
        PhotoUtils.getInstance().bindForResult(requestCode, resultCode, data);

    }

    private void setGroupsInfoChangeListener() {
        //有些权限只有群主有,比如修改群名称等,已经更新UI不需要再更新
        BroadcastManager.getInstance(this).addAction(SealAppContext.UPDATE_GROUP_NAME, new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (intent != null) {
                    String result = intent.getStringExtra("result");
                    if (result != null) {
                        try {
                            List<String> nameList = JsonMananger.jsonToBean(result, List.class);
                            if (nameList.size() != 3)
                                return;
                            String groupID = nameList.get(0);
                            if (groupID != null && !groupID.equals(fromConversationId))
                                return;
                            if (mGroup != null && mGroup.getRole().equals("0"))
                                return;
                            String groupName = nameList.get(1);
                            String operationName = nameList.get(2);
                            mGroupName.setText(groupName);
                            newGroupName = groupName;
                            RongIM.getInstance().refreshGroupInfoCache(new Group(fromConversationId, newGroupName, TextUtils.isEmpty(mGroup.getPortraitUri()) ? Uri.parse(RongGenerate.generateDefaultAvatar(newGroupName, mGroup.getGroupsId())) : Uri.parse(mGroup.getPortraitUri())));
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                    }
                }
            }
        });
        BroadcastManager.getInstance(this).addAction(SealAppContext.UPDATE_GROUP_MEMBER, new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (intent != null) {
                    String groupID = intent.getStringExtra("String");
                    if (groupID != null && groupID.equals(fromConversationId))
                        getGroupMembers();
                }
            }
        });
        BroadcastManager.getInstance(this).addAction(SealAppContext.GROUP_DISMISS, new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (intent != null) {
                    String groupID = intent.getStringExtra("String");
                    if (groupID != null && groupID.equals(fromConversationId)) {
                        if (mGroup.getRole().equals("1"))
                            backAsGroupDismiss();
                    }
                }
            }
        });
    }

    private void backAsGroupDismiss() {
        this.setResult(501, new Intent());
        finish();
    }




   


    private void initViews() {
        messageTop = (SwitchButton) findViewById(R.id.sw_group_top);
        messageNotification = (SwitchButton) findViewById(R.id.sw_group_notfaction);
        sw_group_banned = (SwitchButton) findViewById(R.id.sw_group_banned);
        llsw_group_banned = (LinearLayout) findViewById(R.id.llsw_group_banned);
        ll_add_friend = (LinearLayout) findViewById(R.id.ll_add_friend);
        sw_add_friend = (SwitchButton) findViewById(R.id.sw_add_friend);
        messageTop.setOnCheckedChangeListener(this);
        sw_group_banned.setOnCheckedChangeListener(this);
        sw_add_friend.setOnCheckedChangeListener(this);
        messageNotification.setOnCheckedChangeListener(this);
        LinearLayout groupClean = (LinearLayout) findViewById(R.id.group_clean);
        mGridView = (DemoGridView) findViewById(R.id.gridview);
        mTextViewMemberSize = (TextView) findViewById(R.id.group_member_size);
        mGroupHeader = (RoundImageView) findViewById(R.id.group_header);
        LinearLayout mGroupDisplayName = (LinearLayout) findViewById(R.id.group_displayname);
        mGroupDisplayNameText = (TextView) findViewById(R.id.group_displayname_text);
        mGroupName = (TextView) findViewById(R.id.group_name);

        tvNotice=findViewById(R.id.tv_Notice);
        mQuitBtn = (Button) findViewById(R.id.group_quit);
        mDismissBtn = (Button) findViewById(R.id.group_dismiss);
        RelativeLayout totalGroupMember = (RelativeLayout) findViewById(R.id.group_member_size_item);
        RelativeLayout memberOnlineStatus = (RelativeLayout) findViewById(R.id.group_member_online_status);
        mGroupPortL = (LinearLayout) findViewById(R.id.ll_group_port);
       mGroupNameL = (LinearLayout) findViewById(R.id.ll_group_name);
        mGroupAnnouncementDividerLinearLayout = (LinearLayout) findViewById(R.id.ac_ll_group_announcement_divider);
        mGroupNotice = (LinearLayout) findViewById(R.id.group_announcement);
        group_code = (LinearLayout) findViewById(R.id.group_code);

        mSearchMessagesLinearLayout = (LinearLayout) findViewById(R.id.ac_ll_search_chatting_records);
        mGroupPortL.setOnClickListener(this);
        mGroupNameL.setOnClickListener(this);
        group_code.setOnClickListener(this);
        totalGroupMember.setOnClickListener(this);
        mGroupDisplayName.setOnClickListener(this);
        memberOnlineStatus.setOnClickListener(this);
        if (getSharedPreferences("config", Context.MODE_PRIVATE).getBoolean("isDebug", false)) {
            memberOnlineStatus.setVisibility(View.VISIBLE);
        }
        mQuitBtn.setOnClickListener(this);
        mDismissBtn.setOnClickListener(this);
        groupClean.setOnClickListener(this);
        mGroupNotice.setOnClickListener(this);
        mSearchMessagesLinearLayout.setOnClickListener(this);
    }

    @Override
    public void onBackPressed() {
        SealAppContext.getInstance().popActivity(this);
        super.onBackPressed();
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
                    com.bcr.jianxinIM.util.photo.PhotoUtils.getInstance().takePhoto();
                    break;
                case 2:
                    com.bcr.jianxinIM.util.photo.PhotoUtils.getInstance().selectPhoto();
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
                com.bcr.jianxinIM.util.photo.PhotoUtils.getInstance().takePhoto();
                break;
            case 2:
                com.bcr.jianxinIM.util.photo.PhotoUtils.getInstance().selectPhoto();
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


}
