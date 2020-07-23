package cn.csdb.msis.config;

import cn.csdb.msis.integrator.service.CacheService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

/**
 * @Author jinbao
 * @Date 2020/4/20
 * @Description ehcache数据定时刷新
 **/
@Configuration
@ComponentScan("cn.csdb.msis.manager.service")
@EnableScheduling
public class CacheQuartz {
    private Logger logger = LoggerFactory.getLogger(CacheQuartz.class);

    @Autowired
    private CacheService cacheService;

    @Scheduled(cron = "0 0 */3 * * ?")
    public void refreshHomePage() {
        logger.info("刷新缓存--oneDay : 开始");
        cacheService.clearOneDayCache();
        cacheService.refreshHomePage();
        logger.info("刷新缓存--oneDay : 结束");
    }
}
