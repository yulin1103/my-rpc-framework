package club.rongyue.remoting.transport.netty.client;

import club.rongyue.registry.ServiceDiscovery;
import club.rongyue.registry.zk.ZkServiceDiscovery;
import club.rongyue.remoting.dto.RpcRequest;
import club.rongyue.remoting.dto.RpcResponse;
import club.rongyue.remoting.transport.ClientTransport;
import club.rongyue.utils.factories.SingletonFactory;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;
import java.util.concurrent.CompletableFuture;

/**
 * netty客户端 发送数据到 netty服务端
 * @author yulin
 * @createTime 2020-08-30 21:47
 */
public class NettyClientTransport implements ClientTransport {
    private static final Logger logger = LoggerFactory.getLogger(NettyClientTransport.class);
    private final ServiceDiscovery serviceDiscovery;
    private final UnprocessedRpcRequest unprocessedRpcRequest;
    private final ChannelProvider channelProvider;

    public NettyClientTransport(){
        serviceDiscovery = new ZkServiceDiscovery();
        unprocessedRpcRequest = SingletonFactory.getInstance(UnprocessedRpcRequest.class);
        channelProvider = SingletonFactory.getInstance(ChannelProvider.class);
    }

    /**
     * CompletableFuture 用于异步编程，异步编程是编写非阻塞的代码，运行的任务在一个单独的线程，
     * 与主线程隔离，并且会通知主线程它的进度，成功或者失败。
     * @param rpcRequest 请求体
     */
    @Override
    public CompletableFuture<RpcResponse<Object>> sendRpcRequest(RpcRequest rpcRequest) {
        //这是一个最简单的 CompletableFuture，想获取CompletableFuture 的结果可以使用 CompletableFuture.get()方法
        CompletableFuture<RpcResponse<Object>> resultFuture = new CompletableFuture<>();
        // rpcServiceName == 接口名称+group+version
        String rpcServiceName = rpcRequest.getRpcServiceProperties().toRpcServiceName();
        InetSocketAddress inetSocketAddress = serviceDiscovery.findService(rpcServiceName);
        Channel channel = channelProvider.getChannel(inetSocketAddress);
        if (channel != null && channel.isActive()){
            //添加到等待容器中，当服务端处理完请求回调用CompletableFuture的complete()方法
            unprocessedRpcRequest.put(rpcRequest.getRequestId() , resultFuture);
            //ChannelFuture的作用是用来保存Channel异步操作的结果。
            //添加ChannelFutureListener，以便于在I/O操作完成的时候，你能够获得通知。
            channel.writeAndFlush(rpcRequest).addListener(new ChannelFutureListener() {
                @Override
                public void operationComplete(ChannelFuture future) throws Exception {
                    if (future.isSuccess()){
                        logger.info("客户端成功发送[{}]消息到服务端" , rpcRequest);
                    }else {
                        //发送消息失败，通知CompletableFuture 对象
                        future.channel().close();
                        resultFuture.completeExceptionally(future.cause());
                        logger.info("客户端发送消息失败" , future.cause());
                    }
                }
            });
        } else {
            throw new IllegalStateException();
        }
        return resultFuture;
    }
}
