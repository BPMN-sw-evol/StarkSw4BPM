package com.starksw4b.pmn.starksw4bpmn.facade;

import com.starksw4b.pmn.starksw4bpmn.fileGenerator.external.dbProjectsGenerated.FolderCopyService;
import com.starksw4b.pmn.starksw4bpmn.fileGenerator.external.generator.*;
import com.starksw4b.pmn.starksw4bpmn.fileGenerator.external.ExternalProjectGeneratorService;
import com.starksw4b.pmn.starksw4bpmn.model.TaskModel;
import com.starksw4b.pmn.starksw4bpmn.service.TaskService;
import org.springframework.stereotype.Service;

import java.io.IOException;
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
    private final TaskService taskService; // 🔹 servicio que obtiene las tasks desde la DB

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
        // 1. Crear el proyecto externo (cliente/)
        Path externalPath = externalProjectGeneratorService.generateExternalFormProject(proyectoBase.getParent());

        // 2. Obtener userTasks desde la DB
        List<TaskModel> userTasksFromDb = taskService.getUserTasks();
        System.out.println("📝 userTasks detectadas en DB: " + userTasksFromDb);

        if (userTasksFromDb.isEmpty()) {
            System.out.println("⚠ No se encontraron userTasks, no se generarán controladores ni vistas.");
            return;
        }

        // 🔹 Convertimos cada TaskModel en un Map con name + key
        List<Map<String, String>> userTasks = new ArrayList<>();
        for (TaskModel task : userTasksFromDb) {
            Map<String, String> taskInfo = new HashMap<>();
            taskInfo.put("name", task.getName());         // nombre legible
            taskInfo.put("key", task.getTaskKey());      // en tu modelo podrías mapear el taskDefinitionKey real aquí
            userTasks.add(taskInfo);
        }

        // 3. Generar clases DTO, entidad, repositorios, servicios, controladores, vistas
        dtoGeneratorService.generateDtos(externalPath);
        entityGeneratorService.generateEntities(externalPath);
        repositoryGeneratorService.generateRepositories(externalPath);
        serviceGeneratorService.generateServices(externalPath);

        controllerGeneratorService.generateControllers(externalPath, userTasks);
        viewGeneratorService.generateViews(externalPath, userTasks);

        restConfigGeneratorService.generateRestConfig(externalPath);

        // 4. Copiar cada subcarpeta de 'dbs' al mismo nivel que 'cliente' y 'BPMN-Engine'
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
