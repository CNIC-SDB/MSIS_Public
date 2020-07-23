package cn.csdb.msis.stats.repository;

import cn.csdb.msis.stats.entity.AwDay;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.Tuple;
import java.util.List;

/**
 * @Author jinbao
 * @Date 2019/7/19 13:46
 **/
@Repository
public interface AwDayRepository extends JpaRepository<AwDay, Integer>, JpaSpecificationExecutor<AwDay> {

	List<AwDay> findByYearAndMonth(Integer year, Integer month);


	@Query(value = "select sum(awday.pages) as totalPages,sum (awday.hits) as totalHits from AwDay awday group by awday.year order by totalPages")
	List<Tuple> countAwDayByDay();

	@Query(value = "select sum(awday.pages) as totalPages,sum (awday.hits) as totalHits from AwDay awday where awday.year=:year and awday.month=:month group by awday.year order by totalPages")
	List<Tuple> countAwDayByYear_Month(Integer year, Integer month);

	@Query(value = "select sum(awday.pages) as totalPages,sum (awday.hits) as totalHits from AwDay awday where awday.year in :years group by awday.year order by totalPages")
	List<Tuple> countAwDayInYear(List<Integer> years);

	@Query(value = "select sum(awday.pages) as totalPages,sum (awday.hits) as totalHits from AwDay awday where awday.year like :year group by awday.year order by totalPages")
	List<Tuple> countAwDayLikeYear(Integer year);

	@Query(value = "select awday from AwDay awday,Site sites,Institute institutes where sites.id = awday.site.id and institutes.id = sites.id and sites.id = :sid")
	List<AwDay> findAwDayUnionSiteUnionInstitutesBySiteId(Integer sid);

	@Query(value = "select awday.id,institutes from AwDay awday,Site sites,Institute institutes where sites.id = awday.site.id and institutes.id = sites.id and sites.id = :sid")
	List<Tuple> findAwDayIdAndInstitutes(Integer sid);

	@Query(value = "select coalesce(sum(awday.pages),0) as totalPage, coalesce(sum(awday.visits),0) as totalVisit, coalesce(sum(awday.bandwidth),0) as totalBandwidth, coalesce(sum (awday.hits),0) as totalHit from AwDay awday")
	List<Tuple> summaryData();

	@Query(value = "select sum(awday.visits) as totalVisit, sum(awday.bandwidth) as bandwidth, (awday.year*10000+awday.month*100+awday.day) as dayTime from AwDay awday group by (awday.year*10000+awday.month*100+awday.day) order by (awday.year*10000+awday.month*100+awday.day) asc ")
	List<Tuple> dataByDay();

	@Query(value = "select g.id as gid,g.cnName as gname ,sum(awday.bandwidth) as bandwidth from Group g left join GroupSite gs on g.id=gs.gid left join Site s on gs.sid=s.id left join AwDay awday on s.id=awday.site.id and awday.year=:year and awday.month=:month where g.isRank=:isRank and g.isShow=:isRank group by g.cnName")
	List<Tuple> top10BandwidthOfLastMonth(Integer isRank, Pageable pageable, Integer year, Integer month);

	@Query(value = "select g.id as gid, g.cnName as gname ,sum(awday.visits) as visits from Group g left join GroupSite gs on g.id=gs.gid left join Site s on gs.sid=s.id left join AwDay awday on s.id=awday.site.id and awday.year=:year and awday.month=:month where g.isRank=:isRank and g.isShow=:isRank group by g.cnName")
	List<Tuple> top10VisitsOfLastMonth(Integer isRank, Pageable pageable, Integer year, Integer month);

	@Query(value = "select sum(awday.visits) as totalVisit, " +
			"sum(awday.bandwidth) as bandwidth, (awday.year*10000+awday.month*100+awday.day) as dayTime " +
			"from AwDay awday " +
			"group by (awday.year*10000+awday.month*100+awday.day) " +
			"order by (awday.year*10000+awday.month*100+awday.day) asc ")
	List<Tuple> findDataVisitAll();

	@Query(value = "select sum(awday.visits) as totalVisit, " +
			"sum(awday.bandwidth) as bandwidth, " +
			"(awday.year*10000+awday.month*100+awday.day) as dayTime " +
			"from Group groups " +
			"LEFT JOIN  GroupSite groupSite ON  groups.id=groupSite.gid " +
			"LEFT JOIN  Site sites ON  groupSite.sid=sites.id " +
			"LEFT JOIN  AwDay awday ON  awday.site.id=sites.id " +
			"and groups.id= :groupID " +
			"group by (awday.year*10000+awday.month*100+awday.day) " +
			"order by (awday.year*10000+awday.month*100+awday.day) asc ")
	List<Tuple> findDataVisitById(Integer groupID);

	@Query(value = "select ad.year as year,ad.month as month,ad.day as day from AwDay ad order by year,month,day")
	List<Tuple> findStartTime();

	@Modifying
	@Transactional
	@Query(value = "delete from #{#entityName} as table where table.dataFile.id=:fid ")
	void deleteByFid(Integer fid);
}
