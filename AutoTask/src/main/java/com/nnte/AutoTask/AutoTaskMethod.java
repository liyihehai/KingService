package com.nnte.AutoTask;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
@Documented
@Inherited
/***
 * 自动任务组件过程注解 李毅 2020/12/23
 * 加载方法为 AutoTaskComponent.scanTaskList()
 *
 * 为自动任务提供定义:
 * value = 自动任务代码，系统中不能重复
 * name  = 自动任务名称
 * param = 自动任务参数
 */
public @interface AutoTaskMethod {
    String value();
    String name();
    String param() default "";
    boolean isRun() default true;
}
