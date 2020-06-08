package com.nnte.OfficeConverPDF;

import com.nnte.framework.base.ConfigInterface;
import com.nnte.framework.utils.OSinfo;
import com.nnte.framework.utils.StringUtils;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "nnte.ks.officeconverpdf")

public class OfficeConverPDFConfig implements ConfigInterface {
    private String openofficeHome;

    public String getOpenofficeHome() {
        if (StringUtils.isEmpty(openofficeHome)){
            if (OSinfo.isWindows())
                return "C:/Program Files (x86)/OpenOffice 4";
            else
                return "/opt/openoffice4";
        }
        return openofficeHome;
    }
    public void setOpenofficeHome(String openofficeHome) {
        this.openofficeHome = openofficeHome;
    }
}
