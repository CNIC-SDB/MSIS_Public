package cn.csdb.msis.integrator.web;

import cn.csdb.msis.integrator.bo.DataStatReport;
import cn.csdb.msis.integrator.service.DataStatService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 数据库统计Controller
 *
 * @author CXY
 * @Date 2019/7/29 10:25
 */
@Controller
@RequestMapping("/integrator/dataStat")
public class DataStatController {
    @Autowired
    DataStatService dataStatService;


    /**
     * 综合报告
     *
     * @param model
     * @param groupID 站点Id
     * @return 数据和页面
     */
    @RequestMapping(value = {"", "/synthesize"})
    public String synthesize(Model model, @RequestParam(name = "groupID", required = false) Integer groupID, @RequestParam(name = "unitId", required = false) Integer unitId, HttpServletRequest request) {
        request.getRequestURL();
        System.out.println(request.getRequestURL());
        DataStatReport dataStatReport = new DataStatReport();
        List<Map<String, Object>> groupsVisitList = new ArrayList<>();
        dataStatReport = dataStatService.findVisit(groupID);
        dataStatReport.setId(groupID);
        if (unitId != null) {
            dataStatReport.setId(unitId);
        }
        model.addAttribute("dataStatReport", dataStatReport);
        return "integrator/page/synthesize";
    }

    /**
     * 综合报告
     *
     * @param model
     * @param groupID 站点Id
     * @return 数据和页面
     */
    @ResponseBody
    @RequestMapping(value = {"/synthesize/initPage"})
    public DataStatReport synthesizeInit(Model model, @RequestParam(name = "groupID", required = false) Integer groupID, @RequestParam(name = "unitId", required = false) Integer unitId) {
        DataStatReport dataStatReport = new DataStatReport();
        List<Map<String, Object>> groupsVisitList = new ArrayList<>();
        List<List<BigDecimal>> listVisit = new ArrayList<>(10);
        dataStatReport = dataStatService.findVisitInit(groupID);
        dataStatReport.setId(groupID);
        if (unitId != null) {
            dataStatReport.setId(unitId);
        }
        return dataStatReport;
    }

    /**
     * 综合报告
     *
     * @param model
     * @param groupID 站点Id
     * @return 数据和页面
     */
    @RequestMapping(value = {"/timeAnalyse"})
    public String timeAnalyse(Model model, @RequestParam(name = "groupID", required = false) Integer groupID, @RequestParam(name = "startDate", required = false) Integer startDate, @RequestParam(name = "endDate", required = false) Integer endDate) {
        DataStatReport dataStatReport = new DataStatReport();
        if (groupID == null) {
            groupID = 1;
            dataStatReport.setId(1);
            dataStatReport = dataStatService.findVisitByTime(groupID, startDate, endDate);
            model.addAttribute("dataStatReport", dataStatReport);
            return "integrator/page/timeAnalyse";
        } else {
            dataStatReport = dataStatService.findVisitByTime(groupID, startDate, endDate);
            dataStatReport.setId(groupID);
        }
        model.addAttribute("dataStatReport", dataStatReport);
        return "integrator/page/timeAnalyse";
    }

    @ResponseBody
    @RequestMapping(value = {"/timeAnalyse/getData"})
    public DataStatReport timeAnalyseData(Model model, @RequestParam(name = "groupID", required = false) Integer groupID, @RequestParam(name = "startDate", required = false) Integer startDate, @RequestParam(name = "endDate", required = false) Integer endDate) {
        DataStatReport dataStatReport = new DataStatReport();
        if (groupID == null) {
            dataStatReport.setId(0);
        } else {
            dataStatReport.setId(groupID);
        }
        dataStatReport = dataStatService.findVisitByTime(groupID, startDate, endDate);
        return dataStatReport;
    }

    @RequestMapping(value = {"/fromAnalyse"})
    public String fromAnalyseInit(Model model, @RequestParam(name = "groupID", required = false) Integer groupID, @RequestParam(name = "startDate", required = false) Integer startDate, @RequestParam(name = "endDate", required = false) Integer endDate) {
        DataStatReport dataStatReport = new DataStatReport();
        dataStatReport.setId(groupID);
        model.addAttribute("dataStatReport", dataStatReport);
        return "integrator/page/fromAnalyse";
    }

    @ResponseBody
    @RequestMapping(value = {"/fromAnalyse/initPage"})
    public DataStatReport fromAnalyse(Model model, @RequestParam(name = "groupID", required = false) Integer groupID, @RequestParam(name = "startDate", required = false) Integer startDate, @RequestParam(name = "endDate", required = false) Integer endDate, @RequestParam(name = "countType", required = false) String countType, @RequestParam(name = "sortType", required = false) String sortType) {
        DataStatReport dataStatReport = new DataStatReport();
        dataStatReport.setId(groupID);
        dataStatReport = dataStatService.getFromCountById(groupID, startDate, endDate, countType, sortType);
        model.addAttribute("dataStatReport", dataStatReport);
        return dataStatReport;
    }

    @RequestMapping(value = {"/naturalAnalyse"})
    public String naturalAnalyse(Model model, @RequestParam(name = "groupID", required = false) Integer groupID, @RequestParam(name = "startDate", required = false) Integer startDate, @RequestParam(name = "endDate", required = false) Integer endDate, @RequestParam(name = "orderBy", required = false) String orderBy) {
        DataStatReport dataStatReport = new DataStatReport();
        if (groupID == null) {
            groupID = 0;
            dataStatReport.setId(0);
        } else {
            dataStatReport.setId(groupID);
        }
        dataStatReport = dataStatService.getFileType(groupID, startDate, endDate, orderBy);
        if (groupID == null) {
            groupID = 1;
        }
        dataStatReport.setId(groupID);
        model.addAttribute("dataStatReport", dataStatReport);
        return "integrator/page/naturalAnalyse";
    }

    @ResponseBody
    @RequestMapping(value = {"/naturalAnalyse/getData"})
    public DataStatReport naturalAnalyseData(Model model, @RequestParam(name = "groupID", required = false) Integer groupID, @RequestParam(name = "startDate", required = false) Integer startDate, @RequestParam(name = "endDate", required = false) Integer endDate, @RequestParam(name = "orderBy", required = false) String orderBy) {
        DataStatReport dataStatReport = new DataStatReport();
        if (groupID == null) {
            groupID = 0;
            dataStatReport.setId(0);
        } else {
            dataStatReport.setId(groupID);
        }
        dataStatReport = dataStatService.getFileType(groupID, startDate, endDate, orderBy);
        if (groupID == null) {
            groupID = 1;
        }
        dataStatReport.setId(0);
        model.addAttribute("dataStatReport", dataStatReport);
        return dataStatReport;
    }
    @RequestMapping(value = {"/siteAnalyse"})
    public String siteAnalyse(Model model, @RequestParam(name = "groupID", required = false) Integer groupID, @RequestParam(name = "startDate", required = false) Integer startDate, @RequestParam(name = "endDate", required = false) Integer endDate) {
        DataStatReport dataStatReport = new DataStatReport();
        if (groupID == null) {
            groupID = 0;
            dataStatReport.setId(0);
        } else {
            dataStatReport.setId(groupID);
        }
        dataStatReport = dataStatService.getSiteState(groupID, startDate, endDate);
        if (groupID == null) {
            groupID = 1;
        }
        dataStatReport.setId(groupID);
        model.addAttribute("dataStatReport", dataStatReport);
        return "integrator/page/siteAnalyse";
    }

    @RequestMapping(value = {"/siteAnalyse/site"})
    public String siteAnalyseMonth(Model model, @RequestParam(name = "siteId", required = false) Integer siteId, @RequestParam(name = "groupID", required = false) Integer groupId, @RequestParam(name = "startDate", required = false) Integer startDate, @RequestParam(name = "endDate", required = false) Integer endDate) {
        DataStatReport dataStatReport = new DataStatReport();
        dataStatReport = dataStatService.getSiteStateBySiteId(siteId, groupId, startDate, endDate);
        if (groupId == null) {
            groupId = 1;
        }
        dataStatReport.setId(groupId);
        if (dataStatReport.getSiteStatusList().size() == 0) {
            dataStatReport.setName(dataStatService.getBySiteId(siteId));
        } else {
            dataStatReport.setName(dataStatReport.getSiteStatusList().get(0).get("name").toString());
        }
        model.addAttribute("dataStatReport", dataStatReport);
        model.addAttribute("groupId", groupId);
        model.addAttribute("siteId", siteId);
        return "integrator/page/siteAnalyseSite";
    }

    @ResponseBody
    @RequestMapping(value = {"/siteAnalyse/site/getData"})
    public DataStatReport siteAnalyseGetData(Model model, @RequestParam(name = "siteID", required = false) Integer siteId, @RequestParam(name = "groupId", required = false) Integer groupId, @RequestParam(name = "startDate", required = false) Integer startDate, @RequestParam(name = "endDate", required = false) Integer endDate, @RequestParam(name = "siteName", required = false) String siteName) {
        DataStatReport dataStatReport = new DataStatReport();
        dataStatReport.setId(groupId);
        dataStatReport = dataStatService.getSiteStateBySiteId(siteId, groupId, startDate, endDate);
        if (groupId == null) {
            groupId = 1;
        }
        dataStatReport.setId(groupId);
        dataStatReport.setName(siteName);
        model.addAttribute("dataStatReport", dataStatReport);
        model.addAttribute("siteId", siteId);
        return dataStatReport;
    }

    @RequestMapping(value = {"/rateAnalyse"})
    public String rateAnalyse(Model model, @RequestParam(name = "groupID", required = false) Integer groupID, @RequestParam(name = "startDate", required = false) Integer startDate, @RequestParam(name = "endDate", required = false) Integer endDate) {
        DataStatReport dataStatReport = new DataStatReport();
        dataStatReport = dataStatService.getRateId(groupID, startDate, endDate);
        if (groupID == null) {
            groupID = 1;
        }
        dataStatReport.setId(groupID);
        model.addAttribute("dataStatReport", dataStatReport);
        return "integrator/page/rateAnalyse";
    }

    @ResponseBody
    @RequestMapping(value = {"/rateAnalyse/getData"})
    public DataStatReport rateAnalyseGetData(Model model, @RequestParam(name = "groupID", required = false) Integer groupID, @RequestParam(name = "startDate", required = false) Integer startDate, @RequestParam(name = "endDate", required = false) Integer endDate) {
        DataStatReport dataStatReport = new DataStatReport();
        dataStatReport = dataStatService.getRateId(groupID, startDate, endDate);
        if (groupID == null) {
            groupID = 1;
        }
        dataStatReport.setId(groupID);
        model.addAttribute("dataStatReport", dataStatReport);
        return dataStatReport;
    }
}