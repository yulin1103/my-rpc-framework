package club.rongyue.exception;

/**
 * 序列化、反序列化过程抛出异常
 * @author yulin
 * @createTime 2020-08-31 8:17
 */
public class SerializerException extends RuntimeException{
    public SerializerException(String message){
        super(message);
    }
}
