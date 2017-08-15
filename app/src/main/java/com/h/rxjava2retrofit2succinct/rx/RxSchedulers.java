package com.h.rxjava2retrofit2succinct.rx;

import android.content.Context;

import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.ObservableTransformer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by hs on 2017/8/10.
 */

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

    public static <T> ObservableTransformer<T, T> compose(final Context context) {
        return new ObservableTransformer<T, T>() {
            @Override
            public ObservableSource<T> apply(@NonNull Observable<T> observable) {
                return observable
                        .subscribeOn(Schedulers.io())
//                        .doOnSubscribe(new Consumer<Disposable>() {
//                            @Override
//                            public void accept(Disposable disposable) throws Exception {
//                                if (!NetworkUtils.isNetworkAvailable(context)) {
//                                    Toast.makeText(context, R.string.toast_network_error, Toast.LENGTH_SHORT).show();
//                                }
//                            }
//                        })
                        .observeOn(AndroidSchedulers.mainThread());
            }
        };
    }

}
