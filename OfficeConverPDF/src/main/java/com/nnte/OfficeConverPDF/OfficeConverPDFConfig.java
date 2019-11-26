package com.nnte.OfficeConverPDF;

import com.nnte.kr_business.base.NConfig;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "nnte.ks.officeconverpdf")
@PropertySource(value = "classpath:nnte-kingservice-officeconverpdf.properties")

public class OfficeConverPDFConfig extends NConfig {
    private String openofficeHome;

    public String getOpenofficeHome() {
        return openofficeHome;
    }

    public void setOpenofficeHome(String openofficeHome) {
        this.openofficeHome = openofficeHome;
    }
}
