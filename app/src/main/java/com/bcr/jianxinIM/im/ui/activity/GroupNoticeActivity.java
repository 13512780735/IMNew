package com.bcr.jianxinIM.im.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.bcr.jianxinIM.R;
import com.bcr.jianxinIM.im.server.network.http.HttpException;
import com.bcr.jianxinIM.im.server.response.SetNoticeResponse;
import com.bcr.jianxinIM.im.server.utils.NToast;
import com.bcr.jianxinIM.im.server.widget.LoadDialog;
import com.bcr.jianxinIM.view.CommonDialog;

import io.rong.imkit.RongContext;
import io.rong.imkit.RongIM;
import io.rong.imkit.emoticon.AndroidEmoji;
import io.rong.imlib.IRongCallback;
import io.rong.imlib.RongIMClient;
import io.rong.imlib.model.Conversation;
import io.rong.imlib.model.MentionedInfo;
import io.rong.imlib.model.Message;
import io.rong.message.TextMessage;

@SuppressWarnings("deprecation")
public class GroupNoticeActivity extends BaseActivity implements View.OnClickListener, TextWatcher {
    private static final int NOTICE =207 ;
    EditText mEdit;
    Conversation.ConversationType mConversationType;
    String mTargetId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_notice);
        mEdit = (EditText) findViewById(R.id.edit_area);
        Intent intent = getIntent();
        mConversationType = Conversation.ConversationType.setValue(intent.getIntExtra("conversationType", 0));
        mTargetId = getIntent().getStringExtra("targetId");
        setTitle(R.string.group_announcement);
        Button rightButton = getHeadRightButton();
        rightButton.setVisibility(View.GONE);
        mHeadRightText.setVisibility(View.VISIBLE);
        mHeadRightText.setText(R.string.Done);
        mHeadRightText.setTextColor(getResources().getColor(android.R.color.white));
        mHeadRightText.setClickable(false);
        mHeadRightText.setOnClickListener(this);
        mEdit.addTextChangedListener(this);
    }

    @Override
    public void onHeadLeftButtonClick(View v) {
        CommonDialog dialog = new CommonDialog.Builder()
                .setContentMessage(getString(R.string.group_notice_exist_confirm))
                .setDialogButtonClickListener(new CommonDialog.OnDialogButtonClickListener() {
                    @Override
                    public void onPositiveClick(View v, Bundle bundle) {
                        finish();
                    }



                    @Override
                    public void onNegativeClick(View v, Bundle bundle) {
                    }
                })
                .build();
        dialog.show(getSupportFragmentManager(), null);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.text_right:
                CommonDialog dialog = new CommonDialog.Builder()
                        .setContentMessage( getString(R.string.group_notice_post_confirm))
                        .setDialogButtonClickListener(new CommonDialog.OnDialogButtonClickListener() {
                            @Override
                            public void onPositiveClick(View v, Bundle bundle) {
                                LoadDialog.show(mContext,"loading...",false);
                                request(NOTICE,true);
                            }



                            @Override
                            public void onNegativeClick(View v, Bundle bundle) {
                            }
                        })
                        .build();
                dialog.show(getSupportFragmentManager(), null);

                break;
        }
    }

    @Override
    public Object doInBackground(int requestCode, String id) throws HttpException {
        switch (requestCode){
            case NOTICE:
                return action.setNotice(mEdit.getText().toString(),mTargetId);
        }
        return super.doInBackground(requestCode, id);
    }

    @Override
    public void onSuccess(int requestCode, Object result) {
        if (result != null) {
            switch (requestCode){
                case NOTICE:
                    SetNoticeResponse setNoticeResponse= (SetNoticeResponse) result;
                    if(setNoticeResponse.isSuccess()==true){
                        TextMessage textMessage = TextMessage.obtain(RongContext.getInstance().getString(R.string.group_notice_prefix) + mEdit.getText().toString());
                        MentionedInfo mentionedInfo = new MentionedInfo(MentionedInfo.MentionedType.ALL, null, null);
                        textMessage.setMentionedInfo(mentionedInfo);

                        RongIM.getInstance().sendMessage(Message.obtain(mTargetId, mConversationType, textMessage), null, null, new IRongCallback.ISendMessageCallback() {
                            @Override
                            public void onAttached(Message message) {

                            }

                            @Override
                            public void onSuccess(Message message) {

                            }

                            @Override
                            public void onError(Message message, RongIMClient.ErrorCode errorCode) {

                            }
                        });
                        Intent data = new Intent();
                        data.putExtra("notice",mEdit.getText().toString());
                        setResult(1001,data);
                        LoadDialog.dismiss(mContext);
                        NToast.shortToast(mContext, setNoticeResponse.getMessage());
                        finish();
                    }
                    break;
            }
        }
    }

    @Override
    public void onFailure(int requestCode, int state, Object result) {
        switch (requestCode){
            case NOTICE:
                SetNoticeResponse setNoticeResponse= (SetNoticeResponse) result;
                LoadDialog.dismiss(mContext);
                NToast.shortToast(mContext,setNoticeResponse.getMessage());
                break;
        }
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        if (s.toString().length() > 0) {
            mHeadRightText.setClickable(true);
            mHeadRightText.setTextColor(getResources().getColor(android.R.color.white));
        } else {
            mHeadRightText.setClickable(false);
            mHeadRightText.setTextColor(getResources().getColor(android.R.color.darker_gray));
        }
    }

    @Override
    public void afterTextChanged(Editable s) {
        if ( s != null) {
            int start = mEdit.getSelectionStart();
            int end = mEdit.getSelectionEnd();
            mEdit.removeTextChangedListener(this);
            mEdit.setText(AndroidEmoji.ensure(s.toString()));
            mEdit.addTextChangedListener(this);
            mEdit.setSelection(start, end);
        }
    }
}
