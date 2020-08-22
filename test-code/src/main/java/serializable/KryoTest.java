package serializable;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import org.junit.Test;

import java.io.*;

/**
 * 使用kryo序列化对象
 *
 * @author yulin
 * @create 2020-08-16 16:17
 */
public class KryoTest {
    public static void main(String[] args) {
        Person2 p = new Person2(1, "tom", "女", 20);
        Kryo kryo = new Kryo();
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        Output output = new Output(bos);
        kryo.writeObject(output, p);
        output.close();
        byte[] bytes = bos.toByteArray();
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream("test-code/object2.txt");
            fos.write(bytes);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (fos != null) {
                try {
                    fos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        System.out.println(bytes.length);
    }

    /**
     * 使用kryo反序列化
     */
    @Test
    public void read() {
        InputStream fis = null;
        Input input = null;
        Person2 p2 = null;
        try {
            fis = new FileInputStream("object2.txt");
            Kryo kryo = new Kryo();
            input = new Input(fis);
            p2 = kryo.readObject(input, Person2.class);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } finally {
            if (fis != null) {
                try {
                    fis.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        input.close();
        System.out.println(p2);
    }

}
