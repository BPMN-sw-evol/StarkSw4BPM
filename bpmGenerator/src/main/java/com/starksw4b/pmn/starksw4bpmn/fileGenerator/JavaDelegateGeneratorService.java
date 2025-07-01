package com.starksw4b.pmn.starksw4bpmn.fileGenerator;

import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Service
public class JavaDelegateGeneratorService {

    private static final String BASE_PATH = "../generatedProjects/generatedproject/BPM-Engine/src/main/java/com/example/workflow";

    public void generateJavaDelegateClass(String className) throws IOException {
        String content = generateClassContent(className);
        Path outputPath = Paths.get(BASE_PATH, className + ".java");

        // Crear directorios si no existen
        Files.createDirectories(outputPath.getParent());

        // Escribir archivo
        Files.writeString(outputPath, content);

        System.out.println("Clase generada en: " + outputPath.toAbsolutePath());
    }

    private String generateClassContent(String className) {
        return "package com.example.workflow;\n\n" +
                "import org.camunda.bpm.engine.delegate.DelegateExecution;\n" +
                "import org.camunda.bpm.engine.delegate.JavaDelegate;\n" +
                "import org.slf4j.Logger;\n" +
                "import org.slf4j.LoggerFactory;\n" +
                "import org.springframework.stereotype.Component;\n\n" +
                "@Component\n" +
                "public class " + className + " implements JavaDelegate {\n\n" +
                "    private static final Logger LOGGER = LoggerFactory.getLogger(" + className + ".class);\n\n" +
                "    @Override\n" +
                "    public void execute(DelegateExecution execution) throws Exception {\n" +
                "        LOGGER.info(\"Ejecutando: Register Guarantee Analysis Request\");\n\n" +
                "        String solicitudId = (String) execution.getVariable(\"solicitudId\");\n" +
                "        LOGGER.info(\"Registrando solicitud de análisis de garantía para ID: {}\", solicitudId);\n" +
                "        execution.setVariable(\"garantiaRegistrada\", true);\n" +
                "    }\n" +
                "}\n";
    }
}