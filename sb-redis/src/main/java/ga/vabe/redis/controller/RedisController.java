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
@RequestMapping("user")
public class RedisController {

    @Autowired
    private UserService userService;

    @RequestMapping(produces = MediaType.TEXT_PLAIN_VALUE)
    public String index() {
        return userService.users().stream().map(User::toString).collect(Collectors.joining("\n"));
    }

    @RequestMapping(value = "add", produces = MediaType.TEXT_PLAIN_VALUE)
    public String add() {
        return userService.add().toString();
    }

    @RequestMapping(value = "{id}", produces = MediaType.TEXT_PLAIN_VALUE)
    public String user(@PathVariable("id") String id) {
        return userService.user(id).orElseGet(User::empty).toString();
    }

    @RequestMapping(value = "delete/{id}", produces = MediaType.TEXT_PLAIN_VALUE)
    public String delete(@PathVariable("id") String id) {
        return String.valueOf(userService.delete(id));
    }

}
