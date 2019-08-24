package ga.vabe.beetl.util;

import ga.vabe.beetl.domain.User;
import org.beetl.sql.core.*;
import org.beetl.sql.core.db.DBStyle;
import org.beetl.sql.core.db.MySqlStyle;
import org.beetl.sql.core.query.Query;
import org.beetl.sql.ext.DebugInterceptor;

import java.util.List;

public class Gen {

    public static void main(String[] args) throws Exception {
        ConnectionSource source = ConnectionSourceHelper.getSimple(
                "com.mysql.cj.jdbc.Driver",
                "jdbc:mysql://192.168.56.11:3306/beetl?autoReconnect=true&useSSL=false&useUnicode=true&characterEncoding=utf-8&allowMultiQueries=true&serverTimezone=Asia/Shanghai",
                "root",
                "123456");
        DBStyle mysql = new MySqlStyle();
        // sql语句放在classpagth的/sql 目录下
        SQLLoader loader = new ClasspathLoader("/sql");
        // 数据库命名跟java命名一样，所以采用DefaultNameConversion，还有一个是UnderlinedNameConversion，下划线风格的，
        UnderlinedNameConversion nc = new UnderlinedNameConversion();
        // 最后，创建一个SQLManager,DebugInterceptor 不是必须的，但可以通过它查看sql执行情况
        SQLManager sqlManager = new SQLManager(mysql, loader, source, nc, new Interceptor[]{new DebugInterceptor()});

        // 使用内置的生成的sql 新增用户，如果需要获取主键，可以传入KeyHolder
        User user = new User();
        user.setAge(19);
        user.setName("abe");
        sqlManager.insert(user);

        // 使用内置sql查询用户
        // int id = 1;
        // user = sqlManager.unique(User.class, id);

        // 模板更新,仅仅根据id更新值不为null的列
        User newUser = new User();
        newUser.setId(1l);
        newUser.setAge(20);
        sqlManager.updateTemplateById(newUser);

        // 模板查询
        User query = new User();
        query.setName("abe");
        List<User> list = sqlManager.template(query);

        // Query查询
        Query<User> userQuery = sqlManager.query(User.class);
        List<User> users = userQuery.lambda().andEq(User::getName, "xiandafy").select();

        // sqlManager.genPojoCodeToConsole("user");
        // sqlManager.genSQLTemplateToConsole("user");
        // 使用user.md 文件里的select语句，参考下一节。
        User user2 = new User();
        user2.setName("abe");
        List<User> list2 = sqlManager.select("user.sample", User.class, user2);

        // 这一部分需要参考mapper一章
        // UserDao dao = sqlManager.getMapper(UserDao.class);
        // List<User> list3 = dao.select(query2);
    }
}
