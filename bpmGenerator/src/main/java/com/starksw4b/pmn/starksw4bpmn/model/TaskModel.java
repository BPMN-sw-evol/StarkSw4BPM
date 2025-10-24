package com.starksw4b.pmn.starksw4bpmn.model;

import jakarta.persistence.*;

@Entity
public class TaskModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String taskType;
    private String taskCategory;
    private String taskKey;

    // 🔹 Campos adicionales para compuertas y condiciones
    private String sourceRef;              // Nodo origen (solo gateways)
    private String targetRef;              // Nodo destino (solo gateways)
    @Column(length = 1000)
    private String conditionExpression;    // Ej: ${allFine == true}
    private String variableName;           // Ej: allFine
    private String expectedValue;          // Ej: true
    private String gatewayType;            // Ej: exclusive, parallel, inclusive
    @Column(length = 1000)
    private String description;            // Descripción opcional
    private String parentProcessKey;       // Para futuros subprocesos

    // 🔸 NUEVOS CAMPOS (para manejar caminos true/false detectados)
    private String conditionTrue;          // ID del flujo con condición ${true}
    private String conditionFalse;         // ID del flujo con condición ${false}

    // Getters y Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getTaskType() { return taskType; }
    public void setTaskType(String taskType) { this.taskType = taskType; }

    public String getTaskCategory() { return taskCategory; }
    public void setTaskCategory(String taskCategory) { this.taskCategory = taskCategory; }

    public String getTaskKey() { return taskKey; }
    public void setTaskKey(String taskKey) { this.taskKey = taskKey; }

    public String getSourceRef() { return sourceRef; }
    public void setSourceRef(String sourceRef) { this.sourceRef = sourceRef; }

    public String getTargetRef() { return targetRef; }
    public void setTargetRef(String targetRef) { this.targetRef = targetRef; }

    public String getConditionExpression() { return conditionExpression; }
    public void setConditionExpression(String conditionExpression) { this.conditionExpression = conditionExpression; }

    public String getVariableName() { return variableName; }
    public void setVariableName(String variableName) { this.variableName = variableName; }

    public String getExpectedValue() { return expectedValue; }
    public void setExpectedValue(String expectedValue) { this.expectedValue = expectedValue; }

    public String getGatewayType() { return gatewayType; }
    public void setGatewayType(String gatewayType) { this.gatewayType = gatewayType; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getParentProcessKey() { return parentProcessKey; }
    public void setParentProcessKey(String parentProcessKey) { this.parentProcessKey = parentProcessKey; }

    // ✅ Getters y Setters nuevos
    public String getConditionTrue() { return conditionTrue; }
    public void setConditionTrue(String conditionTrue) { this.conditionTrue = conditionTrue; }

    public String getConditionFalse() { return conditionFalse; }
    public void setConditionFalse(String conditionFalse) { this.conditionFalse = conditionFalse; }
}
