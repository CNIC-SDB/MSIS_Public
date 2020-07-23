package cn.csdb.msis.stats.repository;

import cn.csdb.msis.stats.entity.Institute;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @Author jinbao
 * @Date 2019/7/17 12:57
 **/
@Repository
public interface InstitutesRepository extends JpaRepository<Institute, Integer>, JpaSpecificationExecutor<Institute> {

	@Override
	@Modifying
	void deleteById(Integer integer);

	@Override
	@Modifying
	<S extends Institute> S save(S entity);

	@Query(value = "select i from Institute as i where i.cnName like concat('%',:cnName,'%') ")
	Page<Institute> findByCnNameLike(String cnName, Pageable pageable);

	@Override
	List<Institute> findAll();
}
