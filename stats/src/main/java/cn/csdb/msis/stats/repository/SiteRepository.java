package cn.csdb.msis.stats.repository;

import cn.csdb.msis.stats.entity.Site;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @Author jinbao
 * @Date 2019/7/19 13:58
 **/
@Repository
public interface SiteRepository extends JpaRepository<Site, Integer>, JpaSpecificationExecutor<Site> {

	Site findByEnName(String enName);

	@Override
	<S extends Site> S save(S entity);

	@Override
	void deleteById(Integer integer);

	@Query(value = "select s from Site as s where s.cnName like concat('%',:cnName,'%') or s.enName like concat('%',:cnName,'%')")
	Page<Site> findByCnNameLike(String cnName, Pageable pageable);

	@Query(value = "select s from Site as s " +
			" left join Group g on s.gid = g.id " +
			"where  (s.cnName like concat('%',:cnName,'%') or s.enName like concat('%',:cnName,'%')) and s.gid = :gid ")
	Page<Site> findByCnNameLikeWithCurrentGroup(Integer gid, String cnName, Pageable pageable);

	@Query(value = "select s from Site s where s.id in :ids")
	List<Site> findInId(Integer[] ids);

	Site findByUriOrCnName(String uri, String cnName);

//	@Query(value = "select s from Site s where s.id=:id and (s.uri=:uri or s.cnName=:cnName)")
//	Site findByUriOrCnNameWithId(Integer id, String uri, String cnName);
}
