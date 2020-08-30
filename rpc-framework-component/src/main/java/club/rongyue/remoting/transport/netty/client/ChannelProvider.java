package club.rongyue.remoting.transport.netty.client;

import club.rongyue.utils.factories.SingletonFactory;
import io.netty.channel.Channel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 使用Map保存Channel，对外提供get()方法获取Channel
 * @author yulin
 * @createTime 2020-08-30 21:50
 */
public class ChannelProvider {
    private static final Logger logger = LoggerFactory.getLogger(ChannelProvider.class);
    /**
     * key: IP:PORT
     * value: Channel对象
     */
    private final Map<String , Channel> channelMap;
    private final NettyClient nettyClient;

    public ChannelProvider(){
        channelMap = new ConcurrentHashMap<>();
        nettyClient = SingletonFactory.getInstance(NettyClient.class);
    }

    /**
     * 获取Channel对象
     * @param inetSocketAddress  服务端地址
     */
    public Channel getChannel(InetSocketAddress inetSocketAddress){
        //判断channelMap是否已经存在
        String key = inetSocketAddress.toString();
        if (channelMap.containsKey(key)){
            Channel channel = channelMap.get(key);
            if (channel != null && channel.isActive()){
                return channel;
            }else {
                channelMap.remove(key);
            }
        }
        //不存在则新建一个并保存到channelMap中
        Channel channel = nettyClient.doConnect(inetSocketAddress);
        channelMap.put(key , channel);
        return channel;
    }
}
