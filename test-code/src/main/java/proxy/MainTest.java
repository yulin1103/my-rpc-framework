package proxy;

/**
 * @author yulin
 * @create 2020-08-19 15:28
 */
public class MainTest {
    public static void main(String[] args) {
        TestInterface test1 = new TestInterfaceImpl();
        ProxyUtil proxyUtil = new ProxyUtil(test1);
        TestInterface proxyObject = (TestInterface) proxyUtil.getProxyInstance();
        System.out.println(proxyObject.print());
    }
}
