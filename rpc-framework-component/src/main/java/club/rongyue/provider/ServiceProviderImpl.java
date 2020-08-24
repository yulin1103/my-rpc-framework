package club.rongyue.provider;

import club.rongyue.entity.RpcServiceProperties;
import club.rongyue.enumeration.RpcErrorMessage;
import club.rongyue.exception.RpcException;
import club.rongyue.registry.ServiceRegistry;
import club.rongyue.registry.zk.ZkServiceRegistry;
import club.rongyue.utils.GlobalVariable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 服务生产者（提供服务的一方）
 * 保存服务到容器（Set、Map）中，并将服务的IP地址和端口号注册到注册中心
 * @author yulin
 * @createTime 2020-08-22 20:41
 */
public class ServiceProviderImpl implements ServiceProvider{
    private static final Logger logger = LoggerFactory.getLogger(ServiceProviderImpl.class);
    /**
     * key: 服务名称
     * value: 服务对象
     */
    private final Map<String , Object> registeredServiceMap;
    /**
     * 没有提供ConcurrentHashSet类，只能通过ConcurrentHashMap.newKeySet()获取线程安全的Set对象
     * value: 服务名称
     */
    private final Set<String> registeredServiceNameSet;
    /**
     * 注册服务到注册中心
     */
    private final ServiceRegistry serviceRegistry;

    public ServiceProviderImpl(){
        registeredServiceMap = new ConcurrentHashMap<>();
        registeredServiceNameSet = ConcurrentHashMap.newKeySet();
        serviceRegistry = new ZkServiceRegistry();
    }


    /**
     * @param service 服务对象
     * @param serviceClass 服务类实现的接口，serviceClass == 接口的Class对象
     * @param rpcServiceProperties 服务对象属性（用于区分同一个接口的不同实现类）
     */
    @Override
    public void addService(Object service, Class<?> serviceClass, RpcServiceProperties rpcServiceProperties) {
        String rpcServiceName = rpcServiceProperties.toRpcServiceName();
        //服务已经注册了
        if (registeredServiceNameSet.contains(rpcServiceName)){
            return;
        }
        //服务还未注册,加入容器
        registeredServiceNameSet.add(rpcServiceName);
        registeredServiceMap.put(rpcServiceName , service);
    }

    /**
     * @param rpcServiceProperties 服务对象属性（用于区分同一个接口的不同实现类）
     */
    @Override
    public Object getService(RpcServiceProperties rpcServiceProperties) {
        Object service = registeredServiceMap.get(rpcServiceProperties.toRpcServiceName());
        if (service == null){
            //找不到这个服务
            throw new RpcException(RpcErrorMessage.SERVICE_CANNOT_BE_FOUND);
        }
        return service;
    }

    /**
     *发布服务
     * @param service 服务对象（即接口实现类的对象）
     */
    @Override
    public void publishService(Object service) {
        RpcServiceProperties rpcServiceProperties = new RpcServiceProperties();
        rpcServiceProperties.setGroup("");
        rpcServiceProperties.setVersion("");
        this.publishService(service , rpcServiceProperties);
    }

    /**
     *服务端发布服务。将服务的IP地址和端口号注册到注册中心
     * @param service 服务对象（即接口实现类的对象）
     * @param rpcServiceProperties 服务对象属性（用于区分同一个接口的不同实现类）
     */
    @Override
    public void publishService(Object service, RpcServiceProperties rpcServiceProperties) {
        try {
            String host = InetAddress.getLocalHost().getHostAddress();
            //服务对象实现接口的Class对象
            Class<?> serviceRelatedInterface = service.getClass().getInterfaces()[0];
            //服务对象实现接口的名称（全类名）
            String serviceName = serviceRelatedInterface.getCanonicalName();
            rpcServiceProperties.setServiceName(serviceName);
            //将服务添加到容器（服务名称、服务对象）
            this.addService(service , serviceRelatedInterface , rpcServiceProperties);
            //将服务注册到注册中心
            serviceRegistry.registryService(rpcServiceProperties.toRpcServiceName() , new InetSocketAddress(host , GlobalVariable.PORT));
        } catch (UnknownHostException e) {
            logger.error("获取本机IP地址失败（服务所在的地址）" , e);
        }
    }
}
