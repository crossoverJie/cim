package com.crossoverjie.cim.persistence.api.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.hash.Jackson2HashMapper;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

/**
 * @author zhongcanyu
 * @date 2025/5/19
 * @description
 */
@Configuration("persistenceBeanConfig")
@Slf4j
public class BeanConfig {

    /**
     * Redis bean
     *
     * @param factory
     * @return
     */
    @Bean
    public RedisTemplate<String, Object> stringObjectRedisTemplate(RedisConnectionFactory factory) {
        RedisTemplate<String, Object> redisTemplate = new RedisTemplate<>();
        redisTemplate.setConnectionFactory(factory);

        // 设置键的序列化器为字符串
        redisTemplate.setKeySerializer(new StringRedisSerializer());

        // 设置值的序列化器为JSON，支持对象存储
        redisTemplate.setValueSerializer(new GenericJackson2JsonRedisSerializer());

        // 设置Hash键和值的序列化器（可选）
        redisTemplate.setHashKeySerializer(new StringRedisSerializer());
        redisTemplate.setHashValueSerializer(new GenericJackson2JsonRedisSerializer());

        // 初始化属性
        redisTemplate.afterPropertiesSet();
        return redisTemplate;
    }

    @Bean
    public Jackson2HashMapper hashMapper(ObjectMapper objectMapper) {
        // false 表示不在 key 里加 @class 信息
        return new Jackson2HashMapper(objectMapper, false);
    }
}
