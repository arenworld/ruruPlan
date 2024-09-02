package net.datasa.ruruplan;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@EnableJpaAuditing
@SpringBootApplication
public class RuruPlanApplication {

    public static void main(String[] args) {
        SpringApplication.run(RuruPlanApplication.class, args);
    }

}
