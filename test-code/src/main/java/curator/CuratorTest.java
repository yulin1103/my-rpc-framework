package curator;

import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.cache.PathChildrenCache;
import org.apache.curator.framework.recipes.cache.PathChildrenCacheEvent;
import org.apache.curator.framework.recipes.cache.PathChildrenCacheListener;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.junit.Before;
import org.junit.Test;
import sun.rmi.runtime.Log;

import java.io.*;
import java.util.Arrays;
import java.util.List;

/**
 * 测试Curator
 * @author yulin
 * @create 2020-08-20 20:42
 */
public class CuratorTest {
    /**
     * 重试之间等待的初始时间
     */
    private static final int BASE_SLEEP_TIME = 1000;
    /**
     * 最大重试次数
     */
    private static final int MAX_RETRIES = 3;
    private CuratorFramework zkClient;

    /**
     * 创建Zookeeper客户端
     */
    @Before
    public void init(){
        RetryPolicy retryPolicy = new ExponentialBackoffRetry(BASE_SLEEP_TIME , MAX_RETRIES);
        zkClient = CuratorFrameworkFactory.builder()
                //要连接的服务器列表
                .connectString("127.0.0.1:2181")
                //重试策略
                .retryPolicy(retryPolicy)
                .build();
        zkClient.start();
    }

    /**
     * 创建节点。不指定内容时，默认为IP地址
     */
    @Test
    public void creatNode() throws Exception {
        //zkClient.create().forPath("/T3" , "new Node".getBytes());
        zkClient.create().creatingParentContainersIfNeeded().forPath("/n/n1");
    }

    /**
     * 删除节点
     */
    @Test
    public void deleteNode() throws Exception {
        zkClient.delete().forPath("/T3");
        //递归删除（删除所有节点）
        //zkClient.delete().deletingChildrenIfNeeded().forPath("/T3");
    }

    /**
     * 获取节点数据
     */
    @Test
    public void getData() throws Exception {
        byte[] bytes = zkClient.getData().forPath("/T3");
        String str = new String(bytes);
        System.out.println(str);
    }

    /**
     * 更新节点数据
     */
    @Test
    public void updateNode() throws Exception {
        zkClient.setData().forPath("/T3" , "update the value".getBytes());
    }

    /**
     * 获取某个节点所有子节点的路径
     */
    @Test
    public void getAllChildNodes() throws Exception {
        List<String> children = zkClient.getChildren().forPath("/");
        for (String childNode : children){
            System.out.println(childNode);
        }
    }

    /**
     * 监听器。监听某个节点的变化，这个节点的子节点发生增加、删除、修改等操作时，可以自定义回调操作。
     * PathChildrenCache子节点监听器只能监听子节点，监听不到当前节点
     * 不能递归监听，子节点下的子节点不能递归监控
     */
    @Test
    public void listener() throws Exception {
        String path = "/";
        PathChildrenCache pathChildrenCache = new PathChildrenCache(zkClient , path , true);
        PathChildrenCacheListener pathChildrenCacheListener = new PathChildrenCacheListener() {
            @Override
            public void childEvent(CuratorFramework curatorFramework, PathChildrenCacheEvent pathChildrenCacheEvent) throws Exception {
                PathChildrenCacheEvent.Type type = pathChildrenCacheEvent.getType();
                System.out.println("事件类型：" + type);
            }
        };
        pathChildrenCache.getListenable().addListener(pathChildrenCacheListener);
        pathChildrenCache.start();
        Thread.sleep(Long.MAX_VALUE);
    }
}













