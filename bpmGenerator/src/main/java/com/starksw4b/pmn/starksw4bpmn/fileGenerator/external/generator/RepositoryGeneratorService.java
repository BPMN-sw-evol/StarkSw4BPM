package com.starksw4b.pmn.starksw4bpmn.fileGenerator.external.generator;

import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.nio.file.*;

@Service
public class RepositoryGeneratorService {

    private static final String PACKAGE_NAME = "com.form.client.repository";

    public void generateRepositories(Path externalProjectPath) throws IOException {
        Path repositoryDir = getRepositoryDirectory(externalProjectPath);
        Files.createDirectories(repositoryDir);

        Path repositoryFile = repositoryDir.resolve("FormularioRepository.java");

        String content = """
                package com.form.client.repository;

                import com.form.client.model.Formulario;
                import org.springframework.data.jpa.repository.JpaRepository;
                import java.util.Optional;

                public interface FormularioRepository extends JpaRepository<Formulario, Long> {
                    Optional<Formulario> findByNombre(String nombre);
                }
                """;

        Files.writeString(repositoryFile, content);
        System.out.println("📁 Repositorio generado: " + repositoryFile.getFileName());
    }

    private Path getRepositoryDirectory(Path externalProjectPath) {
        return externalProjectPath.resolve("src/main/java/com/form/client/repository".replace(".", File.separator));
    }
}
