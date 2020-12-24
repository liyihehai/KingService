package com.nnte.kservice.controller;

import com.nnte.AutoTask.AutoTaskComponent;
import com.nnte.AutoTask.TaskInfo;
import com.nnte.basebusi.excption.BusiException;
import com.nnte.framework.base.BaseNnte;
import com.nnte.framework.utils.JsonUtil;
import com.nnte.framework.utils.NumberDefUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;
import java.util.Map;

@CrossOrigin
@Controller
@RequestMapping(value = "/AutoTaskManage")
public class AutoTaskManageController {
    @Autowired
    private AutoTaskComponent autoTaskComponent;

    @RequestMapping(value = "/queryTaskInfoByCode",produces = "application/json;charset=UTF-8")
    @ResponseBody
    public Object queryTaskInfoByCode(@RequestParam String json){
        Map<String,Object> ret = BaseNnte.newMapRetObj();
        try {
            JsonUtil.JNode jnode=JsonUtil.createJNode(JsonUtil.jsonToNode(json));
            String taskCode= NumberDefUtil.getDefString(jnode.getText("taskCode"));
            TaskInfo taskInfo=autoTaskComponent.getTaskInfo(taskCode);
            ret.put("taskInfo",taskInfo);
            BaseNnte.setRetTrue(ret,"success");
        }catch (BusiException e){
            BaseNnte.setRetFalse(ret,e.getExpCode(),BaseNnte.getExpMsg(e));
        }catch (Exception e){
            BaseNnte.setRetFalse(ret,9999,BaseNnte.getExpMsg(e));
        }
        return ret;
    }

    @RequestMapping(value = "/notifyTaskRun",produces = "application/json;charset=UTF-8")
    @ResponseBody
    public Object notifyTaskRun(@RequestParam String json){
        Map<String,Object> ret = BaseNnte.newMapRetObj();
        try {
            JsonUtil.JNode jnode=JsonUtil.createJNode(JsonUtil.jsonToNode(json));
            String taskCode= NumberDefUtil.getDefString(jnode.getText("taskCode"));
            TaskInfo taskInfo=autoTaskComponent.notifyTaskRun(taskCode);
            ret.put("taskInfo",taskInfo);
            BaseNnte.setRetTrue(ret,"success");
        }catch (BusiException e){
            BaseNnte.setRetFalse(ret,e.getExpCode(),BaseNnte.getExpMsg(e));
        }catch (Exception e){
            BaseNnte.setRetFalse(ret,9999,BaseNnte.getExpMsg(e));
        }
        return ret;
    }

    @RequestMapping(value = "/startTaskListen",produces = "application/json;charset=UTF-8")
    @ResponseBody
    public Object startTaskListen(@RequestParam String json){
        Map<String,Object> ret = BaseNnte.newMapRetObj();
        try {
            JsonUtil.JNode jnode=JsonUtil.createJNode(JsonUtil.jsonToNode(json));
            String taskCode= NumberDefUtil.getDefString(jnode.getText("taskCode"));
            TaskInfo taskInfo=autoTaskComponent.startTaskListen(taskCode);
            ret.put("taskInfo",taskInfo);
            BaseNnte.setRetTrue(ret,"success");
        }catch (BusiException e){
            BaseNnte.setRetFalse(ret,e.getExpCode(),BaseNnte.getExpMsg(e));
        }catch (Exception e){
            BaseNnte.setRetFalse(ret,9999,BaseNnte.getExpMsg(e));
        }
        return ret;
    }

    @RequestMapping(value = "/stopTaskListen",produces = "application/json;charset=UTF-8")
    @ResponseBody
    public Object stopTaskListen(@RequestParam String json){
        Map<String,Object> ret = BaseNnte.newMapRetObj();
        try {
            JsonUtil.JNode jnode=JsonUtil.createJNode(JsonUtil.jsonToNode(json));
            String taskCode= NumberDefUtil.getDefString(jnode.getText("taskCode"));
            TaskInfo taskInfo=autoTaskComponent.stopTaskListen(taskCode);
            ret.put("taskInfo",taskInfo);
            BaseNnte.setRetTrue(ret,"success");
        }catch (BusiException e){
            BaseNnte.setRetFalse(ret,e.getExpCode(),BaseNnte.getExpMsg(e));
        }catch (Exception e){
            BaseNnte.setRetFalse(ret,9999,BaseNnte.getExpMsg(e));
        }
        return ret;
    }

    @RequestMapping(value = "/getAllTaskInfo",produces = "application/json;charset=UTF-8")
    @ResponseBody
    public Object getAllTaskInfo(){
        Map<String,Object> ret = BaseNnte.newMapRetObj();
        try {
            List<TaskInfo> list=autoTaskComponent.getAllTaskInfo();
            ret.put("list",list);
            BaseNnte.setRetTrue(ret,"success");
        }catch (Exception e){
            BaseNnte.setRetFalse(ret,9999,BaseNnte.getExpMsg(e));
        }
        return ret;
    }
}
