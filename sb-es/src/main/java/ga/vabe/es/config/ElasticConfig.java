package ga.vabe.es.config;

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
import org.springframework.util.StringUtils;

@Configuration
@ConditionalOnProperty(name = "es.enable", havingValue = "true")
public class ElasticConfig {

    @Value("${es.host}")
    public String host;
    @Value("${es.port}")
    public int port;
    @Value("${es.scheme}")
    public String scheme;
    @Value("${es.username:}")
    public String username;
    @Value("${es.password:}")
    public String password;

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
    public RestClient restClient(){
        return RestClient.builder(makeHttpHost()).build();
    }

    private HttpHost makeHttpHost() {
        return new HttpHost(host, port, scheme);
    }

    @Bean
    public RestHighLevelClient restHighLevelClient(@Autowired RestClientBuilder restClientBuilder){
        return new RestHighLevelClient(restClientBuilder);
    }

    @Bean
    public CredentialsProvider credentialsProvider() {
        if (StringUtils.isEmpty(username)) {
            return null;
        }
        CredentialsProvider credentialsProvider = new BasicCredentialsProvider();
        credentialsProvider.setCredentials(AuthScope.ANY, new UsernamePasswordCredentials(username, password));
        return credentialsProvider;
    }
}