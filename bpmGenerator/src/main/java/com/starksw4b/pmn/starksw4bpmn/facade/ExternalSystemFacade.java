package com.starksw4b.pmn.starksw4bpmn.facade;

import com.starksw4b.pmn.starksw4bpmn.fileGenerator.external.generator.*;
import com.starksw4b.pmn.starksw4bpmn.fileGenerator.external.ExternalProjectGeneratorService;
import com.starksw4b.pmn.starksw4bpmn.model.FormFieldData;
import com.starksw4b.pmn.starksw4bpmn.service.BpmnService;
import com.starksw4b.pmn.starksw4bpmn.service.TaskService;
import org.springframework.stereotype.Service;

import java.io.IOException;
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

    public ExternalSystemFacade(ExternalProjectGeneratorService externalProjectGeneratorService,
                                DtoGeneratorService dtoGeneratorService,
                                EntityGeneratorService entityGeneratorService,
                                BpmnService bpmnService,
                                RepositoryGeneratorService repositoryGeneratorService,
                                ServiceGeneratorService serviceGeneratorService,
                                ControllerGeneratorService controllerGeneratorService,
                                ViewGeneratorService viewGeneratorService,
                                RestConfigGeneratorService restConfigGeneratorService) {
        this.externalProjectGeneratorService = externalProjectGeneratorService;
        this.dtoGeneratorService = dtoGeneratorService;
        this.entityGeneratorService = entityGeneratorService;
        this.bpmnService = bpmnService;
        this.repositoryGeneratorService = repositoryGeneratorService;
        this.serviceGeneratorService = serviceGeneratorService;
        this.controllerGeneratorService = controllerGeneratorService;
        this.viewGeneratorService = viewGeneratorService;
        this.restConfigGeneratorService = restConfigGeneratorService;
    }

    public void generarSistemaExterno(Path proyectoBase, String bpmnFileName) throws Exception {
        // 1. Crear el proyecto externo (cliente/)
        Path externalPath = externalProjectGeneratorService.generateExternalFormProject(proyectoBase.getParent());

        // 2. Leer archivo BPMN para extraer datos enriquecidos
        String path = System.getProperty("user.dir") + "/uploads/" + bpmnFileName;
        Map<String, List<FormFieldData>> enrichedFormData = bpmnService.getEnrichedUserTaskFormFields(path);

        // 3. Generar clases DTO y entidad
        dtoGeneratorService.generateDtos(externalPath, enrichedFormData);
        entityGeneratorService.generateEntities(externalPath, enrichedFormData);
        repositoryGeneratorService.generateRepositories(externalPath, enrichedFormData);
        serviceGeneratorService.generateServices(externalPath, enrichedFormData);
        controllerGeneratorService.generateControllers(externalPath, enrichedFormData);
        viewGeneratorService.generateViews(externalPath, enrichedFormData);
        restConfigGeneratorService.generateRestConfig(externalPath);

        System.out.println("✅ Proyecto externo enriquecido correctamente.");
    }
}
