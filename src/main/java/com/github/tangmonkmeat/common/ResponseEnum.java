package com.github.tangmonkmeat.common;

public enum ResponseEnum {
	/* 成功状态码 */
    SUCCESS(200, "success"),

    /* 参数错误：10001-19999 */
    PARAM_TYPE_BIND_ERROR(10003, "学号或者密码格式错误！"),

    /* 用户错误：20001-29999*/
    USER_LOGIN_ERROR(20002, "学号或者密码错误！"),
    USER_HAS_EXISTED(20005, "无需重复登记！"),

    /* 服务器内部错误 */
    INTERNAL_SERVER_ERROR( 500, "Internal Server Error");

    private final int code;
    
    private final String msg;

    public int code() {
        return code;
    }

    public String msg() {
        return msg;
    }

    private ResponseEnum(int code, String msg) {
        this.code = code;
        this.msg = msg;
    }
}
