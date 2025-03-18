package com.starksw4b.pmn.starksw4bpmn.controller;

import com.starksw4b.pmn.starksw4bpmn.service.SpringBootGeneratorService;
import com.starksw4b.pmn.starksw4bpmn.service.FileCopyService;
import com.starksw4b.pmn.starksw4bpmn.service.POMService;
import com.starksw4b.pmn.starksw4bpmn.service.PropertiesService;
import com.starksw4b.pmn.starksw4bpmn.service.MainClassService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.nio.file.Path;

@RestController
@RequestMapping("/generate")
public class SpringBootGeneratorController {

    private final SpringBootGeneratorService generatorService;
    private final FileCopyService fileCopyService;
    private final POMService pomService;
    private final PropertiesService propertiesService;
    private final MainClassService mainClassService;

    // Inyectando los servicios en el constructor
    public SpringBootGeneratorController(SpringBootGeneratorService generatorService, FileCopyService fileCopyService,
                                         POMService pomService, PropertiesService propertiesService, MainClassService mainClassService) {
        this.generatorService = generatorService;
        this.fileCopyService = fileCopyService;
        this.pomService = pomService;
        this.propertiesService = propertiesService;
        this.mainClassService = mainClassService;
    }

    @GetMapping("/springboot")
    public ResponseEntity<String> generateSpringBootProject() {
        try {
            // Generar y extraer el proyecto
            Path projectPath = generatorService.generateAndExtractProject();

            // Copiar el archivo BPMN al proyecto
            fileCopyService.copyBpmnFile();

            // Modificar el pom.xml del proyecto generado
            pomService.modifyPomXml();

            // Modificar el application.properties del proyecto generado
            propertiesService.modifyApplicationProperties();

            // Modificar el Main Class (DemoApplication.java) del proyecto generado
            mainClassService.modifyMainClass();

            return ResponseEntity.ok("Proyecto generado en: " + projectPath.toAbsolutePath() +
                    " y archivo BPMN copiado correctamente. pom.xml, application.properties y MainClass actualizados.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error al generar el proyecto, copiar el BPMN, modificar el pom.xml, application.properties o MainClass: " + e.getMessage());
        }
    }
}
