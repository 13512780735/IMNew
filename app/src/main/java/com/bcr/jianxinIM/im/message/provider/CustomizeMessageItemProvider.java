package com.bcr.jianxinIM.im.message.provider;

import android.content.Context;

import io.rong.imlib.model.Message;

import android.os.Build;
import android.support.annotation.RequiresApi;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bcr.jianxinIM.im.message.CustomizeMessage;
import com.bcr.jianxinIM.im.server.SealAction;
import com.bcr.jianxinIM.im.server.network.async.AsyncTaskManager;
import com.bcr.jianxinIM.im.server.network.async.OnDataListener;
import com.bcr.jianxinIM.im.server.network.http.HttpException;
import com.bcr.jianxinIM.im.server.response.GetUserInfoByIdResponse;
import com.bcr.jianxinIM.util.AndroidDes3Util;
import com.bcr.jianxinIM.util.SharedPreferencesUtil;
import com.orhanobut.logger.Logger;

import io.rong.imkit.model.ProviderTag;
import io.rong.imkit.model.UIMessage;
import io.rong.imkit.widget.provider.IContainerItemProvider;

@ProviderTag(messageContent = CustomizeMessage.class)
public class CustomizeMessageItemProvider extends IContainerItemProvider.MessageProvider<CustomizeMessage> {
    //private static final String KEY = "983F0495B28B46DA9F7500D4E69FFC8F";
    private String content;
    private String contents;
    private int flag;
    private Context mContext;
    private String yourEncode;
    private static final int REQUSERINFO = 4234;

    private static String secretKey="";
    private String targetId;
    private String secretKey1;
    private String secretKey2;

    class ViewHolder {
        TextView message;
    }

    @Override
    public View newView(Context context, ViewGroup group) {
        View view = LayoutInflater.from(context).inflate(io.rong.imkit.R.layout.rc_item_text_message, null);
        ViewHolder holder = new ViewHolder();
        holder.message = (TextView) view.findViewById(android.R.id.text1);
        mContext=context;
        view.setTag(holder);
        return view;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void bindView(View view, int i, CustomizeMessage message, UIMessage uiMessage) {
        Logger.d("密码：--》"+message.getExtra());
        secretKey=message.getExtra();
        ViewHolder holder = (ViewHolder) view.getTag();
        if (uiMessage.getMessageDirection() == Message.MessageDirection.SEND) {//消息方向，自己发送的
            holder.message.setBackgroundResource(io.rong.imkit.R.drawable.rc_ic_bubble_right);
        } else {
            holder.message.setBackgroundResource(io.rong.imkit.R.drawable.rc_ic_bubble_left);
        }
        try {
            content= AndroidDes3Util.decode(message.getContent(),secretKey);
        } catch (Exception e) {
            e.printStackTrace();
        }
        holder.message.setText(content);
    }


    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public Spannable getContentSummary(CustomizeMessage data) {
        return null;
    }

    @Override
    public Spannable getContentSummary(Context context, CustomizeMessage data) {
        Logger.d("密码：--》"+data.getExtra());
        try {
            contents=AndroidDes3Util.decode(data.getContent(),data.getExtra());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new SpannableString(contents);
    }

    @Override
    public void onItemClick(View view, int i, CustomizeMessage customizeMessage, UIMessage uiMessage) {

    }


}
