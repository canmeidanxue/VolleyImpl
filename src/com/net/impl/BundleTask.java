package com.net.impl;

import java.util.HashMap;
import java.util.Map;

import android.os.AsyncTask;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.example.com.present.BasePresenter.ResultListener;
import com.wizarpos.atool.net.volley2.MsgRequest;
import com.wizarpos.atool.net.volley2.Response;
import com.wizarpos.pay.common.utils.Logger2;

public class BundleTask extends AsyncTask<Void, Void, Void> {

	private HashMap<String, String> header;// 请求头
	private Map<String, Object> requestParams;
	private String serviceCode;

	private MsgRequest msgRequest;
	private ResultListener listener;
	private String serverUrl;

	public void setServiceCode(String serviceCode) {
		this.serviceCode = serviceCode;
	}

	public void setListener(ResultListener listener) {
		this.listener = listener;
	}

	public void setRequestParams(Map<String, Object> requestParams) {
		this.requestParams = requestParams;
	}

	public HashMap<String, String> getHeader() {
		return header;
	}

	public MsgRequest getMsgRequest() {
		return msgRequest;
	}

	public String getServerUrl() {
		return serverUrl;
	}

	@Override
	protected Void doInBackground(Void... params) {
		msgRequest = bundleRequest(serviceCode, requestParams);
		header = bundleHeader();
		serverUrl = bundleServiceUrl();
		return null;
	}

	@Override
	protected void onPostExecute(Void result) {
		super.onPostExecute(result);
		if (listener != null) {
			listener.onSuccess(new Response(0, "success"));
		}
	}

	private String bundleServiceUrl() {//拼接网络请求地址
//		return "http://"
//				+ AppConfigHelper.getConfig(AppConfigDef.ip,
//						Constants.DEFAULT_IP)
//				+ ":"
//				+ AppConfigHelper.getConfig(AppConfigDef.port,
//						Constants.DEFAULT_PORT) + Constants.SUFFIX_URL;
		return "http://fashion.chinadaily.com.cn/2014-06/04/content_17562553.htm";
	}

	private MsgRequest bundleRequest(String serviceCode,
			Map<String, Object> params) {
		appendParams(params);
		/* suffix */
//		String mid = AppConfigHelper.getConfig(AppConfigDef.mid);
//		String fid = AppConfigHelper.getConfig(AppConfigDef.fid);
//		String operatorNo = AppConfigHelper.getConfig(AppConfigDef.operatorNo);
//		Map<String, Object> suffix = new HashMap<String, Object>();
//		suffix.put("mid", mid);
//		suffix.put("fid", fid);
//		suffix.put("operatorNo", operatorNo);

		/* pubCertificate */
//		String _pubCertificate = AppStateManager
//				.getState(AppStateDef.PUB_CERT_AILAS);

		MsgRequest msgRequest = new MsgRequest();
		msgRequest.setServiceCode(serviceCode);
		msgRequest.setParam(params);

//		msgRequest.setPem(_pubCertificate);
//
//		msgRequest.setSuffix(suffix);

		String signPart = JSON.toJSONString(msgRequest.getParam(),
				SerializerFeature.WriteDateUseDateFormat);
		Logger2.debug("serviceCode:" + serviceCode + "\nparam:" + signPart);
//		byte[] s = DeviceManager.getInstance().doRSAEncrypt(signPart);
//		msgRequest.setSignature(ByteUtil.byteToHex(s));
		return msgRequest;
	}

	private HashMap<String, String> bundleHeader() {//拼接请求头
		/* header */
	/*	String sn = AppConfigHelper.getConfig(AppConfigDef.sn);
		String merchantId = AppConfigHelper.getConfig(AppConfigDef.merchantId);
		String operatorId = AppConfigHelper.getConfig(AppConfigDef.operatorId);
		String terminalId = AppConfigHelper.getConfig(AppConfigDef.terminalId);
		HashMap<String, String> header = new HashMap<String, String>();
		header.put("sn", sn);
		header.put("merchantId", merchantId);
		header.put("operatorId", operatorId);
		header.put("pos_code", terminalId);
		if (Constants.TRUE.equals(AppConfigHelper
				.getConfig(AppConfigDef.test_load_safe_mode))) {
			header.put("mobileType", "");
		} else {
			header.put("mobileType", "hanxin");
		}*/
		header.put("charset", "UTF-8");// 将编码格式设为utf-8，避免原先的在parseNetworkResponse方法中设死为“UTF-8”
		// (volley解析服务器返回的数据使用的编码格式是从请求头中获取，如果请求头中没有，则默认使用ISO-8859-1参照HttpHeaderParser)
		// wu@[20150329]
		// NetRequest.getInstance().setHeader(header);
		return header;
	}

	/**
	 * 每次请求带上收单商户号,终端号,慧商户号,收单渠道,收单操作员号
	 * 
	 * @param params
	 */
	private void appendParams(Map<String, Object> params) {
		/*if (!params.containsKey("merchantId")) {
			params.put("merchantId",
					AppConfigHelper.getConfig(AppConfigDef.merchantId));
		}
		if (!params.containsKey("terminalId")) {
			params.put("terminalId",
					AppConfigHelper.getConfig(AppConfigDef.terminalId));
		}
		if (!params.containsKey("payId")) {
			params.put("payId", AppConfigHelper.getConfig(AppConfigDef.pay_id));
		}
		if (!params.containsKey("mid")) {
			params.put("mid", AppConfigHelper.getConfig(AppConfigDef.mid));
		}
		if (!params.containsKey("operatorId")) {
			params.put("operatorId",
					AppConfigHelper.getConfig(AppConfigDef.operatorId));
		}*/
	}

}
