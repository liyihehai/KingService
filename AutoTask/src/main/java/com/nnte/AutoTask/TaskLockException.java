package com.nnte.AutoTask;

import com.nnte.basebusi.excption.BusiException;
import com.nnte.framework.utils.LogUtil;

public class TaskLockException extends BusiException {
    public TaskLockException(String msg){
        super(msg);
        setExpCode(3100);
        setExpLevel(LogUtil.LogLevel.error);
    }
}
