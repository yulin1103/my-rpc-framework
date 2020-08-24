package club.rongyue.registry;

import java.net.InetSocketAddress;

/**
 * 注册服务接口
 * @author yulin
 * @createTime 2020-08-24 19:35
 */
public interface ServiceRegistry {
    /**
     * 注册服务
     * @param rpcServiceName 服务名称
     * @param inetSocketAddress 服务的地址（IP + 端口号）
     */
    void registryService(String rpcServiceName , InetSocketAddress inetSocketAddress);
}
