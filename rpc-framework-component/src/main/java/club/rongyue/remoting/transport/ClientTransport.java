package club.rongyue.remoting.transport;

import club.rongyue.remoting.dto.RpcRequest;

/**
 * 传输 RpcRequest
 * @author yulin
 * @createTime 2020-08-22 17:44
 */
public interface ClientTransport {
    /**
     * 发送消息到服务端
     * @param rpcRequest 请求体
     * @return 服务端返回的数据
     */
    Object sendRpcRequest(RpcRequest rpcRequest);
}
