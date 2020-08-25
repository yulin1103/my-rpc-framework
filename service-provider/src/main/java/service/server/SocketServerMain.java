package service.server;

import club.rongyue.remoting.transport.socket.SocketRpcServer;
import user.service.UserService;
import user.service.impl.UserServiceImpl;

/**
 * @author yulin
 * @createTime 2020-08-24 23:44
 */
public class SocketServerMain {
    public static void main(String[] args) {
        UserService userService = new UserServiceImpl();
        SocketRpcServer server = new SocketRpcServer();
        server.registryService(userService);
        server.start();
    }
}
