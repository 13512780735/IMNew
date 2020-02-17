package com.bcr.jianxinIM.activity.main;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bcr.jianxinIM.R;
import com.bcr.jianxinIM.im.ui.activity.BaseActivity;
import com.shuyu.gsyvideoplayer.builder.GSYVideoOptionBuilder;
import com.shuyu.gsyvideoplayer.listener.GSYSampleCallBack;
import com.shuyu.gsyvideoplayer.listener.LockClickListener;
import com.shuyu.gsyvideoplayer.utils.OrientationUtils;
import com.shuyu.gsyvideoplayer.video.StandardGSYVideoPlayer;

import io.rong.imageloader.core.ImageLoader;

public class CollectDetailsActivity extends BaseActivity {
    String Content,type,Conver;
    TextView tvContent;
    ImageView ivContent;
    private OrientationUtils orientationUtils;
    private StandardGSYVideoPlayer detailPlayer;
    private boolean isPlay;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_collect_details);
        setTitle("详情");
        Content =getIntent().getExtras().getString("content");
        type =getIntent().getExtras().getString("type");
        Conver =getIntent().getExtras().getString("Conver");
//        Logger.d("内容1：--》"+Content);
//        Logger.d("内容2：--》"+type);
       initView();
    }
    public void initView(){
        tvContent=findViewById(R.id.tvContent);
        ivContent=findViewById(R.id.ivContent);
        detailPlayer =  findViewById(R.id.detail_player);

        if("0".equals(type)){
            ivContent.setVisibility(View.GONE);
            detailPlayer.setVisibility(View.GONE);
            tvContent.setVisibility(View.VISIBLE);
            tvContent.setText(Content);
        }else if("1".equals(type)){
            tvContent.setVisibility(View.GONE);
            detailPlayer.setVisibility(View.GONE);
            ivContent.setVisibility(View.VISIBLE);
            ImageLoader.getInstance().displayImage(Content,ivContent);
        }else {
            detailPlayer.setVisibility(View.VISIBLE);
            tvContent.setVisibility(View.GONE);
            ivContent.setVisibility(View.GONE);

            ImageView imageView = new ImageView(mContext);
            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);

            ImageLoader.getInstance().displayImage(Conver,imageView);
            detailPlayer.getTitleTextView().setVisibility(View.GONE);
            detailPlayer.getBackButton().setVisibility(View.GONE);
            //外部辅助的旋转，帮助全屏
            orientationUtils = new OrientationUtils((Activity) mContext, detailPlayer);
            //初始化不打开外部的旋转
            orientationUtils.setEnable(false);
            GSYVideoOptionBuilder gsyVideoOption = new GSYVideoOptionBuilder();
            gsyVideoOption
                    .setThumbImageView(imageView)
                    .setIsTouchWiget(true)
                    .setRotateViewAuto(false)
                    .setLockLand(false)
                    .setAutoFullWithSize(true)
                    .setShowFullAnimation(false)
                    .setNeedLockFull(true)
                    .setUrl(Content)
                    .setCacheWithPlay(false)
                    .setVideoTitle("")
                    .setVideoAllCallBack(new GSYSampleCallBack() {
                        @Override
                        public void onPrepared(String url, Object... objects) {
                            super.onPrepared(url, objects);
                            //开始播放了才能旋转和全屏
                            orientationUtils.setEnable(true);
                            isPlay=true;
                        }

                        @Override
                        public void onQuitFullscreen(String url, Object... objects) {
                            super.onQuitFullscreen(url, objects);
                            if (orientationUtils != null) {
                                orientationUtils.backToProtVideo();
                            }
                        }
                    }).setLockClickListener(new LockClickListener() {
                @Override
                public void onClick(View view, boolean lock) {
                    if (orientationUtils != null) {
                        //配合下方的onConfigurationChanged
                        orientationUtils.setEnable(!lock);
                    }
                }
            }).build(detailPlayer);

            detailPlayer.getFullscreenButton().setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //直接横屏
                    orientationUtils.resolveByClick();
                    //第一个true是否需要隐藏actionbar，第二个true是否需要隐藏statusbar
                    detailPlayer.startWindowFullscreen(mContext, true, true);
                }
            });


        }
    }
}
