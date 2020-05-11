package com.sakura.task;


import java.util.concurrent.DelayQueue;

public class CheckJobPorcesser {

    private static DelayQueue<ItemVo<String>> queue = new DelayQueue<ItemVo<String>>();

    private static class CheakJobHolder {
        public static CheckJobPorcesser checkJob = new CheckJobPorcesser();
    }

    public static CheckJobPorcesser getInstance() {
        return CheakJobHolder.checkJob;
    }

    public void putJob(String jobName, long expireTime) {
        ItemVo<String> itemVo = new ItemVo<>(expireTime, jobName);
        queue.offer(itemVo);
        System.out.println("Job[" + jobName + "已经放入了过期检查缓存，过期时长：" + expireTime);
    }

    public static class FecthJob implements Runnable {

        @Override
        public void run() {
            while (true) {
                try {
                    ItemVo<String> itemVo = queue.take();
                    String jobName = itemVo.getData();
                    PendingJobPool.getMap().remove(jobName);
                    System.out.println(jobName + " is out of date,remove from map!");
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    static {
        Thread thread = new Thread(new FecthJob());
        thread.setDaemon(true);
        thread.start();
    }
}
