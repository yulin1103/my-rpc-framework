package user.service;

import java.util.List;

/**
 * @author yulin
 * @createTime 2020-08-24 23:31
 */
public interface UserService {
    /**
     * 通过userId获取用户信息
     * @param userId 用户id
     */
    List<Object> getUser(int userId);
}
