package com.sakura.task;

import com.sakura.task.vo.ITaskProcesser;
import com.sakura.task.vo.JobInfo;
import com.sakura.task.vo.TaskResult;
import com.sakura.task.vo.TaskResultType;

import java.util.List;
import java.util.concurrent.*;

public class PendingJobPool {
    private final int THREAD_NUM = Runtime.getRuntime().availableProcessors();
    private BlockingQueue<Runnable> queue = new ArrayBlockingQueue<>(5000);
    private static ConcurrentHashMap<String, JobInfo<?>> jonInfoMap = new ConcurrentHashMap<>();
    private ExecutorService executor = new ThreadPoolExecutor(THREAD_NUM, THREAD_NUM, 60L, TimeUnit.SECONDS, queue);
    private static CheckJobPorcesser checkJobPorcesser = CheckJobPorcesser.getInstance();


    private static class PeningTask<T, R> implements Runnable {


        private JobInfo<R> jobInfo;
        private T data;

        public PeningTask(JobInfo<R> jobInfo, T data) {
            this.jobInfo = jobInfo;
            this.data = data;
        }

        @Override
        public void run() {
            R r = null;
            ITaskProcesser<T, R> taskProcesser = (ITaskProcesser<T, R>) jobInfo.getTaskProcesser();
            TaskResult<R> result = null;
            try {
                result = taskProcesser.execute(data);
                if (null == result) {
                    result = new TaskResult<R>("result is null", r, TaskResultType.Exception);
                }
                if (null == result.getType()) {
                    if (null == result.getReason()) {
                        result = new TaskResult<R>("reason is null", r, TaskResultType.Exception);
                    } else {
                        result = new TaskResult<R>("result is null,but reason" + result.getReason(), r, TaskResultType.Exception);
                    }
                }

            } catch (Exception e) {
                e.printStackTrace();
                result = new TaskResult<R>(e.getMessage(), r, TaskResultType.Exception);
            } finally {
                jobInfo.addTaskResult(result, checkJobPorcesser);
            }
        }
    }

    private static class PendingPoolHolder {
        private static PendingJobPool pendingJobPool = new PendingJobPool();
    }

    public static PendingJobPool getInstance() {
        return PendingPoolHolder.pendingJobPool;
    }

    public static ConcurrentHashMap<String, JobInfo<?>> getMap() {
        return jonInfoMap;
    }

    public <R> JobInfo<R> getJob(String jobName) {
        JobInfo<R> jobInfo = (JobInfo<R>) jonInfoMap.get(jobName);
        if (jobInfo == null) {
            throw new RuntimeException("是个非法任务");
        }
        return jobInfo;
    }

    public <T, R> void putTask(String jobName, T t) {
        JobInfo<R> job = getJob(jobName);
        PeningTask<T, R> task = new PeningTask<>(job, t);
        executor.execute(task);
    }

    public <R> void register(String jobName, int jobLength, ITaskProcesser<?, ?> processer, long expireTime) {
        JobInfo<R> jobInfo = new JobInfo(jobLength, expireTime, processer, jobName);
        if (jonInfoMap.putIfAbsent(jobName, jobInfo) != null) {
            throw new RuntimeException(jobName + "已经注册了！");
        }
    }

    public <R> List<TaskResult<R>> getTaskResult(String jobName) {
        JobInfo<R> job = getJob(jobName);
        return job.getTaskResult();
    }

    public <R> String getTaskProcess(String jobName) {
        JobInfo<R> job = getJob(jobName);
        return job.getTotalProcess();
    }

}
