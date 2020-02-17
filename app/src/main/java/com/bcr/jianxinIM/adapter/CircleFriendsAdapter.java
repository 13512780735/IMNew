package com.bcr.jianxinIM.adapter;

import android.content.Intent;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;


import com.bcr.jianxinIM.R;
import com.bcr.jianxinIM.im.server.widget.SelectableRoundedImageView;
import com.bcr.jianxinIM.model.CircleFriendsModel1;
import com.bcr.jianxinIM.util.ImageLoaderUtil;
import com.bcr.jianxinIM.util.SharedPreferencesUtil;
import com.bcr.jianxinIM.view.CircleImageView;
import com.bcr.jianxinIM.view.CommentsView;
import com.bcr.jianxinIM.view.LikesView;
import com.bcr.jianxinIM.view.NineGridTestLayout;
import com.bcr.jianxinIM.view.photoview.ViewPagerActivity;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.orhanobut.logger.Logger;

import java.util.ArrayList;
import java.util.List;

import io.rong.imageloader.core.ImageLoader;

public class CircleFriendsAdapter extends BaseQuickAdapter<CircleFriendsModel1, BaseViewHolder> {
    private static final int TOLIKE = 230;
    String likesNumber;
    private String islike;
    private int LikeitCount;
    private List<CircleFriendsModel1.LikeitBean> listLike;
    private int CommentCount;
    private CircleFriendsModel1 circleFriendsModel1;
    private ImageAdapter01 mAdapter;
    private ArrayList<String> urlLink;

    public CircleFriendsAdapter(int layoutResId, List<CircleFriendsModel1> data) {
        super(R.layout.circle_friends_items, data);
    }

    @Override
    protected void convert(BaseViewHolder helper, CircleFriendsModel1 item) {
        circleFriendsModel1=item;
        listLike = new ArrayList<>();
       NineGridTestLayout layout = helper.getView(R.id.layout_nine_grid);
       // urlLink= (ArrayList<String>) item.getLinks();
//        RecyclerView mRecyclerView=helper.getView(R.id.mRecyclerView);
//        FullyGridLayoutManager manager = new FullyGridLayoutManager(mContext, 3, GridLayoutManager.VERTICAL, false);
//        mRecyclerView.setLayoutManager(manager);
//        mAdapter = new ImageAdapter01(R.layout.layout_circle_recycler_items,urlLink);
//        mRecyclerView.setAdapter(mAdapter);
        for(int i=0;i<item.getLinks().size();i++){
        Logger.d("图片路径：--》"+item.getLinks().get(i));}
        CommentsView commentView = helper.getView(R.id.commentView);
        LikesView mLikeView = helper.getView(R.id.likeView);
        TextView tvLike = helper.getView(R.id.tvLike);
        TextView tvMessage = helper.getView(R.id.tvMessage);
        TextView telDel = helper.getView(R.id.tvDel);
        layout.setIsShowAll(item.isShowAll);
        layout.setUrlList(item.getLinks());

        if(TextUtils.isEmpty(item.getRemark())){
            helper.setText(R.id.tvName, item.getUserNickName());
        }else {
            helper.setText(R.id.tvName, item.getRemark());
        }


        helper.setText(R.id.tvContent, item.getContent());
        helper.setText(R.id.tvAddress, item.getLocation());
        helper.setText(R.id.tvTime, item.getReleaseDate());
        tvLike.setText(item.getLikeitCount() + "");
        tvMessage.setText( item.getCommentCount() + "");
//        if(item.getUserId().equals(SharedPreferencesUtil.getString(mContext,"userId",""))){
//
//        }else {
//            tvLike.setText("");
//            tvMessage.setText("");
//        }
//        mAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
//            @Override
//            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
//                Intent intent = new Intent(mContext, ViewPagerActivity.class);
//                intent.putStringArrayListExtra("items", urlLink);
//                intent.putExtra("currentPosition", position);
//                mContext.startActivity(intent);
//            }
//        });

        LikeitCount = item.getLikeitCount();
        CommentCount = item.getCommentCount();
        if (CommentCount == 0) {
            commentView.setVisibility(View.GONE);

        }else {
            commentView.setVisibility(View.VISIBLE);
            commentView.setList(item.getComment(),item,commentView);
            commentView.notifyDataSetChanged();
        }
        if ("1".equals(item.getIsLikeit())) {
            tvLike.setCompoundDrawablesWithIntrinsicBounds(mContext.getResources().getDrawable(R.mipmap.icon_like), null, null, null);
            islike = "1";
        } else {
            tvLike.setCompoundDrawablesWithIntrinsicBounds(mContext.getResources().getDrawable(R.mipmap.icon_unlike), null, null, null);
            islike = "0";
        }
        String userId = SharedPreferencesUtil.getString(mContext, "userId", "");
        if (item.getUserId().equals(userId)) {
            telDel.setVisibility(View.VISIBLE);
        } else {
            telDel.setVisibility(View.GONE);
        }
        mLikeView.setVisibility(View.VISIBLE);
        listLike = item.getLikeit();
        if (item.getLikeitCount() == 0) {
            mLikeView.setVisibility(View.GONE);
        } else {
            mLikeView.setVisibility(View.VISIBLE);
            mLikeView.setList(listLike);
            mLikeView.notifyDataSetChanged();
        }
        ImageLoader.getInstance().displayImage(item.getUserPic(), (CircleImageView) helper.getView(R.id.ivAvatar));
        helper.addOnClickListener(R.id.tvLike);
        helper.addOnClickListener(R.id.tvDel);
        helper.addOnClickListener(R.id.tvMessage);
        helper.addOnClickListener(R.id.ivAvatar);
        helper.addOnClickListener(R.id.likeView);
        helper.addOnClickListener(R.id.commentView);
//        tvMessage.setOnClickListener(v -> {
//            if (onItemClickListener != null) {
//                if(LikeitCount==0&&CommentCount==0){
//                onItemClickListener.onItemClick(tvMessage, circleFriendsModel1);}
//               else if(LikeitCount!=0&&CommentCount==0){
//                    onItemClickListener.onItemClick(mLikeView, circleFriendsModel1);
//                }else if(LikeitCount!=0&&CommentCount!=0){
//                   onItemClickListener.onItemClick(commentView,circleFriendsModel1);
//                }else if(LikeitCount==0&&CommentCount!=0){
//                    onItemClickListener.onItemClick(commentView,circleFriendsModel1);
//                }
//            }
//        });

    }
    private OnItemClickListener onItemClickListener = null;

    //setter方法
    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }
    //回调接口
    public interface OnItemClickListener {
        void onItemClick(View v, CircleFriendsModel1 circleFriendsModel1);
    }


    public class ImageAdapter01 extends BaseQuickAdapter<String, BaseViewHolder>{

        public ImageAdapter01(int layoutResId, List<String> data) {
            super(R.layout.layout_circle_recycler_items, data);
        }

        @Override
        protected void convert(BaseViewHolder helper, String item) {
            Logger.d("图片链接："+item);
            ImageView img=helper.getView(R.id.img_photo);
            ImageLoaderUtil.displayImage(mContext,img,item,ImageLoaderUtil.getPhotoImageOption1());
        }
    }
}
