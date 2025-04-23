package com.example.workflow;

import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class CreditRequestHandler implements JavaDelegate {

    private static final Logger LOGGER = LoggerFactory.getLogger(CreditRequestHandler.class);

    @Override
    public void execute(DelegateExecution execution) throws Exception {
        LOGGER.info("Ejecutando: Register Guarantee Analysis Request");

        // Obtener variables si es necesario
        String solicitudId = (String) execution.getVariable("solicitudId");

        // Simulación de lógica de negocio
        LOGGER.info("Registrando solicitud de análisis de garantía para ID: {}", solicitudId);

        // Establecer alguna variable para el siguiente paso
        execution.setVariable("garantiaRegistrada", true);
    }
}
