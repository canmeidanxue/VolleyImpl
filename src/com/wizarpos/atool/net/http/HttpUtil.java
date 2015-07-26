package com.wizarpos.atool.net.http;

import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.security.KeyStore;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.apache.http.HttpResponse;
import org.apache.http.HttpVersion;
import org.apache.http.client.HttpClient;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;
import org.apache.http.util.EntityUtils;

import com.wizarpos.atool.net.http.HttpTool.Task;

public class HttpUtil {
	
	private static final String CHARSET = "utf-8";

	public static String doGet(String url) throws Exception {
		HttpTool httpTool = new HttpTool();
		httpTool.setHttpClient(getHttpClient());
		HttpResponse res = httpTool.doGet(url);
		if (res.getStatusLine().getStatusCode() != 200) {
			throw new IllegalStateException("Request has some problem. The status code is " + res.getStatusLine().getStatusCode());
		}
		return EntityUtils.toString(res.getEntity(), CHARSET);
	}
	
	public static void doGet(String url, Task task) throws Exception {
		HttpTool httpTool = new HttpTool();
		httpTool.setHttpClient(getHttpClient());
		httpTool.doAsyncGet(url, task);
	}
	
	public static String doPostForString(String url, String param) throws Exception {
		HttpTool httpTool = new HttpTool();
		httpTool.setHttpClient(getHttpClient());
		HttpResponse res = httpTool.doPost(url, param, CHARSET);
		if (res.getStatusLine().getStatusCode() != 200) {
			throw new IllegalStateException("Request has some problem. The status code is " + res.getStatusLine().getStatusCode());
		}
		return EntityUtils.toString(res.getEntity(), CHARSET);
	}
	
	public static byte[] doPostForBytes(String url, String param) throws Exception {
		HttpTool httpTool = new HttpTool();
		httpTool.setHttpClient(getHttpClient());
		HttpResponse res = httpTool.doPost(url, param, CHARSET);
		if (res.getStatusLine().getStatusCode() != 200) {
			throw new IllegalStateException("Request has some problem. The status code is " + res.getStatusLine().getStatusCode());
		}
		return EntityUtils.toByteArray(res.getEntity());
	}
	
	public static InputStream doPostForStream(String url, String param) throws Exception {
		HttpTool httpTool = new HttpTool();
		httpTool.setHttpClient(getHttpClient());
		HttpResponse res = httpTool.doPost(url, param, CHARSET);
		if (res.getStatusLine().getStatusCode() != 200) {
			throw new IllegalStateException("Request has some problem. The status code is " + res.getStatusLine().getStatusCode());
		}
		return res.getEntity().getContent();
	}
	
	public static HttpClient getHttpClient() {
		try {
			HttpClient httpClient = null;

			final SSLContext ctx = SSLContext.getInstance("TLS");
			X509TrustManager tm = new X509TrustManager() {
				public X509Certificate[] getAcceptedIssuers() {
					return null;
				}

				public void checkClientTrusted(X509Certificate[] arg0, String arg1) throws CertificateException {
				}

				public void checkServerTrusted(X509Certificate[] arg0, String arg1) throws CertificateException {
				}
			};
			ctx.init(null, new TrustManager[] { tm }, null);

			KeyStore trustStore = KeyStore.getInstance(KeyStore.getDefaultType());
			trustStore.load(null, null);
			SSLSocketFactory ssf = new SSLSocketFactory(trustStore) {
				@Override
				public Socket createSocket(Socket socket, String host, int port, boolean autoClose) throws IOException,
						UnknownHostException {
					return ctx.getSocketFactory().createSocket(socket, host, port, autoClose);
				}

				@Override
				public Socket createSocket() throws IOException {
					return ctx.getSocketFactory().createSocket();
				}
			};
			ssf.setHostnameVerifier(SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);
			SchemeRegistry registry = new SchemeRegistry();
			registry.register(new Scheme("https", ssf, 443));
			registry.register(new Scheme("http", PlainSocketFactory.getSocketFactory(), 80));

			HttpParams httpParams = new BasicHttpParams();
			// 设置一些基本参数
			HttpProtocolParams.setVersion(httpParams, HttpVersion.HTTP_1_1);
			HttpProtocolParams.setContentCharset(httpParams, CHARSET);
			HttpProtocolParams.setUseExpectContinue(httpParams, true);

			ThreadSafeClientConnManager mgr = new ThreadSafeClientConnManager(httpParams, registry);
			httpClient = new DefaultHttpClient(mgr, httpParams);

			return httpClient;
		} catch (Exception ex) {
			ex.printStackTrace();
			return null;
		}
	}
	
}
