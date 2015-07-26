/*
 * Copyright (C) 2011 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.wizarpos.atool.net.volley;

import java.io.UnsupportedEncodingException;
import java.util.Map;

import android.text.TextUtils;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.Request.Method;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HttpHeaderParser;

/**
 * A canned request for retrieving the response body at a given URL as a String.
 */
public class StringRequest extends Request<String> {
	private final Listener<String> mListener;
	private Map<String, String> header;
	private String params;

	/**
	 * Creates a new request with the given method.
	 * 
	 * @param method
	 *            the request {@link Method} to use
	 * @param url
	 *            URL to fetch the string at
	 * @param listener
	 *            Listener to receive the String response
	 * @param errorListener
	 *            Error listener, or null to ignore errors
	 */
	public StringRequest(int method, String url, Listener<String> listener,
			ErrorListener errorListener) {
		super(method, url, errorListener);
		mListener = listener;
	}

	/**
	 * 
	 * @param url 请求地址
	 * @param params  请求参数
	 * @param header 头
	 * @param listener 
	 * @param errorListener
	 */
	public StringRequest(String url, String params, Map<String, String> header,
			Listener<String> listener, ErrorListener errorListener) {
		super(Method.POST, url, errorListener);
		mListener = listener;
		this.params = params;
		this.header = header;
	}

	@Override
	public Map<String, String> getHeaders() throws AuthFailureError {
		if (header != null) {//除bug wu@[20150225]
			return header;
		} else {
			return super.getHeaders();
		}
	}

	/**
	 * Creates a new GET request.
	 * 
	 * @param url
	 *            URL to fetch the string at
	 * @param listener
	 *            Listener to receive the String response
	 * @param errorListener
	 *            Error listener, or null to ignore errors
	 */
	public StringRequest(String url, Listener<String> listener,
			ErrorListener errorListener) {
		this(Method.GET, url, listener, errorListener);
	}

	@Override
	public byte[] getBody() throws AuthFailureError {
		if (TextUtils.isEmpty(params)) {
			return null;
		}
		try {
			return params.getBytes(getParamsEncoding());
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
			VolleyError error = new VolleyError(e.getMessage());
			deliverError(error);// 分发错误
		}
		return null;
	}

	@Override
	protected void deliverResponse(String response) {
		mListener.onResponse(response);
	}

	@Override
	protected Response<String> parseNetworkResponse(NetworkResponse response) {
		String parsed;
		try {
			parsed = new String(response.data,
					HttpHeaderParser.parseCharset(response.headers));
//			parsed = new String(response.data,getParamsEncoding());//此处使用UTF-8 wu@[20150225]  ；改为在header中指定 wu@[20150329]
		} catch (UnsupportedEncodingException e) {
			parsed = new String(response.data);
		}
		return Response.success(parsed,
				HttpHeaderParser.parseCacheHeaders(response));
	}
}
