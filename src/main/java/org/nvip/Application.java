

package org.nvip;

import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvValidationException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.nvip.util.CVSSVector;
import org.nvip.util.CvssGenUtil;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ResourceLoader;
import org.springframework.http.HttpHeaders;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import javax.servlet.Filter;
import javax.servlet.FilterRegistration;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;


@SpringBootApplication
@Configuration
public class Application {

    private static final Logger logger = LogManager.getLogger(Application.class);

    public static void main(String... args) {
        SpringApplication.run(Application.class, args);
    }

    //todo: is this still needed
    @Bean
    public WebMvcConfigurer corsConfigurer()
    {
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                registry.addMapping("/**")
                        .allowedOrigins(
                                "http://localhost:4200",
                                "https://www-staging.cve.live",
                                "https://www.cve.live"
                        );
            }
        };
    }

    @Bean
    public FilterRegistrationBean corsFilterRegistration() {
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowCredentials(true);
        config.addAllowedOrigin("http://localhost:4200");
        config.addAllowedOrigin("https://www-staging.cve.live");
        config.addAllowedOrigin("https://www.cve.live");
        config.setAllowedHeaders(Arrays.asList(
                HttpHeaders.AUTHORIZATION,
                HttpHeaders.CONTENT_TYPE,
                HttpHeaders.ACCEPT
        ));
        config.setAllowedMethods(Arrays.asList(
                "GET",
                "POST",
                "PUT",
                "DELETE"
        ));
        source.registerCorsConfiguration("/**", config);
        FilterRegistrationBean registration = new FilterRegistrationBean(new CorsFilter(source));
        registration.setOrder(-102);
        return registration;
    }

    @Bean CvssGenUtil cvssGenUtil(ResourceLoader resourceLoader) throws IOException, CsvValidationException{

        Map<CVSSVector, Double> scoreTable = new HashMap<>();
        try (CSVReader reader = new CSVReader(new InputStreamReader(resourceLoader.getResource("classpath:cvss_map.csv").getInputStream()))) {
            String[] line;
            while ((line=reader.readNext()) != null) {
                scoreTable.put(new CVSSVector(line[0]), Double.parseDouble(line[1]));
            }
        } catch (IOException | CsvValidationException e) {
            logger.error("Error while loading CVSS score map");
            throw e;
        }

        return new CvssGenUtil(scoreTable);
    }
}
