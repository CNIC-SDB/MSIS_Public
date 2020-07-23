package cn.csdb.msis.stats.repository;

import cn.csdb.msis.stats.entity.Monitor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;


/**
 * @Author CXY
 * @Date 2019/8/13 13:56
 **/
@Repository
public interface MonitorRepository extends JpaRepository<Monitor, Integer>, JpaSpecificationExecutor<Monitor> {

}
