package com.bcr.jianxinIM.im.ui.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;

import com.bcr.jianxinIM.R;
import com.orhanobut.logger.Logger;

import io.rong.imkit.model.UIConversation;
import io.rong.imkit.widget.AsyncImageView;
import io.rong.imkit.widget.adapter.ConversationListAdapter;
import io.rong.imlib.model.Conversation;

/**
 * Created by weiqinxiao on 15/11/5.
 */
public class ConversationListAdapterEx extends ConversationListAdapter {
    private int defaultId;

    public ConversationListAdapterEx(Context context) {
        super(context);
    }

    @Override
    protected View newView(Context context, int position, ViewGroup group) {
        return super.newView(context, position, group);
    }

    @Override
    protected void bindView(View v, int position, UIConversation data) {
        Logger.d("数据获取：--》"+data.getConversationSenderId());
        Logger.d("数据获取：--》"+data.getConversationType());
//
//        if (data != null) {
////            if (data.getConversationType().equals(Conversation.ConversationType.DISCUSSION))
////                data.setUnreadType(UIConversation.UnreadRemindType.REMIND_ONLY);
//            if (data.getConversationGatherState() && data.getConversationType() == Conversation
//
//                    .ConversationType.SYSTEM) {
//
//                AsyncImageView img = v.findViewById(io.rong.imkit.R.id.rc_left);
//
//                img.setAvatar(null, R.drawable.rc_default_portrait2);//设置头像
//
//            }
//        }
//        if (data.getConversationType().equals(Conversation.ConversationType.GROUP)) {
//
//            defaultId = R.drawable.rc_default_group_portrait;
//
//        } else if (data.getConversationType().equals(Conversation.ConversationType.SYSTEM)){
//            defaultId = R.drawable.rc_default_portrait;
//        }
//        else if (data.getConversationType().equals(Conversation.ConversationType.DISCUSSION)) {
//
//          // defaultId = R.drawable.rc_default_portrait1;
//            data.setUnreadType(UIConversation.UnreadRemindType.REMIND_ONLY);
//
//        } else {
//
//
//
//        }
        super.bindView(v, position, data);
    }
}
