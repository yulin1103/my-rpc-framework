package club.rongyue.remoting.transport.netty.client;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.timeout.IdleStateHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

/**
 * netty 客户端
 * @author yulin
 * @createTime 2020-08-27 22:46
 */
public class NettyClient {
    private static final Logger logger = LoggerFactory.getLogger(NettyClient.class);
    private final Bootstrap bootstrap;
    private final EventLoopGroup eventLoopGroup;

    public NettyClient(){
        //1、客户端只需要一个事件循环组
        this.eventLoopGroup = new NioEventLoopGroup();
        //2、创建客户端启动对象
        bootstrap = new Bootstrap();
        //3、设置相关参数
        //设置线程组
        bootstrap.group(eventLoopGroup)
                //客户端通道实现类
                .channel(NioSocketChannel.class)
                //日志
                .handler(new LoggingHandler(LogLevel.INFO))
                //连接超时时间。
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS , 5000)
                .handler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel ch) throws Exception {
                        //如果15秒之内没有发送数据给服务端的话，就发送一次心跳请求
                        ch.pipeline().addLast(new IdleStateHandler(0 , 5 , 0 , TimeUnit.SECONDS));

                    }
                });
    }

    public Channel doConnect(InetSocketAddress inetSocketAddress) throws ExecutionException, InterruptedException {
        CompletableFuture<Channel> completableFuture = new CompletableFuture<>();
        //4、启动客户端，连接服务端
        bootstrap.connect(inetSocketAddress).addListener(new ChannelFutureListener() {
            @Override
            public void operationComplete(ChannelFuture future) throws Exception {
                if (future.isSuccess()){
                    logger.info("客户端成功连接[{}]" , inetSocketAddress.toString());
                    completableFuture.complete(future.channel());
                }else {
                    throw new IllegalStateException();
                }
            }
        });
        return completableFuture.get();
    }

    public void close(){
        eventLoopGroup.shutdownGracefully();
    }

}
