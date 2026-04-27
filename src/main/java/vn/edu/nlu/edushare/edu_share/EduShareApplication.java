package vn.edu.nlu.edushare.edu_share;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(
        excludeName = "org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration"
)
public class EduShareApplication {

    public static void main(String[] args) {
        SpringApplication.run(EduShareApplication.class, args);
    }

}
