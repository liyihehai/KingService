package com.nnte.kservice;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

@Component
@Configuration
@ConfigurationProperties(prefix = "server")

@Getter
@Setter
public class KingServiceConfig {
    @Bean(initMethod = "initMain")
    public KingServiceCompnent kingServiceCompnent(){
        return new KingServiceCompnent();
    }
    private String port;
}
