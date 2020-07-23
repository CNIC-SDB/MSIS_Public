package cn.csdb.msis.stats.repository;

import cn.csdb.msis.stats.entity.FtpUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;


@Repository
public interface FtpUserRepository extends JpaRepository<FtpUser, Integer>, JpaSpecificationExecutor<FtpUser> {
	@Modifying
	@Query(value = "delete from FtpUser ftpUser where ftpUser.username=:username ")
	void deleteFtpUserByUsername(String username);
}
