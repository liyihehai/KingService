package com.nnte.kservice.controller;

import com.nnte.OfficeConverPDF.OfficeConverPDFComponent;
import com.nnte.framework.base.BaseNnte;
import com.nnte.framework.utils.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

@CrossOrigin
@Controller
@RequestMapping(value = "/OfficeConverPDF")
public class OfficeConverPDFController {
    @Autowired
    private OfficeConverPDFComponent officeConverPDFComponent;

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
}
