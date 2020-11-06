package ga.vabe.es.service;

import ga.vabe.es.model.SlowSql;

import java.util.List;

public interface EsStatisticService {


    List<SlowSql> getSlowSql(long timestampBegin, long timestampEnd);

    void generateXml(List<SlowSql> ssList, String filepath);

}
