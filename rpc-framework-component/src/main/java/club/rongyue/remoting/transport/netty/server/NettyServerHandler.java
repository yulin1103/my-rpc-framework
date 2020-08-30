package club.rongyue.remoting.transport.netty.server;

import club.rongyue.enumeration.RpcErrorMessage;
import club.rongyue.enumeration.RpcMessageType;
import club.rongyue.enumeration.RpcResponseCode;
import club.rongyue.exception.RpcException;
import club.rongyue.remoting.dto.RpcRequest;
import club.rongyue.remoting.dto.RpcResponse;
import club.rongyue.remoting.handler.RpcRequestHandler;
import club.rongyue.utils.factories.SingletonFactory;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.util.ReferenceCountUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 自定义处理器，处理客户端发送过来的数据
 * @author yulin
 * @createTime 2020-08-27 21:36
 */
public class NettyServerHandler extends ChannelInboundHandlerAdapter {
    private static final Logger logger = LoggerFactory.getLogger(NettyServerHandler.class);
    private final RpcRequestHandler rpcRequestHandler;

    public NettyServerHandler(){
        this.rpcRequestHandler = SingletonFactory.getInstance(RpcRequestHandler.class);
    }

    /**
     * 当通道有读取事件时触发（客户端发送数据过来）
     * @param ctx 上下文对象
     * @param msg 客户端发送过来的消息
     */
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg)throws Exception {
        try {
            logger.info("服务端接收到的数据：" + msg);
            if (!(msg instanceof RpcRequest)){
                //数据传输过程中出错
                throw new RpcException(RpcErrorMessage.TRANSPORT_DATA_ERROR);
            }
            RpcRequest rpcRequest = (RpcRequest) msg;
            if (rpcRequest.getRpcMessageType().equals(RpcMessageType.HEART_BEAT)){
                logger.info("心跳请求，不需要处理");
                return;
            }
            //处理请求，执行目标方法，返回结果
            Object result = rpcRequestHandler.handle(rpcRequest);
            logger.info("服务端处理rpc请求得到的结果：" + result);
            if (ctx.channel().isActive() || ctx.channel().isWritable()){
                RpcResponse<Object> rpcResponse = RpcResponse.success(result , rpcRequest.getRequestId());
                ctx.writeAndFlush(rpcResponse).addListener(ChannelFutureListener.CLOSE_ON_FAILURE);
            }else {
                RpcResponse<Object> rpcResponse = RpcResponse.failure(RpcResponseCode.FAILURE);
                ctx.writeAndFlush(rpcResponse).addListener(ChannelFutureListener.CLOSE_ON_FAILURE);
                logger.info("现在不能传输回消息给客户端");
            }
        } finally {
            //确保ByteBuf释放资源，防止出现内存泄漏错误（继承SimpleChannelInboundHandler 的话就不要考虑 ByteBuf 的释放 ）
            ReferenceCountUtil.release(msg);
        }
    }

    /**
     * Netty 处理心跳超时事件，在IdleStateHandler设置超时时间，如果达到了，就会直接调用该方法。如果没有超时则不调用。
     * @param ctx  上下文对象
     * @param evt 事件
     */
    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        //服务端等待超时(30s)，关闭连接
        if (evt instanceof IdleState){
            IdleState state = ((IdleStateEvent) evt).state();
            if (state == IdleState.READER_IDLE){
                logger.info("等待超时，关闭连接");
                ctx.close();
            }else {
                super.userEventTriggered(ctx , evt);
            }
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        logger.info("netty Server捕获到异常");
        cause.printStackTrace();
        ctx.close();
    }
}
