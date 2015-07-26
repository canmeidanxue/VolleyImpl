package com.net.impl;

import java.util.HashMap;
import java.util.Map;

import android.text.TextUtils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.wizarpos.atool.net.volley2.BaseNetRequest;
import com.wizarpos.atool.net.volley2.MsgRequest;
import com.wizarpos.atool.tool.ByteUtil;
import com.wizarpos.pay.common.Constants;
import com.wizarpos.pay.common.device.DeviceManager;
import com.wizarpos.pay.common.utils.Logger2;
import com.wizarpos.pay.db.AppConfigDef;
import com.wizarpos.pay.db.AppConfigHelper;
import com.wizarpos.pay.db.AppStateDef;
import com.wizarpos.pay.db.AppStateManager;

/**
 * 根据业务封装请求.
 * 
 * @author wu
 */
public class NetRequest extends BaseNetRequest {

	protected static NetRequest request;

	public static NetRequest getInstance() {
		if (request == null) {
			request = new NetRequest();
		}
		return request;
	}

	/**
	 * 新增网络请求 <br>
	 * 所有请求默认带上:<br>
	 * merchantId(收单商户号),<br>
	 * terminalId(终端号),<br>
	 * payId(收单渠道),<br>
	 * mid(慧商户号),<br>
	 * operatorId(操作员号)
	 * 
	 * @param serviceCode
	 *            接口码
	 * @param params
	 *            请求内容
	 * @param listener
	 *            回调
	 */
	public void addRequest(String serviceCode, Map<String, Object> params,
			ResponseListener listener) {
		String tag = BaseNetRequest.getInstance().getClass().getSimpleName();
		this.addRequest(serviceCode, params, tag, listener);
	}

	/**
	 * 用于获取服务员信息
	 * 
	 * @param serviceCode
	 *            接口码
	 * @param params
	 *            请求内容
	 * @param listener
	 *            回调
	 */
	public void addRequestCashier(final String serviceCode,
			Map<String, Object> params, final String tag,
			final ResponseListener listener) {
		if (params == null) {
			params = new HashMap<String, Object>();
		}
		MsgRequest msgRequest = bundleRequest(serviceCode, params);
		HashMap<String, String> header = bundleHeader();
		setServerUrl("http://cashier2.wizarpos.com:80/cashier-server/serviceV1_0");
		Logger2.debug("请求地址:--->" + serverUrl);
		addRequest(serviceCode, msgRequest, header, tag, listener);
	}

	/**
	 * 新增网络请求 <br>
	 * 所有请求默认带上:<br>
	 * merchantId(收单商户号),<br>
	 * terminalId(终端号),<br>
	 * payId(收单渠道),<br>
	 * mid(慧商户号),<br>
	 * operatorId(操作员号)
	 * 
	 * @param serviceCode
	 *            接口码
	 * @param params
	 *            请求内容
	 * @param tag
	 *            标识,可用于撤销请求
	 * @param listener
	 *            回调
	 */
	public void addRequest(final String serviceCode,
			Map<String, Object> params, final String tag,
			final ResponseListener listener) {
		if (params == null) {
			params = new HashMap<String, Object>();
		}
		// final BundleTask bundle = new BundleTask();
		// bundle.setServiceCode(serviceCode);
		// bundle.setRequestParams(params);
		// bundle.setListener(new ResultListener() {
		//
		// @Override
		// public void onSuccess(Response response) {
		// MsgRequest msgRequest = bundle.getMsgRequest();
		// HashMap<String, String> header = bundle.getHeader();
		// setServerUrl(bundle.getServerUrl());
		// addRequest(serviceCode, msgRequest, header, tag, listener);
		// }
		//
		// @Override
		// public void onFaild(Response response) {
		// }
		// });
		// bundle.execute();
		MsgRequest msgRequest = bundleRequest(serviceCode, params);
		HashMap<String, String> header = bundleHeader();
		setServerUrl(bundleServiceUrl());
		Logger2.debug("请求地址:--->" + serverUrl);
		addRequest(serviceCode, msgRequest, header, tag, listener);
	}

	private String bundleServiceUrl() {
		return "www.baidu.com";
//		return "http://" + AppConfigHelper.getConfig(AppConfigDef.ip, Constants.DEFAULT_IP) + ":"
//				+ AppConfigHelper.getConfig(AppConfigDef.port, Constants.DEFAULT_PORT) + Constants.SUFFIX_URL;
	}

	private MsgRequest bundleRequest(String serviceCode,
			Map<String, Object> params) {
		appendParams(params);
		handleTransCode(params);
		/* suffix */
		String mid = AppConfigHelper.getConfig(AppConfigDef.mid);
		String fid = AppConfigHelper.getConfig(AppConfigDef.fid);
		String operatorNo = AppConfigHelper.getConfig(AppConfigDef.operatorNo);
		String operatorName = AppConfigHelper
				.getConfig(AppConfigDef.operatorTrueName);
		Map<String, Object> suffix = new HashMap<String, Object>();
		suffix.put("mid", mid);
		suffix.put("fid", fid);
		suffix.put("operatorNo", operatorNo);
		suffix.put("operatorName", operatorName);

		/* pubCertificate */
		String pubCertificate = AppStateManager
				.getState(AppStateDef.PUB_CERT_AILAS);

		MsgRequest msgRequest = new MsgRequest();
		msgRequest.setServiceCode(serviceCode);
		msgRequest.setParam(params);
		if (Constants.TRUE.equals(AppConfigHelper
				.getConfig(AppConfigDef.test_load_safe_mode))) {
			if (!TextUtils.isEmpty(pubCertificate)) {
				msgRequest.setPem(pubCertificate);
			}
		}

		msgRequest.setSuffix(suffix);

		String signPart = JSON.toJSONString(msgRequest.getParam(),
				SerializerFeature.WriteDateUseDateFormat);
		Logger2.debug("serviceCode:" + serviceCode + "\nparam:" + signPart);
		byte[] s = DeviceManager.getInstance().doRSAEncrypt(signPart);
		msgRequest.setSignature(ByteUtil.byteToHex(s));
		return msgRequest;
	}

	private HashMap<String, String> bundleHeader() {
		/* header */
		String sn = AppConfigHelper.getConfig(AppConfigDef.sn);
		String operatorId = AppConfigHelper.getConfig(AppConfigDef.operatorNo);
		String operatorName = AppConfigHelper
				.getConfig(AppConfigDef.operatorTrueName);
		HashMap<String, String> header = new HashMap<String, String>();
		header.put("sn", sn);
		header.put("mid", AppConfigHelper.getConfig(AppConfigDef.mid));
		header.put("operatorId", operatorId);
		header.put("operatorName", operatorName);
		// if
		// (Constants.FALSE.equals(AppConfigHelper.getConfig(AppConfigDef.test_load_safe_mode)))
		// {
		header.put("mobileType", "hanxin");
		// }
		header.put("charset", "UTF-8");
		return header;
	}

	/**
	 * 每次请求带上收单商户号,终端号,慧商户号,收单渠道,收单操作员号
	 * 
	 * @param params
	 */
	private void appendParams(Map<String, Object> params) {
		if (!params.containsKey("merchantId")) {
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
		}
	}

	/**
	 * 修改订单号
	 * 
	 * @param params
	 */
	private Map<String, Object> handleTransCode(Map<String, Object> params) {

		return params;
	}

//	public void addRequest(String url, Object req, String tag,
//			ResponseListener listener) {
//		String reqStr = "";
//		try {
//			reqStr = SignUtil.bundleRequest(req).toString();
//			if (reqStr.endsWith("&")) {
//				reqStr = reqStr.substring(0, reqStr.length() - 1);
//			}
//		} catch (Exception e) {
//			e.printStackTrace();
//			Logger2.debug("请求参数无法解析为json");
//			listener.onFaild(new Response(1, "请求失败，参数错误"));
//			return;
//		}
//		HashMap<String, String> header = new HashMap<String, String>();
//		header.put("charset", "UTF-8");
//		Logger2.debug("请求地址：" + url);
//		Logger2.debug("请求参数：" + reqStr);
//		super.addRequest(url, reqStr, header, tag, listener);
//	}

//	public void addRequest(String url, HashMap<String, String> req, String tag,
//			ResponseListener listener) {
//		String reqStr = "";
//		try {
//			reqStr = SignUtil.bundleRequest(req).toString();
//			if (reqStr.endsWith("&")) {
//				reqStr = reqStr.substring(0, reqStr.length() - 1);
//			}
//		} catch (Exception e) {
//			e.printStackTrace();
//			Logger2.debug("请求参数无法解析为json");
//			listener.onFaild(new Response(1, "请求失败，参数错误"));
//			return;
//		}
//		HashMap<String, String> header = new HashMap<String, String>();
//		header.put("charset", "UTF-8");
//		Logger2.debug("请求地址：" + url);
//		Logger2.debug("请求参数：" + reqStr);
//		super.addRequest(url, reqStr, header, tag, listener);
//	}

}
