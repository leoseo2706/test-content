package com.fiats.content.redis.repo;


import com.fiats.content.redis.entity.ContractTemplateRedis;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.QueryByExampleExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface ContractTemplateRedisRepo extends JpaRepository<ContractTemplateRedis, String>, QueryByExampleExecutor<ContractTemplateRedis> {

    ContractTemplateRedis findByIndex(String code);


}
