package com.starksw4b.pmn.starksw4bpmn.fileGenerator.internal;

import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Service
public class DelegateExpressionGeneratorService {

    private static final String BASE_PATH = "../generatedProjects/generatedproject/BPM-Engine/src/main/java/com/example/workflow";

    public void generateDelegateExpressionClass(String className) throws IOException {
        String content = generateClassContent(className);
        Path outputPath = Paths.get(BASE_PATH, className + ".java");

        Files.createDirectories(outputPath.getParent());
        Files.writeString(outputPath, content);

        System.out.println("Clase generada para Delegate Expression en: " + outputPath.toAbsolutePath());
    }

    private String generateClassContent(String className) {
        String beanName = Character.toLowerCase(className.charAt(0)) + className.substring(1);

        return "package com.example.workflow;\n\n" +
                "import org.camunda.bpm.engine.delegate.DelegateExecution;\n" +
                "import org.camunda.bpm.engine.delegate.JavaDelegate;\n" +
                "import org.slf4j.Logger;\n" +
                "import org.slf4j.LoggerFactory;\n" +
                "import org.springframework.stereotype.Component;\n\n" +
                "@Component(\"" + beanName + "\")\n" +
                "public class " + className + " implements JavaDelegate {\n\n" +
                "    private static final Logger LOGGER = LoggerFactory.getLogger(" + className + ".class);\n\n" +
                "    @Override\n" +
                "    public void execute(DelegateExecution execution) throws Exception {\n" +
                "        LOGGER.info(\"Delegate Expression ejecutada: " + beanName + "\");\n\n" +
                "        String solicitudId = (String) execution.getVariable(\"solicitudId\");\n" +
                "        LOGGER.info(\"Procesando solicitud ID: {}\", solicitudId);\n" +
                "        execution.setVariable(\"procesado\", true);\n" +
                "    }\n" +
                "}\n";
    }
}
