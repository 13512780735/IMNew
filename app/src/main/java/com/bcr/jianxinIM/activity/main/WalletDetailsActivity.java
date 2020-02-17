package com.bcr.jianxinIM.activity.main;

import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.View;
import android.widget.AbsListView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.orhanobut.logger.Logger;
import com.bcr.jianxinIM.R;
import com.bcr.jianxinIM.im.ui.activity.BaseActivity;
import com.bcr.jianxinIM.util.AppManager;
import com.wang.avi.AVLoadingIndicatorView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class WalletDetailsActivity extends BaseActivity {

    private ImageView ivBack;
    private TextView tvTitle;
    private ListView listView;
    private List<Map<String, Object>> arrayList;
    //加载进度条
    private AVLoadingIndicatorView albetlistLoading;
    private LinearLayout llLoadingBg;
    private TextView txtNoData; //暂无数据
    private SwipeRefreshLayout swipeRefreshLayout;
    private SimpleAdapter simpleAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wallet_details);
        setHeadVisibility(View.GONE);
        AppManager.getAppManager().addActivity(this);
        initView();
    }

    private void initView() {
        ivBack=findViewById(R.id.titlebar_iv_left);
        ivBack.setVisibility(View.VISIBLE);
        ivBack.setOnClickListener(v->onBackPressed());
        tvTitle=findViewById(R.id.titlebar_tv);
        tvTitle.setText("明细");  
        txtNoData= findViewById(R.id.txtNoData);    //暂无数据
        listView = findViewById(R.id.lvbets);
        arrayList = new ArrayList<>();  //初始化结果集
        llLoadingBg = findViewById(R.id.llLoadingBg);           //获取进度条背景
        albetlistLoading = findViewById(R.id.alLoginLoading); //获取加载进度条
        swipeRefreshLayout= findViewById(R.id.swipe);           //获取上拉下拉容器
        LoadingDisplay(true);
        GetListData();
        regSwipeClick();
    }
    //设置手势滑动监听器
    private void regSwipeClick(){
        //设置手势滑动监听器
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            public void onRefresh() {
                //PageIndex = 1;
                arrayList.clear();
                listView.setAdapter(null);
                GetListData();
                //发送一个延时1秒的handler信息
                //handler.sendEmptyMessageDelayed(199,1000);
            }
        });

        //给listview设置一个滑动的监听
        listView.setOnScrollListener(new AbsListView.OnScrollListener() {
            //当滑动状态发生改变的时候执行
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                switch (scrollState){
                    //当不滚动的时候
                    case AbsListView.OnScrollListener.SCROLL_STATE_IDLE:

                        //判断是否是最底部
                        if(view.getLastVisiblePosition()==(view.getCount())-1){
                           // PageIndex += 1;
                           // IsFirstLoad = true;
                            LoadingDisplay(true);
                            GetListData();
                        }
                        break;
                }
            }
            //正在滑动的时候执行
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
            }
        });
    }

    private void GetListData() {
        arrayList=new ArrayList<>();
        LoadingDisplay(false);
        swipeRefreshLayout.setRefreshing(false);
        Map<String, Object> map = new HashMap<>();
        map.put("img_url",R.mipmap.icon_zhuanzhang01);
        map.put("source","转帐-转给联系人1");
        map.put("time","2019-09-03 13:23");
        map.put("money","-20");
        arrayList.add(map);
        Map<String, Object> map1 = new HashMap<>();
        map1.put("img_url", R.mipmap.icon_zhuanzhang01);
        map1.put("source","转帐-转给联系人1");
        map1.put("time","2019-09-03 13:23");
        map1.put("money","+20");
            arrayList.add(map1);
        Map<String, Object> map2 = new HashMap<>();
        map2.put("img_url", R.mipmap.icon_hongbao01);
        map2.put("source","红包-转给联系人1");
        map2.put("time","2019-09-03 13:23");
        map2.put("money","+20");
        arrayList.add(map2);
        Map<String, Object> map3 = new HashMap<>();
        map3.put("img_url",R.mipmap.icon_withdrawal01);
        map3.put("source","提现-微信");
        map3.put("time","2019-09-03 13:23");
        map3.put("money","-20");
        arrayList.add(map3);
        Map<String, Object> map4 = new HashMap<>();
        map4.put("img_url", R.mipmap.icon_recherge01);
        map4.put("source","充值-微信");
        map4.put("time","2019-09-03 13:23");
        map4.put("money","+20");
        arrayList.add(map4);
        Logger.d("arrayList:"+arrayList);
        SetListData();
    }

    private void SetListData() {
        simpleAdapter = new SimpleAdapter(WalletDetailsActivity.this,
                arrayList,
                R.layout.walletdetails_items,
                new String[]{"img_url", "source", "time", "money"},
                new int[]{R.id.ivAvatar, R.id.tvName, R.id.tvTime, R.id.tvAmount});
        listView.setAdapter(simpleAdapter);
        //listView.setOnItemClickListener(this);
    }

    public void LoadingDisplay(boolean isShow){
        if (isShow)
        {
            llLoadingBg.setVisibility(View.VISIBLE);
            albetlistLoading.setVisibility(View.VISIBLE);
        }else{
            llLoadingBg.setVisibility(View.GONE);
            albetlistLoading.setVisibility(View.GONE);
        }
    }
}
