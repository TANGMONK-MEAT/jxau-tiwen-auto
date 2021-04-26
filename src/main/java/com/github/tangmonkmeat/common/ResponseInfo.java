package com.github.tangmonkmeat.common;

public class ResponseInfo<T> {
	  /**
     * 状态码
     */
    private int code;

    /**
     * 描述
     */
    private String msg;

    /**
     * 具体数据
     */
    private T data;

    public ResponseInfo(int code, String msg, T data) {
        this.code = code;
        this.msg = msg;
        this.data = data;
    }

    public ResponseInfo(ResponseEnum status, T data) {
        this.code = status.code();
        this.msg = status.msg();
        this.data = data;
    }

    @Override
    public String toString() {
        return "Result{" +
                "code=" + code +
                ", msg='" + msg + '\'' +
                ", data=" + data +
                '}';
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    /**
     * 业务成功返回业务代码和描述信息
     */
    public static ResponseInfo<Object> success() {
        return new ResponseInfo<Object>(ResponseEnum.SUCCESS, null);
    }

    /**
     * 业务成功返回业务代码,描述和返回的参数
     */
    public static <T> ResponseInfo<T> success(T data) {
        return new ResponseInfo<T>(ResponseEnum.SUCCESS, data);
    }

    /**
     * 业务成功返回业务代码,描述和返回的参数
     */
    public static <T> ResponseInfo<T> success(ResponseEnum status, T data) {
        if (status == null) {
            return success(data);
        }
        return new ResponseInfo<T>(status, data);
    }

    /**
     * 业务异常返回业务代码和描述信息
     */
    public static <T> ResponseInfo<T> failure() {
        return new ResponseInfo<T>(ResponseEnum.INTERNAL_SERVER_ERROR, null);
    }

    /**
     * 业务异常返回业务代码,描述和返回的参数
     */
    public static <T> ResponseInfo<T> failure(ResponseEnum status) {
        return failure(status, null);
    }

    /**
     * 业务异常返回业务代码,描述和返回的参数
     */
    public static <T> ResponseInfo<T> failure(ResponseEnum status, T data) {
        if (status == null) {
            return new ResponseInfo<T>(ResponseEnum.INTERNAL_SERVER_ERROR, null);
        }
        return new ResponseInfo<T>(status, data);
    }
}
