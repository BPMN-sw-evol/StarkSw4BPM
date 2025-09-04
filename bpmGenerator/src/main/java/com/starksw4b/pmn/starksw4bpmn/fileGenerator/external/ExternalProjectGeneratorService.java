package com.starksw4b.pmn.starksw4bpmn.fileGenerator.external;

import com.starksw4b.pmn.starksw4bpmn.fileGenerator.external.generator.PropertiesFileGeneratorService;
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
            "&packageName=com.form.client" +
            "&dependencies=web,data-jpa,postgresql,thymeleaf";

    private final PropertiesFileGeneratorService propertiesFileGeneratorService;

    public ExternalProjectGeneratorService(PropertiesFileGeneratorService propertiesFileGeneratorService) {
        this.propertiesFileGeneratorService = propertiesFileGeneratorService;
    }

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

        // Descargar el zip temporal-
        Path zipPath = targetDir.resolve("external.zip");
        try (InputStream in = new URL(DOWNLOAD_URL).openStream()) {
            Files.copy(in, zipPath, StandardCopyOption.REPLACE_EXISTING);
        }

        System.out.println("📦 Proyecto ZIP descargado en: " + zipPath);

        // Descomprimir ZIP
        unzip(zipPath.toString(), parentDir.toString());

        // 🔧 Limpiar archivos que no necesitamos
        cleanGeneratedProject(targetDir);


        // Borrar ZIP
        Files.delete(zipPath);

        // ⚠️ Agregar esta línea
        ensureMainApplicationClass(targetDir);

        // ⚙ Sobrescribir application.properties
        propertiesFileGeneratorService.generateApplicationProperties(targetDir);

        // ⚙ Sobrescribir application.properties
        propertiesFileGeneratorService.generateApplicationProperties(targetDir);

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

    private void cleanGeneratedProject(Path targetDir) throws IOException {
        // Eliminar clases basura
        Files.walk(targetDir)
                .filter(path -> {
                    String fileName = path.getFileName().toString();
                    return fileName.equals("ClienteApplication.java")
                            || fileName.equals("WebConfig.java")
                            || fileName.equals("RestConfig.java")
                            || fileName.equals("CamundaService.java")
                            || fileName.equals("FormularioService.java");
                })
                .forEach(path -> {
                    try {
                        Files.deleteIfExists(path);
                        System.out.println("🗑️ Archivo eliminado: " + path);
                    } catch (IOException e) {
                        System.err.println("⚠️ Error al eliminar: " + path);
                    }
                });

        // Eliminar archivos HTML innecesarios
        Path templatesDir = targetDir.resolve("src/main/resources/templates/formulario");
        if (Files.exists(templatesDir)) {
            Files.walk(templatesDir)
                    .filter(path -> {
                        String fileName = path.getFileName().toString();
                        return fileName.equals("form.html") || fileName.equals("list.html");
                    })
                    .forEach(path -> {
                        try {
                            Files.deleteIfExists(path);
                            System.out.println("🗑️ Archivo eliminado: " + path);
                        } catch (IOException e) {
                            System.err.println("⚠️ Error al eliminar: " + path);
                        }
                    });
        }
    }

    private void ensureMainApplicationClass(Path targetDir) throws IOException {
        Path mainClassPath = targetDir.resolve("src/main/java/com/form/client/ClienteApplication.java");
        Files.createDirectories(mainClassPath.getParent());

        String mainClassContent = """
        package com.form.client;

        import org.springframework.boot.SpringApplication;
        import org.springframework.boot.autoconfigure.SpringBootApplication;

        @SpringBootApplication
        public class ClienteApplication {

            public static void main(String[] args) {
                SpringApplication.run(ClienteApplication.class, args);
            }
        }
        """;

        Files.writeString(mainClassPath, mainClassContent);
    }


}
