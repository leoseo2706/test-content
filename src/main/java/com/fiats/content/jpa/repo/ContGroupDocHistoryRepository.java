package com.fiats.content.jpa.repo;


import com.fiats.content.jpa.entity.ContGroupDocHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ContGroupDocHistoryRepository extends JpaRepository<ContGroupDocHistory, Long> {

    List<ContGroupDocHistory> findByGroupIdAndStatus(Long groupI, Integer status);

    List<ContGroupDocHistory> findByStatusAndTemplateId( Integer status, Long templateId);
}
