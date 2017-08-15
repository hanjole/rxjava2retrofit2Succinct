package com.h.rxjava2retrofit2succinct;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.concurrent.TimeUnit;

import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import okio.Buffer;
import okio.BufferedSource;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by hs on 2017/8/8.
 */

public class MaRetrofit {
    LogUtils log = LogUtils.hLog();
    Retrofit retrofit;
    OkHttpClient.Builder client;
    JSONObject postJson;

    public MaRetrofit() {
        if (client == null) {
            client = new OkHttpClient.Builder();
        }
        addInterceptor();
    }

    public Retrofit create() {
        retrofit = new Retrofit.Builder()
                .client(client.build())
                .baseUrl(Constants.URL + "/")
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .build();
        return retrofit;
    }

    public UrlServiceInterface getUrlServiceInterface() {
        if (retrofit == null) {
            retrofit = new Retrofit.Builder()
                    .client(client.build())
                    .baseUrl(Constants.URL + "/")
                    .addConverterFactory(GsonConverterFactory.create())
                    .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                    .build();
        }
        return retrofit.create(UrlServiceInterface.class);
    }


    public MaRetrofit addJson(String key, String value) {
        try {
            getBaseJson().put(key, value);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return this;
    }

    private JSONObject getBaseJson() {
        if (postJson == null) {
            //获取一个json对象
            postJson = new JsonBean().getJson(MaApplication.getContext());
        }
        return postJson;
    }


    public Retrofit addInterceptor() {
        if (client == null) {
            return null;
        }
        client.addInterceptor(new Interceptor() {
            @Override
            public okhttp3.Response intercept(Chain chain) throws IOException {
                //获得请求信息，
                Request originalRequest = chain.request();
                // 返回添加body的请求
                Request requests = setupRequestBody(originalRequest);
                log.i(requests.url());
                //记录请求耗时
                long startNs = System.nanoTime();
                okhttp3.Response response;
                try {
                    //发送请求，获得相应，
                    response = chain.proceed(requests);
                } catch (Exception e) {
                    e.printStackTrace();
                    throw e;
                }
                long tookMs = TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - startNs);
                //打印请求耗时
                log.i("耗时:" + tookMs + "ms");

                //获得返回的body，注意此处不要使用responseBody.string()获取返回数据，原因在于这个方法会消耗返回结果的数据(buffer)
                ResponseBody responseBody = response.body();

                //为了不消耗buffer，我们这里使用source先获得buffer对象，然后clone()后使用
                BufferedSource source = responseBody.source();
                source.request(Long.MAX_VALUE); // Buffer the entire body.
                //获得返回的数据
                Buffer buffer = source.buffer();
                //使用前clone()下，避免直接消耗
                log.i("response:" + buffer.clone().readString(Charset.forName("UTF-8")));
                return response;
            }
        });
        return retrofit;
    }

    private Request setupRequestBody(Request oldRequests) {
        JSONObject json = getBaseJson();
        RequestBody body = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), json.toString());
        log.i("requestBody-json", json.toString());
        //返回一个新的RequestBody
        return oldRequests.newBuilder()
                .url(oldRequests.url())
                .method(oldRequests.method(), body)
                .build();
    }
}
