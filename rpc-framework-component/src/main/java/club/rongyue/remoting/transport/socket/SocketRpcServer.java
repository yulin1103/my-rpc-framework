package club.rongyue.remoting.transport.socket;

import club.rongyue.entity.RpcServiceProperties;
import club.rongyue.provider.ServiceProvider;
import club.rongyue.provider.ServiceProviderImpl;
import club.rongyue.utils.CuratorUtils;
import club.rongyue.utils.GlobalVariable;
import club.rongyue.utils.concurrent.threadpool.ThreadPoolFactoryUtils;
import club.rongyue.utils.factories.SingletonFactory;
import org.apache.curator.framework.CuratorFramework;
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
        threadPool = ThreadPoolFactoryUtils.createCustomThreadPool("socket-server-rpc-pool");
        serviceProvider = SingletonFactory.getInstance(ServiceProviderImpl.class);
    }

    /**
     * 注册服务，将服务信息注册到注册中心
     * @param service 接口实现类（即服务）
     */
    public void registryService(Object service){
        serviceProvider.publishService(service);
    }

    /**
     * 注册服务，将服务注册到注册中心（当一个接口有多个实现类时，使用这个注册方法）
     * @param service 接口实现类（即服务）
     * @param rpcServiceProperties 服务的其他信息（group，version，serviceName,用于区分同一个接口的不同实现类）
     */
    public void registryService(Object service , RpcServiceProperties rpcServiceProperties){
        serviceProvider.publishService(service , rpcServiceProperties);
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
            //启动服务端时，清除注册中心旧的服务数据（旧服务地址不再提供的话，客户会调用失败）
            CuratorFramework zkClient = CuratorUtils.getZkClient();
            CuratorUtils.clearRegisteredService(zkClient);
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
