package com.starksw4b.pmn.starksw4bpmn.controller;

import com.starksw4b.pmn.starksw4bpmn.DTOs.TaskRequestDTO;
import com.starksw4b.pmn.starksw4bpmn.service.TaskService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/tasks")
public class TaskController {

    private final TaskService service;

    public TaskController(TaskService service) {
        this.service = service;
    }

    // ✅ Importar tareas desde JSON
    @PostMapping("/import")
    public ResponseEntity<String> importTasks(@RequestBody TaskRequestDTO dto) {
        service.saveTasksFromDto(dto);
        return ResponseEntity.ok("✅ Tareas guardadas correctamente en la base de datos.");
    }

    // ✅ Consultar todas las tareas (debug)
    @GetMapping
    public ResponseEntity<?> getAllTasks() {
        return ResponseEntity.ok(service.getAllTasks());
    }

    // ✅ Consultar solo las UserTasks (con name + key)
    @GetMapping("/user")
    public ResponseEntity<?> getUserTasks() {
        return ResponseEntity.ok(service.getUserTaskData());
    }
}

