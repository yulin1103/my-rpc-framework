package club.rongyue.remoting.transport.netty.coder;

import club.rongyue.exception.SerializerException;
import club.rongyue.remoting.transport.Serializer;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 自定义编码器（使用Kryo序列化对象）。负责处理“出站”的消息，
 * 将消息格式转成字节数据然后写入到Netty的字节数据容器ByteBuf对象中
 * @author yulin
 * @createTime 2020-08-31 7:43
 */
public class NettyKryoEncoder extends MessageToByteEncoder<Object> {
    private static final Logger logger = LoggerFactory.getLogger(NettyKryoEncoder.class);
    private final Serializer serializer;
    /**
     * 出站消息的类型
     */
    private final Class<?> clazz;

    public NettyKryoEncoder(Serializer serializer , Class<?> clazz){
        this.serializer = serializer;
        this.clazz = clazz;
    }

    /**
     * 序列化，将对象转成字节数据输出到ByteBuf对象中
     * @param ctx 上下文对象
     * @param msg 消息
     * @param out ByteBuf对象,字节数据容器
     */
    @Override
    protected void encode(ChannelHandlerContext ctx, Object msg, ByteBuf out) throws Exception {
        if (clazz.isInstance(msg)){
            // 1、将对象转成byte数据
            byte[] bytes = serializer.serialize(msg);
            // 2、读取消息长度
            int dataLength = bytes.length;
            // 3、在出站的消息头部记录消息长度信息，占4byte。因此出站消息的长度 == dataLength + 4
            out.writeInt(dataLength);
            // 4、将字节数组写入ByteBuf对象中
            out.writeBytes(bytes);
            logger.info("出站消息序列化成功");
        }else {
            throw new SerializerException("编码出站消息，要求编码的类型与实际类型不同！！！");
        }
    }
}
