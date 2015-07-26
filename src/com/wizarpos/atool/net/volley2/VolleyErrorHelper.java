package com.wizarpos.atool.net.volley2;

import android.text.TextUtils;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkError;
import com.android.volley.NetworkResponse;
import com.android.volley.NoConnectionError;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;

public class VolleyErrorHelper {
    /**
     * Returns appropriate message which is to be displayed to the user against the specified error object.
     *
     * @param error
     * @return
     */
    public static Response getMessage(Object error) {
        Response response = new Response(1,"未知异常");
        if (error instanceof TimeoutError) {
            response.code = BaseNetRequest.NETWORK_ERR;
            response.msg = "请求超时";
        } else if (isServerProblem(error)) {
            response.code = BaseNetRequest.NETWORK_ERR;
            response.msg = handleServerError(error);
        } else if (isNetworkProblem(error)) {
            response.code = BaseNetRequest.NETWORK_ERR;
            response.msg = "无法链接到服务器";
        }
        return response;
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
     * Handles the server error, tries to determine whether to show a stock message or to show a message retrieved from the server.
     *
     * @param err
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
                    // invalid request
                    if (TextUtils.isEmpty(error.getMessage())) {
                        return "服务器未知异常" + "[" + response.statusCode + "]";
                    } else {
                        return error.getMessage() + "[" + response.statusCode + "]";
                    }
                default:
                    return "服务器未知异常"+"[" + response.statusCode + "]";
            }
        }
        return "服务器未知异常";
    }
}