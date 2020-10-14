package ga.vabe.test.controller;

import ga.vabe.test.annotation.OperationLog;
import ga.vabe.test.entity.UserInfo;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

@RestController
public class UserInfoController {

    @OperationLog(value = "这是一个SPEL表达式 $(#userInfo?.clientIp),---- $(#test), ---- $(#result.userAgent) ")
    @RequestMapping(value = "/user")
    public UserInfo userInfo(HttpServletRequest request, @RequestBody(required = false) UserInfo userInfo,
                             @RequestParam String test) {
        if (userInfo == null) {
            userInfo = new UserInfo();
        }
        userInfo.setClientIp(request.getRemoteAddr());
        userInfo.setRequestURL(request.getRequestURL().toString());
        String header = request.getHeader("user-agent");
        userInfo.setUserAgent(StringUtils.isEmpty(header) ? "unknown" : header);
        return userInfo;
    }

    @GetMapping(value = "/user/{capacity}")
    public UserInfo userInfo(HttpServletRequest request, @PathVariable int capacity) {
        UserInfo userInfo = userInfo(request, null, null);
        userInfo.setUserAgent(String.valueOf(capacity));
        return userInfo;
    }

}
