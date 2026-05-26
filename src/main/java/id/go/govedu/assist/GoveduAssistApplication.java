package id.go.govedu.assist;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class GoveduAssistApplication {

    public static void main(String[] args) {
        SpringApplication.run(GoveduAssistApplication.class, args);
    }

}
