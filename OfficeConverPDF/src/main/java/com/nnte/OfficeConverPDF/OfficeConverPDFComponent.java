package com.nnte.OfficeConverPDF;

import com.nnte.framework.base.BaseNnte;
import com.nnte.kr_business.base.Office2PDF;
import org.artofsolving.jodconverter.office.OfficeManager;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class OfficeConverPDFComponent implements DisposableBean {
    @Autowired
    public OfficeConverPDFConfig cConfig;

    //启动连接Openoffice
    public OfficeManager startOpenofficeManager(){
        if (Office2PDF.officeManager==null){
            Office2PDF.setOfficeHome(cConfig.getOpenofficeHome());
            Office2PDF.officeManager=Office2PDF.getOfficeManager();
            BaseNnte.outConsoleLog("连接Openoffice["+cConfig.getOpenofficeHome()+"]成功!");
        }
        if (Office2PDF.officeManager==null)
            BaseNnte.outConsoleLog("连接Openoffice["+cConfig.getOpenofficeHome()+"]失败......");
        return Office2PDF.officeManager;
    }
    //断开连接Openoffice
    public void closeOpenofficeManager(){
        if (Office2PDF.officeManager!=null){
            Office2PDF.officeManager.stop();
            Office2PDF.officeManager=null;
        }
    }
    //执行文件转换
    public String converOfficeFile(String srcFile){
        return Office2PDF.openOfficeToPDF(srcFile);
    }

    @Override
    public void destroy() throws Exception {
        try {
            closeOpenofficeManager();
        }catch (Exception e){
            throw new Exception("关闭连接Openoffice异常");
        }
    }

    public static void main(String[] args){
    }
}
