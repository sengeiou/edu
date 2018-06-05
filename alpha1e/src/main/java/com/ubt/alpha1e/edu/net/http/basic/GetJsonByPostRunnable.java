package com.ubt.alpha1e.edu.net.http.basic;

import com.ubt.alpha1e.edu.utils.connect.ConnectClientUtil;
import com.ubt.alpha1e.edu.utils.log.MyLog;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

public class GetJsonByPostRunnable implements BaseWebRunnable, Runnable {

	public long mRequestCode;
	public String mUrl;
	public String mParams;
	public IJsonListener mListener;

	public GetJsonByPostRunnable(long _requestCode, String _url,
			String _params, IJsonListener _listener) {
		mRequestCode = _requestCode;
		mUrl = _url;
		mParams = _params;
		mListener = _listener;
	}

	@Override
	public void run() {
		try {
			MyLog.writeLog("网络功能", "请求URL：" + mUrl);
			HttpURLConnection conn = getHttpURLConnectionByUrl(mUrl);
			conn.setConnectTimeout(5000);
			conn.setReadTimeout(10*1000);
			conn.setRequestProperty("Content-Type",
					"application/json;charset=UTF-8");
			conn.setRequestProperty("accept", "*/*");
			conn.setRequestProperty("connection", "Keep-Alive");
			conn.setRequestProperty("user-agent",
					"Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1;SV1)");
			conn.setDoOutput(true);
			conn.setDoInput(true);
			PrintWriter out = new PrintWriter(new OutputStreamWriter(
					conn.getOutputStream(), "UTF-8"));
			out.print(mParams);
			out.flush();

			String result = "";
			BufferedReader in = new BufferedReader(new InputStreamReader(
					conn.getInputStream(), "UTF-8"));
			String line;
			while ((line = in.readLine()) != null) {
				result += line;
			}

			//MyLog.writeLog("网络功能", "收到回复：" + result);
			if (mListener != null)
				mListener.onGetJson(true, result, mRequestCode);

			out.close();
			in.close();

			return;

		} catch (Exception e) {
			MyLog.writeLog("网络功能", "数据请求失败：" + e.getMessage());
			if (mListener != null)
				mListener.onGetJson(false, e.getMessage(), mRequestCode);
			return;
		}
	}

	public static HttpURLConnection getHttpURLConnectionByUrl(String urlStr){
		HttpURLConnection urlConnection = null;
		try {
			URL url = new URL(urlStr);
			if(urlStr.startsWith("https") && urlStr.contains(HttpAddress.WebAddressDevelop)){
				urlConnection = (HttpsURLConnection) url.openConnection();
				((HttpsURLConnection) urlConnection).setSSLSocketFactory(ConnectClientUtil.getSocketFactory());
			}else {
				urlConnection = (HttpURLConnection) url.openConnection();
			}
		}catch (MalformedURLException e) {
			e.printStackTrace();
		}catch (IOException e) {
			e.printStackTrace();
		}
		return urlConnection;
	}

	@Override
	public void disableTask() {
		mRequestCode = -1111;
	}

}
