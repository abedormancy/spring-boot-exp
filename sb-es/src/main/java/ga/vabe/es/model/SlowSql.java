package ga.vabe.es.model;

import com.alibaba.excel.annotation.ExcelIgnore;
import lombok.Builder;
import lombok.Data;
import org.apache.commons.codec.binary.StringUtils;

@Data
@Builder
public class SlowSql implements Comparable {

    @ExcelIgnore
    private String user;

    private String host;

    private double queryTime;

    private double lockTime;

    private int rowsSent;

    private int rowsExamined;

    private String sql;

    // TODO add sql 执行的时间

    @Override
    public int compareTo(Object o) {
        if (o instanceof SlowSql) {
            SlowSql target = (SlowSql) o;
            double value = queryTime - target.getQueryTime();
            if (value == 0d) {
                return 0;
            }
            return value < 0 ? 1 : -1;
        }
        return 0;
    }

    @Override
    public boolean equals(Object object) {
        if (!(object instanceof SlowSql)) {
            return false;
        }
        SlowSql target = (SlowSql) object;
        return StringUtils.equals(user, target.user) && StringUtils.equals(sql, target.sql);
    }

    @Override
    public int hashCode() {
        return String.valueOf(sql).hashCode() + String.valueOf(user).hashCode();
    }

}
