package ga.vabe.redis.controller;

import ga.vabe.redis.model.User;
import ga.vabe.redis.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.stream.Collectors;

/**
 * <b>description:</b><br/>
 *  redis controller
 * @author Abe
 */
@RestController
public class RedisController {

    @Autowired
    private UserService userService;

    @RequestMapping(value = "/add", produces = MediaType.TEXT_PLAIN_VALUE)
    public String add() {
        return userService.add().toString();
    }

    @RequestMapping(value = "/", produces = MediaType.TEXT_PLAIN_VALUE)
    public String index() {
        return userService.users().stream().map(User::toString).collect(Collectors.joining("\n"));
    }

    @RequestMapping(value = "/user/{id}", produces = MediaType.TEXT_PLAIN_VALUE)
    public String user(@PathVariable("id") String id) {
        return userService.user(id).orElse(User.EMPTY).toString();
    }

    @RequestMapping(value = "/user/delete/{id}", produces = MediaType.TEXT_PLAIN_VALUE)
    public String delete(@PathVariable("id") String id) {
        return String.valueOf(userService.delete(id));
    }

}
