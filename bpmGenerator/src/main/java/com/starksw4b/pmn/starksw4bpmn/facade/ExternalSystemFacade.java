package com.starksw4b.pmn.starksw4bpmn.facade;

import com.starksw4b.pmn.starksw4bpmn.fileGenerator.external.dbProjectsGenerated.FolderCopyService;
import com.starksw4b.pmn.starksw4bpmn.fileGenerator.external.generator.*;
import com.starksw4b.pmn.starksw4bpmn.fileGenerator.external.ExternalProjectGeneratorService;
import com.starksw4b.pmn.starksw4bpmn.model.FormFieldData;
import com.starksw4b.pmn.starksw4bpmn.service.BpmnService;
import com.starksw4b.pmn.starksw4bpmn.service.TaskService;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;

@Service
public class ExternalSystemFacade {

    private final ExternalProjectGeneratorService externalProjectGeneratorService;
    private final DtoGeneratorService dtoGeneratorService;
    private final EntityGeneratorService entityGeneratorService;
    private final BpmnService bpmnService;
    private final RepositoryGeneratorService repositoryGeneratorService;
    private final ServiceGeneratorService serviceGeneratorService;
    private final ControllerGeneratorService controllerGeneratorService;
    private final ViewGeneratorService viewGeneratorService;
    private final RestConfigGeneratorService restConfigGeneratorService;
    private FolderCopyService folderCopyService;

    public ExternalSystemFacade(ExternalProjectGeneratorService externalProjectGeneratorService,
                                DtoGeneratorService dtoGeneratorService,
                                EntityGeneratorService entityGeneratorService,
                                BpmnService bpmnService,
                                RepositoryGeneratorService repositoryGeneratorService,
                                ServiceGeneratorService serviceGeneratorService,
                                ControllerGeneratorService controllerGeneratorService,
                                ViewGeneratorService viewGeneratorService,
                                RestConfigGeneratorService restConfigGeneratorService,
                                FolderCopyService folderCopyService) {
        this.externalProjectGeneratorService = externalProjectGeneratorService;
        this.dtoGeneratorService = dtoGeneratorService;
        this.entityGeneratorService = entityGeneratorService;
        this.bpmnService = bpmnService;
        this.repositoryGeneratorService = repositoryGeneratorService;
        this.serviceGeneratorService = serviceGeneratorService;
        this.controllerGeneratorService = controllerGeneratorService;
        this.viewGeneratorService = viewGeneratorService;
        this.restConfigGeneratorService = restConfigGeneratorService;
        this.folderCopyService = folderCopyService;
    }

    public void generarSistemaExterno(Path proyectoBase, String bpmnFileName) throws Exception {
        // 1. Crear el proyecto externo (cliente/)
        Path externalPath = externalProjectGeneratorService.generateExternalFormProject(proyectoBase.getParent());

        // 2. Leer archivo BPMN para extraer datos enriquecidos
        String path = System.getProperty("user.dir") + "/uploads/" + bpmnFileName;
        Map<String, List<FormFieldData>> enrichedFormData = bpmnService.getEnrichedUserTaskFormFields(path);
        System.out.println("📦 enrichedFormData: " + enrichedFormData);

        // 3. Generar clases DTO y entidad
        dtoGeneratorService.generateDtos(externalPath);
        entityGeneratorService.generateEntities(externalPath);
        repositoryGeneratorService.generateRepositories(externalPath);
        serviceGeneratorService.generateServices(externalPath);
        controllerGeneratorService.generateControllers(externalPath);
        viewGeneratorService.generateViews(externalPath);
        restConfigGeneratorService.generateRestConfig(externalPath);

        // 4. Copiar cada subcarpeta de 'dbs' por separado al mismo nivel que 'cliente' y 'BPMN-Engine'
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
