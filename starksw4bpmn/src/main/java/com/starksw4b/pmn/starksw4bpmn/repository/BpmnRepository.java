package com.starksw4b.pmn.starksw4bpmn.repository;

import com.starksw4b.pmn.starksw4bpmn.model.BpmnModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BpmnRepository extends JpaRepository<BpmnModel,Long> {
        }
