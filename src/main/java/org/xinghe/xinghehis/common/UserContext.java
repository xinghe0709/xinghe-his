package org.xinghe.xinghehis.common;

public class UserContext {
    private static final ThreadLocal<Long> userId = new ThreadLocal<>();
    private static final ThreadLocal<String> username = new ThreadLocal<>();
    private static final ThreadLocal<String> role = new ThreadLocal<>();

    public static void set(Long id, String name, String r) {
        userId.set(id);
        username.set(name);
        role.set(r);
    }

    public static Long getUserId() { return userId.get(); }
    public static String getUsername() { return username.get(); }
    public static String getRole() { return role.get(); }

    public static void clear() {
        userId.remove();
        username.remove();
        role.remove();
    }
}
