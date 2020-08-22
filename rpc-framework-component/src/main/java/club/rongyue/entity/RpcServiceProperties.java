package club.rongyue.entity;

/**
 * 服务的属性。service + group + version 组成服务的全称
 * @author yulin
 * @createTime 2020-08-22 16:27
 */
public class RpcServiceProperties {
    /**
     * 默认值为空
     */
    private String version;
    /**
     * 用于区分同一个接口的不同实现类，默认值为空
     */
    private String group;
    /**
     * 服务的接口名
     */
    private String serviceName;

    /**
     * 返回服务的全称，默认值为服务接口名。即 this.service
     */
    public String toRpcServiceName(){
        return this.getServiceName() + this.getGroup() + this.getVersion();
    }

    public RpcServiceProperties() {
        this.group = "";
        this.version = "";
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getGroup() {
        return group;
    }

    public void setGroup(String group) {
        this.group = group;
    }

    public String getServiceName() {
        return serviceName;
    }

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }

    @Override
    public String toString() {
        return "RpcServiceProperties{" +
                "version='" + version + '\'' +
                ", group='" + group + '\'' +
                ", serviceName='" + serviceName + '\'' +
                '}';
    }
}
