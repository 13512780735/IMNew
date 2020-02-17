/*
package com.bcr.jianxin.im.message.provider;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.support.v7.widget.ViewUtils;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bcr.jianxin.R;

import java.util.Date;

import io.rong.imkit.RongContext;
import io.rong.imkit.emoticon.AndroidEmoji;
import io.rong.imkit.model.ConversationKey;
import io.rong.imkit.model.ConversationProviderTag;
import io.rong.imkit.model.ProviderTag;
import io.rong.imkit.model.UIConversation;
import io.rong.imkit.utils.RongDateUtils;
import io.rong.imkit.widget.provider.IContainerItemProvider;

@ConversationProviderTag(conversationType = "group", portraitPosition = 1)
public class GroupConversationProvider implements IContainerItemProvider.ConversationProvider<UIConversation> {
    private static int i = 0;
    private String TAG = GroupConversationProvider.class.getSimpleName();

    class ViewHolder {

        TextView title;
        TextView time;
        TextView content;
        ImageView notificationBlockImage;
        TextView atMe;
        final GroupConversationProvider provider;

        ViewHolder() {
            provider = GroupConversationProvider.this;
        }
    }

    public GroupConversationProvider() {

    }

    @Override
    public void bindView(View view, int position, UIConversation data) {

        ViewHolder holder = (ViewHolder) view.getTag();
        ProviderTag tag = null;

        if (data == null) {
            holder.title.setText(null);
            holder.time.setText(null);
            holder.content.setText(null);
        } else {
            //设置会话标题
            holder.title.setText(data.getUIConversationTitle());
            //设置会话时间
            String time = RongDateUtils.getConversationListFormatDate(new Date(data.getUIConversationTime()));
            holder.time.setText(time);
            //设置内容
            if (!TextUtils.isEmpty(data.getDraft())) {
                SpannableStringBuilder builder = new SpannableStringBuilder();
                SpannableString string = new SpannableString("[草稿]");
                string.setSpan(new ForegroundColorSpan(Color.parseColor("#cb120f")), 0, string.length(), 33);

                if(data.getDraft().toString().substring(data.getDraft().toString().length() - 1, data.getDraft().toString().length()).equals("@")){
                    data.setDraft(data.getDraft().toString().substring(0,data.getDraft().toString().length()-1));
                }
                builder.append(string).append(data.getDraft());
                AndroidEmoji.ensure(builder);
                holder.content.setText(builder);
            } else {
                setDateView(holder, data);
                holder.content.setText(data.getConversationContent());
            }
            if (RongContext.getInstance() != null && data.getMessageContent() != null)
                tag = RongContext.getInstance().getMessageProviderTag(data.getMessageContent().getClass());
            if (data.getSentStatus() != null && (data.getSentStatus() == io.rong.imlib.model.Message.SentStatus.FAILED || data.getSentStatus() == io.rong.imlib.model.Message.SentStatus.SENDING) && tag != null && tag.showWarning()) {
                int width = ViewUtils.dp2px(17);
                Drawable drawable = null;
                if (data.getSentStatus() == io.rong.imlib.model.Message.SentStatus.FAILED)
                    drawable = view.getContext().getResources().getDrawable(R.drawable.de_conversation_list_msg_send_failure);
                else if (data.getSentStatus() == io.rong.imlib.model.Message.SentStatus.SENDING)
                    drawable = view.getContext().getResources().getDrawable(R.drawable.de_conversation_list_msg_sending);
                if (drawable != null) {
                    drawable.setBounds(0, 0, width, width);
                    holder.content.setCompoundDrawablePadding(10);
                    holder.content.setCompoundDrawables(drawable, null, null, null);
                }
            } else {
                holder.content.setCompoundDrawables(null, null, null, null);
            }
            ConversationKey key = ConversationKey.obtain(data.getConversationTargetId(), data.getConversationType());
            io.rong.imlib.model.Conversation.ConversationNotificationStatus status = RongContext.getInstance().getConversationNotifyStatusFromCache(key);
            if (status != null && status.equals(io.rong.imlib.model.Conversation.ConversationNotificationStatus.DO_NOT_DISTURB))
                holder.notificationBlockImage.setVisibility(View.VISIBLE);
            else
                holder.notificationBlockImage.setVisibility(View.GONE);
        }
    }

    */
/**
     * @param holder
     * @param data
     * @ 消息提示
     *//*

    private void setDateView(ViewHolder holder, UIConversation data) {
        if (AtUserService.getInstance().getAtGroupIds() != null && AtUserService.getInstance().getAtGroupIds().size() > 0
                &&AtUserService.getInstance().getAtGroupIds().contains(data.getConversationTargetId())) {
            if (data.getUnReadMessageCount() == 0) {
                holder.atMe.setVisibility(View.GONE);
                data.setExtraFlag(false);
            } else if (data.getUnReadMessageCount() > 0) {
                holder.atMe.setVisibility(View.VISIBLE);
                data.setExtraFlag(true);
            }
        } else {
            if (data.getExtraFlag()) {
                holder.atMe.setVisibility(View.VISIBLE);
            } else {
                holder.atMe.setVisibility(View.GONE);
                data.setExtraFlag(false);
            }
            if (data.getUnReadMessageCount() == 0) {
                holder.atMe.setVisibility(View.GONE);
                data.setExtraFlag(false);
            }
        }
    }

    @Override
    public View newView(Context context, ViewGroup viewgroup) {
        View result = LayoutInflater.from(context).inflate(R.layout.de_item_base_conversation, null);
        ViewHolder holder = new ViewHolder();
        holder.title = (TextView) result.findViewById(R.id.de_conversation_title);
        holder.time = (TextView) result.findViewById(R.id.de_conversation_time);
        holder.content = (TextView) result.findViewById(R.id.de_conversation_content);
        holder.notificationBlockImage = (ImageView) result.findViewById(R.id.de_conversation_msg_block);
        holder.atMe = (TextView) result.findViewById(R.id.de_at_me);
        result.setTag(holder);
        return result;
    }

    @Override
    public String getTitle(String s) {
        String name;
        if (RongContext.getInstance().getGroupInfoFromCache(s) == null)
            name = "群组";
        else{
            name = RongContext.getInstance().getGroupInfoFromCache(s).getName();
        }

        return name;
    }

    @Override
    public Uri getPortraitUri(String s) {
        Uri uri;
        if (RongContext.getInstance().getGroupInfoFromCache(s) == null)
            uri = null;
        else{
            uri = RongContext.getInstance().getGroupInfoFromCache(s).getPortraitUri();
        }
        return uri;
    }

}*/
