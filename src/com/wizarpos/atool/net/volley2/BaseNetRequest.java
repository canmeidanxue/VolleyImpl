package com.wizarpos.atool.net.volley2;

import java.util.HashMap;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.wizarpos.atool.net.log.Logger;

/**
 * 根据业务封装请求.
 * 
 * @author wu
 * 
 */
public class BaseNetRequest extends BaseRequest {

	protected String serverUrl = "";// 网络请求地址

	protected static BaseNetRequest request;

	public static BaseNetRequest getInstance() {
		if (request == null) {
			request = new BaseNetRequest();
		}
		return request;
	}

	/**
	 * post 请求
	 * 
	 * @param serviceCode
	 *            服务码
	 * @param msgRequest
	 *            请求实体
	 * @param header
	 *            头
	 * @param tag
	 *            标识
	 * @param listener
	 *            回调
	 */
	public void addRequest(String serviceCode, MsgRequest msgRequest, HashMap<String, String> header, String tag, ResponseListener listener) {
		String reqParam = JSON.toJSONString(msgRequest, SerializerFeature.WriteDateUseDateFormat);
		Logger.debug("请求实体：" + reqParam);
		super.addRequest(serverUrl, reqParam, header, tag, listener);
	}

	public String getServerUrl() {
		return serverUrl;
	}

	public void setServerUrl(String serverUrl) {
		this.serverUrl = serverUrl;
	}

}
