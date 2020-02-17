package com.bcr.jianxinIM.view;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;

import com.bcr.jianxinIM.R;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.bcr.jianxinIM.util.ImageLoaderUtil;
import com.bcr.jianxinIM.view.photoview.ViewPagerActivity;

import java.util.ArrayList;

import javax.annotation.Nullable;

public class NineGridTestLayout extends NineGridLayout {

    protected static final int MAX_W_H_RATIO = 3;

    public NineGridTestLayout(Context context) {
        super(context);
    }

    public NineGridTestLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected boolean displayOneImage(final ImageView imageView, String url, final int parentWidth) {
        RoundedCorners roundedCorners= new RoundedCorners(dip2px(mContext, 10));
        RequestOptions options = new RequestOptions()
                .centerCrop()
                .placeholder(R.color.white)
                // .bitmapTransform(roundedCorners)
                .diskCacheStrategy(DiskCacheStrategy.ALL);
        //通过RequestOptions扩展功能,override:采样率,因为ImageView就这么大,可以压缩图片,降低内存消耗
        // RequestOptions options=RequestOptions.bitmapTransform(roundedCorners).override(300, 300);
        Glide.with(mContext)
                .load(url)
                .apply(options)
                .into(imageView);



//        ImageLoaderUtil.displayImage(mContext, imageView, url, ImageLoaderUtil.getPhotoImageOption(), new ImageLoadingListener() {
//            @Override
//            public void onLoadingStarted(String imageUri, View view) {
//
//            }
//
//            @Override
//            public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
//
//            }
//
//            @Override
//            public void onLoadingComplete(String imageUri, View view, Bitmap bitmap) {
//                int w = bitmap.getWidth();
//                int h = bitmap.getHeight();
//                int newW;
//                int newH;
//                if (h > w * MAX_W_H_RATIO) {//h:w = 5:3
//                    newW = parentWidth / 2;
//                    newH = newW * 5 / 3;
//                } else if (h < w) {//h:w = 2:3
//                    newW = parentWidth * 2 / 3;
//                    newH = newW * 2 / 3;
//                } else {//newH:h = newW :w
//                    newW = parentWidth / 2;
//                    newH = h * newW / w;
//                }
//                setOneImageLayoutParams(imageView, newW, newH);
//            }
//
//            @Override
//            public void onLoadingCancelled(String imageUri, View view) {
//
//            }
//        });
//
       return false;
    }

    @Override
    protected void displayImage(ImageView imageView, String url) {
        //ImageLoaderUtil.getImageLoader(mContext).displayImage(url, imageView, ImageLoaderUtil.getPhotoImageOption());
//        CornerTransform transformation = new CornerTransform(mContext, dip2px(mContext, 10));
////只是绘制左上角和右上角圆角
//        transformation.setExceptCorner(false, false, false, false);

//        Glide.with(mContext)
//                .load(url)
//                .apply(options)
//                .into(imageView);

        RoundedCorners roundedCorners= new RoundedCorners(dip2px(mContext, 10));
        RequestOptions options = new RequestOptions()
                .centerCrop()
                .placeholder(R.color.white)
               // .bitmapTransform(roundedCorners)
                .diskCacheStrategy(DiskCacheStrategy.ALL);
        //通过RequestOptions扩展功能,override:采样率,因为ImageView就这么大,可以压缩图片,降低内存消耗
       // RequestOptions options=RequestOptions.bitmapTransform(roundedCorners).override(300, 300);
        Glide.with(mContext)
                .load(url)
                .apply(options)
                .into(imageView);
//        Glide.with(mContext).load(url).apply(options).listener(new RequestListener<Drawable>() {
//            @Override
//            public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
//               // LogTools.i(TAG,"Glide==onLoadFailed="+e.getMessage());
//                return false;
//            }
//
//            @Override
//            public boolean onResourceReady(Drawable drawable, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
//                imageView.setBackground(drawable);
//
//                return false;
//            }
//        }).submit();


    }
    public static int dip2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }
    @Override
    protected void onClickImage(int i, String url, ArrayList<String> urlList) {
      //  Toast.makeText(mContext, "点击了图片" + url, Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(mContext, ViewPagerActivity.class);
        intent.putStringArrayListExtra("items", urlList);
        intent.putExtra("currentPosition", i);
        mContext.startActivity(intent);


    }
}
