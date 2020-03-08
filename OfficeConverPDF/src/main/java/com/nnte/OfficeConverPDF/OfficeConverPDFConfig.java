package com.nnte.OfficeConverPDF;

import com.nnte.framework.base.NConfigI;
import com.nnte.framework.utils.OSinfo;
import com.nnte.framework.utils.StringUtils;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "nnte.ks.officeconverpdf")

public class OfficeConverPDFConfig implements NConfigI {
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

    @Override
    public String getLocalConfig(String type, String key) {
        if ("openofficeHome".equals(key)) {
            return getOpenofficeHome();
        }
        return openofficeHome;
    }

    @Override
    public boolean setLocalConfig(String type, String key, String val) {
        return false;
    }
}
