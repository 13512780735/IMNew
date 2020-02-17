package com.bcr.jianxinIM.activity.main;


import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bcr.jianxinIM.R;
import com.bcr.jianxinIM.adapter.CircleFriendsAdapter;
import com.bcr.jianxinIM.im.server.BaseAction;
import com.bcr.jianxinIM.im.server.SealAction;
import com.bcr.jianxinIM.im.server.network.async.AsyncTaskManager;
import com.bcr.jianxinIM.im.server.network.async.OnDataListener;
import com.bcr.jianxinIM.im.server.network.http.HttpException;
import com.bcr.jianxinIM.im.server.response.CollectListMessage;
import com.bcr.jianxinIM.im.server.response.CollectListMessage1;
import com.bcr.jianxinIM.im.server.response.CollectMessageRespone;
import com.bcr.jianxinIM.im.server.response.DelCollectMessage;
import com.bcr.jianxinIM.im.server.utils.NToast;
import com.bcr.jianxinIM.im.server.widget.LoadDialog;
import com.bcr.jianxinIM.im.ui.activity.BaseActivity;
import com.bcr.jianxinIM.model.CircleFriendsModel1;
import com.bcr.jianxinIM.model.CollectModel;
import com.bcr.jianxinIM.model.ComUserBean;
import com.bcr.jianxinIM.util.AppManager;
import com.bcr.jianxinIM.util.HttpUtil;
import com.bcr.jianxinIM.util.ImageLoaderUtil;
import com.bcr.jianxinIM.util.SharedPreferencesUtil;
import com.bcr.jianxinIM.view.RoundImageView;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.loopj.android.http.RequestParams;
import com.orhanobut.logger.Logger;
import com.wang.avi.AVLoadingIndicatorView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import io.rong.imageloader.core.ImageLoader;


public class CollectActivity extends BaseActivity implements View.OnClickListener, SwipeRefreshLayout.OnRefreshListener, BaseQuickAdapter.RequestLoadMoreListener {
    private static final int COLLECTLIST = 243;
    private static final int DELCOLLLECT = 244;
    LinearLayout ivBack;
    TextView tvTitle;
    TextView tvRight;
    Button btn_delete;
    private CheckBox che_all;
    RelativeLayout mRlBottom;
    LinearLayout llLoadingBg;
    AVLoadingIndicatorView albetlistLoading;
    SwipeRefreshLayout swipeRefreshLayout;
    RecyclerView mRecyclerView;
    TextView txtNoData;
    boolean flag;
    //声明一个集合（数据源）
    private List<CollectListMessage1> data;
    private ArrayList<CheckBox> checkBoxList;
    private GollectAdapter mAdapter;
    private JSONObject objects;


    private static final int PAGE_SIZE = 10;//为什么是6呢？
    private int pageNum = 1;
    private int mCurrentCounter;
    private boolean isErr = false;
    private int count;
    private int TOTAL_COUNTER=0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_collect);
        setHeadVisibility(View.GONE);
        AppManager.getAppManager().addActivity(this);
         //initdata();
         initView();
    }

    private void initView() {
        data = new ArrayList<>();
        checkBoxList = new ArrayList<>();
        ivBack=findViewById(R.id.back_view);
        tvTitle=findViewById(R.id.title);
        tvRight=findViewById(R.id.toolbar_righ_tv);
        che_all=findViewById(R.id.che_all);
        btn_delete=findViewById(R.id.btn_delete);
        mRlBottom=findViewById(R.id.rl_bottom);
        llLoadingBg=findViewById(R.id.llLoadingBg);
        albetlistLoading=findViewById(R.id.alLoginLoading);
        swipeRefreshLayout=findViewById(R.id.swipe);
        mRecyclerView=findViewById(R.id.lvbets);
        txtNoData=findViewById(R.id.txtNoData);
        tvTitle.setText("我的收藏");
        ivBack.setOnClickListener(v->onBackPressed());

        if(data.size()==0){
            tvRight.setVisibility(View.GONE);
        }else {
            tvRight.setVisibility(View.VISIBLE);
        }
        tvRight.setText("编辑");
        tvRight.setOnClickListener(this);
        swipeRefreshLayout.setOnRefreshListener(this);
        swipeRefreshLayout.setColorSchemeColors(Color.rgb(47, 223, 189));
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        initAdapter();
        mRlBottom.setVisibility(View.GONE);
        initlistener();
        mAdapter.setOnItemClickListener((adapter, view, position) -> {
            String content=data.get(position).getContent();
            String type=data.get(position).getType();
            Bundle bundle=new Bundle();
            bundle.putString("content",content);
            bundle.putString("type",type);
            bundle.putString("Conver",data.get(position).getConver());
            toActivity(CollectDetailsActivity.class,bundle);
        });
       // swipeRefreshLayout.setr



    }
    private void initAdapter() {
        mAdapter = new GollectAdapter(R.layout.layout_collect_items, data);
        mAdapter.setOnLoadMoreListener(this, mRecyclerView);
        mRecyclerView.setAdapter(mAdapter);
        mAdapter.disableLoadMoreIfNotFullPage();
        initdata(pageNum, false);
        mCurrentCounter = mAdapter.getData().size();
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
                initdata(pageNum, false);
                swipeRefreshLayout.setRefreshing(false);
                mAdapter.setEnableLoadMore(true);//启用加载
            }
        }, 1500);
    }

    /**
     * 上拉加载
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
                        initdata(pageNum, true);
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
    private void initlistener() {
        /**
         * 全选复选框设置事件监听
         */
        che_all.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (data.size() != 0) {//判断列表中是否有数据
                    if (isChecked) {
                        for (int i = 0; i < data.size(); i++) {
                            data.get(i).setChecked(true);
                        }
                        //通知适配器更新UI
                        mAdapter.notifyDataSetChanged();
                    } else {
                        for (int i = 0; i < data.size(); i++) {
                            data.get(i).setChecked(false);
                        }
                        //通知适配器更新UI
                        mAdapter.notifyDataSetChanged();
                    }
                } else {//若列表中没有数据则隐藏全选复选框
                    che_all.setVisibility(View.GONE);
                }
            }
        });
        //删除按钮点击事件
        btn_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //创建一个要删除内容的集合，不能直接在数据源data集合中直接进行操作，否则会报异常
                List<CollectListMessage1> deleSelect = new ArrayList<>();

                //把选中的条目要删除的条目放在deleSelect这个集合中
                for (int i = 0; i < data.size(); i++) {
                    if (data.get(i).getChecked()) {
                        deleSelect.add(data.get(i));
                    }
                }
                //判断用户是否选中要删除的数据及是否有数据
                if (deleSelect.size() != 0 && data.size() != 0) {
                    //从数据源data中删除数据
                    delCollect(deleSelect);
                    data.removeAll(deleSelect);
                    //把deleSelect集合中的数据清空
                    deleSelect.clear();
                    //把全选复选框设置为false
                    che_all.setChecked(false);
                    //通知适配器更新UI
                    mAdapter.notifyDataSetChanged();


                } else if (data.size() == 0) {
                    Toast.makeText(CollectActivity.this, "没有要删除的数据", Toast.LENGTH_SHORT).show();
                } else if (deleSelect.size() == 0) {
                    Toast.makeText(CollectActivity.this, "请选中要删除的数据", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
    /**
     * 删除收藏
     * @param deleSelect
     */
    private void delCollect(List<CollectListMessage1> deleSelect) {
        Logger.d("删除");
        List<String> ids=new ArrayList<>();
        for (int i=0;i<deleSelect.size();i++){
            ids.add(deleSelect.get(i).getID());
        }
        AsyncTaskManager.getInstance(mContext).request(DELCOLLLECT, new OnDataListener() {
            @Override
            public Object doInBackground(int requestCode, String parameter) throws HttpException {
                return new SealAction(mContext).DelCollectMessage(SharedPreferencesUtil.getString(mContext,"userId",""),ids);
            }

            @Override
            public void onSuccess(int requestCode, Object result) {
                if (result != null) {
                    DelCollectMessage delCollectMessage= (DelCollectMessage) result;
                    if(delCollectMessage.isSuccess()==true){
                        NToast.shortToast(mContext,delCollectMessage.getMessage());


                        if(data.size()==0){
                            tvRight.setVisibility(View.GONE);
                            mRlBottom.setVisibility(View.GONE);
                            mAdapter.setEmptyView(R.layout.notdata_view1);
                        }else {
                            tvRight.setVisibility(View.VISIBLE);
                            mRlBottom.setVisibility(View.VISIBLE);
                            mAdapter.notifyDataSetChanged();
                        }
                    }else {
                        NToast.shortToast(mContext,delCollectMessage.getMessage());
                    }
                }
            }

            @Override
            public void onFailure(int requestCode, int state, Object result) {

            }
        });


    }

    public void onClick(View v) {
        switch (v.getId()) {
            default:
                break;
            case R.id.toolbar_righ_tv:
                flag=!flag;
                    if (flag) {
                        tvRight.setText("完成");
                        mRlBottom.setVisibility(View.VISIBLE);
                        for (int i = 0; i < checkBoxList.size(); i++) {
                            checkBoxList.get(i).setVisibility(View.VISIBLE);
                        }
                    } else {
                        tvRight.setText("编辑");
                        mRlBottom.setVisibility(View.GONE);
                        for (int i = 0; i < checkBoxList.size(); i++) {
                            checkBoxList.get(i).setVisibility(View.GONE);
                        }
                    }
                break;
        }
    }

    //给数据源添加数据
    private void initdata(int pageNum, final boolean isloadmore) {
        RequestParams params=new RequestParams();
        params.put("userId", SharedPreferencesUtil.getString(mContext,"userId",""));
        params.put("pageIndex",String.valueOf(pageNum));
        HttpUtil.post(BaseAction.DOMAIN+"/OSS.ashx?Method=GetUpload", params, new HttpUtil.RequestListener() {
            @Override
            public void success(String response) {
                LoadDialog.dismiss(mContext);
                try {
                    JSONObject object=new JSONObject(response);
                    if(object.optBoolean("Success")==true){
                       List<CollectListMessage1> data1=new ArrayList<>();
                        objects=object.optJSONObject("ResultData");
                        count=objects.optInt("Count");
                        JSONArray array=objects.optJSONArray("UserCollect");
                        if (count != 0) {
                        for(int i=0;i<array.length();i++){
                            JSONObject object1 = array.optJSONObject(i);
                            CollectListMessage1 collectListMessage1=new CollectListMessage1();
                            collectListMessage1.setChecked(false);
                            collectListMessage1.setContent(object1.optString("Content"));
                            collectListMessage1.setDate(object1.optString("Date"));
                            collectListMessage1.setID(object1.optString("ID"));
                            collectListMessage1.setUserId(object1.optString("userId"));
                            collectListMessage1.setUserName(object1.optString("UserName"));
                            collectListMessage1.setUserPic(object1.optString("UserPic"));
                            collectListMessage1.setUserPic(object1.optString("UserSex"));
                            collectListMessage1.setType(object1.optString("Type"));
                            collectListMessage1.setSource(object1.optString("Source"));
                            collectListMessage1.setConver(object1.optString("Conver"));
                            data1.add(collectListMessage1);
                        } if (!isloadmore) {
                                data = data1;
                            } else {
                                data.addAll(data1);
                            }
                            mAdapter.setNewData(data);
                            mCurrentCounter = data.size();
                            mAdapter.notifyDataSetChanged();
                            if(data.size()==0){
                                tvRight.setClickable(false);
                                tvRight.setVisibility(View.GONE);
                            }else {
                                tvRight.setClickable(true);
                                tvRight.setVisibility(View.VISIBLE);
                            }

                        }
                        else {
                            mAdapter.notifyDataSetChanged();
                            mAdapter.setEmptyView(R.layout.notdata_view1);
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
            }
        });

    }



    //返回数据给MyAdapter使用
    public List<CollectListMessage1> getData() {
        return this.data;
    }



    public class GollectAdapter  extends BaseQuickAdapter<CollectListMessage1, BaseViewHolder> {
       public GollectAdapter(int layoutResId, List<CollectListMessage1> data) {
           super(R.layout.layout_collect_items, data);
       }
       @Override
       protected void convert(BaseViewHolder helper, CollectListMessage1 item) {
         //  final CollectListMessage1 currItem = data.get(position);
           RoundImageView ivPicture=helper.getView(R.id.tv_goods_picture);
           CheckBox ch_delete=helper.getView(R.id.ch_delete);
            if("0".equals(item.getType())){
                ivPicture.setVisibility(View.GONE);
                helper.setText(R.id.tv_name,item.getContent());
            }else if("1".equals(item.getType())){
                ivPicture.setVisibility(View.VISIBLE);
                ImageLoaderUtil.displayImage(mContext,ivPicture,item.getContent(),ImageLoaderUtil.getPhotoImageOption());
                helper.setText(R.id.tv_name,"图片");
            }else {
                ivPicture.setVisibility(View.VISIBLE);
                ImageLoaderUtil.displayImage(mContext,ivPicture,item.getConver(),ImageLoaderUtil.getPhotoImageOption());
                helper.setText(R.id.tv_name,"视频");
            }
           helper.setText(R.id.tv_goods_new_price,item.getSource());
            helper.setText(R.id.tv_goods_old_price,item.getDate());
           ch_delete.setChecked(item.getChecked());
           checkBoxList.add(ch_delete);
           ch_delete.setOnClickListener(
                    new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            //   currItem.setChoosed(((CheckBox) v).isChecked());
                            item.setChecked(((CheckBox) v).isChecked());
                        }
                    }
            );
       }
    }
}
