package ga.vabe.test.controller;

import ga.vabe.test.entity.UserInfo;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

@RestController
public class UserInfoController {

    @RequestMapping(value = "/user")
    public UserInfo userInfo(HttpServletRequest request) {
        UserInfo userInfo = new UserInfo();
        userInfo.setClientIp(request.getRemoteAddr());
        userInfo.setRequestURL(request.getRequestURL().toString());
        userInfo.setUserAgent(request.getHeader("user-agent"));
        return userInfo;
    }

    @GetMapping(value = "/user/{capacity}")
    public UserInfo userInfo(HttpServletRequest request, @PathVariable int capacity) {
        UserInfo userInfo = userInfo(request);
        userInfo.setUserAgent(String.valueOf(capacity));
        return userInfo;
    }


}
