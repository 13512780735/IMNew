package com.bcr.jianxinIM.im.message;

import android.os.Parcel;
import android.text.TextUtils;
import android.util.Log;

import com.bcr.jianxinIM.im.utils.RSAUtils;
import com.orhanobut.logger.Logger;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import io.rong.common.ParcelUtils;
import io.rong.imlib.MessageTag;
import io.rong.imlib.model.MentionedInfo;
import io.rong.imlib.model.MessageContent;
import io.rong.imlib.model.UserInfo;

//@MessageTag(value = "RC:TxtMsg", flag = MessageTag.ISCOUNTED | MessageTag.ISPERSISTED)
@MessageTag(value = "app:custom", flag = MessageTag.ISCOUNTED | MessageTag.ISPERSISTED)
public class CustomizeMessage extends MessageContent {
    private  UserInfo userInfo;
    private String extra;
    private String content;
    public String getExtra() {
        return extra;
    }

    public void setExtra(String extra) {
        this.extra = extra;
    }

    public CustomizeMessage(byte[] data) {
        String jsonStr = null;

        try {
            jsonStr = new String(data, "UTF-8");
        } catch (UnsupportedEncodingException e1) {

        }

        try {
            JSONObject jsonObj = new JSONObject(jsonStr);
            if (jsonObj.has("extra")) {
               setExtra(jsonObj.optString("extra"));
            }

            if (jsonObj.has("content")) {
               setContent(jsonObj.optString("content"));
            }
//            if (jsonObj.has("user")) {
//                setUserInfo(this.parseJsonToUserInfo(jsonObj.getJSONObject("user")));
//            }
        } catch (JSONException e) {
            Log.e("JSONException", e.getMessage());
        }

    }

    //给消息赋值。
    public CustomizeMessage(Parcel in) {
        content=ParcelUtils.readFromParcel(in);//该类为工具类，消息属性
        extra=ParcelUtils.readFromParcel(in);
//        userInfo=ParcelUtils.readFromParcel(in,UserInfo.class);

//        setContent(ParcelUtils.readFromParcel(in));
//        setExtra(ParcelUtils.readFromParcel(in));
 //       this.setContent(ParcelUtils.readFromParcel(in));
//        this.setExtra(ParcelUtils.readFromParcel(in));
//        this.setUserInfo((UserInfo)ParcelUtils.readFromParcel(in, UserInfo.class));
//        this.setMentionedInfo((MentionedInfo)ParcelUtils.readFromParcel(in, MentionedInfo.class));
        //这里可继续增加你消息的属性
    }

    /**
     * 读取接口，目的是要从Parcel中构造一个实现了Parcelable的类的实例处理。
     */
    public static final Creator<CustomizeMessage> CREATOR = new Creator<CustomizeMessage>() {

        @Override
        public CustomizeMessage createFromParcel(Parcel source) {
            return new CustomizeMessage(source);
        }

        @Override
        public CustomizeMessage[] newArray(int size) {
            return new CustomizeMessage[size];
        }
    };

    /**
     * 描述了包含在 Parcelable 对象排列信息中的特殊对象的类型。
     *
     * @return 一个标志位，表明Parcelable对象特殊对象类型集合的排列。
     */
    public int describeContents() {
        return 0;
    }

    /**
     * 将类的数据写入外部提供的 Parcel 中。
     *
     * @param dest  对象被写入的 Parcel。
     * @param flags 对象如何被写入的附加标志。
     */
    @Override
    public void writeToParcel(Parcel dest, int flags) {
        Logger.d("消息88：--》"+content);
        ParcelUtils.writeToParcel(dest, content);//该类为工具类，对消息中属性进行序列化
        ParcelUtils.writeToParcel(dest, extra);
        Logger.d("消息88：--》"+extra);
        //这里可继续增加你消息的属性
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    @Override
    public byte[] encode() {
        JSONObject jsonObj = new JSONObject();

        try {
            jsonObj.put("content", content);
            jsonObj.put("extra",extra);

        } catch (JSONException e) {
            Log.e("JSONException", e.getMessage());
        }

        try {
            return jsonObj.toString().getBytes("UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        return null;
    }
    public CustomizeMessage(){}
    public static CustomizeMessage obtain(String content,String extra) {
        CustomizeMessage model = new CustomizeMessage();
        Logger.d("消息99：--》"+content);
        model.setContent(content);
        model.setExtra(extra);
        return model;
    }
    public List<String> getSearchableWord() {
        List<String> words = new ArrayList();
        words.add(this.content);
        return words;
    }
}
