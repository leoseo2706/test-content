package com.fiats.content.jpa.repo;


import com.fiats.content.jpa.entity.ContTemplateDocVersion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;
import java.util.Map;

@Repository
public interface ContTemplateDocVersionRepository extends JpaRepository<ContTemplateDocVersion, Long> {

    ContTemplateDocVersion findByTemplateIdAndStatus(Long templateId, Integer status);

    ContTemplateDocVersion findByTemplateIdAndVersion(Long templateId, Integer version);

    ContTemplateDocVersion findFirstByTemplateIdAndStatus(Long templateId, Integer status);

    List<ContTemplateDocVersion> findByStatusAndTemplateIdIn(Integer status, Collection<Long>templateId );

    @Query(value = "from ContTemplateDocVersion ctdv " +
            "inner join fetch ctdv.contTemplateDoc ctd " +
            "where ctdv.templateId in :templateIds and ctdv.status = 1 and ctd.status = 1")
    List<ContTemplateDocVersion> findByTemplateIdIn(Collection<Long> templateIds);

}
