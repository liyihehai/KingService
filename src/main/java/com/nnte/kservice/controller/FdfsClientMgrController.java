package com.nnte.kservice.controller;

import com.nnte.fdfs_client_mgr.FdfsClientMgrComponent;
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
@RequestMapping(value = "/FdfsClientMgr")
public class FdfsClientMgrController {
    @Autowired
    private FdfsClientMgrComponent fdfsClientMgrComponent;
    @RequestMapping("/srcFileUpload")
    @ResponseBody
    public Map<String, Object> srcFileUpload(HttpServletRequest request, String srcFileName){
        Map<String,Object> ret = BaseNnte.newMapRetObj();
        String sfileName=fdfsClientMgrComponent.uploadFile("webstatic",srcFileName);
        if (StringUtils.isNotEmpty(sfileName)){
            ret.put("sfileName",sfileName);
            BaseNnte.setRetTrue(ret,"文件上传成功!");
        }else
            BaseNnte.setRetFalse(ret,1002,"文件上传失败!");
        return ret;
    }

    @RequestMapping("/srcFileDelete")
    @ResponseBody
    public Map<String, Object> srcFileDelete(HttpServletRequest request, String srcFileName){
        Map<String,Object> ret = BaseNnte.newMapRetObj();
        int err=fdfsClientMgrComponent.deleteFile("webstatic",srcFileName);
        if (err==0){
            BaseNnte.setRetTrue(ret,"文件删除成功!");
        }else
            BaseNnte.setRetFalse(ret,1002,"文件删除失败(err="+new Integer(err)+")!");
        return ret;
    }
}
