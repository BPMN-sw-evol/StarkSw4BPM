package com.starksw4b.pmn.starksw4bpmn.repository;

import com.starksw4b.pmn.starksw4bpmn.model.TaskModel;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TaskRepository extends JpaRepository<TaskModel, Long> {
    List<TaskModel> findByTaskType(String taskType);
}