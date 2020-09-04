package ga.vabe.test.config;

import ga.vabe.test.filter.TestFilter;
import ga.vabe.test.filter.UrlFilter;
import ga.vabe.test.interceptor.requestTimeInterceptor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import javax.servlet.Filter;

@Configuration
// @EnableWebMvc
@Slf4j
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new requestTimeInterceptor());
    }

    @Bean
    public FilterRegistrationBean<? extends Filter> urlFilter() {
        FilterRegistrationBean<Filter> registry = new FilterRegistrationBean<>();
        UrlFilter urlFilter = new UrlFilter();
        registry.setFilter(urlFilter);
        registry.setEnabled(true);
        registry.setOrder(urlFilter.getOrder());
        registry.addUrlPatterns("/*");
        return registry;
    }

    @Bean
    public FilterRegistrationBean<? extends Filter> testFilter() {
        FilterRegistrationBean<Filter> registry = new FilterRegistrationBean<>();
        TestFilter testFilter = new TestFilter();
        registry.setFilter(testFilter);
        registry.setOrder(testFilter.getOrder());
        registry.setEnabled(true);
        registry.addUrlPatterns("/*");
        return registry;
    }

    // @Bean
    // public StringHttpMessageConverter stringHttpMessageConverter(){
    //     return new StringHttpMessageConverter(Charset.forName("UTF-8"));
    // }
}
