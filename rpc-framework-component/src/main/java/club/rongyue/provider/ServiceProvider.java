package club.rongyue.provider;

import club.rongyue.entity.RpcServiceProperties;

/**
 * 保存服务到容器（Set、Map）中，并将服务的IP地址和端口号注册到注册中心
 * @author yulin
 * @createTime 2020-08-22 20:41
 */
public interface ServiceProvider {

    /**
     * 服务端添加服务（即接口实现类的对象）到容器中，getService()方法从容器中获取服务对象
     * @param service 服务对象
     * @param serviceClass 服务类实现接口，serviceClass == 接口的Class对象
     * @param rpcServiceProperties 服务对象属性（用于区分同一个接口的不同实现类）
     */
    void addService(Object service , Class<?> serviceClass , RpcServiceProperties rpcServiceProperties);

    /**
     * RPC请求处理器（RpcRequestHandler）获取服务对象，处理RPC请求
     * @param rpcServiceProperties 服务对象属性（用于区分同一个接口的不同实现类）
     * @return 服务对象（即接口实现类的对象）
     */
    Object getService(RpcServiceProperties rpcServiceProperties);

    /**
     * 服务端发布服务。将服务的IP地址和端口号注册到注册中心
     * @param service 服务对象（即接口实现类的对象）
     * @param rpcServiceProperties 服务对象属性（用于区分同一个接口的不同实现类）
     */
    void publishService(Object service , RpcServiceProperties rpcServiceProperties);

    /**
     * 发布服务
     * @param service 服务对象（即接口实现类的对象）
     */
    void publishService(Object service);
}
