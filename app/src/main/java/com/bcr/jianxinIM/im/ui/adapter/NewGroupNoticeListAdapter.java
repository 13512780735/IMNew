package com.bcr.jianxinIM.im.ui.adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bcr.jianxinIM.R;
import com.bcr.jianxinIM.im.server.response.getGroupNoticeResponse;
import com.bcr.jianxinIM.view.CircleImageView;

import io.rong.imageloader.core.ImageLoader;

public class NewGroupNoticeListAdapter extends BaseAdapters {
    public NewGroupNoticeListAdapter(Context context) {
        super(context);
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = mInflater.inflate(R.layout.rs_ada_group_ship, parent, false);
            holder.mName = (TextView) convertView.findViewById(R.id.ship_name);
            holder.mMessage = (TextView) convertView.findViewById(R.id.ship_message);
            holder.mHead = (CircleImageView) convertView.findViewById(R.id.new_header);
            holder.mState = (TextView) convertView.findViewById(R.id.ship_state);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        final getGroupNoticeResponse.ResultDataBean bean = (getGroupNoticeResponse.ResultDataBean) dataSet.get(position);
        holder.mName.setText(bean.getUserNickName());
        String portraitUri = null;
//        if (bean != null ) {
//            // UserRelationshipResponse.ResultDataBean userEntity = bean);
//            portraitUri = SealUserInfoManager.getInstance().getPortraitUri(new UserInfo(
//                    bean.getUserId(), bean.getUserNickName(), Uri.parse(bean.getUserPic())));
//        }
        ImageLoader.getInstance().displayImage(bean.getUserPic(), holder.mHead);
        holder.mMessage.setText(bean.getMessage());
        holder.mState.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mOnItemButtonClick != null) {
                    mOnItemButtonClick.onButtonClick(position, v, bean.getState());
                }
            }
        });

        switch (bean.getState()) {
            case 10: //收到了好友邀请
                holder.mState.setText(R.string.agree);
                holder.mState.setTextColor(Color.WHITE);
                holder.mState.setBackgroundDrawable(mContext.getResources().getDrawable(R.drawable.login_submit_style));
                break;
            case 30: // 发出了好友邀请
                holder.mState.setText(R.string.added);
                holder.mState.setBackgroundDrawable(null);
                break;

        }
        return convertView;
    }

    /**
     * displayName :
     * message : 手机号:18622222222昵称:的用户请求添加你为好友
     * status : 11
     * updatedAt : 2016-01-07T06:22:55.000Z
     * user : {"id":"i3gRfA1ml","nickname":"nihaoa","portraitUri":""}
     */

    class ViewHolder {
        CircleImageView mHead;
        TextView mName;
        TextView mState;
        //        TextView mtime;
        TextView mMessage;
    }

    OnItemButtonClick mOnItemButtonClick;


    public void setOnItemButtonClick(OnItemButtonClick onItemButtonClick) {
        this.mOnItemButtonClick = onItemButtonClick;
    }

    public interface OnItemButtonClick {
        boolean onButtonClick(int position, View view, int status);

    }
}
