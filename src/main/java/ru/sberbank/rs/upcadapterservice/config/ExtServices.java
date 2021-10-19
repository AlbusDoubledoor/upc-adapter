package ru.sberbank.rs.upcadapterservice.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@Getter
@Setter
@Configuration
@EnableConfigurationProperties
@PropertySource(value ={"classpath:extservices.yml"}, factory = YamlPropertyLoaderFactory.class)
@ConfigurationProperties("extservices")
public class ExtServices {
    private ExtServiceDefinition getRelatedIndividual;

    @Getter
    @Setter
    public static class ExtServiceDefinition {
       private long timeout;
       private String url;
       private String name;
       private boolean transformRequest;
       private boolean transformResponse;
    }
}
