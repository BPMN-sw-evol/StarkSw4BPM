package com.starksw4b.pmn.starksw4bpmn.DTOs;

public class TaskResponseDTO {
    private String name;
    private String taskKey;
    private String taskType;
    private String taskCategory;

    public TaskResponseDTO(String name, String taskKey, String taskType, String taskCategory) {
        this.name = name;
        this.taskKey = taskKey;
        this.taskType = taskType;
        this.taskCategory = taskCategory;
    }

    public String getName() { return name; }
    public String getTaskKey() { return taskKey; }
    public String getTaskType() { return taskType; }
    public String getTaskCategory() { return taskCategory; }
}
