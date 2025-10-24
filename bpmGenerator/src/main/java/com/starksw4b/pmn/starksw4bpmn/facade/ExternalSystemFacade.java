package com.starksw4b.pmn.starksw4bpmn.facade;

import com.starksw4b.pmn.starksw4bpmn.DTOs.TaskResponseDTO;
import com.starksw4b.pmn.starksw4bpmn.fileGenerator.external.dbProjectsGenerated.FolderCopyService;
import com.starksw4b.pmn.starksw4bpmn.fileGenerator.external.generator.*;
import com.starksw4b.pmn.starksw4bpmn.fileGenerator.external.ExternalProjectGeneratorService;
import com.starksw4b.pmn.starksw4bpmn.service.BpmnParserService;
import com.starksw4b.pmn.starksw4bpmn.service.TaskService;
import org.springframework.stereotype.Service;

import java.io.FileNotFoundException;
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
    private final ConditionFlowGeneratorService conditionFlowGeneratorService;
    private final BpmnParserService bpmnParserService;

    public ExternalSystemFacade(
            ExternalProjectGeneratorService externalProjectGeneratorService,
            DtoGeneratorService dtoGeneratorService,
            EntityGeneratorService entityGeneratorService,
            RepositoryGeneratorService repositoryGeneratorService,
            ServiceGeneratorService serviceGeneratorService,
            ControllerGeneratorService controllerGeneratorService,
            ViewGeneratorService viewGeneratorService,
            RestConfigGeneratorService restConfigGeneratorService,
            FolderCopyService folderCopyService,
            TaskService taskService,
            ConditionFlowGeneratorService conditionFlowGeneratorService,
            BpmnParserService bpmnParserService
    ) {
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
        this.conditionFlowGeneratorService = conditionFlowGeneratorService;
        this.bpmnParserService = bpmnParserService;
    }

    public void generarSistemaExterno(Path proyectoBase, String bpmnFileName) throws Exception {
        // 1️⃣ Crear el proyecto externo (cliente/)
        Path externalPath = externalProjectGeneratorService.generateExternalFormProject(proyectoBase.getParent());

        // 2️⃣ Buscar el BPMN en la carpeta "uploads/"
        Path bpmnDir = Path.of("uploads").toAbsolutePath().normalize();
        Path bpmnPath = bpmnDir.resolve(bpmnFileName);

        System.out.println("📂 Buscando BPMN en: " + bpmnPath);

        if (!Files.exists(bpmnPath)) {
            throw new FileNotFoundException("❌ No se encontró el archivo BPMN en: " + bpmnPath);
        }

        // 3️⃣ Extraer el processDefinitionKey desde el BPMN
        String processDefinitionKey = bpmnParserService.extractProcessId(bpmnPath.toString());
        System.out.println("✅ processDefinitionKey extraído del BPMN: " + processDefinitionKey);

        // 4️⃣ Obtener tareas (solo userTasks) desde la DB
        List<TaskResponseDTO> userTasksFromDb = taskService.getUserTaskDTOs();
        System.out.println("📝 userTasks detectadas en DB: " + userTasksFromDb.size());

        // 5️⃣ Obtener compuertas desde la DB
        List<TaskResponseDTO> gatewaysFromDb = taskService.getTasksByCategory("gateway");
        System.out.println("🌀 Compuertas detectadas en DB: " + gatewaysFromDb.size());

        if (userTasksFromDb.isEmpty() && gatewaysFromDb.isEmpty()) {
            System.out.println("⚠ No se encontraron tareas ni compuertas para generar el sistema externo.");
            return;
        }

        // 6️⃣ Convertir userTasks a formato compatible con los generadores
        List<Map<String, String>> userTasks = new ArrayList<>();
        for (TaskResponseDTO task : userTasksFromDb) {
            Map<String, String> taskInfo = new HashMap<>();
            taskInfo.put("name", task.getName());
            taskInfo.put("key", task.getTaskKey());
            userTasks.add(taskInfo);
        }

        // 7️⃣ Generar capas base del proyecto
        dtoGeneratorService.generateDtos(externalPath);
        entityGeneratorService.generateEntities(externalPath);
        repositoryGeneratorService.generateRepositories(externalPath);
        serviceGeneratorService.generateServices(externalPath, processDefinitionKey); // ✅ usa la clave real del BPMN

        // 8️⃣ Generar controladores y vistas
        if (!userTasks.isEmpty()) {
            controllerGeneratorService.generateControllers(externalPath, userTasks);
            viewGeneratorService.generateViews(externalPath, userTasks);
            conditionFlowGeneratorService.generateConditionalControllers(externalPath);
        }

        // 9️⃣ Configuración REST global
        restConfigGeneratorService.generateRestConfig(externalPath);

        // 🔟 Procesar compuertas BPMN (futuras expansiones)
        if (!gatewaysFromDb.isEmpty()) {
            for (TaskResponseDTO gateway : gatewaysFromDb) {
                System.out.printf("⚙️ Detectada %s → %s (%s)%n",
                        gateway.getTaskType(), gateway.getName(), gateway.getTaskKey());
            }
        }

        // 1️⃣1️⃣ Copiar subcarpetas de dbs/
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
