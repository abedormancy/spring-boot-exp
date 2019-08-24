package ga.vabe.beetl.dao;


import ga.vabe.beetl.domain.User;
import org.beetl.sql.core.mapper.BaseMapper;
import org.springframework.stereotype.Repository;

@Repository
public interface UserDao extends BaseMapper<User> {

}
