package club.rongyue.registry;

import java.net.InetSocketAddress;

/**
 * 暴露注册中心的服务
 * @author yulin
 * @createTime 2020-08-24 22:10
 */
public interface ServiceDiscovery {

    /**
     * 通过服务名称从注册中心查找服务
     * @param rpcServiceName 服务名称
     * @return @return InetSocketAddress对象
     */
    InetSocketAddress findService(String rpcServiceName);
}
