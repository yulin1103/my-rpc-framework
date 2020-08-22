package netty.rpc;

/**
 * 自定义序列化、反序列化接口
 * @author yulin
 * @create 2020-08-16 17:28
 */
public interface KryoSerializer {
    /**
     * 序列化方法
     * @param obj 需要进行序列化的对象
     * @return 返回字节数组
     */
    byte[] serialize(Object obj);

    /**
     * 反序列化
     * @param bytes 需要进行反系列化的字节数组
     * @param clazz 返回值的Class对象
     * @param <T> 返回值类型
     * @return 返回一个T类型的对象
     */
    <T> T deserialize(byte[] bytes , Class<T> clazz);
}
