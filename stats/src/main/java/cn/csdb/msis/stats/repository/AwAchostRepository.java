package cn.csdb.msis.stats.repository;

import cn.csdb.msis.stats.entity.AwAchost;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.Tuple;
import java.util.List;

@Repository
public interface AwAchostRepository extends JpaRepository<AwAchost, String>, JpaSpecificationExecutor<AwAchost> {

	@Query(value = "select g.id as gid,g.cnName as gname,ah.hostType as hostType, " +
			"SUM(ah.pages) as page," +
			"SUM(ah.bandwidth) as bandwidth" +
			" from Group g " +
			"LEFT JOIN GroupSite gs ON g.id=gs.gid " +
			"LEFT JOIN Site s ON gs.sid=s.id " +
			"LEFT JOIN AwAchost ah ON s.id=ah.site.id where g.isRank=1 and g.isShow=1" +
			"AND  :beginTime <= (ah.year*10000+ah.month*100) " +
			"AND (ah.year*10000+ah.month*100) <=:endTime " +
			"GROUP BY g.id ,ah.hostType")
	List<Tuple> groupHostRank(Integer beginTime, Integer endTime);

    @Modifying
    @Transactional
    @Query(value = "delete from #{#entityName} as table where table.dataFile.id=:fid ")
    void deleteByFid(Integer fid);
}
