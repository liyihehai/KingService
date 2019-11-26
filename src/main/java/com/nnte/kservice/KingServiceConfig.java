package com.nnte.kservice;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class KingServiceConfig {
    @Bean(initMethod = "initMain")
    public KingServiceCompnent kingServiceCompnent(){
        return new KingServiceCompnent();
    }
}
