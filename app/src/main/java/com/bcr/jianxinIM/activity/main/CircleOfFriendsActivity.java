package com.bcr.jianxinIM.activity.main;


import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.Display;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.widget.AbsListView;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bcr.jianxinIM.R;
import com.bcr.jianxinIM.adapter.CircleFriendsAdapter;
import com.bcr.jianxinIM.event.EventBusCarrier;
import com.bcr.jianxinIM.im.db.Friend;
import com.bcr.jianxinIM.im.server.BaseAction;
import com.bcr.jianxinIM.im.server.SealAction;
import com.bcr.jianxinIM.im.server.network.async.AsyncTaskManager;
import com.bcr.jianxinIM.im.server.network.async.OnDataListener;
import com.bcr.jianxinIM.im.server.network.http.HttpException;
import com.bcr.jianxinIM.im.server.pinyin.CharacterParser;
import com.bcr.jianxinIM.im.server.response.CirFriedPicRespone;
import com.bcr.jianxinIM.im.server.response.CirFriendComRespone;
import com.bcr.jianxinIM.im.server.response.ClickSupportRespone;
import com.bcr.jianxinIM.im.server.response.DeleteCirFriendRespone;
import com.bcr.jianxinIM.im.server.response.DeleteComRespone;
import com.bcr.jianxinIM.im.server.response.SetPortraitResponse;
import com.bcr.jianxinIM.im.server.response.UserInfoRespone;
import com.bcr.jianxinIM.im.server.utils.NToast;
import com.bcr.jianxinIM.im.server.utils.RongGenerate;
import com.bcr.jianxinIM.im.server.widget.LoadDialog;
import com.bcr.jianxinIM.im.ui.activity.BaseActivity;
import com.bcr.jianxinIM.im.ui.activity.UserDetailActivity;
import com.bcr.jianxinIM.model.CircleFriendsModel1;
import com.bcr.jianxinIM.model.ComUserBean;
import com.bcr.jianxinIM.util.AppManager;
import com.bcr.jianxinIM.util.CommonUtils;
import com.bcr.jianxinIM.util.HttpUtil;
import com.bcr.jianxinIM.util.ImageUtils;
import com.bcr.jianxinIM.util.SharedPreferencesUtil;
import com.bcr.jianxinIM.util.Utils;
import com.bcr.jianxinIM.util.UtilsClick;
import com.bcr.jianxinIM.util.photo.PhotoUtils;
import com.bcr.jianxinIM.view.ActionSheetDialog;
import com.bcr.jianxinIM.view.CircleImageView;
import com.bcr.jianxinIM.view.CommentsView;
import com.bcr.jianxinIM.view.CommonDialog;
import com.bcr.jianxinIM.view.LikePopupWindow;
import com.bcr.jianxinIM.view.LikesView;
import com.bcr.jianxinIM.view.PullToZoomListView;
import com.bcr.jianxinIM.view.PushPhoto.util.ImageLoaderUtils;
import com.bcr.jianxinIM.view.ReplyDialog;
import com.bcr.jianxinIM.view.ScrollSpeedLinearLayoutManger;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.loopj.android.http.RequestParams;
import com.orhanobut.logger.Logger;
import com.yuyh.library.imgsel.ImgSelActivity;
import com.yuyh.library.imgsel.ImgSelConfig;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import io.rong.imageloader.core.ImageLoader;
import io.rong.imlib.model.UserInfo;


public class CircleOfFriendsActivity extends BaseActivity implements BaseQuickAdapter.RequestLoadMoreListener, SwipeRefreshLayout.OnRefreshListener{
    private static final int GETUSERINFO =231 ;
    private static final int TOLIKE =230 ;
    private static final int DELCIR =232 ;
    private static final int CirFriendCom =233 ;
    private static final int DELCOM =234 ;
    private static final int UPLOAD =208 ;
    private static final int CLICK_CONVERSATION_USER_PORTRAIT = 1;
    private static final int REQUEST_CODE_ACTIVITY = 1002;//发布返回
    private static final int REQUEST_CODE = 120;//换头部背景
    private static final int UPLOADPIC =235 ;
    private static final String TAG = "CircleOfFriendsActivity";
    RecyclerView mRecyclerView;
    SwipeRefreshLayout mSwipeRefreshLayout;
    ImageButton mIvBack;
    private TextView tvPost;
    private CircleFriendsAdapter mAdapter;
    private List<CircleFriendsModel1> data;


    private static final int PAGE_SIZE = 10;//为什么是6呢？
    private int pageNum = 1;
    private int mCurrentCounter;
    private boolean isErr = false;
    private int count;
    private int TOTAL_COUNTER=0;
    /**
     * 头部
     */
    private View mHeaderView;
    private CircleImageView riv_avatar;
    private TextView tvName;
    private ImageView ivBg;
    private RelativeLayout mRlTop;


    private int height ;  // 滑动到什么地方完全变色
    private int ScrollUnm = 0;  //滑动的距离总和
    private View mFooterView;
    private FrameLayout rlHead;
    private String userName,userid,userpic,bgPic;//获取用户信息
    private ReplyDialog replyDialog;
    private JSONObject objects;
    private String base64;
    private String pic;
    //底部彈出按钮
    private EditText etComment;
    private TextView tvSend;
    private LinearLayout llComment;
    private LinearLayout llScroll;
    private int screenHeight;
    private int editTextBodyHeight;
    private int currentKeyboardH;
    private ScrollSpeedLinearLayoutManger layoutManger;
    private LikePopupWindow likePopupWindow;
    private View llComment1;
    private CircleFriendsModel1 circleFriendsModel1;
    private String id;
    private int flag;
    private String FriendId;
        String targetId;
    private String disname;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        if (Build.VERSION.SDK_INT >= 21) {
//            View decorView = getWindow().getDecorView();
//            //设置全屏和状态栏透明
//            decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN |
//                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
//            // 设置状态栏为灰色
//            getWindow().setStatusBarColor(getResources().getColor(R.color.black));
//            // getWindow().setStatusBarColor(Color.TRANSPARENT);
//        }
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//           // Window window = activity.getWindow();
//            getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
//            getWindow().setStatusBarColor(getResources().getColor(R.color.black));
//        }

        //getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_circle_of_friends01);
        setHeadVisibility(View.GONE);
        AppManager.getAppManager().addActivity(this);
        EventBus.getDefault().register(this);
        targetId=getIntent().getExtras().getString("targetId");
        initUI();
        initUserInfo();
//        StatusBarUtils.from(this)
//                .setTransparentStatusbar(true)//状态栏是否透明
//                .setTransparentNavigationbar(false)//Navigationbar是否透明
//                .setActionbarView(mRlTop)//view是否透明
//                .setLightStatusBar(false)//状态栏字体是否为亮色
//                .process();
    }

    private void initHeaderView(){
        if(!TextUtils.isEmpty(userpic)){
            ImageLoader.getInstance().displayImage(userpic,riv_avatar);
        }else
            riv_avatar.setImageResource(R.mipmap.icon_wangwang);

        if(TextUtils.isEmpty(disname)){
            if(!TextUtils.isEmpty(userName)){
                tvName.setText(userName);}else
                tvName.setText("123木头人");
        }else {
            tvName.setText(disname);
        }


//        if(!TextUtils.isEmpty(userName)){
//            tvName.setText(userName);}else
//            tvName.setText("123木头人");
        if(!TextUtils.isEmpty(bgPic)){
            ImageLoader.getInstance().displayImage(bgPic,ivBg);
        }else
            ivBg.setImageResource(R.mipmap.icon_friends_bg);
    }
    private void initUserInfo() {
        AsyncTaskManager.getInstance(mContext).request(GETUSERINFO, new OnDataListener() {
            @Override
            public Object doInBackground(int requestCode, String parameter) throws HttpException {
                return new SealAction(mContext).GetUserInfo(SharedPreferencesUtil.getString(mContext,"userId",""),targetId);
            }

            @Override
            public void onSuccess(int requestCode, Object result) {
                if (result != null) {
                    UserInfoRespone userInfoRespone= (UserInfoRespone) result;
                    if(userInfoRespone.isSuccess()==true){
                        userName=userInfoRespone.getResultData().getNickName();
                        userid=userInfoRespone.getResultData().getUserId();
                        userpic=userInfoRespone.getResultData().getUserPic();
                        bgPic=userInfoRespone.getResultData().getCirFriendPic();
                        disname=userInfoRespone.getResultData().getRemark();
                        SharedPreferencesUtil.putString(mContext,"userName",userName);
                      //  SharedPreferencesUtil.putString(mContext,"userId",userid);
                        SharedPreferencesUtil.putString(mContext,"userpic",userpic);
                        initHeaderView();
                    }else {
                        initHeaderView();
                    }
                }
            }

            @Override
            public void onFailure(int requestCode, int state, Object result) {
                initHeaderView();
            }
        });

    }

    private void initUI() {
        data=new ArrayList<>();
        mSwipeRefreshLayout=findViewById(R.id.SwipeRefreshLayout);
        mRecyclerView=findViewById(R.id.RecyclerView);
        mIvBack=findViewById(R.id.ib_back);
        mRlTop = findViewById(R.id.rl_top);
        tvPost = findViewById(R.id.tv_post);
        etComment = findViewById(R.id.et_comment);
        tvSend = findViewById(R.id.tv_send_comment);
        llComment = findViewById(R.id.ll_comment);
        llComment1 = findViewById(R.id.llComment);
        llScroll = findViewById(R.id.ll_scroll);
        mIvBack.setOnClickListener(v -> {
                    onBackPressed();
                    updateEditTextBodyVisible(View.GONE);
                }

        );
        if(targetId.equals(SharedPreferencesUtil.getString(mContext,"userId",""))){
            tvPost.setVisibility(View.VISIBLE);
        }else {
            tvPost.setVisibility(View.INVISIBLE);
        }

        tvPost.setOnClickListener(v -> {
            if (ContextCompat.checkSelfPermission(CircleOfFriendsActivity.this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(CircleOfFriendsActivity.this, new String[]{Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE,Manifest.permission.MOUNT_UNMOUNT_FILESYSTEMS}, 1);
            } else {
                if(UtilsClick.isNotFastClick()){
                    toPostActivity();}
            }
        });
        mSwipeRefreshLayout.setOnRefreshListener(this);
        mSwipeRefreshLayout.setColorSchemeColors(Color.rgb(47, 223, 189));
        // mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        // mRecyclerView.setLayoutManager(new ScrollSpeedLinearLayoutManger(this));
        layoutManger = new ScrollSpeedLinearLayoutManger(this);
        mRecyclerView.setLayoutManager(layoutManger);
        layoutManger.setSpeedSlow();
        initAdapter();
        mHeaderView = View.inflate(mContext, R.layout.header_userinfo, null);
        mFooterView=View.inflate(mContext,R.layout.notdata_view,null);
        WindowManager wm = getWindowManager();
        Display d = wm.getDefaultDisplay();
        //拿到布局参数
        riv_avatar =  mHeaderView.findViewById(R.id.riv_avatar);
        rlHead =  mHeaderView.findViewById(R.id.rlHead);
        tvName =  mHeaderView.findViewById(R.id.tvName);
        ivBg =  mHeaderView.findViewById(R.id.ivBg);
        RelativeLayout.LayoutParams l = (RelativeLayout.LayoutParams) rlHead.getLayoutParams();
        l.width=d.getWidth();
        l.height=d.getHeight()/6*2;
        height=mHeaderView.getHeight();
        rlHead.setLayoutParams(l);
        mAdapter.addHeaderView(mHeaderView);

        if(targetId.equals(SharedPreferencesUtil.getString(mContext,"userId",""))){
            ivBg.setOnClickListener(v -> {
                riv_avatar.setFocusable(true);
                tvName.setFocusable(true);
                new ActionSheetDialog(mContext).builder().setCancelable(true).setCanceledOnTouchOutside(true).addSheetItem("更换封面", ActionSheetDialog.SheetItemColor.Black, new ActionSheetDialog.OnSheetItemClickListener() {
                    @Override
                    public void onClick(int which) {
                        choosePhoto();
                    }
                }).show();
            });
        }else {
            //return;
        }
        riv_avatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Uri myUri = Uri.parse(userpic);
                UserInfo userInfo = new UserInfo(userid, userName, TextUtils.isEmpty(userpic.toString()) ? Uri.parse(RongGenerate.generateDefaultAvatar(userName,userid)) : myUri);
                Intent intent = new Intent(mContext, UserDetailActivity.class);
                Friend friend = CharacterParser.getInstance().generateFriendFromUserInfo(userInfo);
                intent.putExtra("friend", friend);
                intent.putExtra("type", CLICK_CONVERSATION_USER_PORTRAIT);
                intent.putExtra("conversationType","PRIVATE");
                startActivity(intent);
            }
        });
        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @SuppressLint("NewApi")
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                ScrollUnm = ScrollUnm + dy; //滑动距离总合
                Logger.e("dy",dy+"");
                Logger.e("overallXScroll",ScrollUnm+"");
                if (ScrollUnm<=0){  //在顶部时完全透明
                    //mRlTop.setBackgroundColor(Color.argb((int) 0, 62, 182, 234));
//                    getWindow().setNavigationBarColor(getResources().getColor(R.color.transparent));
//                    getWindow().setStatusBarColor(getResources().getColor(R.color.transparent));
                    mRlTop.setBackgroundColor(Color.argb((int)255, 62, 182, 234));
                    getWindow().setNavigationBarColor(getResources().getColor(R.color.black));
                    getWindow().setStatusBarColor(getResources().getColor(R.color.theme_color));
                }else if (ScrollUnm>0&&ScrollUnm<=height){  //在滑动高度中时，设置透明度百分比（当前高度/总高度）
                    double d = (double) ScrollUnm / height;
                    double alpha = (d*255);
                    mRlTop.setBackgroundColor(Color.argb((int) alpha, 62, 182, 234));
                    getWindow().setNavigationBarColor(getResources().getColor(R.color.black));
                    getWindow().setStatusBarColor(getResources().getColor(R.color.theme_color));
                }else{ //滑出总高度 完全不透明
                    mRlTop.setBackgroundColor(Color.argb((int) 255, 62, 182, 234));
                    getWindow().setNavigationBarColor(getResources().getColor(R.color.black));
                    getWindow().setStatusBarColor(getResources().getColor(R.color.theme_color));
                }

            }
        });
        // mRecyclerView.addOnLayoutChangeListener(this);
        mRecyclerView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if (llComment.getVisibility() == View.VISIBLE) {
                    updateEditTextBodyVisible(View.GONE);
                    return true;
                }
                return false;
            }
        });
        mAdapter.setOnItemChildClickListener(new BaseQuickAdapter.OnItemChildClickListener() {
            @Override
            public void onItemChildClick(BaseQuickAdapter adapter, View view, int position) {
                circleFriendsModel1 = (CircleFriendsModel1) adapter.getItem(position);
                String islike=circleFriendsModel1.getIsLikeit();
                List<CircleFriendsModel1.LikeitBean> likeitBeans=circleFriendsModel1.getLikeit();
                int LikeitCount=circleFriendsModel1.getLikeitCount();
                id=circleFriendsModel1.getID();
                TextView tvLike=view.findViewById(R.id.tvLike);
                CommentsView mCommentView=view.findViewById(R.id.commentView);
                LikesView likeView=view.findViewById(R.id.likeView);
                TextView tvMessage=view.findViewById(R.id.tvMessage);
                switch (view.getId()){
                    case R.id.tvLike:
                        CircleFriendsModel1.LikeitBean likeitBean=new CircleFriendsModel1.LikeitBean();
                        likeitBean.setUserId(SharedPreferencesUtil.getString(mContext,"userId",""));
                        likeitBean.setUserNickName(SharedPreferencesUtil.getString(mContext,"userName",""));
                        likeitBean.setUserPic(SharedPreferencesUtil.getString(mContext,"userpic",""));
                        if("1".equals(islike)){
                            tvLike.setCompoundDrawablesWithIntrinsicBounds(mContext.getResources().getDrawable(R.mipmap.icon_unlike), null, null, null);
                            circleFriendsModel1.setIsLikeit("0");
                            islike="0";
                            for (int i=0;i<likeitBeans.size();i++){
                                if(likeitBeans.get(i).getUserId().equals(likeitBean.getUserId())){
                                    likeitBeans.remove(i);
                                }
                            }
                            LikeitCount--;
                            circleFriendsModel1.setLikeitCount(LikeitCount);
                            circleFriendsModel1.setLikeit(likeitBeans);
                            toLike(islike,id);
                        }else {
                            tvLike.setCompoundDrawablesWithIntrinsicBounds(mContext.getResources().getDrawable(R.mipmap.icon_like), null, null, null);
                            islike="1";
                            circleFriendsModel1.setIsLikeit("1");
                            if(LikeitCount==0){
                                likeitBeans=new ArrayList<>();
                            }
                            likeitBeans.add(likeitBean);
                            circleFriendsModel1.setLikeit(likeitBeans);
                            LikeitCount++;
                            circleFriendsModel1.setLikeitCount(LikeitCount);
                            toLike(islike,id);
                        }
                        break;
                    case R.id.tvDel:
                        CommonDialog.Builder  builder = new CommonDialog.Builder();
                        builder.setContentMessage("是否删除该条朋友圈?");
                        builder.setDialogButtonClickListener(new CommonDialog.OnDialogButtonClickListener() {
                            @Override
                            public void onPositiveClick(View v, Bundle bundle) {
                                //logout();
                                // 通知退出
                                data.remove(position);
                                toDel(userid,id);
                            }
                            @Override
                            public void onNegativeClick(View v, Bundle bundle) {

                            }
                        });
                        CommonDialog dialog = builder.build();
                        dialog.show(getSupportFragmentManager(), "logout_dialog");
                        break;
                    case R.id.commentView:
                        break;
                    case R.id.ivAvatar:
                        Uri myUri = Uri.parse(circleFriendsModel1.getUserPic());
                        UserInfo userInfo = new UserInfo(circleFriendsModel1.getUserId(), circleFriendsModel1.getUserNickName(), TextUtils.isEmpty(circleFriendsModel1.getUserPic().toString()) ? Uri.parse(RongGenerate.generateDefaultAvatar(circleFriendsModel1.getUserNickName(), circleFriendsModel1.getUserId())) : myUri);
                        Intent intent = new Intent(mContext, UserDetailActivity.class);
                        Friend friend = CharacterParser.getInstance().generateFriendFromUserInfo(userInfo);
                        intent.putExtra("friend", friend);
                        intent.putExtra("created", "0");
                        intent.putExtra("type", CLICK_CONVERSATION_USER_PORTRAIT);
                        intent.putExtra("conversationType","PRIVATE");
                        startActivity(intent);
                        break;
                    case R.id.likeView:
                        likeView.setListener(new LikesView.onItemClickListener() {
                            @Override
                            public void onItemClick(int position, CircleFriendsModel1.LikeitBean bean) {
                                Uri myUri = Uri.parse(bean.getUserPic());
                                UserInfo userInfo = new UserInfo(bean.getUserId(), bean.getUserNickName(), TextUtils.isEmpty(bean.getUserPic().toString()) ? Uri.parse(RongGenerate.generateDefaultAvatar(bean.getUserNickName(), bean.getUserId())) : myUri);
                                Intent intent = new Intent(mContext, UserDetailActivity.class);
                                Friend friend = CharacterParser.getInstance().generateFriendFromUserInfo(userInfo);
                                intent.putExtra("friend", friend);
                                intent.putExtra("type", CLICK_CONVERSATION_USER_PORTRAIT);
                                intent.putExtra("conversationType","PRIVATE");
                                intent.putExtra("created", "0");
                                startActivity(intent);
                            }
                        });
                        break;
                    case R.id.tvMessage:
                        flag=0;
                        FriendId="";
                        final int itemBottomY = getCoordinateY(tvMessage) + tvMessage.getHeight();//item 底部y坐标
                        setViewTreeObserver();
                        llComment.setVisibility(View.VISIBLE);
                        etComment.requestFocus();
                        CommonUtils.showSoftInput(mContext, llComment);
                        etComment.setHint("评论...");
                        etComment.setText("");
                        tvMessage.postDelayed(() -> {
                            int y = getCoordinateY(llComment);
                            //评论时滑动到对应item底部和输入框顶部对齐
                            mRecyclerView.smoothScrollBy(0,itemBottomY-y);
                        }, 300);
                        break;
                }
            }
        });
//        mAdapter.setOnItemClickListener(new CircleFriendsAdapter.OnItemClickListener() {
//            @Override
//            public void onItemClick(View v, CircleFriendsModel1 circleFriendsModel1s) {
//                flag=0;
//                FriendId="";
//                circleFriendsModel1=circleFriendsModel1s;
//                id=circleFriendsModel1s.getID();
//                final int itemBottomY = getCoordinateY(v) + v.getHeight();//item 底部y坐标
//                setViewTreeObserver();
//                llComment.setVisibility(View.VISIBLE);
//                etComment.requestFocus();
//                CommonUtils.showSoftInput(mContext, llComment);
//                etComment.setHint("评论...");
//                etComment.setText("");
//                v.postDelayed(() -> {
//                    int y = getCoordinateY(llComment);
//                    //评论时滑动到对应item底部和输入框顶部对齐
//                    mRecyclerView.smoothScrollBy(0,itemBottomY-y);
//                }, 300);
//            }
//        });
        tvSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (TextUtils.isEmpty(etComment.getText().toString())) {
                    NToast.shortToast(mContext,"请输入评论内容");
                    return;
                }
                setViewTreeObserver();

                toComment(id,etComment.getText().toString(),FriendId,circleFriendsModel1);

            }
        });
    }

    /**
     * 跳转发布页面
     */
    private void toPostActivity() {
        Intent intent = new Intent(mContext, PostDynamic01Activity.class);
        startActivityForResult(intent, REQUEST_CODE_ACTIVITY);
    }

    /**
     * 评论列表点击事件
     * @param carrier
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void handleEvent(EventBusCarrier carrier) {
        CircleFriendsModel1.CommentBean commentBean=carrier.getCommentBean();
        circleFriendsModel1=carrier.getCircleFriendsModel1();
        int position=carrier.getPosition();
        CommentsView commentsView=carrier.getView();
        int type=carrier.getType();//0点击用户名 1点击评论内容 3点击的用户自己
        if(type==1) {
            if(commentBean.getMyuser().getUserId().equals(SharedPreferencesUtil.getString(mContext,"userId",""))){
                new ActionSheetDialog(mContext).builder().setCancelable(true).setCanceledOnTouchOutside(true).addSheetItem("删除", ActionSheetDialog.SheetItemColor.Red, new ActionSheetDialog.OnSheetItemClickListener() {
                    @Override
                    public void onClick(int which) {
                        toDelCom(commentBean.getComId(),circleFriendsModel1,commentBean);
                    }
                }).show();
            }else {
                flag=1;
                FriendId=commentBean.getMyuser().getUserId();
                id=circleFriendsModel1.getID();
                final int itemBottomY = getCoordinateY(commentsView) + commentsView.getHeight();//item 底部y坐标
                setViewTreeObserver();
                llComment.setVisibility(View.VISIBLE);
                etComment.requestFocus();
                CommonUtils.showSoftInput(mContext, llComment);
                etComment.setHint("回复" + commentBean.getFriuser().getUserName());
                etComment.setText("");
                commentsView.postDelayed(() -> {
                    int y = getCoordinateY(llComment);
                    //评论时滑动到对应item底部和输入框顶部对齐
                    mRecyclerView.smoothScrollBy(0,itemBottomY-y);
                }, 300);
            }
        }
        else if(type==0){
            Logger.d("userName:-->"+commentBean.getFriuser().getUserPic());
            Uri myUri = Uri.parse(commentBean.getFriuser().getUserPic());
            UserInfo userInfo = new UserInfo( commentBean.getFriuser().getUserId(),  commentBean.getFriuser().getUserName(), TextUtils.isEmpty(commentBean.getFriuser().getUserPic().toString()) ? Uri.parse(RongGenerate.generateDefaultAvatar(commentBean.getFriuser().getUserName(), commentBean.getFriuser().getUserId())) : myUri);
            Intent intent = new Intent(mContext, UserDetailActivity.class);
            Friend friend = CharacterParser.getInstance().generateFriendFromUserInfo(userInfo);
            intent.putExtra("friend", friend);
            intent.putExtra("created", "0");
            intent.putExtra("type", CLICK_CONVERSATION_USER_PORTRAIT);
            intent.putExtra("conversationType","PRIVATE");
            startActivity(intent);

        }else if(type==3){
            Logger.d("userName:-->"+commentBean.getMyuser().getUserPic());
            Uri myUri = Uri.parse(commentBean.getMyuser().getUserPic());
            UserInfo userInfo = new UserInfo( commentBean.getMyuser().getUserId(),  commentBean.getMyuser().getUserName(), TextUtils.isEmpty(commentBean.getMyuser().getUserPic().toString()) ? Uri.parse(RongGenerate.generateDefaultAvatar(commentBean.getMyuser().getUserName(), commentBean.getMyuser().getUserId())) : myUri);
            Intent intent = new Intent(mContext, UserDetailActivity.class);
            Friend friend = CharacterParser.getInstance().generateFriendFromUserInfo(userInfo);
            intent.putExtra("friend", friend);
            intent.putExtra("created", "0");
            intent.putExtra("type", CLICK_CONVERSATION_USER_PORTRAIT);
            intent.putExtra("conversationType","PRIVATE");
            startActivity(intent);
        }


    }


    private void initAdapter() {
        mAdapter = new CircleFriendsAdapter(R.layout.circle_friends_items, data);
        mAdapter.setOnLoadMoreListener(this, mRecyclerView);
        mRecyclerView.setAdapter(mAdapter);
        mAdapter.disableLoadMoreIfNotFullPage();
        initData(pageNum, false);
        LoadDialog.show(mContext,"Loading...",false);
        mCurrentCounter = mAdapter.getData().size();
    }
    private void initData(int pageNum, final boolean isloadmore) {
        Logger.d("Page：--》"+pageNum);
        RequestParams params=new RequestParams();
        params.put("UserId", SharedPreferencesUtil.getString(mContext,"userId",""));
        params.put("FriendId", targetId);
        params.put("pageIndex",String.valueOf(pageNum));
        HttpUtil.post(BaseAction.DOMAIN+"/Dynamic.ashx?Method=Get_SinglyDynamic", params, new HttpUtil.RequestListener() {
            @Override
            public void success(String response) {
                LoadDialog.dismiss(mContext);
                try {
                    JSONObject object=new JSONObject(response);
                    if(object.optBoolean("Success")==true){
                        List<CircleFriendsModel1> datas=new ArrayList<>();
                        objects=object.optJSONObject("ResultData");
                        count=objects.optInt("count");
                        // count=0;
                        JSONArray array=objects.optJSONArray("UserCirFriend");
                        if(array.length()!=0) {
                            if (count != 0) {
                                for (int i = 0; i < array.length(); i++) {
                                    JSONObject object1 = array.optJSONObject(i);
                                    CircleFriendsModel1 circleFriendsModel1 = new CircleFriendsModel1();
                                    circleFriendsModel1.setID(object1.optString("ID"));
                                    circleFriendsModel1.setUserId(object1.optString("UserId"));
                                    circleFriendsModel1.setUserPic(object1.optString("UserPic"));
                                    circleFriendsModel1.setUserNickName(object1.optString("UserNickName"));
                                    circleFriendsModel1.setReleaseDate(object1.optString("ReleaseDate"));
                                    circleFriendsModel1.setContent(object1.optString("Content"));
                                    circleFriendsModel1.setRemark(object1.optString("Remark"));
                                    circleFriendsModel1.setLocation(object1.optString("Location"));
                                    circleFriendsModel1.setLinksCount(object1.optInt("LinksCount"));
                                    circleFriendsModel1.setIsLikeit(object1.optString("IsLikeit"));
                                    circleFriendsModel1.setLikeitCount(object1.optInt("LikeitCount"));
                                    circleFriendsModel1.setCommentCount(object1.optInt("CommentCount"));
                                    List<String> imgas = new ArrayList<>();
                                    if (object1.optInt("LinksCount") != 0) {
                                        JSONArray array1Image = object1.optJSONArray("Links");
                                        for (int z = 0; z < array1Image.length(); z++) {
                                            imgas.add(array1Image.optJSONObject(z).optString("Links"));
                                        }
                                        circleFriendsModel1.setLinks(imgas);
                                    } else {
                                        circleFriendsModel1.setLikeit(null);
                                    }
                                    List<CircleFriendsModel1.LikeitBean> likeitBeans = new ArrayList<>();
                                    if (object1.optInt("LikeitCount") != 0) {
                                        JSONArray array1 = object1.optJSONArray("Likeit");
                                        for (int j = 0; j < array1.length(); j++) {
                                            CircleFriendsModel1.LikeitBean likeitBean = new CircleFriendsModel1.LikeitBean();
                                            JSONObject listLike = array1.optJSONObject(j);
                                            likeitBean.setUserId(listLike.optString("UserId"));
                                            likeitBean.setUserPic(listLike.optString("UserPic"));
                                            likeitBean.setUserNickName(listLike.optString("UserNickName"));
                                            likeitBeans.add(likeitBean);
                                        }
                                        circleFriendsModel1.setLikeit(likeitBeans);
                                    } else {
                                        circleFriendsModel1.setLikeit(null);
                                    }

                                    List<CircleFriendsModel1.CommentBean> commentBeans = new ArrayList<>();
                                    if (object1.optInt("CommentCount") != 0) {
                                        JSONArray array2 = object1.optJSONArray("Comment");
                                        for (int k = 0; k < array2.length(); k++) {
                                            CircleFriendsModel1.CommentBean commentBean = new CircleFriendsModel1.CommentBean();
                                            JSONObject commentList = array2.optJSONObject(k);
                                            commentBean.setComDate(commentList.optString("ComDate"));
                                            commentBean.setContent(commentList.optString("Content"));
                                            commentBean.setComId(commentList.optString("ComId"));
                                            ComUserBean Myuser = new ComUserBean();
                                            Myuser.setUserId(commentList.optString("UserId"));
                                            Myuser.setUserName(commentList.optString("UserNickName"));
                                            Myuser.setUserPic(commentList.optString("UserPic"));
                                            commentBean.setMyuser(Myuser);
                                            ComUserBean Friuser = new ComUserBean();
                                            Friuser.setUserId(commentList.optString("FriendId"));
                                            Friuser.setUserName(commentList.optString("FriendName"));
                                            Friuser.setUserPic(commentList.optString("FriendPic"));
                                            commentBean.setFriuser(Friuser);
                                            commentBeans.add(commentBean);
                                        }
                                        circleFriendsModel1.setComment(commentBeans);
                                    } else {
                                        circleFriendsModel1.setComment(null);
                                    }
                                    datas.add(circleFriendsModel1);
                                }
                                if (!isloadmore) {
                                    data = datas;
                                } else {
                                    data.addAll(datas);
                                }
                                mAdapter.setNewData(data);
                                mCurrentCounter = data.size();
                                mAdapter.notifyDataSetChanged();
                            }
                        }  else {
                            mAdapter.notifyDataSetChanged();
                            mAdapter.addFooterView(mFooterView);
                        }
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void failed(Throwable e) {
                LoadDialog.dismiss(mContext);
                mAdapter.notifyDataSetChanged();
                mAdapter.addFooterView(mFooterView);
            }
        });
    }

    /**
     * 加载更多
     */
    @Override
    public void onLoadMoreRequested() {
        TOTAL_COUNTER = objects.optInt("count");
        mRecyclerView.postDelayed(new Runnable() {
            @Override
            public void run() {
                // mCurrentCounter=12;
                if (mCurrentCounter >= TOTAL_COUNTER) {
                    //数据全部加载完毕
                    mAdapter.loadMoreEnd();
                } else {
                    if (!isErr) {
                        //成功获取更多数据
                        //  mQuickAdapter.addData(DataServer.getSampleData(PAGE_SIZE));
                        pageNum += 1;
                        initData(pageNum, true);
                        mAdapter.loadMoreComplete();
                    } else {
                        //获取更多数据失败
                        isErr = true;
                        mAdapter.loadMoreFail();

                    }
                }
            }

        }, 1500);
    }


    /**
     * 下拉刷新
     */
    @Override
    public void onRefresh() {
        LoadDialog.show(mContext,"loading...",false);
        mAdapter.setEnableLoadMore(false);//禁止加载
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                // mAdapter.setNewData(data);
                isErr = false;
                mCurrentCounter = 0;
                pageNum = 1;//页数置为1 才能继续重新加载

                initData(pageNum, false);
                mSwipeRefreshLayout.setRefreshing(false);
                mAdapter.setEnableLoadMore(true);//启用加载
            }
        }, 1500);
    }

    /**
     * 点赞接口
     * @param id
     * @param islike
     */
    private void toLike(String islike,String id ) {
        // LoadDialog.show(mContext,"loading...",false);
        AsyncTaskManager.getInstance(mContext).request(TOLIKE, new OnDataListener() {
            @Override
            public Object doInBackground(int requestCode, String parameter) throws HttpException {
                return new SealAction(mContext).click_support(SharedPreferencesUtil.getString(mContext,"userId",""),id,islike);
            }

            @Override
            public void onSuccess(int requestCode, Object result) {
                //LoadDialog.dismiss(mContext);
                if (result != null) {
                    ClickSupportRespone clickSupportRespone= (ClickSupportRespone) result;
                    if(clickSupportRespone.isSuccess()==true){
                        mAdapter.notifyDataSetChanged();
                    }else {
                        //NToast.shortToast(mContext,clickSupportRespone.getMessage());
                    }
                }
            }

            @Override
            public void onFailure(int requestCode, int state, Object result) {
                LoadDialog.dismiss(mContext);
            }
        });
    }

    /**
     * 删除朋友圈
     * @param userid
     * @param id
     */
    private void toDel(String userid, String id) {
        LoadDialog.show(mContext,"loading...",false);
        AsyncTaskManager.getInstance(mContext).request(DELCIR, new OnDataListener() {
            @Override
            public Object doInBackground(int requestCode, String parameter) throws HttpException {
                return new SealAction(mContext).DeleteCirFriend(targetId,id);
            }

            @Override
            public void onSuccess(int requestCode, Object result) {
                LoadDialog.dismiss(mContext);
                if (result != null) {
                    DeleteCirFriendRespone clickSupportRespone= (DeleteCirFriendRespone) result;
                    if(clickSupportRespone.isSuccess()==true){
                        mAdapter.notifyDataSetChanged();
                        NToast.shortToast(mContext,clickSupportRespone.getMessage());
                    }else {
                        NToast.shortToast(mContext,clickSupportRespone.getMessage());
                    }
                }
            }

            @Override
            public void onFailure(int requestCode, int state, Object result) {
                LoadDialog.dismiss(mContext);
            }
        });

    }

    /**
     * 评论
     * @param CirFrendId
     * @param content
     * @param FriendId
     * @param circleFriendsModel1
     */
    private void toComment(String CirFrendId, String content, String FriendId, CircleFriendsModel1 circleFriendsModel1) {
        LoadDialog.show(mContext,"loading...",false);
        AsyncTaskManager.getInstance(mContext).request(CirFriendCom, new OnDataListener() {
            @Override
            public Object doInBackground(int requestCode, String parameter) throws HttpException {
                return new SealAction(mContext).CirFriendCom(SharedPreferencesUtil.getString(mContext,"userId",""),CirFrendId,content,FriendId);
            }

            @Override
            public void onSuccess(int requestCode, Object result) {
                LoadDialog.dismiss(mContext);
                if (result != null) {
                    CirFriendComRespone cirFriendComRespone= (CirFriendComRespone) result;
                    if(cirFriendComRespone.isSuccess()==true){
                        List<CircleFriendsModel1.CommentBean> list=circleFriendsModel1.getComment();
                        int CommentCount=circleFriendsModel1.getCommentCount();
                        if(CommentCount==0){
                            list=new ArrayList<>();
                        }
                        CircleFriendsModel1.CommentBean commentBean=new CircleFriendsModel1.CommentBean();
                        commentBean.setComDate(cirFriendComRespone.getResultData().getComDate());
                        commentBean.setContent(cirFriendComRespone.getResultData().getContent());
                        commentBean.setComId(cirFriendComRespone.getResultData().getComId());
                        ComUserBean FriUser=new ComUserBean();
                        FriUser.setUserId(cirFriendComRespone.getResultData().getFriendId());
                        FriUser.setUserName(cirFriendComRespone.getResultData().getFriendName());
                        FriUser.setUserPic(cirFriendComRespone.getResultData().getFriendPic());
                        commentBean.setFriuser(FriUser);
                        ComUserBean MyUser=new ComUserBean();
                        MyUser.setUserId(cirFriendComRespone.getResultData().getUserId());
                        MyUser.setUserName(cirFriendComRespone.getResultData().getUserNickName());
                        MyUser.setUserPic(cirFriendComRespone.getResultData().getUserPic());
                        commentBean.setMyuser(MyUser);
                        list.add(commentBean);
                        CommentCount++;
                        circleFriendsModel1.setCommentCount(CommentCount);
                        circleFriendsModel1.setComment(list);
                        mAdapter.notifyDataSetChanged();
                    }else {
                        //NToast.shortToast(mContext,cirFriendComRespone.getMessage());
                    }
                    updateEditTextBodyVisible(View.GONE);
                }
            }

            @Override
            public void onFailure(int requestCode, int state, Object result) {
                LoadDialog.dismiss(mContext);
            }
        });
    }

    /**
     * 刪除自己的評論
     * @param comId
     * @param circleFriendsModel1
     * @param commentBean
     */
    private void toDelCom(String comId, CircleFriendsModel1 circleFriendsModel1, CircleFriendsModel1.CommentBean commentBean) {
        LoadDialog.show(mContext,"loading...",false);
        AsyncTaskManager.getInstance(mContext).request(DELCOM, new OnDataListener() {
            @Override
            public Object doInBackground(int requestCode, String parameter) throws HttpException {
                return new SealAction(mContext).DeleteCom(targetId,comId);
            }
            @Override
            public void onSuccess(int requestCode, Object result) {
                LoadDialog.dismiss(mContext);
                if (result != null) {
                    DeleteComRespone deleteComRespone= (DeleteComRespone) result;
                    if(deleteComRespone.isSuccess()==true){
                        mAdapter.notifyDataSetChanged();
                        List<CircleFriendsModel1.CommentBean> list=circleFriendsModel1.getComment();
                        int CommentCount=circleFriendsModel1.getCommentCount();
                        for(int i=0;i<list.size();i++){
                            if(list.get(i).getComId().equals(commentBean.getComId())){
                                list.remove(commentBean);
                            }
                        }
                        CommentCount--;
                        // list.add(commentBean);
                        circleFriendsModel1.setComment(list);
                        circleFriendsModel1.setCommentCount(CommentCount);
                    }else {
                        //NToast.shortToast(mContext,cirFriendComRespone.getMessage());
                    }
                }
            }

            @Override
            public void onFailure(int requestCode, int state, Object result) {
                LoadDialog.dismiss(mContext);
            }
        });
    }




    /**
     * 开启图片选择器
     */
    private void choosePhoto() {
        ImgSelConfig config = new ImgSelConfig.Builder(loader)
                // 是否多选
                .multiSelect(false)
                // 确定按钮背景色
                .btnBgColor(Color.TRANSPARENT)
                .titleBgColor(ContextCompat.getColor(this, R.color.theme_color))
                // 使用沉浸式状态栏
                .statusBarColor(ContextCompat.getColor(this, R.color.theme_color))
                // 返回图标ResId
                .backResId(R.drawable.ic_back)
                .needCrop(true)
                .cropSize(1, 1, 500, 500)
                .title("图片")
                // 第一个是否显示相机
                .needCamera(true)
                // 最大选择图片数量
                .maxNum(9 )
                .build();
        ImgSelActivity.startActivity(this, config, REQUEST_CODE);
    }

    private com.yuyh.library.imgsel.ImageLoader loader = new com.yuyh.library.imgsel.ImageLoader() {
        @Override
        public void displayImage(Context context, String path, ImageView imageView) {
            ImageLoaderUtils.display(context, imageView, path);
        }
    };
    //    @Override
//    public void onLayoutChange(View v, int left, int top, int right, int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {
//        //old是改变前的左上右下坐标点值，没有old的是改变后的左上右下坐标点值 //现在认为只要控件将Activity向上推的高度超过了1/4屏幕高，就认为软键盘弹起
//        int keyHeight =getWindowHeight(this) / 4;
//        if (oldBottom != 0 && bottom != 0 && (oldBottom - bottom > keyHeight)) {
//            //软键盘弹起状态
//        } else if (oldBottom != 0 && bottom != 0 && (bottom - oldBottom > keyHeight)) {
//            //软键盘关闭状态
//            replyDialog.dismiss();
//        }
//    }
    /*
     *
     * 获取屏幕的高度
     * 	@param activity Activity
     */
    public static int getWindowHeight(Context context) {
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        return wm.getDefaultDisplay().getHeight();
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case 1:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if(UtilsClick.isNotFastClick()){
                        toPostActivity();}
                } else {
                    Toast.makeText(this, "你拒绝了权限申请，可能无法打开相机扫码哟！", Toast.LENGTH_SHORT).show();
                }
                break;
            default:
        }
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode){
            case REQUEST_CODE_ACTIVITY:
                onRefresh();
                break;
            case REQUEST_CODE:
                if(resultCode == RESULT_OK && data != null){
                    ArrayList<String> pathList = data.getStringArrayListExtra(ImgSelActivity.INTENT_RESULT);
                    Uri uri= Uri.parse(pathList.get(0));
                    Bitmap bmp = BitmapFactory.decodeFile(uri.toString());//filePath
                    base64= ImageUtils.bitmapToBase64(bmp);
                    ivBg.setImageBitmap(bmp);
                    request(UPLOAD,true);

                    Logger.d("pathList:-->"+pathList);}
                break;
        }
        PhotoUtils.getInstance().bindForResult(requestCode, resultCode, data);
    }


    @Override
    public Object doInBackground(int requestCode, String id) throws HttpException {
        switch (requestCode) {
            case UPLOAD:
                return  action.setPortrait(base64);
            case UPLOADPIC:
                return action.setCirFriedPic(SharedPreferencesUtil.getString(mContext,"userId",""),pic);
        }
        return super.doInBackground(requestCode, id);

    }
    @Override
    public void onSuccess(int requestCode, Object result) {
        if (result != null) {
            switch (requestCode) {
                case UPLOAD:
                    SetPortraitResponse setPortraitResponse= (SetPortraitResponse) result;
                    if(setPortraitResponse.isSuccess()==true){
                        pic=setPortraitResponse.getResultData();
                        LoadDialog.show(mContext,"loading...",false);
                        request(UPLOADPIC,true);
                    }else {
                        LoadDialog.dismiss(mContext);
                        NToast.shortToast(mContext, setPortraitResponse.getMessage());
                    }
                    break;
                case UPLOADPIC:
                    CirFriedPicRespone cirFriedPicRespone= (CirFriedPicRespone) result;
                    LoadDialog.dismiss(mContext);
                    if(cirFriedPicRespone.isSuccess()==true){
                        NToast.shortToast(mContext,cirFriedPicRespone.getMessage());
                    }else {
                        NToast.shortToast(mContext,cirFriedPicRespone.getMessage());
                    }

                    break;
            }
        }
    }

    private void setViewTreeObserver() {
        final ViewTreeObserver swipeRefreshLayoutVTO = llScroll.getViewTreeObserver();
        swipeRefreshLayoutVTO.addOnGlobalLayoutListener(() -> {
            Rect r = new Rect();
            llScroll.getWindowVisibleDisplayFrame(r);
            int statusBarH = Utils.getStatusBarHeight();//状态栏高度
            int screenH = llScroll.getRootView().getHeight();
            if (r.top != statusBarH) {
                //在这个demo中r.top代表的是状态栏高度，在沉浸式状态栏时r.top＝0，通过getStatusBarHeight获取状态栏高度
                r.top = statusBarH;
            }
            int keyboardH = screenH - (r.bottom - r.top);
            //Log.d(TAG, "screenH＝ " + screenH + " &keyboardH = " + keyboardH + " &r.bottom=" + r.bottom + " &top=" + r.top + " &statusBarH=" + statusBarH);

            if (keyboardH == currentKeyboardH) {//有变化时才处理，否则会陷入死循环
                return;
            }
            currentKeyboardH = keyboardH;
            // currentKeyboardH = getWindowHeight(mContext) / 3;
            screenHeight = screenH;//应用屏幕的高度
            editTextBodyHeight = llComment.getHeight();
            if (keyboardH < 150) {//说明是隐藏键盘的情况
                CircleOfFriendsActivity.this.updateEditTextBodyVisible(View.GONE);
                return;
            }
        });
    }
    public void updateEditTextBodyVisible(int visibility) {
        llComment.setVisibility(visibility);
        if (View.VISIBLE == visibility) {
            llComment.requestFocus();
            //弹出键盘
            CommonUtils.showSoftInput(etComment.getContext(), etComment);

        } else if (View.GONE == visibility) {
            //隐藏键盘
            CommonUtils.hideSoftInput(etComment.getContext(), etComment);
        }
    }
    /**
     * 获取控件左上顶点Y坐标
     *
     * @param view
     * @return
     */
    private int getCoordinateY(View view) {
        int[] coordinate = new int[2];
        view.getLocationOnScreen(coordinate);
        return coordinate[1];
    }
    @Override
    public void onFailure(int requestCode, int state, Object result) {
        super.onFailure(requestCode, state, result);
        LoadDialog.dismiss(mContext);
    }

    @Override
    protected void onDestroy() {
        EventBus.getDefault().unregister(this); //解除注册
        super.onDestroy();
    }

}
