package cn.csdb.msis.stats.repository;

import cn.csdb.msis.stats.entity.Group;
import cn.csdb.msis.stats.entity.Site;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import javax.persistence.Tuple;
import java.util.List;

/**
 * @Author jinbao
 * @Date 2019/7/19 13:55
 **/
@Repository
public interface GroupsRepository extends JpaRepository<Group, Integer>, JpaSpecificationExecutor<Group> {

	@Override
	@Modifying
	<S extends Group> S save(S entity);

	List<Group> findBySitesEquals(Site Site);

	List<Group> findByIsRankAndIsShow(Integer isRank, Integer isShow);

	Group findByEnName(String enName);

	@Query(value = "SELECT groups.id as id,groups.cnName as name ,coalesce(SUM(awgeneral.totalVisits),0) as sumTotalVisits,coalesce(SUM(awgeneral.totalUnique),0) as sumTotalUnique " +
			"from Group groups LEFT JOIN " +
			"GroupSite groupSite ON " +
			"groups.id=groupSite.gid LEFT JOIN " +
			"Site sites ON " +
			"groupSite.sid=sites.id LEFT JOIN " +
			"AwGeneral awgeneral ON " +
			"sites.id=awgeneral.site.id Where groups.isRank=1")
	List<Tuple> findVisitAll();

	@Query(value = "SELECT groups.id as id,groups.cnName as name ,coalesce(SUM(awgeneral.totalVisits),0) as sumTotalVisits,coalesce(SUM(awgeneral.totalUnique),0) as sumTotalUnique " +
			"from Group groups LEFT JOIN " +
			"GroupSite groupSite ON " +
			"groups.id=groupSite.gid LEFT JOIN " +
			"Site sites ON " +
			"groupSite.sid=sites.id LEFT JOIN " +
			"AwGeneral awgeneral ON " +
			"sites.id=awgeneral.site.id " +
			"AND  groups.id= :groupID ")
	List<Tuple> findVisitByGroupId(Integer groupID);

	@Query(value = "SELECT groups.id as id,groups.cnName as name ," +
			"coalesce(SUM(awtime.pages),0) as pages," +
			"coalesce(SUM(awtime.hits),0) as hits, " +
			"coalesce(SUM(awtime.bandwidth),0) as bandwidth, " +
			"coalesce(SUM(awtime.nonViewedPages),0) as robotPages, " +
			"coalesce(SUM(awtime.nonViewedHits),0) as robotHits, " +
			"coalesce(SUM(awtime.nonViewedBandwidth),0) as robotBandwidth " +
			"from AwTime awtime LEFT JOIN " +
			"Site sites ON " +
			"awtime.site.id = sites.id LEFT JOIN " +
			"GroupSite groupSite ON " +
			"sites.id = groupSite.sid LEFT JOIN " +
			"Group groups ON " +
			"groupSite.gid =groups.id" +
			" Where groups.isRank=1")
	List<Tuple> findPageAll();

	@Query(value = "SELECT groups.id as id,groups.cnName as name ," +
			"coalesce(SUM(awtime.pages),0) as pages," +
			"coalesce(SUM(awtime.hits),0) as hits, " +
			"coalesce(SUM(awtime.bandwidth),0) as bandwidth, " +
			"coalesce(SUM(awtime.nonViewedPages),0) as robotPages, " +
			"coalesce(SUM(awtime.nonViewedHits),0) as robotHits, " +
			"coalesce(SUM(awtime.nonViewedBandwidth),0) as robotBandwidth " +
			"from AwTime awtime LEFT JOIN " +
			"Site sites ON " +
			"awtime.site.id = sites.id LEFT JOIN " +
			"GroupSite groupSite ON " +
			"sites.id = groupSite.sid LEFT JOIN " +
			"Group groups ON " +
			"groupSite.gid =groups.id" +
			" Where groups.id= :groupID ")
	List<Tuple> findPageByGroupId(Integer groupID);

	@Query(value = "select groups.id as gid,groups.cnName as gname ," +
			"sum(awday.visits) as totalVisit, " +
			"sum(awday.bandwidth) as bandwidth, " +
			"(awday.year*10000+awday.month*100+awday.day) as dayTime " +
			"from Group groups " +
			"LEFT JOIN  GroupSite groupSite ON  groups.id=groupSite.gid " +
			"LEFT JOIN  Site sites ON  groupSite.sid=sites.id " +
			"LEFT JOIN  AwDay awday ON  awday.site.id=sites.id " +
			"where groups.id= :groupID " +
			"group by (awday.year*10000+awday.month*100+awday.day) " +
			"order by (awday.year*10000+awday.month*100+awday.day) asc ")
	List<Tuple> findDataVisitById(Integer groupID);

	@Query(value = "select sum(awday.visits) as totalVisit, " +
			"sum(awday.bandwidth) as bandwidth, (awday.year*10000+awday.month*100+awday.day) as dayTime " +
			"from Group groups " +
			" JOIN  GroupSite groupSite ON  groups.id=groupSite.gid " +
			" JOIN  Site sites ON  groupSite.sid=sites.id " +
			" JOIN  AwDay awday ON  awday.site.id=sites.id Where groups.isRank=1" +
			"group by (awday.year*10000+awday.month*100+awday.day) " +
			"order by (awday.year*10000+awday.month*100+awday.day) asc ")
	List<Tuple> findDataVisitAll();

	@Query(value =
			"SELECT " +
					"groups.id,percentages.id," +
					"groups.cnName,SUM(percentages.totalhours), " +
					"CASE " +
					"WHEN (SUM(percentages.totalhours)=0) THEN 0  ELSE  (SUM(percentages.validhours)/SUM(percentages.totalhours)*10000) END AS num," +
					"(percentages.year*10000+percentages.month*100+percentages.day) as dayTime" +
					" FROM " +
					" Group groups " +
					" JOIN GroupSite groupSite ON groups.id = groupSite.gid" +
					" JOIN Site sites ON groupSite.sid = sites.id" +
					" JOIN Monitor monitors ON sites.id = monitors.sid" +
					" JOIN Percentage percentages ON monitors.id = percentages.mid " +
					"Where groups.isRank=1 and " +
					"(percentages.year*10000+percentages.month*100) BETWEEN  :beginDate AND  :endDate " +
					"GROUP BY(percentages.year*10000+percentages.month*100+percentages.day)" +
					"order by (percentages.year*10000+percentages.month*100+percentages.day) asc ")
	List<Tuple> findRateAll(Integer beginDate, Integer endDate);

	@Query(value =
			"SELECT " +
					"groups.id,percentages.id," +
					"groups.cnName,SUM(percentages.totalhours), " +
					"CASE " +
					"WHEN (SUM(percentages.totalhours)=0) THEN 0  ELSE  (SUM(percentages.validhours)/SUM(percentages.totalhours)*10000) END AS num," +
					"(percentages.year*10000+percentages.month*100+percentages.day) as dayTime" +
					" FROM " +
					" Group groups " +
					" JOIN GroupSite groupSite ON groups.id = groupSite.gid" +
					" JOIN Site sites ON groupSite.sid = sites.id" +
					" JOIN Monitor monitors ON sites.id = monitors.sid" +
					" JOIN Percentage percentages ON monitors.id = percentages.mid " +
					"WHERE" +
					"(percentages.year*10000+percentages.month*100) BETWEEN  :beginDate AND  :endDate " +
					"AND groups.id=:groupID " +
					"GROUP BY(percentages.year*10000+percentages.month*100+percentages.day)" +
					"order by (percentages.year*10000+percentages.month*100+percentages.day) asc ")
	List<Tuple> findRateByID(Integer beginDate, Integer endDate, Integer groupID);

	@Query(value = "SELECT groups.id as id,groups.cnName as name ,coalesce(SUM(awgeneral.totalVisits),0) as sumTotalVisits,coalesce(SUM(awgeneral.totalUnique),0) as sumTotalUnique " +
			"from Group groups LEFT JOIN " +
			"GroupSite groupSite ON " +
			"groups.id=groupSite.gid LEFT JOIN " +
			"Site sites ON " +
			"groupSite.sid=sites.id LEFT JOIN " +
			"AwGeneral awgeneral ON " +
			"sites.id=awgeneral.site.id Where groups.isRank=1 AND (awgeneral.year*10000+awgeneral.month*100+1) BETWEEN  :startDate AND  :endDate")
	List<Tuple> findVisitAll(Integer startDate, Integer endDate);


	@Query(value = "select g.id as gid, g.cnName as gname,sum(ad.visits) as visit, sum(ad.pages) as page, sum(ad.bandwidth) as bandwidth " +
			"from Group g " +
			"LEFT JOIN GroupSite gs ON g.id=gs.gid " +
			"LEFT JOIN Site s ON gs.sid=s.id " +
			"LEFT JOIN AwDay ad ON s.id=ad.site.id " +
			"where g.isRank=1 and g.isShow=1" +
			"AND :beginTime <= (ad.year*10000+ad.month*100+ad.day) " +
			"AND (ad.year*10000+ad.month*100+ad.day) <= :endTime " +
			"GROUP BY g.id")
	List<Tuple> groupsRank(Integer beginTime, Integer endTime, Sort sort);

	@Query(value = "SELECT groups.id as id,groups.cnName as name ," +
			"coalesce(SUM(awtime.pages),0) as pages," +
			"coalesce(SUM(awtime.hits),0) as hits, " +
			"coalesce(SUM(awtime.bandwidth),0) as bandwidth, " +
			"coalesce(SUM(awtime.nonViewedPages),0) as robotPages, " +
			"coalesce(SUM(awtime.nonViewedHits),0) as robotHits, " +
			"coalesce(SUM(awtime.nonViewedBandwidth),0) as robotBandwidth " +
			"from Group groups LEFT JOIN " +
			"GroupSite groupSite ON " +
			"groups.id=groupSite.gid LEFT JOIN " +
			"Site sites ON " +
			"groupSite.sid=sites.id LEFT JOIN " +
			"AwTime awtime ON " +
			"sites.id=awtime.site.id Where groups.isRank=1 AND (awtime.year*10000+awtime.month*100+1) BETWEEN  :startDate AND  :endDate")
	List<Tuple> findPageAll(Integer startDate, Integer endDate);

	@Query(value = "SELECT groups.id as id,groups.cnName as name ,coalesce(SUM(awgeneral.totalVisits),0) as sumTotalVisits,coalesce(SUM(awgeneral.totalUnique),0) as sumTotalUnique " +
			"from Group groups LEFT JOIN " +
			"GroupSite groupSite ON " +
			"groups.id=groupSite.gid LEFT JOIN " +
			"Site sites ON " +
			"groupSite.sid=sites.id LEFT JOIN " +
			"AwGeneral awgeneral ON " +
			"sites.id=awgeneral.site.id " +
			"AND  groups.id= :groupID " +
			"Where (awgeneral.year*10000+awgeneral.month*100+1) BETWEEN  :startDate AND  :endDate")
	List<Tuple> findVisitByGroupId(Integer groupID, Integer startDate, Integer endDate);

	@Query(value = "SELECT groups.id as id,groups.cnName as name ," +
			"coalesce(SUM(awtime.pages),0) as pages," +
			"coalesce(SUM(awtime.hits),0) as hits, " +
			"coalesce(SUM(awtime.bandwidth),0) as bandwidth, " +
			"coalesce(SUM(awtime.nonViewedPages),0) as robotPages, " +
			"coalesce(SUM(awtime.nonViewedHits),0) as robotHits, " +
			"coalesce(SUM(awtime.nonViewedBandwidth),0) as robotBandwidth " +
			"from Group groups LEFT JOIN " +
			"GroupSite groupSite ON " +
			"groups.id=groupSite.gid LEFT JOIN " +
			"Site sites ON " +
			"groupSite.sid=sites.id LEFT JOIN " +
			"AwTime awtime ON " +
			"sites.id=awtime.site.id " +
			"AND  groups.id= :groupID " +
			"Where (awtime.year*10000+awtime.month*100+1) BETWEEN  :startDate AND  :endDate")
	List<Tuple> findPageByGroupId(Integer groupID, Integer startDate, Integer endDate);

	@Query(value = "SELECT groups.id as id,groups.cnName as name ,coalesce(SUM(awgeneral.totalVisits),0) as sumTotalVisits,coalesce(SUM(awgeneral.totalUnique),0) as sumTotalUnique ," +
			"(awgeneral.year*10000+awgeneral.month*100) as dayTime " +
			"from AwGeneral awgeneral LEFT JOIN " +
			" Site sites  ON " +
			" sites.id=awgeneral.site.id LEFT JOIN " +
			" GroupSite groupSite ON " +
			"sites.id=groupSite.sid LEFT JOIN " +
			" Group groups ON groups.id=groupSite.gid " +
			" Where groups.isRank=1 AND (awgeneral.year*10000+awgeneral.month*100+1) BETWEEN  :startDate AND  :endDate " +
			"group by (awgeneral.year*10000+awgeneral.month*100)")
	List<Tuple> findVisitAllByMonth(Integer startDate, Integer endDate);

	@Query(value = "SELECT groups.id as id,groups.cnName as name ," +
			"coalesce(SUM(awday.pages),0) as sumPages," +
			"coalesce(SUM(awday.hits),0) as sumHits ," +
			"coalesce(SUM(awday.bandwidth),0) as sumBandwidth," +
			"coalesce(SUM(awday.visits),0) as sumVisits," +
			"(awday.year*10000+awday.month*100+awday.day) as dayTime " +
			"from Group groups LEFT JOIN " +
			"GroupSite groupSite ON " +
			"groups.id=groupSite.gid LEFT JOIN " +
			"Site sites ON " +
			"groupSite.sid=sites.id LEFT JOIN " +
			"AwDay awday ON " +
			"sites.id=awday.site.id Where groups.isRank=1 AND (awday.year*10000+awday.month*100+1) BETWEEN  :startDate AND  :endDate " +
			"group by (awday.year*10000+awday.month*100+awday.day)")
	List<Tuple> findDataByDay(Integer startDate, Integer endDate);

	@Query(value = "SELECT groups.id as id,groups.cnName as name ," +
			"coalesce(SUM(awtime.pages),0) as sumPages," +
			"coalesce(SUM(awtime.hits),0) as sumHits, " +
			"coalesce(SUM(awtime.bandwidth),0) as sumBandwidth, " +
			"(awtime.year*10000+awtime.month*100) as dayTime," +
			"coalesce(SUM(awtime.nonViewedPages),0) as robotPages, " +
			"coalesce(SUM(awtime.nonViewedHits),0) as robotHits, " +
			"coalesce(SUM(awtime.nonViewedBandwidth),0) as robotBandwidth " +
			"from Group groups LEFT JOIN " +
			"GroupSite groupSite ON " +
			"groups.id=groupSite.gid LEFT JOIN " +
			"Site sites ON " +
			"groupSite.sid=sites.id LEFT JOIN " +
			"AwTime awtime ON " +
			"sites.id=awtime.site.id Where groups.isRank=1 AND (awtime.year*10000+awtime.month*100+1) BETWEEN  :startDate AND  :endDate " +
			"group by (awtime.year*10000+awtime.month*100+1)")
	List<Tuple> findPageAllByMonth(Integer startDate, Integer endDate);


	@Query(value = "SELECT groups.id as id,groups.cnName as name ,coalesce(SUM(awgeneral.totalVisits),0) as sumTotalVisits,coalesce(SUM(awgeneral.totalUnique),0) as sumTotalUnique ," +
			"(awgeneral.year*10000+awgeneral.month*100) as dayTime " +
			"from Group groups LEFT JOIN " +
			"GroupSite groupSite ON " +
			"groups.id=groupSite.gid LEFT JOIN " +
			"Site sites ON " +
			"groupSite.sid=sites.id LEFT JOIN " +
			"AwGeneral awgeneral ON " +
			"sites.id=awgeneral.site.id Where  groups.id= :groupID  AND (awgeneral.year*10000+awgeneral.month*100+1) BETWEEN  :startDate AND  :endDate " +
			"group by (awgeneral.year*10000+awgeneral.month*100)")
	List<Tuple> findVisitAllByMonthID(Integer groupID, Integer startDate, Integer endDate);

	@Query(value = "SELECT groups.id as id,groups.cnName as name ," +
			"coalesce(SUM(awtime.pages),0) as sumPages," +
			"coalesce(SUM(awtime.hits),0) as sumHits, " +
			"coalesce(SUM(awtime.bandwidth),0) as sumBandwidth, " +
			"(awtime.year*10000+awtime.month*100) as dayTime," +
			"coalesce(SUM(awtime.nonViewedPages),0) as robotPages, " +
			"coalesce(SUM(awtime.nonViewedHits),0) as robotHits, " +
			"coalesce(SUM(awtime.nonViewedBandwidth),0) as robotBandwidth " +
			"from Group groups LEFT JOIN " +
			"GroupSite groupSite ON " +
			"groups.id=groupSite.gid LEFT JOIN " +
			"Site sites ON " +
			"groupSite.sid=sites.id LEFT JOIN " +
			"AwTime awtime ON " +
			"sites.id=awtime.site.id Where groups.id= :groupID  AND (awtime.year*10000+awtime.month*100+1) BETWEEN  :startDate AND  :endDate " +
			"group by (awtime.year*10000+awtime.month*100+1)")
	List<Tuple> findPageAllByMonthID(Integer groupID, Integer startDate, Integer endDate);

	@Query(value = "SELECT groups.id as id,groups.cnName as name ," +
			"coalesce(SUM(awday.pages),0) as sumPages," +
			"coalesce(SUM(awday.hits),0) as sumHits ," +
			"coalesce(SUM(awday.bandwidth),0) as sumBandwidth," +
			"coalesce(SUM(awday.visits),0) as sumVisits," +
			"(awday.year*10000+awday.month*100+awday.day) as dayTime " +
			"from Group groups LEFT JOIN " +
			"GroupSite groupSite ON " +
			"groups.id=groupSite.gid LEFT JOIN " +
			"Site sites ON " +
			"groupSite.sid=sites.id LEFT JOIN " +
			"AwDay awday ON " +
			"sites.id=awday.site.id Where groups.id= :groupID AND (awday.year*10000+awday.month*100+1) BETWEEN  :startDate AND  :endDate " +
			"group by (awday.year*10000+awday.month*100+awday.day)")
	List<Tuple> findDataByDayId(Integer groupID, Integer startDate, Integer endDate);

	@Query(value = "SELECT groups.id as id,groups.cnName as name ," +
			"awFileType.fileType as fileType, " +
			"coalesce(SUM(awFileType.hits),0) as sumHits, " +
			"coalesce(SUM(awFileType.bandwidth),0) as sumBandWidth " +
			"from Group groups LEFT JOIN " +
			"GroupSite groupSite ON " +
			"groups.id=groupSite.gid LEFT JOIN " +
			"Site sites ON " +
			"groupSite.sid=sites.id LEFT JOIN " +
			"AwFileType awFileType ON " +
			"sites.id=awFileType.site.id Where groups.isRank=1 AND (awFileType.year*10000+awFileType.month*100+1) BETWEEN  :startDate AND  :endDate " +
			"group by awFileType.fileType " +
			"order by sumHits desc ")
	List<Tuple> getFileTypeHAll(Integer startDate, Integer endDate);

	@Query(value = "SELECT groups.id as id,groups.cnName as name ," +
			"awFileType.fileType as fileType, " +
			"coalesce(SUM(awFileType.hits),0) as sumHits, " +
			"coalesce(SUM(awFileType.bandwidth),0) as sumBandWidth " +
			"from Group groups LEFT JOIN " +
			"GroupSite groupSite ON " +
			"groups.id=groupSite.gid LEFT JOIN " +
			"Site sites ON " +
			"groupSite.sid=sites.id LEFT JOIN " +
			"AwFileType awFileType ON " +
			"sites.id=awFileType.site.id Where groups.isRank=1 AND (awFileType.year*10000+awFileType.month*100+1) BETWEEN  :startDate AND  :endDate " +
			"group by awFileType.fileType " +
			"order by sumBandWidth desc")
	List<Tuple> getFileTypeDAll(Integer startDate, Integer endDate);

	@Query(value = "SELECT groups.id as id,groups.cnName as name ," +
			"awFileType.fileType as fileType, " +
			"coalesce(SUM(awFileType.hits),0) as sumHits, " +
			"coalesce(SUM(awFileType.bandwidth),0) as sumBandWidth " +
			"from Group groups LEFT JOIN " +
			"GroupSite groupSite ON " +
			"groups.id=groupSite.gid LEFT JOIN " +
			"Site sites ON " +
			"groupSite.sid=sites.id LEFT JOIN " +
			"AwFileType awFileType ON " +
			"sites.id=awFileType.site.id Where groups.id= :groupID AND (awFileType.year*10000+awFileType.month*100+1) BETWEEN  :startDate AND  :endDate " +
			"group by awFileType.fileType " +
			"order by sumHits desc ")
	List<Tuple> getFileTypeHById(Integer groupID, Integer startDate, Integer endDate);

	@Query(value = "SELECT groups.id as id,groups.cnName as name ," +
			"awFileType.fileType as fileType, " +
			"coalesce(SUM(awFileType.hits),0) as sumHits, " +
			"coalesce(SUM(awFileType.bandwidth),0) as sumBandWidth " +
			"from Group groups LEFT JOIN " +
			"GroupSite groupSite ON " +
			"groups.id=groupSite.gid LEFT JOIN " +
			"Site sites ON " +
			"groupSite.sid=sites.id LEFT JOIN " +
			"AwFileType awFileType ON " +
			"sites.id=awFileType.site.id Where  groups.id= :groupID AND (awFileType.year*10000+awFileType.month*100+1) BETWEEN  :startDate AND  :endDate " +
			"group by awFileType.fileType " +
			"order by sumBandWidth desc")
	List<Tuple> getFileTypeDByid(Integer groupID, Integer startDate, Integer endDate);

//	@Query(value = "SELECT groups.id as id,groups.cnName as name ,GROUP_CONCAT(statusDaily.hour) as hour,GROUP_CONCAT(statusDaily.status) as hour " +
//			"from Group groups LEFT JOIN " +
//			"GroupSite groupSite ON " +
//			"groups.id=groupSite.gid LEFT JOIN " +
//			"Site sites ON " +
//			"groupSite.sid=sites.id LEFT JOIN " +
//			"Monitor monitors ON " +
//			"sites.id=monitors.sid LEFT JOIN " +
//			"StatusDaily statusDaily ON " +
//			"monitors.id=statusDaily.mid " +
//			"Where  groups.id= :groupID AND (statusDaily.year*10000+statusDaily.month*100+statusDaily.day) BETWEEN  :startDate AND  :endDate " +
//			"group by statusDaily.mid ")

	@Query(value = "SELECT sites.id as id,groups.id as gid,groups.cn_name as gname,sites.cn_name as name ,GROUP_CONCAT(statusDaily.hour) as hours,GROUP_CONCAT(statusDaily.status) as status " +
			"from groups groups LEFT JOIN " +
			"group_site groupSite ON " +
			"groups.id=groupSite.gid LEFT JOIN " +
			"sites sites ON " +
			"groupSite.sid=sites.id LEFT JOIN " +
			"monitors monitors ON " +
			"sites.id=monitors.sid LEFT JOIN " +
			"status_daily statusDaily ON " +
			"monitors.id=statusDaily.mid " +
			"Where  groups.id= :groupID AND (statusDaily.year*10000+statusDaily.month*100+statusDaily.day) BETWEEN  :startDate AND  :endDate " +
			"group by statusDaily.mid ", nativeQuery = true)
	List<Tuple> findSiteState(Integer groupID, Integer startDate, Integer endDate);

	@Query(value = "SELECT sites.id as id,sites.cn_name as name ,(statusDaily.year*10000+statusDaily.month*100+statusDaily.day) as dayTime,GROUP_CONCAT(statusDaily.hour) as hours,GROUP_CONCAT(statusDaily.status) as status " +
			"from  " +
			"sites sites  " +
			"LEFT JOIN " +
			"monitors monitors ON " +
			"sites.id=monitors.sid LEFT JOIN " +
			"status_daily statusDaily ON " +
			"monitors.id=statusDaily.mid " +
			"Where  monitors.sid= :siteId AND (statusDaily.year*10000+statusDaily.month*100+statusDaily.day) BETWEEN  :startDate AND  :endDate " +
			"group by (statusDaily.year*10000+statusDaily.month*100+statusDaily.day) ", nativeQuery = true)
	List<Tuple> findSiteStateBySite(Integer siteId, Integer startDate, Integer endDate);

	@Query(value =
			"SELECT " +
					"groups.id,percentages.id," +
					"groups.cnName as cnName,SUM(percentages.totalhours), " +
					"(percentages.year*10000+percentages.month*100+percentages.day) as dayTime, " +
					"CASE " +
					"WHEN (SUM(percentages.totalhours)=0) THEN 0  ELSE  (SUM(percentages.validhours)/SUM(percentages.totalhours)*10000) END AS num," +
					"(percentages.year*10000+percentages.month*100+percentages.day) as dayTime" +
					" FROM " +
					" Group groups " +
					" JOIN GroupSite groupSite ON groups.id = groupSite.gid" +
					" JOIN Site sites ON groupSite.sid = sites.id" +
					" JOIN Monitor monitors ON sites.id = monitors.sid" +
					" JOIN Percentage percentages ON monitors.id = percentages.mid " +
					"WHERE" +
					"(percentages.year*10000+percentages.month*100+percentages.day) BETWEEN  :beginDate AND  :endDate " +
					"AND groups.id=:groupID " +
					"GROUP BY(percentages.year*10000+percentages.month*100+percentages.day)" +
					"order by (percentages.year*10000+percentages.month*100+percentages.day) asc ")
	List<Tuple> findRateByGroup(Integer beginDate, Integer endDate, Integer groupID);

	@Query(value = "select groups.cnName as cnName, groups.id as id, groups.isShow as isShow, groups.gtype as gtype from Group groups WHERE (groups.pid =:groupID and groups.isRank = 0)or groups.id=:groupID")
	List<Tuple> findByManage(Integer groupID);

	@Query(value = "select g.cnName as cnName, g.id as id, g.isShow as isShow, g.gtype as gtype from Group g where (g.iid=:iid and g.gtype <> 50) or g.id =:iid ")
	List<Tuple> findByIidAndGAndGtypeNot(Integer iid);

	@Query(value = "select groups.cnName as cnName ,groups.id as id ," +
			"awDomain.domain as domain,coalesce(SUM(awDomain.pages),0) as pages,coalesce(SUM(awDomain.hits),0) as hits,coalesce(SUM(awDomain.ips),0) as ips,coalesce(SUM(awDomain.bandwidth),0)as downloads," +
			"(awDomain.year*10000+awDomain.month*100) as dayTime from Group groups " +
			" JOIN GroupSite groupSite ON groups.id = groupSite.gid " +
			"JOIN Site sites ON groupSite.sid = sites.id" +
			" JOIN AwDomain awDomain ON sites.id = awDomain.site.id " +
			"WHERE " +
			"groups.id =:groupID " +
			"AND" +
			"(awDomain.year*10000+awDomain.month*100+1) BETWEEN  :startDate AND  :endDate " +
			"group by awDomain.domain " +
			"order by sum(awDomain.ips) DESC ")
	List<Tuple> getcountryCountByIP(Integer groupID, Integer startDate, Integer endDate);

	@Query(value = "select groups.cnName as cnName ,groups.id as id ," +
			"awDomain.domain as domain,coalesce(SUM(awDomain.pages),0) as pages,coalesce(SUM(awDomain.hits),0) as hits,coalesce(SUM(awDomain.ips),0) as ips,coalesce(SUM(awDomain.bandwidth),0)as downloads," +
			"(awDomain.year*10000+awDomain.month*100) as dayTime from Group groups " +
			" JOIN GroupSite groupSite ON groups.id = groupSite.gid " +
			"JOIN Site sites ON groupSite.sid = sites.id" +
			" JOIN AwDomain awDomain ON sites.id = awDomain.site.id " +
			"WHERE " +
			"groups.id =:groupID " +
			"AND" +
			"(awDomain.year*10000+awDomain.month*100+1) BETWEEN  :startDate AND  :endDate " +
			"group by awDomain.domain " +
			"order by sum(awDomain.pages) DESC ")
	List<Tuple> getcountryCountByPage(Integer groupID, Integer startDate, Integer endDate);

	@Query(value = "select groups.cnName as cnName ,groups.id as id ," +
			"awDomain.domain as domain,coalesce(SUM(awDomain.pages),0) as pages,coalesce(SUM(awDomain.hits),0) as hits,coalesce(SUM(awDomain.ips),0) as ips,coalesce(SUM(awDomain.bandwidth),0)as downloads," +
			"(awDomain.year*10000+awDomain.month*100) as dayTime from Group groups " +
			" JOIN GroupSite groupSite ON groups.id = groupSite.gid " +
			"JOIN Site sites ON groupSite.sid = sites.id" +
			" JOIN AwDomain awDomain ON sites.id = awDomain.site.id " +
			"WHERE " +
			"groups.id =:groupID " +
			"AND" +
			"(awDomain.year*10000+awDomain.month*100+1) BETWEEN  :startDate AND  :endDate " +
			"group by awDomain.domain " +
			"order by sum(awDomain.hits) DESC ")
	List<Tuple> getcountryCountByHits(Integer groupID, Integer startDate, Integer endDate);

	@Query(value = "select groups.cnName as cnName ,groups.id as id ," +
			"awDomain.domain as domain,coalesce(SUM(awDomain.pages),0) as pages,coalesce(SUM(awDomain.hits),0) as hits,coalesce(SUM(awDomain.ips),0) as ips,coalesce(SUM(awDomain.bandwidth),0)as downloads," +
			"(awDomain.year*10000+awDomain.month*100) as dayTime from Group groups " +
			" JOIN GroupSite groupSite ON groups.id = groupSite.gid " +
			"JOIN Site sites ON groupSite.sid = sites.id" +
			" JOIN AwDomain awDomain ON sites.id = awDomain.site.id " +
			"WHERE " +
			"groups.id =:groupID " +
			"AND" +
			"(awDomain.year*10000+awDomain.month*100+1) BETWEEN  :startDate AND  :endDate " +
			"group by awDomain.domain " +
			"order by sum(awDomain.bandwidth) DESC ")
	List<Tuple> getcountryCountByDownloads(Integer groupID, Integer startDate, Integer endDate);

	@Query(value = "select groups.cnName as cnName ,groups.id as id ," +
			"awArea.area as domain,coalesce(SUM(awArea.pages),0) as pages,coalesce(SUM(awArea.hits),0) as hits,coalesce(SUM(awArea.ips),0) as ips,coalesce(SUM(awArea.bandwidth),0)as downloads," +
			"(awArea.year*10000+awArea.month*100) as dayTime from Group groups " +
			" JOIN GroupSite groupSite ON groups.id = groupSite.gid " +
			"JOIN Site sites ON groupSite.sid = sites.id" +
			" JOIN AwArea awArea ON sites.id = awArea.site.id " +
			"WHERE " +
			"groups.id =:groupID " +
			"AND" +
			"(awArea.year*10000+awArea.month*100+1) BETWEEN  :startDate AND  :endDate " +
			"group by awArea.area " +
			"order by sum(awArea.ips) DESC ")
	List<Tuple> getinternalCountByIP(Integer groupID, Integer startDate, Integer endDate);


	@Query(value = "select groups.cnName as cnName ,groups.id as id ," +
			"awArea.area as domain,coalesce(SUM(awArea.pages),0) as pages,coalesce(SUM(awArea.hits),0) as hits,coalesce(SUM(awArea.ips),0) as ips,coalesce(SUM(awArea.bandwidth),0)as downloads," +
			"(awArea.year*10000+awArea.month*100) as dayTime from Group groups " +
			" JOIN GroupSite groupSite ON groups.id = groupSite.gid " +
			"JOIN Site sites ON groupSite.sid = sites.id" +
			" JOIN AwArea awArea ON sites.id = awArea.site.id " +
			"WHERE " +
			"groups.id =:groupID " +
			"AND" +
			"(awArea.year*10000+awArea.month*100+1) BETWEEN  :startDate AND  :endDate " +
			"group by awArea.area " +
			"order by sum(awArea.pages) DESC ")
	List<Tuple> getinternalCountByPage(Integer groupID, Integer startDate, Integer endDate);


	@Query(value = "select groups.cnName as cnName ,groups.id as id ," +
			"awArea.area as domain,coalesce(SUM(awArea.pages),0) as pages,coalesce(SUM(awArea.hits),0) as hits,coalesce(SUM(awArea.ips),0) as ips,coalesce(SUM(awArea.bandwidth),0)as downloads," +
			"(awArea.year*10000+awArea.month*100) as dayTime from Group groups " +
			" JOIN GroupSite groupSite ON groups.id = groupSite.gid " +
			"JOIN Site sites ON groupSite.sid = sites.id" +
			" JOIN AwArea awArea ON sites.id = awArea.site.id " +
			"WHERE " +
			"groups.id =:groupID " +
			"AND" +
			"(awArea.year*10000+awArea.month*100+1) BETWEEN  :startDate AND  :endDate " +
			"group by awArea.area " +
			"order by sum(awArea.hits) DESC ")
	List<Tuple> getinternalCountByHits(Integer groupID, Integer startDate, Integer endDate);


	@Query(value = "select groups.cnName as cnName ,groups.id as id ," +
			"awArea.area as domain,coalesce(SUM(awArea.pages),0) as pages,coalesce(SUM(awArea.hits),0) as hits,coalesce(SUM(awArea.ips),0) as ips,coalesce(SUM(awArea.bandwidth),0)as downloads," +
			"(awArea.year*10000+awArea.month*100) as dayTime from Group groups " +
			" JOIN GroupSite groupSite ON groups.id = groupSite.gid " +
			"JOIN Site sites ON groupSite.sid = sites.id" +
			" JOIN AwArea awArea ON sites.id = awArea.site.id " +
			"WHERE " +
			"groups.id =:groupID " +
			"AND" +
			"(awArea.year*10000+awArea.month*100+1) BETWEEN  :startDate AND  :endDate " +
			"group by awArea.area " +
			"order by sum(awArea.bandwidth) DESC ")
	List<Tuple> getinternalCountByDownloads(Integer groupID, Integer startDate, Integer endDate);


	@Query(value = "select groups.cnName as cnName ,groups.id as id ," +
			"awAchost.hostType as domain,coalesce(SUM(awAchost.pages),0) as pages,coalesce(SUM(awAchost.hits),0) as hits,coalesce(SUM(awAchost.ips),0) as ips,coalesce(SUM(awAchost.bandwidth),0)as downloads," +
			"(awAchost.year*10000+awAchost.month*100) as dayTime from Group groups " +
			" JOIN GroupSite groupSite ON groups.id = groupSite.gid " +
			"JOIN Site sites ON groupSite.sid = sites.id" +
			" JOIN AwAchost awAchost ON sites.id = awAchost.site.id " +
			"WHERE " +
			"groups.id =:groupID " +
			"AND" +
			"(awAchost.year*10000+awAchost.month*100+1) BETWEEN  :startDate AND  :endDate " +
			"group by awAchost.hostType " +
			"order by sum(awAchost.ips) DESC ")
	List<Tuple> getyardCountByIP(Integer groupID, Integer startDate, Integer endDate);

	@Query(value = "select groups.cnName as cnName ,groups.id as id ," +
			"awAchost.hostType as domain,coalesce(SUM(awAchost.pages),0) as pages,coalesce(SUM(awAchost.hits),0) as hits,coalesce(SUM(awAchost.ips),0) as ips,coalesce(SUM(awAchost.bandwidth),0)as downloads," +
			"(awAchost.year*10000+awAchost.month*100) as dayTime from Group groups " +
			" JOIN GroupSite groupSite ON groups.id = groupSite.gid " +
			"JOIN Site sites ON groupSite.sid = sites.id" +
			" JOIN AwAchost awAchost ON sites.id = awAchost.site.id " +
			"WHERE " +
			"groups.id =:groupID " +
			"AND" +
			"(awAchost.year*10000+awAchost.month*100+1) BETWEEN  :startDate AND  :endDate " +
			"group by awAchost.hostType " +
			"order by sum(awAchost.pages) DESC ")
	List<Tuple> getyardCountByPage(Integer groupID, Integer startDate, Integer endDate);

	@Query(value = "select groups.cnName as cnName ,groups.id as id ," +
			"awAchost.hostType as domain,coalesce(SUM(awAchost.pages),0) as pages,coalesce(SUM(awAchost.hits),0) as hits,coalesce(SUM(awAchost.ips),0) as ips,coalesce(SUM(awAchost.bandwidth),0)as downloads," +
			"(awAchost.year*10000+awAchost.month*100) as dayTime from Group groups " +
			" JOIN GroupSite groupSite ON groups.id = groupSite.gid " +
			"JOIN Site sites ON groupSite.sid = sites.id" +
			" JOIN AwAchost awAchost ON sites.id = awAchost.site.id " +
			"WHERE " +
			"groups.id =:groupID " +
			"AND" +
			"(awAchost.year*10000+awAchost.month*100+1) BETWEEN  :startDate AND  :endDate " +
			"group by awAchost.hostType " +
			"order by sum(awAchost.hits) DESC ")
	List<Tuple> getyardCountByHits(Integer groupID, Integer startDate, Integer endDate);

	@Query(value = "select groups.cnName as cnName ,groups.id as id ," +
			"awAchost.hostType as domain,coalesce(SUM(awAchost.pages),0) as pages,coalesce(SUM(awAchost.hits),0) as hits,coalesce(SUM(awAchost.ips),0) as ips,coalesce(SUM(awAchost.bandwidth),0)as downloads," +
			"(awAchost.year*10000+awAchost.month*100) as dayTime from Group groups " +
			" JOIN GroupSite groupSite ON groups.id = groupSite.gid " +
			"JOIN Site sites ON groupSite.sid = sites.id" +
			" JOIN AwAchost awAchost ON sites.id = awAchost.site.id " +
			"WHERE " +
			"groups.id =:groupID " +
			"AND" +
			"(awAchost.year*10000+awAchost.month*100+1) BETWEEN  :startDate AND  :endDate " +
			"group by awAchost.hostType " +
			"order by sum(awAchost.bandwidth) DESC ")
	List<Tuple> getyardCountByDownloads(Integer groupID, Integer startDate, Integer endDate);

	Page<Group> findByIidAndGtypeNotAndCnNameLike(Integer pid, Integer gtype, String cnName, Pageable pageable);

	List<Group> findByPidAndCnName(Integer pid, String cnName);
}
