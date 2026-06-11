package org.xinghe.xinghehis.common;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

/**
 * JWT（JSON Web Token）工具类
 *
 * JWT 是无状态认证的核心组件，工作流程：
 *   1. 用户登录成功 → 服务端生成 JWT 返回
 *   2. 客户端将 JWT 存储在 localStorage，后续请求通过 Authorization 头携带
 *   3. 服务端解析 JWT 获取用户信息，无需查询数据库即可验证身份
 *
 * JWT 结构（Header.Payload.Signature）：
 *   - Header：算法类型（HS256）
 *   - Payload：存放 userId、username、role 等自定义信息
 *   - Signature：用密钥对前两部分签名，防止篡改
 *
 * 从 application.yaml 读取 jwt.secret 和 jwt.expiration 配置值。
 */
@Component
public class JwtUtil {

    private final SecretKey key;    // HMAC-SHA256 签名密钥
    private final long expiration;  // Token 有效期（毫秒），默认 86400000 = 24小时

    /**
     * 构造函数注入配置值
     * @param secret 从 yaml 中的 jwt.secret 读取，至少 256 bits
     * @param expiration 从 yaml 中的 jwt.expiration 读取
     */
    public JwtUtil(@Value("${jwt.secret}") String secret,
                   @Value("${jwt.expiration}") long expiration) {
        this.key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
        this.expiration = expiration;
    }

    /**
     * 生成 JWT Token
     * @param userId   用户ID，存入 subject 字段
     * @param username 用户名，存入自定义 claim
     * @param role     角色，存入自定义 claim
     */
    public String generateToken(Long userId, String username, String role) {
        Date now = new Date();
        Date exp = new Date(now.getTime() + expiration);
        return Jwts.builder()
                .subject(userId.toString())     // sub: 用户ID
                .claim("username", username)    // 自定义字段
                .claim("role", role)            // 自定义字段
                .issuedAt(now)                  // iat: 签发时间
                .expiration(exp)                // exp: 过期时间
                .signWith(key)                  // 使用 HMAC-SHA256 签名
                .compact();
    }

    /**
     * 解析并验证 JWT Token
     * 如果 Token 被篡改或签名不匹配，会自动抛出异常
     */
    public Claims parseToken(String token) {
        return Jwts.parser()
                .verifyWith(key)               // 设置验证密钥
                .build()
                .parseSignedClaims(token)       // 解析并验证签名
                .getPayload();
    }

    /**
     * 判断 Token 是否已过期
     * 解析失败（如签名错误、格式错误）也视为过期
     */
    public boolean isTokenExpired(String token) {
        try {
            return parseToken(token).getExpiration().before(new Date());
        } catch (Exception e) {
            return true;
        }
    }
}
