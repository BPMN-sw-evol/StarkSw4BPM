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
public class RepositoryGeneratorService {

    public void generateRepositories(Path externalProjectPath, Map<String, List<FormFieldData>> formFieldsByTask) throws IOException {
        Path basePackage = Files.walk(externalProjectPath.resolve("src/main/java"))
                .filter(path -> path.toFile().getName().equals("ClienteApplication.java"))
                .findFirst()
                .map(Path::getParent)
                .orElseThrow(() -> new IOException("No se encontró la clase principal"));

        Path repositoryDir = basePackage.resolve("repository");
        Files.createDirectories(repositoryDir);

        for (String taskName : formFieldsByTask.keySet()) {
            String className = taskName.replaceAll("\\s+", "");
            String repositoryName = className + "Repository";

            Path repositoryFile = repositoryDir.resolve(repositoryName + ".java");
            String packageName = basePackageToPackageName(basePackage);

            String content = String.format("""
                package %s.repository;

                import %s.entity.%s;
                import org.springframework.data.jpa.repository.JpaRepository;
                import org.springframework.stereotype.Repository;

                @Repository
                public interface %s extends JpaRepository<%s, Long> {
                }
                """,
                    packageName,
                    packageName,
                    className,
                    repositoryName,
                    className
            );

            Files.writeString(repositoryFile, content);
            System.out.println("✔ Repositorio generado: " + repositoryFile);
        }
    }

    private String basePackageToPackageName(Path basePath) {
        return basePath.subpath(basePath.getNameCount() - 3, basePath.getNameCount())
                .toString().replace(File.separator, ".");
    }
}
