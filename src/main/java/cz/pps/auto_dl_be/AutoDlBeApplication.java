package cz.pps.auto_dl_be;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(exclude = {org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration.class})
public class AutoDlBeApplication {

    public static void main(String[] args) {
        SpringApplication.run(AutoDlBeApplication.class, args);
    }

}
