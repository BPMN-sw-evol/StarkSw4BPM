package com.starksw4b.pmn.starksw4bpmn.service;

import com.starksw4b.pmn.starksw4bpmn.DTOs.TaskRequestDTO;
import com.starksw4b.pmn.starksw4bpmn.model.TaskModel;
import com.starksw4b.pmn.starksw4bpmn.repository.TaskRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class TaskService {

    private final TaskRepository repository;

    public TaskService(TaskRepository repository) {
        this.repository = repository;
    }

    public void saveTasksFromDto(TaskRequestDTO dto) {
        List<TaskModel> tasks = new ArrayList<>();

        if (dto.getUserTasks() != null) {
            dto.getUserTasks().forEach((name, category) -> {
                tasks.add(createTask(name, category, "userTasks"));
                System.out.println("Tarea de usuario guardada: " + name + " - " + category);

            });
        }

        if (dto.getServiceTasks() != null) {
            dto.getServiceTasks().forEach((name, category) -> {
                tasks.add(createTask(name, category, "serviceTasks"));
                System.out.println("Tarea de servicio guardada: " + name + " - " + category);

            });
        }

        if (dto.getSendTasks() != null) {
            dto.getSendTasks().forEach((name, category) -> {
                tasks.add(createTask(name, category, "sendTasks"));
                System.out.println("Tarea de envío guardada: " + name + " - " + category);

            });
        }

        repository.saveAll(tasks);
    }

    private TaskModel createTask(String name, String category, String type) {
        TaskModel task = new TaskModel();
        task.setName(name);
        task.setTaskCategory(category);
        task.setTaskType(type);
        return task;
    }

    public List<TaskModel> getServiceTasks() {
        return repository.findByTaskType("serviceTasks");
    }

    public List<TaskModel> getUserTasks() {
        return repository.findByTaskCategoryIn(
                List.of("Formulario", "Camunda Forms", "Embedded or External Task Forms", "Generated Task Forms")
        );
    }


}
