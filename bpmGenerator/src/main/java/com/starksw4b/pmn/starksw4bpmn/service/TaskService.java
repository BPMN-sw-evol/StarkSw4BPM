package com.starksw4b.pmn.starksw4bpmn.service;

import com.starksw4b.pmn.starksw4bpmn.DTOs.TaskRequestDTO;
import com.starksw4b.pmn.starksw4bpmn.model.TaskModel;
import com.starksw4b.pmn.starksw4bpmn.repository.TaskRepository;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class TaskService {

    private final TaskRepository repository;

    public TaskService(TaskRepository repository) {
        this.repository = repository;
    }

    public void saveTasksFromDto(TaskRequestDTO dto) {
        repository.deleteAll();
        System.out.println("🗑️ Todas las tareas anteriores fueron eliminadas.");

        List<TaskModel> tasks = new ArrayList<>();

        // === UserTasks ===
        if (dto.getUserTasks() != null) {
            dto.getUserTasks().forEach(task -> {
                String name = task.get("name");
                String key = task.get("key");
                String category = task.get("category");
                tasks.add(createTask(name, key, "userTasks", category));
                System.out.println("✅ UserTask guardada: " + name + " - " + key + " - " + category);
            });
        }

        // === ServiceTasks ===
        if (dto.getServiceTasks() != null) {
            dto.getServiceTasks().forEach(task -> {
                String name = task.get("name");
                String key = task.get("key");
                String category = task.get("category");
                tasks.add(createTask(name, key, "serviceTasks", category));
                System.out.println("✅ ServiceTask guardada: " + name + " - " + key + " - " + category);
            });
        }

        // === SendTasks ===
        if (dto.getSendTasks() != null) {
            dto.getSendTasks().forEach(task -> {
                String name = task.get("name");
                String key = task.get("key");
                String category = task.get("category");
                tasks.add(createTask(name, key, "sendTasks", category));
                System.out.println("✅ SendTask guardada: " + name + " - " + key + " - " + category);
            });
        }

        repository.saveAll(tasks);
    }

    private TaskModel createTask(String name, String key, String type, String category) {
        TaskModel task = new TaskModel();
        task.setName(name);        // Nombre visible en BPMN
        task.setTaskKey(key);      // taskDefinitionKey
        task.setTaskType(type);    // userTasks / serviceTasks / sendTasks
        task.setTaskCategory(category); // categoría elegida en Streamlit
        return task;
    }


    private TaskModel createTask(String name, String taskKey, String type) {
        TaskModel task = new TaskModel();
        task.setName(name);         // nombre visible en BPMN
        task.setTaskKey(taskKey);   // 🔹 taskDefinitionKey (ej: Activity_0viydpl)
        task.setTaskType(type);
        return task;
    }

    // === Getters ===

    public List<TaskModel> getServiceTasks() {
        return repository.findByTaskType("serviceTasks");
    }

    public List<TaskModel> getUserTasks() {
        return repository.findByTaskType("userTasks");
    }

    public List<TaskModel> getAllTasks() {
        return repository.findAll();
    }

    // ✅ devuelve solo nombres (compatibilidad)
    public List<String> getUserTaskNames() {
        return getUserTasks().stream()
                .map(TaskModel::getName)
                .toList();
    }

    // ✅ devuelve pares name + key para el generador
    public List<Map<String, String>> getUserTaskData() {
        List<Map<String, String>> result = new ArrayList<>();
        for (TaskModel task : getUserTasks()) {
            Map<String, String> map = new HashMap<>();
            map.put("name", task.getName());     // ej: Acknowledge as received
            map.put("key", task.getTaskKey());   // ej: Activity_0viydpl
            result.add(map);
        }
        return result;
    }
}
