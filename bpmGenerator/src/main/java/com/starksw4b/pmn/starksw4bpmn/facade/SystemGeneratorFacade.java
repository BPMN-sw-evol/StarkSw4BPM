package com.starksw4b.pmn.starksw4bpmn.facade;

import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Comparator;

@Service
public class SystemGeneratorFacade {

    private final CamundaEngineFacade camundaEngineFacade;
    private final ExternalSystemFacade externalSystemFacade;

    public void generarClaseJavaDelegate(String className) throws IOException {
        camundaEngineFacade.generarClaseJavaDelegate(className);
    }

    public void generarClaseDelegateExpression(String className) throws IOException {
        camundaEngineFacade.generarClaseDelegateExpression(className);
    }

    public void generarClaseSendTask(String className) throws IOException {
        camundaEngineFacade.generarClaseSendTask(className);
    }

    public SystemGeneratorFacade(CamundaEngineFacade camundaEngineFacade,
                                 ExternalSystemFacade externalSystemFacade) {
        this.camundaEngineFacade = camundaEngineFacade;
        this.externalSystemFacade = externalSystemFacade;
    }

    public void generarClasesDesdeConfiguracion() throws IOException {
        Path proyectoCamunda = camundaEngineFacade.generarProyectoCamunda();

        // Obtener nombre del BPMN más reciente
        File folder = new File(System.getProperty("user.dir") + File.separator + "uploads");
        File[] archivos = folder.listFiles((dir, name) -> name.endsWith(".bpmn"));
        if (archivos == null || archivos.length == 0) {
            throw new IOException("No se encontró ningún archivo BPMN en uploads/");
        }

        File archivoMasReciente = Arrays.stream(archivos)
                .max(Comparator.comparingLong(File::lastModified))
                .orElseThrow();

        String bpmnFileName = archivoMasReciente.getName();

        // Llamar a generación del sistema externo con ese BPMN
        try {
            externalSystemFacade.generarSistemaExterno(proyectoCamunda, bpmnFileName);
        } catch (Exception e) {
            throw new IOException("Error generando sistema externo: " + e.getMessage(), e);
        }
    }

}
