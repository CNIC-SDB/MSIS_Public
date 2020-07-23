package cn.csdb.msis.stats.repository;

import cn.csdb.msis.stats.entity.GroupSite;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface GroupSiteRepository extends JpaRepository<GroupSite, Integer>, JpaSpecificationExecutor<GroupSite> {

	@Override
	void delete(GroupSite entity);

	@Override
	<S extends GroupSite> S save(S entity);

	@Query(value = "select gs from GroupSite gs where gs.sid in (:sid) and gs.gid = :gid")
	List<GroupSite> findInSidAndGid(Integer sid[], Integer gid);

	List<GroupSite> findByGid(Integer gid);
}
