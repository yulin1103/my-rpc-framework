package club.rongyue.remoting.transport.socket;

import club.rongyue.entity.RpcServiceProperties;
import club.rongyue.exception.RpcException;
import club.rongyue.registry.ServiceDiscovery;
import club.rongyue.registry.zk.ZkServiceDiscovery;
import club.rongyue.remoting.dto.RpcRequest;
import club.rongyue.remoting.transport.ClientTransport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;

/**
 * 使用Socket传输RpcRequest
 * @author yulin
 * @createTime 2020-08-22 17:47
 */
public class SocketRpcClient implements ClientTransport {
    private static final Logger logger = LoggerFactory.getLogger(SocketRpcClient.class);
    private final ServiceDiscovery serviceDiscovery;

    public SocketRpcClient(){
        serviceDiscovery = new ZkServiceDiscovery();
    }

    @Override
    public Object sendRpcRequest(RpcRequest rpcRequest) {
        //从rpcRequest获取服务名称
        RpcServiceProperties rpcServiceProperties = rpcRequest.getRpcServiceProperties();
        String rpcServiceName = rpcServiceProperties.toRpcServiceName();
        //从注册中心找到该服务的位置
        InetSocketAddress inetSocketAddress = serviceDiscovery.findService(rpcServiceName);
        logger.info("服务地址：" + inetSocketAddress.toString());
        Socket rpcClient = null;
        ObjectOutputStream oos = null;
        ObjectInputStream ois = null;
        try {
            rpcClient = new Socket();
            rpcClient.connect(inetSocketAddress);
            oos = new ObjectOutputStream(rpcClient.getOutputStream());
            //将rpcRequest通过Socket传到服务端
            oos.writeObject(rpcRequest);
            ois = new ObjectInputStream(rpcClient.getInputStream());
            //会阻塞在这等待服务端传输数据过来
            return ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            throw new RpcException("调用服务失败" , e);
        } finally {
            if (rpcClient != null){
                try {
                    rpcClient.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (oos != null){
                try {
                    oos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (ois != null){
                try {
                    ois.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
