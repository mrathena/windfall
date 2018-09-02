package com.mrathena.windfall.itswr.tool;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

import okhttp3.Cookie;
import okhttp3.CookieJar;
import okhttp3.FormBody;
import okhttp3.HttpUrl;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class Http {

	private static final int CONNECT_TIMEOUT = 1000 * 60;// 默认毫秒,获取连接用
	private static final int READ_TIMEOUT = 1000 * 60;// 默认毫秒,下载文件用
	private static final int WRITE_TIMEOUT = 1000 * 10;// 默认毫秒,上传文件用

	public static Http getInstance() {
		return new Http(getOkHttpClient());
	}

	// OkHttpClient
	private OkHttpClient client;

	private Http(OkHttpClient client) {
		this.client = client;
	}

	private static OkHttpClient getOkHttpClient() {

		OkHttpClient.Builder clientBuilder = new OkHttpClient.Builder();
		clientBuilder.connectTimeout(CONNECT_TIMEOUT, TimeUnit.MILLISECONDS);
		clientBuilder.readTimeout(READ_TIMEOUT, TimeUnit.MILLISECONDS);
		clientBuilder.writeTimeout(WRITE_TIMEOUT, TimeUnit.MILLISECONDS);
		clientBuilder.cookieJar(new CookieJar() {
			private final Map<String, Map<String, Cookie>> cookies = new ConcurrentHashMap<>();

			@Override
			public void saveFromResponse(HttpUrl url, List<Cookie> cookies) {
				List<Cookie> tempCookieList = new ArrayList<>(cookies);
				// List转Map
				Map<String, Cookie> tempCookieMap = new LinkedHashMap<>();
				tempCookieList.forEach(cookie -> {
					tempCookieMap.put(cookie.name(), cookie);
				});
				// 获取domain下的cookie
				Map<String, Cookie> domainCookieMap = this.cookies.get(url.host());
				if (domainCookieMap == null) {
					this.cookies.put(url.host(), tempCookieMap);
				} else {
					tempCookieMap.forEach((name, cookie) -> {
						domainCookieMap.put(name, cookie);
					});
				}
			}

			@Override
			public List<Cookie> loadForRequest(HttpUrl url) {
				Map<String, Cookie> domainCookieMap = this.cookies.get(url.host());
				List<Cookie> cookieList = new ArrayList<>();
				if (domainCookieMap != null && !domainCookieMap.isEmpty()) {
					domainCookieMap.forEach((name, cookie) -> {
						cookieList.add(cookie);
					});
				}
				return cookieList;
			}
		});
		OkHttpClient client = clientBuilder.build();

		return client;
	}

	public String get(String url, Map<String, String> headers, Map<String, Object> parameters) throws Exception {

		Request.Builder requestBuilder = new Request.Builder().url(url);
		if (headers != null && !headers.isEmpty()) {
			headers.forEach((key, value) -> {
				requestBuilder.addHeader(key, value);
			});
		}
		Request request = requestBuilder.build();

		if (parameters != null && !parameters.isEmpty()) {
			StringBuilder sb = new StringBuilder();
			parameters.forEach((k, v) -> sb.append("&").append(k).append("=").append(v.toString()));
			if (!url.contains("?")) {
				// url不包含?
				sb.deleteCharAt(0).insert(0, "?");
			}
			url += sb.toString();
		}

		Response response = client.newCall(request).execute();
		if (response.isSuccessful()) {
			return response.body().string();
		} else {
			throw new RuntimeException(response.protocol() + " - " + response.code() + " - " + response.message());
		}
	}

	public String post(String url, Map<String, String> headers, Map<String, Object> parameters) throws Exception {

		FormBody.Builder formBodyBuilder = new FormBody.Builder();
		if (parameters != null) {
			parameters.forEach((key, value) -> {
				formBodyBuilder.add(key, value.toString());
			});
		}
		RequestBody body = formBodyBuilder.build();

		Request.Builder requestBuilder = new Request.Builder().url(url).post(body);
		if (headers != null && !headers.isEmpty()) {
			headers.forEach((key, value) -> {
				requestBuilder.addHeader(key, value);
			});
		}
		Request request = requestBuilder.build();

		Response response = client.newCall(request).execute();
		if (response.isSuccessful()) {
			return response.body().string();
		} else {
			throw new RuntimeException(response.protocol() + " - " + response.code() + " - " + response.message());
		}

	}

	public String post(String url, Map<String, String> headers, String json) throws Exception {

		RequestBody body = FormBody.create(MediaType.parse("application/json; charset=utf-8"), json);

		Request.Builder requestBuilder = new Request.Builder().url(url).post(body);
		if (headers != null && !headers.isEmpty()) {
			headers.forEach((key, value) -> {
				requestBuilder.addHeader(key, value);
			});
		}
		Request request = requestBuilder.build();

		Response response = client.newCall(request).execute();
		if (response.isSuccessful()) {
			return response.body().string();
		} else {
			throw new RuntimeException(response.protocol() + " - " + response.code() + " - " + response.message());
		}

	}

}
