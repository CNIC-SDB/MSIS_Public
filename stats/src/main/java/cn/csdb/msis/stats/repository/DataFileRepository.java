package cn.csdb.msis.stats.repository;

import cn.csdb.msis.stats.entity.DataFile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * @Author jinbao
 * @Date 2019/7/19 13:53
 **/
@Repository
public interface DataFileRepository extends JpaRepository<DataFile, Integer> {
}
