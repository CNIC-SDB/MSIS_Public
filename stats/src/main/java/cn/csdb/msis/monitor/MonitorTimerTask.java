package cn.csdb.msis.monitor;


import cn.csdb.msis.stats.repository.StatusDailyRepository;
import cn.csdb.msis.stats.repository.StatusRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.List;
import java.util.concurrent.BlockingQueue;

/**
 * 监控TimerTask类
 *
 * @author CXY
 */

@Component
public class MonitorTimerTask {

    private final static String module = MonitorTimerTask.class.getName();
    private static StatusRepository statusRepositoryThis;
    private static StatusDailyRepository statusDailyRepositoryThis;
    private BlockingQueue statusQueue;
    private List list;
    private MonitorFactory monitorfactory;
    private ServiceStatus sstatus;
    @Lazy
    @Autowired
    private StatusRepository statusRepository;
    @Autowired
    private StatusDailyRepository statusDailyRepository;

    /**
     * 构造函数
     * @param list  存放该时间间隔的所有监控点
     */
    public MonitorTimerTask(List list) {
        super();
        monitorfactory = new MonitorFactory();
        this.list = list;

    }

    public List getList() {
        return this.list;
    }

    @PostConstruct
    public void init() {
        statusRepositoryThis = statusRepository;
        statusDailyRepositoryThis = statusDailyRepository;
    }


}