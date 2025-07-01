package com.starksw4b.pmn.starksw4bpmn.fileGenerator;

import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Service
public class SendTaskGeneratorService {

    private static final String BASE_PATH = "../generatedProjects/generatedproject/BPM-Engine/src/main/java/com/example/workflow";

    public void generateSendTaskClass(String className) throws IOException {
        String content = generateClassContent(className);
        Path outputPath = Paths.get(BASE_PATH, className + ".java");

        Files.createDirectories(outputPath.getParent());
        Files.writeString(outputPath, content);

        System.out.println("Clase generada para SendTask en: " + outputPath.toAbsolutePath());
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
                "        LOGGER.info(\"SendTask ejecutado: simulando envío de mensaje o integración externa\");\n\n" +
                "        String destinatario = (String) execution.getVariable(\"destinatario\");\n" +
                "        LOGGER.info(\"Enviando mensaje a: {}\", destinatario);\n" +
                "        execution.setVariable(\"mensajeEnviado\", true);\n" +
                "    }\n" +
                "}\n";
    }
}
