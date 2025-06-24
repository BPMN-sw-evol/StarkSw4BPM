package com.starksw4b.pmn.starksw4bpmn.repository;

import com.starksw4b.pmn.starksw4bpmn.model.TaskModel;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TaskRepository extends JpaRepository<TaskModel, Long> {
}