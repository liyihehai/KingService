package com.nnte.kservice.controller;

import com.nnte.OfficeConverPDF.OfficeConverPDFComponent;
import com.nnte.fdfs_client_mgr.FdfsClientMgrComponent;
import com.nnte.fdfs_client_mgr.FdfsClientMgrException;
import com.nnte.framework.base.BaseNnte;
import com.nnte.framework.utils.FileUtil;
import com.nnte.framework.utils.HttpUtil;
import com.nnte.framework.utils.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;
import java.util.UUID;

@CrossOrigin
@Controller
@RequestMapping(value = "/OfficeConverPDF")
public class OfficeConverPDFController {
    @Autowired
    private OfficeConverPDFComponent officeConverPDFComponent;
    @Autowired
    private FdfsClientMgrComponent fdfsClientMgrComponent;

    @RequestMapping("/convertOffice2Pdf")
    @ResponseBody
    public Map<String, Object> convertOffice2Pdf(HttpServletRequest request, String srcFileName){
        Map<String,Object> ret = BaseNnte.newMapRetObj();
        String sPdf=officeConverPDFComponent.converOfficeFile(srcFileName);
        if (StringUtils.isNotEmpty(sPdf)){
            ret.put("pdfFileName",sPdf);
            BaseNnte.setRetTrue(ret,"文件转换成功!");
        }else
            BaseNnte.setRetFalse(ret,1002,"文件转换失败!");
        return ret;
    }

    @RequestMapping("/office2PdfBytes")
    @ResponseBody
    public String office2PdfBytes(HttpServletRequest request,String fileName,String type,
                                  HttpServletResponse response){
        Map<String,Object> ret = BaseNnte.newMapRetObj();
        String tmpfile=null;
        String sPdf=null;
        try {
            byte[] buf=HttpUtil.recvHttpFile(request, response);
            if (buf!=null && buf.length>0){
                String tmpFolder=System.getProperty("java.io.tmpdir");
                tmpfile= tmpFolder+UUID.randomUUID().toString()+"."+FileUtil.getExtention(fileName);
                FileUtil.writeFile(tmpfile,buf);
                sPdf=officeConverPDFComponent.converOfficeFile(tmpfile);
                if (StringUtils.isNotEmpty(sPdf)){
                    //如果文件转换成功，需要将文件上传至文件服务器FastDFS
                    String officFile=fdfsClientMgrComponent.uploadFile(type,tmpfile);
                    if (StringUtils.isEmpty(officFile))
                        throw new FdfsClientMgrException("上传office文件至文件服务器失败");
                    String pdfFile=fdfsClientMgrComponent.uploadFile(type,sPdf);
                    if (StringUtils.isEmpty(pdfFile)){
                        fdfsClientMgrComponent.deleteFile(type,officFile);
                        throw new FdfsClientMgrException("上传pdf文件至文件服务器失败");
                    }
                    ret.put("officeFile",officFile);
                    ret.put("pdfFile",pdfFile);
                    BaseNnte.setRetTrue(ret,"文件转换上传成功!");
                }else {
                    BaseNnte.setRetFalse(ret, 1002, "文件转换失败!");
                }
            }
        }catch (Exception e){
            BaseNnte.setRetFalse(ret,1002,"文件转换上传异常!"+e.getMessage());
        }finally {
            try {
                if (StringUtils.isNotEmpty(tmpfile))
                    FileUtil.delFile(tmpfile);
                if (StringUtils.isNotEmpty(sPdf))
                    FileUtil.delFile(sPdf);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return ret.toString();
    }
}
