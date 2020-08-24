package club.rongyue.utils.concurrent.threadpool;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.concurrent.*;

/**
 * 用于创建线程池的工具类
 *
 * @author yulin
 * @createTime 2020-08-22 21:59
 */
public class ThreadPoolFactoryUtils {
    private static final Logger logger = LoggerFactory.getLogger(ThreadPoolFactoryUtils.class);
    /**
     * 管理不同的线程池
     * key：线程池的名称（对用各个业务场景）
     * value：ThreadPool
     */
    private static final Map<String, ExecutorService> THREAD_POOLS_MAP = new ConcurrentHashMap<String, ExecutorService>();

    public ThreadPoolFactoryUtils() {
    }

    /**
     * 使用默认线程池配置参数创建线程池
     */
    public static ExecutorService createCustomThreadPool(String threadNamePrefix) {
        //默认不是守护线程
        return createThreadPool(new CustomThreadPoolConfig(), threadNamePrefix, false);

    }

    /**
     * 指定线程池配置参数创建线程池
     */
    public static ExecutorService createCustomThreadPool(CustomThreadPoolConfig customThreadPoolConfig,
                                                         String threadNamePrefix) {
        //不是守护线程
        return createThreadPool(customThreadPoolConfig, threadNamePrefix, false);
    }

    /**
     * 真正创建线程池的方法
     *
     * @param customThreadPoolConfig 配置参数
     * @param threadNamePrefix       线程名前缀
     * @param isDaemon               是否为Daemon Thread（守护线程）
     * @return 线程池对象
     */
    private static ExecutorService createThreadPool(CustomThreadPoolConfig customThreadPoolConfig,
                                                    String threadNamePrefix,
                                                    Boolean isDaemon) {
        ExecutorService threadPool = THREAD_POOLS_MAP.get(threadNamePrefix);
        //不存在这个线程池，则新建一个
        if (threadPool == null) {
            threadPool = new ThreadPoolExecutor(customThreadPoolConfig.getCorePoolSize(), customThreadPoolConfig.getMaximumPoolSize(),
                    customThreadPoolConfig.getKeepAliveTime(), customThreadPoolConfig.getUnit(),
                    customThreadPoolConfig.getWorkQueue(), createThreadFactory(threadNamePrefix, isDaemon));
            THREAD_POOLS_MAP.put(threadNamePrefix, threadPool);
        }
        return threadPool;
    }

    /**
     * 创建一个线程工厂，负责生产线程。可配置线程的一些属性（如线程名等）
     *
     * @param threadNamePrefix 线程名前缀
     * @param isDaemon         指定是否为Daemon Thread（守护线程）
     */
    private static ThreadFactory createThreadFactory(String threadNamePrefix, Boolean isDaemon) {
        if (threadNamePrefix != null) {
            if (isDaemon != null) {
                //google的jre包
                return new ThreadFactoryBuilder().setNameFormat(threadNamePrefix + "-%d")
                        .setDaemon(isDaemon).build();
            } else {
                return new ThreadFactoryBuilder().setNameFormat(threadNamePrefix + "-%d").build();
            }
        }
        //threadNamePrefix为空，使用默认的ThreadFactory
        return Executors.defaultThreadFactory();
    }

    /**
     * 打印线程池状态
     *
     * @param threadPool 线程池对象
     */
    public static void printThreadPoolStatus(final ThreadPoolExecutor threadPool) {
        //定时周期执行指定的任务
        ScheduledExecutorService scheduledExecutorService = new ScheduledThreadPoolExecutor(1, createThreadFactory("print-thread-pool-status", false));
        scheduledExecutorService.scheduleAtFixedRate(new Runnable() {
            @Override
            public void run() {
                logger.info("-------------ThreadPool Status-------------");
                logger.info("ThreadPool Size: [{}]", threadPool.getPoolSize());
                logger.info("Active Threads: [{}]", threadPool.getActiveCount());
                logger.info("Number of Tasks: [{}]", threadPool.getCompletedTaskCount());
                logger.info("Number of Tasks in Queue: [{}]", threadPool.getQueue().size());
                logger.info("--------------------------------------------");
            }
        }, 0, 1, TimeUnit.SECONDS);
    }
}
















