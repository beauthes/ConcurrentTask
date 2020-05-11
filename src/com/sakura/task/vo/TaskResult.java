package com.sakura.task.vo;

public class TaskResult<R> {
    private String reason;

    private R value;

    private TaskResultType type;


    public TaskResult(String reason, R value, TaskResultType type) {
        this.reason = reason;
        this.value = value;
        this.type = type;
    }

    public TaskResult(R value, TaskResultType type) {
        this.value = value;
        this.type = type;
    }

    public String getReason() {
        return reason;
    }

    public R getValue() {
        return value;
    }

    public TaskResultType getType() {
        return type;
    }

    @Override
    public String toString() {
        return "TaskResult{" +
                "reason='" + reason + '\'' +
                ", value=" + value +
                ", type=" + type +
                '}';
    }
}
