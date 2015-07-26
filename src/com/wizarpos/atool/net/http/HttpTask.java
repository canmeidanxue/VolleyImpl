package com.wizarpos.atool.net.http;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpUriRequest;

import android.os.AsyncTask;

import com.wizarpos.atool.net.log.Logger;

class HttpTask extends AsyncTask<Object, String, Object> {
	
	private HttpTool httpTool = null;
	private HttpClient httpClient = null;
	private HttpUriRequest httpRequest = null;
	private HttpTool.Task task = null;
	private boolean isAsyncTask = false;
	
	private HttpResponse httpResponse = null;
	private int rcode = 1;
	private Exception e;
	
	HttpTask(HttpTool httpTool) {
		this.httpTool = httpTool;
	}
	
	void setHttpElement(HttpClient httpClient, HttpUriRequest httpRequest) {
		this.httpClient = httpClient;
		this.httpRequest = httpRequest;
	}
	
	void setTask(HttpTool.Task task) {
		this.task = task;
	}
	
	void setIsAsyncTask(boolean isAsyncTask) {
		this.isAsyncTask = isAsyncTask;
	}

	@Override
	protected Object doInBackground(Object... params) {
		Logger.debug("执行网络任务 开始...");
		
		try {
			httpResponse = httpClient.execute(httpRequest);
			rcode = 1;
		} catch (Exception e) {
			rcode = 2;
			this.e = e;
		}
		if (task != null) {
			task.doInBackground(httpResponse, rcode, e);
		}
		
		Logger.debug("执行网络任务 结束...");
		if (isAsyncTask == false) {
			synchronized (httpTool) {
				httpTool.notify();
			}
		}
		
		return httpResponse;
	}
	
	HttpResponse getHttpResponse() {
		return this.httpResponse;
	}
	
	Exception getException() {
		return this.e;
	}
	
	int getRcode() {
		return this.rcode;
	}
}
