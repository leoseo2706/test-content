package com.fiats.content.jpa.repo;


import com.fiats.content.jpa.entity.ContTemplateDoc;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Repository
public interface ContTemplateRepository extends JpaRepository<ContTemplateDoc, Long>, JpaSpecificationExecutor<ContTemplateDoc> {
    Optional<ContTemplateDoc> findByCode(String code);

    List<ContTemplateDoc> findByCodeIn(Collection<String> codes);

    List<ContTemplateDoc> findByIdIn(Collection<Long> ids);

}
