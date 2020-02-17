package com.bcr.jianxinIM.im.ui.adapter;

import android.content.Context;
import android.net.Uri;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.SectionIndexer;
import android.widget.TextView;

import com.bcr.jianxinIM.R;
import com.bcr.jianxinIM.im.SealUserInfoManager;
import com.bcr.jianxinIM.im.db.Friend;
import com.bcr.jianxinIM.im.server.SealAction;
import com.bcr.jianxinIM.im.server.broadcast.BroadcastManager;
import com.bcr.jianxinIM.im.server.network.async.AsyncTaskManager;
import com.bcr.jianxinIM.im.server.network.async.OnDataListener;
import com.bcr.jianxinIM.im.server.network.http.HttpException;
import com.bcr.jianxinIM.im.server.response.GetFriendInfoByIDResponse;
import com.bcr.jianxinIM.im.server.response.GetUserInfoByIdResponse;
import com.bcr.jianxinIM.util.SharedPreferencesUtil;
import com.bcr.jianxinIM.view.CircleImageView;
import com.orhanobut.logger.Logger;

import java.util.List;

import io.rong.imageloader.core.ImageLoader;
import io.rong.imkit.RongIM;
import io.rong.imlib.model.UserInfo;

/**
 * Created by AMing on 16/1/14.
 * Company RongCloud
 */
public class FriendListAdapter extends BaseAdapter implements SectionIndexer {

    private Context context;
    private static final int REQUSERINFO = 4234;
    private List<Friend> list;
    private String userName;

    public FriendListAdapter(Context context, List<Friend> list) {
        this.context = context;
        this.list = list;
    }


    /**
     * 传入新的数据 刷新UI的方法
     */
    public void updateListView(List<Friend> list) {
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
        final Friend mContent = list.get(position);
        if (convertView == null) {
            viewHolder = new ViewHolder();
            convertView = LayoutInflater.from(context).inflate(R.layout.friend_item, parent, false);
            viewHolder.tvTitle = (TextView) convertView.findViewById(R.id.friendname);
            viewHolder.tvLetter = (TextView) convertView.findViewById(R.id.catalog);
            viewHolder.mImageView = (CircleImageView) convertView.findViewById(R.id.frienduri);
            viewHolder.tvUserId = (TextView) convertView.findViewById(R.id.friend_id);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        //根据position获取分类的首字母的Char ascii值
        int section = getSectionForPosition(position);
        //如果当前位置等于该分类首字母的Char的位置 ，则认为是第一次出现
        if (position == getPositionForSection(section)) {
            viewHolder.tvLetter.setVisibility(View.VISIBLE);
            String letterFirst = mContent.getLetters();
            if (!TextUtils.isEmpty(letterFirst)) {
                if (!isLetterDigitOrChinese(letterFirst)) {
                    letterFirst = "#";
                }else {
                    letterFirst = String.valueOf(letterFirst.toUpperCase().charAt(0));
                }
            }
            viewHolder.tvLetter.setText(letterFirst);
        } else {
            viewHolder.tvLetter.setVisibility(View.GONE);
        }
        if (mContent.isExitsDisplayName()) {
            viewHolder.tvTitle.setText(this.list.get(position).getDisplayName());
        } else {
            viewHolder.tvTitle.setText(this.list.get(position).getName());
        }
        String portraitUri = SealUserInfoManager.getInstance().getPortraitUri(list.get(position));

        Logger.d("用戶ID:-->"+list.get(position).getUserId());

        AsyncTaskManager.getInstance(context).request(REQUSERINFO, new OnDataListener() {
            @Override
            public Object doInBackground(int requestCode, String parameter) throws HttpException {
                return new SealAction(context).getFriendInfoByID(SharedPreferencesUtil.getString(context,"userId",""),list.get(position).getUserId());
            }

            @Override
            public void onSuccess(int requestCode, Object result) {
                if (result != null) {
                    GetFriendInfoByIDResponse getFriendInfoByIDResponse= (GetFriendInfoByIDResponse) result;
                    if(TextUtils.isEmpty(getFriendInfoByIDResponse.getResultData().getRemark())){
                        userName=getFriendInfoByIDResponse.getResultData().getUserNickName();
                    }else {
                        userName=getFriendInfoByIDResponse.getResultData().getRemark();
                    }
                    RongIM.getInstance().refreshUserInfoCache(new UserInfo(getFriendInfoByIDResponse.getResultData().getFriendId(), userName, Uri.parse(getFriendInfoByIDResponse.getResultData().getUserPic())));
                  //  BroadcastManager.getInstance(context).sendBroadcast(UPDATE_FRIEND);
//                    yourEncode=getUserInfoByIdResponse.getResultData().getEncode();
//                    String  userId=SharedPreferencesUtil.getString(mContext,"userId","");
//                    if(Long.parseLong(userId)-Long.parseLong(targetId)>0){
//                        secretKey=SharedPreferencesUtil.getString(mContext,"myEncode","")+yourEncode+SharedPreferencesUtil.getString(mContext,"PublicEncode","");
//                    }else {
//                        secretKey=yourEncode+SharedPreferencesUtil.getString(mContext,"myEncode","")+SharedPreferencesUtil.getString(mContext,"PublicEncode","");
//                    }
//                    SharedPreferencesUtil.putString(mContext,"secretKey",secretKey);

                    if(!TextUtils.isEmpty(getFriendInfoByIDResponse.getResultData().getUserPic())){

                        Logger.d("頭像：--》"+getFriendInfoByIDResponse.getResultData().getUserPic());
                        ImageLoader.getInstance().displayImage(getFriendInfoByIDResponse.getResultData().getUserPic(), viewHolder.mImageView);

                    } else viewHolder.mImageView.setImageDrawable(context.getResources().getDrawable(R.mipmap.icon_wangwang));

                }
            }

            @Override
            public void onFailure(int requestCode, int state, Object result) {

            }
        });



        //ImageLoader.getInstance().displayImage(portraitUri, viewHolder.mImageView, MyApplication.getOptions());
        if (context.getSharedPreferences("config", Context.MODE_PRIVATE).getBoolean("isDebug", false)) {
            viewHolder.tvUserId.setVisibility(View.VISIBLE);
            viewHolder.tvUserId.setText(list.get(position).getUserId());
        }
        return convertView;
    }

    @Override
    public Object[] getSections() {
        return new Object[0];
    }

    @Override
    public int getPositionForSection(int sectionIndex) {
        for (int i = 0; i < getCount(); i++) {
            String sortStr = list.get(i).getLetters();
            char firstChar = sortStr.charAt(0);
            if (firstChar == sectionIndex) {
                return i;
            }
        }

        return -1;
    }

    @Override
    public int getSectionForPosition(int position) {
        return list.get(position).getLetters().charAt(0);
    }


    final static class ViewHolder {
        /**
         * 首字母
         */
        TextView tvLetter;
        /**
         * 昵称
         */
        TextView tvTitle;
        /**
         * 头像
         */
        CircleImageView mImageView;
        /**
         * userid
         */
        TextView tvUserId;
    }

    private boolean isLetterDigitOrChinese(String str) {
        String regex = "^[a-z0-9A-Z\u4e00-\u9fa5]+$";//其他需要，直接修改正则表达式就好
        return str.matches(regex);
    }
}
