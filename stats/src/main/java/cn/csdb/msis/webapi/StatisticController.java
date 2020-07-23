package cn.csdb.msis.webapi;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Controller()
@RequestMapping(value = "/Joggle")
public class StatisticController {

    /**
     * 数据库名称标识
     */
    private static Map<String, String> DBMAP;
    /**
     * 查询类型标识
     */
    private static Map<String, String> KEYMAP;

    // 初始化数据库的标识桶
    static {
        Map<String, String> dbMap = new HashMap<>(20);
        // 大数据驱动的空间科学领域创新示范平台
        dbMap.put("cn.csdb.space", "cn.csdb.space");
        // 大数据驱动的生物信息领域创新示范平台
        dbMap.put("cn.csdb.bigd", "cn.csdb.bigd");
        // 大数据驱动的第三极环境创新示范平台
        dbMap.put("cn.csdb.tpdc", "cn.csdb.tpdc");
        // 大数据驱动的资源学科领域创新示范平台
        dbMap.put("cn.csdb.man-earth", "cn.csdb.man-earth");
        // 喀斯特地形地貌区重点数据库建设与应用服务
        dbMap.put("cn.csdb.karst", "cn.csdb.karst");
        // 动物学重点数据库建设与应用服务
        dbMap.put("cn.csdb.zoology", "cn.csdb.zoology");
        // 化学学科领域重点数据库建设与应用服务
        dbMap.put("cn.csdb.chemdb", "cn.csdb.chemdb");
        // 农业多尺度病虫害图像重点数据库建设与应用服务
        dbMap.put("cn.csdb.idadp", "cn.csdb.idadp");
        // 南海海洋重点数据库建设与应用服务
        dbMap.put("cn.csdb.scsio", "cn.csdb.scsio");
        // 环境微生物多样性重点数据库建设与应用服务
        dbMap.put("cn.csdb.egcloud", "cn.csdb.egcloud");
        // 光学天文重点数据库建设与应用服务
        dbMap.put("cn.csdb.astro", "cn.csdb.astro");
        // 湖泊学科领域特色专题数据库
        dbMap.put("cn.csdb.lakesci", "cn.csdb.lakesci");
        // 中国土壤特色数据库
        dbMap.put("cn.csdb.soil", "cn.csdb.soil");
        // 多民族语言资源特色数据库
        dbMap.put("cn.csdb.natilangip", "cn.csdb.natilangip");
        // 中国古代颜料光谱特色数据库
        dbMap.put("cn.csdb.scpp", "cn.csdb.scpp");
        // 青海湖流域综合研究特色数据库
        dbMap.put("cn.csdb.qinghailake", "cn.csdb.qinghailake");
        // 植物园植物资源保育特色数据库
        dbMap.put("cn.csdb.bgdb", "cn.csdb.bgdb");
        // 安全漏洞特色数据库
        dbMap.put("cn.csdb.security", "cn.csdb.security");
        // 中国生态系统评估与生态安全格局特色数据库
        dbMap.put("cn.csdb.ecosystem", "cn.csdb.ecosystem");
        // 世界山茶属植物品种特色数据库
        dbMap.put("cn.csdb.camellia", "cn.csdb.camellia");
        // 野生动物携带病毒病原特色数据库
        dbMap.put("cn.csdb.batvirus", "cn.csdb.batvirus");
        // 深海科考音像特色数据库
        dbMap.put("cn.csdb.dsiv", "cn.csdb.dsiv");
        // 材料科学特色数据库
        dbMap.put("cn.csdb.matsci", "cn.csdb.matsci");
        // 抑郁症静息态功能磁共振成像特色数据库
        dbMap.put("cn.csdb.rfmri", "cn.csdb.rfmri");
        // 国家时间频率体系特色数据库
        dbMap.put("cn.csdb.tfsc", "cn.csdb.tfsc");
        // DarwinTree-面向系统发育研究特色数据库
        dbMap.put("cn.csdb.darwintree", "cn.csdb.darwintree");
        // 半导体材料科学特色数据库
        dbMap.put("cn.csdb.semidb", "cn.csdb.semidb");
        // 微生物组特色数据库
        dbMap.put("cn.csdb.gcmeta", "cn.csdb.gcmeta");
        // MUSER 太阳射电特色数据库
        dbMap.put("cn.csdb.muser", "cn.csdb.muser");
        // 材料低温物性特色数据库
        dbMap.put("cn.csdb.cryomd", "cn.csdb.cryomd");
        // 冰冻圈领域特色数据库
        dbMap.put("cn.csdb.cryosphere", "cn.csdb.cryosphere");
        DBMAP = dbMap;

        Map<String, String> keyMap = new HashMap<>(6);
        // 中断天数
        keyMap.put("outage_time", "outageTime");
        // 访问量
        keyMap.put("all_visit", "allVisit");
        // 下载量
        keyMap.put("all_downloads", "allDownloads");
        // IP数量
        keyMap.put("ip_count", "ipCount");
        // 页面访问数量
        keyMap.put("pageVisit_count", "pageVisitCount");
        KEYMAP = keyMap;
    }

    @Autowired
    private StatisticService statisticService;
    private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM");

    @RequestMapping(value = "")
    @ResponseBody
    public Map<String, Object> s(@RequestParam(value = "DB") String db,
                                 @RequestParam(value = "key") String key,
                                 @RequestParam(value = "startDate") String startDate,
                                 @RequestParam(value = "endDate") String endDate) {
        Map<String, Object> resultMap = initResult();
        if (!validateDb(db)) {
            resultMap.put("status", "401");
            resultMap.put("msg", "db未注册");
            return resultMap;
        }

        if (!validateKey(key)) {
            resultMap.put("status", "402");
            resultMap.put("msg", "key未注册");
            return resultMap;
        }
        Integer start = validateTime(startDate);
        if (start == 0) {
            resultMap.put("status", "403");
            resultMap.put("msg", "参数格式不正确：startDate 正确格式yyyy-MM");
            return resultMap;
        }
        Integer end = validateTime(endDate);
        if (end == 0) {
            resultMap.put("status", "403");
            resultMap.put("msg", "参数格式不正确：endDate 正确格式yyyy-MM");
            return resultMap;
        }

        Map<String, Object> stringObjectMap;
        if ("outage_time".equals(key)) {
            // 中断天数
            stringObjectMap = statisticService.outageTime(db, start, end);
        } else if ("all_visit".equals(key)) {
            // 访问量
            stringObjectMap = statisticService.allVisit(db, start, end);
        } else if ("all_downloads".equals(key)) {
            // 下载量
            stringObjectMap = statisticService.allDownloads(db, start, end);
        } else if ("ip_count".equals(key)) {
            // 独立IP数
            stringObjectMap = statisticService.ipCount(db, start, end);
        } else if ("pageVisit_count".equals(key)) {
            // 页面访问数
            stringObjectMap = statisticService.pageVisitCount(db, start, end);
        } else {
            resultMap.put("status", "402");
            resultMap.put("msg", "key未注册");
            return resultMap;
        }

        resultMap.putAll(stringObjectMap);


        return resultMap;
    }

    private Map<String, Object> initResult() {
        Map<String, Object> resultMap = new HashMap<>(16);
        resultMap.put("status", "200");
        resultMap.put("msg", "返回成功");
        return resultMap;
    }

    private Boolean validateDb(String db) {
        if (DBMAP.get(db) == null) {
            return Boolean.FALSE;
        }
        return Boolean.TRUE;
    }

    private Boolean validateKey(String key) {
        if (KEYMAP.get(key) == null) {
            return Boolean.FALSE;
        }
        return Boolean.TRUE;
    }

    private Integer validateTime(String timeString) {
        try {
            Date parse = sdf.parse(timeString);
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(parse);
            int year = calendar.get(Calendar.YEAR) * 10000;
            int month = (calendar.get(Calendar.MONTH) + 1) * 100;
            int day = 1;
            return year + month + day;
        } catch (ParseException e) {
            e.printStackTrace();
            return 0;
        }

    }

}
