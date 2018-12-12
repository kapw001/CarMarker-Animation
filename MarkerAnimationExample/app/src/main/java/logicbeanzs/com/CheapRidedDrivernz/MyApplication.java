package logicbeanzs.com.CheapRidedDrivernz;

import android.app.Activity;
import android.arch.lifecycle.Lifecycle;
import android.arch.lifecycle.LifecycleObserver;
import android.arch.lifecycle.OnLifecycleEvent;
import android.arch.lifecycle.ProcessLifecycleOwner;
import android.content.Context;
import android.support.multidex.MultiDex;
import android.support.multidex.MultiDexApplication;
import android.text.TextUtils;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

import logicbeanzs.com.CheapRidedDrivernz.Utils.NoInternetDialog;


/**
 * Created by sudip_j0hgrea on 7/26/2017.
 */

public class MyApplication extends MultiDexApplication implements LifecycleObserver {
    private static MyApplication appInstance;
    private static RequestQueue mRequestQueue;
    private String TAG;
    @Override
    protected void attachBaseContext(Context context) {
        super.attachBaseContext(context);
        MultiDex.install(this);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        appInstance = this;
        ProcessLifecycleOwner.get().getLifecycle().addObserver(this);
//        noInternetDialog = new NoInternetDialog.Builder(this).build();
    }
    NoInternetDialog noInternetDialog;
    @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
    private void onAppBackgrounded() {
//        noInternetDialog.onDestroy();
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_START)
    private void onAppForegrounded() {
//        noInternetDialog = new NoInternetDialog.Builder(this).build();
//        noInternetDialog.setCancelable(false);
    }
    public static RequestQueue getRequestQueue() {
        if (mRequestQueue == null) {
            // getApplicationContext() is key, it keeps you from leaking the
            // Activity or BroadcastReceiver if someone passes one in.
            mRequestQueue = Volley.newRequestQueue(appInstance);
        }
        return mRequestQueue;
    }

    public <T> void addToRequestQueue(Request<T> req, String tag) {
        if (TextUtils.isEmpty(tag)) {
            tag = TAG;
        }
        req.setTag(tag);
        getRequestQueue().add(req);
    }

    public <T> void addToRequestQueue(Request<T> req) {
        req.setTag(TAG);
        getRequestQueue().add(req);
    }

    public static synchronized MyApplication getAppInstance() {
        return appInstance;
    }


}
