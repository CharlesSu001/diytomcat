/**
 * Copyright (C), 2015-2020, XXX有限公司
 * FileName: ContextFileChangeWatcher
 * Author:   苏晨宇
 * Date:     2020/12/15 20:15
 * Description: Context文件改变监听器
 * History:
 * <author>          <time>          <version>          <desc>
 * 作者姓名           修改时间           版本号              描述
 */
package cn.how2j.diytomcat.watcher;

import cn.how2j.diytomcat.catalina.Context;
import cn.hutool.core.io.watch.WatchMonitor;
import cn.hutool.core.io.watch.WatchUtil;
import cn.hutool.core.io.watch.Watcher;
import cn.hutool.log.LogFactory;

import java.nio.file.Path;
import java.nio.file.WatchEvent;

/**
 * 〈一句话功能简述〉<br>
 * 〈Context文件改变监听器〉
 *
 * @author 苏晨宇
 * @create 2020/12/15
 * @since 1.0.0
 */
public class ContextFileChangeWatcher {
    private WatchMonitor monitor;
    private boolean stop = false;

    public ContextFileChangeWatcher(Context context) {
        this.monitor = WatchUtil.createAll(context.getDocBase(), Integer.MAX_VALUE, new Watcher() {

            private void dealWith(WatchEvent<?> event) {
                synchronized (ContextFileChangeWatcher.class) {
                    String fileName = event.context().toString();
                    if (stop)
                        return;
                    if (fileName.endsWith(".jar") || fileName.endsWith(".class") || fileName.endsWith(".xml")) {
                        stop = true;
                        LogFactory.get().info(ContextFileChangeWatcher.this + "检测到了Web应用下的重要文件变化 {}", fileName);
                        context.reload();
                    }
                }
            }

            @Override
            public void onCreate(WatchEvent<?> watchEvent, Path path) {
                dealWith(watchEvent);

            }

            @Override
            public void onModify(WatchEvent<?> watchEvent, Path path) {
                dealWith(watchEvent);

            }

            @Override
            public void onDelete(WatchEvent<?> watchEvent, Path path) {
                dealWith(watchEvent);

            }

            @Override
            public void onOverflow(WatchEvent<?> watchEvent, Path path) {
                dealWith(watchEvent);

            }
        });


    }
    public void start(){
        monitor.start();
    }

    public void stop(){
        monitor.close();
    }


}
 
