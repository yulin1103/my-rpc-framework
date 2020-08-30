import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

/**
 * @author yulin
 * @createTime 2020-08-30 21:34
 */
public class CompletableFutureTest2 {
    public CompletableFuture<String> send(){
        //使用参构造函数简单的创建 CompletableFuture
        return new CompletableFuture<>();
    }

    public void complete(CompletableFuture<String> completableFuture){
        //使用 CompletableFuture.complete() 手工的完成一个 Future
        try {
            Thread.sleep(3500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        completableFuture.complete("完成");
    }

    public static void main(String[] args) throws ExecutionException, InterruptedException {
        CompletableFutureTest2 completableFutureTest2 = new CompletableFutureTest2();
        CompletableFuture<String> future = completableFutureTest2.send();
        System.out.println("等待结果");
        completableFutureTest2.complete(future);
        //get() 方法会一直阻塞直到 Future 完成
        String str = future.get();
        System.out.println(str);
    }
}
