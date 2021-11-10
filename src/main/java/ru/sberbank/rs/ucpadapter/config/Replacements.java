package ru.sberbank.rs.ucpadapter.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

import java.util.List;

@Getter
@Setter
@Configuration
@EnableConfigurationProperties
@PropertySource(value ={"file:/deployments/config/replacements.yml"}, factory = YamlPropertyLoaderFactory.class)
@ConfigurationProperties("replacements")
public class Replacements {
    private List<Replacement> replacementList;

    @Getter
    @Setter
    public static class Replacement {
        private String service;
        private String source;
        private String target;
    }
}