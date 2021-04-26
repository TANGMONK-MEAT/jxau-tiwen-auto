package com.github.tangmonkmeat.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.SocketTimeoutException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.http.HttpEntity;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ConnectTimeoutException;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Http请求
 * 
 * @author zwl
 *
 */
public class HttpUtil {

	private static Logger logger = LoggerFactory.getLogger(HttpUtil.class);

	/**
	 * CloseableHttpClient
	 * 
	 * @return
	 */
	public static CloseableHttpClient createDefault() {
		RequestConfig config = RequestConfig.custom().setConnectTimeout(15000)
				.setConnectionRequestTimeout(10000).setSocketTimeout(10000)
				.build();
		return HttpClientBuilder.create().setDefaultRequestConfig(config)
				.build();
	}

	/**
	 * get请求
	 * 
	 * @param url
	 * @param headers
	 * @return
	 */
	public static String get(String url, Map<String, Object> headers) {
		CloseableHttpResponse response = null;
		HttpGet request = null;
		CloseableHttpClient client = null;
		try {
			client = HttpUtil.createDefault();
			request = new HttpGet(url);
			if (headers != null) {
				for (Iterator<String> iter = headers.keySet().iterator(); iter.hasNext();) {
					String key = (String) iter.next();
					String value = String.valueOf(headers.get(key));
					request.addHeader(key, value);
				}
			}
			response = client.execute(request);
			int code = response.getStatusLine().getStatusCode();
			logger.info("请求URL：" + url + ";code：" + code);
			if (code == HttpStatus.SC_OK) {
				String result = EntityUtils.toString(response.getEntity());
				return result;
			}
		} catch(ConnectTimeoutException e) {
			logger.error("请求连接超时，由于 " + e.getLocalizedMessage());
		} catch(SocketTimeoutException e) {
			logger.error("请求通信超时，由于 " + e.getLocalizedMessage());
		} catch(ClientProtocolException e) {
			logger.error("协议错误（比如构造HttpGet对象时传入协议不对(将'http'写成'htp')or响应内容不符合），由于 " + e.getLocalizedMessage());
		} catch(IOException e) {
			logger.error("网络异常， 由于 " + e.getLocalizedMessage());
		} finally {
			try {
				if(response!=null) {
					response.close();
				}
			} catch(IOException e) {
				logger.error("响应关闭异常， 由于 " + e.getLocalizedMessage());
			}
			if(request!=null) {
				request.releaseConnection();
			} 
			if (client != null) {
				try {
					client.close();
				} catch (IOException e) {
					logger.error("HttpClient 关闭失败，由于 " + e.getLocalizedMessage());
				}
			}
		}
		return null;
	}
	
	/**
	 * get请求
	 * 
	 * @param url
	 * @param headers
	 * @return
	 */
	public static String get2(String url, String authorization) {
		CloseableHttpResponse response = null;
		HttpGet request = null;
		CloseableHttpClient client = null;
		try {
			client = HttpUtil.createDefault();
			request = new HttpGet(url);
			request.addHeader("Authorization", authorization);
			response = client.execute(request);
			int code = response.getStatusLine().getStatusCode();
			logger.info("请求URL：" + url + ";code：" + code);
			if (code == HttpStatus.SC_OK) {
				String result = EntityUtils.toString(response.getEntity());
				return result;
			}
		} catch(ConnectTimeoutException e) {
			logger.error("请求连接超时，由于 " + e.getLocalizedMessage());
		} catch(SocketTimeoutException e) {
			logger.error("请求通信超时，由于 " + e.getLocalizedMessage());
		} catch(ClientProtocolException e) {
			logger.error("协议错误（比如构造HttpGet对象时传入协议不对(将'http'写成'htp')or响应内容不符合），由于 " + e.getLocalizedMessage());
		} catch(IOException e) {
			logger.error("实体转换异常或者网络异常， 由于 " + e.getLocalizedMessage());
		} finally {
			try {
				if(response!=null) {
					response.close();
				}
			} catch(IOException e) {
				logger.error("响应关闭异常， 由于 " + e.getLocalizedMessage());
			}
			if(request!=null) {
				request.releaseConnection();
			} 
			if (client != null) {
				try {
					client.close();
				} catch (IOException e) {
					logger.error("HttpClient 关闭失败，由于 " + e.getLocalizedMessage());
				}
			}
		}
		return null;
	}

	/**
	 * key-value格式的参数
	 * 
	 * @param url
	 * @param params
	 * @return
	 */
	public static String post(String url, Map<String, Object> params) {
		BufferedReader in = null;
		CloseableHttpResponse response = null;
		HttpPost request =  null;
		CloseableHttpClient client = null;
		try {
			client = HttpUtil.createDefault();
			request = new HttpPost(url);
			List<NameValuePair> nvps = new ArrayList<NameValuePair>();
			for (Iterator<String> iter = params.keySet().iterator(); iter.hasNext();) {
				String key = (String) iter.next();
				String value = String.valueOf(params.get(key));
				nvps.add(new BasicNameValuePair(key, value));
			}
			request.setEntity(new UrlEncodedFormEntity(nvps, StandardCharsets.UTF_8));
			response = (CloseableHttpResponse) client.execute(request);
			int code = response.getStatusLine().getStatusCode();
			logger.info("请求URL：" + url + ";code：" + code);
			if (code == HttpStatus.SC_OK) {
				in = new BufferedReader(
						new InputStreamReader(response.getEntity().getContent(), StandardCharsets.UTF_8));
				StringBuffer sb = new StringBuffer("");
				String line = "";
				String NL = System.getProperty("line.separator");
				while ((line = in.readLine()) != null) {
					sb.append(line + NL);
				}
				return sb.toString();
			}
		} catch(ConnectTimeoutException e) {
			logger.error("请求连接超时，由于 " + e.getLocalizedMessage());
		} catch(SocketTimeoutException e) {
			logger.error("请求通信超时，由于 " + e.getLocalizedMessage());
		} catch(ClientProtocolException e) {
			logger.error("协议错误（比如构造HttpGet对象时传入协议不对(将'http'写成'htp')or响应内容不符合），由于 " + e.getLocalizedMessage());
		} catch(IOException e) {
			logger.error("实体转换异常或者网络异常， 由于 " + e.getLocalizedMessage());
		} finally{
			try {
				if(response!=null) {
					response.close();
				}
			} catch(IOException e) {
				logger.error("响应关闭异常， 由于 " + e.getLocalizedMessage());
			}
			if(request!=null) {
				request.releaseConnection();
			} 
			if (in != null) {
				try {
					in.close();
				} catch (IOException e) {
					logger.error("响应关闭失败，由于 " + e.getLocalizedMessage());
				}
			}
			if (client != null) {
				try {
					client.close();
				} catch (IOException e) {
					logger.error("HttpClient 关闭失败，由于 " + e.getLocalizedMessage());
				}
			}
		}
		return null;
	}
	
	
	

	/**
	 * 请求json格式的参数
	 * 
	 * @param url
	 * @param params
	 * @return
	 */
	public static String post(String url, String params, Map<String, Object> headers) {
		CloseableHttpClient httpclient = HttpClients.createDefault();
		HttpPost httpPost = new HttpPost(url);
		httpPost.setHeader("Accept", "application/json");
		httpPost.setHeader("Content-Type", "application/json");
		if (headers != null) {
			for (Iterator<String> iter = headers.keySet().iterator(); iter.hasNext();) {
				String key = (String) iter.next();
				String value = String.valueOf(headers.get(key));
				httpPost.addHeader(key, value);
			}
		}
		StringEntity entity = new StringEntity(params, StandardCharsets.UTF_8);
		httpPost.setEntity(entity);
		CloseableHttpResponse response = null;
		try {
			response = httpclient.execute(httpPost);
			int code = response.getStatusLine().getStatusCode();
			logger.info("请求URL：" + url + ";code：" + code);
			if (code == HttpStatus.SC_OK) {
				HttpEntity responseEntity = response.getEntity();
				String jsonString = EntityUtils.toString(responseEntity);
				return jsonString;
			}
			if (response != null) {
				response.close();
			}
			httpclient.close();
		} catch(ConnectTimeoutException e) {
			logger.error("请求连接超时，由于 " + e.getLocalizedMessage());
		} catch(SocketTimeoutException e) {
			logger.error("请求通信超时，由于 " + e.getLocalizedMessage());
		} catch(ClientProtocolException e) {
			logger.error("协议错误（比如构造HttpGet对象时传入协议不对(将'http'写成'htp')or响应内容不符合），由于 " + e.getLocalizedMessage());
		} catch(IOException e) {
			logger.error("实体转换异常或者网络异常， 由于 " + e.getLocalizedMessage());
		} finally{
			try {
				if(response!=null) {
					response.close();
				}
			} catch(IOException e) {
				logger.error("响应关闭异常， 由于 " + e.getLocalizedMessage());
			}
			if(httpPost!=null) {
				httpPost.releaseConnection();
			} 
			if (httpclient != null) {
				try {
					httpclient.close();
				} catch (IOException e) {
					logger.error("HttpClient 关闭失败，由于 " + e.getLocalizedMessage());
				}
			}
		}
		return null;
	}
	
	/**
	 * 请求json格式的参数
	 * 
	 * @param url
	 * @param authorization
	 * @return
	 */
	public static String post2(String url, String params, String authorization) {
		CloseableHttpClient httpclient = HttpClients.createDefault();
		HttpPost httpPost = new HttpPost(url);
		httpPost.setHeader("Accept", "application/json");
		httpPost.setHeader("Content-Type", "application/json");
		httpPost.addHeader("Authorization", authorization);
		StringEntity entity = new StringEntity(params, StandardCharsets.UTF_8);
		httpPost.setEntity(entity);
		CloseableHttpResponse response = null;
		try {
			response = httpclient.execute(httpPost);
			int code = response.getStatusLine().getStatusCode();
			logger.info("请求URL：" + url + ";code：" + code);
			if (code == HttpStatus.SC_OK) {
				HttpEntity responseEntity = response.getEntity();
				String jsonString = EntityUtils.toString(responseEntity);
				return jsonString;
			}
			if (response != null) {
				response.close();
			}
			httpclient.close();
		} catch(ConnectTimeoutException e) {
			logger.error("请求连接超时，由于 " + e.getLocalizedMessage());
		} catch(SocketTimeoutException e) {
			logger.error("请求通信超时，由于 " + e.getLocalizedMessage());
		} catch(ClientProtocolException e) {
			logger.error("协议错误（比如构造HttpGet对象时传入协议不对(将'http'写成'htp')or响应内容不符合），由于 " + e.getLocalizedMessage());
		} catch(IOException e) {
			logger.error("实体转换异常或者网络异常， 由于 " + e.getLocalizedMessage());
		} finally{
			try {
				if(response!=null) {
					response.close();
				}
			} catch(IOException e) {
				logger.error("响应关闭异常， 由于 " + e.getLocalizedMessage());
			}
			if(httpPost!=null) {
				httpPost.releaseConnection();
			} 
			if (httpclient != null) {
				try {
					httpclient.close();
				} catch (IOException e) {
					logger.error("HttpClient 关闭失败，由于 " + e.getLocalizedMessage());
				}
			}
		}
		return null;
	}
}
