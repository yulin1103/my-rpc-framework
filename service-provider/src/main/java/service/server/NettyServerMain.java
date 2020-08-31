package service.server;

import club.rongyue.remoting.transport.netty.server.NettyServer;
import user.service.impl.UserServiceImpl;

/**
 * @author yulin
 * @createTime 2020-08-31 16:27
 */
public class NettyServerMain {
    public static void main(String[] args) {
        NettyServer nettyServer = new NettyServer();
        UserServiceImpl userService = new UserServiceImpl();
        nettyServer.registryService(userService);
        nettyServer.start();
    }
}
