/*
package cn.csdb.msis.stats.service;

import cn.csdb.msis.stats.entity.AwAchost;
import cn.csdb.msis.stats.entity.AwTime;
import cn.csdb.msis.stats.entity.Institute;
import cn.csdb.msis.stats.repository.AwAchostRepository;
import cn.csdb.msis.stats.repository.AwTimeRepository;
import cn.csdb.msis.stats.repository.InstitutesRepository;
import cn.csdb.msis.stats.repository.UserRepository;
import cn.csdb.msis.user.entity.Role;
import cn.csdb.msis.user.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;

*/
/**
 * @Author jinbao
 * @Date 2019/7/16 11:07
 **//*

@Service
public class AwAchostService {

	@Autowired
	AwAchostRepository awAchostRepository;

	@Autowired
	UserRepository userRepository;

	@Autowired
	InstitutesRepository institutesRepository;

	@Autowired
	AwAchostRepository awAchostDao;

	@Autowired
	AwTimeRepository awTimeDao;

	@Transactional
	public void findAwAchostById() {
		List<Institute> institutesRepositoryAll = institutesRepository.findAll();

//		institutesRepository.deleteById(85);

		List<User> userRepositoryAll = userRepository.findAll();

		List<AwAchost> awAchostRepositoryAll = awAchostRepository.findAll();

		List<AwAchost> awAchostDaoAll = awAchostDao.findAll();

		List<AwTime> awTimeDaoAll = awTimeDao.findAll();

		for (Institute institutes : institutesRepositoryAll) {
//			Set<User> users = institutes.getUsers();
			String cnName = institutes.getCnName();
			System.out.println("");
		}

		for (User users : userRepositoryAll) {
//			Institute institutes = users.getInstitutes();
			String username = users.getUserName();
			Set<Role> roles = users.getRoles();
			if (username.equalsIgnoreCase("test_user_delete")) {
				System.out.println("执行删除");
				userRepository.delete(users);
			}
			System.out.println("");
		}
//		throw new RuntimeException();
	}


}
*/
