package com.bcr.jianxinIM.im.ui.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.bcr.jianxinIM.R;
import com.bcr.jianxinIM.im.SealAppContext;
import com.bcr.jianxinIM.im.SealUserInfoManager;
import com.bcr.jianxinIM.im.db.Friend;
import com.bcr.jianxinIM.im.server.broadcast.BroadcastManager;
import com.bcr.jianxinIM.im.server.network.http.HttpException;
import com.bcr.jianxinIM.im.server.pinyin.CharacterParser;
import com.bcr.jianxinIM.im.server.response.AgreeFriendsResponse;
import com.bcr.jianxinIM.im.server.response.UserRelationshipResponse;
import com.bcr.jianxinIM.im.server.utils.CommonUtils;
import com.bcr.jianxinIM.im.server.utils.NToast;
import com.bcr.jianxinIM.im.server.widget.LoadDialog;
import com.bcr.jianxinIM.im.ui.adapter.NewFriendListAdapter;
import com.bcr.jianxinIM.util.SharedPreferencesUtil;
import com.orhanobut.logger.Logger;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import io.rong.imkit.RongIM;
import io.rong.imlib.RongIMClient;
import io.rong.imlib.model.Conversation;


public class NewFriendListActivity extends BaseActivity implements NewFriendListAdapter.OnItemButtonClick, View.OnClickListener {

    private static final int GET_ALL = 11;
    private static final int AGREE_FRIENDS = 12;
    public static final int FRIEND_LIST_REQUEST_CODE = 1001;
    private ListView shipListView;
    private NewFriendListAdapter adapter;
    private String friendId;
    private TextView isData;
    private UserRelationshipResponse userRelationshipResponse;
    private SharedPreferences sp;
    private SharedPreferences.Editor editor;
    public String userId;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_friendlist);
        sp = getSharedPreferences("config", MODE_PRIVATE);
        editor = sp.edit();
        userId=sp.getString("userId","");
        initView();
        if (!CommonUtils.isNetworkConnected(mContext)) {
            NToast.shortToast(mContext, R.string.check_network);
            return;
        }

    }

    @Override
    protected void onResume() {
        super.onResume();
        onRefresh();
    }
    public  void onRefresh(){
        LoadDialog.show(mContext,"loading...",false);
        request(GET_ALL);
        adapter = new NewFriendListAdapter(mContext);
        shipListView.setAdapter(adapter);

    }

    protected void initView() {
        setTitle(R.string.new_friends);
        shipListView = (ListView) findViewById(R.id.shiplistview);
        isData = (TextView) findViewById(R.id.isData);
        Button rightButton = getHeadRightButton();
        rightButton.setBackgroundDrawable(getResources().getDrawable(R.drawable.de_address_new_friend));
        rightButton.setOnClickListener(this);
    }


    @Override
    public Object doInBackground(int requestCode, String id) throws HttpException {
        switch (requestCode) {
            case GET_ALL:
                return action.getAllUserRelationship(SharedPreferencesUtil.getString(NewFriendListActivity.this,"userId",""));
            case AGREE_FRIENDS:
                return action.agreeFriends(SharedPreferencesUtil.getString(NewFriendListActivity.this,"userId",""),friendId);
        }
        return super.doInBackground(requestCode, id);
    }


    @Override
    @SuppressWarnings("unchecked")
    public void onSuccess(int requestCode, Object result) {
        if (result != null) {
            switch (requestCode) {
                case GET_ALL:
                    userRelationshipResponse = (UserRelationshipResponse) result;
                    if(userRelationshipResponse.isSuccess()==true){
                    if (userRelationshipResponse.getResultData().size() == 0) {
                        isData.setVisibility(View.VISIBLE);
                        LoadDialog.dismiss(mContext);
                        return;
                    }

//                    Collections.sort(userRelationshipResponse.getResultData(), new Comparator<UserRelationshipResponse.ResultDataBean>() {
//
//                        @Override
//                        public int compare(UserRelationshipResponse.ResultDataBean lhs, UserRelationshipResponse.ResultDataBean rhs) {
//                            Date date1 = stringToDate(lhs);
//                            Date date2 = stringToDate(rhs);
//                            if (date1.before(date2)) {
//                                return 1;
//                            }
//                            return -1;
//                        }
//                    });
                    adapter.removeAll();
                    adapter.addData(userRelationshipResponse.getResultData());
                    adapter.notifyDataSetChanged();
                    adapter.setOnItemButtonClick(this);
                    LoadDialog.dismiss(mContext);
                    clearMessage(userRelationshipResponse);
                        BroadcastManager.getInstance(mContext).sendBroadcast(SealAppContext.UPDATE_RED_DOT);
                        BroadcastManager.getInstance(mContext).sendBroadcast(SealAppContext.CLEAR_RED_DOT);
                    }
                    else {
                        NToast.shortToast(mContext,userRelationshipResponse.getMessage());
                    }
                    break;
                case AGREE_FRIENDS:
                    AgreeFriendsResponse afres = (AgreeFriendsResponse) result;
                    if (afres.isSuccess() == true) {
                        UserRelationshipResponse.ResultDataBean bean = userRelationshipResponse.getResultData().get(index);
                        SealUserInfoManager.getInstance().addFriend(new Friend(bean.getUserId(),
                                bean.getUserNickName(),
                                Uri.parse(bean.getUserPic()),
                                bean.getRemark(),
                                String.valueOf(bean.getStatus()),
                                null,
                                null,
                                null,
                                CharacterParser.getInstance().getSpelling(bean.getUserNickName()),
                                CharacterParser.getInstance().getSpelling(bean.getRemark())));
                        // 通知好友列表刷新数据
                        NToast.shortToast(mContext, R.string.agreed_friend);
                        LoadDialog.dismiss(mContext);
                        BroadcastManager.getInstance(mContext).sendBroadcast(SealAppContext.UPDATE_FRIEND);
                        BroadcastManager.getInstance(mContext).sendBroadcast(SealAppContext.UPDATE_RED_DOT);
                        BroadcastManager.getInstance(mContext).sendBroadcast(SealAppContext.CLEAR_RED_DOT);

                        //request(GET_ALL); //刷新 UI 按钮
                        //adapter.notifyDataSetChanged();
                        onRefresh();
                    }

            }
        }
    }

    private void clearMessage(UserRelationshipResponse userRelationshipResponse) {
        List<UserRelationshipResponse.ResultDataBean> resultDataBean=  userRelationshipResponse.getResultData();
        for(int i=0;i<resultDataBean.size();i++){
            if(resultDataBean.get(i).getStatus()==11){
            clear(resultDataBean.get(i).getUserId());}
            if (resultDataBean.get(i).getStatus()==20){
                clear(resultDataBean.get(i).getUserId());
            }
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
    public void onFailure(int requestCode, int state, Object result) {
        switch (requestCode) {
            case GET_ALL:
                break;

        }
    }


    @Override
    protected void onDestroy() {
        if (adapter != null) {
            adapter = null;
        }
        super.onDestroy();
    }

    private int index;

    @Override
    public boolean onButtonClick(int position, View view, int status) {
        index = position;
        switch (status) {
            case 11: //收到了好友邀请
                if (!CommonUtils.isNetworkConnected(mContext)) {
                    NToast.shortToast(mContext, R.string.check_network);
                    break;
                }
                LoadDialog.show(mContext,"loading...",false);
//                friendId = null;
                friendId = userRelationshipResponse.getResultData().get(position).getUserId();
                request(AGREE_FRIENDS);
                break;
            case 10: // 发出了好友邀请
                break;
            case 21: // 忽略好友邀请
                break;
            case 20: // 已是好友
                break;
            case 30: // 删除了好友关系
                break;
        }
        return false;
    }

    private Date stringToDate(UserRelationshipResponse.ResultDataBean resultDataBean) {
        String updatedAt = resultDataBean.getUpdateDate();
        String updatedAtDateStr = updatedAt.substring(0, 10) + " " + updatedAt.substring(11, 16);
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm");
        Date updateAtDate = null;
        try {
            updateAtDate = simpleDateFormat.parse(updatedAtDateStr);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return updateAtDate;
    }

    @Override
    public void onClick(View v) {
        Intent intent = new Intent(NewFriendListActivity.this, SearchFriendActivity.class);
        startActivityForResult(intent, FRIEND_LIST_REQUEST_CODE);
    }
}
