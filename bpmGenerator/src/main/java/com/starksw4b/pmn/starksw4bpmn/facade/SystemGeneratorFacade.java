package com.starksw4b.pmn.starksw4bpmn.facade;

import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Path;

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
        externalSystemFacade.generarSistemaExterno(proyectoCamunda);
    }
}
