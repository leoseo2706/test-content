package com.fiats.content.jpa.repo;


import com.fiats.content.jpa.entity.ContTemplateDocHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ContTemplateDocHistoryRepository  extends JpaRepository<ContTemplateDocHistory,Long> {
    ContTemplateDocHistory findByTemplateIdAndVersionId(Long templateId,Long versionId);
}
