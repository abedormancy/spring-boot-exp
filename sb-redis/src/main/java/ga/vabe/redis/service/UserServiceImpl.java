package ga.vabe.redis.service;

import ga.vabe.common.IdGenerator;
import ga.vabe.redis.model.User;
import io.netty.util.internal.ThreadLocalRandom;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

/**
 * <b>description:</b><br/>
 * user service impl
 * @author Abe
 */
@Service
@Slf4j
public class UserServiceImpl implements UserService {

    {
        log.info("初始化 UserServiceImpl...");
        try {
            Thread.sleep(10000L);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        log.info("初始化 UserServiceImpl 完毕");
    }

    private IdGenerator idGen = IdGenerator.instance();

    private ConcurrentHashMap<String, User> userMap = new ConcurrentHashMap<>();

    @Override
    @Cacheable("10")
    public Collection<User> users() {
        log.info("invoke method users");
        return userMap.values();
    }

    @Override
    @CacheEvict("10")
    public User add() {
        log.info("invoke method add");
        User user = new User();
        user.setId(idGen.hexId());
        user.setName("Abe" + ThreadLocalRandom.current().nextInt(1000, 10000));
        user.setAge(ThreadLocalRandom.current().nextInt(1, 30));
        userMap.put(user.getId(), user);
        return user;
    }

    @Override
    @CacheEvict("10")
    public boolean delete(String id) {
        log.info("invoke method delete");
        return userMap.remove(id) != null;
    }

    @Override
    @Cacheable("20")
    public Optional<User> user(String id) {
        log.info("invoke method user");
        return Optional.ofNullable(userMap.get(id));
    }

}
