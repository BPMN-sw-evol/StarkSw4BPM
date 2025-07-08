package com.starksw4b.pmn.starksw4bpmn.fileGenerator.external;

import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

@Service
public class FormClassGeneratorService {

    public void generateFormController(Path externalProjectPath, String className) throws IOException {
        // Buscar el paquete base dinámicamente
        Path basePackage = Files.walk(externalProjectPath.resolve("src/main/java"))
                .filter(path -> path.toFile().getName().equals("ClienteApplication.java"))
                .findFirst()
                .map(path -> path.getParent()) // com/form/cliente
                .orElseThrow(() -> new IOException("No se encontró la clase principal para detectar el paquete base"));

        Path controllerDir = basePackage.resolve("controller");
        Files.createDirectories(controllerDir);

        String finalClassName = className + "Controller";
        Path classPath = controllerDir.resolve(finalClassName + ".java");

        String classContent = String.format("""
        package %s.controller;

        import org.springframework.web.bind.annotation.GetMapping;
        import org.springframework.web.bind.annotation.RestController;

        @RestController
        public class %s {

            @GetMapping("/form/%s")
            public String hello() {
                return "Formulario para %s";
            }
        }
        """, basePackageToPackageName(basePackage), finalClassName, className.toLowerCase(), className.replaceAll("([A-Z])", " $1").trim());

        Files.writeString(classPath, classContent);
        System.out.println("✔ Clase de formulario generada: " + classPath);
    }

    // Convierte com/form/cliente → com.form.cliente
    private String basePackageToPackageName(Path basePath) {
        return basePath.subpath(basePath.getNameCount() - 3, basePath.getNameCount())
                .toString().replace(File.separator, ".");
    }
}
