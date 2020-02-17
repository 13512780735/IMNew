package com.bcr.jianxinIM.im;

import android.content.Context;
import android.content.Intent;

import com.bcr.jianxinIM.activity.main.MainActivity;
import com.bcr.jianxinIM.im.ui.activity.NewFriendListActivity;
import com.orhanobut.logger.Logger;
import com.bcr.jianxinIM.im.server.broadcast.BroadcastManager;

import io.rong.push.PushType;
import io.rong.push.notification.PushMessageReceiver;
import io.rong.push.notification.PushNotificationMessage;


public class SealNotificationReceiver extends PushMessageReceiver {

    @Override
    public boolean onNotificationMessageArrived(Context context, PushType pushType, PushNotificationMessage pushNotificationMessage) {

        BroadcastManager.getInstance(context).sendBroadcast(SealAppContext.UPDATE_FRIEND);
        Logger.e("TAG","接受到消息------》onNotificationMessageArrived"+pushNotificationMessage.getTargetId()+"--"+pushNotificationMessage.getPushData());
        return false;
    }

    @Override
    public boolean onNotificationMessageClicked(Context context, PushType pushType, PushNotificationMessage message) {
        Logger.e("TAG","接受到消息------》onNotificationMessageArrived"+message.getTargetId()+"--"+message.getPushData());

        if (!message.getSourceType().equals(PushNotificationMessage.PushSourceType.FROM_ADMIN)) {
            String targetId = message.getTargetId();
            //10000 为 Demo Server 加好友的 id，若 targetId 为 10000，则为加好友消息，默认跳转到 NewFriendListActivity
            if (targetId != null && targetId.equals("10000")) {
                Intent intentMain = new Intent(context, NewFriendListActivity.class);
                intentMain.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK );
                Intent intentNewFriend = new Intent(context, MainActivity.class);
                intentNewFriend.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK );
                Intent[] intents = new Intent[]{};
                intents[0] = intentMain;
                intents[1] = intentNewFriend;
                context.startActivities(intents);
                return true;
            } else {
                Intent intentMain = new Intent(context, MainActivity.class);
                intentMain.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK );
                context.startActivity(intentMain);
            }
        }
        return false;
    }
    @Override
    public void onThirdPartyPushState(PushType pushType, String action, long resultCode) {
        super.onThirdPartyPushState(pushType, action, resultCode);
    }
}
