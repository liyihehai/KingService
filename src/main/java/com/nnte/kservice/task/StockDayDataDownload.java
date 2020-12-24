package com.nnte.kservice.task;

import com.nnte.AutoTask.AutoTaskInterface;
import com.nnte.AutoTask.AutoTaskMethod;
import com.nnte.AutoTask.TaskRunTime;
import com.nnte.framework.utils.ThreadUtil;
import org.springframework.stereotype.Component;

@Component
public class StockDayDataDownload implements AutoTaskInterface {
    @Override
    public TaskRunTime getTaskRunTimeConfig(String code) {
        TaskRunTime retConfig = new TaskRunTime();
        retConfig.setIntervalType("Day");
        retConfig.setIntervalCount(1);
        retConfig.setDayStartHour(8);
        retConfig.setDayStartMinute(28);
        retConfig.setDayStartSecond(0);
        return retConfig;
    }
    @AutoTaskMethod(value = "downloadStocksDayData",name = "下载特定股票日线数据")
    public void downloadStocksDayData(String param) throws Exception{
        ThreadUtil.Sleep(10*1000);
        throw new Exception("测试异常");
    }
}
