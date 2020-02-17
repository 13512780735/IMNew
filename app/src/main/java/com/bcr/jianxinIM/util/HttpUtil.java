package com.bcr.jianxinIM.util;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

import com.bcr.jianxinIM.activity.base_util.ToastUtils;
import com.bcr.jianxinIM.app.MyApplication;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.BinaryHttpResponseHandler;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;


public class HttpUtil {
    private static AsyncHttpClient client = new AsyncHttpClient(); // 实例话对象

    static {
        client.setTimeout(15000); // 设置链接超时，如果不设置，默认为15s
    }

    /**
     * 用一个完整url获取一个string对象
     *
     * @param urlString
     * @param res
     */
    public static void get(String urlString, AsyncHttpResponseHandler res) {
        Log.i("HttpUtil", urlString);
        client.get(urlString, res);
    }

    /**
     * url里面带参数
     *
     * @param
     * @param params
     * @param res
     */
    public static void get(String url, RequestParams params, final RequestListener res) {
        //Log.i("HttpUtil", url + "?" + params.toString());

        client.get(url, params, new JsonHttpResponseHandler() {

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject responseBody) {
                response(statusCode, responseBody, res);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable e, JSONArray errorResponse) {
                res.failed(e);
                Log.i("TAG1", errorResponse.toString());
            }


        });
    }

    /**
     * url里面带参数、请求头
     *
     * @param
     * @param params
     * @param res
     */
    public static void get2(String url, RequestParams params, final RequestListener res) {
        Log.i("HttpUtil", url + "?" + params.toString());

        // client.addHeader("User-Agent", "Java");

        client.get(url, params, new JsonHttpResponseHandler() {

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject responseBody) {
                response(statusCode, responseBody, res);

            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable e, JSONArray errorResponse) {
                res.failed(e);
                Log.i("TAG1", errorResponse.toString());
            }

        });
    }

    /**
     * 不带参数，获取json对象或者数组
     *
     * @param urlString
     * @param res
     */
    public static void get(String urlString, JsonHttpResponseHandler res) {
        Log.i("HttpUtil", urlString);
        client.get(urlString, res);
    }

    /**
     * 带参数，获取json对象或者数组
     *
     * @param urlString
     * @param params
     * @param res
     */
    public static void get(String urlString, RequestParams params, JsonHttpResponseHandler res) {
        Log.i("HttpUtil", urlString + "?" + params.toString());
        client.get(urlString, params, res);
    }

    /**
     * 下载数据使用，会返回byte数据
     *
     * @param uString
     * @param bHandler
     */
    public static void get(String uString, BinaryHttpResponseHandler bHandler) {
        Log.i("HttpUtil", uString);
        client.get(uString, bHandler);
    }

    /**
     * url里面带参数
     *
     * @param
     * @param params
     * @param res
     */
    public static void post(String url, RequestParams params, final RequestListener res) {
        Log.i("HttpUtil", url + "?" + params.toString());
     //   isNetwork();
        client.post(url, params, new JsonHttpResponseHandler() {

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                response(statusCode, response, res);
            }


            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable e, JSONArray errorResponse) {
                res.failed(e);
                Log.i("TAG1", errorResponse.toString());
            }

            @Override
            public void onFinish() {
                Log.d("TAG", "http-->" + "请求完成");
                res.onFinish();
            }

        });

    }

    private static void isNetwork() {
        if (isNetworkAvailable(MyApplication.getInstance())) {
            // ToastUtil.showL(getApplicationContext(), );
            Log.d("TAG", "当前有可用网络！");
        } else {
            ToastUtils.showToast(MyApplication.getInstance(), "当前没有可用网络！");
        }
    }

    /**
     * 检查当前网络是否可用
     *
     * @param
     * @return
     */

    public static boolean isNetworkAvailable(MyApplication myApplication) {
        Context context = myApplication.getApplicationContext();
        // 获取手机所有连接管理对象（包括对wi-fi,net等连接的管理）
        ConnectivityManager connectivityManager = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);

        if (connectivityManager == null) {
            return false;
        } else {
            // 获取NetworkInfo对象
            NetworkInfo[] networkInfo = connectivityManager.getAllNetworkInfo();

            if (networkInfo != null && networkInfo.length > 0) {
                for (int i = 0; i < networkInfo.length; i++) {
                    System.out.println(i + "===状态===" + networkInfo[i].getState());
                    System.out.println(i + "===类型===" + networkInfo[i].getTypeName());
                    // 判断当前网络状态是否为连接状态
                    if (networkInfo[i].getState() == NetworkInfo.State.CONNECTED) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    /**
     * url里面不带参数
     *
     * @param
     * @param
     * @param res
     */
    public static void post(String url, final RequestListener res) {
        Log.i("HttpUtil", url);
        isNetwork();
        client.post(url, new JsonHttpResponseHandler() {

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                response(statusCode, response, res);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable error, JSONArray errorResponse) {
                res.failed(error);
            }


            @Override
            public void onFinish() {
                Log.d("TAG", "http-->" + "请求完成");
                res.onFinish();
            }
        });
    }

    public static void response(int statusCode, JSONObject responseBody, RequestListener res) {

        if (statusCode == 200) {
            try {
                String result = responseBody.toString();
                // result = StringUtil.unescape(result);
                Log.i("HttpUtil", result);
                res.success(result);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public static AsyncHttpClient getClient() {
        return client;
    }

    public static abstract class RequestListener {
        /**
         * 请求成功
         *
         * @param response
         */
        public abstract void success(String response);

        public void onFinish() {

        }

        /**
         * 请求失败
         *
         * @param
         */
        public abstract void failed(Throwable e);

    }

    /**
     * 取消请求
     *
     * @param context
     */
    public static void onCancelRequest(Context context) {
        client.cancelRequests(context, true);
    }

}
