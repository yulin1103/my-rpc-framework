package cglib2proxy;

/**
 * @author yulin
 * @create 2020-08-19 16:04
 */
public class MainTest {
    public static void main(String[] args) {
        Test t1 = new Test();
        Test proxyObject = (Test) CglibProxyFactory.getProxyObject(Test.class);
        System.out.println(proxyObject.print());
    }
}
