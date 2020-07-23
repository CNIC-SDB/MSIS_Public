package cn.csdb.msis.stats.repository;

import cn.csdb.msis.stats.entity.AwGeneral;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

/**
 * @Author jinbao
 * @Date 2019/7/19 13:52
 **/
@Repository
public interface AwGeneralRepository extends JpaRepository<AwGeneral, Integer> {

	@Modifying
	@Transactional
	@Query(value = "delete from #{#entityName} as table where table.dataFile.id=:fid ")
	void deleteByFid(Integer fid);
}
