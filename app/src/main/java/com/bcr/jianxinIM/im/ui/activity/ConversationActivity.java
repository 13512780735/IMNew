package com.bcr.jianxinIM.im.ui.activity;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.FragmentTransaction;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bcr.jianxinIM.im.db.Friend;
import com.bcr.jianxinIM.im.server.pinyin.CharacterParser;
import com.bcr.jianxinIM.im.server.response.CollectMessageRespone;
import com.bcr.jianxinIM.im.server.response.GetFriendInfoByIDResponse;
import com.bcr.jianxinIM.im.server.response.GetGroupInfoResponse;
import com.bcr.jianxinIM.im.server.utils.RongGenerate;
import com.orhanobut.logger.Logger;
import com.bcr.jianxinIM.activity.main.MainActivity;
import com.bcr.jianxinIM.R;
import com.bcr.jianxinIM.activity.LoginRegister.Login01Activity;
import com.bcr.jianxinIM.im.SealAppContext;
import com.bcr.jianxinIM.im.SealUserInfoManager;
import com.bcr.jianxinIM.im.db.GroupMember;
import com.bcr.jianxinIM.im.server.SealAction;
import com.bcr.jianxinIM.im.server.network.async.AsyncTaskManager;
import com.bcr.jianxinIM.im.server.network.async.OnDataListener;
import com.bcr.jianxinIM.im.server.network.http.HttpException;
import com.bcr.jianxinIM.im.server.response.SendGroupImageResponse;
import com.bcr.jianxinIM.im.server.response.SetPortraitResponse;
import com.bcr.jianxinIM.im.server.utils.NLog;
import com.bcr.jianxinIM.im.server.utils.NToast;
import com.bcr.jianxinIM.im.ui.fragment.ConversationFragmentEx;
import com.bcr.jianxinIM.im.ui.widget.LoadingDialog;
import com.bcr.jianxinIM.im.utils.ScreenCaptureUtil;
import com.bcr.jianxinIM.im.utils.ThreadManager;
import com.bcr.jianxinIM.util.ImageUtils;
import com.bcr.jianxinIM.util.SharedPreferencesUtil;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

import io.rong.callkit.RongCallKit;
import io.rong.imkit.RongIM;
import io.rong.imkit.RongKitIntent;
import io.rong.imkit.fragment.UriFragment;
import io.rong.imkit.userInfoCache.RongUserInfoManager;
import io.rong.imlib.MessageTag;
import io.rong.imlib.RongIMClient;
import io.rong.imlib.model.Conversation;
import io.rong.imlib.model.Discussion;
import io.rong.imlib.model.PublicServiceProfile;
import io.rong.imlib.model.UserInfo;
import io.rong.imlib.typingmessage.TypingStatus;
import io.rong.message.TextMessage;
import io.rong.message.VoiceMessage;

//CallKit start 1
//CallKit end 1

/**
 * 会话页面
 * 1，设置 ActionBar title
 * 2，加载会话页面
 * 3，push 和 通知 判断
 */
public class ConversationActivity extends BaseActivity1 implements View.OnClickListener {
    private static final int UPLOAD =208 ;
    private static final int SENDGROUPIMG =217 ;
    private static final int GETGROUPINFO =214 ;
    private String TAG = ConversationActivity.class.getSimpleName();
    /**
     * 对方id
     */
    private String mTargetId;
    /**
     * 会话类型
     */
    private Conversation.ConversationType mConversationType;
    /**
     * title
     */
    private String title;
    /**
     * 是否在讨论组内，如果不在讨论组内，则进入不到讨论组设置页面
     */
    private boolean isFromPush = false;

    private LoadingDialog mDialog;

    private SharedPreferences sp;

    private final String TextTypingTitle = "对方正在输入...";
    private final String VoiceTypingTitle = "对方正在讲话...";

    private Handler mHandler;

    public static final int SET_TEXT_TYPING_TITLE = 1;
    public static final int SET_VOICE_TYPING_TITLE = 2;
    public static final int SET_TARGET_ID_TITLE = 0;

    private Button mRightButton;
    private RelativeLayout layout_announce;
    private TextView tv_announce;
    private ImageView iv_arrow;
    private ScreenCaptureUtil screenCaptureUtil;
    private List<GroupMember> groups;
    private static final int CLICK_CONVERSATION_USER_PORTRAIT = 1;
    @Override
    @TargetApi(23)
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.conversation);
        sp = getSharedPreferences("config", MODE_PRIVATE);
        mDialog = new LoadingDialog(this);
        layout_announce = (RelativeLayout) findViewById(R.id.ll_annouce);
        iv_arrow = (ImageView) findViewById(R.id.iv_announce_arrow);
        layout_announce.setVisibility(View.GONE);
        tv_announce = (TextView) findViewById(R.id.tv_announce_msg);

        mRightButton = getHeadRightButton();

        Intent intent = getIntent();

        if (intent == null || intent.getData() == null)
            return;

        mTargetId = intent.getData().getQueryParameter("targetId");
        //10000 为 Demo Server 加好友的 id，若 targetId 为 10000，则为加好友消息，默认跳转到 NewFriendListActivity
        // Demo 逻辑
        if (mTargetId != null && mTargetId.equals("10000")) {

            Logger.d("用户Id：--》"+mTargetId);
            startActivity(new Intent(ConversationActivity.this, NewFriendListActivity.class));

            return;
        }
        mConversationType = Conversation.ConversationType.valueOf(intent.getData()
                .getLastPathSegment().toUpperCase(Locale.US));

        title = intent.getData().getQueryParameter("title");

        setActionBarTitle(mConversationType, mTargetId);


        if (mConversationType.equals(Conversation.ConversationType.GROUP)) {
            mRightButton.setBackground(getResources().getDrawable(R.drawable.icon2_menu));
        } else if (mConversationType.equals(Conversation.ConversationType.PRIVATE)
                || mConversationType.equals(Conversation.ConversationType.PUBLIC_SERVICE)
                || mConversationType.equals(Conversation.ConversationType.APP_PUBLIC_SERVICE)
                || mConversationType.equals(Conversation.ConversationType.DISCUSSION)) {
            mRightButton.setBackground(getResources().getDrawable(R.drawable.contact_more));
        } else {
            mRightButton.setVisibility(View.GONE);
            mRightButton.setClickable(false);
        }
        mRightButton.setOnClickListener(this);

        isPushMessage(intent);
        if (mConversationType.equals(Conversation.ConversationType.CUSTOMER_SERVICE)) {
            setAnnounceListener();
        }

        mHandler = new Handler(new Handler.Callback() {
            @Override
            public boolean handleMessage(Message msg) {
                switch (msg.what) {
                    case SET_TEXT_TYPING_TITLE:
                        setTitle(TextTypingTitle);
                        break;
                    case SET_VOICE_TYPING_TITLE:
                        setTitle(VoiceTypingTitle);
                        break;
                    case SET_TARGET_ID_TITLE:
                        setActionBarTitle(mConversationType, mTargetId);
                        break;
                    default:
                        break;
                }
                return true;
            }
        });

        RongIMClient.setTypingStatusListener(new RongIMClient.TypingStatusListener() {
            @Override
            public void onTypingStatusChanged(Conversation.ConversationType type, String targetId, Collection<TypingStatus> typingStatusSet) {
                //当输入状态的会话类型和targetID与当前会话一致时，才需要显示
                if (type.equals(mConversationType) && targetId.equals(mTargetId)) {
                    int count = typingStatusSet.size();
                    //count表示当前会话中正在输入的用户数量，目前只支持单聊，所以判断大于0就可以给予显示了
                    if (count > 0) {
                        Iterator iterator = typingStatusSet.iterator();
                        TypingStatus status = (TypingStatus) iterator.next();
                        String objectName = status.getTypingContentType();

                        MessageTag textTag = TextMessage.class.getAnnotation(MessageTag.class);
                        MessageTag voiceTag = VoiceMessage.class.getAnnotation(MessageTag.class);
                        //匹配对方正在输入的是文本消息还是语音消息
                        if (objectName.equals(textTag.value())) {
                            mHandler.sendEmptyMessage(SET_TEXT_TYPING_TITLE);
                        } else if (objectName.equals(voiceTag.value())) {
                            mHandler.sendEmptyMessage(SET_VOICE_TYPING_TITLE);
                        }
                    } else {//当前会话没有用户正在输入，标题栏仍显示原来标题
                        mHandler.sendEmptyMessage(SET_TARGET_ID_TITLE);
                    }
                }
            }
        });

        SealAppContext.getInstance().pushActivity(this);

        //CallKit start 2
        RongCallKit.setGroupMemberProvider(new RongCallKit.GroupMembersProvider() {
            @Override
            public ArrayList<String> getMemberList(String groupId, final RongCallKit.OnGroupMembersResult result) {
                getGroupMembersForCall();
                mCallMemberResult = result;
                return null;
            }
        });
        RongIM.getInstance().setGroupMembersProvider(new RongIM.IGroupMembersProvider() {
            @Override
            public void getGroupMembers(String groupId, RongIM.IGroupMemberCallback callback) {
                //获取群组成员信息列表

                groups = SealUserInfoManager.getInstance().getGroupMembers1(mTargetId);
                List<UserInfo> list = new ArrayList<>();
                for (GroupMember group : groups) {
                    UserInfo userInfo = new UserInfo(group.getUserId(), group.getName(), group.getPortraitUri());
                    list.add(userInfo);
                }
                callback.onGetGroupMembersResult(list); // 调用 callback 的 onGetGroupMembersResult 回传群组信息
            }
        });

        //CallKit end 2

        RongIM.setConversationBehaviorListener(new RongIM.ConversationBehaviorListener() {
            @Override
            public boolean onUserPortraitClick(Context context, Conversation.ConversationType conversationType, UserInfo userInfo) {
                if (conversationType == Conversation.ConversationType.CUSTOMER_SERVICE || conversationType == Conversation.ConversationType.PUBLIC_SERVICE || conversationType == Conversation.ConversationType.APP_PUBLIC_SERVICE) {
                    return false;
                }
                if(conversationType==Conversation.ConversationType.PRIVATE){
                    //开发测试时,发送系统消息的userInfo只有id不为空
                    if (userInfo != null && userInfo.getName() != null && userInfo.getPortraitUri() != null) {
                        Intent intent = new Intent(context, UserDetailActivity.class);
                        // intent.putExtra("conversationType", conversationType.getValue());
                        intent.putExtra("conversationType", conversationType.getName());
                        Logger.d("conversationType:-->"+ conversationType.getValue());
                        Logger.d("conversationType:-->"+ conversationType.getName());

                        Friend friend = CharacterParser.getInstance().generateFriendFromUserInfo(userInfo);
                        intent.putExtra("friend", friend);
                        intent.putExtra("type", CLICK_CONVERSATION_USER_PORTRAIT);
                        context.startActivity(intent);
                    }}else if(conversationType== Conversation.ConversationType.GROUP){
                    AsyncTaskManager.getInstance(mContext).request(GETGROUPINFO, new OnDataListener() {
                        @Override
                        public Object doInBackground(int requestCode, String parameter) throws HttpException {
                            return new SealAction(mContext).getGroupInfo(mTargetId);
                        }
                        @Override
                        public void onSuccess(int requestCode, Object result) {
                            if (result != null) {
                                GetGroupInfoResponse getGroupInfoResponse= (GetGroupInfoResponse) result;
                                if(getGroupInfoResponse.isSuccess()==true){
                                    int isAddFriend=getGroupInfoResponse.getResultData().getIsAddFriend();
                                    String userId=SharedPreferencesUtil.getString(mContext,"userId","");
                                    String createdId=getGroupInfoResponse.getResultData().getCreatorId();
                                    if (userId.equals(createdId)) {
                                        Intent intent = new Intent(context, UserDetailActivity.class);
                                        Friend friend = CharacterParser.getInstance().generateFriendFromUserInfo(userInfo);
                                        intent.putExtra("friend", friend);
                                        //intent.putExtra("conversationType", Conversation.ConversationType.GROUP.getValue());
                                        //Logger.d("conversationType:-->"+ Conversation.ConversationType.GROUP.getName());
                                        //Groups not Serializable,just need group name
                                        intent.putExtra("groupName", conversationType.getName());
                                        intent.putExtra("created", "1");
                                        intent.putExtra("type", CLICK_CONVERSATION_USER_PORTRAIT);
                                        context.startActivity(intent);
                                    }else {
                                    if(isAddFriend==1){
                                        Intent intent = new Intent(context, UserDetailActivity.class);
                                        // intent.putExtra("conversationType", conversationType.getValue());
                                        intent.putExtra("conversationType", conversationType.getName());
//                                        Logger.d("conversationType:-->"+ conversationType.getValue());
//                                        Logger.d("conversationType:-->"+ conversationType.getName());
                                        intent.putExtra("created", "0");
                                        Friend friend = CharacterParser.getInstance().generateFriendFromUserInfo(userInfo);
                                        intent.putExtra("friend", friend);
                                        intent.putExtra("type", CLICK_CONVERSATION_USER_PORTRAIT);
                                        context.startActivity(intent);
                                    }else {
                                        return;
                                    }}

                                   // NToast.shortToast(mContext,getGroupInfoResponse.getMessage());
                                }else {
                                    NToast.shortToast(mContext,getGroupInfoResponse.getMessage());
                                }
                            }
                        }

                        @Override
                        public void onFailure(int requestCode, int state, Object result) {

                        }
                    });

                }
                return true;
            }

            @Override
            public boolean onUserPortraitLongClick(Context context, Conversation.ConversationType conversationType, UserInfo userInfo) {
                return false;
            }

            @Override
            public boolean onMessageClick(Context context, View view, io.rong.imlib.model.Message message) {
                return false;
            }

            @Override
            public boolean onMessageLinkClick(Context context, String s) {
                return false;
            }

            @Override
            public boolean onMessageLongClick(Context context, View view, io.rong.imlib.model.Message message) {
                return false;
            }
        });
    }

    //    private void getGroupMembers1() {
//        SealUserInfoManager.getInstance().getGroupMembers(mTargetId, new SealUserInfoManager.ResultCallback<List<GroupMember>>() {
//            @Override
//            public void onSuccess(List<GroupMember> groupMembers) {
//                for (GroupMember u : groupMembers) {
//                }
//                LoadDialog.dismiss(mContext);
//                if (groupMembers != null && groupMembers.size() > 0) {
//                    list = groupMembers;
//                   // initGroupMemberData();
//                }
//            }
//
//            @Override
//            public void onError(String errString) {
//                LoadDialog.dismiss(mContext);
//            }
//        });
//    }
    @Override
    protected void onResume() {
        refreshScreenCaptureStatus();

        Logger.d("执行了会话");
        Logger.d("执行了会话"+mConversationType);
        Logger.d("执行了会话"+mTargetId);


        setActionBarTitle(mConversationType, mTargetId);
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (screenCaptureUtil != null) {
            screenCaptureUtil.unRegister();
        }
    }

    private void refreshScreenCaptureStatus() {
        initScreenShotListener();
        screenCaptureUtil.register();
    }

    private void initScreenShotListener() {
        screenCaptureUtil = new ScreenCaptureUtil(this);
        screenCaptureUtil.setScreenShotListener(new ScreenCaptureUtil.ScreenShotListener() {
            @Override
            public void onScreenShotComplete(String data, long dateTaken) {
                Logger.d("截屏：--》"+data);
                Logger.d(TAG, "onScreenShotComplete===");
                String localImagePath = data;
                BitmapFactory.Options options = new BitmapFactory.Options();

                options.inSampleSize = 2;
                Bitmap bitmap = BitmapFactory.decodeFile(data,options);
               // ImageMessage imgMsg = ImageMessage.obtain(Uri.parse("file://" + localImagePath), Uri.parse("file://" + localImagePath));
                String base64= ImageUtils.bitmapToBase64(bitmap);
                ThreadManager.getInstance().runOnUIThread(new Runnable() {
                    @Override
                    public void run() {
                        System.out.println("执行了");
                        //在主线程注册 observeForever 因为截屏时候可能使得 activity 处于 pause 状态，无法发送消息
                        if(mConversationType.equals(Conversation.ConversationType.GROUP)){
                            System.out.println("执行了111");
                            //群里面截屏
                            upload(base64);

                        }
//                        RongIM.getInstance().getRongIMClient().sendImageMessage(mConversationType, mTargetId, imgMsg, null, null, new RongIMClient.SendImageMessageCallback() {
//                            @Override
//                            public void onAttached(io.rong.imlib.model.Message message) {
//                                //保存数据库成功
//                            }
//
//                            @Override
//                            public void onError(io.rong.imlib.model.Message message, RongIMClient.ErrorCode errorCode) {
//                                //发送失败
//                            }
//
//                            @Override
//                            public void onSuccess(io.rong.imlib.model.Message message) {
//                                //发送成功
//                            }
//
//                            @Override
//                            public void onProgress(io.rong.imlib.model.Message message, int i) {
//                                //发送进度
//                            }
//
//                        });
//                        CommonDialog commonDialog = new CommonDialog.Builder()
//                                .setContentMessage("是否发送该图片")
//                                .setDialogButtonClickListener(new CommonDialog.OnDialogButtonClickListener() {
//                                    @Override
//                                    public void onPositiveClick(View v, Bundle bundle) {
//
//                                    }
//
//                                    @Override
//                                    public void onNegativeClick(View v, Bundle bundle) {
//                                    }
//                                })
//                                .build();
//                        commonDialog.show(getSupportFragmentManager(), null);


                    }
                });
            }
        });
        screenCaptureUtil.register();
    }

    /**
     * 上传截屏图片转url
     * @param base64
     */
    private void upload(String base64) {
        AsyncTaskManager.getInstance(mContext).request(UPLOAD, new OnDataListener() {
            @Override
            public Object doInBackground(int requestCode, String parameter) throws HttpException {
                return new SealAction(mContext).setPortrait(base64);
            }

            @Override
            public void onSuccess(int requestCode, Object result) {
                if (result != null) {
                    SetPortraitResponse setPortraitResponse= (SetPortraitResponse) result;
                    if(setPortraitResponse.isSuccess()==true){
                        System.out.println("图片获取成功");
                       // finish();
                        String url=setPortraitResponse.getResultData();
                        sendGroupImage(url);
                    }else {
                    }
                }
            }

            @Override
            public void onFailure(int requestCode, int state, Object result) {
                //LoadDialog.dismiss(mContext);
            }
        });
    }

    /**
     * 发送截屏给服务器
     * @param url
     */
    private void sendGroupImage(String url) {
        AsyncTaskManager.getInstance(mContext).request(SENDGROUPIMG, new OnDataListener() {
            @Override
            public Object doInBackground(int requestCode, String parameter) throws HttpException {
                return new SealAction(mContext).SendGroupIamge(mTargetId,SharedPreferencesUtil.getString(mContext,"userId",""),url);
            }

            @Override
            public void onSuccess(int requestCode, Object result) {
                if (result != null) {
                    SendGroupImageResponse sendGroupImageResponse= (SendGroupImageResponse) result;
                    if(sendGroupImageResponse.isSuccess()==true){
                        // finish();

                        System.out.println("截屏消息执行");
                    }else {
                        System.out.println("截屏消息未执行");
                    }
                }
            }

            @Override
            public void onFailure(int requestCode, int state, Object result) {
                //LoadDialog.dismiss(mContext);
            }
        });

    }

    /**
     * 设置通告栏的监听
     */
    private void setAnnounceListener() {
        if (fragment != null) {
            fragment.setOnShowAnnounceBarListener(new ConversationFragmentEx.OnShowAnnounceListener() {
                @Override
                public void onShowAnnounceView(String announceMsg, final String announceUrl) {
                    layout_announce.setVisibility(View.VISIBLE);
                    tv_announce.setText(announceMsg);
                    layout_announce.setClickable(false);
                    if (!TextUtils.isEmpty(announceUrl)) {
                        iv_arrow.setVisibility(View.VISIBLE);
                        layout_announce.setClickable(true);
                        layout_announce.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                String str = announceUrl.toLowerCase();
                                if (!TextUtils.isEmpty(str)) {
                                    if (!str.startsWith("http") && !str.startsWith("https")) {
                                        str = "http://" + str;
                                    }
                                    Intent intent = new Intent(RongKitIntent.RONG_INTENT_ACTION_WEBVIEW);
                                    intent.setPackage(v.getContext().getPackageName());
                                    intent.putExtra("url", str);
                                    v.getContext().startActivity(intent);
                                }
                            }
                        });
                    } else {
                        iv_arrow.setVisibility(View.GONE);
                    }
                }
            });
        }
    }

    /**
     * 判断是否是 Push 消息，判断是否需要做 connect 操作
     */
    private void isPushMessage(Intent intent) {

        if (intent == null || intent.getData() == null)
            return;
        //push
        if (intent.getData().getScheme().equals("rong") && intent.getData().getQueryParameter("isFromPush") != null) {

            //通过intent.getData().getQueryParameter("push") 为true，判断是否是push消息
            if (intent.getData().getQueryParameter("isFromPush").equals("true")) {
                //只有收到系统消息和不落地 push 消息的时候，pushId 不为 null。而且这两种消息只能通过 server 来发送，客户端发送不了。
                //RongIM.getInstance().getRongIMClient().recordNotificationEvent(id);
                if (mDialog != null && !mDialog.isShowing()) {
                    mDialog.show();
                }
                isFromPush = true;
                enterActivity();
            } else if (RongIM.getInstance().getCurrentConnectionStatus().equals(RongIMClient.ConnectionStatusListener.ConnectionStatus.DISCONNECTED)) {
                if (mDialog != null && !mDialog.isShowing()) {
                    mDialog.show();
                }
                if (intent.getData().getPath().contains("conversation/system")) {
                    Intent intent1 = new Intent(mContext, MainActivity.class);
                    intent1.putExtra("systemconversation", true);
                    startActivity(intent1);
                    SealAppContext.getInstance().popAllActivity();
                    return;
                }
                enterActivity();
            } else {
                if (intent.getData().getPath().contains("conversation/system")) {
                    Intent intent1 = new Intent(mContext, MainActivity.class);
                    intent1.putExtra("systemconversation", true);
                    startActivity(intent1);
                    SealAppContext.getInstance().popAllActivity();
                    return;
                }
                enterFragment(mConversationType, mTargetId);
            }

        } else {
            if (RongIM.getInstance().getCurrentConnectionStatus().equals(RongIMClient.ConnectionStatusListener.ConnectionStatus.DISCONNECTED)) {
                if (mDialog != null && !mDialog.isShowing()) {
                    mDialog.show();
                }
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        enterActivity();
                    }
                }, 300);
            } else {
                enterFragment(mConversationType, mTargetId);
            }
        }
    }


    /**
     * 收到 push 消息后，选择进入哪个 Activity
     * 如果程序缓存未被清理，进入 MainActivity
     * 程序缓存被清理，进入 LoginActivity，重新获取token
     * <p>
     * 作用：由于在 manifest 中 intent-filter 是配置在 ConversationActivity 下面，所以收到消息后点击notifacition 会跳转到 DemoActivity。
     * 以跳到 MainActivity 为例：
     * 在 ConversationActivity 收到消息后，选择进入 MainActivity，这样就把 MainActivity 激活了，当你读完收到的消息点击 返回键 时，程序会退到
     * MainActivity 页面，而不是直接退回到 桌面。
     */
    private void enterActivity() {

        String token = sp.getString("loginToken", "");

        if (token.equals("default")) {
            NLog.e("ConversationActivity push", "push2");

            Intent intent=new Intent(ConversationActivity.this,Login01Activity.class);
            Bundle bundle=new Bundle();
            bundle.putString("flag","0");
            intent.putExtras(bundle);
            startActivity(intent);
            //startActivity(new Intent(ConversationActivity.this, Login01Activity.class));
            SealAppContext.getInstance().popAllActivity();
        } else {
            NLog.e("ConversationActivity push", "push3");
            reconnect(token);
        }
    }

    private void reconnect(String token) {
        RongIM.connect(token, new RongIMClient.ConnectCallback() {
            @Override
            public void onTokenIncorrect() {

                Log.e(TAG, "---onTokenIncorrect--");
            }

            @Override
            public void onSuccess(String s) {
                Log.i(TAG, "---onSuccess--" + s);
                NLog.e("ConversationActivity push", "push4");

                if (mDialog != null)
                    mDialog.dismiss();

                enterFragment(mConversationType, mTargetId);

            }

            @Override
            public void onError(RongIMClient.ErrorCode e) {
                Log.e(TAG, "---onError--" + e);
                if (mDialog != null)
                    mDialog.dismiss();

                enterFragment(mConversationType, mTargetId);
            }
        });

    }

    private ConversationFragmentEx fragment;

    /**
     * 加载会话页面 ConversationFragmentEx 继承自 ConversationFragment
     *
     * @param mConversationType 会话类型
     * @param mTargetId         会话 Id
     */
    private void enterFragment(Conversation.ConversationType mConversationType, String mTargetId) {

        fragment = new ConversationFragmentEx();

        Uri uri = Uri.parse("rong://" + getApplicationInfo().packageName).buildUpon()
                .appendPath("conversation").appendPath(mConversationType.getName().toLowerCase())
                .appendQueryParameter("targetId", mTargetId).build();

        fragment.setUri(uri);

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        //xxx 为你要加载的 id
        transaction.add(R.id.rong_content, fragment);
        transaction.commitAllowingStateLoss();
    }



    /**
     * 设置会话页面 Title
     *
     * @param conversationType 会话类型
     * @param targetId         目标 Id
     */
    private void setActionBarTitle(Conversation.ConversationType conversationType, String targetId) {

        if (conversationType == null)
            return;

        if (conversationType.equals(Conversation.ConversationType.PRIVATE)) {
            setPrivateActionBar(targetId);
        } else if (conversationType.equals(Conversation.ConversationType.GROUP)) {
            setGroupActionBar(targetId);
        } else if (conversationType.equals(Conversation.ConversationType.DISCUSSION)) {
            setDiscussionActionBar(targetId);
        } else if (conversationType.equals(Conversation.ConversationType.CHATROOM)) {
            setTitle(title);
        } else if (conversationType.equals(Conversation.ConversationType.SYSTEM)) {
            setTitle(R.string.de_actionbar_system);
        } else if (conversationType.equals(Conversation.ConversationType.APP_PUBLIC_SERVICE)) {
            setAppPublicServiceActionBar(targetId);
        } else if (conversationType.equals(Conversation.ConversationType.PUBLIC_SERVICE)) {
            setPublicServiceActionBar(targetId);
        } else if (conversationType.equals(Conversation.ConversationType.CUSTOMER_SERVICE)) {
            setTitle(R.string.main_customer);
        } else {
            setTitle(R.string.de_actionbar_sub_defult);
        }

    }
    private static final int GET_GROUP_INFOS =206;
    /**
     * 设置群聊界面 ActionBar
     *
     * @param targetId 会话 Id
     */
    private void setGroupActionBar(String targetId) {
        AsyncTaskManager.getInstance(mContext).request(GET_GROUP_INFOS, new OnDataListener() {
            @Override
            public Object doInBackground(int requestCode, String parameter) throws HttpException {
                return new SealAction(mContext).getGroupInfo(targetId);
            }

            @Override
            public void onSuccess(int requestCode, Object result) {
                if (result != null) {
                    GetGroupInfoResponse getGroupInfoResponse= (GetGroupInfoResponse) result;
                    setTitle(getGroupInfoResponse.getResultData().getName());
//                    if(TextUtils.isEmpty(getGroupInfoResponse.getResultData().getName())){
//                        setTitle(getGroupInfoResponse.getResultData().getUserNickName());
//                    }else {
//                        setTitle(getGroupInfoResponse.getResultData().getRemark());
//                    }
                }
            }

            @Override
            public void onFailure(int requestCode, int state, Object result) {

            }
        });


        if (!TextUtils.isEmpty(title)) {
            setTitle(title);
        } else {
            setTitle(targetId);
        }
    }

    /**
     * 设置应用公众服务界面 ActionBar
     */
    private void setAppPublicServiceActionBar(String targetId) {
        if (targetId == null)
            return;

        RongIM.getInstance().getPublicServiceProfile(Conversation.PublicServiceType.APP_PUBLIC_SERVICE
                , targetId, new RongIMClient.ResultCallback<PublicServiceProfile>() {
                    @Override
                    public void onSuccess(PublicServiceProfile publicServiceProfile) {
                        setTitle(publicServiceProfile.getName());
                    }

                    @Override
                    public void onError(RongIMClient.ErrorCode errorCode) {

                    }
                });
    }

    /**
     * 设置公共服务号 ActionBar
     */
    private void setPublicServiceActionBar(String targetId) {

        if (targetId == null)
            return;


        RongIM.getInstance().getPublicServiceProfile(Conversation.PublicServiceType.PUBLIC_SERVICE
                , targetId, new RongIMClient.ResultCallback<PublicServiceProfile>() {
                    @Override
                    public void onSuccess(PublicServiceProfile publicServiceProfile) {
                        setTitle(publicServiceProfile.getName());
                    }

                    @Override
                    public void onError(RongIMClient.ErrorCode errorCode) {

                    }
                });
    }

    /**
     * 设置讨论组界面 ActionBar
     */
    private void setDiscussionActionBar(String targetId) {

        if (targetId != null) {

            RongIM.getInstance().getDiscussion(targetId
                    , new RongIMClient.ResultCallback<Discussion>() {
                        @Override
                        public void onSuccess(Discussion discussion) {
                            setTitle(discussion.getName());
                        }

                        @Override
                        public void onError(RongIMClient.ErrorCode e) {
                            if (e.equals(RongIMClient.ErrorCode.NOT_IN_DISCUSSION)) {
                                setTitle("不在讨论组中");
                                supportInvalidateOptionsMenu();
                            }
                        }
                    });
        } else {
            setTitle("讨论组");
        }
    }

    private static final int REQUSERINFO = 4234;
    /**
     * 设置私聊界面 ActionBar
     */
    private void setPrivateActionBar(String targetId) {
        AsyncTaskManager.getInstance(mContext).request(REQUSERINFO, new OnDataListener() {
            @Override
            public Object doInBackground(int requestCode, String parameter) throws HttpException {
                return new SealAction(mContext).getFriendInfoByID(SharedPreferencesUtil.getString(mContext,"userId",""),targetId);
            }

            @Override
            public void onSuccess(int requestCode, Object result) {
                if (result != null) {
                    GetFriendInfoByIDResponse getFriendInfoByIDResponse= (GetFriendInfoByIDResponse) result;
                    if(TextUtils.isEmpty(getFriendInfoByIDResponse.getResultData().getRemark())){
                        setTitle(getFriendInfoByIDResponse.getResultData().getUserNickName());
                    }else {
                        setTitle(getFriendInfoByIDResponse.getResultData().getRemark());
                    }
                }
            }

            @Override
            public void onFailure(int requestCode, int state, Object result) {

            }
        });
//        if (!TextUtils.isEmpty(title)) {
//            if (title.equals("null")) {
//                if (!TextUtils.isEmpty(targetId)) {
//                    UserInfo userInfo = RongUserInfoManager.getInstance().getUserInfo(targetId);
//                    if (userInfo != null) {
//                        setTitle(userInfo.getName());
//                    }
//                }
//            } else {
//                setTitle(title);
//            }
//
//        } else {
//            setTitle(targetId);
//        }
    }

    /**
     * 根据 targetid 和 ConversationType 进入到设置页面
     */
    private void enterSettingActivity() {

        if (mConversationType == Conversation.ConversationType.PUBLIC_SERVICE
                || mConversationType == Conversation.ConversationType.APP_PUBLIC_SERVICE) {

            RongIM.getInstance().startPublicServiceProfile(this, mConversationType, mTargetId);
        } else {
            UriFragment fragment = (UriFragment) getSupportFragmentManager().getFragments().get(0);
            //得到讨论组的 targetId
            mTargetId = fragment.getUri().getQueryParameter("targetId");

            if (TextUtils.isEmpty(mTargetId)) {
                NToast.shortToast(mContext, "讨论组尚未创建成功");
            }


            Intent intent = null;
            if (mConversationType == Conversation.ConversationType.GROUP) {
                intent = new Intent(this, GroupDetailActivity.class);
                intent.putExtra("conversationType", Conversation.ConversationType.GROUP);
            } else if (mConversationType == Conversation.ConversationType.PRIVATE) {
                intent = new Intent(this, PrivateChatDetailActivity.class);
                intent.putExtra("conversationType", Conversation.ConversationType.PRIVATE);
            } else if (mConversationType == Conversation.ConversationType.DISCUSSION) {
                intent = new Intent(this, DiscussionDetailActivity.class);
                intent.putExtra("TargetId", mTargetId);
                startActivityForResult(intent, 166);
                return;
            }
            intent.putExtra("TargetId", mTargetId);
            if (intent != null) {
                startActivityForResult(intent, 500);
            }

        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == 501) {
            SealAppContext.getInstance().popAllActivity();
        }
    }

    @Override
    protected void onDestroy() {
        //CallKit start 3
        RongCallKit.setGroupMemberProvider(null);
        //CallKit end 3

        RongIMClient.setTypingStatusListener(null);
        SealAppContext.getInstance().popActivity(this);
        super.onDestroy();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (KeyEvent.KEYCODE_BACK == event.getKeyCode()) {
            if (fragment != null && !fragment.onBackPressed()) {
                if (isFromPush) {
                    isFromPush = false;
                    startActivity(new Intent(this, MainActivity.class));
                    SealAppContext.getInstance().popAllActivity();
                } else {
                    if (fragment.isLocationSharing()) {
                        fragment.showQuitLocationSharingDialog(this);
                        return true;
                    }
                    if (mConversationType.equals(Conversation.ConversationType.CHATROOM)
                            || mConversationType.equals(Conversation.ConversationType.CUSTOMER_SERVICE)) {
                        SealAppContext.getInstance().popActivity(this);
                    } else {
                        SealAppContext.getInstance().popActivity(this);
                    }
                }
            }
        }
        return false;
    }

    private void hintKbTwo() {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm.isActive() && getCurrentFocus() != null) {
            if (getCurrentFocus().getWindowToken() != null) {
                imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
            }
        }
    }

    //CallKit start 4
    private RongCallKit.OnGroupMembersResult mCallMemberResult;

    private void getGroupMembersForCall() {
        SealUserInfoManager.getInstance().getGroupMembers(mTargetId, new SealUserInfoManager.ResultCallback<List<GroupMember>>() {
            @Override
            public void onSuccess(List<GroupMember> groupMembers) {
                ArrayList<String> userIds = new ArrayList<>();
                if (groupMembers != null) {
                    for (GroupMember groupMember : groupMembers) {
                        if (groupMember != null) {
                            userIds.add(groupMember.getUserId());
                        }
                    }
                }
                mCallMemberResult.onGotMemberList(userIds);
            }

            @Override
            public void onError(String errString) {
                mCallMemberResult.onGotMemberList(null);
            }
        });
    }
    //CallKit end 4

    @Override
    public void onClick(View v) {
        enterSettingActivity();
    }

    @Override
    public void onHeadLeftButtonClick(View v) {
        if (fragment != null && !fragment.onBackPressed()) {
            if (fragment.isLocationSharing()) {
                fragment.showQuitLocationSharingDialog(this);
                return;
            }
            hintKbTwo();
            if (isFromPush) {
                isFromPush = false;
                startActivity(new Intent(this, MainActivity.class));
            }
            if (mConversationType.equals(Conversation.ConversationType.CHATROOM)
                    || mConversationType.equals(Conversation.ConversationType.CUSTOMER_SERVICE)) {
                SealAppContext.getInstance().popActivity(this);
            } else {
                SealAppContext.getInstance().popAllActivity();
            }
        }
    }
}
