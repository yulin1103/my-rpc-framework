package club.rongyue.enumeration;

/**
 * @author yulin
 * @createTime 2020-08-22 18:09
 */
public enum RpcErrorMessage {
    CLIENT_CONNECT_SERVER_FAILURE("客户端连接服务端失败！！！"),
    SERVER_INVOCATION_FAILURE("调用服务失败！！！"),
    SERVICE_CANNOT_BE_FOUND("找不到该服务！！！"),
    SERVICE_NOT_IMPLEMENTS_ANY_INTERFACE("注册的服务没有实现任何接口！！！"),
    REQUEST_NOT_MATCH_RESPONSE("返回结果错误！请求和响应不匹配！！！"),
    SERVICE_ADDRESS_ERROR("从注册中心获取的服务地址错误！！！"),
    TRANSPORT_DATA_ERROR("传输数据出错");
    private final String message;

    RpcErrorMessage(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    @Override
    public String toString() {
        return "RpcErrorMessage{" +
                "message='" + message + '\'' +
                '}';
    }
}
