package com.bcr.jianxinIM.view.photoview;

import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;


import com.bcr.jianxinIM.R;
import com.bcr.jianxinIM.activity.base.BaseActivity;

import java.util.List;


public class ViewPagerActivity extends BaseActivity {
    PhotoViewPager mViewPager;
    TextView mTvTitle;

    private List<String> items;
    public static final String TAG = ViewPagerActivity.class.getSimpleName();
    private int currentPosition;
    private MyImageAdapter adapter;
    private ImageView ivBack;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_pager);
        initView();
        initData1();
    }

    private void initData1() {
        items = getIntent().getStringArrayListExtra("items");
        currentPosition = getIntent().getIntExtra("currentPosition", 0);
        adapter = new MyImageAdapter(items, this);
        mViewPager.setAdapter(adapter);
        mViewPager.setCurrentItem(currentPosition, false);
        mTvTitle.setText(currentPosition + 1 + "/" + items.size());
        mViewPager.addOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                currentPosition = position;
                mTvTitle.setText(currentPosition + 1 + "/" + items.size());
            }
        });
    }

    private void initView() {
        mViewPager=findViewById(R.id.mViewPager);
        mTvTitle=findViewById(R.id.title);
        ivBack=findViewById(R.id.toolbar_left_iv);
        ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

    }

    @Override
    public void initUI() {

    }

    public void initData() {

    }



}
