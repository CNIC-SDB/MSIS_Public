package cn.csdb.msis.stats.repository;

import cn.csdb.msis.stats.entity.StatusDaily;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

/**
 * @Author jinbao
 * @Date 2019/7/19 14:01
 **/
@Repository
public interface StatusDailyRepository extends JpaRepository<StatusDaily, Integer>, JpaSpecificationExecutor<StatusDaily> {
}
