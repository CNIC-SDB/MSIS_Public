package cn.csdb.msis.manager.service;

import cn.csdb.msis.manager.query.MonitorQuery;
import cn.csdb.msis.stats.entity.Monitor;
import cn.csdb.msis.stats.repository.MonitorRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.Optional;

/**
 * @Author jinbao
 * @Date 2019/8/28 14:43
 * @Description TODO
 **/
@Service
public class MonitorService {

	@Resource
	public MonitorRepository monitorDao;

	public Monitor findById(MonitorQuery monitorQuery) {
		Optional<Monitor> byId = monitorDao.findById(monitorQuery.getId());
		Monitor monitor = byId.get();
		return monitor;
	}

	@Transactional
	public Monitor saveOrUpdate(MonitorQuery monitorQuery) {
		Monitor m = new Monitor();
		if (monitorQuery.getId() != null) {
			Optional<Monitor> byId = monitorDao.findById(monitorQuery.getId());
			m = byId.get();
		} else {
			m.setSid(monitorQuery.getSid());
			m.setFlag(0);
		}
		m.setType(monitorQuery.getType());
		m.setName(monitorQuery.getName());
		m.setUri(monitorQuery.getUri());
		m.setKeyword(monitorQuery.getKeyword());
		m.setDescription(monitorQuery.getDescription());
		Monitor monitor = monitorDao.save(m);
		return monitor;
	}

}
