package club.rongyue.utils.factories;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

/**
 * 单例对象工厂类，使用Map维护系统的所有单例对象
 * @author yulin
 * @createTime 2020-08-23 21:37
 */
public final class SingletonFactory {
    private static final Logger logger  = LoggerFactory.getLogger(SingletonFactory.class);
    /**
     * 维护系统的所有单例对象
     * key: Class对象名称
     * value: 单例对象
     */
    private static final Map<String , Object> OBJECT_MAP = new HashMap<>();

    private SingletonFactory(){

    }

    /**
     * 单例模式，双重检查
     * @param clazz Class对象
     * @param <T> 泛型
     * @return T ， 传进来什么类型，就返回什么类型
     */
    public static <T> T getInstance(Class<T> clazz){
        String className = clazz.toString();
        logger.info("类名：" + className);
        Object instance = OBJECT_MAP.get(className);
        if (instance == null){
            synchronized (clazz){
                if (instance == null){
                    try {
                        instance = clazz.newInstance();
                        OBJECT_MAP.put(className , instance);
                    } catch (InstantiationException | IllegalAccessException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        return clazz.cast(instance);
    }
}
