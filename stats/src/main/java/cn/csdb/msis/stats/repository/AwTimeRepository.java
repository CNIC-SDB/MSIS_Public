package cn.csdb.msis.stats.repository;

import cn.csdb.msis.stats.entity.AwTime;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public interface AwTimeRepository extends JpaRepository<AwTime, Integer> {
    @Modifying
    @Transactional
    @Query(value = "delete from #{#entityName} as table where table.dataFile.id=:fid ")
    void deleteByFid(Integer fid);
}
