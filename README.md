# rxjava2retrofit2Succinct
rxjava2retrofit2封装,使用retrofit结合rxjava进行网络请求  使用post json参数
http://www.jianshu.com/p/9df6c7e3c39f


复制粘贴 来的  有点乱 还是去看博客的好


需求:服务端要求请求方式为post,传递参数为json格式
在请求后台服务时,移动端又要每次携带固定参数
普通的@post 然后通过@Query设置参数 无法满足需求
经一番查询post Json需要通过okhttp中拦截器实现


   //post数据
 RequestBody body = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), json.toString());
拦截器添加了返回结果输出

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
这里getBaseJson是后台服务要求json数据.
除了固定参数,有时json还需要添加其他参数
所以需要定义一个向BaseJson添加数据的接口

public MaRetrofit addJson(String key, String value) {
        try {
            getBaseJson().put(key, value);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return this;
    }
下面首先Retrofit类全部代码

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
                //添加返回rx对象
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
                     //添加返回rx对象
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
UrlServiceInterface.class就是一些url请求方法名了
示例
由于参数都是又post json传递了所以这里goLogin就不需要在添加参数了

public interface UrlServiceInterface {
    String URL_LOGIN = "xxxx/xxxxx/valid_verify_code";
    String moblie = "mobile";
    String verifyCode = "verifyCode";
    /**
     * 登录
     * 添加参数 mobile
     * 添加参数 verifyCode
     */
    @POST(URL_LOGIN)
    Observable<BaseEntity<Bean>> goLogin();
    //如果不需要使用rxjava直接返回call对象即可
    @POST(URL_LOGIN)
    Call<BaseEntity<Bean>> goLoginCall();

    }
}
到这里retrofit2就简单封装完毕了
根据需要选择直接返回retrofit还是返回UrlServiceInterface对象

如果不使用rxjava的使用方式

  UrlServiceInterface loginService = new MaRetrofit()
                .addJson(UrlServiceInterface.moblie, tel)
                .addJson(UrlServiceInterface.verifyCode, code)
                .getUrlServiceInterface();
        Call<BaseEntity<Bean>> call =  loginService.goLoginCall();
        call.enqueue(new Callback<BaseEntity<Bean>>() {
            @Override
            public void onResponse(retrofit2.Call<BaseEntity<Bean>> call, Response<BaseEntity<Bean>> response) {
                //处理response返回对象
            }

            @Override
            public void onFailure(retrofit2.Call<BaseEntity<Bean>> call, Throwable t) {

            }
        });
BaseEntity.java

public class BaseEntity<T> implements Serializable {
    /**
     * code :
     * message :
     * resultData : {}
     * success :
     * token :
     */

    private String code;
    private String message;
    public T resultData;
    private boolean success;
    private String token;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }


    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

}
如果rxjava和retrofit结合,
先封装一个RxSchedulers

public class RxSchedulers {
    public static <T> ObservableTransformer<T, T> ioMain() {
        return new ObservableTransformer<T, T>() {
            @Override
            public ObservableSource<T> apply(@NonNull Observable<T> upstream) {
                return upstream.subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread());
            }
        };
    }
}
然后简单封装一个Observable对象

public abstract class BaseObserver<T> implements Observer<BaseEntity<T>> {
    private Context mContext;
    LogUtils log = LogUtils.hLog();
    private Disposable mDisposable;
    public BaseObserver(Context context) {
        mContext = context;
    }
    @Override
    public void onSubscribe(Disposable d) {
        mDisposable = d;
    }
    @Override
    public void onNext(BaseEntity<T> value) {
        //根据判断选择返回正确对象
        if (value.isSuccess()) {
            T t = value.resultData;
            if(t!=null){
                onHandleSuccess(t);
            }
        } else {
            onHandleError(value.getCode(), value.getMessage());
        }
        //有时需要返回的全部对象
        onHandle(value);
    }

    @Override
    public void onError(Throwable e) {
        log.d(" ", "error:" + e.toString());
        Toast.makeText(mContext, "网络异常，请稍后再试", Toast.LENGTH_LONG).show();
    }

    @Override
    public void onComplete() {
    }

    public abstract void onHandleSuccess(T t);
    public void onHandle(BaseEntity<T> baseEntity){
    };
    public void onHandleError(String code, String message) {
        Toast.makeText(mContext, message + code, Toast.LENGTH_LONG).show();
    }
}
下面使用rxjava和retrofit结合使用网络请求

 UrlServiceInterface loginService = new MaRetrofit()
                .addJson(UrlServiceInterface.moblie, tel)
                .addJson(UrlServiceInterface.verifyCode, code)
                .getUrlServiceInterface();

        Observable<BaseEntity<Bean>> observable = loginService.goLogin();

            observable.compose(RxSchedulers.<BaseEntity<Bean>>ioMain())
                .subscribe(new BaseObserver<Bean>(this) {
                    @Override
                    public void onHandleSuccess(Bean bean) {
                        //正常数据返回
                    }
                   @Override
                   public void onHandle(BaseEntity<Bean> baseEntity) {
                          super.onHandle(baseEntity);
                          //根据需要使用BaseEntity
                    }
                });
gradle引用包列表

    compile 'io.reactivex.rxjava2:rxandroid:2.0.1'
    compile 'io.reactivex.rxjava2:rxjava:2.1.0'
    compile 'com.google.code.gson:gson:2.6.2'
    compile 'com.squareup.retrofit2:converter-gson:2.1.0'//转换器，请求结果转换成Model
    compile 'com.squareup.retrofit2:adapter-rxjava2:2.2.0'
    compile 'com.squareup.okhttp3:okhttp:3.6.0'
结合网上许多资源 ,然后根据自己需求封装,
