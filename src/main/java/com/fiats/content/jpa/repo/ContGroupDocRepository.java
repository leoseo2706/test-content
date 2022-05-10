package com.fiats.content.jpa.repo;

import com.fiats.content.jpa.entity.ContGroupDoc;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Repository
public interface ContGroupDocRepository extends JpaRepository<ContGroupDoc, Long>, JpaSpecificationExecutor<ContGroupDoc> {

    Optional<ContGroupDoc> findByCode(String code);

    ContGroupDoc findByName(String name);

    List<ContGroupDoc> findByCodeIn(Collection<String> codes);

    List<ContGroupDoc> findByIdIn(Collection<Long> ids);

    List<ContGroupDoc> findByStatus(Integer status);

    @Query(value = "select distinct cgd from ContGroupDoc cgd " +
            "inner join fetch cgd.contGroupDocHistories cgdh " +
            "inner join fetch cgdh.contTemplateDoc ctd " +
            "where cgd.name = :name and cgd.status = 1 and cgdh.status = 1 and ctd.status = 1")
    Optional<ContGroupDoc> findLatestTemplatesByName(String name);
}
