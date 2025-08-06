package com.starksw4b.pmn.starksw4bpmn.fileGenerator.external.generator;

import com.starksw4b.pmn.starksw4bpmn.model.FormFieldData;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;

@Service
public class ControllerGeneratorService {

    public void generateControllers(Path projectPath, Map<String, List<FormFieldData>> formFields) throws IOException {
        Path basePackage = findBasePackage(projectPath);
        Path controllerDir = basePackage.resolve("controller");
        Files.createDirectories(controllerDir);

        for (Map.Entry<String, List<FormFieldData>> entry : formFields.entrySet()) {
            String taskName = entry.getKey().replaceAll("\\s+", "");
            String className = taskName + "Controller";
            String entityName = taskName;
            String serviceName = taskName + "Service";

            String controllerContent = String.format("""
                package %s.controller;

                import %s.model.%s;
                import %s.service.%s;
                import org.springframework.beans.factory.annotation.Autowired;
                import org.springframework.web.bind.annotation.*;

                import java.util.List;

                @RestController
                @RequestMapping("/%s")
                public class %s {

                    private final %s %sService;

                    @Autowired
                    public %s(%s %sService) {
                        this.%sService = %sService;
                    }

                    @GetMapping
                    public List<%s> getAll() {
                        return %sService.findAll();
                    }

                    @PostMapping
                    public %s save(@RequestBody %s data) {
                        return %sService.save(data);
                    }
                }
                """,
                    getBasePackage(basePackage),
                    getBasePackage(basePackage), entityName,
                    getBasePackage(basePackage), serviceName,
                    entityName.toLowerCase(), className,
                    serviceName, decapitalize(taskName),
                    className, serviceName, decapitalize(taskName),
                    decapitalize(taskName), decapitalize(taskName),
                    entityName, decapitalize(taskName),
                    entityName, entityName, decapitalize(taskName)
            );

            Files.writeString(controllerDir.resolve(className + ".java"), controllerContent);
            System.out.println("🌐 Controlador generado: " + className + ".java");
        }
    }

    private Path findBasePackage(Path projectPath) throws IOException {
        return Files.walk(projectPath.resolve("src/main/java"))
                .filter(path -> path.toFile().getName().equals("ClienteApplication.java"))
                .findFirst()
                .map(Path::getParent)
                .orElseThrow(() -> new IOException("No se encontró la clase principal del proyecto externo"));
    }

    private String getBasePackage(Path basePath) {
        return basePath.subpath(basePath.getNameCount() - 3, basePath.getNameCount())
                .toString().replace(File.separator, ".");
    }

    private String decapitalize(String str) {
        return str.substring(0, 1).toLowerCase() + str.substring(1);
    }
}
