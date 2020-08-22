package netty.rpc;

import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.ReferenceCountUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 处理客户端消息
 * @author yulin
 * @create 2020-08-15 17:03
 */
public class NettyServerHandler extends ChannelInboundHandlerAdapter {
    private static final Logger log = LoggerFactory.getLogger(NettyServerHandler.class);

    /**
     * 当通道有读取事件时触发（客户端发送数据过来）
     * @param ctx 上下文对象
     * @param msg 客户端发送的消息
     */
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        log.info("服务端channelRead");
        try {
            RpcRequest rpcRequest = (RpcRequest) msg;
            //打印来自客户端的消息
            log.info("server receive msg: [{}]" , rpcRequest.toString());
            RpcResponse rpcResponse = new RpcResponse();
            //向客户端发送回复消息
            rpcResponse.setMessage("message from server");
            ChannelFuture channelFuture = ctx.writeAndFlush(rpcResponse);
            channelFuture.addListener(ChannelFutureListener.CLOSE);
        } finally {
            ReferenceCountUtil.release(msg);
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        log.info("server catch exception" , cause);
        ctx.close();
    }
}
