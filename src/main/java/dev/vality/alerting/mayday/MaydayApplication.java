package dev.vality.alerting.mayday;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.scheduling.annotation.EnableScheduling;

@ServletComponentScan
@SpringBootApplication
public class MaydayApplication {
    public static void main(String[] args) {
        SpringApplication.run(MaydayApplication.class, args);
    }

}
