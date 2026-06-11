package org.xinghe.xinghehis.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

/**
 * Redis 配置
 *
 * 自定义 RedisTemplate 的序列化方式：
 *   - Key 使用 String 序列化（可读性好，便于调试）
 *   - Value 使用 JSON 序列化（支持复杂对象，且跨语言兼容）
 *
 * 默认的 JdkSerializationRedisSerializer 会将对象序列化为二进制，
 * 不可读且要求类实现 Serializable，不推荐使用。
 *
 * 当前项目中 Redis 的使用场景：
 *   1. Token 黑名单/白名单缓存
 *   2. 热点数据缓存（如科室列表、药品列表）
 *   3. 后续可扩展 Session 管理和分布式锁
 */
@Configuration
public class RedisConfig {

    @Bean
    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory factory) {
        RedisTemplate<String, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(factory);
        // Key 序列化：可读的字符串
        template.setKeySerializer(new StringRedisSerializer());
        // Value 序列化：JSON 格式，支持任意 Java 对象
        template.setValueSerializer(new GenericJackson2JsonRedisSerializer());
        // Hash 结构的 Key/Value 序列化
        template.setHashKeySerializer(new StringRedisSerializer());
        template.setHashValueSerializer(new GenericJackson2JsonRedisSerializer());
        return template;
    }
}
