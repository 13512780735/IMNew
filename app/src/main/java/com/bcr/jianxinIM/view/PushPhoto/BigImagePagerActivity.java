package com.bcr.jianxinIM.view.PushPhoto;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.Nullable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import com.bcr.jianxinIM.R;
import com.bcr.jianxinIM.im.ui.activity.BaseActivity;
import com.bcr.jianxinIM.view.PushPhoto.view.ViewPagerFixed;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;

import java.util.ArrayList;
import java.util.List;

import uk.co.senab.photoview.PhotoView;
import uk.co.senab.photoview.PhotoViewAttacher;


public class BigImagePagerActivity extends BaseActivity {
    public static final String INTENT_IMGURLS = "imgurls";
    public static final String INTENT_POSITION = "position";
    private List<View> guideViewList = new ArrayList<View>();
    private LinearLayout guideGroup;

    public static void startImagePagerActivity(Activity activity, List<String> imgUrls, int position){
        Intent intent = new Intent(activity, BigImagePagerActivity.class);
        intent.putStringArrayListExtra(INTENT_IMGURLS, new ArrayList<String>(imgUrls));
        intent.putExtra(INTENT_POSITION, position);
        activity.startActivity(intent);
        activity.overridePendingTransition(R.anim.fade_in,
                R.anim.fade_out);
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_big_image_pager);
        ViewPager viewPager = (ViewPagerFixed) findViewById(R.id.pager);
        setTitle("预览图片");
        guideGroup = (LinearLayout) findViewById(R.id.guideGroup);

        int startPos = getIntent().getIntExtra(INTENT_POSITION, 0);
        ArrayList<String> imgUrls = getIntent().getStringArrayListExtra(INTENT_IMGURLS);

        ImageAdapter mAdapter = new ImageAdapter(this);
        mAdapter.setDatas(imgUrls);
        viewPager.setAdapter(mAdapter);
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {

            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                for(int i=0; i<guideViewList.size(); i++){
                    guideViewList.get(i).setSelected(i==position ? true : false);
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        viewPager.setCurrentItem(startPos);

        addGuideView(guideGroup, startPos, imgUrls);
    }

    /**
     * 监听返回键
     *
     * @param keyCode
     * @param event
     * @return
     */
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            BigImagePagerActivity.this.finish();
            BigImagePagerActivity.this.overridePendingTransition(R.anim.fade_in,
                    R.anim.fade_out);
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }


    private void addGuideView(LinearLayout guideGroup, int startPos, ArrayList<String> imgUrls) {
        if(imgUrls!=null && imgUrls.size()>0){
            guideViewList.clear();
            if(imgUrls.size()==9){
                for (int i=0; i<imgUrls.size(); i++){
                    View view = new View(this);
                    view.setBackgroundResource(R.drawable.selector_guide_bg);
                    view.setSelected(i==startPos ? true : false);
                    LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(getResources().getDimensionPixelSize(R.dimen.gudieview_width),
                            getResources().getDimensionPixelSize(R.dimen.gudieview_heigh));
                    layoutParams.setMargins(10, 0, 0, 0);
                    guideGroup.addView(view, layoutParams);
                    guideViewList.add(view);
                }
            }else {
                for (int i=0; i<imgUrls.size()-1; i++){
                    View view = new View(this);
                    view.setBackgroundResource(R.drawable.selector_guide_bg);
                    view.setSelected(i==startPos ? true : false);
                    LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(getResources().getDimensionPixelSize(R.dimen.gudieview_width),
                            getResources().getDimensionPixelSize(R.dimen.gudieview_heigh));
                    layoutParams.setMargins(10, 0, 0, 0);
                    guideGroup.addView(view, layoutParams);
                    guideViewList.add(view);
                }}
        }
    }

    private  class ImageAdapter extends PagerAdapter {

        private List<String> datas = new ArrayList<String>();
        private LayoutInflater inflater;
        private Context context;

        public void setDatas(List<String> datas) {
            if(datas != null )
                this.datas = datas;
        }

        public ImageAdapter(Context context){
            this.context = context;
            this.inflater = LayoutInflater.from(context);

        }

        @Override
        public int getCount() {
            if(datas == null) return 0;
            if(datas.size()==9) return datas.size();
            else
            return datas.size()-1;
        }


        @Override
        public Object instantiateItem(ViewGroup container, final int position) {
            View view = inflater.inflate(R.layout.item_pager_image, container, false);
            if(view != null){
                final PhotoView imageView = (PhotoView) view.findViewById(R.id.image);

                //单击图片退出
                imageView.setOnPhotoTapListener(new PhotoViewAttacher.OnPhotoTapListener() {
                    @Override
                    public void onPhotoTap(View view, float x, float y) {
                        BigImagePagerActivity.this.finish();
                        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                    }
                });

                //loading
                final ProgressBar loading = new ProgressBar(context);
                FrameLayout.LayoutParams loadingLayoutParams = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT);
                loadingLayoutParams.gravity = Gravity.CENTER;
                loading.setLayoutParams(loadingLayoutParams);
                ((FrameLayout)view).addView(loading);

                final String imgurl = datas.get(position);

               loading.setVisibility(View.VISIBLE);
                Glide.with(context).load(imgurl)
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .error(R.drawable.default_pic)
                        .thumbnail(0.1f)
                        .listener(new RequestListener<Drawable>() {
                            @Override
                            public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                                loading.setVisibility(View.GONE);
                                return false;
                            }

                            @Override
                            public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                                loading.setVisibility(View.GONE);
                                return false;
                            }

                        })
                        .into(imageView);
                container.addView(view, 0);
            }
            return view;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view.equals(object);
        }

        @Override
        public void restoreState(Parcelable state, ClassLoader loader) {
        }

        @Override
        public Parcelable saveState() {
            return null;
        }


    }
}
