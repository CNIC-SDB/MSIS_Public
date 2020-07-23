package cn.csdb.msis.stats.repository;

import cn.csdb.msis.stats.entity.Status;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @Author jinbao
 * @Date 2019/7/19 13:59
 **/
@Repository
public interface StatusRepository extends JpaRepository<Status, Integer>, JpaSpecificationExecutor<Status> {

    @Query(value = "select count(status) as normal" +
            " from Status status " +
            "where status.mid = :mid and status.status<=300 and status.mtime between :intYesterday and :intToday ")
    int normalCount(Integer mid, Date intYesterday, Date intToday);


    @Query(value = "select count(status) as sumC" +
            " from Status status " +
            "where status.mid = :mid and status.mtime between :intYesterday and :intToday ")
    int sumCount(Integer mid, Date intYesterday, Date intToday);

    @Query(nativeQuery = true, value = "SELECT\n" +
            "DATE_FORMAT( s.mtime, '%Y-%m' ) AS `month`,\n" +
            "round( count( s.id ) / 24, 2 ) AS days \n" +
            "FROM \n" +
            "`status` s \n" +
            "LEFT JOIN monitors m ON s.mid = m.id \n" +
            "LEFT JOIN sites site ON m.sid = site.id \n" +
            "LEFT JOIN group_site gs ON site.id = gs.sid \n" +
            "LEFT JOIN groups g ON gs.gid = g.id \n" +
            "WHERE \n" +
            "g.id = :gid \n" +
            "AND s.`status` != 200 \n" +
            "AND :startDate < s.mtime \n" +
            "AND s.mtime < :endDate \n" +
            "GROUP BY `month` \n" +
            "ORDER BY `month` desc")
    List<Map<String, String>> countOutageTime(Integer gid, Date startDate, Date endDate);
}
