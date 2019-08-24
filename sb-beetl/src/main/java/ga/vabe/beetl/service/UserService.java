package ga.vabe.beetl.service;

import ga.vabe.beetl.dao.UserDao;
import ga.vabe.beetl.domain.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    /**
     * 根据官网步骤注入 dao 失败
     */
    @Autowired
    private UserDao dao;

    public User select(Long id) {
        return dao.single(id);
    }

    public UserDao dao() {
        return dao;
    }
}
