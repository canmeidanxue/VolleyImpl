package com.net.impl;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import android.os.AsyncTask;

import com.wizarpos.atool.net.volley2.Response;
import com.wizarpos.pay.common.Constants;
import com.wizarpos.pay.common.base.BasePresenter.ResultListener;
import com.wizarpos.pay.common.utils.Logger2;
import com.wizarpos.pay.db.AppConfigDef;
import com.wizarpos.pay.db.AppConfigHelper;

public class NetLineHelper extends AsyncTask<Void, Void, Boolean> {

	private ResultListener listener;

	public void setListener(ResultListener listener) {
		this.listener = listener;
	}

	@Override
	protected Boolean doInBackground(Void... params) {
		String serviceUrl = "http://"
				+ AppConfigHelper.getConfig(AppConfigDef.ip,
						Constants.DEFAULT_IP)
				+ ":"
				+ AppConfigHelper.getConfig(AppConfigDef.port,
						Constants.DEFAULT_PORT) + Constants.SUFFIX_URL;
		// String serviceUrl = "http://10.0.0.59:8090"+ Constants.SUFFIX_URL;
		boolean isConn = false;
		HttpURLConnection conn = null;
		try {
			URL url = new URL(serviceUrl);
			Logger2.debug("服务器地址:" + serviceUrl);
			conn = (HttpURLConnection) url.openConnection();
			conn.setConnectTimeout(5 * 1000);
			if (conn.getResponseCode() == 200) {
				isConn = true;
			}
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			conn.disconnect();
		}
		return isConn;
	}

	@Override
	protected void onPostExecute(Boolean result) {
		super.onPostExecute(result);
		if (result) {
			listener.onSuccess(new Response(0, "success"));
		} else {
			listener.onFaild(new Response(1, "无法连接到服务器"));
		}
	}

}
