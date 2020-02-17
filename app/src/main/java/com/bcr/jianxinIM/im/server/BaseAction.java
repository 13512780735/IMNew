package com.bcr.jianxinIM.im.server;

import android.content.Context;

import com.bcr.jianxinIM.im.server.network.http.HttpException;
import com.bcr.jianxinIM.im.server.network.http.SyncHttpClient;
import com.bcr.jianxinIM.im.server.utils.json.JsonMananger;

import java.util.List;


/**
 * Created by AMing on 16/1/14.
 * Company RongCloud
 */
public  class BaseAction {

    // private static final String DOMAIN = "http://api.sealtalk.im";
    //  public static final String DOMAIN = "http://192.168.3.35:9992/Ashx";//内网测试
    //  public static final String DOMAIN = "http://im.szjykj888.cn/Ashx";//外网测试
    public static final String DOMAIN = "http://im.bliaoliao.cn/Ashx";//数据接口
//      public static final String DOMAIN2 = "http://msg.bliaoliao.cn/Ashx";//短信接口
//      public static final String DOMAIN3 = "http://push.bliaoliao.cn/Ashx";//极光推送接口
   //  public static final String DOMAIN4 = "http://file.bliaoliao.cn/Ashx";//收藏接口
    protected Context mContext;
    protected SyncHttpClient httpManager;


    /**
     * 构造方法
     *
     * @param context 上下文
     */
    public BaseAction(Context context) {
        this.mContext = context;
        this.httpManager = SyncHttpClient.getInstance(context);
    }

    /**
     * JSON转JAVA对象方法
     *
     * @param json json
     * @param cls  class
     * @throws HttpException
     */
    public <T> T jsonToBean(String json, Class<T> cls) throws HttpException {
        return JsonMananger.jsonToBean(json, cls);
    }

    /**
     * JSON转JAVA数组方法
     *
     * @param json json
     * @param cls  class
     * @throws HttpException
     */
    public <T> List<T> jsonToList(String json, Class<T> cls) throws HttpException {
        return JsonMananger.jsonToList(json, cls);
    }

    /**
     * JAVA对象转JSON方法
     *
     * @param obj object
     * @throws HttpException
     */
    public String BeanTojson(Object obj) throws HttpException {
        return JsonMananger.beanToJson(obj);
    }


    /**
     * 获取完整数据URL方法
     *
     * @param url url
     */
    protected String getURL(String url) {
        return getURL(url, new String[] {});
    }

    /**
     * 短信接口
     * @param url
     * @return
     */
    protected String getURL2(String url) {
        return getURL2(url, new String[] {});
    }

    /**
     * 极光推送
     * @param url
     * @return
     */
    protected String getURL3(String url) {
        return getURL3(url, new String[] {});
    }

    /**
     * 收藏
     * @param url
     * @return
     */
    protected String getURL4(String url) {
        return getURL4(url, new String[] {});
    }
    /**
     * 获取完整数据URL方法
     *
     * @param url    url
     * @param params params
     */
    protected String getURL(String url, String... params) {
        StringBuilder urlBuilder = new StringBuilder(DOMAIN).append("/").append(url);
        if (params != null) {
            for (String param : params) {
                if (!urlBuilder.toString().endsWith("/")) {
                    urlBuilder.append("/");
                }
                urlBuilder.append(param);
            }
        }
        return urlBuilder.toString();
    }
    /**
     * 获取完整短信URL方法
     *
     * @param url    url
     * @param params params
     */
    protected String getURL2(String url, String... params) {
        StringBuilder urlBuilder = new StringBuilder(DOMAIN).append("/").append(url);
        if (params != null) {
            for (String param : params) {
                if (!urlBuilder.toString().endsWith("/")) {
                    urlBuilder.append("/");
                }
                urlBuilder.append(param);
            }
        }
        return urlBuilder.toString();
    }
    /**
     * 获取完整极光URL方法
     *
     * @param url    url
     * @param params params
     */
    protected String getURL3(String url, String... params) {
        StringBuilder urlBuilder = new StringBuilder(DOMAIN).append("/").append(url);
        if (params != null) {
            for (String param : params) {
                if (!urlBuilder.toString().endsWith("/")) {
                    urlBuilder.append("/");
                }
                urlBuilder.append(param);
            }
        }
        return urlBuilder.toString();
    }
    /**
     * 获取完整收藏URL方法
     *
     * @param url    url
     * @param params params
     */
    protected String getURL4(String url, String... params) {
        StringBuilder urlBuilder = new StringBuilder(DOMAIN).append("/").append(url);
        if (params != null) {
            for (String param : params) {
                if (!urlBuilder.toString().endsWith("/")) {
                    urlBuilder.append("/");
                }
                urlBuilder.append(param);
            }
        }
        return urlBuilder.toString();
    }
}
