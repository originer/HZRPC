package hzradmin.hzradmin;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.support.SpringBootServletInitializer;

@SpringBootApplication
public class AdminApplication extends SpringBootServletInitializer {

	protected SpringApplicationBuilder config(SpringApplicationBuilder applicationBuilder){
		return applicationBuilder.sources(AdminApplication.class);
	}
	public static void main(String[] args) {
		SpringApplication.run(AdminApplication.class, args);
	}
}
