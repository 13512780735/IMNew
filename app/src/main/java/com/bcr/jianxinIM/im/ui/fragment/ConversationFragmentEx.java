package com.bcr.jianxinIM.im.ui.fragment;

import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.RequiresApi;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.bcr.jianxinIM.im.SealCSEvaluateInfo;
import com.bcr.jianxinIM.im.message.CustomizeMessage;
import com.bcr.jianxinIM.im.model.SealCSEvaluateItem;
import com.bcr.jianxinIM.im.server.SealAction;
import com.bcr.jianxinIM.im.server.network.async.AsyncTaskManager;
import com.bcr.jianxinIM.im.server.network.async.OnDataListener;
import com.bcr.jianxinIM.im.server.network.http.HttpException;
import com.bcr.jianxinIM.im.server.response.CheckOnlineRespone;
import com.bcr.jianxinIM.im.server.response.GetGroupInfoResponse;
import com.bcr.jianxinIM.im.server.response.GetUserInfoByIdResponse;
import com.bcr.jianxinIM.im.server.response.PushMessageRespone;
import com.bcr.jianxinIM.im.ui.activity.ReadReceiptDetailActivity;
import com.bcr.jianxinIM.im.ui.widget.BottomEvaluateDialog;
import com.bcr.jianxinIM.util.AndroidDes3Util;
import com.bcr.jianxinIM.util.SharedPreferencesUtil;
import com.orhanobut.logger.Logger;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;

import io.rong.common.RLog;
import io.rong.imkit.RongExtension;
import io.rong.imkit.RongIM;
import io.rong.imkit.fragment.ConversationFragment;
import io.rong.imkit.mention.RongMentionManager;
import io.rong.imkit.plugin.IPluginModule;
import io.rong.imlib.CustomServiceConfig;
import io.rong.imlib.IRongCallback;
import io.rong.imlib.RongIMClient;
import io.rong.imlib.cs.CustomServiceManager;
import io.rong.imlib.model.Conversation;
import io.rong.imlib.model.MentionedInfo;
import io.rong.imlib.model.Message;

/**
 * 会话 Fragment 继承自ConversationFragment
 * onResendItemClick: 重发按钮点击事件. 如果返回 false,走默认流程,如果返回 true,走自定义流程
 * onReadReceiptStateClick: 已读回执详情的点击事件.
 * 如果不需要重写 onResendItemClick 和 onReadReceiptStateClick ,可以不必定义此类,直接集成 ConversationFragment 就可以了
 */
public class ConversationFragmentEx extends ConversationFragment {
    private static final int CHECKONLINE =240 ;
    private static final int PUSHMESSAGE =241 ;
    private OnShowAnnounceListener onShowAnnounceListener;
    private BottomEvaluateDialog dialog;
    private List<SealCSEvaluateItem> mEvaluateList;
    private String mTargetId = "";
    private RongExtension rongExtension;
    private ListView listView;
    private Conversation.ConversationType mConversationType;
    private CustomizeMessage textMessage;
    private static final int REQUSERINFO = 4234;
    private String yourEncode;
    private static final int GETGROUPINFO =214 ;
    private String groupEncode;
    private String staus;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        RongIMClient.getInstance().setCustomServiceHumanEvaluateListener(new CustomServiceManager.OnHumanEvaluateListener() {
            @Override
            public void onHumanEvaluate(JSONObject evaluateObject) {
                JSONObject jsonObject = evaluateObject;
                SealCSEvaluateInfo sealCSEvaluateInfo = new SealCSEvaluateInfo(jsonObject);
                mEvaluateList = sealCSEvaluateInfo.getSealCSEvaluateInfoList();
            }
        });

        Intent intent = getActivity().getIntent();

        if (intent == null || intent.getData() == null) {

            return;

        }

        mTargetId = intent.getData().getQueryParameter("targetId");

        mConversationType = Conversation.ConversationType

                .valueOf(intent.getData().getLastPathSegment().toUpperCase(Locale.getDefault()));
        Logger.d("mTargetId:--》"+mTargetId);
        Logger.d("mConversationType:--》"+mConversationType.getName());
        //initGroupInfo();
        if(mConversationType.getName().equals("group")){
            initGroupInfo();
        }else {
            initUserInfo();
            checkOnLine(mTargetId);
        }
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = super.onCreateView(inflater, container, savedInstanceState);
        rongExtension = (RongExtension) v.findViewById(io.rong.imkit.R.id.rc_extension);
        View messageListView = findViewById(v, io.rong.imkit.R.id.rc_layout_msg_list);
        listView = findViewById(messageListView, io.rong.imkit.R.id.rc_list);
        return v;
    }


    /**
     * 獲取群信息
     */
    private void initGroupInfo() {
        AsyncTaskManager.getInstance(getActivity()).request(GETGROUPINFO, new OnDataListener() {
            @Override
            public Object doInBackground(int requestCode, String parameter) throws HttpException {
                return new SealAction(getActivity()).getGroupInfo(mTargetId);
            }

            @Override
            public void onSuccess(int requestCode, Object result) {
                if (result != null) {
                    GetGroupInfoResponse getGroupInfoResponse= (GetGroupInfoResponse) result;
                    if(getGroupInfoResponse.isSuccess()==true){
                    groupEncode=getGroupInfoResponse.getResultData().getEncode();
                    Logger.d("发送消息9977：--》"+ groupEncode);}
                }
            }

            @Override
            public void onFailure(int requestCode, int state, Object result) {

            }
        });

    }

    /**
     * 查询用户信息
     */
    private void initUserInfo() {
        AsyncTaskManager.getInstance(getActivity()).request(REQUSERINFO, new OnDataListener() {
            @Override
            public Object doInBackground(int requestCode, String parameter) throws HttpException {
                return new SealAction(getActivity()).getUserInfoById(mTargetId);
            }

            @Override
            public void onSuccess(int requestCode, Object result) {
                if (result != null) {
                    GetUserInfoByIdResponse getUserInfoByIdResponse= (GetUserInfoByIdResponse) result;
                    yourEncode=getUserInfoByIdResponse.getResultData().getEncode();
                }
            }

            @Override
            public void onFailure(int requestCode, int state, Object result) {

            }
        });

    }



    @Override
    protected void initFragment(Uri uri) {
        super.initFragment(uri);
        if (uri != null) {
            mTargetId = uri.getQueryParameter("targetId");
        }
    }

    @Override
    public void onReadReceiptStateClick(io.rong.imlib.model.Message message) {
        if (message.getConversationType() == Conversation.ConversationType.GROUP) { //目前只适配了群组会话
            Intent intent = new Intent(getActivity(), ReadReceiptDetailActivity.class);
            intent.putExtra("message", message);
            getActivity().startActivity(intent);
        }
    }

    public void onWarningDialog(String msg) {
        String typeStr = getUri().getLastPathSegment();
        if (!typeStr.equals("chatroom")) {
            super.onWarningDialog(msg);
        }
    }

    @Override
    public void onShowAnnounceView(String announceMsg, String announceUrl) {
        if (onShowAnnounceListener != null) {
            onShowAnnounceListener.onShowAnnounceView(announceMsg, announceUrl);
        }
    }

    /**
     * 显示通告栏的监听器
     */
    public interface OnShowAnnounceListener {

        /**
         * 展示通告栏的回调
         *
         * @param announceMsg 通告栏展示内容
         * @param annouceUrl  通告栏点击链接地址，若此参数为空，则表示不需要点击链接，否则点击进入链接页面
         * @return
         */
        void onShowAnnounceView(String announceMsg, String annouceUrl);
    }

    public void setOnShowAnnounceBarListener(OnShowAnnounceListener listener) {
        onShowAnnounceListener = listener;
    }

    public void showStartDialog(final String dialogId) {
        if (dialog != null && dialog.isShowing()) {
            dialog.dismiss();
        }
        dialog = new BottomEvaluateDialog(getActivity(), mEvaluateList);
        dialog.show();
        dialog.setEvaluateDialogBehaviorListener(new BottomEvaluateDialog.OnEvaluateDialogBehaviorListener() {
            @Override
            public void onSubmit(int source, String tagText, CustomServiceConfig.CSEvaSolveStatus resolveStatus, String suggestion) {
                RongIMClient.getInstance().evaluateCustomService(mTargetId, source, resolveStatus, tagText,
                        suggestion, dialogId, null);
                if (dialog != null && getActivity() != null) {
                    getActivity().finish();
                }
            }

            @Override
            public void onCancel() {
                if (dialog != null && getActivity() != null) {
                    getActivity().finish();
                }
            }
        });
    }

    @Override
    public void onShowStarAndTabletDialog(String dialogId) {
        showStartDialog(dialogId);
    }

    @Override
    public void onPause() {
        super.onPause();
        if (getActivity() != null && getActivity().isFinishing()) {
            RongIMClient.getInstance().setCustomServiceHumanEvaluateListener(null);
        }
    }

    @Override
    public void onPluginToggleClick(View v, ViewGroup extensionBoard) {
        if (!rongExtension.isExtensionExpanded()) {
            listView.postDelayed(new Runnable() {
                @Override
                public void run() {
                    listView.requestFocusFromTouch();
                    listView.setSelection(listView.getCount() - listView.getFooterViewsCount() - listView.getHeaderViewsCount());
                }
            }, 100);
        }
    }

    @Override
    public void onEmoticonToggleClick(View v, ViewGroup extensionBoard) {
        if (!rongExtension.isExtensionExpanded()) {
            listView.postDelayed(new Runnable() {
                @Override
                public void run() {
                    listView.requestFocusFromTouch();
                    listView.setSelection(listView.getCount() - listView.getFooterViewsCount() - listView.getHeaderViewsCount());
                }
            }, 100);
        }
    }

    /**
     * 文字消息
     */
    //private final static String secretKey = "123456789012345678901234";
    private static String secretKey="";
    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onSendToggleClick(View v, String text) {
        String myEncode=SharedPreferencesUtil.getString(getActivity(),"myEncode","");
        String PublicEncode=SharedPreferencesUtil.getString(getActivity(),"PublicEncode","");
        String  userId=SharedPreferencesUtil.getString(getActivity(),"userId","");
        if (!TextUtils.isEmpty(text) && !TextUtils.isEmpty(text.trim())) {
            if(!this.rongExtension.isFireStatus()){
                try {
                    Logger.d("mConversationType:-->"+mConversationType);
                    if(mConversationType.getName().equals("group")){
                        secretKey=groupEncode+PublicEncode;
                        textMessage = CustomizeMessage.obtain(AndroidDes3Util.encode(text,secretKey),secretKey);
                        MentionedInfo mentionedInfo = RongMentionManager.getInstance().onSendButtonClick();
                        if (mentionedInfo != null) {
                            textMessage.setMentionedInfo(mentionedInfo);
                        }
                        Message message = Message.obtain(this.mTargetId, mConversationType, textMessage);
                        RongIM.getInstance().sendMessage(message, null, (String)null, (IRongCallback.ISendMessageCallback)null);
                    }else {
                        if(Long.parseLong(userId)-Long.parseLong(mTargetId)>0){
                            secretKey=myEncode+yourEncode+PublicEncode;
                        }else {
                            secretKey=yourEncode+myEncode+PublicEncode;
                        }

                        textMessage = CustomizeMessage.obtain(AndroidDes3Util.encode(text,secretKey),secretKey);
                        Logger.d("加密消息：--》"+AndroidDes3Util.decode(textMessage.getContent(),secretKey));
                        Logger.d("加密消息22：--》"+textMessage.getContent());
                        MentionedInfo mentionedInfo = RongMentionManager.getInstance().onSendButtonClick();
                        if (mentionedInfo != null) {
                            textMessage.setMentionedInfo(mentionedInfo);
                        }
                        Message message = Message.obtain(this.mTargetId, mConversationType, textMessage);
                        RongIM.getInstance().sendMessage(message, null, (String)null, (IRongCallback.ISendMessageCallback)null);

                        //checkOnLine(mTargetId);

                        checkuserOnLine(staus);
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
                 else {
                //checkOnLine(mTargetId);
                checkuserOnLine(staus);
                super.onSendToggleClick(v, text);
            }
        } else {
            RLog.e("ConversationFragment", "text content must not be null");
        }

     //
    }


    private void checkuserOnLine( String staus) {
        if("0".equals(staus)){
            //调用后台服务器推送
            pushMessage(mTargetId);
        }else {
            delayedShow();
            RongIMClient.setReadReceiptListener(new RongIMClient.ReadReceiptListener() {
                @Override
                public void onReadReceiptReceived(final Message message) {
                    id=message.getTargetId();
//                if (Conversation != null && Conversation.getTargetId().equals(message.getTargetId()) && Conversation.getConversationType() == message.getConversationType()) {
//
//                    ReadReceiptMessage content = (ReadReceiptMessage) message.getContent();
//                    long ntfTime = content.getLastMessageSendTime();    //获取发送时间戳
//
//                    //自行进行UI处理，把会话中发送时间戳之前的所有已发送消息状态置为已读
//                }
                }

                @Override
                public void onMessageReceiptRequest(Conversation.ConversationType type, String targetId, String messageUId) {
                }
                @Override
                public void onMessageReceiptResponse(Conversation.ConversationType type, String targetId, String messageUId, HashMap<String, Long> respondUserIdList) {
                }
            });

        }
    }


    private String id;
    /**
     * 检测是否在线
     * @param mTargetId
     */
    private void checkOnLine(String mTargetId) {
        AsyncTaskManager.getInstance(getActivity()).request(CHECKONLINE, new OnDataListener() {


            @Override
            public Object doInBackground(int requestCode, String parameter) throws HttpException {
                return new SealAction(getActivity()).checkOnline(mTargetId);
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
    public void delayedShow(){
        new Handler().postDelayed(new Runnable(){
            public void run() {

                if(id!=null){
                    return;
                }else {
                    Logger.d("执行检测15秒后");
                    //15s后执行代码
                    //调用后台推送
                    pushMessage(mTargetId);
                    id=null;
                }
            }
        }, 15000);
    }
    /**
     * 调用后台推送
     * @param mTargetId
     */
    private void pushMessage(String mTargetId) {
        Logger.d("推送消息111：--》"+mTargetId);
        AsyncTaskManager.getInstance(getActivity()).request(PUSHMESSAGE, new OnDataListener() {


            @Override
            public Object doInBackground(int requestCode, String parameter) throws HttpException {
                return new SealAction(getActivity()).pushMessage(mTargetId);
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

    @Override
    public void onPluginClicked(IPluginModule pluginModule, int position) {
        super.onPluginClicked(pluginModule, position);
        Logger.d("点击事件：--》"+position);

        if("0".equals(staus)){
            pushMessage(mTargetId);
        }


    }

    @Override
    public boolean showMoreClickItem() {
        return true;
    }
}
