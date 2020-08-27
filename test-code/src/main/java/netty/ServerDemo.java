package netty;

/**
 * @author yulin
 * @create 2020-08-15 18:13
 */
public class ServerDemo {
    public static void main(String[] args) {
        NettyServer server = new NettyServer(9999);
        //启动服务器端
        server.run();
    }
}
