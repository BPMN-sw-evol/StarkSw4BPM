package com.example.workflow;

import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class MiClaseHandler implements JavaDelegate {

    private static final Logger LOGGER = LoggerFactory.getLogger(MiClaseHandler.class);

    @Override
    public void execute(DelegateExecution execution) throws Exception {
        LOGGER.info("Ejecutando: Register Guarantee Analysis Request");

        String solicitudId = (String) execution.getVariable("solicitudId");
        LOGGER.info("Registrando solicitud de análisis de garantía para ID: {}", solicitudId);
        execution.setVariable("garantiaRegistrada", true);
    }
}
