package hzr.spring.provider.exmple;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Slf4j
@Configuration
@Controller
@SpringBootApplication
@RequestMapping("/test")
@ComponentScan(basePackages = "hzr.spring.provider.*")
public class SpringClientConfig {
    public static void main(String[] args) {
        SpringApplication.run(SpringClientConfig.class, "--server.port=9092");
    }
}
