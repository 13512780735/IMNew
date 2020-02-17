package com.bcr.jianxinIM.activity.LoginRegister;

import android.os.Bundle;

import com.bcr.jianxinIM.R;
import com.bcr.jianxinIM.im.ui.activity.BaseActivity;
import com.bcr.jianxinIM.view.X5WebView;

/**
 * 隐私协议
 */
public class PrivacyActivity extends BaseActivity {
    X5WebView wvTask;
    String url;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_privacy);
        setTitle("隐私协议条款");
        initHardwareAccelerate();
      url="http://ys.tiyunguoji.com/a.html";
        //   url="http://www.baidu.com";
        initView();
    }
    private void initView(){
        wvTask=findViewById(R.id.wv_task);
      //  wvTask.loadUrl(url);
        String url1 = "file:///android_asset/" + "a.html";
        wvTask.loadUrl(url1);
    }
    /**

         * 启用硬件加速

         */

private void initHardwareAccelerate() {

        try {

            if (Integer.parseInt(android.os.Build.VERSION.SDK) >=11) {

                getWindow()

                        .setFlags(

                                android.view.WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED,

                                android.view.WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED);

 }

        }catch (Exception e) {

        }

    }

}
