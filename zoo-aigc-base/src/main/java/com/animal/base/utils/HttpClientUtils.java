package com.animal.base.utils;

import cn.hutool.http.ContentType;
import cn.hutool.json.JSONUtil;
import com.animal.product.common.ErrorCode;
import com.animal.product.exception.BusinessException;
import okhttp3.*;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * @author 咏鹅
 * @version 1.0
 * @description http请求工具类
 * @date 2023/8/30 17:56
 */
public class HttpClientUtils {

    private static final OkHttpClient httpClient;

    static {
        ConnectionPool connectionPool = new ConnectionPool(5, 10, TimeUnit.MINUTES);
        httpClient = new OkHttpClient
                .Builder()
                .writeTimeout(60, TimeUnit.SECONDS)
                .readTimeout(60, TimeUnit.SECONDS)
                .connectTimeout(15, TimeUnit.SECONDS)
                .connectionPool(connectionPool)
                .build();
    }

    public static String doGet(String url) throws IOException {
        Request request = new Request
                .Builder()
                .url(url)
                .get()
                .build();
        return doHttp(request);
    }

    public static String doGet(String url,  Map<String, Object> params) throws IOException {
        HttpUrl.Builder urlBuilder = HttpUrl.parse(url).newBuilder();
        for (Map.Entry<String, Object> entry : params.entrySet()) {
            urlBuilder.addQueryParameter(entry.getKey(),String.valueOf(entry.getValue()));
        }
        Request request = new Request
                .Builder()
                .url(urlBuilder.build().toString())
                .get()
                .build();
        return doHttp(request);
    }

    public static String doGet(String url, Map<String, Object> headers, Map<String, Object> params) throws IOException {
        HttpUrl.Builder urlBuilder = HttpUrl.parse(url).newBuilder();
        for (Map.Entry<String, Object> entry : params.entrySet()) {
            urlBuilder.addQueryParameter(entry.getKey(),String.valueOf(entry.getValue()));
        }
        Headers.Builder header = new Headers.Builder();
        mapConvert(header,headers);
        Request request = new Request
                .Builder()
                .url(urlBuilder.build().toString())
                .headers(header.build())
                .get()
                .build();
        return doHttp(request);
    }


    /**
     * @description http请求
     * @param url 请求路径
     * @param params 携带参数
     * @return java.lang.String
     * @author 咏鹅
     * @date 2023/8/30 21:13
    */
    public static String doPostJson(String url,  Map<String, Object> params) throws IOException {
        Request request = new Request
                .Builder()
                .url(url)
                .post(RequestBody.create(JSONUtil.toJsonStr(params).getBytes(), MediaType.parse(ContentType.JSON.getValue())))
                .build();
        return doHttp(request);
    }

    public static String doPostJson(String url, Map<String, Object> headers, Map<String, Object> params) throws IOException {
        Headers.Builder header = new Headers.Builder();
        mapConvert(header,headers);
        Request request = new Request
                .Builder()
                .url(url)
                .headers(header.build())
                .post(RequestBody.create(JSONUtil.toJsonStr(params).getBytes(), MediaType.parse(ContentType.JSON.getValue())))
                .build();
        return doHttp(request);
    }

    public static String doPostForm(String url, Headers header, Map<String, Object> params) throws IOException {
        FormBody.Builder formBody = new FormBody.Builder();
        mapConvert(formBody,params);
        Request request = new Request
                .Builder()
                .url(url)
                .headers(header)
                .post(formBody.build())
                .build();
        return doHttp(request);
    }

    private static void mapConvert(Headers.Builder header, Map<String, Object> params){
        for (Map.Entry<String, Object> entry : params.entrySet()) {
            header.add(entry.getKey(), String.valueOf(entry.getValue()));
        }
    }
    private static void mapConvert(FormBody.Builder formBody, Map<String, Object> params){
        for (Map.Entry<String, Object> entry : params.entrySet()) {
            formBody.add(entry.getKey(), String.valueOf(entry.getValue()));
        }
    }

    private static String doHttp(Request request) throws IOException {
        return doRequest(request);
    }

    //以后再做 `=v=`
    private static String doHttps(Request request) throws IOException {
        return doRequest(request);
    }

    private static String doRequest(Request request) throws IOException {
        try (Response response = httpClient.newCall(request).execute()) {
            int statusCode = response.code();
            if (statusCode != 200) {
                throw new BusinessException(ErrorCode.HTTP_ERROR);
            }
            ResponseBody entity = response.body();


            return response.body().string();
        }
    }

}
