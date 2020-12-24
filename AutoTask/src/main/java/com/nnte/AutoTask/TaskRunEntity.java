package com.nnte.AutoTask;

import com.nnte.basebusi.base.BaseBusiComponent;
import com.nnte.basebusi.excption.BusiException;
import com.nnte.basebusi.excption.ExpLogInterface;
import com.nnte.framework.base.BaseNnte;
import com.nnte.framework.base.NameLockComponent;
import com.nnte.framework.base.SpringContextHolder;
import com.nnte.framework.utils.DateUtils;
import com.nnte.framework.utils.StringUtils;
import com.nnte.framework.utils.ThreadUtil;

import java.lang.reflect.Method;
import java.util.Date;
import java.util.UUID;

public class TaskRunEntity extends TaskEntity implements Runnable{

    public final static int err_code_tasklock_failed = 21101;
    public final static String err_msg_tasklock_failed = "取任务锁失败";

    private static long lockTimes = 5 * 1000;

    private Method taskRunMethod;
    private String taskRunBeanName;
    private Object taskRunBean;             //组件Bean
    private Thread threadHandle;            //线程句柄
    private boolean isRunTask;              //是否执行接口函数
    private int taskRunTimes=0;             //任务执行次数(从任务加载到当前的任务执行次数)
    private String lastTaskUUID;              //任务执行ID
    private boolean isLastTaskSuc;          //上次任务执行是否成功
    private int taskRunExceptTimes=0;       //任务执行异常次数
    private int taskRunState=0;             //任务执行状态:0未执行，2等待执行，1执行中
    private Date listenStartTime = null;    //监听开始时间
    private Date lastRunStartTime = null;   //上次执行开始时间
    private Date lastRunEndTime = null;         //上次执行结束时间
    private Date lastRunExceptionTime = null;   //上次执行异常时间
    private Date nextRunStartTime = null;       //下次开始执行时间
    private String lastRunExceptionMsg = "";    //上次执行异常的错误信息
    private ExpLogInterface logInterface;       //任务日志接口
    private NameLockComponent.NameReentrantLock taskLock;     //任务锁对象
    private boolean isListen;               //是否监听
    private int taskListenStatus;           //任务监听状态:0未监听，1已监听:处于监听状态的任务才能接受外部消息,2:监听挂起

    public static void setComponentTaskRunMethod(TaskRunEntity tre) throws BusiException{
        Object bean=SpringContextHolder.getBean(tre.getTaskComponent());
        if (bean==null)
            throw new BusiException("装载任务组件错误");
        if (!(bean instanceof AutoTaskInterface))
            throw new BusiException("组件不是合法的任务组件");
        tre.setTaskRunBean(bean);
        tre.setTaskRunBeanName(tre.getTaskComponent());
        try {
            Method method=bean.getClass().getMethod(tre.getTaskMethod(),String.class);
            tre.setTaskRunMethod(method);
        } catch (NoSuchMethodException e) {
            throw new BusiException("任务组件入口函数未找到");
        }
    }

    public TaskRunEntity(TaskEntity te,ExpLogInterface logInterface){
        this.setTaskCode(te.getTaskCode());
        this.setTaskName(te.getTaskName());
        this.setTaskComponent(te.getTaskComponent());
        this.setTaskMethod(te.getTaskMethod());
        this.setTaskParamJson(te.getTaskParamJson());
        this.setTaskListenStatus(0);//初始化时，任务的监听状态为0:未监听
        this.threadHandle = null;
        this.logInterface = logInterface;
        String taskLockName = "taskLockName_"+te.getTaskCode();
        this.taskLock = AutoTaskComponent.AutoTaskLock.getLockByName(taskLockName,true);
        this.setRunTask(te.isTaskRun());
        this.setListen(true);   //设置需要监听
    }
    /**
     * 拷贝任务信息
     * */
    public TaskInfo getTaskRunInfo(){
        TaskInfo info = new TaskInfo();
        info.setTaskCode(getTaskCode());
        info.setTaskName(getTaskName());
        info.setTaskMethod((taskRunMethod!=null)?taskRunMethod.getName():"");
        info.setTaskBean(this.taskRunBeanName);
        info.setThread((threadHandle!=null)?threadHandle.getName():"");
        info.setRunTask(isRunTask);
        info.setTaskRunTimes(taskRunTimes);
        info.setLastTaskUUID(lastTaskUUID);
        info.setLastTaskSuc(isLastTaskSuc);
        info.setTaskRunExceptTimes(taskRunExceptTimes);
        info.setTaskRunStateName(getTaskRunStateName());
        info.setListenStartTime((listenStartTime!=null)? DateUtils.dateToString(listenStartTime,DateUtils.DF_YMDHMS) :"");
        info.setLastRunStartTime((lastRunStartTime!=null)? DateUtils.dateToString(lastRunStartTime,DateUtils.DF_YMDHMS) :"");
        info.setLastRunEndTime((lastRunEndTime!=null)?DateUtils.dateToString(lastRunEndTime,DateUtils.DF_YMDHMS) :"");
        info.setLastRunExceptionTime((lastRunExceptionTime!=null)?DateUtils.dateToString(lastRunExceptionTime,DateUtils.DF_YMDHMS) :"");
        info.setNextRunStartTime((nextRunStartTime!=null)?DateUtils.dateToString(nextRunStartTime,DateUtils.DF_YMDHMS) :"");
        info.setLastRunExceptionMsg(lastRunExceptionMsg);
        info.setListen(isListen);
        info.setTaskListenStatusName(getTaskListenStatusName());
        return info;
    }
    /**
     * 获取任务锁
     * */
    private boolean getTaskLock(){
        return AutoTaskComponent.AutoTaskLock.TryLock(taskLock, lockTimes, null);
    }
    /**
     * 释放任务锁
     * */
    private void releaseTaskLock(){
        AutoTaskComponent.AutoTaskLock.ReleasLock(taskLock,null);
    }
    /**
     * 设置任务开始信息
     * */
    private void setTaskStartInfo() throws TaskLockException{
        lastTaskUUID = "";
        if (!getTaskLock())
            throw new TaskLockException("执行监听任务函数时取得任务锁失败");
        if (!isListen())
            throw new TaskLockException("任务监听为不监听，任务不再执行");
        if (!isRunTask)
            throw new TaskLockException("任务当前设置为不运行");
        if (this.getTaskListenStatus()!=1)
            throw new TaskLockException("任务未处于监听状态，不能设置任务开始信息");
        this.setTaskListenStatus(2);//设置监听状态为挂起
        taskRunState = 1; //设置任务执行状态为 1执行
        lastRunStartTime = new Date();
        nextRunStartTime = null;
        lastTaskUUID = UUID.randomUUID().toString();
        isLastTaskSuc = false;
    }

    private void setTaskEndInfo(){
        taskRunState = 0; //设置任务执行状态为0非执行
        this.setTaskListenStatus(1);//设置监听状态为监听
        if (!StringUtils.isEmpty(lastTaskUUID))
            lastRunEndTime = new Date();
    }

    /**
     * 打印任务执行日志
     * */
    private void printTaskLog(){
        try{
            StringBuffer logs = new StringBuffer();
            String uuid = this.getLastTaskUUID();
            logs.append("Task[uuid="+uuid+",code="+this.getTaskCode()+",name="+this.getTaskName()+",times="+taskRunTimes+"]\n");
            String milTimes="null";
            if (lastRunEndTime!=null && lastRunStartTime!=null && lastRunStartTime.before(lastRunEndTime)){
                milTimes= Long.valueOf(lastRunEndTime.getTime() - lastRunStartTime.getTime()).toString();
            }
            logs.append("Task[uuid="+uuid+",result="+((this.isLastTaskSuc)?"成功":"失败")+",milTimes="+milTimes+"]\n");
            if (!this.isLastTaskSuc){
                logs.append("Task[uuid="+uuid+",error="+this.getLastRunExceptionMsg()+"]\n");
            }
            BaseBusiComponent.logInfo(logInterface,logs.toString());
        }catch (Exception e){
            e.printStackTrace();
        }
    }
    /**
     * 等待任务执行时间到达或任务被提前唤醒
     * */
    private void waitArriveRunTime(long interval) throws BusiException{
        synchronized (threadHandle){
            try {
                taskRunState = 2;   //设置任务执行状态为阻塞
                threadHandle.wait(interval);
            } catch (InterruptedException e) {
                throw new BusiException("等待任务被唤醒时被打断:"+BaseNnte.getExpMsg(e));
            }catch (Exception e){
                throw new BusiException("等待任务被唤醒时异常:"+BaseNnte.getExpMsg(e));
            }finally {
            }
        }
    }
    /**
     * 唤醒处于等待状态的阻塞线程
     * */
    public void notifyTaskWait() throws BusiException{
        if (taskRunState==2){
            synchronized(threadHandle){
                threadHandle.notify();
            }
            return;
        }
        throw new BusiException(22001,"任务未处于等待状态", BusiException.ExpLevel.WARN);
    }
    /**
     * 执行监听任务函数，执行任务时，任务暂时不接收监听
     * */
    private void runTaskInterface(AutoTaskInterface taskInterface){
        try {
            setTaskStartInfo();
            taskRunTimes++;     //不管执行是否成功，任务执行次数要累加上去
            taskInterface.startRunTask(getTaskCode(),taskRunMethod,getTaskParamJson());
            isLastTaskSuc = true;
        }catch (TaskLockException tLockExp){
            taskRunExceptTimes++;
            lastRunExceptionTime = new Date();
            lastRunExceptionMsg = BaseNnte.getExpMsg(tLockExp);
            BaseBusiComponent.logError(logInterface,tLockExp);
        }catch (BusiException e){
            taskRunExceptTimes++;
            lastRunExceptionTime = new Date();
            lastRunExceptionMsg = BaseNnte.getExpMsg(e);
            BaseBusiComponent.logError(logInterface,e);
        }finally {
            setTaskEndInfo();
            releaseTaskLock();
            printTaskLog();
        }
    }
    /**
     * 反复测试监听状态是否是指定的状态，持续5秒
     * */
    private boolean checkTaskListenStatue(int statue){
        int tryTimes=100;
        do {
            if (this.getTaskListenStatus()==statue)
                tryTimes = -1;
            else
                tryTimes--;
            if (tryTimes>0)
                ThreadUtil.Sleep(50);
        }while(tryTimes>0);
        if (tryTimes == -1)
            return true;
        return false;
    }
    /**
     * 启动任务监听
     * */
    public void startTaskListen(boolean isCheck)throws BusiException{
        if (getTaskLock()) {
            try {
                if (getTaskListenStatus()==0) {
                    setListen(true); //先设置为需要监听
                    new Thread(this).start();
                }
            }finally {
                releaseTaskLock();
            }
            if (isCheck) {
                if (!checkTaskListenStatue(1))
                    throw new BusiException(21103, "任务监听没有正确启动",
                            BusiException.ExpLevel.WARN);
            }
        }else
            throw new BusiException(err_code_tasklock_failed,err_msg_tasklock_failed,
                                    BusiException.ExpLevel.ERROR);
    }
    /**
     * 停止任务监听
     * */
    public void stopTaskListen() throws BusiException{
        if (getTaskLock()) {
            try {
                setListen(false);
                if (taskRunState==2)
                    notifyTaskWait();
            }finally {
                releaseTaskLock();
            }
            if (!checkTaskListenStatue(0))
                throw new BusiException(21102,"任务监听阻塞,没有正确停止",
                        BusiException.ExpLevel.WARN);
        }else
            throw new BusiException(err_code_tasklock_failed,err_msg_tasklock_failed,
                    BusiException.ExpLevel.ERROR);
    }

    @Override
    public void run() {
        try {
            listenStartTime = new Date();
            threadHandle = Thread.currentThread();
            if (this.getTaskRunBean()==null)
                throw new Exception("任务["+this.getTaskName()+"]监听启动失败，没指定特定的组件");
            AutoTaskInterface taskInterface = (AutoTaskInterface) this.getTaskRunBean();
            if (this.taskRunMethod == null)
                throw new Exception("任务["+this.getTaskName()+"]入口函数为空");
            this.setTaskListenStatus(1);
            BaseBusiComponent.logInfo(logInterface,"任务["+this.getTaskName()+"]监听启动......");
            while (this.isListen()) {
                try {
                    nextRunStartTime = null;
                    Date priRunTime = (this.lastRunEndTime==null)? new Date():this.lastRunEndTime;
                    long interval=AutoTaskComponent.getNextRunMilInterval(
                            taskInterface.getTaskRunTimeConfig(this.getTaskCode()),priRunTime);
                    if (interval>0) {
                        nextRunStartTime = new Date(priRunTime.getTime() + interval);
                        this.waitArriveRunTime(interval);
                    }
                    if (this.isListen())
                        runTaskInterface(taskInterface);
                } catch (BusiException be){
                    if (this.isListen() && this.isRunTask)
                        ThreadUtil.Sleep(1000);
                } catch (Exception e){
                    BaseBusiComponent.logError(logInterface,e);
                    if (this.isListen() && this.isRunTask)
                        ThreadUtil.Sleep(1000);
                }
                //如果继续监听但是不需要运行，先等1秒
                if (this.isListen() && !this.isRunTask)
                    ThreadUtil.Sleep(1000);
            }
        }catch (Exception e){
            BaseBusiComponent.logInfo(logInterface,BaseNnte.getExpMsg(e));
        }finally {
            this.setTaskListenStatus(0);
            this.listenStartTime = null;
            this.threadHandle = null;
            this.taskRunState = 0;
            this.nextRunStartTime = null;
            this.lastRunExceptionMsg = "";
            BaseBusiComponent.logInfo(logInterface,"任务["+this.getTaskName()+"]监听停止......");
        }
    }

    public int getTaskRunTimes() {
        return taskRunTimes;
    }

    public String getLastTaskUUID() {
        return lastTaskUUID;
    }

    public boolean isLastTaskSuc() {
        return isLastTaskSuc;
    }

    public String getLastRunExceptionMsg() {
        return lastRunExceptionMsg;
    }

    public void setRunTask(boolean runTask) {
        isRunTask = runTask;
    }

    public boolean getRunTask() {
        return isRunTask;
    }

    public int getTaskListenStatus() {
        return taskListenStatus;
    }

    private void setTaskListenStatus(int taskListenStatus){
        this.taskListenStatus = taskListenStatus;
    }

    public boolean isListen() {
        return isListen;
    }

    private void setListen(boolean listen) {
        isListen = listen;
    }

    public Object getTaskRunBean() {
        return taskRunBean;
    }

    public void setTaskRunMethod(Method taskRunMethod) {
        this.taskRunMethod = taskRunMethod;
    }

    public void setTaskRunBean(Object taskRunBean) {
        this.taskRunBean = taskRunBean;
    }

    public String getTaskRunBeanName() {
        return taskRunBeanName;
    }

    public void setTaskRunBeanName(String taskRunBeanName) {
        this.taskRunBeanName = taskRunBeanName;
    }

    public String getTaskRunStateName(){
        switch (taskRunState){
            case 0:return "未执行";
            case 2:return "等待执行";
            case 1:return "执行中";
        }
        return "未知";
    }

    public String getTaskListenStatusName(){
        switch (taskListenStatus){
            case 0:return "未监听";
            case 1:return "已监听";
            case 2:return "监听挂起";
        }
        return "未知";
    }
}
