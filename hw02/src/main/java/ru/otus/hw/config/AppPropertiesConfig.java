package ru.otus.hw.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;

import java.util.Optional;

@Configuration
@PropertySource("classpath:/application.properties")
public class AppPropertiesConfig {

    @Bean
    public AppProperties appProperties(Environment environment) {
        AppProperties appProperties = new AppProperties();

        Optional.ofNullable(environment.getProperty("test.rightAnswersCountToPass", int.class))
                .ifPresent(appProperties::setRightAnswersCountToPass);
        Optional.ofNullable(environment.getProperty("test.fileName"))
                .ifPresent(appProperties::setTestFileName);
        Optional.ofNullable(environment.getProperty("test.TestFileHeaderSize", int.class))
                .ifPresent(appProperties::setTestFileHeaderSize);

        return appProperties;
    }
}
