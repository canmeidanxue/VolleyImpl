package com.wizarpos.atool.net.http;

import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.entity.FileEntity;
import org.apache.http.entity.InputStreamEntity;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicHeader;

import com.wizarpos.atool.net.log.Logger;

public final class HttpTool {
	
	private List<Header> headerList = new ArrayList<Header>();
	private HttpClient mHttpClient = null;
	
	public HttpTool() {}
	
	/**
	 * 同步的http动作。业务逻辑在获取HttpResponse以后自己处理。<br/>
	 * 注意：连接没有关闭。请自行处理
	 * 
	 * @param httpClient HttpClient对象，可以为null
	 * @param httpRequest HttpUriRequest
	 * @return HttpResponse
	 * @throws Exception 连接发生异常，或处理HTTP消息发生异常时抛出此异常。
	 */
	public HttpResponse doSyncHttp(HttpClient httpClient, HttpUriRequest httpRequest) throws Exception {
		return doSyncHttp(httpClient, httpRequest, null);
	}
	
	/**
	 * 同步的http动作。业务逻辑可以在Task中处理，也可以在获取HttpResponse以后自己处理。<br/>
	 * 注意：连接没有关闭。请自行处理
	 * 
	 * @param httpClient HttpClient对象，可以为null
	 * @param httpRequest HttpUriRequest
	 * @param params 此参数可以在Task的doInBackground中使用，should be null. Becuase you can not use it.
	 * @param task 可以为null
	 * @return
	 * @throws Exception 连接发生异常，或处理HTTP消息发生异常时抛出此异常。
	 */
	public HttpResponse doSyncHttp(HttpClient httpClient, HttpUriRequest httpRequest, Task task) throws Exception {
		HttpTask httpTask = new HttpTask(this);
		mHttpClient = getHttpClient(httpClient);
		httpTask.setHttpElement(mHttpClient, httpRequest);
		httpTask.setTask(task);
		httpTask.execute(new Object[0]);
		
		Logger.debug("等待执行任务 开始...");
		try {
			synchronized (this) {
				this.wait();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		Logger.debug("等待执行任务 结束...");
		
		int rcode = httpTask.getRcode();
		if (rcode == 1) {
			return httpTask.getHttpResponse();
		} else {
			throw httpTask.getException();
		}
	}
	
	/**
	 * 异步Http动作。业务逻辑可以在Task中处理<br/>
	 * 注意：连接没有关闭，请自行处理。
	 * 
	 * @param httpClient
	 * @param httpRequest
	 * @param params
	 * @param task
	 * @throws Exception
	 */
	public void doAsyncHttp(HttpClient httpClient, HttpUriRequest httpRequest, Task task) throws Exception {
		HttpTask httpTask = new HttpTask(this);
		mHttpClient = getHttpClient(httpClient);
		httpTask.setHttpElement(mHttpClient, httpRequest);
		httpTask.setTask(task);
		httpTask.setIsAsyncTask(true);
		httpTask.execute(new Object[0]);
	}
	
	/**
	 * 同步post方法。传文本参数。
	 * 
	 * @param url
	 * @param param 参数
	 * @param charset 参数编码方式
	 * @return
	 * @throws Exception
	 */
	public HttpResponse doPost(String url, String param, String charset) throws Exception {
		return doPost(url, new StringEntity(param, charset));
	}
	
	public void doAsyncPost(String url, String param, String charset, Task task) throws Exception {
		doAsyncPost(url, new StringEntity(param, charset), task);
	}
	
	/**
	 * 同步post方法。传递键值对列表。
	 *  
	 * @param url
	 * @param param NameValuePair list. the default subclass is BasicNameValuePair. The usage: new BasicNameValuePair(String name, String value);
	 * @param charset
	 * @return
	 * @throws Exception
	 */
	public HttpResponse doPost(String url, List<NameValuePair> param, String charset) throws Exception {
		return doPost(url, new UrlEncodedFormEntity(param, charset));
	}
	
	public void doAsyncPost(String url, List<NameValuePair> param, String charset, Task task) throws Exception {
		doAsyncPost(url, new UrlEncodedFormEntity(param, charset), task);
	}
	
	/**
	 * 同步post方法。传字节参数。
	 * 
	 * @param url
	 * @param param
	 * @return
	 * @throws Exception
	 */
	public HttpResponse doPost(String url, byte[] param) throws Exception {
		return doPost(url, new ByteArrayEntity(param));
	}
	
	public void doAsyncPost(String url, byte[] param, Task task) throws Exception {
		doAsyncPost(url, new ByteArrayEntity(param), task);
	}
	
	/**
	 * 同步post方法。传流参数。
	 * 
	 * @param url
	 * @param instream
	 * @param length
	 * @return
	 * @throws Exception
	 */
	public HttpResponse doPost(String url, InputStream instream, long length) throws Exception {
		return doPost(url, new InputStreamEntity(instream, length));
	}
	
	public void doAsyncPost(String url, InputStream instream, long length, Task task) throws Exception {
		doAsyncPost(url, new InputStreamEntity(instream, length), task);
	}
	
	/**
	 * 同步post方法。传文件参数。
	 * 
	 * @param url
	 * @param file
	 * @param contentType
	 * @return
	 * @throws Exception
	 */
	public HttpResponse doPost(String url, File file, String contentType) throws Exception {
		return doPost(url, new FileEntity(file, contentType));
	}
	
	public void doAsyncPost(String url, File file, String contentType, Task task) throws Exception {
		doAsyncPost(url, new FileEntity(file, contentType), task);
	}
	
	public HttpResponse doPost(String url, HttpEntity entity) throws Exception {
		HttpPost httpUri = new HttpPost(url);
		for (Header header : headerList) {
			httpUri.addHeader(header);
		}
		httpUri.setEntity(entity);
		return doSyncHttp(null, httpUri);
	}
	
	public void doAsyncPost(String url, HttpEntity entity, Task task) throws Exception {
		HttpPost httpUri = new HttpPost(url);
		for (Header header : headerList) {
			httpUri.setHeader(header);
		}
		httpUri.setEntity(entity);
		doAsyncHttp(null, httpUri, task);
	}
	
	/**
	 * 同步get方法。
	 * @param url
	 * @return
	 * @throws Exception
	 */
	public HttpResponse doGet(String url) throws Exception {
		HttpGet httpUri = new HttpGet(url);
		for (Header header : headerList) {
			httpUri.addHeader(header);
		}
		return doSyncHttp(null, httpUri);
	}
	
	/**
	 * 异步get方法。
	 * @param url
	 * @param task
	 * @throws Exception
	 */
	public void doAsyncGet(String url, Task task) throws Exception {
		HttpGet httpUri = new HttpGet(url);
		for (Header header : headerList) {
			httpUri.addHeader(header);
		}
		doAsyncHttp(null, httpUri, task);
	}
	
	private HttpClient getHttpClient(HttpClient httpClient) {
		if (this.mHttpClient == null) {
			if (httpClient == null) {
				mHttpClient = new DefaultHttpClient();
			} else {
				mHttpClient = httpClient;
			}
		}
		return mHttpClient;
	}
	
	public void setHttpClient(HttpClient httpClient) {
		this.mHttpClient = httpClient;
	}
	
	public void addHeader(Header header) {
		headerList.add(header);
	}
	
	public void addHeader(String name, String value) {
		headerList.add(new BasicHeader(name, value));
	}
	
	public void cleanAppendHeader() {
		headerList.clear();
	}
	
	public static interface Task {
		/**
		 * asynchroized task
		 * @param httpResponse 
		 * @param resultCode 1 success 2 has exception
		 * @param cause exist only for resultCode == 2
		 * @return HttpResponse
		 */
		public Object doInBackground(HttpResponse httpResponse, int resultCode, Throwable cause);
	}
}
