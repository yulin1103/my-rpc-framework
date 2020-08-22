package netty.rpc;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.AttributeKey;
import io.netty.util.ReferenceCountUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 自定义处理服务端消息,NettyClientHandler用于读取服务端发送过来的 RpcResponse 消息对象，
 * 并将 RpcResponse 消息对象保存到 AttributeMap 中
 * @author yulin
 * @create 2020-08-15 16:15
 */
public class NettyClientHandler extends ChannelInboundHandlerAdapter {
    private static final Logger log = LoggerFactory.getLogger(NettyClientHandler.class);

    /**
     *  当通道有读取事件时触发（服务端传输数据过来）
     * @param ctx 上下文对象
     * @param msg 服务端发送过来的消息
     */
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
        try {
            RpcResponse rpcResponse = (RpcResponse) msg;
            log.info("client receive msg: [{}]" , rpcResponse.toString());
            //声明一个AttributeKey对象
            AttributeKey<RpcResponse> key = AttributeKey.valueOf("rpcResponse");
            //将服务端的返回结果保存到AttributeMap中，AttributeMap可以看作是一个Channel的共享数据源
            //AttributeMap的key是AttributeKey ， value是Attribute
            ctx.channel().attr(key).set(rpcResponse);
            ctx.channel().close();
        } finally {
            ReferenceCountUtil.release(msg);
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        log.info("client caught exception" , cause);
        ctx.close();
    }
}
