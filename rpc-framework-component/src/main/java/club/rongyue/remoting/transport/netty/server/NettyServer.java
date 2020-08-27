package club.rongyue.remoting.transport.netty.server;

import club.rongyue.utils.GlobalVariable;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.timeout.IdleStateHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.concurrent.TimeUnit;

/**
 * netty 服务端
 * @author yulin
 * @createTime 2020-08-27 21:00
 */
public class NettyServer {
    private static final Logger logger = LoggerFactory.getLogger(NettyServer.class);

    /**
     * 启动netty服务端
     */
    public void start() {
        //1、创建两个线程循环组
        EventLoopGroup bossGroup = new NioEventLoopGroup();
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        try {
            //2、创建服务器启动对象，并配置相关启动参数
            ServerBootstrap serverBootstrap = new ServerBootstrap();
            //3、设置相关参数
            //设置两个线程组
            serverBootstrap.group(bossGroup , workerGroup)
                    //使用NioServerSocketChannel作为服务器通道的实现
                    .channel(NioServerSocketChannel.class)
                    //TCP默认开启 Nagle算法。该算法的作用是尽可能的发送大数据块，减少网络传输。TCP_NOdELAY参数控制是否开启这个算法
                    .childOption(ChannelOption.TCP_NODELAY , true)
                    //开启TCP底层心跳机制
                    .childOption(ChannelOption.SO_KEEPALIVE , true)
                    //表示系统用于临时存放已经完成三次握手的请求队列的最大长度。如果连接建立频繁，服务器处理创建新连接较慢。
                    .option(ChannelOption.SO_BACKLOG , 128)
                    //日志
                    .handler(new LoggingHandler(LogLevel.INFO))
                    //当客户端第一次进行请求的时候才会进行初始化
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel ch) throws Exception {
                            //30s之内没有收到客户端请求的话就关闭连接
                            ch.pipeline().addLast(new IdleStateHandler(30 , 0 , 0 , TimeUnit.SECONDS));
                            //自定义处理器
                            ch.pipeline().addLast(new NettyServerHandler());
                        }
                    });
            String host = InetAddress.getLocalHost().getHostAddress();
            //4、绑定一个端口并且同步，生成了一个 ChannelFuture对象。（启动服务器，并绑定端口）
            ChannelFuture channelFuture = serverBootstrap.bind(host , GlobalVariable.PORT).sync();
            //5、对关闭通道进行监听
            channelFuture.channel().closeFuture().sync();
        } catch (UnknownHostException | InterruptedException e) {
            logger.error("启动netty服务端出现异常"  , e);
        } finally {
            logger.info("netty 服务端关闭连接");
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }

}
