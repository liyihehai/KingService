package com.nnte.kservice;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

@Component
@Configuration
@ConfigurationProperties(prefix = "server")

public class KingServiceConfig {
    @Bean(initMethod = "initMain")
    public KingServiceCompnent kingServiceCompnent(){
        return new KingServiceCompnent();
    }

    private String port;

    public String getPort() {
        return port;
    }

    public void setPort(String port) {
        this.port = port;
    }
}
