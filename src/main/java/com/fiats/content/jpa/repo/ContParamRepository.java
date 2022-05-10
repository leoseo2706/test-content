package com.fiats.content.jpa.repo;

import com.fiats.content.jpa.entity.ContParam;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ContParamRepository extends JpaRepository<ContParam, Long>, JpaSpecificationExecutor<ContParam> {

    Optional<ContParam> findByCode(String code);

}
