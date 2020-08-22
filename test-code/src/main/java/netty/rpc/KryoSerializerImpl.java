package netty.rpc;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

/**
 * @author yulin
 * @create 2020-08-16 17:36
 */
public class KryoSerializerImpl implements KryoSerializer {
    /**
     * Kryo线程不安全，每个线程都应该有自己的Kryo、Input、Output实例
     */
    private static final ThreadLocal<Kryo> KRYO_THREAD_LOCAL = ThreadLocal.withInitial(()->{
        Kryo kryo = new Kryo();
        //将该类与一个 int 型的 ID 相关联
        kryo.register(RpcRequest.class);
        kryo.register(RpcResponse.class);
        //默认值为true。是否关闭注册行为，关闭之后可能存在序列化问题，一般推荐设置为true
        kryo.setReferences(true);
        //默认值为false。是否关闭循环引用，可以提供性能，但一般推荐设置为false
        kryo.setRegistrationRequired(false);
        return kryo;

    });

    /**
     *  序列化
     * @param obj 需要进行序列化的对象
     * @return 字节数组
     */
    @Override
    public byte[] serialize(Object obj) {
        try {
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            Output output = new Output(bos);
            Kryo kryo = KRYO_THREAD_LOCAL.get();
            kryo.writeObject(output , obj);
            KRYO_THREAD_LOCAL.remove();
            return output.toBytes();
        } catch (Exception e) {
            throw new RuntimeException("序列化失败");
        }
    }

    /**
     *  反序列化
     * @param bytes 需要进行反系列化的字节数组
     * @param clazz 返回值的Class对象
     * @param <T> 返回值类型
     * @return 返回一个对象
     */
    @Override
    public <T> T deserialize(byte[] bytes, Class<T> clazz) {
        try {
            ByteArrayInputStream bis = new ByteArrayInputStream(bytes);
            Input input = new Input(bis);
            Kryo kryo = KRYO_THREAD_LOCAL.get();
            Object obj = kryo.readObject(input , clazz);
            KRYO_THREAD_LOCAL.remove();
            return clazz.cast(obj);
        } catch (Exception e) {
            throw new RuntimeException("反序列化失败");
        }
    }
}
