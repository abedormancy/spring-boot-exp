package ga.vabe.es.config;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.http.HttpHost;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestClientBuilder;
import org.elasticsearch.client.RestHighLevelClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Configuration
@ConditionalOnProperty(name = "es.enable", havingValue = "true")
@Slf4j
// @RefreshScope
public class ElasticConfig {

    @Deprecated
    @Value("${es.host:}")
    public String host;
    @Deprecated
    @Value("${es.port:9200}")
    public int port;
    @Deprecated
    @Value("${es.scheme:}")
    public String scheme;

    @Value("${es.username:}")
    public String username;
    @Value("${es.password:}")
    public String password;

    /**
     * 自定义 hosts
     */
    @Value("${es.hosts:}")
    String[] hosts;

    @Bean
    public RestClientBuilder restClientBuilder() {
        RestClientBuilder builder = RestClient.builder(makeHttpHost());
        CredentialsProvider credentialsProvider = credentialsProvider();
        if (credentialsProvider != null) {
            builder.setHttpClientConfigCallback(f -> f.setDefaultCredentialsProvider(credentialsProvider));
        }
        return builder;
    }

    @Bean
    public RestClient restClient() {
        return RestClient.builder(makeHttpHost()).build();
    }

    private HttpHost[] makeHttpHost() {
        // 解析 hosts
        if (hosts != null && hosts.length > 0) {
            Pattern regex = Pattern.compile("((.+)://)?([^:]+):(\\d+)");
            List<HttpHost> hostList = Stream.of(hosts).map(host -> {
                Matcher matcher = regex.matcher(host);
                if (matcher.find() && matcher.groupCount() == 4) {
                    return new HttpHost(matcher.group(3), Integer.parseInt(matcher.group(4)), matcher.group(2));
                }
                log.warn("es.hosts 格式设置错误");
                return null;
            }).filter(Objects::nonNull).collect(Collectors.toList());
            if (CollectionUtils.isNotEmpty(hostList)) {
                return hostList.toArray(new HttpHost[]{});
            }
        }

        // 兼容老的配置方式
        if (host != null && host.trim().length() > 0) {
            return new HttpHost[]{new HttpHost(host, port, scheme)};
        }

        throw new RuntimeException("ES无法连接");
    }

    @Bean
    public RestHighLevelClient restHighLevelClient(@Autowired RestClientBuilder restClientBuilder) {
        return new RestHighLevelClient(restClientBuilder);
    }

    @Bean
    public CredentialsProvider credentialsProvider() {
        if (username == null || username.trim().length() == 0) {
            return null;
        }
        CredentialsProvider credentialsProvider = new BasicCredentialsProvider();
        credentialsProvider.setCredentials(AuthScope.ANY, new UsernamePasswordCredentials(username, password));
        return credentialsProvider;
    }
}