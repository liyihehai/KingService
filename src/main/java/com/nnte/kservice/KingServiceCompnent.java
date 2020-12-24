package com.nnte.kservice;

import com.nnte.OfficeConverPDF.OfficeConverPDFComponent;
import com.nnte.basebusi.base.BaseBusiComponent;
import com.nnte.basebusi.base.WatchComponent;
import com.nnte.basebusi.excption.BusiException;
import com.nnte.fdfs_client_mgr.FdfsClientMgrComponent;
import com.nnte.framework.base.BaseNnte;
import com.nnte.framework.base.SpringContextHolder;
import org.springframework.beans.factory.annotation.Autowired;

public class KingServiceCompnent {

    @Autowired
    WatchComponent watchComponent;

    public void initMain() throws BusiException {
        BaseNnte.outConsoleLog("KingService服务程序初始化......");
        FdfsClientMgrComponent fdfsClientMgrComponent = SpringContextHolder.getBean(FdfsClientMgrComponent.class);
        fdfsClientMgrComponent.runFdfsClientMgr(null);
        OfficeConverPDFComponent converPDFComponent = SpringContextHolder.getBean(OfficeConverPDFComponent.class);
        converPDFComponent.startOpenofficeManager();
        //装载系统模块入口,也是框架环境初始化函数--
        BaseBusiComponent.loadSystemFuntionEnters(null);
        //-------------------------------------
        //--启动程序守护线程，注册组件（系统参数）
        watchComponent.setSleepSeconds(60*1000);//60秒钟启动一次
        watchComponent.startWatch();
        //-------------------------------------
    }
}
