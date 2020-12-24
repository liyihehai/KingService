package com.nnte.AutoTask;

import com.nnte.basebusi.annotation.BusiLogAttr;
import com.nnte.basebusi.annotation.WatchAttr;
import com.nnte.basebusi.annotation.WatchInterface;
import com.nnte.basebusi.base.BaseBusiComponent;
import com.nnte.basebusi.excption.BusiException;
import com.nnte.framework.base.BaseNnte;
import com.nnte.framework.base.NameLockComponent;
import com.nnte.framework.base.SpringContextHolder;
import com.nnte.framework.utils.DateUtils;
import com.nnte.framework.utils.StringUtils;
import com.nnte.framework.utils.ThreadUtil;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.util.*;
/**
 * 自动任务核心组件 李毅 2020/12/23
 * 所有自动任务必须是spring boot组件，并且实现了接口AutoTaskInterface
 * 每个组件可以提供多个自动任务的执行入口函数，相当于独立的任务，每个入口
 * 函数应该有AutoTaskMethod注解，每个函数的格式为 void method(String param) throw BusiException;
 * 如果系统加载了AutoTaskComponent组件，则系统在所有组件加载完成后会调用scanTaskList扫描系统中所有
 * 的自动任务定义，并且依据有效的定义自动注册自动任务并启动监听循环
 * */
@Component
@BusiLogAttr("AutoTaskRoot")
@WatchAttr(value = 1,execTimes=1)
public class AutoTaskComponent extends BaseBusiComponent implements WatchInterface {
    private static List<TaskEntity> taskList = new ArrayList<>();
    private static TreeMap<String,TaskRunEntity> taskMap = new TreeMap<>();
    public static NameLockComponent AutoTaskLock = new NameLockComponent();

    public final static int err_code_taskcode_invalid = 21001;
    public final static String err_msg_taskcode_invalid = "未找到指定代码的任务";
    /**
     * 扫描任务列表，加载任务并执行，本函数程序启动时只执行一次
     * */
    private void scanTaskList(){
        ApplicationContext sch= SpringContextHolder.getApplicationContext();
        String[] names=sch.getBeanDefinitionNames();
        for(String beanName:names){
            Object instanceBody=sch.getBean(beanName);
            if (instanceBody instanceof AutoTaskInterface){
                Method[] methods=instanceBody.getClass().getDeclaredMethods();
                for(Method m:methods) {
                    AutoTaskMethod feAnno = m.getAnnotation(AutoTaskMethod.class);
                    if (feAnno!=null){
                        TaskEntity task = new TaskEntity();
                        task.setTaskCode(feAnno.value());
                        task.setTaskName(feAnno.name());
                        task.setTaskComponent(beanName);
                        task.setTaskMethod(m.getName());
                        task.setTaskParamJson(feAnno.param());
                        task.setTaskRun(feAnno.isRun());
                        taskList.add(task);
                    }
                }
            }
        }
        for(TaskEntity task:taskList){
            TaskRunEntity TRE=taskMap.get(task.getTaskCode());
            if (TRE==null){
                //需要初始化一个任务，并将它加入到任务MAP中
                addTaskAndListen(task);
            }
        }
    }

    private void addTaskAndListen(TaskEntity task) {
        try {
            TaskRunEntity newTre = new TaskRunEntity(task, this);
            TaskRunEntity.setComponentTaskRunMethod(newTre);
            taskMap.put(newTre.getTaskCode(), newTre);
            //启动该任务
            newTre.startTaskListen(false);
        }catch (BusiException be){
            BaseBusiComponent.logError(this,be);
        }
    }
    /**
     * 增加任务并启动监听
     * */
    public void addTaskAndListen(String code,String name,String paramJson,
                                 String component,String method) throws BusiException{
        TaskRunEntity TRE=taskMap.get(code);
        if (TRE!=null)
            throw new BusiException("该编号任务已存在");
        TaskRunEntity.setComponentTaskRunMethod(TRE); //检测一下组件是否正确
        TaskEntity task=new TaskEntity();
        task.setTaskCode(code);
        task.setTaskName(name);
        task.setTaskComponent(component);
        task.setTaskParamJson(paramJson);
        task.setTaskMethod(method);
        task.setTaskRun(true);
        addTaskAndListen(task);
    }
    /**
     * 设置任务是否执行：isRun = true：执行；isRun = false：不执行
     * */
    public void setTaskIsRun(String code,boolean isRun){
        TaskRunEntity TRE=taskMap.get(code);
        if (TRE!=null){
            TRE.setRunTask(isRun);
        }
    }

    /**
     * 设置任务继续执行
     * */
    public boolean getTaskIsRun(String code){
        TaskRunEntity TRE=taskMap.get(code);
        if (TRE!=null){
            return TRE.getRunTask();
        }
        return false;
    }

    @Override
    public void runWatch() {
        try{
            BaseBusiComponent.logInfo(this,"自动任务扫描任务列表启动......");
            scanTaskList();
            BaseBusiComponent.logInfo(this,"自动任务扫描任务列表结束！");
        }catch (Exception e){
            BaseBusiComponent.logInfo(this, BaseNnte.getExpMsg(e));
        }
    }

    /**
     * 得到下次运行时间与当前时间的间隔毫秒数
     * return <= 0 表示不能计算出合理的间隔数
     * */
    public static long getNextRunMilInterval(TaskRunTime timeConfig, Date priRunTime) throws BusiException {
        if (timeConfig==null || priRunTime==null)
            throw new BusiException("执行时间配置或上次运行时间未确定");
        String[] allTypes = {"Month","Week","Day","Hour","Minute","Second"};
        if (!StringUtils.equalsIgnoreCaseOneof(timeConfig.getIntervalType(),allTypes))
            throw new BusiException("执行时间配置间隔分类错误");
        if (timeConfig.getIntervalCount()<=0)
            throw new BusiException("执行时间配置间隔数错误");
        if (timeConfig.getDayStartHour()<0 || timeConfig.getDayStartHour()>23)
            throw new BusiException("执行时间配置日开始小时错误");
        if (timeConfig.getDayStartMinute()<0 || timeConfig.getDayStartMinute()>59)
            throw new BusiException("执行时间配置日开始分钟错误");
        if (timeConfig.getDayStartSecond()<0 || timeConfig.getDayStartSecond()>59)
            throw new BusiException("执行时间配置日开始秒数错误");
        Date addDate;
        String dayTimeAppend = String.format("%02d:%02d:%02d", timeConfig.getDayStartHour(),
                timeConfig.getDayStartMinute(), timeConfig.getDayStartSecond());
        if (timeConfig.getIntervalType().equalsIgnoreCase("Month")) {
            if (timeConfig.getMonthStartDay()<1 || timeConfig.getMonthStartDay()>31)
                throw new BusiException("执行时间配置月开始日期错误");
            int maxMonthDay = DateUtils.getDay(DateUtils.getLastDate(priRunTime));
            if (maxMonthDay>=timeConfig.getMonthStartDay())
                maxMonthDay = timeConfig.getMonthStartDay();
            Date maxNextDay = DateUtils.stringToDate(String.format("%d-%02d-%02d %s", DateUtils.getYear(priRunTime),
                    DateUtils.getMonth(priRunTime),maxMonthDay ,dayTimeAppend),DateUtils.DF_YMDHMS);
            if (maxNextDay.after(priRunTime)){
                return maxNextDay.getTime() - priRunTime.getTime();
            }else {
                addDate = DateUtils.addMonth(priRunTime, timeConfig.getIntervalCount());
                int maxNextMonthDay = DateUtils.getDay(DateUtils.getLastDate(addDate));
                if (maxNextMonthDay>=timeConfig.getMonthStartDay())
                    maxNextMonthDay = timeConfig.getMonthStartDay();
                Date aimDate = DateUtils.stringToDate(String.format("%d-%02d-%02d %s", DateUtils.getYear(addDate),
                        DateUtils.getMonth(addDate),maxNextMonthDay ,dayTimeAppend),DateUtils.DF_YMDHMS);
                return aimDate.getTime() - priRunTime.getTime();
            }
        }
        if (timeConfig.getIntervalType().equalsIgnoreCase("Week")) {
            if (timeConfig.getWeekStartDay()<0 || timeConfig.getWeekStartDay()>6)
                throw new BusiException("执行时间配置周开始日数错误");
            int priWeekDay=DateUtils.getWeek(priRunTime);
            Date nextDate;
            if (priWeekDay<timeConfig.getWeekStartDay()){
                nextDate = DateUtils.addDay(priRunTime, timeConfig.getWeekStartDay()-priWeekDay);
            }else {
                Date addWeeksDate = DateUtils.addWeek(priRunTime, timeConfig.getIntervalCount());
                int addWeeksDay=DateUtils.getWeek(addWeeksDate);
                nextDate = DateUtils.addDay(addWeeksDate,timeConfig.getWeekStartDay() - addWeeksDay);
            }
            Date aimDate = DateUtils.stringToDate(String.format("%d-%02d-%02d %s", DateUtils.getYear(nextDate),
                    DateUtils.getMonth(nextDate),DateUtils.getDay(nextDate) ,dayTimeAppend),DateUtils.DF_YMDHMS);
            return aimDate.getTime() - priRunTime.getTime();
        }
        if (timeConfig.getIntervalType().equalsIgnoreCase("Day")) {
            Date nowDayTime=DateUtils.stringToDate(DateUtils.dateToString(priRunTime)+" "+dayTimeAppend,
                    DateUtils.DF_YMDHMS);
            if (nowDayTime.after(priRunTime)){
                return nowDayTime.getTime() - priRunTime.getTime();
            }else {
                addDate = DateUtils.addDay(priRunTime, timeConfig.getIntervalCount());
                Date aimDate = DateUtils.stringToDate(DateUtils.dateToString(addDate) + " " + dayTimeAppend,
                        DateUtils.DF_YMDHMS);
                return aimDate.getTime() - priRunTime.getTime();
            }
        }
        if (timeConfig.getIntervalType().equalsIgnoreCase("Hour")) {
            addDate = DateUtils.addNHours(priRunTime, timeConfig.getIntervalCount());
            return addDate.getTime() - priRunTime.getTime();
        }
        if (timeConfig.getIntervalType().equalsIgnoreCase("Minute")) {
            addDate = DateUtils.addDateMinute(priRunTime, timeConfig.getIntervalCount());
            return addDate.getTime() - priRunTime.getTime();
        }
        if (timeConfig.getIntervalType().equalsIgnoreCase("Second")) {
            addDate = DateUtils.addDateSecond(priRunTime, timeConfig.getIntervalCount());
            return addDate.getTime() - priRunTime.getTime();
        }
        throw new BusiException("计算下次开始时间间隔错误");
    }

    public TaskRunEntity getTaskRunEntity(String taskCode) throws BusiException{
        TaskRunEntity TRE=taskMap.get(taskCode);
        if (TRE==null)
            throw new BusiException(err_code_taskcode_invalid,
                    err_msg_taskcode_invalid, BusiException.ExpLevel.ERROR);
        return TRE;
    }
    /**
     * 取得任务信息
     * */
    public TaskInfo getTaskInfo(String taskCode) throws BusiException{
        TaskRunEntity TRE=getTaskRunEntity(taskCode);
        return TRE.getTaskRunInfo();
    }
    /**
     * 通知任务立即执行
     * */
    public TaskInfo notifyTaskRun(String taskCode) throws BusiException{
        TaskRunEntity TRE=getTaskRunEntity(taskCode);
        TRE.notifyTaskWait();
        ThreadUtil.Sleep(300);
        return TRE.getTaskRunInfo();
    }
    /**
     * 启动任务监听
     * */
    public TaskInfo startTaskListen(String code) throws BusiException{
        TaskRunEntity TRE=getTaskRunEntity(code);
        TRE.startTaskListen(true);
        return TRE.getTaskRunInfo();
    }
    /**
     * 停止任务监听
     * */
    public TaskInfo stopTaskListen(String code) throws BusiException{
        TaskRunEntity TRE=getTaskRunEntity(code);
        TRE.stopTaskListen();
        return TRE.getTaskRunInfo();
    }
    /**
     * 返回所有的任务信息
     * */
    public List<TaskInfo> getAllTaskInfo(){
        List<TaskInfo> ret = new ArrayList<>();
        taskMap.forEach((key,taskRun)->{
            ret.add(taskRun.getTaskRunInfo());
        });
        return ret;
    }
}
