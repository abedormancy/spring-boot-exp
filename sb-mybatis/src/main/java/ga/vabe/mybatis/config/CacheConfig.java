package ga.vabe.mybatis.config;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CachingConfigurerSupport;
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

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * <b>Description:</b><br>
 * redis 配置类
 * @author abeholder
 */
@Configuration
public class CacheConfig extends CachingConfigurerSupport {

	/**
	 * 设置默认的 keyGenerator
	 * @return
	 */
	@Bean
	@Override
	public KeyGenerator keyGenerator() {
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


	/**
	 * redis cacheManager
	 * @param redisConnectionFactory
	 * @return
	 */
	@Bean
	public CacheManager cacheManager(RedisConnectionFactory redisConnectionFactory) {
		RedisCacheWriter writer = RedisCacheWriter.nonLockingRedisCacheWriter(redisConnectionFactory);
		// 自定义配置
		Map<String, RedisCacheConfiguration> configs = new HashMap<>();
		configs.put("60", generateRedisCacheConfiguration(60));
		configs.put("30", generateRedisCacheConfiguration(30));
		configs.put("5", generateRedisCacheConfiguration(5));
		return new RedisCacheManager(writer, generateRedisCacheConfiguration(3600), configs);
	}

	/**
	 * redis 中默认使用的是 java 序列化机制，对象如果不实现 Serializable 接口，无法序列化。<br>
	 * 这里通过使用 StringRedisTemplate 结合 Jackson2JsonRedisSerializer 方式序列化，而不需要对象实现 Serializable 接口
	 */
	@Bean
	public RedisTemplate<?, ?> redisTemplate(RedisConnectionFactory factory) {
		StringRedisTemplate template = new StringRedisTemplate(factory);
		Jackson2JsonRedisSerializer<Object> jackson2JsonRedisSerializer = getJackson2JsonRedisSerializer();
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
	public RedisCacheConfiguration generateRedisCacheConfiguration(int seconds) {
		Jackson2JsonRedisSerializer<Object> jackson2JsonRedisSerializer = getJackson2JsonRedisSerializer();
		RedisCacheConfiguration configuration = RedisCacheConfiguration.defaultCacheConfig();
		configuration = configuration.serializeValuesWith(
						RedisSerializationContext.SerializationPair.fromSerializer(jackson2JsonRedisSerializer))
						.entryTtl(Duration.ofSeconds(seconds));
		return configuration;
	}

	@Bean
	Jackson2JsonRedisSerializer<Object> getJackson2JsonRedisSerializer() {
		Jackson2JsonRedisSerializer<Object> jackson2JsonRedisSerializer = new Jackson2JsonRedisSerializer<Object>(Object.class);
		ObjectMapper om = new ObjectMapper();
		om.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.ANY);
		om.enableDefaultTyping(ObjectMapper.DefaultTyping.NON_FINAL);
		jackson2JsonRedisSerializer.setObjectMapper(om);
		return jackson2JsonRedisSerializer;
	}

}
