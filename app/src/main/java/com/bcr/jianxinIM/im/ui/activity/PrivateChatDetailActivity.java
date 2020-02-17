package com.bcr.jianxinIM.im.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.orhanobut.logger.Logger;
import com.bcr.jianxinIM.R;
import com.bcr.jianxinIM.im.SealAppContext;
import com.bcr.jianxinIM.im.SealConst;
import com.bcr.jianxinIM.im.SealUserInfoManager;
import com.bcr.jianxinIM.im.db.DBManager;
import com.bcr.jianxinIM.im.db.Friend;
import com.bcr.jianxinIM.im.db.FriendDao;
import com.bcr.jianxinIM.im.model.SealSearchConversationResult;
import com.bcr.jianxinIM.im.server.pinyin.CharacterParser;
import com.bcr.jianxinIM.im.server.utils.NToast;
import com.bcr.jianxinIM.im.server.utils.OperationRong;
import com.bcr.jianxinIM.im.ui.widget.switchbutton.SwitchButton;
import com.bcr.jianxinIM.view.CircleImageView;
import com.bcr.jianxinIM.view.CommonDialog;

import java.util.ArrayList;

import io.rong.eventbus.EventBus;
import io.rong.imageloader.core.ImageLoader;
import io.rong.imkit.RongIM;
import io.rong.imkit.userInfoCache.RongUserInfoManager;
import io.rong.imlib.RongIMClient;
import io.rong.imlib.model.Conversation;
import io.rong.imlib.model.Message;
import io.rong.imlib.model.UserInfo;

/**
 * Created by AMing on 16/3/9.
 * Company RongCloud
 */
public class PrivateChatDetailActivity extends BaseActivity implements View.OnClickListener, CompoundButton.OnCheckedChangeListener {

    private static final int SEARCH_TYPE_FLAG = 1;

    private UserInfo mUserInfo;
    private SwitchButton messageTop, messageNotification;
    private CircleImageView mImageView;
    private TextView friendName;
    private LinearLayout mSearchChattingRecordsLinearLayout;

    private Conversation.ConversationType mConversationType;
    private String fromConversationId;
    private SealSearchConversationResult mResult;
    private LinearLayout llCreateGroup;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fr_friend_detail);
        setTitle("聊天详情");
        initView();
        fromConversationId = getIntent().getStringExtra("TargetId");
        mConversationType = (Conversation.ConversationType) getIntent().getSerializableExtra("conversationType");

        if (!TextUtils.isEmpty(fromConversationId)) {
            mUserInfo = RongUserInfoManager.getInstance().getUserInfo(fromConversationId);
            updateUI();
        }
        EventBus.getDefault().register(this);
        SealAppContext.getInstance().pushActivity(this);
    }

    private void updateUI() {
        if (mUserInfo != null) {
            initData();
            getState(mUserInfo.getUserId());
        }
    }

    private void initData() {
        if (mUserInfo != null) {
            String portraitUri = SealUserInfoManager.getInstance().getPortraitUri(mUserInfo);
            Logger.d("头像：-->"+portraitUri);
            ImageLoader.getInstance().displayImage(portraitUri, mImageView);

            Friend friend = SealUserInfoManager.getInstance().getFriendByID(mUserInfo.getUserId());
            if (friend != null && !TextUtils.isEmpty(friend.getDisplayName())) {
                friendName.setText(friend.getDisplayName());
            } else {
                friendName.setText(mUserInfo.getName());
            }
        }

    }

    private void initView() {
        LinearLayout cleanMessage = (LinearLayout) findViewById(R.id.clean_friend);
        mImageView = (CircleImageView) findViewById(R.id.friend_header);
        mImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(PrivateChatDetailActivity.this, UserDetailActivity.class);
                Friend friend = CharacterParser.getInstance().generateFriendFromUserInfo(mUserInfo);
                intent.putExtra("friend", friend);
                intent.putExtra("conversationType", Conversation.ConversationType.PRIVATE.getValue());
                intent.putExtra("type", 1);
                PrivateChatDetailActivity.this.startActivity(intent);
            }
        });
        llCreateGroup=findViewById(R.id.llCreateGroup);
        messageTop = (SwitchButton) findViewById(R.id.sw_freind_top);
        messageNotification = (SwitchButton) findViewById(R.id.sw_friend_notfaction);
        friendName = (TextView) findViewById(R.id.friend_name);
        mSearchChattingRecordsLinearLayout = (LinearLayout) findViewById(R.id.ac_ll_search_messages);
        cleanMessage.setOnClickListener(this);
        messageNotification.setOnCheckedChangeListener(this);
        messageTop.setOnCheckedChangeListener(this);
        mSearchChattingRecordsLinearLayout.setOnClickListener(this);
        llCreateGroup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(new Intent(mContext, SelectFriendsActivity.class));
                Friend friend = CharacterParser.getInstance().generateFriendFromUserInfo(mUserInfo);
                intent.putExtra("createGroup", true);
                intent.putExtra("friend", friend);
                startActivity(intent);
            }
        });
    }

    @Override
    protected void onDestroy() {
        EventBus.getDefault().unregister(this);
        SealAppContext.getInstance().popActivity(this);
        super.onDestroy();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        finish();
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ac_ll_search_messages:
                Intent searchIntent = new Intent(PrivateChatDetailActivity.this, SealSearchChattingDetailActivity.class);
                searchIntent.putExtra("filterString", "");
                ArrayList<Message> arrayList = new ArrayList<>();
                searchIntent.putParcelableArrayListExtra("filterMessages", arrayList);
                mResult = new SealSearchConversationResult();
                Conversation conversation = new Conversation();
                conversation.setTargetId(fromConversationId);
                conversation.setConversationType(mConversationType);
                mResult.setConversation(conversation);
                Friend friend = DBManager.getInstance().getDaoSession().getFriendDao().queryBuilder().where(FriendDao.Properties.UserId.eq(fromConversationId)).unique();
                SharedPreferences sp = getSharedPreferences("config", Context.MODE_PRIVATE);
                String currentUserId = sp.getString(SealConst.SEALTALK_LOGIN_ID, "");
                String currentUserName = sp.getString(SealConst.SEALTALK_LOGIN_NAME, "");
                String currentUserPortrait = sp.getString(SealConst.SEALTALK_LOGING_PORTRAIT, "");
                if (friend != null) {
                    String portraitUri = friend.getPortraitUri().toString();
                    mResult.setId(friend.getUserId());
                    if (!TextUtils.isEmpty(portraitUri)) {
                        mResult.setPortraitUri(portraitUri);
                    }
                    if (!TextUtils.isEmpty(friend.getDisplayName())) {
                        mResult.setTitle(friend.getDisplayName());
                    } else {
                        mResult.setTitle(friend.getName());
                    }
                } else if (fromConversationId.equals(currentUserId)) {
                    mResult.setId(currentUserId);
                    if (!TextUtils.isEmpty(currentUserPortrait)) {
                        mResult.setPortraitUri(currentUserPortrait);
                    }
                    if (!TextUtils.isEmpty(currentUserName)) {
                        mResult.setTitle(currentUserName);
                    } else {
                        mResult.setTitle(currentUserId);
                    }
                } else {
                    UserInfo userInfo = RongUserInfoManager.getInstance().getUserInfo(conversation.getTargetId());
                    mResult.setId(conversation.getTargetId());
                    String portraitUri = userInfo.getPortraitUri().toString();
                    if (!TextUtils.isEmpty(portraitUri)) {
                        mResult.setPortraitUri(portraitUri);
                    }
                    if (!TextUtils.isEmpty(userInfo.getName())) {
                        mResult.setTitle(userInfo.getName());
                    } else {
                        mResult.setTitle(userInfo.getUserId());
                    }
                }
                searchIntent.putExtra("searchConversationResult", mResult);
                searchIntent.putExtra("flag", SEARCH_TYPE_FLAG);
                startActivity(searchIntent);
                break;
            case R.id.clean_friend:
                CommonDialog dialog = new CommonDialog.Builder()
                        .setContentMessage( getString(R.string.clean_private_chat_history))
                        .setDialogButtonClickListener(new CommonDialog.OnDialogButtonClickListener() {
                            @Override
                            public void onPositiveClick(View v, Bundle bundle) {
                                if (RongIM.getInstance() != null && mUserInfo != null) {
                                    RongIM.getInstance().clearMessages(Conversation.ConversationType.PRIVATE,
                                            mUserInfo.getUserId(), new RongIMClient.ResultCallback<Boolean>() {
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
                                            mUserInfo.getUserId(), System.currentTimeMillis(),
                                            null);
                                }
                            }



                            @Override
                            public void onNegativeClick(View v, Bundle bundle) {
                            }
                        })
                        .build();
                dialog.show(getSupportFragmentManager(), null);
                break;
        }
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        switch (buttonView.getId()) {
            case R.id.sw_friend_notfaction:
                if (isChecked) {
                    if (mUserInfo != null) {
                        OperationRong.setConverstionNotif(mContext, Conversation.ConversationType.PRIVATE, mUserInfo.getUserId(), true);
                    }
                } else {
                    if (mUserInfo != null) {
                        OperationRong.setConverstionNotif(mContext, Conversation.ConversationType.PRIVATE, mUserInfo.getUserId(), false);
                    }
                }
                break;
            case R.id.sw_freind_top:
                if (isChecked) {
                    if (mUserInfo != null) {
                        OperationRong.setConversationTop(mContext, Conversation.ConversationType.PRIVATE, mUserInfo.getUserId(), true);
                    }
                } else {
                    if (mUserInfo != null) {
                        OperationRong.setConversationTop(mContext, Conversation.ConversationType.PRIVATE, mUserInfo.getUserId(), false);
                    }
                }
                break;
        }
    }

    private void getState(String targetId) {
        if (targetId != null) {//群组列表 page 进入
            if (RongIM.getInstance() != null) {
                RongIM.getInstance().getConversation(Conversation.ConversationType.PRIVATE, targetId, new RongIMClient.ResultCallback<Conversation>() {
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

                RongIM.getInstance().getConversationNotificationStatus(Conversation.ConversationType.PRIVATE, targetId, new RongIMClient.ResultCallback<Conversation.ConversationNotificationStatus>() {
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
        }
    }

    public void onEventMainThread(UserInfo userInfo) {
        if (userInfo != null && userInfo.getUserId().equals(fromConversationId)) {
            mUserInfo = userInfo;
            updateUI();
        }
    }
}
