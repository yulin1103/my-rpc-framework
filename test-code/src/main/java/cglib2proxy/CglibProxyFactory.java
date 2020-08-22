package cglib2proxy;

import net.sf.cglib.proxy.Enhancer;

/**
 * 用于获取代理对象
 * @author yulin
 * @create 2020-08-19 15:53
 */
public class CglibProxyFactory {
    public static Object getProxyObject(Class<?> clazz){
        //创建动态代理增强类对象
        Enhancer enhancer = new Enhancer();
        //设置类加载器
        enhancer.setClassLoader(clazz.getClassLoader());
        //设置被代理的类
        enhancer.setSuperclass(clazz);
        //设置方法拦截器
        enhancer.setCallback(new MethodInterceptorImpl());
        //创建代理类对象
        return enhancer.create();
    }
}
