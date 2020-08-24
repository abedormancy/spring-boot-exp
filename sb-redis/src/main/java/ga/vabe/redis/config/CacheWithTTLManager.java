package ga.vabe.redis.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.Cache;
import org.springframework.cache.concurrent.ConcurrentMapCacheManager;
import org.springframework.scheduling.annotation.Scheduled;

import javax.annotation.Nonnull;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;

/**
 * <b>Description:</b><br>
 * 通过继承实现在 @Cacheable 上指定缓存时间
 * 主要用于替代 SpringBoot 中默认的 ConcurrentMapCacheManager
 *
 * @author abeholder
 * @version 1.0
 */
@Slf4j
public class CacheWithTTLManager extends ConcurrentMapCacheManager {

    /**
     * 如果有需要可以开一个 daemon 线程或固定任务清除长时间未用到的对象
     * 或直接使用 Spring Scheduled
     * 每两小时清除已过期但一直未被访问的缓存
     */
    @Scheduled(fixedDelay = 1000 * 60 * 60 * 2, initialDelay = 1000 * 60 * 60 * 2)
    private void clear() {
        log.info("-------- 执行清除缓存任务 --------");
        getCacheNames().stream().map(name -> (TTLCache) getCache(name))
                .filter(Objects::nonNull)
                .forEach(TTLCache::clearOverdueCache);
    }

    @Nonnull
    @Override
    protected Cache createConcurrentMapCache(String name) {
        // Cache cache = super.createConcurrentMapCache(name);
        // 通过装饰器模式返回一个自定义的 Cache 对象
        return new TTLCache(super.createConcurrentMapCache(name));
    }

    /**
     * <b>Description:</b><br>
     * 带过期时间的 ConcurrentHashMap 缓存
     *
     * @author abeholder
     * @version 1.0
     */
    private static class TTLCache implements Cache {

        /**
         * 默认过期时间 3600 秒
         */
        private static final int DEFAULT_SECOND = 3600;

        /**
         * Time To Live ，单位毫秒
         */
        private int ttl = toMillis(DEFAULT_SECOND);

        private Map<Object, Long> mapper = new ConcurrentHashMap<>(64);

        private Cache cache;

        public TTLCache(Cache cache) {
            this.cache = cache;
            int value = toInt(cache.getName());
            if (value > 0) {
                ttl = toMillis(value);
            }
        }

        @Override
        public String getName() {
            return cache.getName();
        }

        @Override
        public Object getNativeCache() {
            return cache.getNativeCache();
        }

        /**
         * 获取缓存时检查是否过期，如果过期那么清除缓存
         * @param key
         * @return
         */
        @Override
        public ValueWrapper get(Object key) {
            if (isOverdue(mapper.get(key))) {
                evict(key);
            }
            return cache.get(key);
        }

        @Override
        public <T> T get(Object key, Class<T> type) {
            return cache.get(key, type);
        }

        @Override
        public <T> T get(Object key, Callable<T> valueLoader) {
            return cache.get(key, valueLoader);
        }

        @Override
        public void put(Object key, Object value) {
            // 设置缓存开始时间
            if (ttl > 0) {
                mapper.put(key, now());
            }
            cache.put(key, value);
        }

        @Override
        public ValueWrapper putIfAbsent(Object key, Object value) {
            return cache.putIfAbsent(key, value);
        }

        @Override
        public void evict(Object key) {
            cache.evict(key);
        }

        @Override
        public void clear() {
            cache.clear();
        }

        /**
         * 检查是否过期
         *
         * @param last
         * @return
         */
        private boolean isOverdue(Long last) {
            return last != null && now() - ttl > last;
        }

        /**
         * 检查是否过期
         *
         * @param entry
         * @return
         */
        private boolean isOverdue(Entry<Object, Long> entry) {
            boolean flag = isOverdue(entry.getValue());
            log.info("{} -> {} : {}", cache.getName(), entry.getKey(), entry.getValue());
            if (flag) {
                evict(entry.getKey());
                log.info("-------- >> 清除缓存 {} [ {} = {} ]", cache.getName(), entry.getKey(), entry.getValue());
            }
            return flag;
        }

        private long now() {
            return System.currentTimeMillis();
        }

        // return > 0 or -1;
        private static final int toInt(String value) {
            try {
                return Integer.parseInt(value);
            } catch (NumberFormatException e) {
                // ignore
                return DEFAULT_SECOND;
            }
        }

        /**
         * 秒转毫秒
         *
         * @param seconds
         * @return
         */
        private static final int toMillis(int seconds) {
            return seconds * 1000;
        }

        /**
         * 清除过期缓存
         */
        public void clearOverdueCache() {
            mapper.entrySet().removeIf(this::isOverdue);
        }

    }
}
