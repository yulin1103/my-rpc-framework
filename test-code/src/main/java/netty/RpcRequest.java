package netty;

import java.io.Serializable;

/**
 * RPC请求实体类
 * @author yulin
 * @create 2020-08-15 15:14
 */
public class RpcRequest implements Serializable {
    private static final Long serialVersionUID = 1L;
    private String interfaceName;
    private String methodName;

    public RpcRequest(){

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

    @Override
    public String toString() {
        return "RpcRequest{" +
                "interfaceName='" + interfaceName + '\'' +
                ", methodName='" + methodName + '\'' +
                '}';
    }
}
