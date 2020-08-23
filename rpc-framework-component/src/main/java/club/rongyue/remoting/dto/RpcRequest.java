package club.rongyue.remoting.dto;

import club.rongyue.entity.RpcServiceProperties;
import club.rongyue.enumeration.RpcMessageType;

import java.io.Serializable;
import java.util.Arrays;

/**
 * 请求对象
 * @author yulin
 * @createTime 2020-08-22 16:15
 */
public class RpcRequest implements Serializable {
    private static final Long serialVersionUID = 1L;
    private String requestId;
    /**
     * 服务接口名（非全名）
     */
    private String interfaceName;
    /**
     * 请求服务的目标方法名
     */
    private String methodName;
    /**
     * 请求参数列表
     */
    private Object[] parameters;
    private Class<?>[] parameterTypes;
    /**
     * 请求类型，目前唯一值：HEART_BEAT，心跳请求
     */
    private RpcMessageType rpcMessageType;
    /**
     *  group、version用于区分同一个接口的不同实现类
     */
    private String group;
    private String version;

    /**
     * 获取服务属性对象，用于获取服务的全称
     */
    public RpcServiceProperties getRpcServiceProperties(){
        RpcServiceProperties rpcServiceProperties = new RpcServiceProperties();
        rpcServiceProperties.setServiceName(this.getInterfaceName());
        rpcServiceProperties.setGroup(this.getGroup());
        rpcServiceProperties.setVersion(this.getVersion());
        return rpcServiceProperties;
    }

    public RpcRequest() {
    }

    public String getRequestId() {
        return requestId;
    }

    public void setRequestId(String requestId) {
        this.requestId = requestId;
    }

    public String getInterfaceName() {
        return interfaceName;
    }

    public void setInterfaceName(String interfaceName) {
        this.interfaceName = interfaceName;
    }

    public String getMethodName() {
        return methodName;
    }

    public void setMethodName(String methodName) {
        this.methodName = methodName;
    }

    public Object[] getParameters() {
        return parameters;
    }

    public void setParameters(Object[] parameters) {
        this.parameters = parameters;
    }

    public Class<?>[] getParameterTypes() {
        return parameterTypes;
    }

    public void setParameterTypes(Class<?>[] parameterTypes) {
        this.parameterTypes = parameterTypes;
    }

    public RpcMessageType getRpcMessageType() {
        return rpcMessageType;
    }

    public void setRpcMessageType(RpcMessageType rpcMessageType) {
        this.rpcMessageType = rpcMessageType;
    }

    public String getGroup() {
        return group;
    }

    public void setGroup(String group) {
        this.group = group;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    @Override
    public String toString() {
        return "RpcRequest{" +
                "requestId='" + requestId + '\'' +
                ", interfaceName='" + interfaceName + '\'' +
                ", methodName='" + methodName + '\'' +
                ", parameters=" + Arrays.toString(parameters) +
                ", parameterTypes=" + Arrays.toString(parameterTypes) +
                ", rpcMessageType=" + rpcMessageType +
                ", group='" + group + '\'' +
                ", version='" + version + '\'' +
                '}';
    }
}
