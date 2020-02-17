package com.bcr.jianxinIM.im.message.provider;

import android.content.Context;
import android.text.Spannable;
import android.view.View;
import android.view.ViewGroup;
import io.rong.imkit.model.ProviderTag;
import io.rong.imkit.model.UIMessage;
import io.rong.imkit.widget.provider.IContainerItemProvider;
import io.rong.message.RecallNotificationMessage;


@ProviderTag(messageContent = RecallNotificationMessage.class, showPortrait = false, showProgress = false, showWarning = false, centerInHorizontal = true,
        showSummaryWithName = false)
public class MyRecallMessageItemProvider extends IContainerItemProvider.MessageProvider<RecallNotificationMessage> {


    @Override
    public void bindView(View view, int i, RecallNotificationMessage recallNotificationMessage, UIMessage uiMessage) {

    }

    @Override
    public Spannable getContentSummary(RecallNotificationMessage recallNotificationMessage) {
        return null;
    }

    @Override
    public void onItemClick(View view, int i, RecallNotificationMessage recallNotificationMessage, UIMessage uiMessage) {
        //System.out.println("点击了");
    }

    @Override
    public View newView(Context context, ViewGroup viewGroup) {
        return null;
    }
}
