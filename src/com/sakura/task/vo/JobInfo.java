package com.sakura.task.vo;

import com.sakura.task.CheckJobPorcesser;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.atomic.AtomicInteger;

public class JobInfo<R> {
    private AtomicInteger successCount;
    private AtomicInteger taskProcesserCount;
    private ITaskProcesser<?, ?> processer;
    private LinkedBlockingDeque<TaskResult<R>> queue;
    private int jobLength;
    private long expireTime;
    private String jobName;

    public JobInfo(int jobLength, long expireTime, ITaskProcesser<?, ?> processer, String jobName) {
        this.jobLength = jobLength;
        this.expireTime = expireTime;
        this.processer = processer;
        this.jobName = jobName;
        successCount = new AtomicInteger(0);
        taskProcesserCount = new AtomicInteger(0);
        queue = new LinkedBlockingDeque<TaskResult<R>>(jobLength);
    }

    public ITaskProcesser<?, ?> getTaskProcesser() {
        return processer;
    }

    public int getSuccessCount() {
        return successCount.get();
    }

    public int getTaskProcesserCount() {
        return taskProcesserCount.get();
    }

    public int getFailureCount() {
        return taskProcesserCount.get() - successCount.get();
    }

    public String getTotalProcess() {
        return "Success[" + successCount.get() + "]/Current["
                + taskProcesserCount.get() + "] Total[" + jobLength + "]";
    }


    public List<TaskResult<R>> getTaskResult() {
        TaskResult<R> taskResult;
        List<TaskResult<R>> list = new LinkedList<>();
        while ((taskResult = queue.pollFirst()) != null) {
            list.add(taskResult);
        }
        return list;
    }

    public void addTaskResult(TaskResult<R> taskResult, CheckJobPorcesser checkJobPorcesser) {
        if (taskResult.getType().equals(TaskResultType.Success)) {
            successCount.getAndIncrement();
        }
        taskProcesserCount.getAndIncrement();
        queue.addLast(taskResult);
        if (taskProcesserCount.get() == jobLength) {
            checkJobPorcesser.putJob(jobName, expireTime);
        }

    }
}
