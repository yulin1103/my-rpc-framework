package club.rongyue.remoting.transport.netty.client;

import club.rongyue.enumeration.RpcErrorMessage;
import club.rongyue.exception.RpcException;
import club.rongyue.remoting.dto.RpcRequest;
import club.rongyue.remoting.dto.RpcResponse;
import club.rongyue.remoting.transport.netty.server.NettyServerHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * 客户端自定义处理器
 * @author yulin
 * @createTime 2020-08-27 23:12
 */
public class NettyClientHandler extends ChannelInboundHandlerAdapter {
    private static final Logger logger = LoggerFactory.getLogger(NettyServerHandler.class);

    /**
     *  当通道有读取事件时触发（服务端传输数据过来）
     * @param ctx 上下文对象
     * @param msg 服务端发送过来的消息
     */
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        logger.info("netty客户端接收到的消息：" + msg);
        if (!(msg instanceof RpcResponse)){
            //数据传输过程中出错
            throw new RpcException(RpcErrorMessage.TRANSPORT_DATA_ERROR);
        }
        RpcResponse<Object> rpcResponse = (RpcResponse<Object>) msg;
    }
}
