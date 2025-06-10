package com.starksw4b.pmn.starksw4bpmn.controller;

import com.starksw4b.pmn.starksw4bpmn.facade.SystemGeneratorFacade;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@RestController
@RequestMapping("/api/generator")
public class JavaDelegateGeneratorController {

    private final SystemGeneratorFacade generatorFacade;

    @Autowired
    public JavaDelegateGeneratorController(SystemGeneratorFacade generatorFacade) {
        this.generatorFacade = generatorFacade;
    }

    @PostMapping("/generate-delegate")
    public String generateDelegateClass(@RequestParam String className) {
        try {
            generatorFacade.generarClaseJavaDelegate(className);
            return "Clase JavaDelegate generada correctamente: " + className + ".java";
        } catch (IOException e) {
            return "Error al generar la clase JavaDelegate: " + e.getMessage();
        }
    }

    @PostMapping("/generate-delegate-expression")
    public String generateDelegateExpressionClass(@RequestParam String className) {
        try {
            generatorFacade.generarClaseDelegateExpression(className);
            return "Clase DelegateExpression generada correctamente: " + className + ".java";
        } catch (IOException e) {
            return "Error al generar la clase DelegateExpression: " + e.getMessage();
        }
    }

    @PostMapping("/generate-send-task")
    public String generateSendTaskClass(@RequestParam String className) {
        try {
            generatorFacade.generarClaseSendTask(className);
            return "Clase SendTask generada correctamente: " + className + ".java";
        } catch (IOException e) {
            return "Error al generar la clase SendTask: " + e.getMessage();
        }
    }
}
