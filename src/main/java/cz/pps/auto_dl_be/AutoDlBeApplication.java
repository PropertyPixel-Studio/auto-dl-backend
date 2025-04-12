package cz.pps.auto_dl_be;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@SpringBootApplication
@EnableAsync
@EnableTransactionManagement
public class AutoDlBeApplication {

    public static void main(String[] args) {
        SpringApplication.run(AutoDlBeApplication.class, args);
    }

}
