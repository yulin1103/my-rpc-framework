package netty;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * 自定义解码器，使用Kryo反序列化
 * @author yulin
 * @create 2020-08-16 19:41
 */
public class NettyDecoder2 extends ByteToMessageDecoder {
    private static final Logger log = LoggerFactory.getLogger(NettyDecoder2.class);
    private final Class<?> clazz;
    private final KryoSerializer kryoSerializer;
    /**
     * Netty传输的消息长度，也就是对象序列化后对应字节数组的大小，存储在ByteBuf头部
     */
    private static final int BODY_LENGTH = 4;

    public NettyDecoder2(Class<?> clazz, KryoSerializer kryoSerializer) {
        this.clazz = clazz;
        this.kryoSerializer = kryoSerializer;
    }

    /**
     * 将字节数据转成对象
     * @param ctx 上下文对象
     * @param in “入站”的数据，也就是ByteBuf对象
     * @param out 解码之后的对象，存放到集合中
     */
    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        log.info("进入自定义解码器");
        //ByteBuf中存放【写入消息的长度所占字节数】为4，所以ByteBuf可读字节数应该大于4
        if (in.readableBytes() <= BODY_LENGTH){
            log.info("消息为空");
            return;
        }
        //标记readIndex的位置，以便后面重置的时候用
        in.markReaderIndex();
        //读取消息的长度
        int dataLength = in.readInt();
        //遇到不合理的情况直接return

        //如果可读字节数小于消息长度，说明消息不完整，重置readIndex
        if (in.readableBytes() < dataLength){
            in.resetReaderIndex();
            return;
        }
        //走到这说明没什么问题了，可以反序列化
        byte[] data = new byte[dataLength];
        in.readBytes(data);
        Object obj = kryoSerializer.deserialize(data, clazz);
        out.add(obj);
        log.info("字节数据解码成功");
    }
}
