package com.bcr.jianxinIM.activity.main;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.bcr.jianxinIM.R;
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
import com.bcr.jianxinIM.view.PushPhoto.adapter.NinePicturesAdapter;
import com.bcr.jianxinIM.view.PushPhoto.util.ImageLoaderUtils;
import com.bcr.jianxinIM.view.PushPhoto.view.NoScrollGridView;
import com.orhanobut.logger.Logger;
import com.squareup.okhttp.Response;
import com.yuyh.library.imgsel.ImgSelActivity;
import com.yuyh.library.imgsel.ImgSelConfig;
import com.yuyh.library.imgsel.ImageLoader;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import io.rong.imkit.plugin.image.PictureSelectorActivity;


public class PostDynamicActivity extends BaseActivity {

    private static final int POSTDYNAMIC =228 ;
    private EditText etContent;
    private NoScrollGridView gridview;
    private TextView tvPublish;

    private NinePicturesAdapter ninePicturesAdapter;
    private int REQUEST_CODE = 120;
    List<String> listPic;

    ImageView tvLeft;
    TextView tvTitle;
    TextView tvRight;
//    private static final OkHttpClient client= new OkHttpClient();
//    private static final MediaType MEDIA_TYPE_PNG = MediaType.parse("image/png");
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_dynamic);
        setHeadVisibility(View.GONE);
        setTitle("发布动态");
        initView();
    }
    private void initView() {
        etContent=findViewById(R.id.et_content);
        gridview=findViewById(R.id.gridview);
        tvPublish=findViewById(R.id.tv_publish);
        tvTitle=findViewById(R.id.titlebar_tv);
        tvRight=findViewById(R.id.titlebar_tv_right);
        tvLeft=findViewById(R.id.titlebar_iv_left);
        tvLeft.setVisibility(View.VISIBLE);
        tvRight.setVisibility(View.VISIBLE);
        tvTitle.setText("发布动态");
        tvRight.setText("发布");
        tvLeft.setOnClickListener(v -> onBackPressed());
        tvRight.setOnClickListener(v -> {
            if (!TextUtils.isEmpty(etContent.getText().toString())) {
                getPictureString();
                //将发表内容提交后台
               // NToast.shortToast(mContext,"发表成功");
                toPost();
            } else {
                NToast.shortToast(mContext,"发表内容不能为空！");
            }
        });
        ninePicturesAdapter = new NinePicturesAdapter(this, 9, new NinePicturesAdapter.OnClickAddListener() {
            @Override
            public void onClickAdd(int positin) {
              //  choosePhoto();

                Intent intent = new Intent(mContext, PictureSelectorActivity.class);
                startActivityForResult(intent, REQUEST_CODE );
            }
        });
        gridview.setAdapter(ninePicturesAdapter);

    }
    String location;
    private void toPost() {
        LoadDialog.show(mContext,"上传中...",false);
        AsyncTaskManager.getInstance(mContext).request(POSTDYNAMIC, new OnDataListener() {
            @Override
            public Object doInBackground(int requestCode, String parameter) throws HttpException {

                return new SealAction(mContext).postDynamic(SharedPreferencesUtil.getString(mContext,"userId",""),listPic,etContent.getText().toString(),location);
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
     * 开启图片选择器
     */
    private void choosePhoto() {
        ImgSelConfig config = new ImgSelConfig.Builder(loader)
                // 是否多选
                .multiSelect(true)
                // 确定按钮背景色
                .btnBgColor(Color.TRANSPARENT)
                .titleBgColor(ContextCompat.getColor(this, R.color.theme_color))
                // 使用沉浸式状态栏
                .statusBarColor(ContextCompat.getColor(this, R.color.theme_color))
                // 返回图标ResId
                .backResId(R.drawable.ic_back)
                .title("图片")
                // 第一个是否显示相机
                .needCamera(true)
                // 最大选择图片数量
                .maxNum(9 - ninePicturesAdapter.getPhotoCount())
                .build();
        ImgSelActivity.startActivity(this, config, REQUEST_CODE);
    }

    private ImageLoader loader = new ImageLoader() {
        @Override
        public void displayImage(Context context, String path, ImageView imageView) {
            ImageLoaderUtils.display(context, imageView, path);
        }
    };

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE && resultCode == RESULT_OK && data != null) {

            ArrayList<String> pathList = data.getStringArrayListExtra(ImgSelActivity.INTENT_RESULT);

            if (ninePicturesAdapter != null) {
                ninePicturesAdapter.addAll(pathList);
            }
        }
    }

    /**
     * 获取到拼接好的图片
     *
     * @return
     */
    private String getPictureString() {
        //拼接图片链接
        List<String> strings = ninePicturesAdapter.getData();
        listPic=new ArrayList<>();
        if (strings != null && strings.size() > 0) {
            StringBuilder allUrl = new StringBuilder();
            for (int i = 0; i < strings.size(); i++) {
                if (!TextUtils.isEmpty(strings.get(i))) {

                    Logger.d("图片路劲：--》"+strings.get(i));
                    Uri uri= Uri.parse(strings.get(i));
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
//    private void uploadMultiFile() {
//        MultipartBody.Builder builder = new MultipartBody.Builder().setType(MultipartBody.FORM);
//        // mImgUrls为存放图片的url集合
//        for (int i = 0; i < ninePicturesAdapter.getData().size() ; i++) {
//
//            File f = new File( ninePicturesAdapter.getData().get(i));
//            if (f!=null){
//                builder.addFormDataPart("img",f.getName(), RequestBody.create(MEDIA_TYPE_PNG,f));
//            }
//        }
//        builder.addFormDataPart("",etContent.getText().toString());
//        //构建请求
//        MultipartBody requestBody = builder.build();
//        Request request = new Request.Builder()
//                .url("http://172.16.52.20:8080/UploadDemo4/UploadFile")//服务器地址
//                .post(requestBody)  //添加请求体
//                .build();
//
//
//        client.newCall(request).enqueue(new Callback() {
//
//            @Override
//            public void onFailure(Call call, IOException e) {
//                runOnUiThread(new Runnable() {
//                    @Override
//                    public void run() {
//                        //Toast.makeText(MainActivity.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
//                    }
//                });
//            }
//
//            @Override
//            public void onResponse(Call call, okhttp3.Response response) throws IOException {
//                runOnUiThread(new Runnable() {
//                    @Override
//                    public void run() {
//                        //Toast.makeText(MainActivity.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
//                    }
//                });
//            }
//
//        });
//
//    }
}
