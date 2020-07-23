package cn.csdb.msis.integrator.web;

import cn.csdb.msis.integrator.service.TerritoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.persistence.Tuple;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * 领域统计Controller
 *
 * @author CXY
 * @Date 2019/7/22 10:25
 */
@Controller
@RequestMapping("/integrator/territory")
public class TerritoryController {
    @Autowired
    TerritoryService territoryService;

    /**
     * 获得点击量排名
     *
     * @param model
     * @param startDate 查询的开始时间
     * @param endDate   查询的结束时间
     * @return 页面和数据
     */
	@RequestMapping(value = {"", "/visit"})
    public String visitTable(Model model, @RequestParam(name = "startDate", required = false) Integer startDate, @RequestParam(name = "endDate", required = false) Integer endDate) {
        List<Tuple> territoryList = territoryService.findByVisit(startDate, endDate);
        List<Map<String, Object>> resultList = new ArrayList<>();
        //循环获得数据
        for (Tuple tuple : territoryList) {
            Map<String, Object> longMap = new LinkedHashMap<>(16);
            String name = new String(tuple.get("name").toString());
            Long sumPages = new Long(tuple.get("sumPages").toString());
            Long sumHits = new Long(tuple.get("sumHits").toString());
            Long sumBandwidth = new Long(tuple.get("sumBandwidth").toString());
            longMap.put("name", name);
            longMap.put("sumPages", sumPages);
            longMap.put("sumHits", sumHits);
            longMap.put("sumBandwidth", sumBandwidth);
            resultList.add(longMap);
        }
        model.addAttribute("resultList", resultList);
        return "integrator/page/territoryVisit";
    }

    /**
     * 获得点击量排名（ajax）
     * @param model
     * @param startDate 查询的开始时间
     * @param endDate 查询的结束时间
     * @return 数据结果集
     */
    @RequestMapping(value = {"/getData"})
    @ResponseBody
    public List<Map<String, Object>> getData(Model model, @RequestParam(name = "startDate", required = false) Integer startDate, @RequestParam(name = "endDate", required = false) Integer endDate) {
        List<Tuple> territoryList = territoryService.findByVisit(startDate, endDate);
        List<Map<String, Object>> resultList = new ArrayList<>();
        //循环获得数据
        for (Tuple tuple : territoryList) {
            Map<String, Object> longMap = new LinkedHashMap<>(16);
            String name = new String(tuple.get("name").toString());
            Long sumPages = new Long(tuple.get("sumPages").toString());
            Long sumHits = new Long(tuple.get("sumHits").toString());
            Long sumBandwidth = new Long(tuple.get("sumBandwidth").toString());
            longMap.put("name", name);
            longMap.put("sumPages", sumPages);
            longMap.put("sumHits", sumHits);
            longMap.put("sumBandwidth", sumBandwidth);
            resultList.add(longMap);
        }
        model.addAttribute("resultList", resultList);
        return resultList;
    }

    /**
     * 获得下载量排名
     * @param model
     * @param startDate 查询的开始时间
     * @param endDate 查询的结束时间
     * @return 页面和数据
     */
    @RequestMapping("/downloads")
    public String downloadsTable(Model model, @RequestParam(name = "startDate", required = false) Integer startDate, @RequestParam(name = "endDate", required = false) Integer endDate) {
        List<Tuple> territoryList = territoryService.findByDownloads(startDate, endDate);
        List<Map<String, Object>> resultList = new ArrayList<>();
        //循环获得数据
        for (Tuple tuple : territoryList) {
            Map<String, Object> longMap = new LinkedHashMap<>(16);
            String name = new String(tuple.get("name").toString());
            Long sumPages = new Long(tuple.get("sumPages").toString());
            Long sumHits = new Long(tuple.get("sumHits").toString());
            Long sumBandwidth = new Long(tuple.get("sumBandwidth").toString());
            longMap.put("name", name);
            longMap.put("sumPages", sumPages);
            longMap.put("sumHits", sumHits);
            longMap.put("sumBandwidth", sumBandwidth);
            resultList.add(longMap);
        }
        model.addAttribute("resultList", resultList);
        return "integrator/page/territoryDownloads";
    }

    /**
     * 获得下载量排名（ajax）
     * @param model
     * @param startDate 查询的开始时间
     * @param endDate 查询的结束时间
     * @return 数据结果集
     */
    @RequestMapping("/downloadData")
    @ResponseBody
    public List<Map<String, Object>> downloadData(Model model, @RequestParam(name = "startDate", required = false) Integer startDate, @RequestParam(name = "endDate", required = false) Integer endDate) {
        List<Tuple> territoryList = territoryService.findByDownloads(startDate, endDate);
        List<Map<String, Object>> resultList = new ArrayList<>();
        //循环获得数据
        for (Tuple tuple : territoryList) {
            Map<String, Object> longMap = new LinkedHashMap<>(16);
            String name = new String(tuple.get("name").toString());
            Long sumPages = new Long(tuple.get("sumPages").toString());
            Long sumHits = new Long(tuple.get("sumHits").toString());
            Long sumBandwidth = new Long(tuple.get("sumBandwidth").toString());
            longMap.put("name", name);
            longMap.put("sumPages", sumPages);
            longMap.put("sumHits", sumHits);
            longMap.put("sumBandwidth", sumBandwidth);
            resultList.add(longMap);
        }
        model.addAttribute("resultList", resultList);
        return resultList;
    }
}
