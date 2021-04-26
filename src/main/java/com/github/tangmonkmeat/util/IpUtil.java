package com.github.tangmonkmeat.util;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class IpUtil {
	
	private final static Logger logger = LoggerFactory.getLogger(IpUtil.class);
	
	/**
	 * 获取用户的IP地址
	 * 
	 * @param request {@link HttpServletRequest}}
	 * @return 如果IP存在返回IP，否则返回 ""
	 */
	public static String getIp(HttpServletRequest request) {
		  String ipAddress = null;
	        try {
	            //X-Forwarded-For：Squid 服务代理
	            String ipAddresses = request.getHeader("X-Forwarded-For");

	            if (ipAddresses == null || ipAddresses.length() == 0 || "unknown".equalsIgnoreCase(ipAddresses)) {
	                //Proxy-Client-IP：apache 服务代理
	                ipAddresses = request.getHeader("Proxy-Client-IP");
	            }

	            if (ipAddresses == null || ipAddresses.length() == 0 || "unknown".equalsIgnoreCase(ipAddresses)) {
	                //WL-Proxy-Client-IP：weblogic 服务代理
	                ipAddresses = request.getHeader("WL-Proxy-Client-IP");
	            }

	            if (ipAddresses == null || ipAddresses.length() == 0 || "unknown".equalsIgnoreCase(ipAddresses)) {
	                //HTTP_CLIENT_IP：有些代理服务器
	                ipAddresses = request.getHeader("HTTP_CLIENT_IP");
	            }

	            if (ipAddresses == null || ipAddresses.length() == 0 || "unknown".equalsIgnoreCase(ipAddresses)) {
	                //X-Real-IP：nginx服务代理
	                ipAddresses = request.getHeader("X-Real-IP");
	            }

	            //有些网络通过多层代理，那么获取到的ip就会有多个，一般都是通过逗号（,）分割开来，并且第一个ip为客户端的真实IP
	            if (ipAddresses != null && ipAddresses.length() != 0) {
	                ipAddress = ipAddresses.split(",")[0];
	            }

	            //还是不能获取到，最后再通过request.getRemoteAddr();获取
	            if (ipAddress == null || ipAddress.length() == 0 || "unknown".equalsIgnoreCase(ipAddresses)) {
	                ipAddress = request.getRemoteAddr();
	            }
	        } catch (Exception e) {
	            ipAddress = "";
	            logger.warn(request.getParameter("account") + "的IP异常");
	        }
	        return ipAddress;
	}

}
