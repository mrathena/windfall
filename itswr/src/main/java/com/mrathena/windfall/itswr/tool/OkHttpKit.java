package com.mrathena.windfall.itswr.tool;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

import lombok.ToString;
import okhttp3.Callback;
import okhttp3.Cookie;
import okhttp3.CookieJar;
import okhttp3.FormBody;
import okhttp3.HttpUrl;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

@ToString
public class OkHttpKit {

	// 默认超时时间
	private static final int CONNECT_TIMEOUT = 1000 * 60;// 默认毫秒,获取连接用
	private static final int READ_TIMEOUT = 1000 * 60;// 默认毫秒,下载文件用
	private static final int WRITE_TIMEOUT = 10000;// 默认毫秒,上传文件用

	// 请求方式
	private static final String GET = "GET";
	private static final String POST = "POST";

	// 媒体类型
	private static final MediaType MEDIA_TYPE_JSON = MediaType.parse("application/json; charset=utf-8");
	private static final MediaType MEDIA_TYPE_FILE = MediaType.parse("application/octet-stream");

	private volatile static OkHttpClient instance = null;

	// OkHttpRequest参数
	private int connectTimeout = CONNECT_TIMEOUT;// 默认毫秒,获取连接用
	private int readTimeout = READ_TIMEOUT;// 默认毫秒,下载文件用
	private int writeTimeout = WRITE_TIMEOUT;// 默认毫秒,上传文件用

	// Request参数
	private String url;
	private String method;
	private Map<String, String> headers;// request
	private Map<String, Object> parameters;// get/post
	// map key-file 可多个entity
	private Map<String, File> fileMap;// 一个key对应一个file / post
	// map key-List<file> 只一个entity
	private String fileListKey;// fileList的key / post
	private List<File> fileList;// key为fileKey / post
	// map key-List<file> 可多个entity
	private Map<String, List<File>> fileListMap;// 多组key.List<File> / post
	// json
	private String json;

	/**
	 * 单例OkHttpClient
	 */
	private static OkHttpClient getOkHttpClient() {
		if (instance == null) {
			synchronized (OkHttpClient.class) {
				if (instance == null) {
					OkHttpClient.Builder clientBuilder = new OkHttpClient.Builder();
					clientBuilder.connectTimeout(CONNECT_TIMEOUT, TimeUnit.MILLISECONDS);
					clientBuilder.readTimeout(READ_TIMEOUT, TimeUnit.MILLISECONDS);
					clientBuilder.writeTimeout(WRITE_TIMEOUT, TimeUnit.MILLISECONDS);
					// Cookie自动管理
					clientBuilder.cookieJar(new CookieJar() {
						private final Map<String, Map<String, Cookie>> cookies = new ConcurrentHashMap<>();

						@Override
						public void saveFromResponse(HttpUrl url, List<Cookie> cookies) {
							List<Cookie> tempCookieList = new ArrayList<>(cookies);
							// List转Map
							Map<String, Cookie> tempCookieMap = new HashMap<>();
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
					//					clientBuilder.proxy(new Proxy(Proxy.Type.HTTP, new InetSocketAddress("localhost", 1080)));
					instance = clientBuilder.build();
				}
			}
		}
		return instance;
	}

	private OkHttpKit(String url, String method) {
		this.url = url;
		this.method = method;
	}

	private static void checkUrl(String url) {
		if (url == null || url.isEmpty()) {
			throw new IllegalArgumentException("Url can't be none");
		}
	}

	public static OkHttpKit get(String url) {
		checkUrl(url);
		return new OkHttpKit(url, GET);
	}

	public static OkHttpKit post(String url) {
		checkUrl(url);
		return new OkHttpKit(url, POST);
	}

	public OkHttpKit connect(int timeout) {
		this.connectTimeout = timeout;
		return this;
	}

	public OkHttpKit read(int timeout) {
		this.readTimeout = timeout;
		return this;
	}

	public OkHttpKit write(int timeout) {
		this.writeTimeout = timeout;
		return this;
	}

	public OkHttpKit header(String key, String value) {
		addHeader(key, value);
		return this;
	}

	public OkHttpKit cookie(String value) {
		addHeader("cookie", value);
		return this;
	}

	public OkHttpKit userAgent(String userAgent) {
		addHeader("User-Agent", userAgent);
		return this;
	}

	public OkHttpKit headers(Map<String, String> headers) {
		if (headers == null || headers.isEmpty()) {
			throw new IllegalArgumentException("Headers can't be none");
		}
		headers.forEach((key, value) -> {
			addHeader(key, value);
		});
		return this;
	}

	public OkHttpKit parameter(String key, Object value) {
		addParameter(key, value);
		return this;
	}

	public OkHttpKit parameters(Map<String, Object> parameters) {
		if (parameters == null || parameters.isEmpty()) {
			throw new IllegalArgumentException("Parameters can't be none");
		}
		parameters.forEach((key, value) -> {
			addParameter(key, value);
		});
		return this;
	}

	// json
	public OkHttpKit json(String jsonStr) {
		checkPost();
		checkJsonAddOperation();
		if (jsonStr == null || jsonStr.isEmpty()) {
			throw new IllegalArgumentException("Json can't be none");
		}
		this.json = jsonStr;
		return this;
	}

	// 多key单文件(每个key对应一个文件)
	public OkHttpKit file(String key, File file) {
		addFile(key, file);
		return this;
	}

	// 多key单文件(每个key对应一个文件)
	public OkHttpKit file(File file) {
		addFile("file", file);
		return this;
	}

	// 多key单文件(每个key对应一个文件)
	public OkHttpKit fileMap(Map<String, File> files) {
		checkPost();
		checkFileMapAddOperation();
		if (files == null || files.isEmpty()) {
			throw new IllegalArgumentException("Files can't be none");
		}
		files.forEach((key, file) -> {
			addFile(key, file);
		});
		return this;
	}

	// 单key多文件
	public OkHttpKit fileList(String key, List<File> files) {
		checkPost();
		checkFileListAddOperation();
		if (key == null || key.isEmpty()) {
			throw new IllegalArgumentException("Files.key can't be none");
		}
		if (files == null || files.isEmpty()) {
			throw new IllegalArgumentException("Files can't be none");
		}
		files.forEach(file -> {
			checkFile(file);
		});
		this.fileListKey = key;
		this.fileList.addAll(files);
		return this;
	}

	// 单key(files)多文件
	public OkHttpKit fileList(List<File> files) {
		fileList("files", files);
		return this;
	}

	// 多key多文件(每个key对应多个文件)
	public OkHttpKit fileListMap(Map<String, List<File>> files) {
		checkPost();
		checkFileListMapAddOperation();
		if (files == null || files.isEmpty()) {
			throw new IllegalArgumentException("Files can't be none");
		}
		files.forEach((key, fileList) -> {
			if (fileList == null || fileList.isEmpty()) {
				throw new IllegalArgumentException("Files can't be none");
			}
		});
		if (this.fileListMap == null) {
			this.fileListMap = new LinkedHashMap<>();
		}
		files.forEach((key, fileList) -> {
			fileList.forEach(file -> {
				checkFile(file);
			});
			this.fileListMap.put(key, fileList);
		});
		return this;
	}

	/**
	 * 同步请求(使用新的OkHttpClient)
	 */
	public String execute() throws IOException {
		return execute(false);
	}

	/**
	 * 同步请求(使用单例OkHttpClient, 有Cookies自动管理功能)
	 */
	public String execute2() throws IOException {
		return execute(true);
	}

	/**
	 * 异步请求(使用新的OkHttpClient)
	 */
	public void enqueue(Callback callback) {
		enqueue(false, callback);
	}

	/**
	 * 异步请求(使用单例OkHttpClient, 有Cookies自动管理功能)
	 */
	public void enqueue2(Callback callback) {
		enqueue(true, callback);
	}

	private void addHeader(String key, String value) {
		if (key == null || key.isEmpty()) {
			throw new IllegalArgumentException("Header.key can't be none");
		}
		if (value == null || value.isEmpty()) {
			throw new IllegalArgumentException("Header.value can't be none");
		}
		if (this.headers == null) {
			this.headers = new LinkedHashMap<>();
		}
		this.headers.put(key, value);
	}

	private void addParameter(String key, Object value) {
		if (this.json != null) {
			throw new UnsupportedOperationException("Unsupport add parameter when you had choosed json request body");
		}
		if (key == null || key.isEmpty()) {
			throw new IllegalArgumentException("Parameter.key can't be none");
		}
		if (value == null) {
			throw new IllegalArgumentException("Parameter.value can't be null");
		}
		if (this.parameters == null) {
			this.parameters = new LinkedHashMap<>();
		}
		this.parameters.put(key, value);
	}

	private void addFile(String key, File file) {
		checkPost();
		checkFileMapAddOperation();
		if (key == null || key.isEmpty()) {
			throw new IllegalArgumentException("File.key can't be none");
		}
		checkFile(file);
		if (this.fileMap == null) {
			this.fileMap = new LinkedHashMap<>();
		}
		this.fileMap.put(key, file);
	}

	private void checkPost() {
		if (!POST.equals(this.method)) {
			throw new UnsupportedOperationException("Only support POST request");
		}
	}

	private void checkFile(File file) {
		if (file == null) {
			throw new IllegalArgumentException("File can't be null");
		}
		if (file.isDirectory()) {
			throw new IllegalArgumentException("File can't be directory");
		}
		if (!file.exists()) {
			throw new IllegalArgumentException("File must be exist");
		}
	}

	private void checkFileMapAddOperation() {
		if (this.fileList != null || this.fileListMap != null || this.json != null) {
			throw new UnsupportedOperationException(
					"Unsupport current operation when you had choosed Other request body");
		}
	}

	private void checkFileListAddOperation() {
		if (this.fileMap != null || this.fileListMap != null || this.json != null) {
			throw new UnsupportedOperationException(
					"Unsupport current operation when you had choosed Other request body");
		}
	}

	private void checkFileListMapAddOperation() {
		if (this.fileMap != null || this.fileList != null || this.json != null) {
			throw new UnsupportedOperationException(
					"Unsupport current operation when you had choosed Other request body");
		}
	}

	private void checkJsonAddOperation() {
		if (this.fileMap != null || this.fileList != null || this.fileListMap != null) {
			throw new UnsupportedOperationException("Unsupport add json when you had choosed Other request body");
		}
		if (this.parameters != null) {
			throw new UnsupportedOperationException("Unsupport add json when you had set parameters");
		}
	}

	private boolean needMultipartBody() {
		return this.fileList != null || this.fileMap != null || this.fileListMap != null;
	}

	/**
	 * 根据现有数据生成一个新的OkHttpClient
	 */
	private OkHttpClient newOkHttpClient() {
		OkHttpClient.Builder clientBuilder = new OkHttpClient.Builder();
		clientBuilder.connectTimeout(this.connectTimeout, TimeUnit.MILLISECONDS);
		clientBuilder.readTimeout(this.readTimeout, TimeUnit.MILLISECONDS);
		clientBuilder.writeTimeout(this.writeTimeout, TimeUnit.MILLISECONDS);
		return clientBuilder.build();
	}

	/**
	 * 根据现有数据生成一个Request
	 */
	private Request getRequest() {
		// RequestBody
		RequestBody body = null;
		if (GET.equals(this.method)) {
			// GET(GET请求没有RequestBody)
			if (this.parameters != null) {
				// 有参数
				StringBuilder sb = new StringBuilder();
				this.parameters.forEach((k, v) -> sb.append("&").append(k).append("=").append(v.toString()));
				if (!this.url.contains("?")) {
					// url不包含?
					sb.deleteCharAt(0).insert(0, "?");
				}
				this.url += sb.toString();
			}
		} else {
			// POST
			// RequestBody
			if (needMultipartBody()) {
				// 需要MultipartBody
				MultipartBody.Builder multipartBodyBuilder = new MultipartBody.Builder().setType(MultipartBody.FORM);
				if (this.parameters != null) {
					this.parameters.forEach((key, value) -> {
						multipartBodyBuilder.addFormDataPart(key, value.toString());
					});
				}
				if (this.fileList != null) {
					this.fileList.forEach(file -> {
						RequestBody fileBody = RequestBody.create(MEDIA_TYPE_FILE, file);
						multipartBodyBuilder.addFormDataPart(this.fileListKey, file.getName(), fileBody);
					});
				}
				if (this.fileMap != null) {
					this.fileMap.forEach((key, file) -> {
						RequestBody fileBody = RequestBody.create(MEDIA_TYPE_FILE, file);
						multipartBodyBuilder.addFormDataPart(key, file.getName(), fileBody);
					});
				}
				if (this.fileListMap != null) {
					this.fileListMap.forEach((key, fileList) -> {
						fileList.forEach(file -> {
							RequestBody fileBody = RequestBody.create(MEDIA_TYPE_FILE, file);
							multipartBodyBuilder.addFormDataPart(key, file.getName(), fileBody);
						});
					});
				}
				body = multipartBodyBuilder.build();
			} else if (this.json != null) {
				body = FormBody.create(MEDIA_TYPE_JSON, this.json);
			} else {
				// 不需要MultipartBody
				FormBody.Builder formBodyBuilder = new FormBody.Builder();
				if (this.parameters != null) {
					// 有参数
					this.parameters.forEach((key, value) -> {
						formBodyBuilder.add(key, value.toString());
					});
				}
				body = formBodyBuilder.build();
			}
		}
		// Request
		Request.Builder requestBuilder = new Request.Builder().url(this.url);
		if (POST.equals(this.method)) {
			requestBuilder.post(body);
		}
		if (this.headers != null) {
			// 有headers
			this.headers.forEach((key, value) -> {
				requestBuilder.addHeader(key, value);
			});
		}
		Request request = requestBuilder.build();
		return request;
	}

	/**
	 * 同步执行请求
	 */
	private String execute(boolean cache) throws IOException {
		String responseStr = null;
		try {
			// OkHttpClient
			OkHttpClient client = cache ? getOkHttpClient() : newOkHttpClient();
			// Request
			Request request = getRequest();
			// Response
			Response response = client.newCall(request).execute();
			if (response.isSuccessful()) {
				responseStr = response.body().string();
			} else {
				throw new RuntimeException(response.protocol() + " - " + response.code() + " - " + response.message());
			}
		} catch (IOException e) {
			throw e;
		}
		return responseStr;
	}

	/**
	 * 异步执行请求
	 */
	private void enqueue(boolean cache, Callback callback) {
		// OkHttpClient
		OkHttpClient client = cache ? getOkHttpClient() : newOkHttpClient();
		// Request
		Request request = getRequest();
		// Response
		client.newCall(request).enqueue(callback);
	}

}
