package com.sakura.task.demo;

import com.sakura.task.vo.ITaskProcesser;
import com.sakura.task.vo.TaskResult;
import com.sakura.task.vo.TaskResultType;
import com.sakura.tools.SleepTools;

import java.util.Random;

public class MyTask implements ITaskProcesser<Integer, Integer> {

    @Override
    public TaskResult<Integer> execute(Integer data) {

        Random random = new Random();
        int flag = random.nextInt(500);
        SleepTools.ms(flag);
        if (flag <= 300) {
            Integer returnValue = data.intValue() + flag;
            return new TaskResult<Integer>(returnValue, TaskResultType.Success);
        } else if (flag > 301 && flag <= 400) {
            return new TaskResult<Integer>("Failure", -1, TaskResultType.Failure);
        } else {
            try {
                throw new RuntimeException("异常发生了！！");
            } catch (Exception e) {
                return new TaskResult<Integer>(e.getMessage(), -1, TaskResultType.Exception
                );
            }
        }

    }
}
