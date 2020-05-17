package ga.vabe.redis.service;

import ga.vabe.redis.model.User;

import java.util.Collection;
import java.util.Optional;

/**
 * <b>description:</b><br/>
 * user service
 * @author Abe
 */
public interface UserService {

    /**
     * get all users
     * @return all users
     */
    Collection<User> users();

    /**
     * add user ( random
     * @return the added user
     */
    User add();

    /**
     * delete user by id
     * @param id user id
     * @return true : success | false : fail
     */
    boolean delete(String id);

    /**
     * get user by id
     * @param id user id
     * @return Optional user
     */
    Optional<User> user(String id);

}
