package com.starksw4b.pmn.starksw4bpmn.service;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpEntity;

import java.io.*;
import java.nio.file.*;
import java.util.zip.*;
import java.util.Comparator;

@Service
public class SpringBootGeneratorService {

    private static final String INITIALIZR_URL = "https://start.spring.io/starter.zip"
            + "?type=maven-project&language=java&bootVersion=3.3.0"
            + "&dependencies=web,data-jpa,mysql";

    // Ruta donde se generará el proyecto
    private static final String PROJECT_DIR = "generated-project";

    public Path generateAndExtractProject() throws IOException {
        // Eliminar cualquier proyecto anterior en la carpeta
        deleteExistingProject(PROJECT_DIR);

        RestTemplate restTemplate = new RestTemplate();

        HttpHeaders headers = new HttpHeaders();
        HttpEntity<String> entity = new HttpEntity<>(headers);

        // Hacer la solicitud GET a Initializr
        ResponseEntity<ByteArrayResource> response = restTemplate.exchange(
                INITIALIZR_URL, HttpMethod.GET, entity, ByteArrayResource.class);

        if (!response.getStatusCode().is2xxSuccessful() || response.getBody() == null) {
            throw new IOException("Error al descargar el proyecto de Initializr");
        }

        // Guardar el archivo ZIP en el servidor
        Path zipFilePath = Path.of("generated-project.zip");
        Files.write(zipFilePath, response.getBody().getByteArray(), StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);

        System.out.println("Proyecto generado exitosamente: " + zipFilePath.toAbsolutePath());

        // Ruta de destino para descomprimir
        Path outputDir = Path.of(PROJECT_DIR);

        // Descomprimir el archivo ZIP en el directorio de destino
        unzipAndDelete(zipFilePath.toString(), outputDir.toString());

        // Verificar si el archivo extraído realmente existe antes de retornarlo
        if (!Files.exists(outputDir) || !Files.isDirectory(outputDir)) {
            throw new IOException("El directorio extraído no existe o no es un directorio válido: " + outputDir);
        }

        return outputDir;
    }

    private void deleteExistingProject(String projectDir) throws IOException {
        Path projectPath = Paths.get(projectDir);

        // Si el directorio ya existe, eliminarlo recursivamente
        if (Files.exists(projectPath)) {
            System.out.println("Eliminando el proyecto existente...");
            Files.walk(projectPath)
                    .sorted(Comparator.reverseOrder())  // Orden inverso para eliminar archivos antes que carpetas
                    .map(Path::toFile)
                    .forEach(File::delete);
        }
    }

    private void unzipAndDelete(String zipFilePath, String destDirectory) throws IOException {
        File zipFile = new File(zipFilePath);
        File destDir = new File(destDirectory);

        // Crear el directorio de destino si no existe
        if (!destDir.exists()) {
            destDir.mkdirs();
        }

        // Descomprimir el archivo
        try (ZipInputStream zipInputStream = new ZipInputStream(new FileInputStream(zipFile))) {
            ZipEntry entry;
            while ((entry = zipInputStream.getNextEntry()) != null) {
                File newFile = new File(destDirectory, entry.getName());

                // Crear directorios si es necesario
                if (entry.isDirectory()) {
                    newFile.mkdirs();
                } else {
                    // Asegurar que el directorio padre existe
                    new File(newFile.getParent()).mkdirs();

                    // Escribir el archivo descomprimido
                    try (FileOutputStream fos = new FileOutputStream(newFile)) {
                        byte[] buffer = new byte[1024];
                        int len;
                        while ((len = zipInputStream.read(buffer)) > 0) {
                            fos.write(buffer, 0, len);
                        }
                    }
                }
                zipInputStream.closeEntry();
            }
        }

        // Eliminar el archivo ZIP después de descomprimirlo
        if (zipFile.delete()) {
            System.out.println("Archivo ZIP eliminado: " + zipFilePath);
        } else {
            System.err.println("No se pudo eliminar el archivo ZIP.");
        }
    }
}
