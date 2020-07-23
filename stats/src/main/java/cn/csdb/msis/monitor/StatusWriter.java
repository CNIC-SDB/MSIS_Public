package cn.csdb.msis.monitor;

import cn.csdb.msis.stats.entity.Status;
import cn.csdb.msis.stats.entity.StatusDaily;
import cn.csdb.msis.stats.repository.StatusDailyRepository;
import cn.csdb.msis.stats.repository.StatusRepository;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.BlockingQueue;

/**
 * 数据写入器,把队列中的数据写入到数据库中
 *
 * @author CXY
 */
public class StatusWriter implements Runnable {

    private final static String module = StatusWriter.class.getName();

    @Autowired
    StatusRepository statusRepository;

    @Autowired
    StatusDailyRepository statusDailyRepository;
    //存放状态数据的队列
    private BlockingQueue statusQueue;
    private boolean runEnable = true;

    public StatusWriter(BlockingQueue queue) {
        super();
        statusQueue = queue;
        runEnable = true;
    }

    public void setDisable() {
        runEnable = false;
    }

    /**
     * 从statusQueue取出数据并写入到数据库中
     */
    public void run() {
        try {
            while (runEnable) {
                //从队列中取出数据
                ServiceStatus status = (ServiceStatus) statusQueue.take();
                //写数据到数据表中
//				Log.logInfo("写数据开始",module);
                Date dateTime = status.getTime();
                Status st = new Status();
                st.setMtime(status.getTime());
                st.setMid(status.getMID());
                st.setStatus(status.getStatus());

                StatusDaily st2 = new StatusDaily();
                Calendar cl = Calendar.getInstance();
                cl.setTime(dateTime);
                st2.setDay(cl.get(Calendar.DAY_OF_MONTH));
                st2.setYear(cl.get(Calendar.YEAR));
                st2.setMonth(cl.get(Calendar.MONTH) + 1);
                st2.setHour(cl.get(Calendar.HOUR_OF_DAY));
                st2.setMinute(cl.get(Calendar.MINUTE));
                st2.setStatus(status.getStatus());
                st2.setMid(status.getMID());

                statusRepository.save(st);

                statusDailyRepository.save(st2);

//				if(status.getExceptionStatus()){
//					//暂时为空
//					MonitorExceptionId meid = new MonitorExceptionId(st.getPoint(),st.getMtime());
//					MonitorException me = new MonitorException(meid);
//					commonMgr.saveOrUpdate(me);
//				}
//				Log.logInfo("写数据结束",module);
                //写数据到文件中
                //writeFile("file1.txt",status);
            }
        } catch (Exception e) {
            //e.printStackTrace();
//			Log.logError("StatusWriter SQLException" + e, module);
        }
    }
}