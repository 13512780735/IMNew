package com.bcr.jianxinIM.fragment.main;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bcr.jianxinIM.R;
import com.bcr.jianxinIM.activity.base.BaseFragment;
import com.bcr.jianxinIM.activity.main.AboutActivity;
import com.bcr.jianxinIM.activity.main.CircleOfFriends01Activity;
import com.bcr.jianxinIM.activity.main.CircleOfFriendsActivity;
import com.bcr.jianxinIM.activity.main.CollectActivity;
import com.bcr.jianxinIM.activity.main.FeedbackActivity;
import com.bcr.jianxinIM.activity.main.MyQRCodeActivity;
import com.bcr.jianxinIM.activity.main.SettingActivity;
import com.bcr.jianxinIM.activity.main.UserInfoActivity;
import com.bcr.jianxinIM.activity.main.WalletActivity;
import com.bcr.jianxinIM.im.SealAppContext;
import com.bcr.jianxinIM.im.SealConst;
import com.bcr.jianxinIM.im.server.SealAction;
import com.bcr.jianxinIM.im.server.broadcast.BroadcastManager;
import com.bcr.jianxinIM.im.server.network.async.AsyncTaskManager;
import com.bcr.jianxinIM.im.server.network.async.OnDataListener;
import com.bcr.jianxinIM.im.server.network.http.HttpException;
import com.bcr.jianxinIM.im.server.response.GetUserAddWayResponse;
import com.bcr.jianxinIM.im.server.response.GetUserInfoByIdResponse;
import com.bcr.jianxinIM.im.server.utils.NToast;
import com.bcr.jianxinIM.im.server.widget.LoadDialog;
import com.bcr.jianxinIM.util.SharedPreferencesUtil;
import com.bcr.jianxinIM.view.CircleImageView;
import com.orhanobut.logger.Logger;

import io.rong.imageloader.core.ImageLoader;
import io.rong.imkit.RongIM;
import io.rong.imlib.model.UserInfo;


public class mMeFragment extends BaseFragment implements View.OnClickListener {


    CircleImageView ivAvatar;
    TextView tvName;
    TextView tvAccount;
    ImageView ivScanCode;
    LinearLayout llUserInfo;
    LinearLayout llFriends;
    LinearLayout llWallet;
    LinearLayout llCollect;
    LinearLayout llSetting;
    LinearLayout llHelp;
    LinearLayout llAbout;
    private static final int REQUSERINFO = 4234;
    private static final int GETUSETADDWAY = 251;
    private int phone;
    private int code=0;
    private int userId;
    private int group;
    private Intent intent;
    private Bundle bundle;
    private String nickName;
    private String pic;

    @Override
    public int inflaterRootView() {
        return R.layout.m_fragment_me;
    }

    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tvAccount:
                break;
            case R.id.ivScanCode:
                // startActivity(new Intent(getActivity(), MyQRCodeActivity.class));
                intent=new Intent(getActivity(), MyQRCodeActivity.class);
                bundle=new Bundle();
                bundle.putInt("code",code);
                bundle.putString("userName",nickName);
                bundle.putString("userPic",pic);
                intent.putExtras(bundle);
                startActivity(intent);
                break;
            case R.id.llUserInfo:
                intent=new Intent(getActivity(), UserInfoActivity.class);
                 bundle=new Bundle();
                bundle.putInt("code",code);
                intent.putExtras(bundle);
                 startActivity(intent);
                //startActivity(new Intent(getActivity(), CircleOfFriendsActivity.class));
                break;
            case R.id.llFriends:
                startActivity(new Intent(getActivity(), CircleOfFriends01Activity.class));
                break;
            case R.id.llWallet:
                startActivity(new Intent(getActivity(), WalletActivity.class));
                break;
            case R.id.llCollect:
                // NToast.shortToast(getActivity(),"暂未开放");
                startActivity(new Intent(getActivity(), CollectActivity.class));
                break;
            case R.id.llSetting:
                startActivity(new Intent(getActivity(), SettingActivity.class));
                break;
            case R.id.llHelp:
                startActivity(new Intent(getActivity(), FeedbackActivity.class));
                break;
            case R.id.llAbout:
                startActivity(new Intent(getActivity(), AboutActivity.class));
                break;
        }
    }
    @Override
    public void initUI() {
        ivAvatar=findViewById(R.id.ivAvatar);
        tvName=findViewById(R.id.tvName);
        tvAccount=findViewById(R.id.tvAccount);
        ivScanCode=findViewById(R.id.ivScanCode);
        llUserInfo=findViewById(R.id.llUserInfo);
        llFriends=findViewById(R.id.llFriends);
        llWallet=findViewById(R.id.llWallet);
        llCollect=findViewById(R.id.llCollect);
        llSetting=findViewById(R.id.llSetting);
        llHelp=findViewById(R.id.llHelp);
        llAbout=findViewById(R.id.llAbout);
        ivScanCode.setOnClickListener(this);
        llUserInfo.setOnClickListener(this);
        llFriends.setOnClickListener(this);
        llWallet.setOnClickListener(this);
        llCollect.setOnClickListener(this);
        llSetting.setOnClickListener(this);
        llHelp.setOnClickListener(this);
        llAbout.setOnClickListener(this);
    }

    @Override
    public void onResume() {
        updateUserInfo();
        initType();
        BroadcastManager.getInstance(getActivity()).addAction(SealConst.CHANGEINFO, new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                updateUserInfo();
            }
        });
        super.onResume();
    }

    /**
     * 获取添加方式
     */
    private void initType() {
        AsyncTaskManager.getInstance(getActivity()).request(GETUSETADDWAY, new OnDataListener() {
            @Override
            public Object doInBackground(int requestCode, String parameter) throws HttpException {
                return new SealAction(getActivity()).getUserAddWay(SharedPreferencesUtil.getString(getActivity(),"userId",""));
            }

            @Override
            public void onSuccess(int requestCode, Object result) {
                if (result != null) {
                    GetUserAddWayResponse getUserAddWayResponse= (GetUserAddWayResponse) result;
                    if(getUserAddWayResponse.isSuccess()==true){
                        phone=getUserAddWayResponse.getResultData().getTel();
                        userId=getUserAddWayResponse.getResultData().getUserId();
                        code=getUserAddWayResponse.getResultData().getCode();
                        group=getUserAddWayResponse.getResultData().getGroup();
                        // NToast.shortToast(mContext,getUserAddWayResponse.getMessage());
                    }else {
                        NToast.shortToast(getActivity(),getUserAddWayResponse.getMessage());
                    }
                }
            }

            @Override
            public void onFailure(int requestCode, int state, Object result) {
               // LoadDialog.dismiss(getActivity());
            }
        });

    }

    @Override
    public void initData() {
        //updateUserInfo();
    }

    private void updateUserInfo() {
        Logger.d("用户Id:-->"+SharedPreferencesUtil.getString(getActivity(),"userId",""));
        AsyncTaskManager.getInstance(getActivity()).request(REQUSERINFO, new OnDataListener() {
            @Override
            public Object doInBackground(int requestCode, String parameter) throws HttpException {
                return new SealAction(getActivity()).getUserInfoById(SharedPreferencesUtil.getString(getActivity(),"userId",""));
            }

            @Override
            public void onSuccess(int requestCode, Object result) {
                if (result != null) {
                    GetUserInfoByIdResponse getUserInfoByIdResponse= (GetUserInfoByIdResponse) result;
                    if(getUserInfoByIdResponse.isSuccess()==true){
                        pic=getUserInfoByIdResponse.getResultData().getUserPic();
                        if(!TextUtils.isEmpty(pic)){
                            ImageLoader.getInstance().displayImage(pic, ivAvatar);}
                        else ivAvatar.setImageDrawable(getResources().getDrawable(R.mipmap.icon_wangwang));
                        nickName=getUserInfoByIdResponse.getResultData().getUserNickName();
                        SharedPreferencesUtil.putString(getActivity(),"userName",nickName);
                        SharedPreferencesUtil.putString(getActivity(),"userPic",pic);
                        tvName.setText(nickName);
                        tvAccount.setText("ID:"+getUserInfoByIdResponse.getResultData().getUserId());

                        RongIM.getInstance().refreshUserInfoCache(new UserInfo(getUserInfoByIdResponse.getResultData().getUserId(), getUserInfoByIdResponse.getResultData().getUserNickName(), Uri.parse(getUserInfoByIdResponse.getResultData().getUserPic())));
                        BroadcastManager.getInstance(getActivity()).sendBroadcast(SealAppContext.UPDATE_FRIEND);
                    }
                }
            }

            @Override
            public void onFailure(int requestCode, int state, Object result) {

            }
        });
    }
}
