package cn.csdb.msis.config;

import cn.csdb.msis.manager.service.DataExtractService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @Author jinbao
 * @Date 2019/9/18 16:32
 * @Description 提取awstats解析结果
 **/
@Configuration
@ComponentScan("cn.csdb.msis.manager.service")
@EnableScheduling
public class DataExtractQuartz {

	private Logger logger = LoggerFactory.getLogger(DataExtractQuartz.class);
	@Autowired
	DataExtractService dataExtractService;

    @Scheduled(cron = "0 0 */3 * * ?")
	public void test() {
		Date date = new Date();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String format = sdf.format(date);
		logger.info("开始执行解析文件的定时任务" + format);
		dataExtractService.extractProcess();
		logger.info("完成执行解析文件的定时任务");
	}
}
