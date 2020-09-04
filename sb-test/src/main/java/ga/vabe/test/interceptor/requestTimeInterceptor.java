package ga.vabe.test.interceptor;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Slf4j
public class requestTimeInterceptor implements HandlerInterceptor {

    private String className = this.getClass().getSimpleName();

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        log.info("{} ==> preHandle", className);
        request.setAttribute("requestTime", System.currentTimeMillis());
        return true;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        long beginTime = (Long) request.getAttribute("requestTime");
        log.info("{} ==> postHandler, elapsed: {}ms", className, System.currentTimeMillis() - beginTime);
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        log.info("{} ==> afterCompletion", className);
    }
}
