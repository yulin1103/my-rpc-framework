package netty.rpc;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 自定义编码器。使用Kryo进行序列化操作
 * @author yulin
 * @create 2020-08-16 18:07
 */
public class NettyEncoder2 extends MessageToByteEncoder<Object> {
    private static final Logger log = LoggerFactory.getLogger(NettyEncoder2.class);
    private final KryoSerializer kryoSerializer;
    private final Class<?> clazz;

    public  NettyEncoder2(Class<?> clazz , KryoSerializer kryoSerializer){
        this.kryoSerializer = kryoSerializer;
        this.clazz = clazz;
    }

    /**
     * 编码。将对象转成字节数据
     * @param ctx 上下文对象
     * @param msg 传输的信息
     */
    @Override
    protected void encode(ChannelHandlerContext ctx, Object msg, ByteBuf out) throws Exception {
        log.info("进入自定义编码器");
       if (clazz.isInstance(msg)){
           //序列化对象
           byte[] data = kryoSerializer.serialize(msg);
           //读取消息长度
           int dataLength = data.length;
           //写入消息的长度
           out.writeInt(dataLength);
           //将字节数组写入ByteBuf对象中
           out.writeBytes(data);
           log.info("消息成功序列化");
       }
    }
}
