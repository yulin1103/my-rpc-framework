package netty;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 服务端
 * @author yulin
 * @create 2020-08-15 16:42
 */
public class NettyServer {
    private static final Logger log = LoggerFactory.getLogger(NettyServer.class);
    private final int port;

    public NettyServer(int port) {
        this.port = port;
    }

    /**
     * 启动服务器端
     */
    protected void run(){
        // 1、创建两个循环线程组
        EventLoopGroup bossGroup = new NioEventLoopGroup();
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        try {
            // 2、创建服务器端启动对象，配置相关参数
            ServerBootstrap serverBootstrap = new ServerBootstrap();
            //序列化和反序列化
            KryoSerializer kryoSerializer = new KryoSerializerImpl();
            // 3、使用链式编程来设置参数
            //设置两个线程组
            serverBootstrap.group(bossGroup , workerGroup)
                    //使用NioSocketChannel作为服务器端的通道实现
                    .channel(NioServerSocketChannel.class)
                    //TCP默认开启Nagel算法，该算法的作用是尽可能的发送大数据快，减少网络传输。
                    .childOption(ChannelOption.TCP_NODELAY , true)
                    //是否开启TCP底层心跳机制
                    .childOption(ChannelOption.SO_KEEPALIVE , true)
                    //表示系统用于临时存放已完成三次握手的请求的队列的最大长度,如果连接建立频繁，服务器处理创建新连接较慢，可以适当调大这个参数
                    .option(ChannelOption.SO_BACKLOG , 128)
                    .handler(new LoggingHandler(LogLevel.INFO))
                    //当客户端第一次进行请求的时候才会进行初始化
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel ch) throws Exception {
                            //自定义编码器
                            ch.pipeline().addLast(new NettyEncoder2(RpcResponse.class , kryoSerializer));
                            //自定义解码器
                            ch.pipeline().addLast(new NettyDecoder2(RpcRequest.class , kryoSerializer));
                            //自定义处理器
                            ch.pipeline().addLast(new NettyServerHandler());
                        }
                    });
            System.out.println("......服务器已经准备好了...");
            // 4、绑定一个端口并且同步，生成了一个 ChannelFuture对象。（启动服务器，并绑定端口）
            ChannelFuture channelFuture = serverBootstrap.bind(port).sync();
            // 5、对关闭通道进行监听
            channelFuture.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            log.info("occur exception when start server: " + e);
        } finally {
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }
}
