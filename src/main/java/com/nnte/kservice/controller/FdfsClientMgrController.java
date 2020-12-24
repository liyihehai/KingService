package com.nnte.kservice.controller;

import com.nnte.fdfs_client_mgr.FdfsClientMgrComponent;
import com.nnte.framework.base.BaseNnte;
import com.nnte.framework.utils.FileUtil;
import com.nnte.framework.utils.HttpServletUtil;
import com.nnte.framework.utils.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;

@CrossOrigin
@Controller
@RequestMapping(value = "/FdfsClientMgr")
public class FdfsClientMgrController {
    @Autowired
    private FdfsClientMgrComponent fdfsClientMgrComponent;
    @RequestMapping("/srcFileUpload")
    @ResponseBody
    public Map<String, Object> srcFileUpload(HttpServletRequest request,String type, String srcFileName){
        Map<String,Object> ret = BaseNnte.newMapRetObj();
        String sfileName=fdfsClientMgrComponent.uploadFile(type,srcFileName);
        if (StringUtils.isNotEmpty(sfileName)){
            ret.put("sfileName",sfileName);
            BaseNnte.setRetTrue(ret,"文件上传成功!");
        }else
            BaseNnte.setRetFalse(ret,1002,"文件上传失败!");
        return ret;
    }

    /**
     * 接受客户端上传的文件并上传到文件服务器，返回文件的ID
     * @param request
     * @param fileName：文件名称，用于提取文件的类型
     * @param type：文件业务类型，用文件保存分组 group
     * @param response
     * @return
     */
    @RequestMapping("/fileUploadBytes")
    @ResponseBody
    public Map<String, Object> fileUploadBytes(HttpServletRequest request,String fileName,String type,
                                               HttpServletResponse response){
        Map<String,Object> ret = BaseNnte.newMapRetObj();
        try {
            byte[] buf= HttpServletUtil.recvHttpFile(request);
            if (buf!=null && buf.length>0){
                String submitName=fdfsClientMgrComponent.uploadFile(type,
                        buf,FileUtil.getExtention(fileName));
                if (StringUtils.isNotEmpty(submitName)){
                    ret.put("submitName",submitName);
                    BaseNnte.setRetTrue(ret,"文件上传成功!");
                }else {
                    BaseNnte.setRetFalse(ret, 1002, "文件上传失败!");
                }
            }
            buf=null;
        }catch (Exception e){
            BaseNnte.setRetFalse(ret,1002,"文件上传异常!"+BaseNnte.getExpMsg(e));
        }
        return ret;
    }

    @RequestMapping("/submitFileDelete")
    @ResponseBody
    public Map<String, Object> submitFileDelete(HttpServletRequest request, String type,String submitName){
        Map<String,Object> ret = BaseNnte.newMapRetObj();
        int err=fdfsClientMgrComponent.deleteFile(type,submitName);
        if (err==0){
            BaseNnte.setRetTrue(ret,"文件删除成功!");
        }else
            BaseNnte.setRetFalse(ret,1002,"文件删除失败(err="+new Integer(err)+")!");
        return ret;
    }
}
