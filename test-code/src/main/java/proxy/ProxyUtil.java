package proxy;

import sun.misc.ProxyGenerator;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

/**
 * 工具类。获取代理对象
 * @author yulin
 * @create 2020-08-17 15:04
 */
public class ProxyUtil {
    /**
     * 需要被代理的对象
     */
    private Object object;
    private Object result;

    public ProxyUtil(Object object) {
        this.object = object;
    }

    public Object getProxyInstance(){
        return Proxy.newProxyInstance(this.getClass().getClassLoader(), new Class[]{TestInterface.class}, new InvocationHandler() {
            @Override
            public Object invoke(Object proxy, Method method, Object[] args) {
                try {
                    System.out.println("前置通知");
                    result = method.invoke(object, args);
                    getProxyClass();
                    System.out.println("返回通知");
                    return result;
                } catch (IllegalAccessException | InvocationTargetException e) {
                    System.out.println("异常通知");
                    e.printStackTrace();
                } finally {
                    System.out.println("后置通知");
                }
                System.out.println("返回通知");
                return result;
            }
        });
    }

    /**
     * 获取自动生成的class文件
     */
    public void getProxyClass(){
        byte[] bytes = ProxyGenerator.generateProxyClass("TestInterface$proxy", new Class[]{TestInterface.class});
        OutputStream os = null;
        try {
            os = new FileOutputStream("test-code/t.class");
            os.write(bytes);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (os != null){
                try {
                    os.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
