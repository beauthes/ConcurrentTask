package com.sakura.task;

import java.util.concurrent.Delayed;
import java.util.concurrent.TimeUnit;

public class ItemVo<T> implements Delayed {

    private long activeTime;//到期时间

    private T data;

    public ItemVo(long activeTime, T data) {
        //将传人的持续时间毫秒转化为纳秒，+当前时间纳秒 = 过期时刻
        this.activeTime = System.nanoTime() + TimeUnit.NANOSECONDS.convert(activeTime, TimeUnit.MILLISECONDS);
        this.data = data;
    }

    public long getActiveTime() {
        return activeTime;
    }

    public T getData() {
        return data;
    }

    @Override
    public long getDelay(TimeUnit unit) {
        return unit.convert(activeTime - System.nanoTime(), TimeUnit.NANOSECONDS);
    }

    @Override
    public int compareTo(Delayed o) {
        long l = getDelay(TimeUnit.NANOSECONDS) - o.getDelay(TimeUnit.NANOSECONDS);
        return (l == 0) ? 0 : (l > 0) ? 1 : -1;
    }
}
