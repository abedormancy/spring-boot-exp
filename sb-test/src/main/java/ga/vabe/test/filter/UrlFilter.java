package ga.vabe.test.filter;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.web.servlet.filter.OrderedFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

@Slf4j
// @WebFilter(urlPatterns = "/*", filterName = "urlFilter")
public class UrlFilter implements OrderedFilter {

    private String className = this.getClass().getSimpleName();

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest req = (HttpServletRequest) request;
        log.info("{} ==> \nrequestURI: {}\nrequestURL: {}", className, req.getRequestURI(), req.getRequestURL());
        chain.doFilter(req, response);
    }

    @Override
    public int getOrder() {
        return 1026;
    }
}