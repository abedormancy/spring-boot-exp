package ga.uuid.config;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CachingConfigurerSupport;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.interceptor.KeyGenerator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.cache.RedisCacheWriter;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * 
 * <b>Description:</b><br> 
 * redis 配置类
 * @author abeholder
 */
@Configuration
@EnableCaching
public class CacheConfig extends CachingConfigurerSupport {
	
	/**
	 * 
	 * <b>Description:</b><br> 
	 * 自定义键生成策略
	 * @return
	 * @Note
	 * <b>Author:</b> abeholder
	 */
	@Bean
	public KeyGenerator simpleKeyGenerator() {
		return (o, method, objects) -> {
			StringBuilder sb = new StringBuilder();
			sb.append(o.getClass().getSimpleName());
			sb.append(".");
			sb.append(method.getName());
			sb.append("[");
			for (Object obj : objects) {
				sb.append(obj.toString());
			}
			sb.append("]");
			return sb.toString();
		};
	}

	@Bean
	public CacheManager cacheManager(RedisConnectionFactory redisConnectionFactory) {
		RedisCacheWriter writer = RedisCacheWriter.nonLockingRedisCacheWriter(redisConnectionFactory);
		// 自定义配置
		Map<String, RedisCacheConfiguration> configs = new HashMap<>();
		configs.put("dudulu", generateRedisCacheConfiguration(60));
		configs.put("dudu", generateRedisCacheConfiguration(40));
		configs.put("du", generateRedisCacheConfiguration(3));
		return new RedisCacheManager(writer, generateRedisCacheConfiguration(600), configs);
	}
	
	/**
	 * redis 中默认使用的是 java 序列化机制，对象如果不实现 Serializable 接口，无法序列化。<br>
	 * 这里通过使用 StringRedisTemplate 结合 Jackson2JsonRedisSerializer 方式序列化，而不需要对象实现 Serializable 接口
	 */
	@Bean
	public RedisTemplate<?, ?> redisTemplate(RedisConnectionFactory factory) {
		StringRedisTemplate template = new StringRedisTemplate(factory);
		Jackson2JsonRedisSerializer<Object> jackson2JsonRedisSerializer = new Jackson2JsonRedisSerializer<Object>(Object.class);
		ObjectMapper om = new ObjectMapper();
		om.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.ANY);
		om.enableDefaultTyping(ObjectMapper.DefaultTyping.NON_FINAL);
		jackson2JsonRedisSerializer.setObjectMapper(om);
		template.setValueSerializer(jackson2JsonRedisSerializer);
		template.afterPropertiesSet();
		return template;
	}
	
	/**
	 * 
	 * <b>Description:</b><br>
	 * 设置 Cacheable 序列化方式 
	 * @param seconds 过期时间
	 * @return
	 * @Note
	 * <b>Author:</b> abeholder
	 */
	@Bean
	public RedisCacheConfiguration generateRedisCacheConfiguration(int seconds) {
		Jackson2JsonRedisSerializer<Object> jackson2JsonRedisSerializer = new Jackson2JsonRedisSerializer<Object>(Object.class);
		ObjectMapper om = new ObjectMapper();
        om.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.ANY);
        om.enableDefaultTyping(ObjectMapper.DefaultTyping.NON_FINAL);
        jackson2JsonRedisSerializer.setObjectMapper(om);

		RedisCacheConfiguration configuration = RedisCacheConfiguration.defaultCacheConfig();
		configuration = configuration.serializeValuesWith(
						RedisSerializationContext.SerializationPair.fromSerializer(jackson2JsonRedisSerializer))
						.entryTtl(Duration.ofSeconds(seconds));
		return configuration;
	}

}
