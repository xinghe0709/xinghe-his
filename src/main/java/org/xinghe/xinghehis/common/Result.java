package org.xinghe.xinghehis.common;

/**
 * 统一 API 响应体
 *
 * 所有 Controller 的返回值都用 Result 包装，前端可以统一处理：
 *   code=200 → 成功
 *   code=400 → 业务异常（参数校验失败等）
 *   code=500 → 服务器内部错误
 *
 * 使用泛型 T 适配不同的 data 类型，例如：
 *   Result<User>、Result<List<Patient>>、Result<Map<String, Object>>
 */
public class Result<T> {
    private int code;       // 状态码，200=成功 400=客户端错误 500=服务端错误
    private String message; // 提示信息
    private T data;         // 响应数据，可为 null

    private Result(int code, String message, T data) {
        this.code = code;
        this.message = message;
        this.data = data;
    }

    /** 成功响应（带数据） */
    public static <T> Result<T> ok(T data) {
        return new Result<>(200, "success", data);
    }

    /** 成功响应（无数据，如删除操作） */
    public static <T> Result<T> ok() {
        return new Result<>(200, "success", null);
    }

    /** 失败响应（自定义状态码和消息） */
    public static <T> Result<T> fail(int code, String message) {
        return new Result<>(code, message, null);
    }

    /** 失败响应（默认 500 状态码） */
    public static <T> Result<T> fail(String message) {
        return new Result<>(500, message, null);
    }

    public int getCode() { return code; }
    public void setCode(int code) { this.code = code; }
    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
    public T getData() { return data; }
    public void setData(T data) { this.data = data; }
}
