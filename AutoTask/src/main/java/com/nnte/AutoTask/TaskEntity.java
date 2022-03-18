package com.nnte.AutoTask;

import com.nnte.basebusi.base.BaseComponent;

public class TaskEntity extends BaseComponent {
    private String taskCode;        //任务代码
    private String taskName;        //任务名称
    private String taskComponent;   //组件名称
    private String taskMethod;      //任务函数
    private String taskParamJson;   //组件执行参数,json格式
    private boolean isTaskRun;      //是否运行

    public String getTaskCode() {
        return taskCode;
    }

    public void setTaskCode(String taskCode) {
        this.taskCode = taskCode;
    }

    public String getTaskName() {
        return taskName;
    }

    public void setTaskName(String taskName) {
        this.taskName = taskName;
    }

    public String getTaskParamJson() {
        return taskParamJson;
    }

    public void setTaskParamJson(String taskParamJson) {
        this.taskParamJson = taskParamJson;
    }

    public String getTaskComponent() {
        return taskComponent;
    }

    public void setTaskComponent(String taskComponent) {
        this.taskComponent = taskComponent;
    }

    public boolean isTaskRun() {
        return isTaskRun;
    }

    public void setTaskRun(boolean taskRun) {
        isTaskRun = taskRun;
    }

    public String getTaskMethod() {
        return taskMethod;
    }

    public void setTaskMethod(String taskMethod) {
        this.taskMethod = taskMethod;
    }
}
