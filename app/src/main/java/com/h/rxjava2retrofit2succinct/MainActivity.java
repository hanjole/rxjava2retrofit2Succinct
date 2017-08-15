package com.h.rxjava2retrofit2succinct;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;

import com.h.rxjava2retrofit2succinct.bean.BaseEntity;
import com.h.rxjava2retrofit2succinct.bean.Bean;
import com.h.rxjava2retrofit2succinct.rx.BaseObserver;
import com.h.rxjava2retrofit2succinct.rx.RxSchedulers;

import io.reactivex.Observable;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        login();

    }
    BaseObserver observer;
    private void login() {
        observer = new BaseObserver<Bean>(MainActivity.this) {
            @Override
            public void onHandle(BaseEntity<Bean> baseEntity) {
                super.onHandle(baseEntity);
            }

            @Override
            public void onHandleSuccess(Bean baseEntity) {

            }
        };

        UrlServiceInterface loginService = new MaRetrofit()
                .addJson(UrlServiceInterface.moblie, "131*****1")
                .addJson(UrlServiceInterface.verifyCode, "2312")
                .getUrlServiceInterface();

        Observable<BaseEntity<Bean>> observable = loginService.goLogin();

        observable.compose(RxSchedulers.<BaseEntity<Bean>>ioMain())
                .subscribe(observer);


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
