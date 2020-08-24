package service.client;

import club.rongyue.proxy.RpcClientProxy;
import club.rongyue.remoting.transport.socket.SocketRpcClient;
import user.service.UserService;

/**
 * @author yulin
 * @createTime 2020-08-24 23:50
 */
public class SocketClientMain {
    public static void main(String[] args) {
        SocketRpcClient client = new SocketRpcClient();
        RpcClientProxy<UserService> rpcClientProxy = new RpcClientProxy<UserService>(client);
        UserService userService = rpcClientProxy.getProxy(UserService.class);
        Object result = userService.getUser(1);
        System.out.println(result);
    }
}
