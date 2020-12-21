/**
 * Copyright (C), 2015-2020, XXX有限公司
 * FileName: ThreadPoolUtil
 * Author:   苏晨宇
 * Date:     2020/12/13 19:06
 * Description: 创建ThreadPoolExecutor对象
 * History:
 * <author>          <time>          <version>          <desc>
 * 作者姓名           修改时间           版本号              描述
 */
package cn.how2j.diytomcat.util;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * 〈一句话功能简述〉<br>
 * 〈创建ThreadPoolExecutor对象〉
 *
 * @author 苏晨宇
 * @create 2020/12/13
 * @since 1.0.0
 */
public class ThreadPoolUtil {
    private static ThreadPoolExecutor threadPool = new ThreadPoolExecutor(20, 100, 60, TimeUnit.SECONDS, new LinkedBlockingQueue<>(10));

    public static void run(Runnable r) {
        threadPool.execute(r);
    }
}
 
