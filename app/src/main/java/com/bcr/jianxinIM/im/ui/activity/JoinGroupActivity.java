package com.bcr.jianxinIM.im.ui.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.bcr.jianxinIM.R;
import com.bcr.jianxinIM.im.server.SealAction;
import com.bcr.jianxinIM.im.server.network.async.AsyncTaskManager;
import com.bcr.jianxinIM.im.server.network.async.OnDataListener;
import com.bcr.jianxinIM.im.server.network.http.HttpException;
import com.bcr.jianxinIM.im.server.response.QRAddGroupResponse;
import com.bcr.jianxinIM.im.server.utils.NToast;
import com.bcr.jianxinIM.im.server.widget.LoadDialog;
import com.bcr.jianxinIM.util.SharedPreferencesUtil;
import com.bcr.jianxinIM.view.RoundImageView;
import android.widget.TextView;
import io.rong.imageloader.core.ImageLoader;

public class JoinGroupActivity extends BaseActivity {
    private static final int QRADDGROUP =216 ;
    String groupName,groupMember,groupPic,groupId;
    RoundImageView mGroupAvatar;
    TextView mGroupName,mGroupMember;
    Button mConfirm;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_join_group);
        setTitle("加入群聊");
        groupName=getIntent().getStringExtra("groupName");
        groupMember=getIntent().getStringExtra("groupMember");
        groupPic=getIntent().getStringExtra("groupPic");
        groupId=getIntent().getStringExtra("groupId");
        initView();
    }
    public void initView(){
        mGroupAvatar=findViewById(R.id.profile_iv_join_group_portrait);
        mGroupName=findViewById(R.id.profile_tv_join_group_name);
        mGroupMember=findViewById(R.id.profile_tv_join_group_member);
        mConfirm=findViewById(R.id.profile_btn_join_group_confirm);
        mGroupName.setText(groupName);
        mGroupMember.setText("群内已有"+groupMember+"人");
        ImageLoader.getInstance().displayImage(groupPic,mGroupAvatar);
        mConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LoadDialog.show(mContext,"loading...",false);
                AsyncTaskManager.getInstance(mContext).request(QRADDGROUP, new OnDataListener() {
                    @Override
                    public Object doInBackground(int requestCode, String parameter) throws HttpException {
                        return new SealAction(mContext).SendAddGroupMsg(groupId, SharedPreferencesUtil.getString(mContext,"userId",""));
                    }

                    @Override
                    public void onSuccess(int requestCode, Object result) {
                        if (result != null) {
                            QRAddGroupResponse qrAddGroupResponse= (QRAddGroupResponse) result;
                            if(qrAddGroupResponse.isSuccess()==true){
                                LoadDialog.dismiss(mContext);
                                NToast.shortToast(mContext,qrAddGroupResponse.getMessage());
                                finish();
                            }else {
                                LoadDialog.dismiss(mContext);
                                NToast.shortToast(mContext,qrAddGroupResponse.getMessage());
                            }
                        }
                    }

                    @Override
                    public void onFailure(int requestCode, int state, Object result) {

                    }
                });

            }
        });
    }
}
