package cn.csdb.msis.config;

import cn.csdb.msis.monitor.MonitorFactory;
import cn.csdb.msis.monitor.MonitorTimerTask;
import cn.csdb.msis.monitor.ServiceStatus;
import cn.csdb.msis.stats.entity.*;
import cn.csdb.msis.stats.repository.*;
import com.google.api.client.util.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

import java.sql.Timestamp;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.*;

@Configuration
@ComponentScan("cn.csdb.msis.monitor")
@EnableScheduling
public class SyncDataQuartzConfig {
	@Autowired
	StatusTempRepository statusTempRepository;
	@Autowired
	private MonitorRepository monitorRepository;
	@Autowired
	private StatusRepository statusRepository;
	@Autowired
	private StatusDailyRepository statusDailyRepository;
	@Autowired
	private SiteRepository sitesRepository;
	@Autowired
	private PercentagesRepository percentagesRepository;

	@Scheduled(cron = "0 0 0/1 * * ?")
	public String monitorTask() {
		System.out.println("测试站点连通率的任务开始");
		List list = null;
		List<cn.csdb.msis.stats.entity.Monitor> monitorList = monitorRepository.findAll();
		Iterator it = monitorList.iterator();
		int i = 0;
		int timerTaskSize = 20;
		List taskList = new ArrayList();
		while (it.hasNext()) {
			if ((i % timerTaskSize) == 0) {
				if (i != 0) {
					taskList.add(new ArrayList(list));
				}
				list = new ArrayList();
			}
			i++;
			Monitor mp = (Monitor) it.next();
			list.add(mp);
		}
		if (i != 0) {
			taskList.add(new ArrayList(list));
		}
		for (int k = 0; k < taskList.size(); k++) {
			MonitorTimerTask monitorTimerTask = new MonitorTimerTask((List) taskList.get(k));
			MonitorFactory monitorfactory = new MonitorFactory();
			for (int j = 0; j < monitorTimerTask.getList().size(); j++) {
				cn.csdb.msis.stats.entity.Monitor mpoint = (cn.csdb.msis.stats.entity.Monitor) monitorTimerTask.getList().get(j);
				cn.csdb.msis.monitor.Monitor monitor = monitorfactory.getMonitor();
				ServiceStatus sstatus = monitor.getStatus(mpoint);
				Date dateTime = sstatus.getTime();
				DateTime dt;
				Timestamp timestamp = new Timestamp(dateTime.getTime()); //2013-01-14 22:45:36.484
				Status st = new Status();
				st.setMtime(timestamp);
				st.setMid(sstatus.getMID());
				st.setStatus(sstatus.getStatus());

				StatusTemp statusTemp = new StatusTemp();
				statusTemp.setMtime(sstatus.getTime());
				statusTemp.setMid(sstatus.getMID());
				statusTemp.setStatus(sstatus.getStatus());

				StatusDaily st2 = new StatusDaily();
				Calendar cl = Calendar.getInstance();
				cl.setTime(dateTime);
				st2.setDay(cl.get(Calendar.DAY_OF_MONTH));
				st2.setYear(cl.get(Calendar.YEAR));
				st2.setMonth(cl.get(Calendar.MONTH) + 1);
				st2.setHour(cl.get(Calendar.HOUR_OF_DAY));
				st2.setMinute(cl.get(Calendar.MINUTE));
				st2.setStatus(sstatus.getStatus());
				st2.setMid(sstatus.getMID());
				statusRepository.save(st);
				statusTempRepository.save(statusTemp);
				statusDailyRepository.save(st2);
			}
		}
		System.out.println("测试站点连通率的任务结束");
		return "";
	}

	@Scheduled(cron = "0 0 1 ? * *")
	public String PercentageTask() {
		System.out.println("计算百分率的任务开始");
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		Date today = new Date();
		Date yesterday = new Date(today.getTime() - 24 * 60 * 60 * 1000);
		String strToday = sdf.format(today);
		String strYesterday = sdf.format(yesterday);
		int yesyear = Integer.parseInt(strYesterday.substring(0, 4));
		int yesmonth = Integer.parseInt(strYesterday.substring(5, 7));
		int yesday = Integer.parseInt(strYesterday.substring(8, 10));

		int year = Integer.parseInt(strYesterday.substring(0, 4));
		int month = Integer.parseInt(strYesterday.substring(5, 7));
		int day = Integer.parseInt(strYesterday.substring(8, 10));

		int intYesterday = yesyear * 10000 + yesmonth * 100 + yesday;
		int intToday = year * 10000 + month * 100 + day;

		List<Site> sites = (List<Site>) sitesRepository.findAll();
		if (!sites.isEmpty()) {
			for (Site site : sites) {
				Set<cn.csdb.msis.stats.entity.Monitor> monitors = site.getMonitors();
				if (!monitors.isEmpty()) {
					for (cn.csdb.msis.stats.entity.Monitor mp : monitors) {
//                        int intYesterday = Integer.parseInt(strYesterday);
//                        int intToday = Integer.parseInt(strToday);
						int normal = statusRepository.normalCount(mp.getId(), yesterday, today);
						int sum = statusRepository.sumCount(mp.getId(), yesterday, today);
						DecimalFormat df = new DecimalFormat("######0.00");
						double percent = 0d;
						if (sum != 0) percent = Double.parseDouble(df.format((double) normal * 100 / (double) sum));
						Percentage percentage = new Percentage();
						percentage.setMid(mp.getId());
						percentage.setPercent(percent);
						percentage.setYear(year);
						percentage.setMonth(month);
						percentage.setDay(day);
						percentage.setValidhours(normal);
						percentage.setTotalhours(sum);
						percentagesRepository.save(percentage);
					}
				}
			}
		}
		System.out.println("计算百分率的任务结束");
		return "";
	}
}
