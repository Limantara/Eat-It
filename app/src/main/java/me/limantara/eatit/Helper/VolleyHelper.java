package me.limantara.eatit.Helper;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.LruCache;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.Volley;

/**
 * Created by edwinlimantara on 8/1/15.
 */
public class VolleyHelper {

    private static VolleyHelper mInstance;
    private RequestQueue mRequestQueue;
    private ImageLoader mImageLoader;
    private static Context mCtx;

    private VolleyHelper(Context context) {
        mCtx = context;
        mRequestQueue = getRequestQueue();

        mImageLoader = new ImageLoader(mRequestQueue,
                new ImageLoader.ImageCache() {
                    @Override
                    public Bitmap getBitmap(String s) {
                        return null;
                    }

                    @Override
                    public void putBitmap(String s, Bitmap bitmap) {

                    }

                    private final LruCache<String, Bitmap> cache =
                            new LruCache<>(20);
                });
    }

    public static synchronized VolleyHelper getInstance(Context context) {
        if (mInstance == null) {
            mInstance = new VolleyHelper(context);
        }
        return mInstance;
    }

    public RequestQueue getRequestQueue() {
        if(mRequestQueue == null) {
            mRequestQueue = Volley.newRequestQueue(mCtx.getApplicationContext());
        }
        return mRequestQueue;
    }

    public <T> void addToRequestObject(Request<T> req) {
        getRequestQueue().add(req);
    }

    public ImageLoader getmImageLoader() {
        return mImageLoader;
    }

}