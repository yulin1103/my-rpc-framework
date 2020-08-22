package netty.rpc;


/**
 * @author yulin
 * @create 2020-08-15 18:16
 */
public class ClientDemo {
    public static void main(String[] args) {
        NettyClient client = new NettyClient("127.0.0.1", 9999);
        RpcRequest rpcRequest = new RpcRequest();
        rpcRequest.setInterfaceName("Order");
        rpcRequest.setMethodName("getOnePageOrder");
        client.sendMessage(rpcRequest);
    }
}
