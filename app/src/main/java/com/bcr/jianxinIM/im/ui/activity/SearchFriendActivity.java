package com.bcr.jianxinIM.im.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bcr.jianxinIM.im.server.response.GetFriendInfoResponse;
import com.bcr.jianxinIM.im.server.response.GetUserInfoByIdResponse;
import com.orhanobut.logger.Logger;
import com.bcr.jianxinIM.R;
import com.bcr.jianxinIM.im.SealAppContext;
import com.bcr.jianxinIM.im.SealConst;
import com.bcr.jianxinIM.im.SealUserInfoManager;
import com.bcr.jianxinIM.im.db.Friend;
import com.bcr.jianxinIM.im.server.network.async.AsyncTaskManager;
import com.bcr.jianxinIM.im.server.network.http.HttpException;
import com.bcr.jianxinIM.im.server.response.FriendInvitationResponse;
import com.bcr.jianxinIM.im.server.response.GetUserInfoByPhoneResponse;
import com.bcr.jianxinIM.im.server.utils.AMUtils;
import com.bcr.jianxinIM.im.server.utils.CommonUtils;
import com.bcr.jianxinIM.im.server.utils.NToast;
import com.bcr.jianxinIM.im.server.widget.ClearWriteEditText;
import com.bcr.jianxinIM.im.server.widget.LoadDialog;
import com.bcr.jianxinIM.im.server.widget.SelectableRoundedImageView;
import com.bcr.jianxinIM.util.SharedPreferencesUtil;
import com.bcr.jianxinIM.view.SimpleInputDialog;

import io.rong.imageloader.core.ImageLoader;
import io.rong.imlib.model.UserInfo;

public class SearchFriendActivity extends BaseActivity {

    private static final int CLICK_CONVERSATION_USER_PORTRAIT = 1;
    private static final int SEARCH_PHONE = 10;
    private static final int ADD_FRIEND = 11;
    private static final int SEARCH_USID = 247;
    private ClearWriteEditText mEtSearch;
    private LinearLayout searchItem;
    private TextView searchName;
    private SelectableRoundedImageView searchImage;
    private String mPhone;
    private String addFriendMessage;
    private String mFriendId;
    private TextView tvNoData;
    private Friend mFriend;
    String types;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);



        setTitle((R.string.search_friend));

        mEtSearch = (ClearWriteEditText) findViewById(R.id.search_edit);
        searchItem = (LinearLayout) findViewById(R.id.search_result);
        tvNoData = (TextView) findViewById(R.id.tvNoData);
        searchName = (TextView) findViewById(R.id.search_name);
        searchImage = (SelectableRoundedImageView) findViewById(R.id.search_header);

        mEtSearch.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    //点击搜索的时候隐藏软键盘
                   // s.length() == 11
                    int leng=mEtSearch.getText().length();
                    mPhone = mEtSearch.getText().toString().trim();
                    if(leng==10){
                        types="userId";
                        LoadDialog.show(mContext,"loading...",false);
                        //request(SEARCH_USID, true);
                        request(SEARCH_PHONE, true);
                    }else if(leng==11){
                        types="Tel";
                         LoadDialog.show(mContext,"loading...",false);
                        request(SEARCH_PHONE, true);
                    }else {
                       // NToast.shortToast(mContext, "查不到此人");
                        tvNoData.setVisibility(View.VISIBLE);
                    }

                    hideKeyboard(mEtSearch);
                    // 在这里写搜索的操作,一般都是网络请求数据




                    return true;
                }

                return false;
            }
        });




//        mEtSearch.addTextChangedListener(new TextWatcher() {
//            @Override
//            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
//
//            }
//
//            @Override
//            public void onTextChanged(CharSequence s, int start, int before, int count) {
//                if (s.length() == 11) {
//                    mPhone = s.toString().trim();
//                    if (!AMUtils.isMobile(mPhone)) {
//                        NToast.shortToast(mContext, "非法手机号");
//                        return;
//                    }
//                    hintKbTwo();
//                    LoadDialog.show(mContext,"loading...",false);
//                    request(SEARCH_PHONE, true);
//                } else {
//                    searchItem.setVisibility(View.GONE);
//                }
//            }
//
//            @Override
//            public void afterTextChanged(Editable s) {
//
//            }
//        });

    }
    /**
     * 隐藏软键盘
     * @param view    :一般为EditText
     */
    public void hideKeyboard(View view) {
        InputMethodManager manager = (InputMethodManager) view.getContext()
                .getSystemService(Context.INPUT_METHOD_SERVICE);
        manager.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }
    @Override
    public Object doInBackground(int requestCode, String id) throws HttpException {
        switch (requestCode) {
            case SEARCH_PHONE:
                return action.getFriendInfoDetaisl( mPhone,types);
//            case SEARCH_USID:
//                return action.getUserInfoById( mPhone);
            case ADD_FRIEND:
                return action.sendFriendInvitation(SharedPreferencesUtil.getString(SearchFriendActivity.this,"userId",""),mFriendId, addFriendMessage);
        }
        return super.doInBackground(requestCode, id);
    }

    @Override
    public void onSuccess(int requestCode, Object result) {
        if (result != null) {
            switch (requestCode) {
                case SEARCH_PHONE:
                    final GetFriendInfoResponse getFriendInfoResponse = (GetFriendInfoResponse) result;
                    LoadDialog.dismiss(mContext);
                    if (getFriendInfoResponse.isSuccess() == true) {
                        String portraitUri = null;
                        if (getFriendInfoResponse.getResultData() != null) {
                            mFriendId = getFriendInfoResponse.getResultData().getUserId();
                            searchItem.setVisibility(View.VISIBLE);
                            tvNoData.setVisibility(View.GONE);
                            GetFriendInfoResponse.ResultDataBean getFriendInfoResponseResultData = getFriendInfoResponse.getResultData();
                            UserInfo userInfo = new UserInfo(getFriendInfoResponseResultData.getId(),
                                    getFriendInfoResponseResultData.getUserNickName(),
                                    Uri.parse(getFriendInfoResponseResultData.getUserPic()));
                            portraitUri = SealUserInfoManager.getInstance().getPortraitUri(userInfo);
                            searchName.setText(getFriendInfoResponse.getResultData().getUserNickName());
                            NToast.shortToast(mContext, getFriendInfoResponse.getMessage());
                        }else {
                            searchItem.setVisibility(View.GONE);
                            tvNoData.setVisibility(View.VISIBLE);
                            NToast.shortToast(mContext, "用户不存在");
                        }
                        if(TextUtils.isEmpty(portraitUri)){
                            searchImage.setImageDrawable(getResources().getDrawable(R.mipmap.icon_wangwang));
                        }else {
                        ImageLoader.getInstance().displayImage(portraitUri, searchImage);}
                        searchItem.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                if (isFriendOrSelf(mFriendId)) {
                                    Intent intent = new Intent(SearchFriendActivity.this, UserDetailActivity.class);
                                    intent.putExtra("friend", mFriend);
                                    intent.putExtra("type", CLICK_CONVERSATION_USER_PORTRAIT);
                                    startActivity(intent);
                                    SealAppContext.getInstance().pushActivity(SearchFriendActivity.this);
                                    return;
                                }
                                final EditText et = new EditText(mContext);
                                SimpleInputDialog dialog = new SimpleInputDialog();
                                dialog.setInputHint("加好友信息..");
                                dialog.setInputDialogListener(new SimpleInputDialog.InputDialogListener() {
                                    @Override
                                    public boolean onConfirmClicked(EditText input) {
                                        if (!CommonUtils.isNetworkConnected(mContext)) {
                                            NToast.shortToast(mContext, R.string.network_not_available);
                                            return false;
                                        }
                                        addFriendMessage = input.getText().toString();
                                        if (TextUtils.isEmpty(addFriendMessage)) {
                                            addFriendMessage = "我是" + SharedPreferencesUtil.getString(mContext,"userName","");
                                        }
                                        if (!TextUtils.isEmpty(mFriendId)) {
                                            LoadDialog.show(mContext,"Loading...",false);
                                            request(ADD_FRIEND);
                                        } else {
                                            NToast.shortToast(mContext, "id is null");
                                        }
                                        return true;
                                    }
                                });
                                dialog.show(getSupportFragmentManager(), null);

                            }
                        });

                    }
                    else LoadDialog.dismiss(mContext);
                    break;
//                case SEARCH_USID:
//                    final GetUserInfoByIdResponse getUserInfoByIdResponse = (GetUserInfoByIdResponse) result;
//                    LoadDialog.dismiss(mContext);
//                    if (getUserInfoByIdResponse.isSuccess() == true) {
//                        NToast.shortToast(mContext, "success");
//                        mFriendId = getUserInfoByIdResponse.getResultData().getUserId();
//                        searchItem.setVisibility(View.VISIBLE);
//                        String portraitUri = null;
//                        if (getUserInfoByIdResponse.getResultData() != null) {
//                            GetUserInfoByIdResponse.ResultDataBean userInfoByPhoneResponseResult = getUserInfoByIdResponse.getResultData();
//                            UserInfo userInfo = new UserInfo(userInfoByPhoneResponseResult.getId(),
//                                    userInfoByPhoneResponseResult.getUserNickName(),
//                                    Uri.parse(userInfoByPhoneResponseResult.getUserPic()));
//                            portraitUri = SealUserInfoManager.getInstance().getPortraitUri(userInfo);
//                        }
//                        Logger.d("portraitUri:-->"+portraitUri);
//                        if(TextUtils.isEmpty(portraitUri)){
//                            searchImage.setImageDrawable(getResources().getDrawable(R.mipmap.icon_wangwang));
//                        }else {
//                            ImageLoader.getInstance().displayImage(portraitUri, searchImage);}
//                        searchName.setText(getUserInfoByIdResponse.getResultData().getUserNickName());
//                        searchItem.setOnClickListener(new View.OnClickListener() {
//                            @Override
//                            public void onClick(View v) {
//                                if (isFriendOrSelf(mFriendId)) {
//                                    Intent intent = new Intent(SearchFriendActivity.this, UserDetailActivity.class);
//                                    intent.putExtra("friend", mFriend);
//                                    intent.putExtra("type", CLICK_CONVERSATION_USER_PORTRAIT);
//                                    startActivity(intent);
//                                    SealAppContext.getInstance().pushActivity(SearchFriendActivity.this);
//                                    return;
//                                }
//                                final EditText et = new EditText(mContext);
//                                SimpleInputDialog dialog = new SimpleInputDialog();
//                                dialog.setInputHint("加好友信息..");
//                                dialog.setInputDialogListener(new SimpleInputDialog.InputDialogListener() {
//                                    @Override
//                                    public boolean onConfirmClicked(EditText input) {
//                                        if (!CommonUtils.isNetworkConnected(mContext)) {
//                                            NToast.shortToast(mContext, R.string.network_not_available);
//                                            return false;
//                                        }
//                                        addFriendMessage = input.getText().toString();
//                                        if (TextUtils.isEmpty(addFriendMessage)) {
//                                            addFriendMessage = "我是" + SharedPreferencesUtil.getString(mContext,"userName","");
//                                        }
//                                        if (!TextUtils.isEmpty(mFriendId)) {
//                                            LoadDialog.show(mContext,"Loading...",false);
//                                            request(ADD_FRIEND);
//                                        } else {
//                                            NToast.shortToast(mContext, "id is null");
//                                        }
//                                        return true;
//                                    }
//                                });
//                                dialog.show(getSupportFragmentManager(), null);
//
//                            }
//                        });
//
//                    }
//                    else{ LoadDialog.dismiss(mContext);
//                    NToast.shortToast(mContext,getUserInfoByIdResponse.getMessage());
//                    }
//                 break;
                case ADD_FRIEND:
                    FriendInvitationResponse fres = (FriendInvitationResponse) result;
                    if (fres.isSuccess() == true) {
                        NToast.shortToast(mContext, getString(R.string.request_success));
                        LoadDialog.dismiss(mContext);
                        finish();
                    } else {
                        NToast.shortToast(mContext, fres.getMessage());
                        LoadDialog.dismiss(mContext);
                    }
                    break;
            }
        }
    }

    @Override
    public void onFailure(int requestCode, int state, Object result) {
        switch (requestCode) {
            case ADD_FRIEND:
                LoadDialog.dismiss(mContext);
                NToast.shortToast(mContext, "你们已经是好友");
                break;
            case SEARCH_PHONE:
                LoadDialog.dismiss(mContext);
                if (state == AsyncTaskManager.HTTP_ERROR_CODE || state == AsyncTaskManager.HTTP_NULL_CODE) {
                    super.onFailure(requestCode, state, result);
                } else {
                    NToast.shortToast(mContext, "用户不存在");
                }
                break;



        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        hintKbTwo();
        finish();
        return super.onOptionsItemSelected(item);
    }

    private void hintKbTwo() {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm.isActive() && getCurrentFocus() != null) {
            if (getCurrentFocus().getWindowToken() != null) {
                imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
            }
        }
    }

    private boolean isFriendOrSelf(String id) {
        String inputPhoneNumber = mEtSearch.getText().toString().trim();
        SharedPreferences sp = getSharedPreferences("config", MODE_PRIVATE);
        String selfPhoneNumber = sp.getString(SealConst.SEALTALK_LOGING_PHONE, "");
        if (inputPhoneNumber != null) {
            if (inputPhoneNumber.equals(selfPhoneNumber)) {
                mFriend = new Friend(sp.getString(SealConst.SEALTALK_LOGIN_ID, ""),
                        sp.getString(SealConst.SEALTALK_LOGIN_NAME, ""),
                        Uri.parse(sp.getString(SealConst.SEALTALK_LOGING_PORTRAIT, "")));
                return true;
            } else {
                mFriend = SealUserInfoManager.getInstance().getFriendByID(id);
                if (mFriend != null) {
                    return true;
                }
            }
        }
        return false;
    }
}
