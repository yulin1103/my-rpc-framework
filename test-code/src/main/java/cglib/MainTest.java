package cglib;

import net.sf.cglib.core.DebuggingClassWriter;

/**
 * @author yulin
 * @create 2020-08-19 16:04
 */
public class MainTest {
    public static void main(String[] args) {
        //保存生成的代理对象
        System.setProperty(DebuggingClassWriter.DEBUG_LOCATION_PROPERTY, "F:\\idea_project2\\my-rpc-framework\\test-code");
        Test t1 = new Test();
        Test proxyObject = (Test) CglibProxyFactory.getProxyObject(Test.class);
        System.out.println(proxyObject.print());
    }
}
