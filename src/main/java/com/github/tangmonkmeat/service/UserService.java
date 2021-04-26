package com.github.tangmonkmeat.service;

import com.github.tangmonkmeat.common.ResponseInfo;

public interface UserService {

	ResponseInfo<Object> loginCheck(String account,String password,String ip);
	
}
