package com.zhangke.websocket.response;

import com.zhangke.websocket.request.Request;

/**
 * 出现错误时的响应，使用 {@link ResponseFactory#createErrorResponse()} 方法创建实例。
 * 使用完后请使用 {@link #release()} 方法释放对象。
 * 除了下面定义的三种错误类型外（对应的值为0、1/2），
 * 用户可自定义自己的错误码，例如 11：数据解析 JSON 错误。
 * </p>
 * Created by ZhangKe on 2018/6/25.
 */
public class ErrorResponse {

    /**
     * ws 未连接
     */
    public static final int ERROR_NO_CONNECT = 0;
    /**
     * 未知错误
     */
    public static final int ERROR_UNKNOWN = 1;
    /**
     * 初始化未完成
     */
    public static final int ERROR_UN_INIT = 2;

    /**
     * 错误信息
     */
    private int errorCode;
    /**
     * 错误原因
     */
    private Throwable cause;
    /**
     * 发送的数据，可能为空
     */
    private Request requestData;
    /**
     * 响应的数据，可能为空
     */
    private Response responseData;
    /**
     * 错误描述，客户端可以通过这个字段来设置统一的错误提示等等
     */
    private String description;

    /**
     * 保留字段，可以自定义存放任意数据
     */
    private Object reserved;

    ErrorResponse() {

    }

    /**
     * 初始化
     */
    public void init(Request request, int code, Throwable tr) {
        this.requestData = request;
        this.cause = tr;
        this.errorCode = code;
    }

    /**
     * 获取错误码：
     * {@link #ERROR_NO_CONNECT}、
     * {@link #ERROR_UNKNOWN}、
     * {@link #ERROR_UN_INIT}
     */
    public int getErrorCode() {
        return errorCode;
    }

    /**
     * @see #getErrorCode()
     */
    public void setErrorCode(int errorCode) {
        this.errorCode = errorCode;
    }

    public Throwable getCause() {
        return cause;
    }

    public void setCause(Throwable cause) {
        this.cause = cause;
    }

    public Request getRequestData() {
        return requestData;
    }

    public void setRequestData(Request requestData) {
        this.requestData = requestData;
    }

    public Response getResponseData() {
        return responseData;
    }

    public void setResponseData(Response responseData) {
        this.responseData = responseData;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Object getReserved() {
        return reserved;
    }

    public void setReserved(Object reserved) {
        this.reserved = reserved;
    }

    public void release() {
        ResponseFactory.releaseErrorResponse(this);
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("[@ErrorResponse");
        builder.append(hashCode());
        builder.append(",");
        builder.append("errorCode=");
        builder.append(errorCode);
        builder.append(",");
        builder.append("cause=");
        builder.append(cause == null ?
                "null" :
                cause.toString());
        builder.append(",");
        builder.append("requestData=");
        String request;
        if (requestData != null) {
            request = requestData.toString();
        } else {
            request = "null";
        }
        builder.append(request);
        builder.append(",");
        builder.append("responseData=");
        String response;
        if (responseData != null) {
            response = responseData.toString();
        } else {
            response = "null";
        }
        builder.append(response);
        builder.append(",");
        builder.append("description=");
        builder.append(description);
        builder.append(",");
        if (reserved != null) {
            builder.append("reserved=");
            builder.append(reserved.toString());
            builder.append(",");
        }
        builder.append("]");
        return builder.toString();
    }
}
