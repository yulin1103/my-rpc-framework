package cglib2proxy;

import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;

import java.lang.reflect.Method;

/**
 * 方法拦截器。
 * @author yulin
 * @create 2020-08-19 15:46
 */
public class MethodInterceptorImpl implements MethodInterceptor {
    /**
     *
     * @param o 被代理的对象
     * @param method 需要拦截的方法
     * @param objects 方法参数
     * @param methodProxy 用于调用原始方法
     * @return 调用原始方法的返回值
     */
    @Override
    public Object intercept(Object o, Method method, Object[] objects, MethodProxy methodProxy){
        Object result = null;
        System.out.println("前置通知CGLib");
        try {
            result = methodProxy.invokeSuper(o , objects);
            System.out.println("返回通知CGLib");
            return result;
        } catch (Throwable throwable) {
            System.out.println("异常通知CGLib");
            throwable.printStackTrace();
        } finally {
            System.out.println("后置通知CGLib");
        }
        System.out.println("返回通知CGLib");
        return result;
    }
}
