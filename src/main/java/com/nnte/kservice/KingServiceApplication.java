package com.nnte.kservice;

import com.nnte.framework.base.BaseNnte;
import com.nnte.framework.utils.NumberUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.server.WebServerFactoryCustomizer;
import org.springframework.boot.web.servlet.server.ConfigurableServletWebServerFactory;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class})
@ComponentScan("com.nnte")
public class KingServiceApplication extends SpringBootServletInitializer
        implements WebServerFactoryCustomizer<ConfigurableServletWebServerFactory>
{
    @Autowired
    public KingServiceConfig kingServiceConfig;

    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder application){
        return application.sources(KingServiceApplication.class);
    }

    public static void main(String[] args)
    {
        SpringApplication.run(KingServiceApplication.class, args);
    }

    //下边customize方法实现EmbeddedServletContainerCustomizer 接口,实现更改tomcat端口号的需求
    @Override
    public void customize(ConfigurableServletWebServerFactory factory) {
        BaseNnte.outConsoleLog("server port="+kingServiceConfig.getPort());
        factory.setPort(NumberUtil.getDefaultInteger(kingServiceConfig.getPort()));
    }
}
