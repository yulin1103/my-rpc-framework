import java.util.concurrent.CompletableFuture;
import java.util.function.Supplier;

/**
 * @author yulin
 * @createTime 2020-08-30 20:45
 */
public class CompletableFutureTest {
    public static void main(String[] args) throws InterruptedException {
        // 创建异步执行任务:
        CompletableFuture<Double> cf = CompletableFuture.supplyAsync(new Supplier<Double>() {
            @Override
            public Double get() {
                return CompletableFutureTest.fetchPrice();
            }
        });
        // 如果执行成功:
        cf.thenAccept((result) -> {
            System.out.println("price: " + result);
        });
        // 如果执行异常:
        cf.exceptionally((e) -> {
            e.printStackTrace();
            return null;
        });
        System.out.println("等待结果：");
        // 主线程不要立刻结束，否则CompletableFuture默认使用的线程池会立刻关闭:
        Thread.sleep(5000);
    }

    static Double fetchPrice() {
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
        }
        if (Math.random() < 0.3) {
            throw new RuntimeException("fetch price failed!");
        }
        return 5 + Math.random() * 20;
    }
}
