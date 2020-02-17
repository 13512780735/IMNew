package com.bcr.jianxinIM.im.ui.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.widget.SwipeRefreshLayout;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.bcr.jianxinIM.im.server.SealAction;
import com.bcr.jianxinIM.im.server.network.async.AsyncTaskManager;
import com.bcr.jianxinIM.im.server.network.async.OnDataListener;
import com.bcr.jianxinIM.im.server.network.http.HttpException;
import com.bcr.jianxinIM.im.server.response.GetFriendInfoByIDResponse;
import com.bcr.jianxinIM.im.server.response.GetGroupResponse;
import com.bcr.jianxinIM.util.ImageUtils;
import com.bcr.jianxinIM.util.SharedPreferencesUtil;
import com.orhanobut.logger.Logger;
import com.bcr.jianxinIM.R;
import com.bcr.jianxinIM.im.SealConst;
import com.bcr.jianxinIM.im.SealUserInfoManager;
import com.bcr.jianxinIM.im.db.Groups;
import com.bcr.jianxinIM.im.server.broadcast.BroadcastManager;
import com.bcr.jianxinIM.im.server.widget.ClearWriteEditText;
import com.bcr.jianxinIM.view.CircleImageView;

import java.util.ArrayList;
import java.util.List;

import io.rong.imageloader.core.ImageLoader;
import io.rong.imkit.RongIM;
import io.rong.imlib.model.UserInfo;

/**
 * Created by AMing on 16/3/8.
 * Company RongCloud
 */
public class GroupListActivity extends BaseActivity implements SwipeRefreshLayout.OnRefreshListener {

    private static final int GETGROUPLIST = 253;
    private ListView mGroupListView;
    private GroupAdapter adapter;
    private TextView mNoGroups;
    private ClearWriteEditText mSearch;
    private List<GetGroupResponse.ResultDataBean> mList;
    private TextView mTextView;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private List<GetGroupResponse.ResultDataBean> mLists;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fr_group_list);
        setTitle(R.string.my_groups);
        mGroupListView = (ListView) findViewById(R.id.group_listview);
        mNoGroups = (TextView) findViewById(R.id.show_no_group);
        mSwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipe);
        mSearch = (ClearWriteEditText) findViewById(R.id.group_search);
        mTextView = (TextView)findViewById(R.id.foot_group_size);
       // regSwipeClick();
        mSwipeRefreshLayout.setOnRefreshListener(this);
        mSwipeRefreshLayout.setColorSchemeColors(Color.rgb(47, 223, 189));
    }
//    private void regSwipeClick() {
//        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
//            public void onRefresh() {
//               // updateUI();
//                //发送一个延时1秒的handler信息
//                //handler.sendEmptyMessageDelayed(199,1000);
//            }
//        });
//    }

    @Override
    protected void onResume() {
        initData();
        BroadcastManager.getInstance(mContext).addAction(SealConst.GROUP_LIST_UPDATE, new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                Logger.d("接收信息");
                initData();
            }
        });
        super.onResume();
    }

    @Override
    public void onRefresh() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                initData();
                mSwipeRefreshLayout.setRefreshing(false);
            }
        }, 1500);
    }

    private void initData() {

        AsyncTaskManager.getInstance(mContext).request(GETGROUPLIST, new OnDataListener() {
            @Override
            public Object doInBackground(int requestCode, String parameter) throws HttpException {
                return new SealAction(mContext).getGroups(SharedPreferencesUtil.getString(mContext,"userId",""));
            }

            @Override
            public void onSuccess(int requestCode, Object result) {
                if (result != null) {
                    GetGroupResponse getGroupResponse= (GetGroupResponse) result;
                    if(getGroupResponse.isSuccess()==true){
                        if(mList!=null){
                            mList.clear();
                            mTextView.setVisibility(View.GONE);
                        }
                        mLists = getGroupResponse.getResultData();
                        if(mLists != null && mLists.size() > 0){
                            mList=mLists;
                            adapter = new GroupAdapter(mContext, mList);
                            mGroupListView.setAdapter(adapter);
                            mNoGroups.setVisibility(View.GONE);
                            mTextView.setVisibility(View.VISIBLE);
                            mTextView.setText(getString(R.string.ac_group_list_group_number, mList.size()));
                            adapter.notifyDataSetChanged();
                            mGroupListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                @Override
                                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                    GetGroupResponse.ResultDataBean bean = (GetGroupResponse.ResultDataBean) adapter.getItem(position);
                                    RongIM.getInstance().startGroupChat(GroupListActivity.this, bean.getGroupId(), bean.getNickName());
                                }
                            });


                            mSearch.addTextChangedListener(new TextWatcher() {
                                @Override
                                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                                }

                                @Override
                                public void onTextChanged(CharSequence s, int start, int before, int count) {
                                    filterData(s.toString());
                                }

                                @Override
                                public void afterTextChanged(Editable s) {
                                }
                            });

                        }else {
                            mGroupListView.setVisibility(View.GONE);
                            mTextView.setVisibility(View.GONE);
                            mNoGroups.setVisibility(View.VISIBLE);
                        }
                    }
                }
                mSwipeRefreshLayout.setRefreshing(false);
            }

            @Override
            public void onFailure(int requestCode, int state, Object result) {
                mSwipeRefreshLayout.setRefreshing(false);
            }
        });

    }

    private void filterData(String s) {
        List<GetGroupResponse.ResultDataBean> filterDataList = new ArrayList<>();
        if (TextUtils.isEmpty(s)) {
            filterDataList = mList;
        } else {
            for (GetGroupResponse.ResultDataBean groups : mList) {
                if (groups.getNickName().contains(s)) {
                    filterDataList.add(groups);
                }
            }
        }
        adapter.updateListView(filterDataList);
        mTextView.setText(getString(R.string.ac_group_list_group_number, filterDataList.size()));
    }


    class GroupAdapter extends BaseAdapter {

        private Context context;

        private List<GetGroupResponse.ResultDataBean> list;

        public GroupAdapter(Context context, List<GetGroupResponse.ResultDataBean> list) {
            this.context = context;
            this.list = list;
        }

        /**
         * 传入新的数据 刷新UI的方法
         */
        public void updateListView(List<GetGroupResponse.ResultDataBean> list) {
            this.list = list;
            notifyDataSetChanged();
        }

        @Override
        public int getCount() {
            if (list != null) return list.size();
            return 0;
        }

        @Override
        public Object getItem(int position) {
            if (list == null)
                return null;

            if (position >= list.size())
                return null;

            return list.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder viewHolder;
            final GetGroupResponse.ResultDataBean mContent = list.get(position);
            if (convertView == null) {
                viewHolder = new ViewHolder();
                convertView = LayoutInflater.from(context).inflate(R.layout.group_item_new, parent, false);
                viewHolder.tvTitle = (TextView) convertView.findViewById(R.id.groupname);
                viewHolder.mImageView = (CircleImageView) convertView.findViewById(R.id.groupuri);
                viewHolder.groupId = (TextView) convertView.findViewById(R.id.group_id);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }
            viewHolder.tvTitle.setText(mContent.getNickName());
            if(TextUtils.isEmpty(mContent.getPicture())){
                viewHolder.mImageView.setImageResource(R.mipmap.icon_wangwang);
            }else {
                ImageLoader.getInstance().displayImage(mContent.getPicture(), viewHolder.mImageView);
            }


//            String portraitUri = SealUserInfoManager.getInstance().getPortraitUri(mContent);
//            ImageLoader.getInstance().displayImage(portraitUri, viewHolder.mImageView);
            if (context.getSharedPreferences("config", MODE_PRIVATE).getBoolean("isDebug", false)) {
                viewHolder.groupId.setVisibility(View.VISIBLE);
                viewHolder.groupId.setText(mContent.getGroupId());
            }
            return convertView;
        }


        class ViewHolder {
            /**
             * 昵称
             */
            TextView tvTitle;
            /**
             * 头像
             */
            CircleImageView mImageView;
            /**
             * userId
             */
            TextView groupId;
        }
    }

    @Override
    public void onBackPressed() {
        finish();
        super.onBackPressed();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        BroadcastManager.getInstance(mContext).destroy(SealConst.GROUP_LIST_UPDATE);
    }


}
