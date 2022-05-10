package com.fiats.content.jpa.repo;

import com.fiats.content.jpa.entity.ContTemplateDoc;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;

@Repository
public interface ContTemplateDocRepo extends JpaRepository<ContTemplateDoc, Long> {

    List<ContTemplateDoc> findByCodeIn(Collection<String> codes);

}