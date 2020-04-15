package ga.vabe.mybatis;

import ga.vabe.mybatis.service.IUuService;
import lombok.extern.slf4j.Slf4j;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@MapperScan(basePackages = "ga.vabe.mybatis.dao")
@SpringBootApplication
@RestController
public class Application {

//    JdbcTemplate jdbcTemplate;

    @Autowired
    IUuService uuService;

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

    @RequestMapping("/")
    public String index() {
        return String.valueOf(uuService.list().stream().findAny().orElse(null));
    }
}
