package com.starksw4b.pmn.starksw4bpmn.service;

import com.starksw4b.pmn.starksw4bpmn.DTOs.TaskRequestDTO;
import com.starksw4b.pmn.starksw4bpmn.DTOs.TaskResponseDTO;
import com.starksw4b.pmn.starksw4bpmn.model.TaskModel;
import com.starksw4b.pmn.starksw4bpmn.repository.TaskRepository;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.*;

@Service
public class TaskService {

    private final TaskRepository repository;
    private final BpmnParserService bpmnParserService;

    public TaskService(TaskRepository repository, BpmnParserService bpmnParserService) {
        this.repository = repository;
        this.bpmnParserService = bpmnParserService;
    }


    /**
     * 💾 Guarda las tareas recibidas desde Streamlit
     * y agrega las compuertas detectadas en el BPMN actual.
     */
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

        // === Agregar compuertas desde el último BPMN subido ===
        try {
            File uploadsDir = new File(System.getProperty("user.dir") + "/uploads");
            Optional<File> ultimoBpmn = Arrays.stream(Objects.requireNonNull(uploadsDir.listFiles()))
                    .filter(f -> f.getName().endsWith(".bpmn"))
                    .max(Comparator.comparingLong(File::lastModified));

            if (ultimoBpmn.isPresent()) {
                String bpmnPath = ultimoBpmn.get().getAbsolutePath();
                Map<String, List<Map<String, String>>> elementos = bpmnParserService.parseElements(bpmnPath);

                for (Map.Entry<String, List<Map<String, String>>> entry : elementos.entrySet()) {
                    String tipo = entry.getKey();
                    if (tipo.endsWith("Gateway")) { // Solo compuertas
                        for (Map<String, String> data : entry.getValue()) {
                            TaskModel gateway = new TaskModel();
                            gateway.setName(data.get("name"));
                            gateway.setTaskKey(data.get("key"));
                            gateway.setTaskType(tipo);
                            gateway.setTaskCategory("gateway");
                            tasks.add(gateway);
                            System.out.println("🌀 Compuerta agregada: " + data.get("name") + " (" + tipo + ")");
                        }
                    }
                }
            } else {
                System.out.println("⚠ No se encontró ningún archivo BPMN en uploads/. No se agregaron compuertas.");
            }
        } catch (Exception e) {
            System.err.println("❌ Error al agregar compuertas: " + e.getMessage());
        }

        repository.saveAll(tasks);
        System.out.println("💾 Total guardado: " + tasks.size() + " elementos (tareas + compuertas).");
    }

    // ======================= MÉTODOS AUXILIARES =======================

    private TaskModel createTask(String name, String key, String type, String category) {
        TaskModel task = new TaskModel();
        task.setName(name);
        task.setTaskKey(key);
        task.setTaskType(type);
        task.setTaskCategory(category);
        return task;
    }

    private TaskModel createTask(String name, String taskKey, String type) {
        TaskModel task = new TaskModel();
        task.setName(name);
        task.setTaskKey(taskKey);
        task.setTaskType(type);
        return task;
    }

    // ======================= CONSULTAS =======================

    public List<TaskModel> getServiceTasks() {
        return repository.findByTaskType("serviceTasks");
    }

    public List<TaskModel> getUserTasks() {
        return repository.findByTaskType("userTasks");
    }

    public List<TaskModel> getAllTasks() {
        return repository.findAll();
    }

    public List<String> getUserTaskNames() {
        return getUserTasks().stream()
                .map(TaskModel::getName)
                .toList();
    }

    public List<Map<String, String>> getUserTaskData() {
        List<Map<String, String>> result = new ArrayList<>();
        for (TaskModel task : getUserTasks()) {
            Map<String, String> map = new HashMap<>();
            map.put("name", task.getName());
            map.put("key", task.getTaskKey());
            result.add(map);
        }
        return result;
    }

    public List<TaskResponseDTO> getAllTaskDTOs() {
        return repository.findAll().stream()
                .map(t -> new TaskResponseDTO(
                        t.getName(),
                        t.getTaskKey(),
                        t.getTaskType(),
                        t.getTaskCategory()
                ))
                .toList();
    }

    public List<TaskResponseDTO> getUserTaskDTOs() {
        return repository.findByTaskType("userTasks").stream()
                .map(t -> new TaskResponseDTO(
                        t.getName(),
                        t.getTaskKey(),
                        t.getTaskType(),
                        t.getTaskCategory()
                ))
                .toList();
    }

    public List<TaskResponseDTO> getTasksByCategory(String category) {
        return repository.findAll().stream()
                .filter(t -> t.getTaskCategory().equalsIgnoreCase(category))
                .map(t -> new TaskResponseDTO(
                        t.getName(),
                        t.getTaskKey(),
                        t.getTaskType(),
                        t.getTaskCategory()
                ))
                .toList();
    }
}
