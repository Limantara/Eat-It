package me.limantara.eatit.app;

import android.app.Application;
import android.text.TextUtils;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.Volley;

import me.limantara.eatit.volley.LruBitmapCache;

/**
 * Created by edwinlimantara on 8/1/15.
 */
public class AppController extends Application {

    public static final String TAG = AppController.class.getSimpleName();

    private RequestQueue mRequestQueue;
    private ImageLoader mImageLoader;
    LruBitmapCache mLruBitmapCache;

    public static final int DEFAULT_BUDGET_PREFERENCE = 11; // 11 USD
    public static final int DEFAULT_DISTANCE_PREFERENCE = 5; // 5 miles

    public static final Float DEFAULT_LATITUDE = new Float(37.3434479);
    public static final Float DEFAULT_LONGITUDE = new Float(-121.8822139);
    private static AppController mInstance;

    private static Float longitude;
    private static Float latitude;

    public static void setLatitude(Float lat) {
        latitude = lat;
    }

    public static void setLongitude(Float lgt) {
        longitude = lgt;
    }

    public static Float getLongitude() {
        return longitude == null ? DEFAULT_LONGITUDE : longitude;
    }

    public static Float getLatitude() {
        return latitude == null ? DEFAULT_LATITUDE : latitude;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mInstance = this;
    }

    public static synchronized AppController getInstance() {
        return mInstance;
    }

    public RequestQueue getRequestQueue() {
        if (mRequestQueue == null) {
            mRequestQueue = Volley.newRequestQueue(getApplicationContext());
        }

        return mRequestQueue;
    }

    public ImageLoader getImageLoader() {
        getRequestQueue();

        if (mImageLoader == null) {
            getLruBitmapCache();
            mImageLoader = new ImageLoader(this.mRequestQueue, mLruBitmapCache);
        }

        return this.mImageLoader;
    }

    public LruBitmapCache getLruBitmapCache() {
        if (mLruBitmapCache == null)
            mLruBitmapCache = new LruBitmapCache();
        return this.mLruBitmapCache;
    }

    public <T> void addToRequestQueue(Request<T> req, String tag) {
        req.setTag(TextUtils.isEmpty(tag) ? TAG : tag);
        getRequestQueue().add(req);
    }

    public <T> void addToRequestQueue(Request<T> req) {
        req.setTag(TAG);
        getRequestQueue().add(req);
    }

    public void cancelPendingRequests(Object tag) {
        if (mRequestQueue != null) {
            mRequestQueue.cancelAll(tag);
        }
    }
}
