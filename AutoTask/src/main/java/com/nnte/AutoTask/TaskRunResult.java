package com.nnte.AutoTask;

import com.nnte.basebusi.excption.BusiException;

import java.util.Date;

public class TaskRunResult {
    private String taskCode;
    private boolean isSucceed;
    private BusiException exception;
    private Date runStartTime;
    private Date runEndTime;

    public TaskRunResult(String code,boolean isSuc,BusiException exp,
                         Date startTime,Date endTime){
        taskCode = code;
        isSucceed = isSuc;
        exception = exp;
        runStartTime = startTime;
        runEndTime = endTime;
    }

    public boolean isSucceed() {
        return isSucceed;
    }

    public void setSucceed(boolean succeed) {
        isSucceed = succeed;
    }

    public BusiException getException() {
        return exception;
    }

    public void setException(BusiException exception) {
        this.exception = exception;
    }

    public Date getRunStartTime() {
        return runStartTime;
    }

    public void setRunStartTime(Date runStartTime) {
        this.runStartTime = runStartTime;
    }

    public Date getRunEndTime() {
        return runEndTime;
    }

    public void setRunEndTime(Date runEndTime) {
        this.runEndTime = runEndTime;
    }

    public String getTaskCode() {
        return taskCode;
    }

    public void setTaskCode(String taskCode) {
        this.taskCode = taskCode;
    }
}
