package serializable;

import club.rongyue.remoting.dto.RpcRequest;
import org.junit.Test;
import user.service.pojo.User;

import java.io.*;
import java.util.UUID;

/**
 * JDK自带的序列化方式
 * @author yulin
 * @create 2020-08-15 14:48
 */
public class JdkTest {
    public static void main(String[] args) {
        RpcRequest rpcRequest = new RpcRequest();
        rpcRequest.setRequestId(UUID.randomUUID().toString());
        rpcRequest.setInterfaceName("接口名");
        rpcRequest.setMethodName("方法名");
        rpcRequest.setGroup("rpcServiceProperties.getGroup()");
        rpcRequest.setVersion("setVersion");
        User u1 = new User(1 , "tom");
        Person p = new Person(1 , "tom" , "男" , 20);
        File file = new File("test-code/object1.txt");
        FileOutputStream fos = null;
        ObjectOutputStream oos = null;
        try {
            fos = new FileOutputStream(file);
            oos = new ObjectOutputStream(fos);
            oos.writeObject(rpcRequest);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (oos != null){
                try {
                    oos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @Test
    public void readObject(){
        File file = new File("object1.txt");
        ObjectInputStream ois = null;
        try {
            ois = new ObjectInputStream(new FileInputStream(file));
            //Person p = (Person) ois.readObject();
            //User u = (User) ois.readObject();
            RpcRequest rpcRequest = (RpcRequest) ois.readObject();
            System.out.println("反序列化获取的对象：" + rpcRequest);
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        } finally {
            if (ois != null){
                try {
                    ois.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
