package com.bcr.jianxinIM.activity.main;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bcr.jianxinIM.R;
import com.bcr.jianxinIM.adapter.FullyGridLayoutManager;
import com.bcr.jianxinIM.adapter.GridImageAdapter;
import com.bcr.jianxinIM.im.server.SealAction;
import com.bcr.jianxinIM.im.server.network.async.AsyncTaskManager;
import com.bcr.jianxinIM.im.server.network.async.OnDataListener;
import com.bcr.jianxinIM.im.server.network.http.HttpException;
import com.bcr.jianxinIM.im.server.response.DynamicRespone;
import com.bcr.jianxinIM.im.server.utils.NToast;
import com.bcr.jianxinIM.im.server.widget.LoadDialog;
import com.bcr.jianxinIM.im.ui.activity.BaseActivity;
import com.bcr.jianxinIM.util.ImageUtils;
import com.bcr.jianxinIM.util.SharedPreferencesUtil;
import com.bcr.jianxinIM.util.UtilsClick;
import com.bcr.jianxinIM.view.PushPhoto.adapter.NinePicturesAdapter;
import com.bcr.jianxinIM.view.city.CityPickerActivity;
import com.luck.picture.lib.PictureSelector;
import com.luck.picture.lib.config.PictureConfig;
import com.luck.picture.lib.config.PictureMimeType;
import com.luck.picture.lib.entity.LocalMedia;
import com.luck.picture.lib.permissions.RxPermissions;
import com.luck.picture.lib.tools.PictureFileUtils;
import com.orhanobut.logger.Logger;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;
import io.rong.imkit.plugin.image.PictureSelectorActivity;
import pub.devrel.easypermissions.AppSettingsDialog;
import pub.devrel.easypermissions.EasyPermissions;

public class PostDynamic01Activity extends BaseActivity {

    private static final int REQUEST_CODE_PICK_CITY = 233;
    private ImageView tvLeft;
    private TextView tvTitle;
    private TextView tvRight,tvLocation;
    private EditText etContent;
    private RecyclerView mRecyclerView;
    private GridImageAdapter adapter;
    private List<LocalMedia> selectList = new ArrayList<>();
    private static final int POSTDYNAMIC =228 ;
    List<String> listPic;
    private String Location;
    static final String[] LOCATIONGPS = new String[]{Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.READ_PHONE_STATE
    };
    private static final int BAIDU_READ_PHONE_STATE = 100;//定位权限请求
    private static final int PRIVATE_CODE = 1315;//开启GPS权限

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_dynamic01);
        setHeadVisibility(View.GONE);
        initView();
    }


    private void initView() {
        etContent=findViewById(R.id.et_content);
        tvTitle=findViewById(R.id.title);
        tvRight=findViewById(R.id.toolbar_righ_tv);
        tvLeft=findViewById(R.id.toolbar_left_iv);
        mRecyclerView=findViewById(R.id.recycler);
        tvLocation=findViewById(R.id.tvLocation);
        tvLeft.setVisibility(View.VISIBLE);
        tvRight.setVisibility(View.VISIBLE);
        tvTitle.setText("发布动态");
        tvRight.setText("发布");
        tvLeft.setOnClickListener(v -> onBackPressed());

        FullyGridLayoutManager manager = new FullyGridLayoutManager(this, 3, GridLayoutManager.VERTICAL, false);
        mRecyclerView.setLayoutManager(manager);
        adapter = new GridImageAdapter(this, onAddPicClickListener);
        adapter.setList(selectList);
        adapter.setSelectMax(9);
        mRecyclerView.setAdapter(adapter);
        tvRight.setOnClickListener(v -> {

            Logger.d("长度：--》"+getPictureString().length());

            if (!TextUtils.isEmpty(etContent.getText().toString())||getPictureString().length()>0) {
                getPictureString();
                //将发表内容提交后台
                if(UtilsClick.isNotFastClick()){
                    toPost();

                }

            } else {
                NToast.shortToast(mContext,"发表内容或图片不能为空！");
            }
        });
        adapter.setOnItemClickListener(new GridImageAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position, View v) {
                if (selectList.size() > 0) {
                    LocalMedia media = selectList.get(position);
                    String pictureType = media.getPictureType();
                    int mediaType = PictureMimeType.pictureToVideo(pictureType);
                    switch (mediaType) {
                        case 1:
                            // 预览图片 可自定长按保存路径
                            //PictureSelector.create(MainActivity.this).externalPicturePreview(position, "../.../", selectList);
                            PictureSelector.create(PostDynamic01Activity.this).externalPicturePreview(position, selectList);
                            break;
                        case 2:
                            // 预览视频
                            //PictureSelector.create(MainActivity.this).externalPictureVideo(media.getPath());
                            break;
                        case 3:
                            // 预览音频
                           // PictureSelector.create(MainActivity.this).externalPictureAudio(media.getPath());
                            break;
                    }
                }
            }
        });
        tvLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //ActivityCompat.requestPermissions(UserInfoActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
                if (ContextCompat.checkSelfPermission(PostDynamic01Activity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED)
                    ActivityCompat.requestPermissions(PostDynamic01Activity.this, LOCATIONGPS, BAIDU_READ_PHONE_STATE);
                else {
                    goCity();
                }
            }
        });

    }
    private void goCity() {
        Intent intent = new Intent(PostDynamic01Activity.this, CityPickerActivity.class);
        startActivityForResult(intent, REQUEST_CODE_PICK_CITY);
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions,
                                           int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
      //  EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
        switch (requestCode) {
            case BAIDU_READ_PHONE_STATE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    goCity();
                } else {
                    Toast.makeText(this, "你拒绝了权限申请，可能无法定位获取地址！", Toast.LENGTH_SHORT).show();
                }
                break;
            default:
        }
    }
    private GridImageAdapter.onAddPicClickListener onAddPicClickListener = new GridImageAdapter.onAddPicClickListener() {

        @Override
        public void onAddPicClick() {
            PictureSelector.create(PostDynamic01Activity.this)
                    .openGallery(PictureMimeType.ofImage())
                    .maxSelectNum(9-selectList.size())
                    .minSelectNum(1)
                    .imageSpanCount(4)
                    .selectionMode(PictureConfig.MULTIPLE)
                    .forResult(PictureConfig.CHOOSE_REQUEST);
        }
    };

    private void toPost() {
        LoadDialog.show(mContext,"上传中...",false);
        AsyncTaskManager.getInstance(mContext).request(POSTDYNAMIC, new OnDataListener() {
            @Override
            public Object doInBackground(int requestCode, String parameter) throws HttpException {
                return new SealAction(mContext).postDynamic(SharedPreferencesUtil.getString(mContext,"userId",""),listPic,etContent.getText().toString(),Location);
            }

            @Override
            public void onSuccess(int requestCode, Object result) {
                LoadDialog.dismiss(mContext);
                if (result != null) {
                    DynamicRespone dynamicRespone= (DynamicRespone) result;
                    if(dynamicRespone.isSuccess()==true){
                        Intent intent = new Intent();
                        setResult(Activity.RESULT_OK, intent);
                        NToast.shortToast(mContext,dynamicRespone.getMessage());
                        clearCache();
                        finish();
                    }else {
                        NToast.shortToast(mContext,dynamicRespone.getMessage());
                    }

                }
            }

            @Override
            public void onFailure(int requestCode, int state, Object result) {

            }
        });
    }
    /**
     * 清空图片缓存，包括裁剪、压缩后的图片，避免OOM
     * 注意:必须要在上传完成后调用 必须要获取权限
     */
    private void clearCache() {
        RxPermissions permissions = new RxPermissions(this);
        permissions.request(Manifest.permission.WRITE_EXTERNAL_STORAGE).subscribe(new Observer<Boolean>() {
            @Override
            public void onSubscribe(Disposable d) {
            }

            @Override
            public void onNext(Boolean aBoolean) {
                if (aBoolean) {
                    //清除缓存
                    PictureFileUtils.deleteCacheDirFile(PostDynamic01Activity.this);
                } else {
                    Toast.makeText(PostDynamic01Activity.this, getString(R.string.picture_jurisdiction), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onError(Throwable e) {
            }

            @Override
            public void onComplete() {
            }
        });
    }

    /**
     * 获取到拼接好的图片
     *
     * @return
     */
    private String getPictureString() {
        //拼接图片链接
        List<LocalMedia> strings = selectList;
        listPic=new ArrayList<>();
        if (strings != null && strings.size() > 0) {
            StringBuilder allUrl = new StringBuilder();
            for (int i = 0; i < strings.size(); i++) {
                if (!TextUtils.isEmpty(strings.get(i).getPath())) {
                    Logger.d("图片路劲：--》"+strings.get(i));
                    Uri uri= Uri.parse(strings.get(i).getPath());
                    Bitmap bmp = BitmapFactory.decodeFile(uri.toString());//filePath

//                    Bitmap bmp= null;
//                    try {
//                        bmp = BitmapFactory.decodeStream(getContentResolver().openInputStream(uri));
//                    } catch (FileNotFoundException e) {
//                        e.printStackTrace();
//                    }
                    String base64= ImageUtils.bitmapToBase64(bmp);
                    listPic.add(base64);
                    allUrl.append(strings.get(i) + ";");
                }
            }
            if (!TextUtils.isEmpty(allUrl)) {
                String url = allUrl.toString();
                url = url.substring(0, url.lastIndexOf(";"));
                return url;
            } else {
                return "";
            }
        } else {
            return "";
        }
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        List<LocalMedia> images;
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case PictureConfig.CHOOSE_REQUEST:
                    // 图片选择结果回调
                    images = PictureSelector.obtainMultipleResult(data);
                    selectList.addAll(images);
//                    selectList = PictureSelector.obtainMultipleResult(data);
                    // LocalMedia 里面返回三种path
                    // 1.media.getPath(); 为原图path
                    // 2.media.getCutPath();为裁剪后path，需判断media.isCut();是否为true
                    // 3.media.getCompressPath();为压缩后path，需判断media.isCompressed();是否为true
                    // 如果裁剪并压缩了，以取压缩路径为准，因为是先裁剪后压缩的
                    adapter.setList(selectList);
                    adapter.notifyDataSetChanged();
                    break;
                case REQUEST_CODE_PICK_CITY:
                    if (data != null) {
                        Location = data.getStringExtra("date");
                        tvLocation.setText(Location);
                        //initData(1, false);
                    }
                    break;
            }
        }
    }
}
