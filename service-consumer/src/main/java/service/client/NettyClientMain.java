package service.client;

import club.rongyue.proxy.RpcClientProxy;
import club.rongyue.remoting.transport.netty.client.NettyClient;
import club.rongyue.remoting.transport.netty.client.NettyClientTransport;
import club.rongyue.utils.GlobalVariable;
import user.service.UserService;
import user.service.pojo.User;

import java.util.List;

/**
 * @author yulin
 * @createTime 2020-08-31 16:34
 */
public class NettyClientMain {
    public static void main(String[] args) {
        NettyClientTransport rpcNettyClient = new NettyClientTransport();
        RpcClientProxy<UserService> proxy = new RpcClientProxy<UserService>(rpcNettyClient);
        UserService userService = proxy.getProxy(UserService.class);
        List<User> user = userService.getUser(1);
        System.out.println(user);
    }
}
