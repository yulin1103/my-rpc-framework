package club.rongyue.remoting.dto;

import club.rongyue.enumeration.RpcErrorMessage;
import club.rongyue.enumeration.RpcResponseCode;
import club.rongyue.exception.RpcException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 检查远程调用返回结果
 * @author yulin
 * @createTime 2020-08-22 16:22
 */
public class RpcMessageChecker {
    private static final Logger logger = LoggerFactory.getLogger(RpcMessageChecker.class);

    public static void check(RpcRequest rpcRequest  ,RpcResponse<Object> rpcResponse){
        if (rpcResponse == null){
            logger.info("服务调用失败，返回值为null");
            throw new RpcException(RpcErrorMessage.SERVER_INVOCATION_FAILURE, ":" + rpcRequest.getRpcServiceProperties().toRpcServiceName());
        }
        if (!rpcRequest.getRequestId().equals(rpcResponse.getRpcRequestId())){
            logger.info("请求与相应不匹配（requestId不等）");
            throw new RpcException(RpcErrorMessage.REQUEST_NOT_MATCH_RESPONSE);
        }
        if (rpcResponse.getCode() == null || !rpcResponse.getCode().equals(RpcResponseCode.SUCCESSS.getCode())){
            logger.info("其他错误");
            throw new RpcException(RpcErrorMessage.SERVER_INVOCATION_FAILURE ,":" + rpcRequest.getRpcServiceProperties().toRpcServiceName());
        }
    }
}
