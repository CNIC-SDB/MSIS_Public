package cn.csdb.msis.manager.service;

import cn.csdb.msis.integrator.util.DateUtil;
import cn.csdb.msis.stats.entity.*;
import cn.csdb.msis.stats.repository.*;
import com.maxmind.util.AcHostUtil;
import com.maxmind.util.AreaHostUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @Author jinbao
 * @Date 2019/9/18 17:00
 * @Description 处理awstats结果文件，结果入库
 **/
@Service
public class DataExtractService {

    Logger logger = LoggerFactory.getLogger(DataExtractService.class);

    private static final String regex = "(awstats)(\\d{2})(\\d{4})(.)(\\S+)(.txt)";
    private static final Pattern pattern = Pattern.compile(regex);
    @Autowired
    AwAchostRepository awAchostDao;
    @Autowired
    AwAreaRepository awAreaDao;
    @Autowired
    AwDayRepository awDayDao;
    @Autowired
    AwDomainRepository awDomainDao;
    @Autowired
    AwFileTypeRepository awFileTypeDao;
    @Autowired
    AwGeneralRepository awGeneralDao;
    @Autowired
    AwTimeRepository awTimeDao;
    @Autowired
    DataFileRepository dataFileDao;
    @Autowired
    SiteRepository siteDao;
    @Value(value = "${custom.awstatsResultPath}")
    private String awstatsResultPath;

    public DataExtractService() {
    }

    public void extractProcess() {
        logger.info("aw结果入库定时任务 : 处理开始");
        Integer year = null;
        Integer month = null;
        String unitName = null;

        File[] awstatsResultFileList = getAwstatsResultFileList();
        Map<String, DataFile> dataFileMap = getDataFileMap();
        Map<String, Site> siteMap = getSiteMap();
        // fileName awstats012008.www.wuli1_0927.txt
        for (File file : awstatsResultFileList) {
            Matcher matcher = pattern.matcher(file.getName());
            if (matcher.matches()) {
                month = Integer.valueOf(matcher.group(2));
                year = Integer.valueOf(matcher.group(3));
                unitName = matcher.group(5);
                if (siteMap.containsKey(unitName)) {
                    Site site = siteMap.get(unitName);
                    DataExtractProcessor de = new DataExtractProcessor(site, year, month, file, dataFileMap);
                    de.extractData();
                    de = null;
                }
            }
        }
        logger.info("aw结果入库定时任务 : 处理结束");
    }

    /**
     * 获取记录中的文件信息
     *
     * @return
     */
    private Map<String, DataFile> getDataFileMap() {
        Map<String, DataFile> map = new HashMap<>(16);
        List<DataFile> dataFileList = dataFileDao.findAll();
        for (DataFile dataFile : dataFileList) {
            map.put(dataFile.getName(), dataFile);
        }
        return map;
    }

    /**
     * 获取本地解析结果
     *
     * @return
     */
    private File[] getAwstatsResultFileList() {
        return new File(awstatsResultPath).listFiles();
    }

    /**
     * 获取全部站点信息
     *
     * @return
     */
    private Map<String, Site> getSiteMap() {
        Map<String, Site> siteMap = new HashMap<>(16);
        List<Site> siteList = siteDao.findAll();
        for (Site site : siteList) {
            siteMap.put(site.getUri(), site);
        }
        return siteMap;
    }

    class DataExtractProcessor {

        private Logger logger = LoggerFactory.getLogger(this.getClass());

        private Site site;

        private Integer year;

        private Integer month;

        private File file;

        private Map<String, DataFile> dataFileMap;

        private DataFile currentDataFile;

        public DataExtractProcessor(Site site, Integer year, Integer month, File file, Map<String, DataFile> dataFileMap) {
            this.site = site;
            this.year = year;
            this.month = month;
            this.file = file;
            this.dataFileMap = dataFileMap;
        }

        @Transactional
        public void extractData() {

            int dataFlag;
            dataFlag = newDataFlag();
            String name = currentDataFile.getName();
            try {
                switch (dataFlag) {
                    case 0:
                        logger.info("首次抽取数据开始 : " + name);
                        doExtractData();
                        logger.info("首次抽取数据结束 : " + name);
                        break;
                    case 1:
                        logger.info("清理老数据 : " + name);
                        cleanData();
                        logger.info("重新抽取数据 : " + name);
                        doExtractData();
                        logger.info("抽取数据完成 : " + name);
                        break;
                    default:
                        logger.info("无更新 : " + name);
                }
            } catch (Exception e) {
                e.printStackTrace();
                logger.error("数据抽取失败" + name);
            }
        }

        /**
         * 当前结果文件状态码
         *
         * @return 0:文件从未被解析	1:已被解析但有更新	2:已被解析没有更新
         */
        int newDataFlag() {
            String fileName = file.getName();
            if (dataFileMap.containsKey(fileName)) {
                currentDataFile = dataFileMap.get(fileName);
                if (file.lastModified() > currentDataFile.getLastModifyDate() && file.length() != currentDataFile.getSize()) {
                    currentDataFile.setLastModifyDate(file.lastModified());
                    currentDataFile.setSize(file.length());
                    return 1;
                } else {
                    return 2;
                }
            } else {
                DataFile df = new DataFile();
                df.setName(fileName);
                df.setLastModifyDate(file.lastModified());
                df.setSize(file.length());
                currentDataFile = df;
                return 0;
            }
        }

        /**
         * 清除老数据
         */
        void cleanData() {
            awGeneralDao.deleteByFid(currentDataFile.getId());
            awTimeDao.deleteByFid(currentDataFile.getId());
            awDayDao.deleteByFid(currentDataFile.getId());
            awFileTypeDao.deleteByFid(currentDataFile.getId());
            awDomainDao.deleteByFid(currentDataFile.getId());
            awAreaDao.deleteByFid(currentDataFile.getId());
            awAchostDao.deleteByFid(currentDataFile.getId());
        }

        /**
         * 执行解析
         */
        void doExtractData() {
            logger.info("执行插入");
            // 更新awstats结果文件信息的记录
            currentDataFile = dataFileDao.save(currentDataFile);
            BufferedReader br = null;
            String line;
            try {
                br = new BufferedReader(new InputStreamReader(new FileInputStream(file), StandardCharsets.UTF_8));


                while ((line = br.readLine()) != null) {

                    // !!!attention 当出现回环地址的时候跳出当前行的解析
                    // # Host - Pages - Hits - Bandwidth - Last visit date - [Start date of last visit] - [Last page of last visit]
                    if (line.startsWith("BEGIN_VISITOR")) {
                        Map<Integer, AwAchost> awAchostMap = new HashMap<>();
                        Map<String, AwArea> awAreaMap = new HashMap<>();
                        Map<String, AwDomain> awDomainMap = new HashMap<>();

                        line:
                        while (!(line = br.readLine()).startsWith("END_VISITOR")) {
                            String[] parts = line.split("\\s+");
                            logger.debug("parts[0]:" + parts[0]);
                            String domain = AreaHostUtil.getDomain(parts[0]);
                            if (domain == null) {
                                continue line;
                            }
                            logger.debug("domain:" + domain);
                            packAwdomain(parts, domain, awDomainMap);

                            String area = AreaHostUtil.getCNArea(parts[0]);
                            packAwArea(parts, area, awAreaMap);

                            int type = AcHostUtil.getHostType(parts[0]);
                            if (type == 0 && domain.equals("cn")) {
                                type = 3;
                            }
                            packAwAcHost(parts, type, awAchostMap);
                        }

                        awAchostDao.saveAll(awAchostMap.values());
                        awAreaDao.saveAll(awAreaMap.values());
                        awDomainDao.saveAll(awDomainMap.values());
                    }

                    if (line.startsWith("BEGIN_GENERAL")) {
                        AwGeneral awGeneral = new AwGeneral();
                        awGeneral.setSite(site);
                        awGeneral.setYear(year);
                        awGeneral.setMonth(month);
                        awGeneral.setDataFile(currentDataFile);
                        while (!(line = br.readLine()).startsWith("END_GENERAL")) {
                            packAwGeneral(line, awGeneral);
                        }
                        awGeneralDao.save(awGeneral);
                    }

                    // # Hour - Pages - Hits - Bandwidth - Not viewed Pages - Not viewed Hits - Not viewed Bandwidth
                    if (line.startsWith("BEGIN_TIME")) {
                        List<AwTime> awTimeList = new ArrayList<>(10);
                        while (!(line = br.readLine()).startsWith("END_TIME")) {
                            AwTime awTime = packAwTime(line);
                            if (awTime != null) {
                                awTimeList.add(awTime);
                            }

                        }
                        awTimeDao.saveAll(awTimeList);
                    }

                    // # Date - Pages - Hits - Bandwidth - Visits
                    if (line.startsWith("BEGIN_DAY")) {
                        List<AwDay> awDayList = new ArrayList<>(10);
                        while (!(line = br.readLine()).startsWith("END_DAY")) {
                            AwDay awDay = packAwDay(line);
                            if (awDay != null) {
                                awDayList.add(awDay);
                            }
                        }
                        awDayDao.saveAll(awDayList);
                    }

                    // # Files type - Hits - Bandwidth - Bandwidth without compression - Bandwidth after compression
                    if (line.startsWith("BEGIN_FILETYPES")) {
                        List<AwFileType> awFileTypeList = new ArrayList<>(10);
                        while (!(line = br.readLine()).startsWith("END_FILETYPES")) {
                            AwFileType awFileType = packAwFileType(line);
                            if (awFileType != null) {
                                awFileTypeList.add(awFileType);
                            }
                            awFileTypeDao.saveAll(awFileTypeList);
                        }
                    }
                }
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        private void packAwAcHost(String[] parts, int type, Map<Integer, AwAchost> awAchostMap) {
            AwAchost achost = awAchostMap.get(type);
            if (achost == null) {
                achost = new AwAchost();
                achost.setSite(site);
                achost.setDataFile(currentDataFile);
                achost.setMonth(month);
                achost.setYear(year);
                achost.setHostType(type);
                achost.setPages(Long.valueOf(parts[1]));
                achost.setHits(Long.valueOf(parts[2]));
                achost.setBandwidth(Long.valueOf(parts[3]));
                achost.setIps(1L);
                awAchostMap.put(type, achost);
            } else {
                achost.setPages(achost.getPages() + Long.valueOf(parts[1]));
                achost.setHits(achost.getHits() + Long.valueOf(parts[2]));
                achost.setBandwidth(achost.getBandwidth() + Long.valueOf(parts[3]));
                achost.setIps(achost.getIps() + 1);
            }

        }

        private void packAwArea(String[] parts, String area, Map<String, AwArea> awAreaMap) {
            if (area != null) {
                AwArea awArea = awAreaMap.get(area);
                if (awArea == null) {
                    awArea = new AwArea();
                    awArea.setSite(site);
                    awArea.setDataFile(currentDataFile);
                    awArea.setMonth(month);
                    awArea.setYear(year);
                    awArea.setArea(area);
                    awArea.setIps(1L);
                    awArea.setPages(Long.valueOf(parts[1]));
                    awArea.setHits(Long.valueOf(parts[2]));
                    awArea.setBandwidth(Long.valueOf(parts[3]));
                    awAreaMap.put(area, awArea);

                } else {
                    awArea.setPages(awArea.getPages() + Long.valueOf(parts[1]));
                    awArea.setHits(awArea.getHits() + Long.valueOf(parts[2]));
                    awArea.setBandwidth(awArea.getBandwidth() + Long.valueOf(parts[3]));
                    awArea.setIps(awArea.getIps() + 1L);
                }
            }
        }

        private void packAwdomain(String[] parts, String domain, Map<String, AwDomain> awDomainMap) {
            if (domain != null) {
                AwDomain awdomain = awDomainMap.get(domain);
                if (awdomain == null) {
                    awdomain = new AwDomain();
                    awdomain.setSite(site);
                    awdomain.setDataFile(currentDataFile);
                    awdomain.setMonth(month);
                    awdomain.setYear(year);
                    awdomain.setDomain(domain);
                    awdomain.setPages(Long.valueOf(parts[1]));
                    awdomain.setHits(Long.valueOf(parts[2]));
                    awdomain.setBandwidth(Long.valueOf(parts[3]));
                    awdomain.setIps(1L);
                    awDomainMap.put(domain, awdomain);

                } else {
                    awdomain.setPages(awdomain.getPages() + Long.valueOf(parts[1]));
                    awdomain.setHits(awdomain.getHits() + Long.valueOf(parts[2]));
                    awdomain.setBandwidth(awdomain.getBandwidth() + Long.valueOf(parts[3]));
                    awdomain.setIps(awdomain.getIps() + 1L);
                }
            }
        }

        private AwFileType packAwFileType(String line) {
            String[] parts = line.split("\\s");
            AwFileType obj = null;
            if (parts.length >= 3) {
                obj = new AwFileType();
                obj.setSite(site);
                obj.setMonth(month);
                obj.setYear(year);

                obj.setFileType(parts[0]);
                try {
                    obj.setHits(Long.valueOf(parts[1]));
                    obj.setBandwidth(new Long(parts[2]));
                } catch (Exception e) {
                    logger.error("packAwFileType error: " + line);
                    obj = null;
                }
                obj.setDataFile(currentDataFile);
            }
            parts = null;
            return obj;
        }

        private AwDay packAwDay(String line) {
            String[] parts = line.split("\\s");
            AwDay awDay = new AwDay();
            try {
                Date date = DateUtil.getDateOfDayStats(parts[0]);
                Calendar cl = Calendar.getInstance();
                cl.setTime(date);
                awDay.setMonth(cl.get(Calendar.MONTH) + 1);
                awDay.setYear(cl.get(Calendar.YEAR));
                awDay.setDay(cl.get(Calendar.DAY_OF_MONTH));
                awDay.setSite(site);

                awDay.setPages(Long.valueOf(parts[1]));
                awDay.setHits(Long.valueOf(parts[2]));
                awDay.setBandwidth(new Long(parts[3]));
                awDay.setVisits(Long.valueOf(parts[4]));
                awDay.setDataFile(currentDataFile);
            } catch (Exception e) {
                logger.error("packAwDay error: " + line);
                awDay = null;
            }
            return awDay;
        }

        private AwTime packAwTime(String line) {
            String[] parts = line.split("\\s");
            AwTime awTime = new AwTime();
            awTime.setSite(site);
            awTime.setMonth(month);
            awTime.setYear(year);
            try {
                awTime.setHour(Integer.valueOf(parts[0]));
                awTime.setPages(Long.valueOf(parts[1]));
                awTime.setHits(Long.valueOf(parts[2]));
                awTime.setBandwidth(new Long(parts[3]));
                awTime.setNonViewedPages(Long.valueOf(parts[4]));
                awTime.setNonViewedHits(Long.valueOf(parts[5]));
                awTime.setNonViewedBandwidth(new Long(parts[6]));
                awTime.setDataFile(currentDataFile);
            } catch (Exception e) {
                logger.error("packAwTime error: " + line);
                awTime = null;
            }
            return awTime;
        }

        private void packAwGeneral(String line, AwGeneral awGeneral) {
            if (line.startsWith("TotalVisits") || line.startsWith("TotalUnique")) {
                String[] parts = line.split("\\s");
                if (line.startsWith("TotalVisits")) {
                    awGeneral.setTotalVisits(Long.valueOf(parts[1]));
                }
                if (line.startsWith("TotalUnique")) {
                    awGeneral.setTotalUnique(Long.valueOf(parts[1]));
                }
            }
        }

    }

}

