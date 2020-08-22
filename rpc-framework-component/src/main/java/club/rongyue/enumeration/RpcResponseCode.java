package club.rongyue.enumeration;

/**
 * @author yulin
 * @createTime 2020-08-22 17:06
 */
public enum RpcResponseCode {
    SUCCESSS(200 , "调用成功"),
    FAILURE(500 , "调用失败");
    private final int code;
    private final String message;

    private RpcResponseCode(int code, String message) {
        this.code = code;
        this.message = message;
    }

    public int getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }

    @Override
    public String toString() {
        return "RpcResponseCode{" +
                "code=" + code +
                ", message='" + message + '\'' +
                '}';
    }
}
