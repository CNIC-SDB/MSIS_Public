package cn.csdb.msis.manager.service;

import cn.csdb.msis.manager.query.PageResultSet;
import cn.csdb.msis.manager.query.UserQuery;
import cn.csdb.msis.stats.entity.FtpUser;
import cn.csdb.msis.stats.entity.Group;
import cn.csdb.msis.stats.repository.FtpUserRepository;
import cn.csdb.msis.stats.repository.UserRepository;
import cn.csdb.msis.user.entity.Role;
import cn.csdb.msis.user.entity.User;
import cn.csdb.msis.util.MD5Util;
import org.apache.logging.log4j.util.Strings;
import org.apache.shiro.SecurityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.HashSet;
import java.util.Random;

/**
 * @Author jinbao
 * @Date 2019/8/13 15:15
 * @Description
 **/
@Service
public class UserService {

	Logger logger = LoggerFactory.getLogger(UserService.class);

	@Resource
	private UserRepository userDao;

	@Resource
	private FtpUserRepository ftpUserDao;

	@Resource
	private SendMailService sendMailService;

	@Value(value = "${custom.ftpRootPath}")
	private String ftpRootPath;

	@Value(value = "${custom.createFtpDir}")
	private String createFtpDir;

	@Value(value = "${custom.salt}")
	private String salt;

	/**
	 * 登录
	 *
	 * @param userName
	 * @return
	 */
	public User findByUsername(String userName) {
		User user = userDao.findByUsername(userName);
		return user;
	}

	public PageResultSet<User> findInstitutesByCnName(UserQuery query) {
		PageRequest pageRequestService = PageRequest.of(query.getPageNumber() - 1, query.getPageSize());
		Page<User> byCnNameLike = userDao.findByCnNameLike(query.getCnName(), pageRequestService);
		PageResultSet<User> institutesPageResultSet = new PageResultSet<>();
		institutesPageResultSet.setRows(byCnNameLike.getContent());
		institutesPageResultSet.setTotal(byCnNameLike.getTotalElements());
		institutesPageResultSet.setPageNumber(query.getPageNumber());
		return institutesPageResultSet;
	}

	/**
	 * 新增院所信息 同时新增一个逻辑分组
	 *
	 * @param query
	 */
	@Transactional
	public User saveInstitute(UserQuery query) {
		// 当前管理员
		User subject = (User) SecurityUtils.getSubject().getPrincipal();
		Integer currentAdminId = subject.getGroup().getId();
		// 生成一个用户信息
		User user = new User();
		user.setUsername(query.getUsername());
		user.setPassword(MD5Util.encryptPassword(user.getUsername(), query.getPassword(), salt));
		user.setEmail(query.getEmail());
		user.setMobile(query.getMobile());
		HashSet<Role> rolesHashSet = new HashSet<>();
		Role roles = new Role();
		roles.setRolename(Role.USER);
		rolesHashSet.add(roles);
		user.setRoles(rolesHashSet);
		user.setCnName(query.getCnName());
		user.setEnName(query.getEnName());
		user.setDescription(query.getDescription());
		user.setIsroot(0);

		// 生成一个学院逻辑组信息
		Group group = new Group();
		group.setCnName(query.getCnName());
		group.setEnName(query.getEnName());
		group.setDescription(query.getDescription());
		group.setGtype(Group.POINT_INSTITUTE);
		// 当前用户所属管理节点
		group.setPid(currentAdminId);
		group.setIsRank(0);
		user.setGroup(group);
		User saveUser = userDao.save(user);
		saveUser.setFtpPasswordTemp(randomFtpPassword(8));
		//生成ftp账号并保存
		createFtpUser(saveUser);

		// 生成ftp用户主目录
		createFtpFolder(query.getEnName());

		// 邮件通知新管理员
		saveUser.setTransientPassword(query.getPassword());
		sendMailService.addUserMessage(saveUser);
		return saveUser;
	}

	private void createFtpUser(User user) {
		FtpUser ftpUser = new FtpUser();
		ftpUser.setUsername(user.getEnName());
		ftpUser.setPassword(user.getFtpPasswordTemp());
		ftpUserDao.save(ftpUser);
	}

	private void createFtpFolder(String enName) {
		if (!System.getProperty("os.name").startsWith("Windows")) {
			StringBuilder path = new StringBuilder("sh ");
			path.append(System.getProperty("user.dir"));
			path.append("/");
			path.append(createFtpDir);
			path.append(" ");
			path.append(ftpRootPath);
			path.append(" ");
			path.append(enName);
			try {
				Runtime.getRuntime().exec(path.toString());
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	public User updateUser(UserQuery query) {
		User user = userDao.findByUsername(query.getOldUsername());

		if (Strings.isNotBlank(query.getCnName())) {
			user.setCnName(query.getCnName());
		}
		if (Strings.isNotBlank(query.getUsername())) {
			user.setUsername(query.getUsername());
		}
		if (Strings.isNotBlank(query.getEmail())) {
			user.setEmail(query.getEmail());
		}
		if (Strings.isNotBlank(query.getMobile())) {
			user.setMobile(query.getMobile());
		}
		if (Strings.isNotBlank(query.getDescription())) {
			user.setDescription(query.getDescription());
		}
		if (Strings.isNotBlank(query.getPassword())) {
			user.setPassword(MD5Util.encryptPassword(user.getUsername(), query.getPassword(), salt));
		}
		if (query.getMonitorMail() != null) {
			user.setMonitorMail(query.getMonitorMail());
		}
		User saveUser = userDao.save(user);
		// 编辑用户信息暂不发邮件通知
//		sendMailService.editUserMessage(saveUser);
		return saveUser;
	}

	@Transactional
	public void deleteUser(UserQuery userQuery) {
		userDao.deleteByUsername(userQuery.getUsername());
		ftpUserDao.deleteFtpUserByUsername(userQuery.getEnName());
	}

	public Boolean cnNameIsExist(String cnName) {
		User byCnName = userDao.findByCnName(cnName);
		if (byCnName == null) {
			return true;
		}
		return false;
	}

	public Boolean enNameIsExist(String enName) {
		User byEnName = userDao.findByEnName(enName);
		if (byEnName == null) {
			return true;
		}
		return false;
	}

	public Boolean usernameIsExist(String username, Integer id) {
		User byUsername = userDao.findByUsername(username);
		if (byUsername == null) {
			return true;
		} else {
			return id != null && byUsername.getId().intValue() == id.intValue();
		}
	}

	public Boolean findByUsernameAndPassword(String username, String oldPassword) {
		User byUsernameAndPassword = userDao.findByUsernameAndPassword(username, MD5Util.encryptPassword(username, oldPassword, salt));
		if (byUsernameAndPassword == null) {
			return false;
		}
		return true;
	}

	private String randomFtpPassword(int digit) {
		char[] symbol = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9',
				'a', 'A', 'b', 'B', 'c', 'C', 'd', 'D', 'e', 'E', 'f', 'F',
				'g', 'G', 'h', 'H', 'i', 'I', 'j', 'J', 'k', 'K', 'l', 'L',
				'm', 'M', 'n', 'N', 'o', 'O', 'p', 'P', 'q', 'Q', 'r', 'R',
				's', 'S', 't', 'T', 'u', 'U', 'v', 'V', 'w', 'W', 'x', 'X',
				'y', 'Y', 'z', 'Z'};
		StringBuilder sb = new StringBuilder();
		Random r = new Random();
		for (int i = digit; i >= 0; i--) {
			sb.append(symbol[r.nextInt(symbol.length - 1)]);
		}
		return sb.toString();
	}
}
