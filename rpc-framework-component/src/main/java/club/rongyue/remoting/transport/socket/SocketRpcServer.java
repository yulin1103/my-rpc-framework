package club.rongyue.remoting.transport.socket;

import club.rongyue.entity.RpcServiceProperties;
import club.rongyue.provider.ServiceProvider;
import club.rongyue.utils.GlobalVariable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;

/**
 * 服务端（在提供服务的服务器上运行，服务将被注册到注册中心）
 * @author yulin
 * @createTime 2020-08-22 18:23
 */
public class SocketRpcServer {
    private static final Logger logger = LoggerFactory.getLogger(SocketRpcServer.class);
    private final ExecutorService threadPool;
    private final ServiceProvider serviceProvider;

    public SocketRpcServer(){
        threadPool = null;
        serviceProvider = null;
    }

    /**
     * 注册服务，将服务信息注册到注册中心
     * @param service 接口实现类（即服务）
     */
    public void registryService(Object service){

    }

    /**
     * 注册服务，将服务注册到注册中心（当一个接口有多个实现类时，使用这个注册方法）
     * @param service 接口实现类（即服务）
     * @param rpcServiceProperties 服务的其他信息（group，version，serviceName）
     */
    public void registryService(Object service , RpcServiceProperties rpcServiceProperties){

    }

    /**
     * 启动服务端
     */
    public void start(){
        try {
            ServerSocket rpcServer = new ServerSocket();
            //当前服务器的IP地址
            String host = InetAddress.getLocalHost().getHostAddress();
            rpcServer.bind(new InetSocketAddress(host , GlobalVariable.PORT));
            //启动服务端时，清除注册中心的服务数据

            Socket socket = null;
            //accept()是一个阻塞方法，等待客户端的连接请求
            while ((socket = rpcServer.accept()) != null){
                logger.info("客户端成功连接" + host + ":" + GlobalVariable.PORT);
                //启动一个线程去处理客户端的请求
                threadPool.execute(new SocketRpcRequestHandlerRunnable(socket));
            }
            threadPool.shutdown();
        } catch (IOException e) {
            logger.error("客户端连接服务端出现IO异常" , e);
        }
    }
}
