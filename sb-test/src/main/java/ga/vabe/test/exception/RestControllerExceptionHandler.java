package ga.vabe.test.exception;

import ga.vabe.common.CommonResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * 全局异常处理
 */
@RestControllerAdvice
@Slf4j
public class RestControllerExceptionHandler {

    @ExceptionHandler(Exception.class)
    public CommonResult<?> handleException(Exception ex) {
        log.error("error: ", ex);
        CommonResult<?> result = new CommonResult<>();
        result.setErrorCode("5555");
        result.setErrorMsg("系统错误: " + ex.getMessage());
        return result;
    }

}
