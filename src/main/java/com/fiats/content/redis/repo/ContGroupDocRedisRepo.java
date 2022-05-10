package com.fiats.content.redis.repo;


import com.fiats.content.redis.entity.ContGroupDocRedis;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ContGroupDocRedisRepo extends JpaRepository<ContGroupDocRedis, String> {
    ContGroupDocRedis findByIndex(String index);

    List<ContGroupDocRedis> findByCodeLike(String code);

}
