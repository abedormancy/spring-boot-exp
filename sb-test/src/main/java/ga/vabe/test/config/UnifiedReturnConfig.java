package ga.vabe.test.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import ga.vabe.common.CommonResult;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

/**
 * 统一返回数据格式
 */
@Configuration
public class UnifiedReturnConfig {

    private static ObjectMapper mapper = new ObjectMapper();

    // @Bean
    // public HttpMessageConverter stringHttpMessageConverter() {
    //     StringHttpMessageConverter stringHttpMessageConverter = new StringHttpMessageConverter();
    //     return null;
    // }

    @RestControllerAdvice("ga.vabe.test.controller")
    // @RestControllerAdvice
    static class CommonResultResponseAdvice implements ResponseBodyAdvice<Object> {

        /**
         * 哪些方法执行 beforeBodyWrite
         */
        @Override
        public boolean supports(MethodParameter methodParameter, Class<? extends HttpMessageConverter<?>> aClass) {
            return true;
        }

        @Override
        public Object beforeBodyWrite(Object body, MethodParameter methodParameter, MediaType mediaType,
                                      Class<? extends HttpMessageConverter<?>> aClass, ServerHttpRequest serverHttpRequest,
                                      ServerHttpResponse serverHttpResponse) {
            if (body instanceof CommonResult) {
                return body;
            }
            return new CommonResult<>(body);
        }
    }
}