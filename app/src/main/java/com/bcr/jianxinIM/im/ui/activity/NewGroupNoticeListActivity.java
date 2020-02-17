package com.bcr.jianxinIM.im.ui.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import com.bcr.jianxinIM.R;
import com.bcr.jianxinIM.im.SealAppContext;
import com.bcr.jianxinIM.im.server.broadcast.BroadcastManager;
import com.bcr.jianxinIM.im.server.network.http.HttpException;
import com.bcr.jianxinIM.im.server.response.AgreeGroupNoticeResponse;
import com.bcr.jianxinIM.im.server.response.UserRelationshipResponse;
import com.bcr.jianxinIM.im.server.response.getGroupNoticeResponse;
import com.bcr.jianxinIM.im.server.utils.CommonUtils;
import com.bcr.jianxinIM.im.server.utils.NToast;
import com.bcr.jianxinIM.im.server.widget.LoadDialog;
import com.bcr.jianxinIM.im.ui.adapter.NewGroupNoticeListAdapter;
import com.bcr.jianxinIM.util.SharedPreferencesUtil;
import com.orhanobut.logger.Logger;

import java.util.List;

import io.rong.imkit.RongIM;
import io.rong.imlib.RongIMClient;
import io.rong.imlib.model.Conversation;

public class NewGroupNoticeListActivity extends BaseActivity implements NewGroupNoticeListAdapter.OnItemButtonClick {

    private static final int GET_GROUP_ALL =218 ;
    private static final int AGREE_GROUP =219 ;
    private NewGroupNoticeListAdapter adapter;
    private ListView shipListView;
    private TextView isData;
    private int index;
    private getGroupNoticeResponse groupNoticeResponse;
    private String userId;
    private String groupId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_group_notice_list);
        initView();
        if (!CommonUtils.isNetworkConnected(mContext)) {
            NToast.shortToast(mContext, R.string.check_network);
            return;
        }


    }
    public  void onrefrsh(){
        LoadDialog.show(mContext,"loading...",false);
        request(GET_GROUP_ALL);
        adapter = new NewGroupNoticeListAdapter(mContext);
        shipListView.setAdapter(adapter);
    }

    @Override
    protected void onResume() {
        super.onResume();
        onrefrsh();
    }

    protected void initView() {
        setTitle("群通知");
        shipListView = (ListView) findViewById(R.id.shiplistview);
        isData = (TextView) findViewById(R.id.isData);
//        Button rightButton = getHeadRightButton();
//        rightButton.setBackgroundDrawable(getResources().getDrawable(R.drawable.de_address_new_friend));
//        rightButton.setOnClickListener(this);
    }
    @Override
    public Object doInBackground(int requestCode, String id) throws HttpException {
        switch (requestCode) {
            case GET_GROUP_ALL:
                return action.GetGroupRequest(SharedPreferencesUtil.getString(mContext,"userId",""));
            case AGREE_GROUP:
                return action.AgreeGroup(groupId,userId);
        }
        return super.doInBackground(requestCode, id);
    }

    @Override
    public void onSuccess(int requestCode, Object result) {
        if (result != null) {
            switch (requestCode) {
                case GET_GROUP_ALL:
                    LoadDialog.dismiss(mContext);
                    groupNoticeResponse= (getGroupNoticeResponse) result;
                    if(groupNoticeResponse.isSuccess()==true){
                    if (groupNoticeResponse.getResultData().size() == 0) {
                        isData.setVisibility(View.VISIBLE);
                        LoadDialog.dismiss(mContext);
                        return;
                    }
                    adapter.removeAll();
                    adapter.addData(groupNoticeResponse.getResultData());
                    adapter.notifyDataSetChanged();
                    adapter.setOnItemButtonClick(this);
                    LoadDialog.dismiss(mContext);
                        clearMessage(groupNoticeResponse);
                        BroadcastManager.getInstance(mContext).sendBroadcast(SealAppContext.UPDATE_RED_DOT);
                        BroadcastManager.getInstance(mContext).sendBroadcast(SealAppContext.CLEAR_RED_DOT);
                    }
                    else {
                        LoadDialog.dismiss(mContext);
                        NToast.shortToast(mContext,groupNoticeResponse.getMessage());
                    }
                    break;
                case AGREE_GROUP:
                    AgreeGroupNoticeResponse agreeGroupNoticeResponse= (AgreeGroupNoticeResponse) result;
                    if(agreeGroupNoticeResponse.isSuccess()==true){
                        NToast.shortToast(mContext,agreeGroupNoticeResponse.getMessage());
                        onrefrsh();
                    }else {
                        NToast.shortToast(mContext,agreeGroupNoticeResponse.getMessage());
                    }
                    break;
            }
        }
        super.onSuccess(requestCode, result);
    }
    private void clearMessage(getGroupNoticeResponse groupNoticeResponse) {
        List<getGroupNoticeResponse.ResultDataBean> resultDataBean=  groupNoticeResponse.getResultData();
        for(int i=0;i<resultDataBean.size();i++){
            if(resultDataBean.get(i).getState()==10){
                clear(resultDataBean.get(i).getUserId());}
        }

    }
    private void clear(String userId) {
        RongIM.getInstance().clearMessagesUnreadStatus(Conversation.ConversationType.SYSTEM,userId ,new RongIMClient.ResultCallback<Boolean>() {
            @Override
            public void onSuccess(Boolean aBoolean) {
                Logger.d("是否清除：--》"+aBoolean);
            }

            @Override
            public void onError(RongIMClient.ErrorCode errorCode) {

            }
        });
    }
    @Override
    public boolean onButtonClick(int position, View view, int status) {
        index = position;
        switch (status) {
            case 10: //收到了好友邀请
                if (!CommonUtils.isNetworkConnected(mContext)) {
                    NToast.shortToast(mContext, R.string.check_network);
                    break;
                }
                //LoadDialog.show(mContext,"loading...",false);
//                friendId = null;
                userId = groupNoticeResponse.getResultData().get(position).getUserId();
                groupId=groupNoticeResponse.getResultData().get(position).getGroupId();
                request(AGREE_GROUP);
                break;
            case 30: // 已同意的群通知
                break;
        }
        return false;
    }
}
