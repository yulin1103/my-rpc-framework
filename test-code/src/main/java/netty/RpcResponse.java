package netty;

import java.io.Serializable;

/**
 * RPC响应实体类
 * @author yulin
 * @create 2020-08-15 15:17
 */
public class RpcResponse implements Serializable {
    private static final Long serialVersionUID = 1L;
    private String message;

    public RpcResponse(){

    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    @Override
    public String toString() {
        return "RpcResponse{" +
                "message='" + message + '\'' +
                '}';
    }
}
