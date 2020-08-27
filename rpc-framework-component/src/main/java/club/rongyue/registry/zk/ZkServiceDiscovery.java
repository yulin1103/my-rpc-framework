package club.rongyue.registry.zk;

import club.rongyue.enumeration.RpcErrorMessage;
import club.rongyue.exception.RpcException;
import club.rongyue.registry.ServiceDiscovery;
import club.rongyue.utils.CuratorUtils;
import org.apache.curator.framework.CuratorFramework;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;
import java.util.List;
import java.util.Random;

/**
 * 暴露服务位置（IP:PORT）
 * @author yulin
 * @createTime 2020-08-24 22:08
 */
public class ZkServiceDiscovery implements ServiceDiscovery {
    private static final Logger logger = LoggerFactory.getLogger(ZkServiceDiscovery.class);

    /**
     * 过服务名称从注册中心查找服务
     * @param rpcServiceName 服务名称
     */
    @Override
    public InetSocketAddress findService(String rpcServiceName) {
        CuratorFramework zkClient = CuratorUtils.getZkClient();
        List<String> serviceAddressList = null;
        // 当路径不存在时，获取子节点将抛出 org.apache.zookeeper.KeeperException$NoNodeException 异常
        try {
            serviceAddressList = CuratorUtils.getChildrenNodes(zkClient , rpcServiceName);
        } catch (Exception e) {
            //找不到这个服务
            throw new RpcException(RpcErrorMessage.SERVICE_CANNOT_BE_FOUND , rpcServiceName);
        }
        //负载平衡（此处随机获取一个地址）,例：serviceAddress == "127.0.0.1:9999"
        String serviceAddress = serviceAddressList.get(new Random().nextInt(serviceAddressList.size()));
        logger.info("服务：[{}]的地址" , serviceAddress);
        String[] socketAddressArray = serviceAddress.split(":");
        if (socketAddressArray.length < 2){
            throw new RpcException(RpcErrorMessage.SERVICE_ADDRESS_ERROR);
        }
        String host = socketAddressArray[0];
        int port = Integer.parseInt(socketAddressArray[1]);
        return new InetSocketAddress(host , port);
    }
}
