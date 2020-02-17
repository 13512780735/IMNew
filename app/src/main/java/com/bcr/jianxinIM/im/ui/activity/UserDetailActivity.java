package com.bcr.jianxinIM.im.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bcr.jianxinIM.activity.main.CircleOfFriends01Activity;
import com.bcr.jianxinIM.activity.main.CircleOfFriendsActivity;
import com.bcr.jianxinIM.activity.main.UserInfoActivity;
import com.bcr.jianxinIM.adapter.DynamicAdapter;
import com.bcr.jianxinIM.im.server.BaseAction;
import com.bcr.jianxinIM.im.server.response.CheckOnlineRespone;
import com.bcr.jianxinIM.im.server.response.GetUserAddWayResponse;
import com.bcr.jianxinIM.im.server.response.PushMessageRespone;
import com.bcr.jianxinIM.util.HttpUtil;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.loopj.android.http.RequestParams;
import com.orhanobut.logger.Logger;
import com.bcr.jianxinIM.R;
import com.bcr.jianxinIM.im.SealAppContext;
import com.bcr.jianxinIM.im.SealConst;
import com.bcr.jianxinIM.im.SealUserInfoManager;
import com.bcr.jianxinIM.im.db.BlackList;
import com.bcr.jianxinIM.im.db.Friend;
import com.bcr.jianxinIM.im.server.SealAction;
import com.bcr.jianxinIM.im.server.broadcast.BroadcastManager;
import com.bcr.jianxinIM.im.server.network.async.AsyncTaskManager;
import com.bcr.jianxinIM.im.server.network.async.OnDataListener;
import com.bcr.jianxinIM.im.server.network.http.HttpException;
import com.bcr.jianxinIM.im.server.pinyin.CharacterParser;
import com.bcr.jianxinIM.im.server.response.DeleteFriendResponse;
import com.bcr.jianxinIM.im.server.response.FriendInvitationResponse;
import com.bcr.jianxinIM.im.server.response.GetFriendInfoByIDResponse;
import com.bcr.jianxinIM.im.server.response.GetUserInfoByIdResponse;
import com.bcr.jianxinIM.im.server.response.delMemberBannedResponse;
import com.bcr.jianxinIM.im.server.response.getMemberBannedResponse;
import com.bcr.jianxinIM.im.server.response.setMemberBannedResponse;
import com.bcr.jianxinIM.im.server.utils.CommonUtils;
import com.bcr.jianxinIM.im.server.utils.NLog;
import com.bcr.jianxinIM.im.server.utils.NToast;
import com.bcr.jianxinIM.im.server.utils.RongGenerate;
import com.bcr.jianxinIM.im.server.widget.LoadDialog;
import com.bcr.jianxinIM.im.ui.widget.switchbutton.SwitchButton;
import com.bcr.jianxinIM.util.SharedPreferencesUtil;
import com.bcr.jianxinIM.view.CommonDialog;
import com.bcr.jianxinIM.view.SimpleInputDialog;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import cn.ycbjie.ycstatusbarlib.bar.StateAppBar;
import io.rong.callkit.RongCallAction;
import io.rong.callkit.RongVoIPIntent;
import io.rong.calllib.RongCallClient;
import io.rong.calllib.RongCallCommon;
import io.rong.calllib.RongCallSession;
import io.rong.imageloader.core.ImageLoader;
import io.rong.imkit.RongIM;
import io.rong.imlib.IRongCallback;
import io.rong.imlib.RongIMClient;
import io.rong.imlib.model.Conversation;
import io.rong.imlib.model.UserInfo;
import io.rong.imlib.model.UserOnlineStatusInfo;

//CallKit start 1
//CallKit end 1

/**
 * Created by tiankui on 16/11/2.
 */

public class UserDetailActivity extends BaseActivity implements View.OnClickListener {

    private static final int SYNC_FRIEND_INFO = 129;
    private static final int DELETE_FRIEND = 204;
    private static final int GETBAND =210 ;
    private static final int SETBAND =211 ;
    private static final int DELBAND =212 ;
    private static final int CHECKONLINE =240 ;
    private static final int PUSHMESSAGE =241 ;
    private ImageView mUserPortrait;
    private TextView mUserNickName;
    private TextView mUserDisplayName;
    private TextView mUserPhone;
    private TextView mUserLineStatus;
    private LinearLayout mChatButtonGroupLinearLayout;
    private TextView mAddFriendButton;
    private LinearLayout mNoteNameLinearLayout;
    private static final int GETUSETADDWAY = 251;
    private static final int ADD_FRIEND = 10086;
    private static final int SYN_USER_INFO = 10087;
    private Friend mFriend;
    private String addMessage;
    private String mGroupName;
    private String mPhoneString;
    private boolean mIsFriendsRelationship;
    private static final int ADDBLACKLIST = 167;
    private int mType;
    private static final int CLICK_CONVERSATION_USER_PORTRAIT = 1;
    private static final int CLICK_CONTACT_FRAGMENT_FRIEND = 2;
    private UserDetailActivityHandler mHandler = new UserDetailActivityHandler(this);
    private LinearLayout profile_siv_detail_blacklist;
    private LinearLayout profile_siv_detail_delete_contact;
    private AsyncTaskManager asyncTaskManager;

   LinearLayout layout_head;
   ImageView btn_left;
   TextView tv_title;
    private LinearLayout llsw_member_banned;
    private String conversationType;
    private SwitchButton sw_member_banned;
    private String groupId;
    private String isGag;//1是被禁言了0是没有被禁言
    private String addFriendMessage;
    private TextView tvVoice,tvVideo;
    private String staus;
    private LinearLayout ll_dynamic;
    List<String> imgas = new ArrayList<>();
    private RecyclerView mRecyclerView;
    private DynamicAdapter mDynamicAdapter;
    private int group=0;
    private String created;
    private List<UserInfo> blackList;
    private LinearLayout del_siv_detail_blacklist;
    private static final int REMOVEBLACKLIST = 168;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_detail);
        StateAppBar.setStatusBarLightMode(this,Color.WHITE);
        //setHeadVisibility(View.GONE);
        asyncTaskManager = AsyncTaskManager.getInstance(this);
        Logger.d("userId:-->"+ SharedPreferencesUtil.getString(UserDetailActivity.this,"userId",""));
        initView();
        //initBlackList();
        initData();
        initBlackListStatusView();
    }

//    private void initBlackList() {
//        SealUserInfoManager.getInstance().getBlackList(new SealUserInfoManager.ResultCallback<List<UserInfo>>() {
//            @Override
//            public void onSuccess(List<UserInfo> userInfoList) {
//                //LoadDialog.dismiss(mContext);
//                //if (userInfoList != null) {
//                    if (userInfoList.size() > 0) {
//                        blackList=userInfoList;
//                    } else {
//                        blackList=null;
//                    }
//               // }
//
//
//
//
//            }
//
//            @Override
//            public void onError(String errString) {
//                LoadDialog.dismiss(mContext);
//            }
//        });
//    }

    private void initView() {
        //setTitle(R.string.user_details);
        setTitle(R.string.user_details);
        layout_head=findViewById(R.id.layout_head);
        btn_left=findViewById(R.id.btn_left);
        tv_title=findViewById(R.id.tv_title);
        layout_head.setBackgroundColor(Color.WHITE);
        tv_title.setTextColor(Color.BLACK);
        btn_left.setImageDrawable(getResources().getDrawable(R.drawable.back));
        mUserNickName = (TextView) findViewById(R.id.contact_below);
        mUserDisplayName = (TextView) findViewById(R.id.contact_top);
        btn_left=findViewById(R.id.btn_left);
        mUserPhone = (TextView) findViewById(R.id.contact_phone);
        mUserLineStatus = (TextView) findViewById(R.id.user_online_status);
        mUserPortrait = (ImageView) findViewById(R.id.ac_iv_user_portrait);
        mChatButtonGroupLinearLayout = (LinearLayout) findViewById(R.id.ac_ll_chat_button_group);
        mAddFriendButton = (TextView) findViewById(R.id.ac_bt_add_friend);
        mNoteNameLinearLayout = (LinearLayout) findViewById(R.id.ac_ll_note_name);
        profile_siv_detail_blacklist = (LinearLayout) findViewById(R.id.profile_siv_detail_blacklist);//黑名单
        del_siv_detail_blacklist = (LinearLayout) findViewById(R.id.del_siv_detail_blacklist);//移除黑名单
        profile_siv_detail_delete_contact = (LinearLayout) findViewById(R.id.profile_siv_detail_delete_contact);//删除好友
        ll_dynamic = (LinearLayout) findViewById(R.id.ll_dynamic);//朋友圈
        mRecyclerView = (RecyclerView) findViewById(R.id.mRecyclerView);//朋友圈
        mDynamicAdapter = new DynamicAdapter(R.layout.dynamic_layout_items,imgas);
        mRecyclerView.setLayoutManager (new GridLayoutManager (mContext,4, GridLayoutManager.VERTICAL,false));
        mRecyclerView.setAdapter (mDynamicAdapter);
        llsw_member_banned=findViewById(R.id.llsw_member_banned);
        sw_member_banned=findViewById(R.id.sw_member_banned);
        tvVoice=findViewById(R.id.tvVoice);
        tvVideo=findViewById(R.id.tvVideo);
        mAddFriendButton.setOnClickListener(this);
        profile_siv_detail_blacklist.setOnClickListener(this);
        del_siv_detail_blacklist.setOnClickListener(this);
        profile_siv_detail_delete_contact.setOnClickListener(this);
        mUserPhone.setOnClickListener(this);
        sw_member_banned.setOnClickListener(this);
        ll_dynamic.setOnClickListener(this);
        mDynamicAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                Bundle bundle=new Bundle();
                bundle.putString("targetId",mFriend.getUserId());
                toActivity(CircleOfFriendsActivity.class,bundle);
            }
        });
    }

    private void initData() {
        Logger.d("conversationType:-->"+ getIntent().getStringExtra("conversationType"));
        mType = getIntent().getIntExtra("type", 0);
        if (mType == CLICK_CONVERSATION_USER_PORTRAIT) {
            SealAppContext.getInstance().pushActivity(this);
        }
        mGroupName = getIntent().getStringExtra("groupName");
        mFriend = getIntent().getParcelableExtra("friend");
        conversationType=getIntent().getStringExtra("conversationType");
        if(conversationType!=null){
            if("group".equals(conversationType)){
                created=getIntent().getStringExtra("created");
                if(SharedPreferencesUtil.getString(mContext,"userId","").equals(mFriend.getUserId())){
                    llsw_member_banned.setVisibility(View.GONE);
                }else {
                    if(created.equals("1")){

                    llsw_member_banned.setVisibility(View.VISIBLE);}
                    else {
                        llsw_member_banned.setVisibility(View.GONE);
                    }
                    groupId=getIntent().getStringExtra("groupId");
                    request(GETBAND);
                }
            }else {
                llsw_member_banned.setVisibility(View.GONE);
            }
        }
        if (mFriend != null) {
            if (mFriend.isExitsDisplayName()) {
                mUserNickName.setVisibility(View.GONE);
                mUserNickName.setText(getString(R.string.ac_contact_nick_name) + " " + mFriend.getName());
                mUserDisplayName.setText(mFriend.getDisplayName());
            } else {
                mUserNickName.setVisibility(View.GONE);
                mUserDisplayName.setText(mFriend.getName());
            }
            String portraitUri = SealUserInfoManager.getInstance().getPortraitUri(mFriend);
            NLog.d("mFriend:->"+mFriend);
            if(TextUtils.isEmpty(portraitUri)){
                mUserPortrait.setImageDrawable(getResources().getDrawable(R.mipmap.icon_wangwang));
            }else {
            ImageLoader.getInstance().displayImage(portraitUri, mUserPortrait);}
        }
        if (getSharedPreferences("config", MODE_PRIVATE).getBoolean("isDebug", false)) {
            RongIMClient.getInstance().getUserOnlineStatus(mFriend.getUserId(), new IRongCallback.IGetUserOnlineStatusCallback() {
                @Override
                public void onSuccess(final ArrayList<UserOnlineStatusInfo> userOnlineStatusInfoList) {
                    if (userOnlineStatusInfoList != null && userOnlineStatusInfoList.size() > 0) {
                        UserOnlineStatusInfo userOnlineStatusInfo = null;
                        for (int i = 0; i < userOnlineStatusInfoList.size(); ++i) {
                            if (i == 0) {
                                userOnlineStatusInfo = userOnlineStatusInfoList.get(i);
                            } else {
                                if (userOnlineStatusInfoList.get(i).getPlatform().getValue() > userOnlineStatusInfo.getPlatform().getValue()) {
                                    userOnlineStatusInfo = userOnlineStatusInfoList.get(i);
                                }
                            }
                        }
                        Message message = mHandler.obtainMessage();
                        message.obj = userOnlineStatusInfo;
                        mHandler.sendMessage(message);
                    } else {
                        Message message = mHandler.obtainMessage();
                        message.obj = null;
                        mHandler.sendMessage(message);
                    }

                }

                @Override
                public void onError(int errorCode) {

                }
            });
        }
        syncPersonalInfo();
        initType(mFriend.getUserId());
        if (!TextUtils.isEmpty(mFriend.getUserId())) {
            String mySelf = SharedPreferencesUtil.getString(mContext,"userId","");
            if (mySelf.equals(mFriend.getUserId())) {
                mChatButtonGroupLinearLayout.setVisibility(View.VISIBLE);
                mAddFriendButton.setVisibility(View.GONE);
                mNoteNameLinearLayout.setVisibility(View.GONE);
                profile_siv_detail_blacklist.setVisibility(View.GONE);
                profile_siv_detail_delete_contact.setVisibility(View.GONE);
                tvVideo.setVisibility(View.GONE);
                tvVoice.setVisibility(View.GONE);
               // return;
            }else {
            if (mIsFriendsRelationship) {
                mChatButtonGroupLinearLayout.setVisibility(View.VISIBLE);
                mAddFriendButton.setVisibility(View.GONE);
            } else {
                mAddFriendButton.setVisibility(View.VISIBLE);
                mChatButtonGroupLinearLayout.setVisibility(View.GONE);
                mNoteNameLinearLayout.setVisibility(View.GONE);
                profile_siv_detail_blacklist.setVisibility(View.GONE);
                profile_siv_detail_delete_contact.setVisibility(View.GONE);

            }}
            checkOnLine(mFriend.getUserId());
            getSinglyDynamic(mFriend.getUserId());
        }
//        if(blackList.size()>0){
//            for(int i=0;i<blackList.size();i++){
//                if(mFriend.getUserId().equals(blackList.get(i).getUserId())){
//                    profile_siv_detail_blacklist.setVisibility(View.GONE);
//                    del_siv_detail_blacklist.setVisibility(View.VISIBLE);
//                }else {
//                    del_siv_detail_blacklist.setVisibility(View.VISIBLE);
//                    profile_siv_detail_blacklist.setVisibility(View.VISIBLE);
//                }
//            }
//        }

    }

    private void initType(String userId) {
        AsyncTaskManager.getInstance(mContext).request(GETUSETADDWAY, new OnDataListener() {
            @Override
            public Object doInBackground(int requestCode, String parameter) throws HttpException {
                return new SealAction(mContext).getUserAddWay(userId);
            }

            @Override
            public void onSuccess(int requestCode, Object result) {
                if (result != null) {
                    GetUserAddWayResponse getUserAddWayResponse= (GetUserAddWayResponse) result;
                    if(getUserAddWayResponse.isSuccess()==true){
                        group=getUserAddWayResponse.getResultData().getGroup();
                        // NToast.shortToast(mContext,getUserAddWayResponse.getMessage());
                    }else {
                        NToast.shortToast(mContext,getUserAddWayResponse.getMessage());
                    }
                }
            }

            @Override
            public void onFailure(int requestCode, int state, Object result) {
                // LoadDialog.dismiss(getActivity());
            }
        });


    }

    /**
     * 获取个人朋友圈
     * @param userId
     */
    private void getSinglyDynamic(String userId) {
        Logger.d("用户ID:-->"+userId);
        RequestParams params=new RequestParams();
        params.put("userId", userId);
        HttpUtil.post(BaseAction.DOMAIN+"/Dynamic.ashx?Method=GetDynamicPic", params, new HttpUtil.RequestListener() {
            @Override
            public void success(String response) {
                try {
                    JSONObject object=new JSONObject(response);
                    if(object.optBoolean("Success")==true){
                        JSONArray array=object.optJSONArray("ResultData");
                        List<String> images=new ArrayList<>();
                        if(array.length()>0){
                            for (int i = 0; i < array.length(); i++) {
                                JSONObject object1 = array.optJSONObject(i);

                                images.add(object1.optString("UserPic"));
                            }
                            imgas.addAll(images);
                            mDynamicAdapter.setNewData(imgas);
                            mDynamicAdapter.notifyDataSetChanged();
                        }
                    }


                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void failed(Throwable e) {

            }
        });


    }

    private void checkOnLine(String userId) {
        AsyncTaskManager.getInstance(mContext).request(CHECKONLINE, new OnDataListener() {
            @Override
            public Object doInBackground(int requestCode, String parameter) throws HttpException {
                return new SealAction(mContext).checkOnline(userId);
            }

            @Override
            public void onSuccess(int requestCode, Object result) {
                if (result != null) {
                    CheckOnlineRespone checkOnlineRespone= (CheckOnlineRespone) result;
                    if(checkOnlineRespone.isSuccess()==true){
                        staus=checkOnlineRespone.getResultData();  //1在线  0 不在线
                    }
                }
            }

            @Override
            public void onFailure(int requestCode, int state, Object result) {

            }
        });
    }


    private void syncPersonalInfo() {
        mIsFriendsRelationship = SealUserInfoManager.getInstance().isFriendsRelationship(mFriend.getUserId());
        if (mIsFriendsRelationship) {
            String userId = mFriend.getUserId();
            mFriend = SealUserInfoManager.getInstance().getFriendByID(userId);
            request(SYNC_FRIEND_INFO, true);
        } else {
            request(SYN_USER_INFO, true);
        }
    }

    private void initBlackListStatusView() {
//        if (mIsFriendsRelationship) {
//            Button rightButton = getHeadRightButton();
//            rightButton.setBackgroundDrawable(getResources().getDrawable(R.drawable.main_activity_contact_more));
//            rightButton.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(final View v) {
//                    RongIM.getInstance().getBlacklistStatus(mFriend.getUserId(), new RongIMClient.ResultCallback<RongIMClient.BlacklistStatus>() {
//                        @Override
//                        public void onSuccess(RongIMClient.BlacklistStatus blacklistStatus) {
//                            SinglePopWindow morePopWindow = new SinglePopWindow(UserDetailActivity.this, mFriend, blacklistStatus);
//                            morePopWindow.showPopupWindow(v);
//                        }
//
//                        @Override
//                        public void onError(RongIMClient.ErrorCode e) {
//
//                        }
//                    });
//                }
//            });
//        }
    }

    public void startChat(View view) {
        String displayName = mFriend.getDisplayName();
        if (!TextUtils.isEmpty(displayName)) {
            RongIM.getInstance().startPrivateChat(mContext, mFriend.getUserId(), displayName);
        } else {
            RongIM.getInstance().startPrivateChat(mContext, mFriend.getUserId(), mFriend.getName());
        }
        finish();

    }

    //CallKit start 2
    public void startVoice(View view) {
        if("0".equals(staus)){
            pushMessage(mFriend.getUserId());
        }
        RongCallSession profile = RongCallClient.getInstance().getCallSession();
        if (profile != null && profile.getActiveTime() > 0) {
            Toast.makeText(mContext,
                    profile.getMediaType() == RongCallCommon.CallMediaType.AUDIO ?
                            getString(io.rong.callkit.R.string.rc_voip_call_audio_start_fail) :
                            getString(io.rong.callkit.R.string.rc_voip_call_video_start_fail),
                    Toast.LENGTH_SHORT)
                    .show();
            return;
        }
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = cm.getActiveNetworkInfo();
        if (networkInfo == null || !networkInfo.isConnected() || !networkInfo.isAvailable()) {
            Toast.makeText(mContext, getString(io.rong.callkit.R.string.rc_voip_call_network_error), Toast.LENGTH_SHORT).show();
            return;
        }

        Intent intent = new Intent(RongVoIPIntent.RONG_INTENT_ACTION_VOIP_SINGLEAUDIO);
        intent.putExtra("conversationType", Conversation.ConversationType.PRIVATE.getName().toLowerCase(Locale.US));
        intent.putExtra("targetId", mFriend.getUserId());
        intent.putExtra("callAction", RongCallAction.ACTION_OUTGOING_CALL.getName());
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setPackage(getPackageName());
        getApplicationContext().startActivity(intent);
    }

    public void startVideo(View view) {
        if("0".equals(staus)){
            pushMessage(mFriend.getUserId());
        }
        RongCallSession profile = RongCallClient.getInstance().getCallSession();
        if (profile != null && profile.getActiveTime() > 0) {
            Toast.makeText(mContext,
                    profile.getMediaType() == RongCallCommon.CallMediaType.AUDIO ?
                            getString(io.rong.callkit.R.string.rc_voip_call_audio_start_fail) :
                            getString(io.rong.callkit.R.string.rc_voip_call_video_start_fail),
                    Toast.LENGTH_SHORT)
                    .show();
            return;
        }
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = cm.getActiveNetworkInfo();
        if (networkInfo == null || !networkInfo.isConnected() || !networkInfo.isAvailable()) {
            Toast.makeText(mContext, getString(io.rong.callkit.R.string.rc_voip_call_network_error), Toast.LENGTH_SHORT).show();
            return;
        }
        Intent intent = new Intent(RongVoIPIntent.RONG_INTENT_ACTION_VOIP_SINGLEVIDEO);
        intent.putExtra("conversationType", Conversation.ConversationType.PRIVATE.getName().toLowerCase(Locale.US));
        intent.putExtra("targetId", mFriend.getUserId());
        intent.putExtra("callAction", RongCallAction.ACTION_OUTGOING_CALL.getName());
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setPackage(getPackageName());
        getApplicationContext().startActivity(intent);
    }
    //CallKit end 2


    /**
     * 调用后台推送
     * @param mTargetId
     */
    private void pushMessage(String mTargetId) {
        Logger.d("推送消息111：--》"+mTargetId);
        AsyncTaskManager.getInstance(mContext).request(PUSHMESSAGE, new OnDataListener() {


            @Override
            public Object doInBackground(int requestCode, String parameter) throws HttpException {
                return new SealAction(mContext).pushMessage(mTargetId);
            }

            @Override
            public void onSuccess(int requestCode, Object result) {
                PushMessageRespone pushMessageRespone= (PushMessageRespone) result;
                Logger.d("推送消息：--》"+pushMessageRespone.getMessage());
            }

            @Override
            public void onFailure(int requestCode, int state, Object result) {

            }
        });
    }


    public void finishPage(View view) {
        this.finish();
    }

    public void setDisplayName(View view) {
        Intent intent = new Intent(mContext, NoteInformationActivity.class);
        intent.putExtra("friend", mFriend);
        startActivityForResult(intent, 99);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ac_bt_add_friend:
                if(group==0){
                        NToast.longToast(mContext,"该用户禁止通过群聊添加好友");
                }else {
                    SimpleInputDialog dialog3 = new SimpleInputDialog();
                    dialog3.setInputHint("加好友信息..");
                    dialog3.setInputDialogListener(new SimpleInputDialog.InputDialogListener() {
                        @Override
                        public boolean onConfirmClicked(EditText input) {
                            if (!CommonUtils.isNetworkConnected(mContext)) {
                                NToast.shortToast(mContext, R.string.network_not_available);
                                return false;
                            }
                            addFriendMessage = input.getText().toString();
                            if (TextUtils.isEmpty(addFriendMessage)) {
                                addFriendMessage = "我是" + getSharedPreferences("config", MODE_PRIVATE).getString(SealConst.SEALTALK_LOGIN_NAME, "");
                            }
                            LoadDialog.show(mContext,"Loading...",false);
                            request(ADD_FRIEND);

                            return true;
                        }
                    });
                    dialog3.show(getSupportFragmentManager(), null);
                }




                break;
            case R.id.contact_phone:
//                if (!TextUtils.isEmpty(mPhoneString)) {
//                    Uri telUri = Uri.parse("tel:"+mPhoneString);
//                    Intent intent = new Intent(Intent.ACTION_DIAL, telUri);
//                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                    startActivity(intent);
//                }
                break;
            case R.id.profile_siv_detail_blacklist:
                CommonDialog commonDialog = new CommonDialog.Builder()
                        .setContentMessage("加入黑名单后，你将不再收到对方的消息，同时删除与该联系人的聊天记录")
                        .setDialogButtonClickListener(new CommonDialog.OnDialogButtonClickListener() {
                            @Override
                            public void onPositiveClick(View v, Bundle bundle) {
                                RongIM.getInstance().addToBlacklist(mFriend.getUserId(), new RongIMClient.OperationCallback() {
                                    @Override
                                    public void onSuccess() {

                                        asyncTaskManager.request(ADDBLACKLIST, new OnDataListener() {
                                            @Override
                                            public Object doInBackground(int requestCode, String parameter) throws HttpException {
                                                return new SealAction(UserDetailActivity.this).addToBlackList(SharedPreferencesUtil.getString(UserDetailActivity.this,"userId",""),mFriend.getUserId());
                                            }

                                            @Override
                                            public void onSuccess(int requestCode, Object result) {
                                                SealUserInfoManager.getInstance().addBlackList(new BlackList(
                                                        mFriend.getUserId(),
                                                        null,
                                                        null
                                                ));
                                                NToast.shortToast(UserDetailActivity.this, "加入成功");
                                                if (RongIM.getInstance() != null && mFriend != null) {
                                                    RongIM.getInstance().clearMessages(Conversation.ConversationType.PRIVATE,
                                                            mFriend.getUserId(), new RongIMClient.ResultCallback<Boolean>() {
                                                                @Override
                                                                public void onSuccess(Boolean aBoolean) {
                                                                    NToast.shortToast
                                                                            (mContext, getString(R.string.clear_success));
                                                                }

                                                                @Override
                                                                public void onError(RongIMClient.ErrorCode errorCode) {
                                                                    NToast.shortToast(mContext, getString(R.string.clear_failure));
                                                                }
                                                            });
                                                    RongIMClient.getInstance().cleanRemoteHistoryMessages(
                                                            Conversation.ConversationType.PRIVATE,
                                                            mFriend.getUserId(), System.currentTimeMillis(),
                                                            null);
                                                }


                                                finish();
                                            }

                                            @Override
                                            public void onFailure(int requestCode, int state, Object result) {

                                            }
                                        });
                                    }

                                    @Override
                                    public void onError(RongIMClient.ErrorCode errorCode) {
                                        NToast.shortToast(UserDetailActivity.this, "加入失败");
                                    }
                                });
                            }

                            @Override
                            public void onNegativeClick(View v, Bundle bundle) {
                            }
                        })
                        .build();
                commonDialog.show(getSupportFragmentManager(), null);

                break;
            case R.id.profile_siv_detail_delete_contact:
                CommonDialog dialog = new CommonDialog.Builder()
                        .setContentMessage("确认删除该联系人同时删除与该联系人的聊天记录？")
                        .setDialogButtonClickListener(new CommonDialog.OnDialogButtonClickListener() {
                            @Override
                            public void onPositiveClick(View v, Bundle bundle) {
                                // 标记正在删除好友
                                //isInDeleteAction = true;
                               // LoadDialog.show(mContext,"Loading...",false);
                              //  request(DELETE_FRIEND, true);
                                RongIM.getInstance().addToBlacklist(mFriend.getUserId(), new RongIMClient.OperationCallback() {
                                    @Override
                                    public void onSuccess() {
                                        asyncTaskManager.request(DELETE_FRIEND, new OnDataListener() {
                                            @Override
                                            public Object doInBackground(int requestCode, String parameter) throws HttpException {
                                                return new SealAction(UserDetailActivity.this).deleteFriend(SharedPreferencesUtil.getString(UserDetailActivity.this,"userId",""),mFriend.getUserId());
                                            }

                                            @Override
                                            public void onSuccess(int requestCode, Object result) {
                                                DeleteFriendResponse deleteFriendResponse= (DeleteFriendResponse) result;
                                                if(deleteFriendResponse.isSuccess()==true){

                                                    SealUserInfoManager.getInstance().delete(
                                                            new Friend(mFriend.getUserId(),
                                                                    mFriend.getDisplayName(),
                                                                    Uri.parse(""),
                                                                    mFriend.getDisplayName(),
                                                                    null, null, null, null,
                                                                    null,
                                                                    TextUtils.isEmpty(mFriend.getDisplayName()) ?
                                                                            null : CharacterParser.getInstance().getSpelling(mFriend.getDisplayName())));
                                                    //更新好友列表
                                                    RongIM.getInstance().refreshUserInfoCache(new UserInfo(mFriend.getUserId(),mFriend.getDisplayName(),mFriend.getPortraitUri()));
                                                    BroadcastManager.getInstance(mContext).sendBroadcast(SealAppContext.UPDATE_FRIEND);

                                                    /**
                                                     * 清除消息记录
                                                     */
                                                    RongIM.getInstance().removeConversation(Conversation.ConversationType.PRIVATE,
                                                            mFriend.getUserId(), new RongIMClient.ResultCallback<Boolean>() {
                                                                @Override
                                                                public void onSuccess(Boolean aBoolean) {

                                                                    Logger.d("融云移除:->","onSuccess"+"123");
                                                                }

                                                                @Override
                                                                public void onError(RongIMClient.ErrorCode errorCode) {
                                                                    Logger.d("融云移除:->","errorCode "+errorCode+"123");

                                                                }
                                                            });
                                                    NToast.shortToast(mContext,deleteFriendResponse.getMessage());
                                                    if (RongIM.getInstance() != null && mFriend != null) {
                                                        RongIM.getInstance().clearMessages(Conversation.ConversationType.PRIVATE,
                                                                mFriend.getUserId(), new RongIMClient.ResultCallback<Boolean>() {
                                                                    @Override
                                                                    public void onSuccess(Boolean aBoolean) {
                                                                        NToast.shortToast
                                                                                (mContext, getString(R.string.clear_success));
                                                                    }

                                                                    @Override
                                                                    public void onError(RongIMClient.ErrorCode errorCode) {
                                                                        NToast.shortToast(mContext, getString(R.string.clear_failure));
                                                                    }
                                                                });
                                                        RongIMClient.getInstance().cleanRemoteHistoryMessages(
                                                                Conversation.ConversationType.PRIVATE,
                                                                mFriend.getUserId(), System.currentTimeMillis(),
                                                                null);
                                                    }
                                                    finish();
                                                }
                                            }

                                            @Override
                                            public void onFailure(int requestCode, int state, Object result) {
                                                DeleteFriendResponse deleteFriendResponse= (DeleteFriendResponse) result;
                                                LoadDialog.dismiss(mContext);
                                                NToast.shortToast(mContext, deleteFriendResponse.getMessage());

                                            }
                                        });
                                    }

                                    @Override
                                    public void onError(RongIMClient.ErrorCode errorCode) {
                                        NToast.shortToast(UserDetailActivity.this, "加入失败");
                                    }
                                });
                            }



                            @Override
                            public void onNegativeClick(View v, Bundle bundle) {
                            }
                        })
                        .build();
                dialog.show(getSupportFragmentManager(), null);
                break;
            case R.id.sw_member_banned:
                if("1".equals(isGag)){
                    request(DELBAND);
                }else {
                    request(SETBAND);
                }
                break;

            case R.id.ll_dynamic:
                Bundle bundle=new Bundle();
               bundle.putString("targetId",mFriend.getUserId());

                Logger.d("targetId111:-->"+mFriend.getUserId());

                toActivity(CircleOfFriendsActivity.class,bundle);
                break;

            case R.id.del_siv_detail_blacklist:
                CommonDialog commonDialog1 = new CommonDialog.Builder()
                        .setContentMessage("是否将好友移除黑名单？")
                        .setDialogButtonClickListener(new CommonDialog.OnDialogButtonClickListener() {
                            @Override
                            public void onPositiveClick(View v, Bundle bundle) {
                                RongIM.getInstance().removeFromBlacklist(mFriend.getUserId(), new RongIMClient.OperationCallback() {
                                    @Override
                                    public void onSuccess() {
                                        asyncTaskManager.request(REMOVEBLACKLIST, new OnDataListener() {
                                            @Override
                                            public Object doInBackground(int requestCode, String parameter) throws HttpException {
                                                return new SealAction(UserDetailActivity.this).removeFromBlackList(SharedPreferencesUtil.getString(UserDetailActivity.this,"userId",""),mFriend.getUserId());
                                            }

                                            @Override
                                            public void onSuccess(int requestCode, Object result) {
                                                SealUserInfoManager.getInstance().deleteBlackList(mFriend.getUserId());
                                                NToast.shortToast(UserDetailActivity.this, "移除成功");
                                                finish();
                                            }

                                            @Override
                                            public void onFailure(int requestCode, int state, Object result) {

                                            }
                                        });
                                    }

                                    @Override
                                    public void onError(RongIMClient.ErrorCode errorCode) {
                                        NToast.shortToast(UserDetailActivity.this, "移除失败");
                                    }
                                });
                            }

                            @Override
                            public void onNegativeClick(View v, Bundle bundle) {
                            }
                        })
                        .build();
                commonDialog1.show(getSupportFragmentManager(), null);


                break;
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == 155 && data != null) {
            String displayName = data.getStringExtra("displayName");
            if (!TextUtils.isEmpty(displayName)) {
                mUserNickName.setVisibility(View.GONE);
                mUserNickName.setText(getString(R.string.ac_contact_nick_name) + " " + mFriend.getName());
                mUserDisplayName.setText(displayName);
                mFriend.setDisplayName(displayName);
            } else {
                mUserNickName.setVisibility(View.GONE);
                mUserDisplayName.setText(mFriend.getName());
                mUserDisplayName.setVisibility(View.VISIBLE);
                mFriend.setDisplayName("");
            }
        }
    }

    @Override
    public Object doInBackground(int requestCode, String id) throws HttpException {
        switch (requestCode) {
            case ADD_FRIEND:
                return action.sendFriendInvitation(SharedPreferencesUtil.getString(UserDetailActivity.this,"userId",""),mFriend.getUserId(), addFriendMessage);
            case SYN_USER_INFO:
                return action.getUserInfoById(mFriend.getUserId());
            case SYNC_FRIEND_INFO:
                return action.getFriendInfoByID(SharedPreferencesUtil.getString(UserDetailActivity.this,"userId",""),mFriend.getUserId());
//            case DELETE_FRIEND:
//                return action.deleteFriend(SharedPreferencesUtil.getString(UserDetailActivity.this,"userId",""),mFriend.getUserId());
            case GETBAND:
                return  action.getMemberBanned(groupId,mFriend.getUserId());
            case DELBAND:
                return  action.delMemberBanned(groupId,mFriend.getUserId());
            case SETBAND:
              return action.setMemberBanned(groupId,mFriend.getUserId());
        }
        return super.doInBackground(requestCode, id);
    }

    @Override
    public void onSuccess(int requestCode, Object result) {
        if (result != null) {
            switch (requestCode) {
                case ADD_FRIEND:
                    FriendInvitationResponse response = (FriendInvitationResponse) result;
                    if (response.isSuccess() == true) {
                        LoadDialog.dismiss(mContext);
                        NToast.shortToast(mContext, getString(R.string.request_success));
                        this.finish();
                    }else {
                        LoadDialog.dismiss(mContext);
                        NToast.shortToast(mContext, response.getMessage());
                    }
                    break;
                case SYN_USER_INFO:
                    //TODO:群组里的好友备注功能，还没有实现；
                    GetUserInfoByIdResponse userInfoByIdResponse = (GetUserInfoByIdResponse) result;
                    if (userInfoByIdResponse.isSuccess() == true&& userInfoByIdResponse.getResultData() != null &&
                            mFriend.getUserId().equals(userInfoByIdResponse.getResultData().getUserId())) {
                        String nickName = userInfoByIdResponse.getResultData().getUserNickName();
                        String portraitUri = userInfoByIdResponse.getResultData().getUserPic();
                        String phone=userInfoByIdResponse.getResultData().getTel();
                        mUserPhone.setVisibility(View.VISIBLE);
                        mUserPhone.setText("ID:" +userInfoByIdResponse.getResultData().getUserId());
                        if (hasNickNameChanged(nickName) || hasPortraitUriChanged(portraitUri)) {
                            if (hasNickNameChanged(nickName)) {
                                mUserDisplayName.setVisibility(View.GONE);
                                mUserNickName.setText(nickName);
                            }
                            if (hasPortraitUriChanged(portraitUri)) {
                                ImageLoader.getInstance().displayImage(portraitUri, mUserPortrait);
                            } else {
                                portraitUri = mFriend.getPortraitUri().toString();
                            }

                            UserInfo userInfo = new UserInfo(userInfoByIdResponse.getResultData().getUserId(), nickName, Uri.parse(portraitUri));
                            RongIM.getInstance().refreshUserInfoCache(userInfo);
                        }
                    }
                    break;
                case SYNC_FRIEND_INFO:
                    GetFriendInfoByIDResponse friendInfoByIDResponse = (GetFriendInfoByIDResponse) result;
                    if (friendInfoByIDResponse.isSuccess() == true) {
                        mUserPhone.setVisibility(View.VISIBLE);
                        mPhoneString = friendInfoByIDResponse.getResultData().getTel();
                        mUserPhone.setText("ID:" + friendInfoByIDResponse.getResultData().getFriendId());
                        GetFriendInfoByIDResponse.ResultDataBean resultEntity = friendInfoByIDResponse.getResultData();
                        if (mFriend.getUserId().equals(resultEntity.getFriendId())) {
                            if (hasFriendInfoChanged(resultEntity)) {
                                String nickName = resultEntity.getUserNickName();
                                String portraitUri = resultEntity.getUserPic();
                                //当前app server返回的displayName为空,先不使用
                                String displayName = resultEntity.getRemark();
                                //如果没有设置头像,好友数据库的头像地址和用户信息提供者的头像处理不一致,这个不一致是seal app代码处理的问题,未来应该矫正回来
                                String userInfoPortraitUri = mFriend.getPortraitUri().toString();
                                //更新UI
                                //if (TextUtils.isEmpty(displayName) && hasDisplayNameChanged(displayName)) {
                                if (!TextUtils.isEmpty(mFriend.getDisplayName())) {
                                    mUserNickName.setVisibility(View.GONE);
                                    mUserNickName.setText(getString(R.string.ac_contact_nick_name) + " " + nickName);
                                    mUserDisplayName.setText(mFriend.getDisplayName());
                                } else if (hasNickNameChanged(nickName)) {
                                    mUserNickName.setVisibility(View.GONE);
                                    if (mFriend.isExitsDisplayName()) {
                                        mUserNickName.setText(getString(R.string.ac_contact_nick_name) + " " + nickName);
                                    } else {
                                        mUserDisplayName.setText(nickName);
                                    }
                                }
                                if (hasPortraitUriChanged(portraitUri)) {
                                    ImageLoader.getInstance().displayImage(portraitUri, mUserPortrait);
                                    userInfoPortraitUri = portraitUri;
                                }
                                //更新好友数据库
                                SealUserInfoManager.getInstance().addFriend(
                                        new Friend(mFriend.getUserId(),
                                                nickName,
                                                Uri.parse(portraitUri),
                                                mFriend.getDisplayName(),
                                                null, null, null, null,
                                                CharacterParser.getInstance().getSpelling(nickName),
                                                TextUtils.isEmpty(mFriend.getDisplayName()) ?
                                                        null : CharacterParser.getInstance().getSpelling(mFriend.getDisplayName())));
                                //更新好友列表
                                BroadcastManager.getInstance(mContext).sendBroadcast(SealAppContext.UPDATE_FRIEND);
                                //更新用户信息提供者
                                if ((!mFriend.isExitsDisplayName() && hasNickNameChanged(nickName)) ||
                                        hasPortraitUriChanged(portraitUri)) {
                                    //如果备注存在,UserInfo设置备注
                                    if (mFriend.isExitsDisplayName())
                                        nickName = mFriend.getDisplayName();
                                    if (TextUtils.isEmpty(userInfoPortraitUri)) {
                                        userInfoPortraitUri = RongGenerate.generateDefaultAvatar(nickName, mFriend.getUserId());
                                    }
                                    UserInfo newUserInfo = new UserInfo(mFriend.getUserId(),
                                            nickName,
                                            Uri.parse(userInfoPortraitUri));
                                    RongIM.getInstance().refreshUserInfoCache(newUserInfo);
                                }
                            }
                        }

                    }
                    break;
                case GETBAND://获取是否禁言
                    getMemberBannedResponse memberBannedResponse= (getMemberBannedResponse) result;
                    if(memberBannedResponse.isSuccess()==true){
                       isGag=memberBannedResponse.getResultData().getIsGag();
                        if("1".equals(isGag)){

                            sw_member_banned.setChecked(true);
                        }else sw_member_banned.setChecked(false);

                    }
                    break;
                case DELBAND:
                    delMemberBannedResponse memberBannedResponse2= (delMemberBannedResponse) result;
                    if(memberBannedResponse2.isSuccess()==true){
                        isGag="0";
                        NToast.shortToast(mContext,memberBannedResponse2.getMessage());
                    }else {
                        NToast.shortToast(mContext,memberBannedResponse2.getMessage());
                    }
                    break;
                case SETBAND:
                    setMemberBannedResponse memberBannedResponse1= (setMemberBannedResponse) result;
                    if(memberBannedResponse1.isSuccess()==true){
                        isGag="1";
                        NToast.shortToast(mContext,memberBannedResponse1.getMessage());
                    }else {
                        NToast.shortToast(mContext,memberBannedResponse1.getMessage());
                    }
                    break;
            }
        }
    }

    private boolean hasNickNameChanged(String nickName) {
        if (mFriend.getName() == null) {
            return nickName != null;
        } else {
            return !mFriend.getName().equals(nickName);
        }
    }

    private boolean hasPortraitUriChanged(String portraitUri) {
        if (mFriend.getPortraitUri() == null) {
            return portraitUri != null;
        } else {
            if (mFriend.getPortraitUri().equals(portraitUri)) {
                return false;
            } else {
                return !TextUtils.isEmpty(portraitUri);
            }
        }
    }

    private boolean hasDisplayNameChanged(String displayName) {
        if (mFriend.getDisplayName() == null) {
            return displayName != null;
        } else {
            return !mFriend.getDisplayName().equals(displayName);
        }
    }

    private boolean hasFriendInfoChanged(GetFriendInfoByIDResponse.ResultDataBean resultEntity) {
       // GetFriendInfoByIDResponse.ResultDataBean resultDataBean = resultEntity.getUser();
        String nickName = resultEntity.getUserNickName();
        String portraitUri = resultEntity.getUserPic();
        String displayName = resultEntity.getRemark();
        return hasNickNameChanged(nickName) ||
                hasPortraitUriChanged(portraitUri) ||
                hasDisplayNameChanged(displayName);
    }

    @Override
    public void onFailure(int requestCode, int state, Object result) {
        switch (requestCode) {
            case ADD_FRIEND://添加好友时报网络异常,其余操作不需要
                break;
        }
        super.onFailure(requestCode, state, result);
    }

    @Override
    public void onBackPressed() {
        if (mType == CLICK_CONVERSATION_USER_PORTRAIT) {
            SealAppContext.getInstance().popActivity(this);
        }
        super.onBackPressed();
    }

    @Override
    public void onHeadLeftButtonClick(View v) {
        if (mType == CLICK_CONVERSATION_USER_PORTRAIT) {
            SealAppContext.getInstance().popActivity(this);
        }
        super.onHeadLeftButtonClick(v);
    }

    private static class UserDetailActivityHandler extends Handler {
        private final WeakReference<UserDetailActivity> mActivity;

        public UserDetailActivityHandler(UserDetailActivity activity) {
            mActivity = new WeakReference<UserDetailActivity>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            if (msg != null) {
                UserDetailActivity activity = mActivity.get();
                if (activity != null) {
                    activity.mUserLineStatus.setVisibility(View.VISIBLE);
                    UserOnlineStatusInfo userOnlineStatusInfo = (UserOnlineStatusInfo) msg.obj;
                    if (userOnlineStatusInfo.getCustomerStatus() > 1) {
                        if (userOnlineStatusInfo.getCustomerStatus() == 5) {
                            activity.mUserLineStatus.setText(activity.getString(R.string.ipad_online));
                            activity.mUserLineStatus.setTextColor(Color.parseColor("#60E23F"));
                        } else if (userOnlineStatusInfo.getCustomerStatus() == 6) {
                            activity.mUserLineStatus.setText(activity.getString(R.string.imac_online));
                            activity.mUserLineStatus.setTextColor(Color.parseColor("#60E23F"));
                        }
                    } else if (userOnlineStatusInfo.getServiceStatus() == 0) {
                        activity.mUserLineStatus.setTextColor(Color.parseColor("#666666"));
                        activity.mUserLineStatus.setText(R.string.offline);
                    } else if (userOnlineStatusInfo != null){
                        switch (userOnlineStatusInfo.getPlatform()) {
                            case Platform_PC:
                                activity.mUserLineStatus.setText(R.string.pc_online);
                                activity.mUserLineStatus.setTextColor(Color.parseColor("#60E23F"));
                                break; //PC
                            case Platform_Android:
                            case Platform_iOS:
                                activity.mUserLineStatus.setText(R.string.phone_online);
                                activity.mUserLineStatus.setTextColor(Color.parseColor("#60E23F"));
                                break; //phone
                            case Platform_Web:
                                activity.mUserLineStatus.setText(R.string.pc_online);
                                activity.mUserLineStatus.setTextColor(Color.parseColor("#60E23F"));
                                break; //web
                            case Platform_Other:
                            default:
                                activity.mUserLineStatus.setTextColor(Color.parseColor("#666666"));
                                activity.mUserLineStatus.setText(R.string.offline);
                                break; // offline
                        }
                    } else {
                        activity.mUserLineStatus.setTextColor(Color.parseColor("#666666"));
                        activity.mUserLineStatus.setText(R.string.offline);
                    }
                }
            }
        }
    }

    @Override
    protected void onDestroy() {
        mHandler.removeCallbacksAndMessages(null);
        super.onDestroy();
    }
}
