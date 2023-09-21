package com.animal.base.utils;

import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.*;

/**
 * @author 咏鹅
 * @version 1.0
 * @description 高可用线程池
 * @date 2023/8/31 0:03
 */
@Slf4j
public class HighAvailableThreadUtil {

    private static ExecutorService threadPool = createThreadPool();

    public static ExecutorService getThreadPool(){
        return threadPool;
    }
    private static final int CORE_POOL_SIZE = Runtime.getRuntime().availableProcessors(); // 根据CPU核心数设置核心线程数
    private static final int MAX_POLL_SIZE = CORE_POOL_SIZE * 2; //最大线程数量
    private static final long KEEP_ALIVE_TIME = 60L; //线程空闲时间
    private static final int QUEUE_CAPACITY = 100; //任务队列容量
    private static final RejectedExecutionHandler REJECTED_EXECUTION_HANDLER = new ThreadPoolExecutor.CallerRunsPolicy();

    public static ExecutorService createThreadPool(){
        ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
        //从创建出线程池开始，每4小时重启，降低系统缓存
        scheduler.scheduleAtFixedRate(()->{
            restartThreadPool();
        },0,4,TimeUnit.HOURS);

        return new ThreadPoolExecutor(
                CORE_POOL_SIZE,
                MAX_POLL_SIZE,
                KEEP_ALIVE_TIME,
                TimeUnit.SECONDS,
                new LinkedBlockingDeque<>(QUEUE_CAPACITY),
                Executors.defaultThreadFactory(),
                REJECTED_EXECUTION_HANDLER
        );
    }

    public static void restartThreadPool(){
        if(threadPool != null){
            threadPool.shutdown();
        }else return;

        try{
            if(!threadPool.awaitTermination(10,TimeUnit.SECONDS)){
                threadPool.shutdownNow();
            }
        } catch (InterruptedException e) {
            threadPool.shutdownNow();
            Thread.currentThread().interrupt();
        }
        threadPool = createThreadPool();
        log.info("线程池开始重启.");
    }

}
