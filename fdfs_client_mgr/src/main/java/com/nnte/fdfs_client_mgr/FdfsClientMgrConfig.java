package com.nnte.fdfs_client_mgr;

import com.nnte.framework.base.NConfigI;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "nnte.ks.fdfsclientmgr")
public class FdfsClientMgrConfig implements NConfigI {
    private String propertiesFile;
    private String groupcount;
    @Override
    public String getLocalConfig(String type, String key) {
        if ("propertiesFile".equals(key))
            return propertiesFile;
        else if ("groupcount".equals(key))
            return groupcount;
        return null;
    }
    @Override
    public boolean setLocalConfig(String type, String key, String val) {
        return false;
    }

    public void setPropertiesFile(String propertiesFile) {
        this.propertiesFile = propertiesFile;
    }

    public void setGroupcount(String groupcount) {
        this.groupcount = groupcount;
    }
}
