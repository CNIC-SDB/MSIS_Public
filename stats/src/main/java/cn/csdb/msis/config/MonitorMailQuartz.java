package cn.csdb.msis.config;

import cn.csdb.msis.manager.service.SendMailService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

/**
 * @Author jinbao
 * @Date 2019/11/5 9:35
 * @Description 邮件发送监控站点异常
 **/
@Configuration
@ComponentScan("cn.csdb.msis.monitor")
@EnableScheduling
public class MonitorMailQuartz {
	Logger logger = LoggerFactory.getLogger(MonitorMailQuartz.class);

	@Autowired
	private SendMailService sendMailService;

	@Scheduled(cron = "0 26 */1 * * ?")
	public void monitorMail() {
		logger.info("监控异常邮件通知任务开始！！！");
		sendMailService.monitorMessage();
		logger.info("监控异常邮件通知任务完成！！！");
	}
}
