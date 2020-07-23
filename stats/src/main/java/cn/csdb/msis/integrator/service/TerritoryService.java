package cn.csdb.msis.integrator.service;

import cn.csdb.msis.stats.repository.SubjectRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.persistence.Tuple;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * 领域统计Service
 *
 * @author CXY
 * @Date 2019/7/23 13:58
 */
@Service
public class TerritoryService {
    @Autowired
	SubjectRepository subjectsRepository;

    public List<Tuple> findByVisit(Integer startDate, Integer endDate) {
        List<Tuple> subjectsTuple = null;
        //进行时间的判断及转换
        Date now = new Date();
        SimpleDateFormat df = new SimpleDateFormat("yyyyMMdd");//设置日期格式
        String hehe = df.format(now);// now为获取当前系统时间
        if (startDate == null && endDate == null) {
            startDate = 20190100;
            endDate = Integer.parseInt(hehe);
        }
        subjectsTuple = subjectsRepository.findByVisit(startDate, endDate);
        return subjectsTuple;
    }

    public List<Tuple> findByDownloads(Integer startDate, Integer endDate) {
        //进行时间的判断及转换
        Date now = new Date();
        SimpleDateFormat df = new SimpleDateFormat("yyyyMMdd");//设置日期格式
        String hehe = df.format(now);// now为获取当前系统时间
        if (startDate == null && endDate == null) {
            startDate = 20190100;
            endDate = Integer.parseInt(hehe);
        }
        List<Tuple> subjectsTuple = subjectsRepository.findByDownloads(startDate, endDate);
        return subjectsTuple;
    }
}
