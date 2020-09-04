package ga.vabe.test.controller;

import ga.vabe.test.entity.UserInfo;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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

}
