package cn.csdb.msis.stats.repository;

import cn.csdb.msis.stats.entity.Subject;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import javax.persistence.Tuple;
import java.util.List;

/**
 * @Author jinbao
 * @Date 2019/7/19 14:03
 **/
@Repository
public interface SubjectRepository extends JpaRepository<Subject, Integer>, JpaSpecificationExecutor<Subject> {

    @Override
    List<Subject> findAll();

    @Query(value = "select subject.name as name, " +
            "coalesce(SUM(awDay.visits),0) as sumPages, " +
            "coalesce(SUM(awDay.pages),0) as sumHits, " +
            "coalesce(SUM(awDay.bandwidth),0) as sumBandwidth " +
            "from Subject subject " +
            "left join SubjectSite subjectSite on subject.id = subjectSite.subjectId " +
            "left join Site site on site.id = subjectSite.siteId " +
            "left join AwDay awDay on (" +
            " site.id = awDay.site.id " +
            "AND (awDay.year*10000+awDay.month*100+awDay.day) BETWEEN  :startDate AND  :endDate )" +
            "group by subject.id " +
            "order by sumHits DESC")
    List<Tuple> findByVisit(Integer startDate, Integer endDate);

    @Query(value = "SELECT subjects.name as name ,coalesce(SUM(awday.visits),0) as sumPages,coalesce(SUM(awday.pages),0) as sumHits," +
            "coalesce(SUM(awday.bandwidth),0) as sumBandwidth from Subject subjects LEFT JOIN " +
            "SubjectSite subjectSite ON " +
            "subjects.id=subjectSite.subjectId  LEFT JOIN " +
            "Site sites ON " +
            "subjectSite.siteId=sites.id LEFT JOIN " +
            "AwDay awday ON (" +
            "sites.id=awday.site.id " +
            "AND (awday.year*10000+awday.month*100+awday.day) BETWEEN  :startDate AND  :endDate )" +
            "GROUP BY subjects.name " +
            "ORDER BY sumBandwidth DESC")
    List<Tuple> findByDownloads(Integer startDate, Integer endDate);
}
