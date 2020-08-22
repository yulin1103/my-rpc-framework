package netty.rpc;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;

/**
 * 自定义编码器，使用JDK的API，ObjectOutputStream 。将  对象 ---> byte[]
 * @author yulin
 * @create 2020-08-15 17:30
 */
public class NettyEncoder extends MessageToByteEncoder<Object> {
    private static final Logger log = LoggerFactory.getLogger(NettyEncoder.class);


    /**
     * 将对象转成字节数据，然后写入到ByteBuf对象中
     */
    @Override
    protected void encode(ChannelHandlerContext ctx, Object msg, ByteBuf out) {
        log.info("进入自定义编码器");
        log.info("msg = " + msg);
        if (msg instanceof RpcRequest){
            log.info("准备进行编码操作");
            // 1、将对象转换成byte[]
            RpcRequest rpcRequest = (RpcRequest) msg;
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            try {
                ObjectOutputStream oos = new ObjectOutputStream(bos);
                oos.writeObject(rpcRequest);
            } catch (IOException e) {
                log.info("编码失败");
                e.printStackTrace();
            }
            log.info("成功将对象转成字节数据");
            byte[] data = bos.toByteArray();
            // 2、读取消息长度
            int dataLength = data.length;
            // 3、写入消息的长度
            out.writeInt(dataLength);
            // 4、将字节数组写入ByteBuf对象中
            out.writeBytes(data);
            log.info("成功将字节数据写入ByteBuf");
        }
    }
}
