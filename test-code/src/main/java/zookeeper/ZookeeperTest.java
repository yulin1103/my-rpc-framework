package zookeeper;

import org.apache.log4j.Logger;
import org.apache.zookeeper.*;
import org.apache.zookeeper.data.Stat;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.util.List;

/**
 * @author yulin
 * @create 2020-08-20 18:08
 */
public class ZookeeperTest {
    private ZooKeeper zkClient;
    private static Logger log = org.apache.log4j.Logger.getLogger(ZooKeeper.class);

    /**
     * 创建Zookeeper客户端
     */
    @Before
    public void init() throws IOException {
        //超时时间，单位ms
        int sessionTimeout = 2000;
        //服务器
        String connectString = "127.0.0.1:2181";
        zkClient = new ZooKeeper(connectString, sessionTimeout, new Watcher() {
            @Override
            public void process(WatchedEvent watchedEvent) {
//                try {
//                    List<String> children = zkClient.getChildren("/", true);
//                    for (String str : children){
//                        log.info(str);
//                    }
//                } catch (KeeperException | InterruptedException e) {
//                    e.printStackTrace();
////                }
            }
        });
    }

    /**
     * 创建子节点
     */
    @Test
    public void createNode() throws KeeperException, InterruptedException {
        zkClient.create("/myTestNode/N2" , "the value of the N2".getBytes() , ZooDefs.Ids.OPEN_ACL_UNSAFE , CreateMode.PERSISTENT);
    }

    /**
     * 获取子节点，并监控节点的变化
     */
    @Test
    public void getDataAndWatch() throws KeeperException, InterruptedException {
        List<String> children = zkClient.getChildren("/", true);
        for (String str : children){
            log.info(str);
        }
        Thread.sleep(Long.MAX_VALUE);
    }

    /**
     * 判断节点是否存在
     */
    @Test
    public void isExist() throws KeeperException, InterruptedException {
        Stat stat = zkClient.exists("/dubbo", false);
        log.info(stat);
    }
}



















