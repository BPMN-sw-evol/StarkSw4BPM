package com.starksw4b.pmn.starksw4bpmn.DTOs;

import java.util.Map;

public class TaskRequestDTO {
    private Map<String, String> userTasks;
    private Map<String, String> serviceTasks;
    private Map<String, String> sendTasks;

    // Getters y setters
    public Map<String, String> getUserTasks() {
        return userTasks;
    }

    public void setUserTasks(Map<String, String> userTasks) {
        this.userTasks = userTasks;
    }

    public Map<String, String> getServiceTasks() {
        return serviceTasks;
    }

    public void setServiceTasks(Map<String, String> serviceTasks) {
        this.serviceTasks = serviceTasks;
    }

    public Map<String, String> getSendTasks() {
        return sendTasks;
    }

    public void setSendTasks(Map<String, String> sendTasks) {
        this.sendTasks = sendTasks;
    }
}
