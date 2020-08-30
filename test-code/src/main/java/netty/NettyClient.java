package netty;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.timeout.IdleStateHandler;
import io.netty.util.AttributeKey;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.TimeUnit;

/**
 * netty 客户端
 * @author yulin
 * @create 2020-08-15 15:18
 */
public class NettyClient {
    private static final Logger log = LoggerFactory.getLogger(NettyClient.class);
    private final String host;
    private final int port;
    private static final Bootstrap BOOTSTRAP;

    public NettyClient(String host, int port) {
        this.host = host;
        this.port = port;
    }

    //初始化相关资源，如EventLoopGroup、Bootstrap
    static {
        // 1、客户端只需要一个事件循环组
        EventLoopGroup eventLoopGroup = new NioEventLoopGroup();
        // 2、创建客户端启动对象
        BOOTSTRAP = new Bootstrap();
        //序列化和反序列化
        KryoSerializer kryoSerializer = new KryoSerializerImpl();
        // 3、设置相关参数
        // 设置线程组
        BOOTSTRAP.group(eventLoopGroup)
                //设置客户端通道实现类
                .channel(NioSocketChannel.class)
                //设置日志处理器
                .handler(new LoggingHandler(LogLevel.INFO))
                //连接超时时间。
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS , 5000)
                .handler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel ch) throws Exception {
                        //如果15秒之内没有发送数据给服务端的话，就发送一次心跳请求
                        ch.pipeline().addLast(new IdleStateHandler(0 , 5 , 0 , TimeUnit.SECONDS));
                        //自定义编码器
                        ch.pipeline().addLast(new NettyEncoder2(RpcRequest.class , kryoSerializer));
                        //自定义解码器
                        ch.pipeline().addLast(new NettyDecoder2(RpcResponse.class , kryoSerializer));
                        //自定义处理器
                        ch.pipeline().addLast(new NettyClientHandler());
                    }
                });
    }

    /**
     * 发送消息到服务端
     * @param rpcRequest 请求实体
     * @return 响应实体
     */
    public RpcResponse sendMessage(RpcRequest rpcRequest){
        try {
            System.out.println("...客户端已经准备好....");
            // 4、启动客户端去连接服务器端，ChannelFuture涉及Netty的异步模型
            ChannelFuture channelFuture = BOOTSTRAP.connect(host , port).sync();
            log.info("client connect {}",host + ":" + port );
            Channel futureChannel = channelFuture.channel();
            log.info("准备发送消息给服务器端");
            if (futureChannel != null){
                //发送消息
                futureChannel.writeAndFlush(rpcRequest);
                log.info("成功发送消息给服务器端" + rpcRequest);
                //阻塞等待，直到Channel关闭
                futureChannel.closeFuture().sync();
                //将服务端返回的数据取出
                AttributeKey<RpcResponse> key = AttributeKey.valueOf("rpcResponse");
                return futureChannel.attr(key).get();
            }
        } catch (InterruptedException e) {
            log.info("occur exception when connect server:" , e);
        }
        return null;
    }
}
