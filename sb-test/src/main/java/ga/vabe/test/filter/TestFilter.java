package ga.vabe.test.filter;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.web.servlet.filter.OrderedFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import java.io.IOException;

@Slf4j
public class TestFilter implements OrderedFilter {

    private String className = this.getClass().getSimpleName();

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain chain) throws IOException, ServletException {
        log.info("{} ==> doFilter", className);
        chain.doFilter(servletRequest, servletResponse);
    }

    @Override
    public int getOrder() {
        return 1025;
    }

}
