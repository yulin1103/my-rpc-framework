package club.rongyue.provider;

import club.rongyue.entity.RpcServiceProperties;
import club.rongyue.enumeration.RpcErrorMessage;
import club.rongyue.exception.RpcException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
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

    public ServiceProviderImpl(){
        registeredServiceMap = new ConcurrentHashMap<>();
        registeredServiceNameSet = ConcurrentHashMap.newKeySet();
    }


    /**
     * @param service 服务对象
     * @param serviceClass 服务类实现接口，serviceClass == 接口的Class对象
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
     *
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
     *
     * @param service 服务对象（即接口实现类的对象）
     * @param rpcServiceProperties 服务对象属性（用于区分同一个接口的不同实现类）
     */
    @Override
    public void publishService(Object service, RpcServiceProperties rpcServiceProperties) {

    }
}
