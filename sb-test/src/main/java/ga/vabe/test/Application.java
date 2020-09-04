package ga.vabe.test;

import ga.vabe.other.OtherPkgClass;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Import;

@SpringBootApplication
// 开启 servlet 扫描
// @ServletComponentScan
@Import(OtherPkgClass.class)
public class Application {

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

}
