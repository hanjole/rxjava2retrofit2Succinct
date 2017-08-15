package com.h.rxjava2retrofit2succinct;

import com.h.rxjava2retrofit2succinct.bean.BaseEntity;
import com.h.rxjava2retrofit2succinct.bean.Bean;

import io.reactivex.Observable;
import retrofit2.Call;
import retrofit2.http.POST;

/**
 * Created by hs on 2017/8/10.
 */

public interface UrlServiceInterface {
    String URL_LOGIN = "/service/valid_verify_code";
    String moblie = "mobile";
    String verifyCode = "verifyCode";
    /**
     * 登录
     * 添加参数 mobile
     * 添加参数 verifyCode
     */
    @POST(URL_LOGIN)
    Observable<BaseEntity<Bean>> goLogin();

    @POST(URL_LOGIN)
    Call<BaseEntity<Bean>> goLoginCall();

}
