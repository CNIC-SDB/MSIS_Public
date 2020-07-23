package cn.csdb.msis.stats.repository;

import cn.csdb.msis.stats.entity.AwDomain;
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
 * @Date 2019/7/19 14:02
 **/
@Repository
public interface AwDomainRepository extends JpaRepository<AwDomain, Integer>, JpaSpecificationExecutor<AwDomain> {

	@Modifying
	@Transactional
	@Query(value = "delete from #{#entityName} as table where table.dataFile.id=:fid ")
	void deleteByFid(Integer fid);

	@Query(value = "select awdomain.domain as domain, sum(awdomain.ips) as ips from AwDomain awdomain group by awdomain.domain order by ips desc")
	List<Tuple> countAll();
}
