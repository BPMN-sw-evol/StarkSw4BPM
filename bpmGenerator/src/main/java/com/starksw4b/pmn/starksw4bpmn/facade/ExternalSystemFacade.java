package com.starksw4b.pmn.starksw4bpmn.facade;

import com.starksw4b.pmn.starksw4bpmn.DTOs.TaskResponseDTO;
import com.starksw4b.pmn.starksw4bpmn.fileGenerator.external.dbProjectsGenerated.FolderCopyService;
import com.starksw4b.pmn.starksw4bpmn.fileGenerator.external.generator.*;
import com.starksw4b.pmn.starksw4bpmn.fileGenerator.external.ExternalProjectGeneratorService;
import com.starksw4b.pmn.starksw4bpmn.service.TaskService;
import org.springframework.stereotype.Service;

import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

@Service
public class ExternalSystemFacade {

    private final ExternalProjectGeneratorService externalProjectGeneratorService;
    private final DtoGeneratorService dtoGeneratorService;
    private final EntityGeneratorService entityGeneratorService;
    private final RepositoryGeneratorService repositoryGeneratorService;
    private final ServiceGeneratorService serviceGeneratorService;
    private final ControllerGeneratorService controllerGeneratorService;
    private final ViewGeneratorService viewGeneratorService;
    private final RestConfigGeneratorService restConfigGeneratorService;
    private final FolderCopyService folderCopyService;
    private final TaskService taskService;

    public ExternalSystemFacade(ExternalProjectGeneratorService externalProjectGeneratorService,
                                DtoGeneratorService dtoGeneratorService,
                                EntityGeneratorService entityGeneratorService,
                                RepositoryGeneratorService repositoryGeneratorService,
                                ServiceGeneratorService serviceGeneratorService,
                                ControllerGeneratorService controllerGeneratorService,
                                ViewGeneratorService viewGeneratorService,
                                RestConfigGeneratorService restConfigGeneratorService,
                                FolderCopyService folderCopyService,
                                TaskService taskService) {
        this.externalProjectGeneratorService = externalProjectGeneratorService;
        this.dtoGeneratorService = dtoGeneratorService;
        this.entityGeneratorService = entityGeneratorService;
        this.repositoryGeneratorService = repositoryGeneratorService;
        this.serviceGeneratorService = serviceGeneratorService;
        this.controllerGeneratorService = controllerGeneratorService;
        this.viewGeneratorService = viewGeneratorService;
        this.restConfigGeneratorService = restConfigGeneratorService;
        this.folderCopyService = folderCopyService;
        this.taskService = taskService;
    }

    public void generarSistemaExterno(Path proyectoBase, String bpmnFileName) throws Exception {
        // 1️⃣ Crear el proyecto externo (cliente/)
        Path externalPath = externalProjectGeneratorService.generateExternalFormProject(proyectoBase.getParent());

        // 2️⃣ Obtener tareas (solo userTasks) desde la capa de servicio en formato DTO
        List<TaskResponseDTO> userTasksFromDb = taskService.getUserTaskDTOs();
        System.out.println("📝 userTasks detectadas en DB: " + userTasksFromDb.size());

        // 3️⃣ Obtener compuertas (Exclusive/Parallel) desde la DB
        List<TaskResponseDTO> gatewaysFromDb = taskService.getTasksByCategory("gateway");
        System.out.println("🌀 Compuertas detectadas en DB: " + gatewaysFromDb.size());

        if (userTasksFromDb.isEmpty() && gatewaysFromDb.isEmpty()) {
            System.out.println("⚠ No se encontraron tareas ni compuertas para generar el sistema externo.");
            return;
        }

        // 🔹 Convertir userTasks a formato compatible con generadores
        List<Map<String, String>> userTasks = new ArrayList<>();
        for (TaskResponseDTO task : userTasksFromDb) {
            Map<String, String> taskInfo = new HashMap<>();
            taskInfo.put("name", task.getName());
            taskInfo.put("key", task.getTaskKey());
            userTasks.add(taskInfo);
        }

        // 4️⃣ Generar capas base del proyecto
        dtoGeneratorService.generateDtos(externalPath);
        entityGeneratorService.generateEntities(externalPath);
        repositoryGeneratorService.generateRepositories(externalPath);
        serviceGeneratorService.generateServices(externalPath);

        // 5️⃣ Generar controladores y vistas de formularios
        if (!userTasks.isEmpty()) {
            controllerGeneratorService.generateControllers(externalPath, userTasks);
            viewGeneratorService.generateViews(externalPath, userTasks);
        }

        // 6️⃣ Configuración REST global
        restConfigGeneratorService.generateRestConfig(externalPath);

        // 7️⃣ Procesar compuertas BPMN (preparado para futuras expansiones)
        if (!gatewaysFromDb.isEmpty()) {
            for (TaskResponseDTO gateway : gatewaysFromDb) {
                String type = gateway.getTaskType();
                String name = gateway.getName();
                String key = gateway.getTaskKey();

                if ("exclusiveGateway".equalsIgnoreCase(type)) {
                    System.out.println("⚙️ Detectada ExclusiveGateway → estructura condicional: " + name + " (" + key + ")");
                    // 🔜 Futuro: GatewayDecisionGeneratorService.generate(...)
                }

                if ("parallelGateway".equalsIgnoreCase(type)) {
                    System.out.println("⚙️ Detectada ParallelGateway → estructura paralela: " + name + " (" + key + ")");
                    // 🔜 Futuro: ParallelGatewayGeneratorService.generate(...)
                }
            }
        }

        // 8️⃣ Copiar subcarpetas de dbs/
        Path sourceDbsDir = Path.of("dbs");
        Path destinationBase = proyectoBase.getParent();

        try (DirectoryStream<Path> stream = Files.newDirectoryStream(sourceDbsDir)) {
            for (Path subDir : stream) {
                if (Files.isDirectory(subDir)) {
                    Path destination = destinationBase.resolve(subDir.getFileName());
                    folderCopyService.copyFolder(subDir, destination);
                    System.out.println("📁 Copiado: " + subDir.getFileName());
                }
            }
        }

        System.out.println("✅ Proyecto externo enriquecido correctamente.");
    }
}
