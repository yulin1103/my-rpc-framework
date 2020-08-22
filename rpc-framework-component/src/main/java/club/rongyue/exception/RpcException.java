package club.rongyue.exception;

import club.rongyue.enumeration.RpcErrorMessage;

/**
 * @author yulin
 * @createTime 2020-08-22 18:15
 */
public class RpcException extends RuntimeException{
    public RpcException(RpcErrorMessage rpcErrorMessage , String detail){
        super(rpcErrorMessage.getMessage() + ":" + detail);
    }

    public RpcException(String message , Throwable e){
        super(message , e);
    }

    public RpcException(RpcErrorMessage rpcErrorMessage){
        super(rpcErrorMessage.getMessage());
    }
}
