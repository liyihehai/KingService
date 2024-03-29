package com.nnte.OfficeConverPDF;

import com.nnte.basebusi.base.BaseComponent;
import com.nnte.basebusi.excption.BusiException;
import org.artofsolving.jodconverter.office.OfficeManager;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class OfficeConverPDFComponent extends BaseComponent implements DisposableBean {
    @Autowired
    public OfficeConverPDFConfig cConfig;

    //启动连接Openoffice
    public OfficeManager startOpenofficeManager(){
        outLogInfo("开始连接Openoffice["+cConfig.getOpenofficeHome()+"]......");
        if (Office2PDF.officeManager==null){
            Office2PDF.officeManager=Office2PDF.getOfficeManager(cConfig.getOpenofficeHome());
            outLogInfo("连接Openoffice["+cConfig.getOpenofficeHome()+"]成功!");
        }
        if (Office2PDF.officeManager==null)
            outLogInfo("连接Openoffice["+cConfig.getOpenofficeHome()+"]失败......");
        return Office2PDF.officeManager;
    }
    //断开连接Openoffice
    public void closeOpenofficeManager(){
        if (Office2PDF.officeManager!=null){
            Office2PDF.officeManager.stop();
            Office2PDF.officeManager=null;
        }
        outLogInfo("断开Openoffice["+cConfig.getOpenofficeHome()+"]连接......");
    }
    //执行文件转换
    public String converOfficeFile(String srcFile){
        return Office2PDF.openOfficeToPDF(srcFile);
    }

    @Override
    public void destroy() throws BusiException {
        try {
            closeOpenofficeManager();
        }catch (Exception e){
            outLogInfo("关闭连接Openoffice异常");
            throw new BusiException(e);
        }
    }
}
