package club.rongyue.utils;

import club.rongyue.exception.RpcException;
import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.cache.PathChildrenCache;
import org.apache.curator.framework.recipes.cache.PathChildrenCacheEvent;
import org.apache.curator.framework.recipes.cache.PathChildrenCacheListener;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.zookeeper.CreateMode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;

/**
 * Curator（Zookeeper客户端）
 * @author yulin
 * @createTime 2020-08-24 19:42
 */
public class CuratorUtils {
    private static final Logger logger = LoggerFactory.getLogger(CuratorUtils.class);
    /**
     * 两次重试之间的等待时间（initial amount of time to wait between retries）
     */
    private static final int BASE_SLEEP_TIME = 1000;
    /**
     * 最大重连次数（max number of times to retry）
     */
    private static final int MAX_RETIES = 3;
    public static final String ZK_PERSISTENT_NODE_ROOT_PATH = "/my-rpc";
    /**
     * 缓存服务的所有子节点（即所有提供该服务的服务器的地址IP:PORT）
     * key: 服务名称
     * value: 地址（IP:PORT）
     */
    private static final Map<String , List<String>> SERVICE_ADDRESS_MAP = new ConcurrentHashMap<String, List<String>>();
    /**
     * 缓存已经注册的服务路径
     * value: 服务路径（ /my-rpc/serviceName/127.0.0.1:**** ）
     */
    private static final Set<String> REGISTERED_PATH_SET = ConcurrentHashMap.newKeySet();
    private static CuratorFramework zkClient;
    private static final String DEFAULT_ZOOKEEPER_ADDRESS = "127.0.0.1:2181";


    public CuratorUtils(){

    }

    /**
     * 获取Zookeeper客户端对象
     */
    public static CuratorFramework getZkClient(){
        //zookeeper客户端已经创建并启动，直接返回
        if (zkClient != null){
            return zkClient;
        }
        RetryPolicy retryPolicy = new ExponentialBackoffRetry(BASE_SLEEP_TIME , MAX_RETIES);
        zkClient = CuratorFrameworkFactory.builder()
                .connectString(DEFAULT_ZOOKEEPER_ADDRESS)
                .retryPolicy(retryPolicy)
                .build();
        zkClient.start();
        return zkClient;
    }

    /**
     * 创建持久的节点
     * @param zkClient CuratorFramework对象
     * @param path 需要注册的节点路径（/my-rpc/serviceName/127.0.0.1:**** ）。节点名称就是地址，父节点名称就是服务名称
     */
    public static void createPersistentNode(CuratorFramework zkClient , String path){
        try {
            if (REGISTERED_PATH_SET.contains(path) || zkClient.checkExists().forPath(path) != null){
                logger.info(path + ":节点已经存在,不要重复注册");
            }else {
                zkClient.create().creatingParentContainersIfNeeded().withMode(CreateMode.PERSISTENT).forPath(path);
                logger.info(path + ":节点注册成功");
                REGISTERED_PATH_SET.add(path);
            }
        } catch (Exception e) {
            throw new RpcException(e.getMessage() , e.getCause());
        }
    }

    /**
     * 获取服务的所有子节点（所有提供该服务的服务器的地址 IP:PORT）
     * @param zkClient zookeeper客户端
     * @param rpcServiceName 服务名称
     */
    public static List<String> getChildrenNodes(CuratorFramework zkClient , String rpcServiceName){
        List<String> serviceAddressList = SERVICE_ADDRESS_MAP.get(rpcServiceName);
        try {
            //缓存中存在
            if (serviceAddressList != null){
                return SERVICE_ADDRESS_MAP.get(rpcServiceName);
            }
            // servicePath == "/my-rpc/serviceName"
            String servicePath = ZK_PERSISTENT_NODE_ROOT_PATH + "/" + rpcServiceName;
            //缓存不存在，从zookeeper获取。并保存到缓存中
            serviceAddressList = zkClient.getChildren().forPath(servicePath);
            SERVICE_ADDRESS_MAP.put(rpcServiceName , serviceAddressList);
            //监听子节点的变化
            registerWatcher(rpcServiceName , zkClient);
        } catch (Exception e) {
            throw new RpcException(e.getMessage() , e.getCause());
        }
        return serviceAddressList;
    }

    /**
     * 注册监听器，监听子节点的变化。当子节点改变时，相应更新缓存SERVICE_ADDRESS_MAP中的数据
     * @param rpcServiceName 需要监听节点的名称（父节点的名称）
     * @param zkClient zookeeper客户端
     */
    private static void registerWatcher(String rpcServiceName , CuratorFramework zkClient){
        //监听节点路径
        String servicePath = ZK_PERSISTENT_NODE_ROOT_PATH + "/" + rpcServiceName;
        PathChildrenCache pathChildrenCache = new PathChildrenCache(zkClient , servicePath , true);
        PathChildrenCacheListener pathChildrenCacheListener = new PathChildrenCacheListener() {
            @Override
            public void childEvent(CuratorFramework curatorFramework, PathChildrenCacheEvent event) throws Exception {
                List<String> serviceAddressList = curatorFramework.getChildren().forPath(servicePath);
                SERVICE_ADDRESS_MAP.put(rpcServiceName , serviceAddressList);
            }
        };
        pathChildrenCache.getListenable().addListener(pathChildrenCacheListener);
        try {
            pathChildrenCache.start();
        } catch (Exception e) {
            throw new RpcException(e.getMessage() , e.getCause());
        }
    }

    /**
     * 删除节点（每次启动服务端时删除注册中心的旧数据）
     */
    public static void clearRegisteredService(CuratorFramework zkClient){
        //对于Set中的元素的操作都会以并行的方式执行
        REGISTERED_PATH_SET.stream().parallel().forEach(new Consumer<String>() {
            @Override
            public void accept(String path) {
                try {
                    zkClient.delete().forPath(path);
                } catch (Exception e) {
                    throw new RpcException(e.getMessage() , e.getCause());
                }
            }
        });
        logger.info("注册中心的所有服务都已经清除" + REGISTERED_PATH_SET.toString());
    }
}
