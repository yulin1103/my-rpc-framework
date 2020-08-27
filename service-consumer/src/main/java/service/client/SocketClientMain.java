package service.client;

import club.rongyue.proxy.RpcClientProxy;
import club.rongyue.remoting.transport.socket.SocketRpcClient;
import user.service.OrderService;
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
        RpcClientProxy<OrderService> orderServiceRpcClientProxy = new RpcClientProxy<OrderService>(client);
        OrderService orderService = orderServiceRpcClientProxy.getProxy(OrderService.class);
        //该服务未注册，测试抛出的异常
        //orderService.getCount();
//        SocketRpcClient client2 = new SocketRpcClient();
//        RpcClientProxy<UserService> rpcClientProxy2 = new RpcClientProxy<UserService>(client2);
        Object result2 = userService.getUser(1);
        System.out.println(result2);
    }
}
