package club.rongyue.remoting.handler;

import club.rongyue.exception.RpcException;
import club.rongyue.provider.ServiceProvider;
import club.rongyue.provider.ServiceProviderImpl;
import club.rongyue.remoting.dto.RpcRequest;
import club.rongyue.utils.factories.SingletonFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * RPC请求处理器(单例对象)
 * @author yulin
 * @createTime 2020-08-22 21:28
 */
public class RpcRequestHandler {
    private static final Logger logger = LoggerFactory.getLogger(RpcRequestHandler.class);
    private final ServiceProvider serviceProvider;

    public RpcRequestHandler(){
        serviceProvider = SingletonFactory.getInstance(ServiceProviderImpl.class);
    }

    /**
     * 处理RPC请求
     * @param rpcRequest 请求体
     * @return 返回处理结果
     */
    public Object handle(RpcRequest rpcRequest){
        Object service = serviceProvider.getService(rpcRequest.getRpcServiceProperties());
        return this.invokeTargetMethod(rpcRequest , service);
    }

    /**
     * 执行rpc请求需要调用的目标方法
     * @param rpcRequest rpc请求体
     * @param service 服务
     */
    private Object invokeTargetMethod(RpcRequest rpcRequest , Object service){
        Object result = null;
        try {
            Method targetMethod = service.getClass().getMethod(rpcRequest.getMethodName() , rpcRequest.getParameterTypes());
            result = targetMethod.invoke(service , rpcRequest.getParameters());
            logger.info("服务 [{}] 成功调用: [{}]方法" , rpcRequest.getRpcServiceProperties().toRpcServiceName() , rpcRequest.getMethodName());
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
            throw new RpcException(e.getMessage() , e);
        }
        return result;
    }
}
