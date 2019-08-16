package ga.vabe.redis.service;

import ga.vabe.redis.model.User;

import java.util.Collection;
import java.util.Optional;

public interface UserService {

    Collection<User> users();

    User add();

    boolean delete(String id);

    Optional<User> user(String id);

}
