package com.nnte.kservice;

import com.nnte.OfficeConverPDF.OfficeConverPDFComponent;
import com.nnte.OfficeConverPDF.OfficeConverPDFConfig;
import com.nnte.framework.base.BaseNnte;
import com.nnte.framework.base.SpringContextHolder;
import com.nnte.kr_business.component.base.KingReportComponent;

public class KingServiceCompnent {
    public void initMain(){
        BaseNnte.outConsoleLog("程序启动......");
        OfficeConverPDFConfig Config= SpringContextHolder.getBean("officeConverPDFConfig");
        KingReportComponent.LoadConfigComponent(Config);
        OfficeConverPDFComponent converPDFComponent = SpringContextHolder.getBean("officeConverPDFComponent");
        BaseNnte.outConsoleLog("开始连接office......");
        converPDFComponent.startOpenofficeManager();
    }
}
