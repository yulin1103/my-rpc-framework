package netty;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayInputStream;
import java.io.ObjectInputStream;
import java.util.List;

/**
 * 自定义解码器，使用JDK的API，ObjectInputStream 。将 byte[] ---> 对象
 * @author yulin
 * @create 2020-08-15 17:39
 */
public class NettyDecoder extends ByteToMessageDecoder {
    private static final Logger log = LoggerFactory.getLogger(NettyDecoder.class);
    /**
     * Netty传输的消息长度，也就是对象序列化后对应字节数组的大小，存储在ByteBuf头部
     */
    private static final int BODY_LENGTH = 4;

    /**
     * 解码ByteBuf对象
     * @param ctx 解码器关联的ChannelHandlerContext对象
     * @param in “入站”数据，也就是ByteBuf对象
     * @param out 解码之后的数据对象，需要添加到out中
     */
    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        log.info("进入自定义解码器");
        // 1、ByteBuf中存放【写入的消息长度所占字节数】为4，所以ByteBuf的可读字节必须大于4（消息不为空）
        if (in.readableBytes() <= BODY_LENGTH){
            log.info("msg is empty");
            return;
        }
        // 2、标记当前readIndex的位置，以便后面重置readIndex的时候使用
        in.markReaderIndex();
        // 3、读取消息的长度
        int dataLength = in.readInt();

        // 4、遇到不合理的情况直接return

        // 5、如果可读字节数小于消息长度，说明消息不完整，重置readIndex
        if (in.readableBytes() < dataLength){
            in.resetReaderIndex();
            return;
        }

        // 6、走到这说明没什么问题了，可以反序列化
        byte[] body = new byte[dataLength];
        in.readBytes(body);
        ByteArrayInputStream bis = new ByteArrayInputStream(body);
        ObjectInputStream ois = new ObjectInputStream(bis);
        Object obj = ois.readObject();
        out.add(obj);
        log.info("解码成功");
    }
}
