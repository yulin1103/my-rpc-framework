[TOC]

# my-rpc-framework

## 1. 整体设计思路

* 服务端创建一个`Map<String , Object>  ` ` key：服务名称，value：服务对象。客户端的rpc请求中带有服务的名称，从Map中找到服务对象，调用相应的方法；
* Zookeeper做注册中心，节点名称就是服务的地址（IP:PORT）,父节点是服务名称。客户端通过服务名称拿到服务地址，之后建立TCP连接传输数据；
* 序列化rpc请求、rpc响应对象，让其能够在网络上传输。使用JDK的ObjectInputStream、ObjectOutputStream或者Kryo进行序列化；
* 使用Socket类或者Netty在客户端与服务端之间传输数据；
* 使用动态代理封装客户端的调用过程；

## 2. 服务端注册服务

