package org.zongf.plugins.idea.util.common;

import java.util.*;
import java.util.concurrent.*;
import java.util.function.Consumer;
import java.util.function.Function;

/** 多线程执行工具
 * @since 1.0
 * @author zongf
 * @created 2019-08-11
 */
public class MultiThreadUtil {

    // 固定线程池
    private static ExecutorService executorService = Executors.newFixedThreadPool(5);


    /** 多线程执行任务, 合并返回结果
     * @param function 参数为T类型 返回值为R类型 的方法
     * @param params 参数列表, 相当于每个参数调用一次方法
     * @return List<R> 返回合并的结果
     * @since 1.0
     * @author zongf
     * @created 2019-08-11
     */
    public static <T, R> List<R> callable(Function<T, R> function, List<T> params) {

        // 异步任务列表
        List<Callable<R>> callableList = new ArrayList<Callable<R>>();

        // 创建异步任务列表
        for (T param : params) {
            Callable<R> callable  = new Callable<R>(){
                @Override
                public R call() throws Exception {
                    return function.apply(param);
                }
            };
            callableList.add(callable);
        }

        // 创建结果列表
        List<R> resultList = new ArrayList<>();

        try {
            // 开始异步任务
            List<Future<R>> futureList =  executorService.invokeAll(callableList);

            // 解析返回结果
            for (Future<R> future : futureList) {
                resultList.add(future.get());
            }

        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }

        return resultList;
    }


    /** 异步执行多线程
     * @param consumer 只有一个参数(类型为T)的方法, 没有返回值
     * @param params 多个参数，相当于每个参数调用一次执行方法
     * @since 1.0
     * @author zongf
     * @created 2019-08-11
     */
    public static <T> void execute(Consumer<T> consumer, List<T> params) {
        for (T param : params) {
            Runnable runnable = new Runnable() {
                @Override
                public void run() {
                    consumer.accept(param);
                }
            };
            executorService.execute(runnable);
        }
    }

}



