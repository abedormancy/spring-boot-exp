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
                        .sql(matcher.group(7).replace("\\n", "\r\n"))
                        .build();
            } else {
                log.warn("正则未匹配，忽略该数据: {}", json);
            }
            return ss;
        }).filter(Objects::nonNull).collect(Collectors.toList());
    }

    public static void main(String[] args) {
        String json1 = "{\"message\":\"# User@Host: eic_aq_1[eic_aq_1] @  [172.31.224.64]  Id: 2564732\\n# Query_time: 2.710368  Lock_time: 0.000992 Rows_sent: 1  Rows_examined: 622552\\nSET timestamp=1604569328;\\nSELECT COUNT(1) FROM ( SELECT\\n        t.*, u. NAME AS unit_name,p.post_id,p.post_name,p.post_code,u.short_name AS unit_short_name,\\n        mfl.bankUnit,\\n        mfl.bankTeam,\\n        mfl.filingPublishDate,\\n        mfl.bankMemberCategoryLabel\\n        FROM\\n        (\\n        SELECT\\n        dm.id,\\n        u.id AS user_id,\\n        u. NAME,\\n        u.username,\\n        u.mobile,\\n        u.idcard,\\n        u.unit_id,\\n        dm.project_id,\\n        dm.depart_id,\\n        d. NAME AS depart_name\\n        ,dm.join_date\\n        ,dm.exit_date\\n        ,dm.safety_test_date\\n        ,dm.test_score\\n        ,dm.user_type\\n        ,dm.gate_auth\\n        ,p.`name` AS project_name\\n        ,ua.face AS face_url\\n        ,ua.avatar AS avatar_url\\n        FROM\\n        ums_user u\\n        LEFT JOIN core_depart_member dm ON dm.user_id = u.id\\n        LEFT JOIN core_depart d ON dm.depart_id = d.id\\n        LEFT JOIN core_project p ON p.id = dm.project_id\\n        LEFT JOIN ums_user_attach ua ON ua.user_id = u.id\\n        WHERE\\n        dm.deleted = 0\\n        AND u.deleted = 0\\n         \\n            AND u.`name` LIKE CONCAT('%','王','%')\\n         \\n         \\n         \\n         \\n         \\n         \\n         \\n            AND dm.project_id = 1194917649922863106\\n         \\n         \\n            AND dm.depart_id = 2045\\n         \\n        ) AS t\\n        LEFT JOIN ums_unit u ON u.id = t.unit_id\\n        LEFT JOIN (\\n        SELECT\\n        dm.id,\\n        GROUP_CONCAT(pt.id) AS post_id,\\n        GROUP_CONCAT(pt.code) AS post_code,\\n        GROUP_CONCAT(pt.NAME) AS post_name\\n        ,MIN(pt.sort_index) AS post_sort_index\\n        FROM\\n        core_depart_member dm,\\n        core_depart_member_post p,\\n        base_post pt\\n        WHERE\\n        dm.id = p.member_id\\n        AND pt.id = p.post_id\\n         \\n            AND dm.project_id = 1194917649922863106\\n         \\n         \\n            AND dm.depart_id = 2045\\n         \\n         \\n         \\n        GROUP BY dm.id\\n        ) AS p on p.id = t.id\\n        left join (\\n        select\\n        smf.project_id , smfu.user_id , unit.name as bankUnit, st.master as bankTeam, smfl.gmt_create as\\n        filingPublishDate,\\n        (SELECT d.label FROM sys_dic d LEFT JOIN sys_dic_type dt ON dt.id = d.type_id\\n        WHERE dt.`code` = 'sub_member_category' AND d.`value` = uu.category ) bankMemberCategoryLabel\\n        from\\n        sub_member_filing_user smfu\\n        left join ums_user uu on\\n        uu.id = smfu.user_id\\n        left join sub_member_filing smf on\\n        smf.id = smfu.member_filing_id\\n        left join sub_team st on\\n        st.id = smfu.team_id\\n        left join ums_unit unit on\\n        unit.id = smfu.unit_id\\n        left join (\\n        select\\n        sal.subject_id , sal.gmt_create\\n        from\\n        sub_approve_log sal\\n        where\\n        sal.deleted = 0\\n        and sal.`type` = 'sub_member_filing'\\n        and sal.status = 20 ) smfl on\\n        smfl.subject_id = smf.id\\n        where\\n        smfu.deleted = 0\\n        and uu.deleted = 0\\n        and smf.deleted = 0\\n        and smf.status = 20\\n        group by\\n        smf.project_id , smfu.user_id )mfl on\\n        mfl.project_id = t.project_id\\n        and mfl.user_id = t.user_id\\n        where 1 = 1\\n         \\n         \\n        ORDER BY p.post_sort_index ASC\\n         \\n            , id desc ) TOTAL;\\n# Time: 2020-11-05T09:42:09.348495Z\"}\n";
        String json2 = "{\"message\":\"# User@Host: eic_aq_1[eic_aq_1] @  [172.31.224.62]  Id: 2597187\\n# Query_time: 1.531349  Lock_time: 0.000711 Rows_sent: 20  Rows_examined: 623245\\nSET timestamp=1604582642;\\nSELECT\\n        t.*, u. NAME AS unit_name,p.post_id,p.post_name,p.post_code,u.short_name AS unit_short_name,\\n        mfl.bankUnit,\\n        mfl.bankTeam,\\n        mfl.filingPublishDate,\\n        mfl.bankMemberCategoryLabel\\n        FROM\\n        (\\n        SELECT\\n        dm.id,\\n        u.id AS user_id,\\n        u. NAME,\\n        u.username,\\n        u.mobile,\\n        u.idcard,\\n        u.unit_id,\\n        dm.project_id,\\n        dm.depart_id,\\n        d. NAME AS depart_name\\n        ,dm.join_date\\n        ,dm.exit_date\\n        ,dm.safety_test_date\\n        ,dm.test_score\\n        ,dm.user_type\\n        ,dm.gate_auth\\n        ,p.`name` AS project_name\\n        ,ua.face AS face_url\\n        ,ua.avatar AS avatar_url\\n        FROM\\n        ums_user u\\n        LEFT JOIN core_depart_member dm ON dm.user_id = u.id\\n        LEFT JOIN core_depart d ON dm.depart_id = d.id\\n        LEFT JOIN core_project p ON p.id = dm.project_id\\n        LEFT JOIN ums_user_attach ua ON ua.user_id = u.id\\n        WHERE\\n        dm.deleted = 0\\n        AND u.deleted = 0\\n         \\n         \\n         \\n         \\n         \\n         \\n         \\n            AND dm.project_id = 1187559994579406850\\n         \\n         \\n            AND dm.depart_id = 7434\\n         \\n        ) AS t\\n        LEFT JOIN ums_unit u ON u.id = t.unit_id\\n        LEFT JOIN (\\n        SELECT\\n        dm.id,\\n        GROUP_CONCAT(pt.id) AS post_id,\\n        GROUP_CONCAT(pt.code) AS post_code,\\n        GROUP_CONCAT(pt.NAME) AS post_name\\n        ,MIN(pt.sort_index) AS post_sort_index\\n        FROM\\n        core_depart_member dm,\\n        core_depart_member_post p,\\n        base_post pt\\n        WHERE\\n        dm.id = p.member_id\\n        AND pt.id = p.post_id\\n         \\n            AND dm.project_id = 1187559994579406850\\n         \\n         \\n            AND dm.depart_id = 7434\\n         \\n         \\n         \\n        GROUP BY dm.id\\n        ) AS p on p.id = t.id\\n        left join (\\n        select\\n        smf.project_id , smfu.user_id , unit.name as bankUnit, st.master as bankTeam, smfl.gmt_create as\\n        filingPublishDate,\\n        (SELECT d.label FROM sys_dic d LEFT JOIN sys_dic_type dt ON dt.id = d.type_id\\n        WHERE dt.`code` = 'sub_member_category' AND d.`value` = uu.category ) bankMemberCategoryLabel\\n        from\\n        sub_member_filing_user smfu\\n        left join ums_user uu on\\n        uu.id = smfu.user_id\\n        left join sub_member_filing smf on\\n        smf.id = smfu.member_filing_id\\n        left join sub_team st on\\n        st.id = smfu.team_id\\n        left join ums_unit unit on\\n        unit.id = smfu.unit_id\\n        left join (\\n        select\\n        sal.subject_id , sal.gmt_create\\n        from\\n        sub_approve_log sal\\n        where\\n        sal.deleted = 0\\n        and sal.`type` = 'sub_member_filing'\\n        and sal.status = 20 ) smfl on\\n        smfl.subject_id = smf.id\\n        where\\n        smfu.deleted = 0\\n        and uu.deleted = 0\\n        and smf.deleted = 0\\n        and smf.status = 20\\n        group by\\n        smf.project_id , smfu.user_id )mfl on\\n        mfl.project_id = t.project_id\\n        and mfl.user_id = t.user_id\\n        where 1 = 1\\n         \\n         \\n        ORDER BY p.post_sort_index ASC\\n         \\n            , id desc LIMIT 60,20;\"}\n";
        String json3 = "{\"message\":\"# User@Host: eic_aq_1[eic_aq_2] @  [172.31.224.64]  Id: 2564732\\n# Query_time: 3.710368  Lock_time: 0.000992 Rows_sent: 1  Rows_examined: 622552\\nSET timestamp=1604569328;\\nSELECT COUNT(1) FROM ( SELECT\\n        t.*, u. NAME AS unit_name,p.post_id,p.post_name,p.post_code,u.short_name AS unit_short_name,\\n        mfl.bankUnit,\\n        mfl.bankTeam,\\n        mfl.filingPublishDate,\\n        mfl.bankMemberCategoryLabel\\n        FROM\\n        (\\n        SELECT\\n        dm.id,\\n        u.id AS user_id,\\n        u. NAME,\\n        u.username,\\n        u.mobile,\\n        u.idcard,\\n        u.unit_id,\\n        dm.project_id,\\n        dm.depart_id,\\n        d. NAME AS depart_name\\n        ,dm.join_date\\n        ,dm.exit_date\\n        ,dm.safety_test_date\\n        ,dm.test_score\\n        ,dm.user_type\\n        ,dm.gate_auth\\n        ,p.`name` AS project_name\\n        ,ua.face AS face_url\\n        ,ua.avatar AS avatar_url\\n        FROM\\n        ums_user u\\n        LEFT JOIN core_depart_member dm ON dm.user_id = u.id\\n        LEFT JOIN core_depart d ON dm.depart_id = d.id\\n        LEFT JOIN core_project p ON p.id = dm.project_id\\n        LEFT JOIN ums_user_attach ua ON ua.user_id = u.id\\n        WHERE\\n        dm.deleted = 0\\n        AND u.deleted = 0\\n         \\n            AND u.`name` LIKE CONCAT('%','王','%')\\n         \\n         \\n         \\n         \\n         \\n         \\n         \\n            AND dm.project_id = 1194917649922863106\\n         \\n         \\n            AND dm.depart_id = 2045\\n         \\n        ) AS t\\n        LEFT JOIN ums_unit u ON u.id = t.unit_id\\n        LEFT JOIN (\\n        SELECT\\n        dm.id,\\n        GROUP_CONCAT(pt.id) AS post_id,\\n        GROUP_CONCAT(pt.code) AS post_code,\\n        GROUP_CONCAT(pt.NAME) AS post_name\\n        ,MIN(pt.sort_index) AS post_sort_index\\n        FROM\\n        core_depart_member dm,\\n        core_depart_member_post p,\\n        base_post pt\\n        WHERE\\n        dm.id = p.member_id\\n        AND pt.id = p.post_id\\n         \\n            AND dm.project_id = 1194917649922863106\\n         \\n         \\n            AND dm.depart_id = 2045\\n         \\n         \\n         \\n        GROUP BY dm.id\\n        ) AS p on p.id = t.id\\n        left join (\\n        select\\n        smf.project_id , smfu.user_id , unit.name as bankUnit, st.master as bankTeam, smfl.gmt_create as\\n        filingPublishDate,\\n        (SELECT d.label FROM sys_dic d LEFT JOIN sys_dic_type dt ON dt.id = d.type_id\\n        WHERE dt.`code` = 'sub_member_category' AND d.`value` = uu.category ) bankMemberCategoryLabel\\n        from\\n        sub_member_filing_user smfu\\n        left join ums_user uu on\\n        uu.id = smfu.user_id\\n        left join sub_member_filing smf on\\n        smf.id = smfu.member_filing_id\\n        left join sub_team st on\\n        st.id = smfu.team_id\\n        left join ums_unit unit on\\n        unit.id = smfu.unit_id\\n        left join (\\n        select\\n        sal.subject_id , sal.gmt_create\\n        from\\n        sub_approve_log sal\\n        where\\n        sal.deleted = 0\\n        and sal.`type` = 'sub_member_filing'\\n        and sal.status = 20 ) smfl on\\n        smfl.subject_id = smf.id\\n        where\\n        smfu.deleted = 0\\n        and uu.deleted = 0\\n        and smf.deleted = 0\\n        and smf.status = 20\\n        group by\\n        smf.project_id , smfu.user_id )mfl on\\n        mfl.project_id = t.project_id\\n        and mfl.user_id = t.user_id\\n        where 1 = 1\\n         \\n         \\n        ORDER BY p.post_sort_index ASC\\n         \\n            , id desc ) TOTAL;\\n# Time: 2020-11-05T09:42:09.348495Z\"}\n";

        List<SlowSql> ssList = toSlowSql(Arrays.asList(json1, json2, json3));
        Map<String, List<SlowSql>> mapList = ssList.stream().sorted().distinct().collect(Collectors.groupingBy(SlowSql::getUser));
        ExcelWriter ew = EasyExcel.write("r:/test5.xlsx", SlowSql.class).build();
        // writer.inMemory(true);
        try {
            mapList.forEach((key, value) -> {
                WriteSheet sheet = EasyExcel.writerSheet(key).build();
                ew.write(value, sheet);
            });
        } finally {
            if (ew != null) {
                ew.finish();
            }
        }

        // System.out.println(json);
        // Matcher matcher = SLOW_SQL_PATTERN.matcher(json);
        // if (matcher.find()) {
        //     System.out.println(matcher.group(1));
        //     System.out.println(matcher.group(2));
        //     System.out.println(matcher.group(3));
        //     System.out.println(matcher.group(4));
        //     System.out.println(matcher.group(5));
        //     System.out.println(matcher.group(6));
        //     System.out.println(matcher.group(7));
        // }
    }

}
