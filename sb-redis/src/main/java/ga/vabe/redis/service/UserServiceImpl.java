package ga.vabe.redis.service;

import ga.vabe.common.IdGenerator;
import ga.vabe.redis.model.User;
import io.netty.util.internal.ThreadLocalRandom;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

/**
 * <b>description:</b><br/>
 * user service impl
 *
 * @author Abe
 */
@Service
@Slf4j
public class UserServiceImpl implements UserService {

    private IdGenerator idGen = IdGenerator.instance();

    private ConcurrentHashMap<String, User> userMap = new ConcurrentHashMap<>();

    @Override
    @Cacheable(cacheNames = "60")
    public List<User> users() {
        log.info("invoke method users");
        return new ArrayList<>(userMap.values());
    }

    /**
     * allEntries 忽略key，清除该缓存中的所有元素
     */
    @Override
    @CacheEvict(cacheNames = "60", allEntries = true)
    public User add() {
        log.info("invoke method add");
        User user = new User();
        user.setId(idGen.hexId());
        user.setName("Abe_" + LocalDateTime.now());
        user.setAge(ThreadLocalRandom.current().nextInt(1, 31));
        userMap.put(user.getId(), user);
        return user;
    }

    @Override
    @CacheEvict("60")
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
