package club.rongyue.remoting.transport.serializer;

import club.rongyue.exception.SerializerException;
import club.rongyue.remoting.dto.RpcRequest;
import club.rongyue.remoting.dto.RpcResponse;
import club.rongyue.remoting.transport.Serializer;
import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.function.Supplier;

/**
 * 使用Kryo进行序列化、反序列化
 * @author yulin
 * @createTime 2020-08-31 7:59
 */
public class KryoSerializerImpl implements Serializer {
    private static final Logger logger = LoggerFactory.getLogger(KryoSerializerImpl.class);
    /**
     * Kryo是线程不安全的，使用ThreadLocal来存储变量
     */
    private final ThreadLocal<Kryo> kryoThreadLocal = ThreadLocal.withInitial(new Supplier<Kryo>() {
        @Override
        public Kryo get() {
            Kryo kryo = new Kryo();
            kryo.register(RpcRequest.class);
            kryo.register(RpcResponse.class);
            return kryo;
        }
    });

    @Override
    public byte[] serialize(Object obj) {
        try {
            //会在内存中创建一个字节数组缓冲区，所有字节数据将保存在缓冲区中
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            Output output = new Output(bos);
            Kryo kryo = kryoThreadLocal.get();
            //对象 ---> 字节数据
            kryo.writeObject(output , obj);
            //最好手动清除（有自动清除功能），以免内存溢出
            kryoThreadLocal.remove();
            return output.toBytes();
        } catch (Exception e) {
            throw new SerializerException("序列化对象抛出异常！！！");
        }
    }

    @Override
    public <T> T deserialize(byte[] bytes, Class<T> clazz) {
        try {
            //会在内存中创建一个字节数组缓冲区，所有字节数据将保存在缓冲区中
            ByteArrayInputStream bis = new ByteArrayInputStream(bytes);
            Input input = new Input(bis);
            Kryo kryo = kryoThreadLocal.get();
            // 字节数据 ---> 对象
            Object obj = kryo.readObject(input , clazz);
            kryoThreadLocal.remove();
            return clazz.cast(obj);
        } catch (Exception e) {
            throw new SerializerException("反序列化过程抛出异常！！！");
        }
    }
}
