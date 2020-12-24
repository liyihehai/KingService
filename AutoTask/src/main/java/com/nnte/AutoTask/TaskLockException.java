package com.nnte.AutoTask;

import com.nnte.basebusi.excption.BusiException;

public class TaskLockException extends BusiException {
    public TaskLockException(String msg){
        super(msg);
        setExpCode(3100);
        setExpLevel(ExpLevel.ERROR);
    }
}
