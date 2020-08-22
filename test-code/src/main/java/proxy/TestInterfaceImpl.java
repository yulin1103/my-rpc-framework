package proxy;

/**
 * 测试JDK动态代理Demo，真实主题类
 * @author yulin
 * @create 2020-08-17 15:02
 */
public class TestInterfaceImpl implements TestInterface{
    @Override
    public String print() {
        return "I am real Object";
    }
}
