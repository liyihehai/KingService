package com.nnte.AutoTask;

public class TaskInfo {
    private String taskCode;
    private String taskName;
    private String taskMethod;
    private String taskBean;                //组件Class
    private String thread;                  //线程句柄
    private boolean isRunTask;              //是否执行接口函数
    private int taskRunTimes;               //任务执行次数(从任务加载到当前的任务执行次数)
    private String lastTaskUUID;            //任务执行ID
    private boolean isLastTaskSuc;          //上次任务执行是否成功
    private int taskRunExceptTimes;         //任务执行异常次数
    private String taskRunStateName;        //任务执行状态:0未执行，2等待执行，1执行中
    private String listenStartTime;         //监听开始时间
    private String lastRunStartTime;        //上次执行开始时间
    private String lastRunEndTime;          //上次执行结束时间
    private String lastRunExceptionTime;    //上次执行异常时间
    private String nextRunStartTime;        //下次开始执行时间
    private String lastRunExceptionMsg;     //上次执行异常的错误信息
    private boolean isListen;               //是否监听
    private String taskListenStatusName;    //任务监听状态:0未监听，

    public String getTaskMethod() {
        return taskMethod;
    }

    public void setTaskMethod(String taskMethod) {
        this.taskMethod = taskMethod;
    }

    public String getTaskBean() {
        return taskBean;
    }

    public void setTaskBean(String taskBean) {
        this.taskBean = taskBean;
    }

    public String getThread() {
        return thread;
    }

    public void setThread(String thread) {
        this.thread = thread;
    }

    public boolean isRunTask() {
        return isRunTask;
    }

    public void setRunTask(boolean runTask) {
        isRunTask = runTask;
    }

    public int getTaskRunTimes() {
        return taskRunTimes;
    }

    public void setTaskRunTimes(int taskRunTimes) {
        this.taskRunTimes = taskRunTimes;
    }

    public String getLastTaskUUID() {
        return lastTaskUUID;
    }

    public void setLastTaskUUID(String lastTaskUUID) {
        this.lastTaskUUID = lastTaskUUID;
    }

    public boolean isLastTaskSuc() {
        return isLastTaskSuc;
    }

    public void setLastTaskSuc(boolean lastTaskSuc) {
        isLastTaskSuc = lastTaskSuc;
    }

    public int getTaskRunExceptTimes() {
        return taskRunExceptTimes;
    }

    public void setTaskRunExceptTimes(int taskRunExceptTimes) {
        this.taskRunExceptTimes = taskRunExceptTimes;
    }

    public String getTaskRunStateName() {
        return taskRunStateName;
    }

    public void setTaskRunStateName(String taskRunStateName) {
        this.taskRunStateName = taskRunStateName;
    }

    public String getLastRunStartTime() {
        return lastRunStartTime;
    }

    public void setLastRunStartTime(String lastRunStartTime) {
        this.lastRunStartTime = lastRunStartTime;
    }

    public String getLastRunEndTime() {
        return lastRunEndTime;
    }

    public void setLastRunEndTime(String lastRunEndTime) {
        this.lastRunEndTime = lastRunEndTime;
    }

    public String getLastRunExceptionTime() {
        return lastRunExceptionTime;
    }

    public void setLastRunExceptionTime(String lastRunExceptionTime) {
        this.lastRunExceptionTime = lastRunExceptionTime;
    }

    public String getNextRunStartTime() {
        return nextRunStartTime;
    }

    public void setNextRunStartTime(String nextRunStartTime) {
        this.nextRunStartTime = nextRunStartTime;
    }

    public String getLastRunExceptionMsg() {
        return lastRunExceptionMsg;
    }

    public void setLastRunExceptionMsg(String lastRunExceptionMsg) {
        this.lastRunExceptionMsg = lastRunExceptionMsg;
    }

    public boolean isListen() {
        return isListen;
    }

    public void setListen(boolean listen) {
        isListen = listen;
    }

    public String getTaskListenStatusName() {
        return taskListenStatusName;
    }

    public void setTaskListenStatusName(String taskListenStatusName) {
        this.taskListenStatusName = taskListenStatusName;
    }

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

    public String getListenStartTime() {
        return listenStartTime;
    }

    public void setListenStartTime(String listenStartTime) {
        this.listenStartTime = listenStartTime;
    }
}
