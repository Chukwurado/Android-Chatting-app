package com.example.letschat.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.LruCache;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.Volley;

public class APISingleton {
    private static APISingleton instance;
    private RequestQueue requestQueue;
    private ImageLoader imageLoader;
    private static Context ctx;

    private APISingleton(Context context) {
        ctx = context;
        requestQueue = getRequestQueue();

    }

    public static synchronized APISingleton getInstance(Context context) {
        if (instance == null) {
            instance = new APISingleton(context);
        }
        return instance;
    }

    public RequestQueue getRequestQueue() {
        if (requestQueue == null) {
            // getApplicationContext() is key, it keeps you from leaking the
            // Activity or BroadcastReceiver if someone passes one in.
            requestQueue = Volley.newRequestQueue(ctx.getApplicationContext());
        }
        return requestQueue;
    }

}
