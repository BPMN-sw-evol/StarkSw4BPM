package com.starksw4b.pmn.starksw4bpmn.facade;

import com.starksw4b.pmn.starksw4bpmn.fileGenerator.DelegateExpressionGeneratorService;
import com.starksw4b.pmn.starksw4bpmn.fileGenerator.JavaDelegateGeneratorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
public class SystemGeneratorFacade {

    private final JavaDelegateGeneratorService javaDelegateGeneratorService;
    private final DelegateExpressionGeneratorService delegateExpressionGeneratorService;

    @Autowired
    public SystemGeneratorFacade(JavaDelegateGeneratorService javaDelegateGeneratorService,
                                 DelegateExpressionGeneratorService delegateExpressionGeneratorService) {
        this.javaDelegateGeneratorService = javaDelegateGeneratorService;
        this.delegateExpressionGeneratorService = delegateExpressionGeneratorService;
    }

    public void generarClaseJavaDelegate(String className) throws IOException {
        javaDelegateGeneratorService.generateJavaDelegateClass(className);
    }

    public void generarClaseDelegateExpression(String className) throws IOException {
        delegateExpressionGeneratorService.generateDelegateExpressionClass(className);
    }
}