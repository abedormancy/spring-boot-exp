package ga.uuid.repository;

import java.util.List;

import ga.uuid.entity.User;

/**
 * 自定义查询
 */
public interface UserRepositoryCustom {

	List<User> myQuery();
}
