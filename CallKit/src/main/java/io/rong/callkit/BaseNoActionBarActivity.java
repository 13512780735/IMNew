package io.rong.callkit;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;

import com.yanzhenjie.sofia.Sofia;

import io.rong.imkit.RongConfigurationManager;

public class BaseNoActionBarActivity extends Activity {
    @Override
    protected void attachBaseContext(Context newBase) {
        Context newContext = RongConfigurationManager.getInstance().getConfigurationContext(newBase);
        super.attachBaseContext(newContext);
//        Sofia.with(this)
//                .statusBarBackground(Color.TRANSPARENT);

    }
}
