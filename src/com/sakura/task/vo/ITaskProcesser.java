package com.sakura.task.vo;

public interface ITaskProcesser<T, R> {
    TaskResult<R> execute(T data);
}
