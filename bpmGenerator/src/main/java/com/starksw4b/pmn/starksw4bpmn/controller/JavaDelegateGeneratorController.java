package com.starksw4b.pmn.starksw4bpmn.controller;

import com.starksw4b.pmn.starksw4bpmn.facade.SystemGeneratorFacade;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.core.io.Resource;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

@RestController
@RequestMapping("/api/generator")
public class JavaDelegateGeneratorController {

    private final SystemGeneratorFacade generatorFacade;

    @Autowired
    public JavaDelegateGeneratorController(SystemGeneratorFacade generatorFacade) {
        this.generatorFacade = generatorFacade;
    }

    @PostMapping("/generate-delegate")
    public String generateDelegateClass(@RequestParam String className) {
        try {
            generatorFacade.generarClaseJavaDelegate(className);
            return "Clase JavaDelegate generada correctamente: " + className + ".java";
        } catch (IOException e) {
            return "Error al generar la clase JavaDelegate: " + e.getMessage();
        }
    }

    @PostMapping("/generate-java-class")
    public String generateJavaClass(@RequestParam String className) {
        try {
            generatorFacade.generarClaseJavaClass(className);
            return "Clase JavaClass generada correctamente: " + className + ".java";
        } catch (IOException e) {
            return "Error al generar la clase JavaClass: " + e.getMessage();
        }
    }

    @PostMapping("/generate-delegate-expression")
    public String generateDelegateExpressionClass(@RequestParam String className) {
        try {
            generatorFacade.generarClaseDelegateExpression(className);
            return "Clase DelegateExpression generada correctamente: " + className + ".java";
        } catch (IOException e) {
            return "Error al generar la clase DelegateExpression: " + e.getMessage();
        }
    }

    @PostMapping("/generate-send-task")
    public String generateSendTaskClass(@RequestParam String className) {
        try {
            generatorFacade.generarClaseSendTask(className);
            return "Clase SendTask generada correctamente: " + className + ".java";
        } catch (IOException e) {
            return "Error al generar la clase SendTask: " + e.getMessage();
        }
    }

    @PostMapping("/generate-from-config")
    public ResponseEntity<String> generateFromConfig() {
        try {
            generatorFacade.generarClasesDesdeConfiguracion();
            return ResponseEntity.ok("Clases generadas desde configuración.");
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error al generar clases desde configuración: " + e.getMessage());
        }
    }

    @GetMapping("/download-zip")
    public ResponseEntity<Resource> downloadGeneratedProjectZip() {
        try {
            Path rootDir = Paths.get("../generatedProjects/generatedProject");
            Path tempZip = Files.createTempFile("projects-", ".zip");

            try (FileOutputStream fos = new FileOutputStream(tempZip.toFile());
                 ZipOutputStream zos = new ZipOutputStream(fos)) {

                Files.walk(rootDir)
                        .filter(path -> !Files.isDirectory(path))
                        .forEach(path -> {
                            try {
                                Path relativePath = rootDir.getFileName().resolve(rootDir.relativize(path));
                                zos.putNextEntry(new ZipEntry(relativePath.toString()));
                                Files.copy(path, zos);
                                zos.closeEntry();
                            } catch (IOException e) {
                                throw new RuntimeException("Error al comprimir: " + path, e);
                            }
                        });
            }

            InputStreamResource resource = new InputStreamResource(new FileInputStream(tempZip.toFile()));

            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=generated-system.zip")
                    .contentType(MediaType.APPLICATION_OCTET_STREAM)
                    .body(resource);

        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }
}
