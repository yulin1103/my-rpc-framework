package user.service.impl;

import pojo.User;
import user.service.UserService;

import java.util.ArrayList;
import java.util.List;

/**
 * @author yulin
 * @createTime 2020-08-24 23:33
 */
public class UserServiceImpl implements UserService {
    public List<Object> getUser(int userId) {
        User u1 = new User(1 , "tom");
        User u2 = new User(2 , "jack");
        User u3 = new User(3 , "Bob");
        List<Object> list = new ArrayList<Object>();
        list.add(u1);
        list.add(u2);
        list.add(u3);
        return list;
    }
}
