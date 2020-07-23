package cn.csdb.msis.stats.repository;

import cn.csdb.msis.user.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @Author jinbao
 * @Date 2019/7/16 14:06
 **/
@Repository
public interface UserRepository extends JpaRepository<User, Integer>, JpaSpecificationExecutor<User> {

	User findByCnName(String cnName);

	User findByEnName(String enName);

	@Query(name = "select user from User where username=:username")
	User findByUsername(String username);

	User findByUsernameAndPassword(String username, String password);

	List<User> findAll();

	@Query(value = "select user from User user where user.isroot = 0")
	List<User> findAllInstitutes();

	@Modifying
	@Transactional
	void deleteByUsername(String username);

	@Query(value = "select u from User as u where (u.cnName like concat('%',:cnName,'%') or u.enName like concat('%',:cnName,'%') )and u.isroot=0")
	Page<User> findByCnNameLike(String cnName, Pageable pageable);

	@Override
	@Modifying
	@Transactional
	<S extends User> S save(S entity);
}
