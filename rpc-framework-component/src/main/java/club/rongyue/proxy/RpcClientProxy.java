package club.rongyue.proxy;

import club.rongyue.entity.RpcServiceProperties;
import club.rongyue.remoting.dto.RpcMessageChecker;
import club.rongyue.remoting.dto.RpcRequest;
import club.rongyue.remoting.dto.RpcResponse;
import club.rongyue.remoting.transport.ClientTransport;
import club.rongyue.remoting.transport.socket.SocketRpcClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sun.misc.ProxyGenerator;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.UUID;

/**
 * 客户端代理类（封装远程调用的过程，使调用远程方法跟调用本地方法一样）
 * @author yulin
 * @createTime 2020-08-24 23:02
 */
public class RpcClientProxy<T> implements InvocationHandler {
    private static final Logger logger = LoggerFactory.getLogger(RpcClientProxy.class);
    private  final ClientTransport clientTransport;
    private final RpcServiceProperties rpcServiceProperties;

    public RpcClientProxy(ClientTransport clientTransport){
        RpcServiceProperties rpcServiceProperties = new RpcServiceProperties();
        rpcServiceProperties.setGroup("");
        rpcServiceProperties.setVersion("");
        this.clientTransport = clientTransport;
        this.rpcServiceProperties = rpcServiceProperties;
    }

    public RpcClientProxy(ClientTransport clientTransport , RpcServiceProperties rpcServiceProperties){
        this.clientTransport = clientTransport;
        this.rpcServiceProperties = rpcServiceProperties;
    }

    public T getProxy(Class<T> clazz){
        return clazz.cast(Proxy.newProxyInstance(this.getClass().getClassLoader() , new Class<?>[]{clazz} , this));
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        logger.info("调用远程方法：[{}]" , method.getName());
        RpcRequest rpcRequest = new RpcRequest();
        rpcRequest.setRequestId(UUID.randomUUID().toString());
        rpcRequest.setInterfaceName(method.getDeclaringClass().getName());
        rpcRequest.setMethodName(method.getName());
        rpcRequest.setParameters(args);
        rpcRequest.setParameterTypes(method.getParameterTypes());
        rpcRequest.setGroup(rpcServiceProperties.getGroup());
        rpcRequest.setVersion(rpcServiceProperties.getVersion());
        RpcResponse<Object> rpcResponse = null;
        //通过Socket传输
        if (clientTransport instanceof SocketRpcClient){
            logger.info("通过Socket传输数据");
            rpcResponse = (RpcResponse<Object>) clientTransport.sendRpcRequest(rpcRequest);
        }
        //检查返回结果
        RpcMessageChecker.check(rpcRequest , rpcResponse);
        assert rpcResponse != null;
        return rpcResponse.getData();
    }
}
