package com.wizarpos.atool.net.volley;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkError;
import com.android.volley.NetworkResponse;
import com.android.volley.NoConnectionError;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;

public class VolleyErrorHelper {
	/**
	 * Returns appropriate message which is to be displayed to the user against
	 * the specified error object.
	 * 
	 * @param error
	 * @param context
	 * @return
	 */
	public static String getMessage(Object error) {
		if (error instanceof TimeoutError) {
			return "请求超时";
		} else if (isServerProblem(error)) {
			return handleServerError(error);
		} else if (isNetworkProblem(error)) {
			return "无法链接到服务器";
		}
		return "未知异常";
	}

	/**
	 * Determines whether the error is related to network
	 * 
	 * @param error
	 * @return
	 */
	private static boolean isNetworkProblem(Object error) {
		return (error instanceof NetworkError) || (error instanceof NoConnectionError);
	}

	/**
	 * Determines whether the error is related to server
	 * 
	 * @param error
	 * @return
	 */
	private static boolean isServerProblem(Object error) {
		return (error instanceof ServerError) || (error instanceof AuthFailureError);
	}

	/**
	 * Handles the server error, tries to determine whether to show a stock
	 * message or to show a message retrieved from the server.
	 * 
	 * @param err
	 * @param context
	 * @return
	 */
	private static String handleServerError(Object err) {
		VolleyError error = (VolleyError) err;

		NetworkResponse response = error.networkResponse;

		if (response != null) {
			switch (response.statusCode) {
			case 404:
			case 422:
			case 401:
				try {
					return new String(response.data);
				} catch (Exception e) {
					e.printStackTrace();
				}
				// invalid request
				if (error.getMessage() == null || "".equals(error.getMessage())) {
					return "未知异常";
				} else {
					return error.getMessage();
				}
			default:
				return "服务器未知异常";
			}
		}
		return "未知异常";
	}
}