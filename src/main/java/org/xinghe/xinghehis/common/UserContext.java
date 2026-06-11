package org.xinghe.xinghehis.common;

/**
 * 当前登录用户上下文
 *
 * 使用 ThreadLocal 存储当前请求的用户信息，确保线程安全。
 * 每个 HTTP 请求由独立线程处理，ThreadLocal 保证了一个请求内
 * 任何地方都能获取当前用户，而不会与其他请求混淆。
 *
 * 数据流：
 *   JwtAuthFilter 解析 JWT → 调用 UserContext.set() 存入
 *   → Controller/Service 通过 UserContext.getUserId() 获取
 *   → 请求结束后 JwtAuthFilter 调用 clear() 清理
 */
public class UserContext {
    private static final ThreadLocal<Long> userId = new ThreadLocal<>();
    private static final ThreadLocal<String> username = new ThreadLocal<>();
    private static final ThreadLocal<String> role = new ThreadLocal<>();

    /** 设置当前请求的用户信息，在 JwtAuthFilter 中调用 */
    public static void set(Long id, String name, String r) {
        userId.set(id);
        username.set(name);
        role.set(r);
    }

    public static Long getUserId() { return userId.get(); }
    public static String getUsername() { return username.get(); }
    public static String getRole() { return role.get(); }

    /**
     * 清理 ThreadLocal，防止内存泄漏。
     * 每次请求结束后必须调用，否则在线程池环境下可能造成数据串用。
     */
    public static void clear() {
        userId.remove();
        username.remove();
        role.remove();
    }
}
