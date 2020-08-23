package club.rongyue.utils.concurrent.threadpool;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;

/**
 * 线程池的配置参数
 * @author yulin
 * @createTime 2020-08-23 15:59
 */
public class CustomThreadPoolConfig {
    /**
     * 默认参数
     */
    private static final int DEFAULT_CORE_POOL_SIZE = 10;
    private static final int DEFAULT_MAXIMUM_POOL_SIZE = 100;
    private static final int DEFAULT_KEEP_ALIVE_TIME = 1;
    private static final TimeUnit DEFAULT_TIME_UNIT = TimeUnit.MINUTES;
    private static final int DEFAULT_BLOCKING_QUEUE_CAPACITY = 100;
    private static final int BLOCKING_QUEUE_CAPACITY = 100;

    /**
     * 可配置参数
     */
    private int corePoolSize = DEFAULT_CORE_POOL_SIZE;
    private int maximumPoolSize = DEFAULT_MAXIMUM_POOL_SIZE;
    private long keepAliveTime = DEFAULT_KEEP_ALIVE_TIME;
    private TimeUnit unit = DEFAULT_TIME_UNIT;

    /**
     * 使用有界队列
     */
    private BlockingQueue<Runnable> workQueue = new ArrayBlockingQueue<Runnable>(BLOCKING_QUEUE_CAPACITY);

    public CustomThreadPoolConfig() {
    }

    public BlockingQueue<Runnable> getWorkQueue() {
        return workQueue;
    }

    public void setWorkQueue(BlockingQueue<Runnable> workQueue) {
        this.workQueue = workQueue;
    }

    public static int getDefaultCorePoolSize() {
        return DEFAULT_CORE_POOL_SIZE;
    }

    public static int getDefaultMaximumPoolSize() {
        return DEFAULT_MAXIMUM_POOL_SIZE;
    }

    public static int getDefaultKeepAliveTime() {
        return DEFAULT_KEEP_ALIVE_TIME;
    }

    public static TimeUnit getDefaultTimeUnit() {
        return DEFAULT_TIME_UNIT;
    }

    public static int getDefaultBlockingQueueCapacity() {
        return DEFAULT_BLOCKING_QUEUE_CAPACITY;
    }

    public static int getBlockingQueueCapacity() {
        return BLOCKING_QUEUE_CAPACITY;
    }

    public int getCorePoolSize() {
        return corePoolSize;
    }

    public void setCorePoolSize(int corePoolSize) {
        this.corePoolSize = corePoolSize;
    }

    public int getMaximumPoolSize() {
        return maximumPoolSize;
    }

    public void setMaximumPoolSize(int maximumPoolSize) {
        this.maximumPoolSize = maximumPoolSize;
    }

    public long getKeepAliveTime() {
        return keepAliveTime;
    }

    public void setKeepAliveTime(long keepAliveTime) {
        this.keepAliveTime = keepAliveTime;
    }

    public TimeUnit getUnit() {
        return unit;
    }

    public void setUnit(TimeUnit unit) {
        this.unit = unit;
    }
}
