package com.nnte.kservice;

import com.nnte.OfficeConverPDF.OfficeConverPDFComponent;
import com.nnte.fdfs_client_mgr.FdfsClientMgrComponent;
import com.nnte.framework.base.BaseNnte;
import com.nnte.framework.base.SpringContextHolder;

public class KingServiceCompnent {
    public void initMain(){
        BaseNnte.outConsoleLog("KingService服务程序初始化......");
        FdfsClientMgrComponent fdfsClientMgrComponent = SpringContextHolder.getBean("fdfsClientMgrComponent");
        fdfsClientMgrComponent.runFdfsClientMgr(null);
        OfficeConverPDFComponent converPDFComponent = SpringContextHolder.getBean("officeConverPDFComponent");
        converPDFComponent.startOpenofficeManager();
    }
}
