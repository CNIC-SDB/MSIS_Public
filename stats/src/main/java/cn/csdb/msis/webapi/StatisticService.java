package cn.csdb.msis.webapi;

import cn.csdb.msis.integrator.bo.DataStatReport;
import cn.csdb.msis.integrator.service.DataStatService;
import cn.csdb.msis.integrator.util.DateUtil;
import cn.csdb.msis.stats.entity.Group;
import cn.csdb.msis.stats.repository.GroupsRepository;
import cn.csdb.msis.stats.repository.StatusRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.*;

@Service
public class StatisticService {

    @Autowired
    private DataStatService dataStatService;

    @Autowired
    private GroupsRepository groupsRepository;

    @Autowired
    private StatusRepository statusRepository;

    Map<String, Object> outageTime(String db, Integer start, Integer end) {
        Map<String, Object> result = new HashMap<>(16);
        Group byEnName = groupsRepository.findByEnName(db);
        Integer gid = byEnName.getId();

        Calendar calendar = Calendar.getInstance();
        calendar.set(start / 10000, start % 10000 / 100 - 1, 1, 0, 0);
        Date startDate = calendar.getTime();
        calendar.set(end / 10000, end % 10000 / 100, 1, 0, 0);
        Date endDate = calendar.getTime();

        List<Map<String, String>> maps = statusRepository.countOutageTime(gid, startDate, endDate);
        BigDecimal data = new BigDecimal(0);
        Map<String, Object> monthDataTmp = new HashMap<>();
        for (Map<String, String> map : maps) {
            String month = map.get("month");
            String days = map.get("days");
            days = days == null || "".equals(days) ? "0.00" : days;
            monthDataTmp.put(month, days);
            data = data.add(new BigDecimal(days));
        }
        result.put("data", data.toString());
        List<String> monthList = monthList(start, end);
        fillMonth(monthDataTmp, monthList);
        Map<String, Object> monthData = mapSortByKey(monthDataTmp);
        result.put("month_data", monthData);
        return result;
    }

    /**
     * 当前数据库访问量统计
     *
     * @param db    数据库标识
     * @param start 开始日期
     * @param end   结束日期
     */
    Map<String, Object> allVisit(String db, Integer start, Integer end) {
        return common(db, start, end, "VisitMonth");
    }

    /**
     * 当前数据库下载量统计
     *
     * @param db    数据库标识
     * @param start 开始日期
     * @param end   结束日期
     */
    Map<String, Object> allDownloads(String db, Integer start, Integer end) {
        return common(db, start, end, "BandMonth");
    }

    /**
     * 当前数据库IP数量统计
     *
     * @param db    数据库标识
     * @param start 开始日期
     * @param end   结束日期
     */
    Map<String, Object> ipCount(String db, Integer start, Integer end) {
        return common(db, start, end, "IPMonth");
    }

    /**
     * 当前数据库页面访问量统计
     *
     * @param db    数据库标识
     * @param start 开始日期
     * @param end   结束日期
     */
    Map<String, Object> pageVisitCount(String db, Integer start, Integer end) {
        Map<String, Object> resultMap = common(db, start, end, "PagesMonth");
        return resultMap;
    }

    /**
     * 一般参数统计公共方法
     *
     * @param db    数据库标识
     * @param start 开始日期
     * @param end   结束日期
     * @param type  选取的统计结果名称
     * @return 结果
     */
    private Map<String, Object> common(String db, Integer start, Integer end, String type) {
        Group byEnName = groupsRepository.findByEnName(db);
        DataStatReport visitByTime = dataStatService.findVisitByTime(byEnName.getId(), start, end);
        List<Map<String, Object>> rowsMonth = visitByTime.getRowsMonth();

        Map<String, Object> resultMap = new HashMap<>(2);
        Map<String, Object> monthDataTmp = new HashMap<>();
        BigDecimal count = new BigDecimal(0);
        for (Map<String, Object> stringObjectMap : rowsMonth) {
            String visitMonth = String.valueOf(stringObjectMap.get(type) == null ? "0" : stringObjectMap.get(type));
            count = count.add(new BigDecimal(visitMonth));
            String dayString = String.valueOf(stringObjectMap.get("dayTime") == null ? "0" : stringObjectMap.get("dayTime"));
            Integer[] dayIntegerArray = DateUtil.string2Time(dayString);
            String dayFormatString = dayIntegerArray[0].toString() + "-" + String.format("%02d", dayIntegerArray[1]);
            monthDataTmp.put(dayFormatString, visitMonth);
        }
        resultMap.put("data", count.toString());
        List<String> monthList = monthList(start, end);
        fillMonth(monthDataTmp, monthList);
        Map<String, Object> monthData = mapSortByKey(monthDataTmp);
        resultMap.put("month_data", monthData);
        return resultMap;
    }

    /**
     * @param start 开始时间
     * @param end   截止时间
     * @return 时间参数跨度数组["2020-01","2020-02","2020-03"...]
     */
    private List<String> monthList(Integer start, Integer end) {
        List<String> monthList = new ArrayList<>(12);
        Integer[] startInteger = DateUtil.string2Time(start.toString());
        Integer[] endInteger = DateUtil.string2Time(end.toString());

        // 开始日期 传入参数月份起始为1 日历处理月份其实为0
        Calendar startCalendar = Calendar.getInstance();
        startCalendar.set(startInteger[0], startInteger[1] - 1, 1);

        // 中间日期
        Calendar nextCalendar = Calendar.getInstance();

        // 结束日期
        Calendar endCalendar = Calendar.getInstance();
        endCalendar.set(endInteger[0], endInteger[1] - 1, 1);

        while (nextCalendar.compareTo(endCalendar) < 0) {
            nextCalendar.set(startCalendar.get(Calendar.YEAR), startCalendar.get(Calendar.MONTH), 1);
            int year = nextCalendar.get(Calendar.YEAR);
            int month = nextCalendar.get(Calendar.MONTH);
            String formatMonth = String.format("%02d", month + 1);
            startCalendar.add(Calendar.MONTH, 1);
            monthList.add(year + "-" + formatMonth);
        }
        return monthList;

    }

    /**
     * 补齐数据库查询结果没有的月份
     *
     * @param monthData 数据库查询的结果集
     * @param monthList 时间参数跨度数组["2020-01","2020-02","2020-03"...]
     */
    private void fillMonth(Map<String, Object> monthData, List<String> monthList) {
        for (String key : monthData.keySet()) {
            monthList.remove(key);
        }
        for (String month : monthList) {
            monthData.put(month, "0");
        }
    }

    /**
     * 原始结果按照时间升序排序
     *
     * @param monthData 原始结果
     * @return 排序后的结果
     */
    private Map<String, Object> mapSortByKey(Map<String, Object> monthData) {
        Map<String, Object> result = new LinkedHashMap<>(16);
        List<String> keyList = new ArrayList<>(12);
        for (String key : monthData.keySet()) {
            keyList.add(key);
        }

        keyList.sort(new Comparator<String>() {
            @Override
            public int compare(String o1, String o2) {
                String s1 = o1.replace("-", "");
                String s2 = o2.replace("-", "");
                int i = Integer.valueOf(s1).compareTo(Integer.valueOf(s2));
                return i;
            }
        });

        for (String key : keyList) {
            result.put(key, monthData.get(key));
        }
        return result;
    }

}
