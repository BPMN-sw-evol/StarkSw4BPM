package com.starksw4b.pmn.starksw4bpmn.fileGenerator.external;

import org.springframework.stereotype.Service;
import org.springframework.util.FileSystemUtils;

import java.io.*;
import java.net.URL;
import java.nio.file.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

@Service
public class ExternalProjectGeneratorService {

    private static final String DOWNLOAD_URL = "https://start.spring.io/starter.zip" +
            "?type=maven-project" +
            "&language=java" +
            "&bootVersion=3.5.3" +
            "&baseDir=cliente" +
            "&groupId=com.form" +
            "&artifactId=cliente" +
            "&name=cliente" +
            "&description=Demo%20project%20for%20Spring%20Boot" +
            "&packageName=com.form.cliente" +
            "&dependencies=web,data-jpa,postgresql,thymeleaf";

    public Path generateExternalFormProject(Path parentDir) throws IOException {
        System.out.println("🔧 Generando proyecto externo...");

        Path targetDir = parentDir.resolve("cliente");

        // 🔥 Eliminar carpeta existente si ya hay un proyecto anterior
        if (Files.exists(targetDir)) {
            System.out.println("⚠ Eliminando proyecto anterior: " + targetDir);
            FileSystemUtils.deleteRecursively(targetDir);
        }

        // Crear directorio limpio
        Files.createDirectories(targetDir);

        // Descargar el zip temporal
        Path zipPath = targetDir.resolve("external.zip");
        try (InputStream in = new URL(DOWNLOAD_URL).openStream()) {
            Files.copy(in, zipPath, StandardCopyOption.REPLACE_EXISTING);
        }

        System.out.println("📦 Proyecto ZIP descargado en: " + zipPath);

        // Descomprimir ZIP
        unzip(zipPath.toString(), parentDir.toString());

        // Borrar ZIP
        Files.delete(zipPath);

        System.out.println("✅ Proyecto externo generado en: " + targetDir.toAbsolutePath());
        return targetDir;
    }

    private void unzip(String zipFilePath, String destDir) throws IOException {
        try (ZipInputStream zipIn = new ZipInputStream(new FileInputStream(zipFilePath))) {
            ZipEntry entry = zipIn.getNextEntry();

            while (entry != null) {
                Path filePath = Paths.get(destDir, entry.getName());
                if (!entry.isDirectory()) {
                    Files.createDirectories(filePath.getParent());
                    try (BufferedOutputStream bos = new BufferedOutputStream(Files.newOutputStream(filePath))) {
                        byte[] buffer = new byte[4096];
                        int len;
                        while ((len = zipIn.read(buffer)) > 0) {
                            bos.write(buffer, 0, len);
                        }
                    }
                } else {
                    Files.createDirectories(filePath);
                }
                zipIn.closeEntry();
                entry = zipIn.getNextEntry();
            }
        }
    }
}
