package org.xinghe.xinghehis.common;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * 全局异常处理器
 *
 * Spring 的 @RestControllerAdvice 会拦截所有 Controller 抛出的异常，
 * 统一转换为 Result 格式返回给前端，避免异常信息直接暴露。
 *
 * 异常处理优先级：越具体的异常类型越优先匹配。
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * 处理参数校验失败（@Valid 注解触发的校验）
     * 例如：LoginRequest.username 为空时抛出此异常
     * 将多个字段的错误信息拼接后返回，方便前端定位问题
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Result<Void> handleValidation(MethodArgumentNotValidException e) {
        String msg = e.getBindingResult().getFieldErrors().stream()
                .map(f -> f.getField() + ": " + f.getDefaultMessage())
                .reduce((a, b) -> a + "; " + b)
                .orElse("参数校验失败");
        return Result.fail(400, msg);
    }

    /**
     * 处理业务异常 — Service 层遇到预期内的错误时抛出 RuntimeException
     * 例如：用户名密码错误、患者不存在、药品不存在等
     */
    @ExceptionHandler(RuntimeException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Result<Void> handleRuntime(RuntimeException e) {
        return Result.fail(400, e.getMessage());
    }

    /**
     * 兜底处理所有未预期的异常，返回通用错误信息
     * 不暴露堆栈信息给前端，避免安全风险
     */
    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public Result<Void> handleException(Exception e) {
        return Result.fail(500, "服务器内部错误");
    }
}
