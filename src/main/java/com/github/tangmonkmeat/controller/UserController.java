package com.github.tangmonkmeat.controller;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.github.tangmonkmeat.common.ResponseEnum;
import com.github.tangmonkmeat.common.ResponseInfo;
import com.github.tangmonkmeat.service.UserService;
import com.github.tangmonkmeat.util.IpUtil;

@Controller
@RequestMapping("/user")
public class UserController {
	
	private Logger logger = LoggerFactory.getLogger(this.getClass());
	
	@Autowired
	private UserService userService;
	
	@ResponseBody
	@PostMapping("/login")
	public ResponseInfo<Object> checkLogin(@RequestParam("account") String account,
							@RequestParam("password") String password,
							HttpServletRequest request) {
		String ip = IpUtil.getIp(request);
		logger.info("user login，[account: {}, password: {}, IP: {}]",account,password,ip);
		if(account.length() != 8 || password.length() == 0) {
			logger.warn("[account: {}, password: {}] 用户名或者密码不符合规格",account,password);
			return ResponseInfo.failure(ResponseEnum.PARAM_TYPE_BIND_ERROR);
		}
		ResponseInfo<Object> result = userService.loginCheck(account, password,ip);
		if(result.getCode() != ResponseEnum.SUCCESS.code()) {
			logger.error(result.getMsg() + "，[account: {}, password: {}, IP: {}]",account,password,ip);
		}
		return result;
	}

}
