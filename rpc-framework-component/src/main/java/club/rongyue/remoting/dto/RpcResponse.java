package club.rongyue.remoting.dto;

import club.rongyue.enumeration.RpcResponseCode;

import java.io.Serializable;

/**
 * 响应对象
 * @author yulin
 * @createTime 2020-08-22 16:47
 */
public class RpcResponse<T> implements Serializable {
    private static final Long serialVersionUID = 1L;
    private String rpcRequestId;
    /**
     * 响应状态码
     */
    private Integer code;
    private String message;
    /**
     * 响应体
     */
    private T data;

    public static <T> RpcResponse<T> success(T data , String rpcRequestId){
        RpcResponse<T> rpcResponse = new RpcResponse<T>();
        rpcResponse.setCode(RpcResponseCode.SUCCESSS.getCode());
        rpcResponse.setMessage(RpcResponseCode.SUCCESSS.getMessage());
        rpcResponse.setRpcRequestId(rpcRequestId);
        if (data != null){
            rpcResponse.setData(data);
        }
        return rpcResponse;
    }

    public static <T> RpcResponse<T> failure(RpcResponseCode rpcResponseCode){
        RpcResponse<T> rpcResponse = new RpcResponse<T>();
        rpcResponse.setCode(rpcResponseCode.getCode());
        rpcResponse.setMessage(rpcResponseCode.getMessage());
        return rpcResponse;
    }

    public RpcResponse() {
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public String getRpcRequestId() {
        return rpcRequestId;
    }

    public void setRpcRequestId(String rpcRequestId) {
        this.rpcRequestId = rpcRequestId;
    }

    @Override
    public String toString() {
        return "RpcResponse{" +
                "rpcRequestId='" + rpcRequestId + '\'' +
                ", code=" + code +
                ", message='" + message + '\'' +
                ", data=" + data +
                '}';
    }
}
