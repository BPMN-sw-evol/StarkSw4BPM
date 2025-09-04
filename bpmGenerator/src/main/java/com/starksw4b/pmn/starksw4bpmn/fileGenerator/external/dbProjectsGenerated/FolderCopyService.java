package com.starksw4b.pmn.starksw4bpmn.fileGenerator.external.dbProjectsGenerated;

import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.*;

@Service
public class FolderCopyService {

    public void copyFolder(Path sourceDir, Path targetDir) throws IOException {
        if (!Files.exists(sourceDir)) {
            throw new IOException("No se encontró la carpeta fuente: " + sourceDir);
        }

        Files.walk(sourceDir).forEach(sourcePath -> {
            try {
                Path targetPath = targetDir.resolve(sourceDir.relativize(sourcePath));
                if (Files.isDirectory(sourcePath)) {
                    Files.createDirectories(targetPath);
                } else {
                    Files.copy(sourcePath, targetPath, StandardCopyOption.REPLACE_EXISTING);
                }
            } catch (IOException e) {
                throw new RuntimeException("Error al copiar: " + sourcePath + " → " + e.getMessage(), e);
            }
        });

        System.out.println("📂 Carpeta copiada: " + sourceDir.getFileName() + " → " + targetDir);
    }
}
