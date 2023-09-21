package com.animal.base.utils;

import com.animal.product.common.ErrorCode;
import com.animal.product.exception.BusinessException;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

/**
 * @author 咏鹅
 * @version 1.0
 * @description 处理多任务线程池
 * @date 2023/6/12 22:31
 */
public class ThreadUtil {
    //线程数量
    private Integer threadCount;

    //集合大小
    private Integer size;

    private Integer timeOut = 60;

    private Function function;

    public ThreadUtil(Integer threadCount, Integer size, Integer timeOut, Function function) {
        this.threadCount = threadCount;
        this.size = size;
        this.timeOut = timeOut;
        this.function = function;
    }
    public ThreadUtil(Integer threadCount, Integer size, Function function) {
        this.threadCount = threadCount;
        this.size = size;
        this.function = function;
    }
    public interface Function{
        void run(int i);
    }

    public void start() throws InterruptedException{
        int size = this.size;
        int threadCount = this.threadCount;
        int splitCount = size / threadCount + (size % threadCount != 0 ? 1 : 0); //计算分拆的数量

        final CountDownLatch cdl = new CountDownLatch(size);


        for (int i = 1; i <= threadCount; i++) {
            final int begin = (i - 1) * splitCount;
            final int end = (i * splitCount) > size ? size : i * splitCount;
            if (begin >= end) break;

            new Thread(() -> {
                for (int j = begin; j < end; j++) {
                    try {
                        function.run(j);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    // 闭锁-1
                    cdl.countDown();
                }
            }).start();
        }
        int time = this.timeOut != null ? this.timeOut : 60;
        // 调用闭锁的await()方法，该线程会被挂起，它会等待直到count值为0才继续执行
        try{
            if(!cdl.await(time, TimeUnit.MINUTES)){
                throw new BusinessException(ErrorCode.SYSTEM_ERROR);
            }
        }catch (InterruptedException e){
            throw e;
        }


    }
}
