package com.wizarpos.atool.net.volley2;

import android.app.Application;
import android.text.TextUtils;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

/**
 * Volley 单例 Application Created by wu on 2014/9/23.
 */
public class VolleyApplication extends Application {

	public static final String TAG = VolleyApplication.class.getSimpleName();

	private RequestQueue mRequestQueue, mSSLRequestQueue;

	private static VolleyApplication mInstance;

	@Override
	public void onCreate() {
		super.onCreate();
		mInstance = this;
	}

	public static synchronized VolleyApplication getInstance() {
		return mInstance;
	}

	public RequestQueue getRequestQueue() {
		if (mRequestQueue == null) {
			mRequestQueue = Volley.newRequestQueue(getApplicationContext());
		}

		return mRequestQueue;
	}

	public RequestQueue getSSLRequestQueue() {
		if (mSSLRequestQueue == null) {
			mSSLRequestQueue = Volley.newRequestQueue(getApplicationContext());
		}
		return mSSLRequestQueue;
	}

	public <T> void addToRequestQueue(Request<T> req, String tag) {
		// set the default tag if tag is empty
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
