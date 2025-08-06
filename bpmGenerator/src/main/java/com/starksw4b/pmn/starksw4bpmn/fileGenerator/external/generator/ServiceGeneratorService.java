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
public class ServiceGeneratorService {

    public void generateServices(Path projectPath, Map<String, List<FormFieldData>> formFields) throws IOException {
        Path basePackage = findBasePackage(projectPath);
        Path serviceDir = basePackage.resolve("service");
        Files.createDirectories(serviceDir);

        for (Map.Entry<String, List<FormFieldData>> entry : formFields.entrySet()) {
            String taskName = entry.getKey().replaceAll("\\s+", "");
            String className = taskName + "Service";
            String entityName = taskName;
            String repositoryName = taskName + "Repository";

            String serviceContent = String.format("""
                package %s.service;

                import %s.model.%s;
                import %s.repository.%s;
                import org.springframework.beans.factory.annotation.Autowired;
                import org.springframework.stereotype.Service;

                import java.util.List;

                @Service
                public class %s {

                    private final %s %sRepository;

                    @Autowired
                    public %s(%s %sRepository) {
                        this.%sRepository = %sRepository;
                    }

                    public List<%s> findAll() {
                        return %sRepository.findAll();
                    }

                    public %s save(%s data) {
                        return %sRepository.save(data);
                    }
                }
                """,
                    getBasePackage(basePackage),
                    getBasePackage(basePackage), entityName,
                    getBasePackage(basePackage), repositoryName,
                    className,
                    repositoryName, decapitalize(taskName),
                    className, repositoryName, decapitalize(taskName),
                    decapitalize(taskName), decapitalize(taskName),
                    entityName,
                    decapitalize(taskName),
                    entityName, decapitalize(taskName),
                    decapitalize(taskName)
            );

            Files.writeString(serviceDir.resolve(className + ".java"), serviceContent);
            System.out.println("🛠 Servicio generado: " + className + ".java");
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
