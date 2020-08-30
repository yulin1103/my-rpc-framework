package club.rongyue.remoting.transport.netty.client;

import club.rongyue.remoting.dto.RpcResponse;

import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 存放netty客户端已经发送到netty服务端，但还未被netty服务端处理的Rpc请求。
 * 当netty服务端处理后，调用complete()方法
 * @author yulin
 * @createTime 2020-08-30 22:31
 */
public class UnprocessedRpcRequest {
    /**
     * key: rpcRequestId
     * value: CompletableFuture对象
     * CompletableFuture用于异步编程，异步编程是编写非阻塞的代码，运行的任务在一个单独的线程，
     * 与主线程隔离，并且会通知主线程它的进度，成功或者失败。
     */
    private static final Map<String , CompletableFuture<RpcResponse<Object>>> UNPROCESSED_RPC_REQUEST_MAP = new ConcurrentHashMap<>();

    public void put(String rpcRequestId , CompletableFuture<RpcResponse<Object>> future){
        UNPROCESSED_RPC_REQUEST_MAP.put(rpcRequestId , future);
    }

    /**
     * netty服务端处理完成Rpc请求，通知CompletableFuture对象
     */
    public void complete(RpcResponse<Object> rpcResponse){
        //Map的remove()方法将返回被删除键值对的value
        CompletableFuture<RpcResponse<Object>> future = UNPROCESSED_RPC_REQUEST_MAP.remove(rpcResponse.getRpcRequestId());
        if (future != null){
            // CompletableFuture的get()方法会一直阻塞直到 Future 完成
            //使用 CompletableFuture.complete() 手工的完成一个 Future,所有等待这个 Future 的客户端都将得到一个指定的结果，
            //并且 CompletableFuture.complete() 之后的调用将被忽略。
            future.complete(rpcResponse);
        }else {
            throw new IllegalStateException();
        }
    }
}
