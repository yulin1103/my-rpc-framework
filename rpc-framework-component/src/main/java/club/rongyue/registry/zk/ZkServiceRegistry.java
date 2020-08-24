package club.rongyue.registry.zk;

import club.rongyue.registry.ServiceRegistry;
import club.rongyue.utils.CuratorUtils;
import org.apache.curator.framework.CuratorFramework;

import java.net.InetSocketAddress;

/**
 * 使用Zookeeper作为注册中心，将服务注册到注册中心（即在zookeeper中创建一个节点）
 * @author yulin
 * @createTime 2020-08-24 19:39
 */
public class ZkServiceRegistry implements ServiceRegistry {
    @Override
    public void registryService(String rpcServiceName, InetSocketAddress inetSocketAddress) {
        // servicePath == "my-rpc/rpcServiceName/IP:PORT"
        String servicePath = CuratorUtils.ZK_PERSISTENT_NODE_ROOT_PATH + "/" + rpcServiceName + inetSocketAddress.toString();
        CuratorFramework zkClient = CuratorUtils.getZkClient();
        CuratorUtils.createPersistentNode(zkClient , servicePath);
    }
}
