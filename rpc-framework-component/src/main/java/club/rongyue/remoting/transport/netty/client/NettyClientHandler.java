package club.rongyue.remoting.transport.netty.client;

import club.rongyue.enumeration.RpcErrorMessage;
import club.rongyue.enumeration.RpcMessageType;
import club.rongyue.exception.RpcException;
import club.rongyue.remoting.dto.RpcRequest;
import club.rongyue.remoting.dto.RpcResponse;
import club.rongyue.remoting.transport.netty.server.NettyServerHandler;
import club.rongyue.utils.factories.SingletonFactory;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.util.ReferenceCountUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;


/**
 * 客户端自定义处理器
 * @author yulin
 * @createTime 2020-08-27 23:12
 */
public class NettyClientHandler extends ChannelInboundHandlerAdapter {
    private static final Logger logger = LoggerFactory.getLogger(NettyServerHandler.class);
    private final UnprocessedRpcRequest unprocessedRpcRequest;
    private final ChannelProvider channelProvider;

    public NettyClientHandler(){
        unprocessedRpcRequest = SingletonFactory.getInstance(UnprocessedRpcRequest.class);
        channelProvider = SingletonFactory.getInstance(ChannelProvider.class);
    }

    /**
     *  当通道有读取事件时触发（服务端传输数据过来）
     * @param ctx 上下文对象
     * @param msg 服务端发送过来的消息
     */
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        try {
            logger.info("客户端接收到的消息：" + msg);
            if (!(msg instanceof RpcResponse)){
                //数据传输过程中出错
                throw new RpcException(RpcErrorMessage.TRANSPORT_DATA_ERROR);
            }
            RpcResponse<Object> rpcResponse = (RpcResponse<Object>) msg;
            //服务端已经处理完成请求，通知CompletableFuture 对象
            unprocessedRpcRequest.complete(rpcResponse);
        } finally {
            ReferenceCountUtil.release(msg);
        }
    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        //客户端空闲超时(15s).发送心跳请求给客户端
        if (evt instanceof IdleStateEvent){
            IdleState state = ((IdleStateEvent)evt).state();
            if (state == IdleState.WRITER_IDLE){
                logger.info("writer idle occur [{}]" , ctx.channel().remoteAddress());
                //发送一个心跳请求
                Channel channel = channelProvider.getChannel((InetSocketAddress) ctx.channel().remoteAddress());
                RpcRequest rpcRequest = new RpcRequest();
                rpcRequest.setRpcMessageType(RpcMessageType.HEART_BEAT);
                //添加监听器ChannelFutureListener.CLOSE_ON_FAILURE
                channel.writeAndFlush(rpcRequest).addListener(ChannelFutureListener.CLOSE_ON_FAILURE);
            }
        }else {
            super.userEventTriggered(ctx , evt);
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        logger.error("客户端捕获到异常" , cause);
        cause.printStackTrace();
        ctx.close();
    }
}
