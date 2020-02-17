package com.bcr.jianxinIM.im.ui.activity;

import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.bcr.jianxinIM.R;
import com.bcr.jianxinIM.im.SealAppContext;
import com.bcr.jianxinIM.im.SealUserInfoManager;
import com.bcr.jianxinIM.im.server.SealAction;
import com.bcr.jianxinIM.im.server.broadcast.BroadcastManager;
import com.bcr.jianxinIM.im.server.network.async.AsyncTaskManager;
import com.bcr.jianxinIM.im.server.network.async.OnDataListener;
import com.bcr.jianxinIM.im.server.network.http.HttpException;
import com.bcr.jianxinIM.im.server.response.GetFriendInfoByIDResponse;
import com.bcr.jianxinIM.im.server.utils.NToast;
import com.bcr.jianxinIM.im.server.widget.LoadDialog;
import com.bcr.jianxinIM.util.ImageUtils;
import com.bcr.jianxinIM.util.SharedPreferencesUtil;
import com.bcr.jianxinIM.view.CircleImageView;
import com.bcr.jianxinIM.view.CommonDialog;
import com.orhanobut.logger.Logger;

import java.util.List;

import io.rong.imageloader.core.ImageLoader;
import io.rong.imkit.RongIM;
import io.rong.imlib.RongIMClient;
import io.rong.imlib.model.UserInfo;

public class BlackListActivity extends BaseActivity {

    private TextView isShowData;
    private ListView mBlackList;
    private static final int REMOVEBLACKLIST = 168;
    private AsyncTaskManager asyncTaskManager;
    private static final int REQUSERINFO = 4234;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_black);
        setTitle(R.string.the_blacklist);
        asyncTaskManager = AsyncTaskManager.getInstance(this);
        initView();
        requestData();

    }

    private void requestData() {
        LoadDialog.show(mContext,"loading...",false);
        SealUserInfoManager.getInstance().getBlackList(new SealUserInfoManager.ResultCallback<List<UserInfo>>() {
            @Override
            public void onSuccess(List<UserInfo> userInfoList) {
                LoadDialog.dismiss(mContext);
                if (userInfoList != null) {
                    if (userInfoList.size() > 0) {
                        MyBlackListAdapter adapter = new MyBlackListAdapter(userInfoList);
                        mBlackList.setAdapter(adapter);
                    } else {
                        isShowData.setVisibility(View.VISIBLE);
                    }
                }
                mBlackList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        CommonDialog commonDialog = new CommonDialog.Builder()
                                .setContentMessage("是否将好友移除黑名单？")
                                .setDialogButtonClickListener(new CommonDialog.OnDialogButtonClickListener() {
                                    @Override
                                    public void onPositiveClick(View v, Bundle bundle) {
                                        RongIM.getInstance().removeFromBlacklist(userInfoList.get(position).getUserId(), new RongIMClient.OperationCallback() {
                                            @Override
                                            public void onSuccess() {
                                                asyncTaskManager.request(REMOVEBLACKLIST, new OnDataListener() {
                                                    @Override
                                                    public Object doInBackground(int requestCode, String parameter) throws HttpException {
                                                        return new SealAction(BlackListActivity.this).removeFromBlackList(SharedPreferencesUtil.getString(BlackListActivity.this,"userId",""),userInfoList.get(position).getUserId());
                                                    }

                                                    @Override
                                                    public void onSuccess(int requestCode, Object result) {
                                                        SealUserInfoManager.getInstance().deleteBlackList(userInfoList.get(position).getUserId());
                                                        NToast.shortToast(BlackListActivity.this, "移除成功");
                                                        finish();
                                                    }

                                                    @Override
                                                    public void onFailure(int requestCode, int state, Object result) {

                                                    }
                                                });
                                            }

                                            @Override
                                            public void onError(RongIMClient.ErrorCode errorCode) {
                                                NToast.shortToast(BlackListActivity.this, "移除失败");
                                            }
                                        });
                                    }

                                    @Override
                                    public void onNegativeClick(View v, Bundle bundle) {
                                    }
                                })
                                .build();
                        commonDialog.show(getSupportFragmentManager(), null);
                    }
                });



            }

            @Override
            public void onError(String errString) {
                LoadDialog.dismiss(mContext);
            }
        });
    }

    private void initView() {
        isShowData = (TextView) findViewById(R.id.blacklsit_show_data);
        mBlackList = (ListView) findViewById(R.id.blacklsit_list);
    }

    class MyBlackListAdapter extends BaseAdapter {

        private List<UserInfo> userInfoList;

        public MyBlackListAdapter(List<UserInfo> dataList) {
            this.userInfoList = dataList;
        }

        @Override
        public int getCount() {
            return userInfoList.size();
        }

        @Override
        public Object getItem(int position) {
            return userInfoList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder viewHolder;
            UserInfo userInfo = userInfoList.get(position);
            if (convertView == null) {
                viewHolder = new ViewHolder();
                convertView = LayoutInflater.from(mContext).inflate(R.layout.black_item_new, parent, false);
                viewHolder.mName = (TextView) convertView.findViewById(R.id.blackname);
                viewHolder.mHead = (CircleImageView) convertView.findViewById(R.id.blackuri);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }
            AsyncTaskManager.getInstance(mContext).request(REQUSERINFO, new OnDataListener() {
                @Override
                public Object doInBackground(int requestCode, String parameter) throws HttpException {
                    return new SealAction(mContext).getFriendInfoByID(SharedPreferencesUtil.getString(mContext,"userId",""),userInfo.getUserId());
                }

                @Override
                public void onSuccess(int requestCode, Object result) {
                    if (result != null) {
                        GetFriendInfoByIDResponse getFriendInfoByIDResponse= (GetFriendInfoByIDResponse) result;
                        if(TextUtils.isEmpty(getFriendInfoByIDResponse.getResultData().getRemark())){
                            viewHolder.mName.setText(getFriendInfoByIDResponse.getResultData().getUserNickName());
                        }else {
                            viewHolder.mName.setText(getFriendInfoByIDResponse.getResultData().getRemark());
                        }
                    }
                }

                @Override
                public void onFailure(int requestCode, int state, Object result) {

                }
            });
            String portraitUri = SealUserInfoManager.getInstance().getPortraitUri(userInfo);
            ImageLoader.getInstance().displayImage(portraitUri, viewHolder.mHead);
            return convertView;
        }


        class ViewHolder {
            CircleImageView mHead;
            TextView mName;
        }
    }
}
