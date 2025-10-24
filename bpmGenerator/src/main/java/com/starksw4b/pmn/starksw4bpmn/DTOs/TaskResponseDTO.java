package com.starksw4b.pmn.starksw4bpmn.DTOs;

public class TaskResponseDTO {

    private String name;
    private String taskKey;
    private String taskType;
    private String taskCategory;

    // 🔹 Nuevos campos opcionales (para gateways y condiciones)
    private String sourceRef;
    private String targetRef;
    private String conditionExpression;
    private String variableName;
    private String expectedValue;
    private String gatewayType;
    private String description;
    private String parentProcessKey;

    // =================== Constructores ===================

    // Constructor base (mantiene compatibilidad con tu código actual)
    public TaskResponseDTO(String name, String taskKey, String taskType, String taskCategory) {
        this.name = name;
        this.taskKey = taskKey;
        this.taskType = taskType;
        this.taskCategory = taskCategory;
    }

    // Constructor completo (para devolver también condiciones)
    public TaskResponseDTO(String name, String taskKey, String taskType, String taskCategory,
                           String sourceRef, String targetRef, String conditionExpression,
                           String variableName, String expectedValue, String gatewayType,
                           String description, String parentProcessKey) {
        this.name = name;
        this.taskKey = taskKey;
        this.taskType = taskType;
        this.taskCategory = taskCategory;
        this.sourceRef = sourceRef;
        this.targetRef = targetRef;
        this.conditionExpression = conditionExpression;
        this.variableName = variableName;
        this.expectedValue = expectedValue;
        this.gatewayType = gatewayType;
        this.description = description;
        this.parentProcessKey = parentProcessKey;
    }

    // =================== Getters y Setters ===================

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getTaskKey() { return taskKey; }
    public void setTaskKey(String taskKey) { this.taskKey = taskKey; }

    public String getTaskType() { return taskType; }
    public void setTaskType(String taskType) { this.taskType = taskType; }

    public String getTaskCategory() { return taskCategory; }
    public void setTaskCategory(String taskCategory) { this.taskCategory = taskCategory; }

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
}
