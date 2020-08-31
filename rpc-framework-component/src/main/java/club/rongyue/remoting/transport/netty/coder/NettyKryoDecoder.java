package club.rongyue.remoting.transport.netty.coder;

import club.rongyue.remoting.transport.Serializer;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * 自定义解码器，解码“入站”的消息。将字节数据转成对象
 * @author yulin
 * @createTime 2020-08-31 8:46
 */
public class NettyKryoDecoder extends ByteToMessageDecoder {
    private static final Logger logger = LoggerFactory.getLogger(NettyKryoDecoder.class);
    private final Serializer serializer;
    private final Class<?> clazz;
    /**
     * 字节数组中头部 4 byte 存储消息的长度，所以传过来的字节数据长度要大于4
     */
    private static final int DATA_LENGTH = 4;

    public NettyKryoDecoder(Serializer serializer , Class<?> clazz){
        this.serializer = serializer;
        this.clazz = clazz;
    }


    /**
     * 反序列化
     * @param ctx 解码器关联的 ChannelHandlerContext 对象
     * @param in ByteBuf对象，字节数据容器
     * @param out 存放解码之后的对象
     */
    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        //1、byteBuf中写入的消息长度所占的字节数已经是4了，所以 byteBuf 的可读字节必须大于 4，
        if (in.readableBytes() > DATA_LENGTH){
            //2、标记当前readIndex的位置，以便后面重置readIndex的时候使用
            in.markReaderIndex();
            //3、读取消息的长度（out.writeInt(dataLength)方法写入的长度）
            int dataLength = in.readInt();
            //4、遇到不合理的情况直接return
            if (dataLength < 0){
                logger.info("消息长度为0！！！");
                return;
            }
            //5、如果可读字节长度小于消息长度，说明消息不完整，重置readIndex
            if (in.readableBytes() < dataLength){
                in.resetReaderIndex();
                return;
            }
            //6、没问题了，可以进行反序列化
            byte[] bytes = new byte[dataLength];
            in.readBytes(bytes);
            //7、将字节数据转成需要的对象
            Object obj = serializer.deserialize(bytes , clazz);
            out.add(obj);
            logger.info("解码成功");
        }
    }
}
