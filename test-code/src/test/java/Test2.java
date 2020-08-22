import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * @author yulin
 * @createTime 2020-08-22 8:38
 */
public class Test2 {
    public static void main(String[] args) {
        Father f = new Son();
        List c = new ArrayList();
        f.fun2(c);
    }
}
class Father{
    public void fun(){
        System.out.println("father");
    }

    public void fun2(Collection<String> collections ){
        System.out.println("collections");
    }
    public void fun2(List<String> list){
        System.out.println("list");
    }
}
class Son extends Father{
    public void fun(){
        System.out.println("son");
    }
}
