package com.example.workflow;

import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class SendNotification implements JavaDelegate {

    private static final Logger LOGGER = LoggerFactory.getLogger(SendNotification.class);

    public SendNotification() {
        // Constructor público sin args
    }

    @Override
    public void execute(DelegateExecution execution) {
        // 1) Loguea un mensaje
        LOGGER.info("SendNotification dummy ejecutado para actividad '{}'",
                execution.getCurrentActivityId());

        // 2) Pone una variable de proceso para evidenciar la ejecución
        execution.setVariable("notificationSent", true);
    }
}

