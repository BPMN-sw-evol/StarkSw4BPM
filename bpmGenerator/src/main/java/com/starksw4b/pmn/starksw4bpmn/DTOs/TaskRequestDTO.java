package com.starksw4b.pmn.starksw4bpmn.DTOs;

import java.util.List;
import java.util.Map;

public class TaskRequestDTO {
    private List<Map<String, String>> userTasks;
    private List<Map<String, String>> serviceTasks;
    private List<Map<String, String>> sendTasks;

    public List<Map<String, String>> getUserTasks() {
        return userTasks;
    }

    public void setUserTasks(List<Map<String, String>> userTasks) {
        this.userTasks = userTasks;
    }

    public List<Map<String, String>> getServiceTasks() {
        return serviceTasks;
    }

    public void setServiceTasks(List<Map<String, String>> serviceTasks) {
        this.serviceTasks = serviceTasks;
    }

    public List<Map<String, String>> getSendTasks() {
        return sendTasks;
    }

    public void setSendTasks(List<Map<String, String>> sendTasks) {
        this.sendTasks = sendTasks;
    }
}
