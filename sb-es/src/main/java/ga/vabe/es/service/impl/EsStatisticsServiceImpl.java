package ga.vabe.es.service.impl;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.ExcelWriter;
import com.alibaba.excel.write.metadata.WriteSheet;
import ga.vabe.es.config.ElasticConfig;
import ga.vabe.es.model.SlowSql;
import ga.vabe.es.service.EsStatisticService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.io.IOException;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Slf4j
@Component
@ConditionalOnBean(ElasticConfig.class)
@RequiredArgsConstructor
public class EsStatisticsServiceImpl implements EsStatisticService {

    /**
     * ES 索引名称
     */
    @Value("${es.index.name}")
    private String indexName;

    final RestHighLevelClient esClient;

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private static final Pattern SLOW_SQL_PATTERN = Pattern.compile(
            "\\{\"message\":\"#\\sUser@Host:\\s\\S*?\\[(\\S+?)]\\s*?@\\s*?\\[(\\S+)].*?" +
            "Query_time:\\s*?(\\S+)\\s*?Lock_time:\\s*?(\\S+)\\s*?" +
            "Rows_sent:\\s+(\\S+)\\s*?" +
            "Rows_examined:\\s*?(\\d+)\\s*?(.*?)\\s*?" +
            "(\\\\n#\\s*?Time:.+)?\"}"
    );

    @Override
    public List<SlowSql> getSlowSql(long timestampBegin, long timestampEnd) {
        BoolQueryBuilder queryBuilder = new BoolQueryBuilder();
        queryBuilder.filter(QueryBuilders.rangeQuery("@timestamp").from(timestampBegin, true));
        queryBuilder.filter(QueryBuilders.rangeQuery("@timestamp").to(timestampEnd, true));
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        searchSourceBuilder.query(queryBuilder);
        searchSourceBuilder.fetchSource(new String[]{"message"}, null);
        searchSourceBuilder.trackTotalHits(true);
        // 限制最大只取100W条数据
        searchSourceBuilder.size(1_000_000);
        SearchRequest request = new SearchRequest(indexName);
        request.source(searchSourceBuilder);
        try {
            LocalDateTime begin = LocalDateTime.ofInstant(Instant.ofEpochMilli(timestampBegin), ZoneId.systemDefault());
            LocalDateTime end = LocalDateTime.ofInstant(Instant.ofEpochMilli(timestampEnd), ZoneId.systemDefault());
            log.info("正在从 {} 索引获取‘{} - {}’的慢sql数据...", indexName, DATE_FORMATTER.format(begin), DATE_FORMATTER.format(end));
            SearchResponse response = esClient.search(request, RequestOptions.DEFAULT);
            SearchHit[] hits = response.getHits().getHits();
            List<String> respList = Stream.of(hits).map(SearchHit::getSourceAsString)
                    .collect(Collectors.toList());
            List<SlowSql> ssList = toSlowSql(respList);
            log.info("数据筛选完毕，共获取到 {} 条有效数据", ssList.size());
            return ssList;
        } catch (IOException e) {
            log.error("操作失败", e);
        }
        return Collections.emptyList();
    }

    @Override
    public void generateXml(List<SlowSql> ssList, String filepath) {
        if (CollectionUtils.isEmpty(ssList)) {
            log.warn("未获取到慢sql数据");
            return;
        }
        log.info("正在生成统计文件 {}", filepath);
        Map<String, List<SlowSql>> mapList = ssList.stream().sorted().distinct().collect(Collectors.groupingBy(SlowSql::getUser));
        ExcelWriter ew = EasyExcel.write(filepath, SlowSql.class).build();
        // writer.inMemory(true);
        try {
            mapList.forEach((key, value) -> {
                WriteSheet sheet = EasyExcel.writerSheet(key).build();
                ew.write(value, sheet);
                log.info("{} 移除重复sql后共计{}条", key, value.size());
            });
        } finally {
            if (ew != null) {
                ew.finish();
            }
        }
        log.info("统计完毕!");
    }

    public static List<SlowSql> toSlowSql(List<String> jsonList) {
        return jsonList.stream().map(json -> {
            Matcher matcher = SLOW_SQL_PATTERN.matcher(json);
            SlowSql ss = null;
            if (matcher.find()) {
                ss = SlowSql.builder()
                        .user(matcher.group(1))
                        .host(matcher.group(2))
                        .queryTime(Double.parseDouble(matcher.group(3)))
                        .lockTime(Double.parseDouble(matcher.group(4)))
                        .rowsSent(Integer.parseInt(matcher.group(5)))
                        .rowsExamined(Integer.parseInt(matcher.group(6)))
                        .sql(matcher.group(7).replace("\\n", "\r\n").replace("\\t", "\t"))
                        .build();
            } else {
                log.warn("正则未匹配，忽略该数据: {}", json);
            }
            return ss;
        }).filter(Objects::nonNull).collect(Collectors.toList());
    }

}
