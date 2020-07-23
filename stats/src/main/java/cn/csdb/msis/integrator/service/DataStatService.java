package cn.csdb.msis.integrator.service;

import cn.csdb.msis.integrator.bo.DataStatReport;
import cn.csdb.msis.stats.entity.Group;
import cn.csdb.msis.stats.entity.Site;
import cn.csdb.msis.stats.repository.AwDayRepository;
import cn.csdb.msis.stats.repository.GroupsRepository;
import cn.csdb.msis.stats.repository.SiteRepository;
import com.maxmind.util.DomainUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import javax.persistence.Tuple;
import java.math.BigDecimal;
import java.util.*;

/**
 * 领域统计Service
 *
 * @author CXY
 * @Date 2019/7/23 13:58
 */
@Service
public class DataStatService {
    @Autowired
    GroupsRepository groupsRepository;
    @Autowired
    AwDayRepository awDayRepository;
    @Autowired
    SiteRepository siteRepository;

    public DataStatReport findVisit(Integer groupID) {
        long l1 = System.currentTimeMillis();
        DataStatReport dataStatReport = new DataStatReport();
        List<Tuple> dataStatTupleNew = null;
        List<Tuple> dataStatTuple = null;
        if (groupID.equals(0)) {
            dataStatTuple = groupsRepository.findVisitAll();
            dataStatTupleNew = groupsRepository.findPageAll();
        } else {
            dataStatTuple = groupsRepository.findVisitByGroupId(groupID);
            dataStatTupleNew = groupsRepository.findPageByGroupId(groupID);
            //dataStatReport.setId(groupID);
        }
        //设置独立IP、访问人次
        BigDecimal sumTotalVisits = new BigDecimal(0);
        BigDecimal sumTotalUnique = new BigDecimal(0);
        for (Tuple dataStat : dataStatTuple) {
            sumTotalVisits = sumTotalVisits.add(new BigDecimal(dataStat.get("sumTotalVisits").toString()));
            sumTotalUnique = sumTotalUnique.add(new BigDecimal(dataStat.get("sumTotalUnique").toString()));
        }
        dataStatReport.setSumTotalVisits(sumTotalVisits);
        dataStatReport.setSumTotalUnique(sumTotalUnique);
        //设置访问数、文件数、下载数
        BigDecimal pages = new BigDecimal(0);
        BigDecimal hits = new BigDecimal(0);
        BigDecimal bandwidth = new BigDecimal(0);
        BigDecimal robotPages = new BigDecimal(0);
        BigDecimal robotHits = new BigDecimal(0);
        BigDecimal robotBandwidth = new BigDecimal(0);
        for (Tuple dataStat : dataStatTupleNew) {
            pages = pages.add(new BigDecimal(dataStat.get("pages").toString()));
            hits = hits.add(new BigDecimal(dataStat.get("hits").toString()));
            bandwidth = bandwidth.add(new BigDecimal(dataStat.get("bandwidth").toString()));
            robotPages = robotPages.add(new BigDecimal(dataStat.get("robotPages").toString()));
            robotHits = robotHits.add(new BigDecimal(dataStat.get("robotHits").toString()));
            robotBandwidth = robotBandwidth.add(new BigDecimal(dataStat.get("robotBandwidth").toString()));
        }
        dataStatReport.setPages(pages);
        dataStatReport.setHits(hits);
        dataStatReport.setBandwidth(bandwidth);
        dataStatReport.setRobotPages(robotPages);
        dataStatReport.setRobotHits(robotHits);
        dataStatReport.setRobotBandwidth(robotBandwidth);
        return dataStatReport;
    }

    public DataStatReport findVisitInit(Integer groupID) {
        DataStatReport dataStatReport = new DataStatReport();


        List<Tuple> dataStatTupleNew = null;
        List<Tuple> dataStatTuple = null;
        dataStatTuple = groupsRepository.findVisitByGroupId(groupID);
        dataStatTupleNew = groupsRepository.findPageByGroupId(groupID);
        //设置独立IP、访问人次
        BigDecimal sumTotalVisits = new BigDecimal(0);
        BigDecimal sumTotalUnique = new BigDecimal(0);
        for (Tuple dataStat : dataStatTuple) {
            sumTotalVisits = sumTotalVisits.add(new BigDecimal(dataStat.get("sumTotalVisits").toString()));
            sumTotalUnique = sumTotalUnique.add(new BigDecimal(dataStat.get("sumTotalUnique").toString()));
        }
        dataStatReport.setSumTotalVisits(sumTotalVisits);
        dataStatReport.setSumTotalUnique(sumTotalUnique);
        //设置访问数、文件数、下载数
        BigDecimal pages = new BigDecimal(0);
        BigDecimal hits = new BigDecimal(0);
        BigDecimal bandwidth = new BigDecimal(0);
        BigDecimal robotPages = new BigDecimal(0);
        BigDecimal robotHits = new BigDecimal(0);
        BigDecimal robotBandwidth = new BigDecimal(0);
        for (Tuple dataStat : dataStatTupleNew) {
            pages = pages.add(new BigDecimal(dataStat.get("pages").toString()));
            hits = hits.add(new BigDecimal(dataStat.get("hits").toString()));
            bandwidth = bandwidth.add(new BigDecimal(dataStat.get("bandwidth").toString()));
            robotPages = robotPages.add(new BigDecimal(dataStat.get("robotPages").toString()));
            robotHits = robotHits.add(new BigDecimal(dataStat.get("robotHits").toString()));
            robotBandwidth = robotBandwidth.add(new BigDecimal(dataStat.get("robotBandwidth").toString()));
        }
        dataStatReport.setPages(pages);
        dataStatReport.setHits(hits);
        dataStatReport.setBandwidth(bandwidth);
        dataStatReport.setRobotPages(robotPages);
        dataStatReport.setRobotHits(robotHits);
        dataStatReport.setRobotBandwidth(robotBandwidth);


        long l1 = System.currentTimeMillis();

        List<Tuple> dataByDay = null;
        List<Tuple> normalRateList = new ArrayList<>();
        List<Tuple> countryTupleList = new ArrayList<>();
        //获得30天之前的日期
        Calendar theCa = Calendar.getInstance();
        theCa.setTime(new Date());
        theCa.add(theCa.DATE, -30);
        Date date = theCa.getTime();
        int year = theCa.get(Calendar.YEAR);
        int month = theCa.get(Calendar.MONTH) + 1;
        int day = theCa.get(Calendar.DAY_OF_MONTH);
        int beginDate = year * 10000 + month * 100 + day;
        //获得当前日期
        Calendar now = Calendar.getInstance();
        int endYear = now.get(Calendar.YEAR);
        int endMonth = now.get(Calendar.MONTH) + 1;
        int endDay = now.get(Calendar.DAY_OF_MONTH);
        int endDate = endYear * 10000 + endMonth * 100 + endDay;
        if (groupID.equals(0)) {
            dataByDay = groupsRepository.findDataVisitAll();
            normalRateList = groupsRepository.findRateAll(beginDate, endDate);
            //dataFromList=groupsRepository.findDataFromAll();
            //dataStatReport.setId(0);
        } else {
            dataByDay = groupsRepository.findDataVisitById(groupID);
            normalRateList = groupsRepository.findRateByID(beginDate, endDate, groupID);
            countryTupleList = groupsRepository.getcountryCountByIP(groupID, 20080000, 20191231);
            //dataStatReport.setId(groupID);
        }
        //设置数据来源相关数据
        List<Map<String, Object>> countListMap = new ArrayList<Map<String, Object>>();
        String domain = null;
        for (Tuple Tuple : countryTupleList) {
            Map<String, Object> longMap = new LinkedHashMap<>(16);

            domain = (Tuple.get("domain").toString()) + DomainUtil.getDomainCNName(Tuple.get("domain").toString());
            pages = new BigDecimal(Tuple.get("pages").toString());
            hits = new BigDecimal(Tuple.get("hits").toString());
            BigDecimal ips = new BigDecimal(Tuple.get("ips").toString());
            BigDecimal downloads = new BigDecimal(Tuple.get("downloads").toString());
            BigDecimal dayTime = new BigDecimal(Tuple.get("dayTime").toString());

            longMap.put("dayTime", dayTime);
            longMap.put("domain", domain);
            longMap.put("pages", pages);
            longMap.put("hits", hits);
            longMap.put("ips", ips);
            longMap.put("downloads", downloads);
            countListMap.add(longMap);
        }
        dataStatReport.setCountryCount(countListMap);

        //设置数据访问量（折线图）
        List<List<BigDecimal>> listVisit = new ArrayList<>(10);
        //设置数据下载量（折线图）
        List<List<BigDecimal>> listDownloads = new ArrayList<>(10);
        for (Tuple Tuple : dataByDay) {
            if (Tuple.get("dayTime") == null) {
                continue;
            }
            BigDecimal dayTime = new BigDecimal(Tuple.get("dayTime").toString());

            ArrayList<BigDecimal> visit = new ArrayList<>();
            BigDecimal bigDecimalVisit = new BigDecimal(Tuple.get("totalVisit").toString());
            BigDecimal bigDecimalDown = new BigDecimal(Tuple.get("bandwidth").toString());
            visit.add(dayTime);
            visit.add(bigDecimalVisit);
            listVisit.add(visit);
            dataStatReport.setVisitDaily(listVisit);

            ArrayList<BigDecimal> bandDaiLy = new ArrayList<>();
            BigDecimal bigDecimalbandDaiLy = new BigDecimal(Tuple.get("bandwidth").toString());
            bandDaiLy.add(dayTime);
            bandDaiLy.add(bigDecimalbandDaiLy);
            listDownloads.add(bandDaiLy);
            dataStatReport.setDownDaily(listDownloads);
        }
        //设置最近30天服务正常率（折线图）
        List<List<BigDecimal>> listnormalRate = new ArrayList<>(10);
        for (Tuple Tuple : normalRateList) {
            BigDecimal dayTime = new BigDecimal(Tuple.get("dayTime").toString());
            ArrayList<BigDecimal> RateNumList = new ArrayList<>();
            BigDecimal RateNum = new BigDecimal((Integer) Tuple.get("num"));
            RateNumList.add(dayTime);
            RateNumList.add(RateNum);
            listnormalRate.add(RateNumList);
            dataStatReport.setNormalRate(listnormalRate);
        }
        return dataStatReport;
    }

    public DataStatReport findVisitByTime(Integer groupID, Integer startDate, Integer endDate) {
        DataStatReport dataStatReport = new DataStatReport();
        //访问数、文件数、下载数
        List<Tuple> dataStatTupleNew = null;
        //独立IP、访问人次
        List<Tuple> dataStatTuple = null;
        //数据下载量、访问量按月统计（折线图）
        List<Tuple> dataByMonth = new ArrayList<>();
        //数据每月网页访问数、请求数、下载量按月统计（折线图）
        List<Tuple> dataPageByMonth = new ArrayList<>();
        //数据下载量、访问量按天统计（折线图）
        List<Tuple> dataByDay = new ArrayList<>();
        //国家或地区前十数据
        List<Tuple> countryTupleList = new ArrayList<>();
        //获得当前日期(设置开始日期与结束日期)
        if (startDate == null && endDate == null) {
            Calendar now = Calendar.getInstance();
            int endYear = now.get(Calendar.YEAR);
            int endMonth = now.get(Calendar.MONTH) + 1;
            int endDay = now.get(Calendar.DAY_OF_MONTH);
            startDate = endYear * 10000;
            endDate = endYear * 10000 + endMonth * 100 + endDay;
        }
        //查询所有数据库 还是单个数据库
        if (groupID.equals(1)) {
            dataStatTuple = groupsRepository.findVisitAll(startDate, endDate);
            dataStatTupleNew = groupsRepository.findPageAll(startDate, endDate);
            dataByMonth = groupsRepository.findVisitAllByMonth(startDate, endDate);
            dataPageByMonth = groupsRepository.findPageAllByMonth(startDate, endDate);
            dataByDay = groupsRepository.findDataByDay(startDate, endDate);
            dataStatReport.setId(1);
        } else {
            dataStatReport.setId(groupID);
            dataStatTuple = groupsRepository.findVisitByGroupId(groupID, startDate, endDate);
            dataStatTupleNew = groupsRepository.findPageByGroupId(groupID, startDate, endDate);
            dataByMonth = groupsRepository.findVisitAllByMonthID(groupID, startDate, endDate);
            dataPageByMonth = groupsRepository.findPageAllByMonthID(groupID, startDate, endDate);
            dataByDay = groupsRepository.findDataByDayId(groupID, startDate, endDate);
        }
        countryTupleList = groupsRepository.getcountryCountByIP(groupID, startDate, endDate);

        //设置国家或地区前十数据
        List<Map<String, Object>> countListMap = new ArrayList<Map<String, Object>>();
        String domain = null;
        for (Tuple Tuple : countryTupleList) {
            Map<String, Object> longMap = new LinkedHashMap<>(16);

            domain = (Tuple.get("domain").toString()) + DomainUtil.getDomainCNName(Tuple.get("domain").toString());
            BigDecimal pages = new BigDecimal(Tuple.get("pages").toString());
            BigDecimal hits = new BigDecimal(Tuple.get("hits").toString());
            BigDecimal ips = new BigDecimal(Tuple.get("ips").toString());
            BigDecimal downloads = new BigDecimal(Tuple.get("downloads").toString());
            BigDecimal dayTime = new BigDecimal(Tuple.get("dayTime").toString());

            longMap.put("dayTime", dayTime);
            longMap.put("domain", domain);
            longMap.put("pages", pages);
            longMap.put("hits", hits);
            longMap.put("ips", ips);
            longMap.put("downloads", downloads);
            countListMap.add(longMap);
        }
        dataStatReport.setCountryCount(countListMap);

        //设置独立IP、访问人次
        BigDecimal sumTotalVisits = new BigDecimal(0);
        BigDecimal sumTotalUnique = new BigDecimal(0);
        for (Tuple dataStat : dataStatTuple) {
            sumTotalVisits = sumTotalVisits.add(new BigDecimal(dataStat.get("sumTotalVisits").toString()));
            sumTotalUnique = sumTotalUnique.add(new BigDecimal(dataStat.get("sumTotalUnique").toString()));
        }
        dataStatReport.setSumTotalVisits(sumTotalVisits);
        dataStatReport.setSumTotalUnique(sumTotalUnique);

        //设置访问数、文件数、下载数
        BigDecimal pages = new BigDecimal(0);
        BigDecimal hits = new BigDecimal(0);
        BigDecimal bandwidth = new BigDecimal(0);
        BigDecimal robotPages = new BigDecimal(0);
        BigDecimal robotHits = new BigDecimal(0);
        BigDecimal robotBandwidth = new BigDecimal(0);
        for (Tuple dataStat : dataStatTupleNew) {
            pages = pages.add(new BigDecimal(dataStat.get("pages").toString()));
            hits = hits.add(new BigDecimal(dataStat.get("hits").toString()));
            bandwidth = bandwidth.add(new BigDecimal(dataStat.get("bandwidth").toString()));
            robotPages = robotPages.add(new BigDecimal(dataStat.get("robotPages").toString()));
            robotHits = robotHits.add(new BigDecimal(dataStat.get("robotHits").toString()));
            robotBandwidth = robotBandwidth.add(new BigDecimal(dataStat.get("robotBandwidth").toString()));
        }
        dataStatReport.setPages(pages);
        dataStatReport.setHits(hits);
        dataStatReport.setBandwidth(bandwidth);
        dataStatReport.setRobotPages(robotPages);
        dataStatReport.setRobotHits(robotHits);
        dataStatReport.setRobotBandwidth(robotBandwidth);
        //设置每月（折线图）
        List<List<BigDecimal>> listIPMonth = new ArrayList<>(10);
        //设置数据下载量（折线图）
        List<List<BigDecimal>> listVisitMonth = new ArrayList<>(10);
        //表格数据
        List<Map<String, Object>> rowsMonth = new ArrayList<Map<String, Object>>();
        List<Map<String, Object>> rowsDay = new ArrayList<Map<String, Object>>();


        for (Tuple Tuple : dataByMonth) {

            Map<String, Object> longMap = new LinkedHashMap<>(16);

            BigDecimal dayTime = new BigDecimal(Tuple.get("dayTime").toString());

            ArrayList<BigDecimal> data = new ArrayList<>();
            BigDecimal sumTotalUniqueM = new BigDecimal(Tuple.get("sumTotalUnique").toString());
            longMap.put("dayTime", dayTime);
            longMap.put("IPMonth", sumTotalUniqueM);
            data.add(dayTime);
            data.add(sumTotalUniqueM);
            listIPMonth.add(data);
            dataStatReport.setIPMonth(listIPMonth);

            BigDecimal sumTotalVisitsM = new BigDecimal(Tuple.get("sumTotalVisits").toString());
            longMap.put("VisitMonth", sumTotalVisitsM);
            ArrayList<BigDecimal> dataVisit = new ArrayList<>();
            dataVisit.add(dayTime);
            dataVisit.add(sumTotalVisitsM);
            listVisitMonth.add(dataVisit);
            dataStatReport.setVisitMonth(listVisitMonth);

            rowsMonth.add(longMap);
        }
        //每月网页访问数、请求数、下载量统计
        //每月网页访问数（折线图）
        List<List<BigDecimal>> listPageMonth = new ArrayList<>(10);
        //每月请求数（折线图）
        List<List<BigDecimal>> listHitsMonth = new ArrayList<>(10);
        //每月下载量（折线图）
        List<List<BigDecimal>> listBandwidthMonth = new ArrayList<>(10);
        for (Tuple Tuple : dataPageByMonth) {

            Map<String, Object> longMap = new LinkedHashMap<>(16);

            BigDecimal dayTime = new BigDecimal(Tuple.get("dayTime").toString());
            ArrayList<BigDecimal> dataPage = new ArrayList<>();
            BigDecimal sumPages = new BigDecimal(Tuple.get("sumPages").toString());
            longMap.put("PagesMonth", sumPages);
            dataPage.add(dayTime);
            dataPage.add(sumPages);
            listPageMonth.add(dataPage);
            dataStatReport.setPageMonth(listPageMonth);

            ArrayList<BigDecimal> dataHits = new ArrayList<>();
            BigDecimal sumHits = new BigDecimal(Tuple.get("sumHits").toString());
            longMap.put("HitsMonth", sumHits);
            dataHits.add(dayTime);
            dataHits.add(sumHits);
            listHitsMonth.add(dataHits);
            dataStatReport.setHitsMonth(listHitsMonth);

            ArrayList<BigDecimal> dataBandwidth = new ArrayList<>();
            BigDecimal sumBandwidth = new BigDecimal(Tuple.get("sumBandwidth").toString());
            longMap.put("BandMonth", sumBandwidth);
            dataBandwidth.add(dayTime);
            dataBandwidth.add(sumBandwidth);
            listBandwidthMonth.add(dataBandwidth);
            dataStatReport.setBandwidthMonth(listBandwidthMonth);

            rowsMonth.add(longMap);
        }
        dataStatReport.setRowsMonth(new ArrayList<Map<String, Object>>());
        //表格数据
        for (int i = 0; i < (rowsMonth.size() / 2); i++) {
            //临时数据
            Map<String, Object> rowsMonthr = new HashMap<>();
            rowsMonthr.putAll(rowsMonth.get(i));
            rowsMonthr.putAll(rowsMonth.get(rowsMonth.size() / 2 + i));
            dataStatReport.getRowsMonth().add(rowsMonthr);
        }
        //设置每日的数据（折线图）
        //设置每日访问人次（折线图）
        List<List<BigDecimal>> listVisitDay = new ArrayList<>(10);
        //设置每日网页访问数（折线图）
        List<List<BigDecimal>> listPageDay = new ArrayList<>(10);
        //设置每日请求数（折线图）
        List<List<BigDecimal>> listHitsDay = new ArrayList<>(10);
        //设置每日下载量（折线图）
        List<List<BigDecimal>> listBandwidthDay = new ArrayList<>(10);
        for (Tuple Tuple : dataByDay) {

            Map<String, Object> longMap = new LinkedHashMap<>(16);

            BigDecimal dayTime = new BigDecimal(Tuple.get("dayTime").toString());
            longMap.put("dayTime", dayTime);

            ArrayList<BigDecimal> dataVisits = new ArrayList<>();
            BigDecimal sumVisits = new BigDecimal(Tuple.get("sumVisits").toString());
            longMap.put("VisitsDay", sumVisits);
            dataVisits.add(dayTime);
            dataVisits.add(sumVisits);
            listVisitDay.add(dataVisits);
            dataStatReport.setVisitDay(listVisitDay);

            ArrayList<BigDecimal> dataPages = new ArrayList<>();
            BigDecimal sumPages = new BigDecimal(Tuple.get("sumPages").toString());
            longMap.put("PagesDay", sumPages);
            dataPages.add(dayTime);
            dataPages.add(sumPages);
            listPageDay.add(dataPages);
            dataStatReport.setPageDay(listPageDay);

            ArrayList<BigDecimal> dataHits = new ArrayList<>();
            BigDecimal sumHits = new BigDecimal(Tuple.get("sumHits").toString());
            longMap.put("HitsDay", sumHits);
            dataHits.add(dayTime);
            dataHits.add(sumHits);
            listHitsDay.add(dataHits);
            dataStatReport.setHitsDay(listHitsDay);

            ArrayList<BigDecimal> dataBandwidth = new ArrayList<>();
            BigDecimal sumBandwidth = new BigDecimal(Tuple.get("sumBandwidth").toString());
            longMap.put("BandDay", sumBandwidth);
            dataBandwidth.add(dayTime);
            dataBandwidth.add(sumBandwidth);
            listBandwidthDay.add(dataBandwidth);
            dataStatReport.setBandwidthDay(listBandwidthDay);

            rowsDay.add(longMap);

        }
        //表格数据
        dataStatReport.setRowsDay(new ArrayList<Map<String, Object>>());
        for (int i = 0; i < (rowsDay.size()); i++) {
            //临时数据
            Map<String, Object> rowsDayr = new HashMap<>();
            rowsDayr.putAll(rowsDay.get(i));
            dataStatReport.getRowsDay().add(rowsDayr);
        }
        List<Map<String, Object>> rows = new ArrayList<Map<String, Object>>();
        return dataStatReport;
    }

    /**
     * 根据日期获取站点的文件类型及下载量
     *
     * @param groupID
     * @param startDate
     * @param endDate
     * @param orderBy
     * @return
     */
    public DataStatReport getFileType(Integer groupID, Integer startDate, Integer endDate, String orderBy) {
        DataStatReport dataStatReport = new DataStatReport();
        List<Tuple> fileTypeTuple = null;

        //获得当前日期(设置开始日期与结束日期)
        if (startDate == null && endDate == null) {
            Calendar now = Calendar.getInstance();
            int endYear = now.get(Calendar.YEAR);
            int endMonth = now.get(Calendar.MONTH) + 1;
            int endDay = now.get(Calendar.DAY_OF_MONTH);
            startDate = endYear * 10000;
            endDate = endYear * 10000 + endMonth * 100 + endDay;
        }
        //查询所有数据库 还是单个数据库
        if (groupID.equals(0)) {
            if (orderBy == null || orderBy.equals("hits")) {
                fileTypeTuple = groupsRepository.getFileTypeHAll(startDate, endDate);
            } else {
                fileTypeTuple = groupsRepository.getFileTypeDAll(startDate, endDate);
            }
            dataStatReport.setId(0);
        } else {
            if (orderBy == null || orderBy.equals("hits")) {
                fileTypeTuple = groupsRepository.getFileTypeHById(groupID, startDate, endDate);
            } else {
                fileTypeTuple = groupsRepository.getFileTypeDByid(groupID, startDate, endDate);
            }
            dataStatReport.setId(groupID);
            //fileTypeTuple = groupsRepository.getFileTypeById(groupID, startDate, endDate);
        }
        List<Map<String, Object>> fileTypeList = new ArrayList<>(10);
        for (Tuple Tuple : fileTypeTuple) {
            Map<String, Object> longMap = new LinkedHashMap<>(16);
            ArrayList<Object> data = new ArrayList<>();
            String fileType = new String(Tuple.get("fileType").toString());
            BigDecimal sumHits = new BigDecimal(Tuple.get("sumHits").toString());
            BigDecimal sumBandWidth = new BigDecimal(Tuple.get("sumBandWidth").toString());
            longMap.put("fileType", fileType);
            longMap.put("sumHits", sumHits);
            longMap.put("sumBandWidth", sumBandWidth);
//            listfileType.add(data);
//            dataStatReport.setFileTypeList(listfileType);
            fileTypeList.add(longMap);
        }
        //表格数据
        dataStatReport.setFileTypeList(new ArrayList<Map<String, Object>>());
        for (int i = 0; i < (fileTypeList.size()); i++) {
            //临时数据
            Map<String, Object> fileTyper = new HashMap<>();
            fileTyper.putAll(fileTypeList.get(i));
            dataStatReport.getFileTypeList().add(fileTyper);
        }
        return dataStatReport;
    }

    public DataStatReport getSiteState(Integer groupID, Integer startDate, Integer endDate) {
        DataStatReport dataStatReport = new DataStatReport();
        List<Tuple> siteStateTuple = null;
        //默认查询今天
        Calendar now = Calendar.getInstance();
        int endYear = now.get(Calendar.YEAR);
        int endMonth = now.get(Calendar.MONTH) + 1;
        int endDay = now.get(Calendar.DAY_OF_MONTH);
        if (startDate == null && endDate == null) {
            startDate = endYear * 10000 + endMonth * 100 + 1;
            endDate = endYear * 10000 + endMonth * 100 + endDay;
        }


        //某组的全部服务正常率
        List<Tuple> siteRateAllTuple = null;
        int startDateAll = 20090101;
        int endDateAll = endYear * 10000 + endMonth * 100 + 31;
        siteRateAllTuple = groupsRepository.findRateByID(startDateAll, endDateAll, groupID);
        List<List<BigDecimal>> listRate = new ArrayList<>(10);
        for (Tuple Tuple : siteRateAllTuple) {
            ArrayList<BigDecimal> rateList = new ArrayList<>();
            BigDecimal num = new BigDecimal(Tuple.get("num").toString());
            BigDecimal dayTime = new BigDecimal(Tuple.get("dayTime").toString());
            rateList.add(dayTime);
            rateList.add(num);
            listRate.add(rateList);
        }
        //设置服务正常率（折线图）
        dataStatReport.setGroupRate(listRate);

        siteStateTuple = groupsRepository.findSiteState(groupID, endDate, endDate);
        List<Map<String, Object>> fileTypeList = new ArrayList<>(10);
        for (Tuple Tuple : siteStateTuple) {
            String id = new String(Tuple.get("id").toString());
            String gid = new String(Tuple.get("gid").toString());
            String name = new String(Tuple.get("name").toString());
            String gname = new String(Tuple.get("gname").toString());
            List<String> hoursList = null;
            String hoursO = Tuple.get("hours").toString();
            hoursList = Arrays.asList(hoursO.split(","));
            if (hoursList.size() < 24) {

            }
            List<String> statusList = null;
            String statusO = Tuple.get("status").toString();
            statusList = Arrays.asList(statusO.split(","));
            Map<String, String> mapq = new TreeMap<String, String>(
                    new Comparator<String>() {
                        public int compare(String obj1, String obj2) {
                            // 降序排序
                            Integer o1 = Integer.parseInt(obj1);
                            Integer o2 = Integer.parseInt(obj2);
                            return o1.compareTo(o2);
                        }
                    });
            for (int i = 0; i < hoursList.size(); i++) {
                mapq.put(hoursList.get(i), statusList.get(i));
            }
            Map<String, Object> longMap = new LinkedHashMap<>(16);
            longMap.put("id", id);
            longMap.put("gid", gid);
            longMap.put("name", name);
            longMap.put("hours", mapq);
            Map<String, Object> hourMap = (Map<String, Object>) longMap.get("hours");
            if (hourMap.size() != 24) {
                for (int hour = 0; hour < 24; hour++) {
                    String hourS = Integer.toString(hour);
                    if (hourMap.get(hourS) == null) {
                        hourMap.put(hourS, "999");
                    }
                }
                longMap.put("hours", hourMap);
            }
            fileTypeList.add(longMap);
        }
        dataStatReport.setSiteStatusList(new ArrayList<Map<String, Object>>());
        for (int i = 0; i < (fileTypeList.size()); i++) {
            //临时数据
            Map<String, Object> fileTyper = new HashMap<>();
            fileTyper.putAll(fileTypeList.get(i));
            dataStatReport.getSiteStatusList().add(fileTyper);
        }
        return dataStatReport;
    }

    public DataStatReport getSiteStateBySiteId(Integer siteId, Integer groupId, Integer startDate, Integer endDate) {
        DataStatReport dataStatReport = new DataStatReport();
        List<Tuple> siteStateTuple = null;
        //默认查询这个月
        Calendar now = Calendar.getInstance();
        int endYear = now.get(Calendar.YEAR);
        int endMonth = now.get(Calendar.MONTH) + 1;
        now = Calendar.getInstance();
        endYear = now.get(Calendar.YEAR);
        endMonth = now.get(Calendar.MONTH) + 1;
        if (startDate == null && endDate == null) {
            startDate = endYear * 10000 + endMonth * 100 + 1;
            endDate = endYear * 10000 + endMonth * 100 + now.getActualMaximum(Calendar.DATE);
        }
        //siteStateTuple = groupsRepository.findSiteStateBySite(siteId, startDate, endDate);
        siteStateTuple = groupsRepository.findSiteStateBySite(siteId, startDate, endDate);


        List<Map<String, Object>> fileTypeList = new ArrayList<>(10);

        for (Tuple Tuple : siteStateTuple) {
            String id = new String(Tuple.get("id").toString());
            String name = new String(Tuple.get("name").toString());
            String dayTime = new String(Tuple.get("dayTime").toString());
            List<String> hoursList = null;
            String hoursO = Tuple.get("hours").toString();
            hoursList = Arrays.asList(hoursO.split(","));

            List<String> statusList = null;
            String statusO = Tuple.get("status").toString();
            statusList = Arrays.asList(statusO.split(","));
            Map<String, String> mapq = new TreeMap<String, String>(
                    new Comparator<String>() {
                        public int compare(String obj1, String obj2) {
                            // 降序排序
                            Integer o1 = Integer.parseInt(obj1);
                            Integer o2 = Integer.parseInt(obj2);
                            return o1.compareTo(o2);
                        }
                    });
            for (int i = 0; i < hoursList.size(); i++) {
                mapq.put(hoursList.get(i), statusList.get(i));
            }
            Map<String, Object> longMap = new LinkedHashMap<>(16);
            longMap.put("id", id);
            longMap.put("name", name);
            longMap.put("dayTime", dayTime);
            longMap.put("hours", mapq);
            fileTypeList.add(longMap);
        }
        dataStatReport.setSiteStatusList(new ArrayList<Map<String, Object>>());
        for (int i = 0; i < (fileTypeList.size()); i++) {
            //临时数据
            Map<String, Object> fileTyper = new HashMap<>();
            fileTyper.putAll(fileTypeList.get(i));
            dataStatReport.getSiteStatusList().add(fileTyper);
        }
        dataStatReport.setId(siteId);
        return dataStatReport;
    }

    public DataStatReport getRateId(Integer groupID, Integer startDate, Integer endDate) {
        DataStatReport dataStatReport = new DataStatReport();
        List<Tuple> siteRateTuple = null;
        dataStatReport.setId(groupID);
        //默认查询今年
        if (startDate == null && endDate == null) {
            Calendar now = Calendar.getInstance();
            int endYear = now.get(Calendar.YEAR);
            startDate = endYear * 10000 + 101;
            endDate = endYear * 10000 + 1231;
        }
        siteRateTuple = groupsRepository.findRateByGroup(startDate, endDate, groupID);
        //设置服务正常率（折线图）
        List<List<BigDecimal>> listnormalRate = new ArrayList<>(10);
        //表格数据
        List<Map<String, Object>> rateList = new ArrayList<Map<String, Object>>();
        String cnName = null;
        for (Tuple Tuple : siteRateTuple) {
            Map<String, Object> longMap = new LinkedHashMap<>(16);
            BigDecimal dayTime = new BigDecimal(Tuple.get("dayTime").toString());
            cnName = new String(Tuple.get("cnName").toString());
            ArrayList<BigDecimal> RateNumList = new ArrayList<>();
            BigDecimal RateNum = new BigDecimal((Integer) Tuple.get("num"));
            RateNumList.add(dayTime);
            RateNumList.add(RateNum);
            longMap.put("dayTime", dayTime);
            longMap.put("RateNum", RateNum);
            listnormalRate.add(RateNumList);
            dataStatReport.setGroupRate(listnormalRate);

            rateList.add(longMap);
        }
        dataStatReport.setName(cnName);
        //表格数据
        dataStatReport.setGroupRateList(new ArrayList<Map<String, Object>>());
        for (int i = 0; i < (rateList.size()); i++) {
            //临时数据
            Map<String, Object> RateMap = new HashMap<>();
            RateMap.putAll(rateList.get(i));
            dataStatReport.getGroupRateList().add(RateMap);
        }
        return dataStatReport;
    }

    public String getBygroupId(Integer groupId) {
        Optional<Group> name = (groupsRepository.findById(groupId));
        return name.get().getCnName();
    }

    public String getBySiteId(Integer siteId) {
        Optional<Site> name = (siteRepository.findById(siteId));
        return name.get().getCnName();
    }

    public DataStatReport getFromCountById(Integer groupID, Integer startDate, Integer endDate, String countType, String sortType) {
        DataStatReport dataStatReport = new DataStatReport();
        //默认查询今年
        if (startDate == null && endDate == null) {
            Calendar now = Calendar.getInstance();
            int endYear = now.get(Calendar.YEAR);
            startDate = endYear * 10000 + 101;
            endDate = endYear * 10000 + 1231;
        }
        Sort sort = new Sort(Sort.Direction.DESC, sortType);
        List<Tuple> countryTuple = null;
        List<Tuple> internalTuple = null;
        List<Tuple> yardTuple = null;
        //ip,page,hits,downloads
        if (countType.equals("country")) {
            if (sortType.equals("ip")) {
                countryTuple = groupsRepository.getcountryCountByIP(groupID, startDate, endDate);
            } else if (sortType.equals("page")) {
                countryTuple = groupsRepository.getcountryCountByPage(groupID, startDate, endDate);
            } else if (sortType.equals("hits")) {
                countryTuple = groupsRepository.getcountryCountByHits(groupID, startDate, endDate);
            } else if (sortType.equals("downloads")) {
                countryTuple = groupsRepository.getcountryCountByDownloads(groupID, startDate, endDate);
            }
        }
        if (countType.equals("internal")) {
            if (sortType.equals("ip")) {
                countryTuple = groupsRepository.getinternalCountByIP(groupID, startDate, endDate);
            } else if (sortType.equals("page")) {
                countryTuple = groupsRepository.getinternalCountByPage(groupID, startDate, endDate);
            } else if (sortType.equals("hits")) {
                countryTuple = groupsRepository.getinternalCountByHits(groupID, startDate, endDate);
            } else if (sortType.equals("downloads")) {
                countryTuple = groupsRepository.getinternalCountByDownloads(groupID, startDate, endDate);
            }
        }
        if (countType.equals("yard")) {
            if (sortType.equals("ip")) {
                countryTuple = groupsRepository.getyardCountByIP(groupID, startDate, endDate);
            } else if (sortType.equals("page")) {
                countryTuple = groupsRepository.getyardCountByPage(groupID, startDate, endDate);
            } else if (sortType.equals("hits")) {
                countryTuple = groupsRepository.getyardCountByHits(groupID, startDate, endDate);
            } else if (sortType.equals("downloads")) {
                countryTuple = groupsRepository.getyardCountByDownloads(groupID, startDate, endDate);
            }
        }

        List<List<Object>> listCountryCount = new ArrayList<>(10);
        List<Map<String, Object>> countListMap = new ArrayList<Map<String, Object>>();
        String cnName = null;
        String domain = null;
        for (Tuple Tuple : countryTuple) {
            Map<String, Object> longMap = new LinkedHashMap<>(16);
            cnName = new String(Tuple.get("cnName").toString());
            if (countType.equals("country")) {
                domain = (Tuple.get("domain").toString()) + DomainUtil.getDomainCNName(Tuple.get("domain").toString());
            } else if (countType.equals("internal")) {
                domain = Tuple.get("domain").toString();
            } else if (countType.equals("yard")) {
                if (Tuple.get("domain").toString().equals("0")) {
                    domain = "其他用户";
                } else if (Tuple.get("domain").toString().equals("1")) {
                    domain = "科技网用户";
                } else if (Tuple.get("domain").toString().equals("2")) {
                    domain = "教育网用户";
                }
            }
            BigDecimal pages = new BigDecimal(Tuple.get("pages").toString());
            BigDecimal hits = new BigDecimal(Tuple.get("hits").toString());
            BigDecimal ips = new BigDecimal(Tuple.get("ips").toString());
            BigDecimal downloads = new BigDecimal(Tuple.get("downloads").toString());
            BigDecimal dayTime = new BigDecimal(Tuple.get("dayTime").toString());
            ArrayList<Object> countryCount = new ArrayList<>();
            longMap.put("dayTime", dayTime);
            longMap.put("domain", domain);
            longMap.put("pages", pages);
            longMap.put("hits", hits);
            longMap.put("ips", ips);
            longMap.put("downloads", downloads);
            countryCount.add(domain);
            countListMap.add(longMap);
            if (sortType.equals("ip")) {
                countryCount.add(ips);
            } else if (sortType.equals("page")) {
                countryCount.add(pages);
            } else if (sortType.equals("hits")) {
                countryCount.add(hits);
            } else if (sortType.equals("downloads")) {
                countryCount.add(downloads);
            }
            listCountryCount.add(countryCount);
        }
        dataStatReport.setCountryCount(countListMap);
        dataStatReport.setName(cnName);
        return dataStatReport;
    }
}
