package com.nnte.AutoTask;

import com.nnte.basebusi.excption.BusiException;
import com.nnte.framework.base.BaseNnte;
import com.nnte.framework.utils.LogUtil;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Date;

/**
 * 自动任务接口，所有自动任务组件都必须实现该接口
 * */
public interface AutoTaskInterface {
    /**
     * 通过本函数取得自动任务的执行时间配置
     * */
    TaskRunTime getTaskRunTimeConfig(String code);
    /**
     * 任务执行之前调用，如果返回false则任务不再继续执行
     * */
    default boolean onBeforeTaskRun(String taskCode) throws BusiException{
        return true;
    }
    /**
     * 自动任务初始化，本接口只在接口组件加载后执行一次
     * */
    default void onAutoTaskBeanInit() throws BusiException{

    }
    /**
     * 任务执行之后调用
     * */
    default void onAfterTaskRun(TaskRunResult result) throws BusiException{
        if (result!=null && result.getException()!=null)
            throw result.getException();
    }
    /**
     * 通过本函数开始启动执行一次自动任务
     * */
    default void startRunTask(String taskCode,Method method,String paramJson) throws BusiException{
        if (method==null)
            throw new BusiException("自动任务入口函数为空");
        Date startTime = new Date();
        try {
            if (!onBeforeTaskRun(taskCode))
                throw new BusiException(22101,"调用前终止", LogUtil.LogLevel.info);
            method.invoke(this,paramJson);
            onAfterTaskRun(new TaskRunResult(taskCode,true,null,startTime,new Date()));
        } catch (IllegalAccessException e) {
            onAfterTaskRun(new TaskRunResult(taskCode,false,
                            new BusiException(e,9997, LogUtil.LogLevel.error),
                            startTime,new Date()));
        } catch (InvocationTargetException e) {
            onAfterTaskRun(new TaskRunResult(taskCode,false,
                    new BusiException(e,9998, LogUtil.LogLevel.error),
                    startTime,new Date()));
        } catch (BusiException e){
            onAfterTaskRun(new TaskRunResult(taskCode,false,
                    e,startTime,new Date()));
        } catch (Exception e){
            onAfterTaskRun(new TaskRunResult(taskCode,false,
                            new BusiException(9999, BaseNnte.getExpMsg(e), LogUtil.LogLevel.error),
                            startTime,new Date()));
        }
    }
}
