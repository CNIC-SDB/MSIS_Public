package cn.csdb.msis.stats.repository;

import cn.csdb.msis.stats.entity.StatusTemp;
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
 * @Date 2019/11/1 13:59
 **/
@Repository
public interface StatusTempRepository extends JpaRepository<StatusTemp, Integer>, JpaSpecificationExecutor<StatusTemp> {

	@Modifying
	@Transactional
	@Query(nativeQuery = true, value = "delete from status_temp where STR_TO_DATE(DATE_FORMAT(mtime,'%Y-%m-%d-%H'),'%Y-%m-%d-%H')<:timeAgo")
	void deleteOvertimeStatus(String timeAgo);

	@Modifying
	@Query(nativeQuery = true, value = "SELECT " +
			"  `site`.id as id, " +
			"  `monitor`.uri as uri, " +
			"  `site`.cn_name as siteCnName, " +
			" GROUP_CONCAT(DATE_FORMAT(`status`.mtime,'%H')) as result, " +
			" `user`.cn_name as userCnName, " +
			" `user`.monitor_mail as userIsMail, " +
			" `user`.email as userEmail " +
			"FROM " +
			" status_temp `status` " +
			" LEFT JOIN `monitors` `monitor` ON `monitor`.id = `status`.mid " +
			" LEFT JOIN `sites` `site` ON `site`.id = `monitor`.sid " +
			" LEFT JOIN `groups` `group` ON`group`.id = `site`.gid " +
			" LEFT JOIN `users` `user` ON`user`.gid = `group`.id " +
			" where `site`.id > 0 and `status`.`status` <> 200 " +
			" group by `site`.id ")
	List<Tuple> findAccordStatus();
}
