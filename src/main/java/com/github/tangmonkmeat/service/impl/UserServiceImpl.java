package com.github.tangmonkmeat.service.impl;

import org.springframework.stereotype.Service;

import com.github.tangmonkmeat.common.ResponseEnum;
import com.github.tangmonkmeat.common.ResponseInfo;
import com.github.tangmonkmeat.config.UserInfoConfig;
import com.github.tangmonkmeat.model.Student;
import com.github.tangmonkmeat.request.JxauRequest;
import com.github.tangmonkmeat.service.UserService;


@Service
public class UserServiceImpl implements UserService{
	
	@Override
	public ResponseInfo<Object> loginCheck(String account, String password,String ip) {
		UserInfoConfig config = UserInfoConfig.instance();
		
		// 如果用户已经登记
		if(config.isExist(account)) {
			return ResponseInfo.failure(ResponseEnum.USER_HAS_EXISTED);
		}
		
		// 获取 guid
		String guid = JxauRequest.getGuid(account, password, null);
		if(guid == null) {
			return ResponseInfo.failure(ResponseEnum.USER_LOGIN_ERROR);
		}
		
		// 登录成功，缓存记录
		ResponseEnum status = ResponseEnum.SUCCESS;
		// 缓存账户
		Student stu = new Student(guid,ip,account,password,null,null);
		String usersJson = config.addUserThenGetJson(stu);
		try {
			// 更新配置
			config.update(usersJson);
		} catch (Exception e) {
			status = ResponseEnum.INTERNAL_SERVER_ERROR;
		}
		return ResponseInfo.success(status, null);
	}

}
