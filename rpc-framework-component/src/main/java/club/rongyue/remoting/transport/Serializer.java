package club.rongyue.remoting.transport;

/**
 * 序列化、反序列化接口。
 * @author yulin
 * @createTime 2020-08-31 7:55
 */
public interface Serializer {

    /**
     * 序列化
     * @param obj 需要序列化的对象
     * @return 字节数组
     */
    byte[] serialize(Object obj);

    /**
     * 反序列化
     * @param bytes 需要反序列化的字节数据
     * @param clazz 目标对象的Class对象
     * @param <T> 目标对象的类型
     * @return 目标对象
     */
    <T> T deserialize(byte[] bytes , Class<T> clazz);
}
