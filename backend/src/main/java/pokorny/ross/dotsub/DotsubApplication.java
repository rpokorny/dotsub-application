package pokorny.ross.dotsub;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Bean;

import org.ebaysf.web.cors.CORSFilter;

@SpringBootApplication
@Configuration
public class DotsubApplication {

    public static void main(String[] args) {
        SpringApplication.run(DotsubApplication.class, args);
    }

    @Bean
    public CORSFilter corsFilter() {
        return new CORSFilter();
    }
}
