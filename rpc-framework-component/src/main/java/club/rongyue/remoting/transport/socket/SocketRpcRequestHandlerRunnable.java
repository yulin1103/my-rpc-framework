package club.rongyue.remoting.transport.socket;

import club.rongyue.remoting.dto.RpcRequest;
import club.rongyue.remoting.dto.RpcResponse;
import club.rongyue.remoting.handler.RpcRequestHandler;
import club.rongyue.utils.factories.SingletonFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

/**
 * 处理客户端请求
 * @author yulin
 * @createTime 2020-08-22 21:24
 */
public class SocketRpcRequestHandlerRunnable implements Runnable{
    private static final Logger logger = LoggerFactory.getLogger(SocketRpcRequestHandlerRunnable.class);
    /**
     * 接收客户端传输过来的数据
     */
    private final Socket socket;
    /**
     * 处理客户端的请求
     */
    private final RpcRequestHandler rpcRequestHandler;

    public SocketRpcRequestHandlerRunnable(Socket socket){
        this.socket = socket;
        this.rpcRequestHandler = SingletonFactory.getInstance(RpcRequestHandler.class);
    }

    @Override
    public void run() {
        logger.info(Thread.currentThread().getName() + " 线程处理客户端的请求");
        ObjectInputStream ois = null;
        ObjectOutputStream oos = null;
        try {
            ois = new ObjectInputStream(socket.getInputStream());
            oos = new ObjectOutputStream(socket.getOutputStream());
            RpcRequest rpcRequest = (RpcRequest) ois.readObject();
            //处理客户端的请求，并返回结果
            Object result = rpcRequestHandler.handle(rpcRequest);
            logger.info("服务端返回给客户端的结果：[{}]" , result);
            //将处理结果传输回客户端
            oos.writeObject(RpcResponse.success(result , rpcRequest.getRequestId()));
            logger.info("服务端成功将结果通过Socket返回");
            oos.flush();
        } catch (IOException | ClassNotFoundException e) {
            logger.info("SocketRpcRequestHandlerRunnable抛出异常");
            e.printStackTrace();
        }finally {
            if (oos != null){
                try {
                    oos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (ois != null){
                try {
                    ois.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (socket != null){
                try {
                    logger.info("调用socket.close()，关闭连接");
                    socket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
