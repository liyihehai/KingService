package com.nnte.fdfs_client_mgr;

import com.nnte.framework.base.ConfigInterface;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "nnte.ks.fdfsclientmgr")

public class FdfsClientMgrConfig implements ConfigInterface {
    private String propertiesFile;
    private String groupcount;
}
