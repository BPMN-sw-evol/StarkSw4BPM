package com.starksw4b.pmn.starksw4bpmn.service;

import org.springframework.stereotype.Service;
import java.io.*;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Comparator;

@Service
public class BpmEngineGeneratorService {

    // Ruta donde se encuentra el proyecto plantilla (carpeta a copiar)
    private static final String TEMPLATE_DIR = Paths.get("..", "generatedProjects", "ProjModelToFollow").toString();  // Subir un nivel y acceder a la plantilla

    // Ruta donde se generarán los nuevos proyectos dentro de 'generatedProjects'
    private static final String GENERATED_DIR = Paths.get("../generatedProjects").toString();  // Generar los proyectos dentro de 'generatedProjects'

    public Path generateProjectFromTemplate() throws IOException {
        // Usar un nombre fijo para el directorio "generatedproject"
        String newProjectDir = GENERATED_DIR + "/BPM-Engine";
        Path outputDir = Paths.get(newProjectDir);

        // Eliminar cualquier proyecto anterior llamado "generatedproject"
        deleteExistingProject(outputDir);

        // Asegurarse de que la ruta de destino exista
        if (!Files.exists(outputDir)) {
            Files.createDirectories(outputDir);  // Crear la carpeta si no existe
            System.out.println("Directorio de salida creado: " + outputDir.toAbsolutePath());
        }

        // Copiar la plantilla a la nueva ubicación
        copyTemplateToNewProject(outputDir);

        // Verificar si el directorio realmente existe antes de retornarlo
        if (!Files.exists(outputDir) || !Files.isDirectory(outputDir)) {
            throw new IOException("El directorio del proyecto generado no existe o no es un directorio válido.");
        }

        return outputDir;
    }

    // Método para eliminar cualquier proyecto anterior en la carpeta "generatedproject"
    private void deleteExistingProject(Path projectDir) throws IOException {
        if (Files.exists(projectDir)) {
            System.out.println("Eliminando el proyecto existente...");
            Files.walk(projectDir)
                    .sorted(Comparator.reverseOrder())  // Orden inverso para eliminar archivos antes que carpetas
                    .map(Path::toFile)
                    .forEach(File::delete);
        }
    }

    // Método para copiar la plantilla de proyecto a la nueva ubicación
    private void copyTemplateToNewProject(Path outputDir) throws IOException {
        Path templateDir = Paths.get(TEMPLATE_DIR);  // Usamos la ruta absoluta para acceder a 'generatedProjects/platilla'

        // Verificar si la plantilla existe
        if (!Files.exists(templateDir) || !Files.isDirectory(templateDir)) {
            throw new IOException("La plantilla no existe o no es un directorio válido.");
        }

        // Copiar la plantilla (directorio completo) a la nueva ubicación, excluyendo archivos no deseados
        Files.walkFileTree(templateDir, new SimpleFileVisitor<Path>() {
            @Override
            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                // Excluir archivos y carpetas no deseados (por ejemplo, .idea y .gitignore)
                if (file.toString().contains(".idea") || file.toString().contains(".gitignore")) {
                    return FileVisitResult.SKIP_SUBTREE;  // Excluir todo el archivo (o directorio)
                }

                // Asegurarse de que los directorios de destino existan
                Path destinationFile = outputDir.resolve(templateDir.relativize(file));
                Files.createDirectories(destinationFile.getParent());  // Crea el directorio si no existe

                Files.copy(file, destinationFile, StandardCopyOption.REPLACE_EXISTING);
                return FileVisitResult.CONTINUE;
            }

            public FileVisitResult visitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
                // Excluir directorios no deseados (por ejemplo, .idea)
                if (dir.toString().contains(".idea")) {
                    return FileVisitResult.SKIP_SUBTREE;  // Excluir todo el directorio y su contenido
                }

                Path destinationDir = outputDir.resolve(templateDir.relativize(dir));
                Files.createDirectories(destinationDir);  // Crear el directorio de destino
                return FileVisitResult.CONTINUE;
            }
        });
    }
}
