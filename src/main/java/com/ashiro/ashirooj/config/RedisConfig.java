package com.ashiro.ashirooj.config;

import com.alibaba.fastjson.support.spring.FastJsonRedisSerializer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.StringRedisSerializer;

/**
 * @author ashiro
 * @description
 */
@Configuration
public class RedisConfig{


    @Bean
    public RedisTemplate<Object,Object> redisTemplate(RedisConnectionFactory factory){
        RedisTemplate<Object, Object> redisTemplate = new RedisTemplate<>();
        redisTemplate.setConnectionFactory(factory);
        FastJsonRedisSerializer<Object> fastJsonRedisSerializer = new FastJsonRedisSerializer<>(Object.class);
        // 使用StringRedisSerializer来序列化和反序列化redis的key值
        StringRedisSerializer serializer = new StringRedisSerializer();
        redisTemplate.setKeySerializer(serializer);
        redisTemplate.setValueSerializer(fastJsonRedisSerializer);
        //hash的value也采用jackson序列化方式
        redisTemplate.setHashKeySerializer(serializer);
        redisTemplate.setHashValueSerializer(fastJsonRedisSerializer);
        redisTemplate.afterPropertiesSet();
        return redisTemplate;
    }
}
