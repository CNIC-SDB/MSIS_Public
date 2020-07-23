package cn.csdb.msis.stats.repository;

import cn.csdb.msis.stats.entity.Percentage;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import javax.persistence.Tuple;
import java.util.List;

/**
 * @Author jinbao
 * @Date 2019/7/19 13:57
 **/
@Repository
public interface PercentagesRepository extends JpaRepository<Percentage, Integer>, JpaSpecificationExecutor<Percentage> {

	/**
	 * 服务正常率统计
	 *
	 * @param year
	 * @return
	 */
	@Query(value = "select g.id as gid,g.cnName as gname,SUM(p.validhours)/SUM(p.totalhours)*100 AS rate from Group g LEFT JOIN GroupSite gs ON g.id=gs.gid LEFT JOIN Site s ON gs.sid=s.id LEFT JOIN Monitor m ON s.id=m.sid LEFT JOIN Percentage p ON m.id=p.mid where p.year=:year AND g.isRank=1 AND g.isShow=1 group BY g.id")
	List<Tuple> serviceRateOfYear(Integer year);

	/**
	 * 某月服务正常率排名
	 *
	 * @param year
	 * @param month
	 * @param pageable
	 * @return
	 */
	@Query(value = "select g.id as gid,g.cnName as gname,COALESCE(SUM(p.validhours)/SUM(p.totalhours)*10000,0) AS rate from Group g LEFT JOIN GroupSite gs ON g.id=gs.gid LEFT JOIN Site s ON gs.sid=s.id LEFT JOIN Monitor m ON s.id=m.sid LEFT JOIN Percentage p ON m.id=p.mid where p.year=:year AND p.month=:month AND g.isRank=1 and g.isShow=1 GROUP BY g.id ORDER BY rate desc ")
	List<Tuple> serviceRateOfMonth(Integer year, Integer month, Pageable pageable);
}
