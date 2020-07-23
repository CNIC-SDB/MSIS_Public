package cn.csdb.msis.integrator.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;

/**
 * 缓存数据更新
 */
@Service
public class CacheService {

    @Autowired
    private HomePageService homePageService;

    @CacheEvict(value = "oneDay", allEntries = true)
    public void clearOneDayCache() {
    }

    public void refreshHomePage() {
        homePageService.homePage();
    }


}
