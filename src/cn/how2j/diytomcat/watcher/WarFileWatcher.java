/**
 * Copyright (C), 2015-2020, XXX有限公司
 * FileName: WarFileWatcher
 * Author:   苏晨宇
 * Date:     2020/12/21 16:35
 * Description: 监控webapps目录
 * History:
 * <author>          <time>          <version>          <desc>
 * 作者姓名           修改时间           版本号              描述
 */
package cn.how2j.diytomcat.watcher;

import cn.how2j.diytomcat.catalina.Host;
import cn.how2j.diytomcat.util.Constant;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.io.watch.WatchMonitor;
import cn.hutool.core.io.watch.WatchUtil;
import cn.hutool.core.io.watch.Watcher;

import static java.nio.file.StandardWatchEventKinds.ENTRY_CREATE;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.WatchEvent;

/**
 * 〈一句话功能简述〉<br>
 * 〈监控webapps目录〉
 *
 * @author 苏晨宇
 * @create 2020/12/21
 * @since 1.0.0
 */
public class WarFileWatcher {
    private WatchMonitor monitor;

    public WarFileWatcher(Host host) {
        this.monitor = WatchUtil.createAll(Constant.webappsFolder, 1, new Watcher() {
            private void dealWith(WatchEvent<?> event, Path currentPath) {
                synchronized (WarFileWatcher.class) {
                    String fileName = event.context().toString();
                    if (fileName.toLowerCase().endsWith(".war") && ENTRY_CREATE.equals(event.kind())) {
                        File warFile = FileUtil.file(Constant.webappsFolder, fileName);
                        host.loadWar(warFile);
                    }
                }
            }

            @Override
            public void onCreate(WatchEvent<?> watchEvent, Path path) {
                dealWith(watchEvent, path);

            }

            @Override
            public void onModify(WatchEvent<?> watchEvent, Path path) {
                dealWith(watchEvent, path);
            }

            @Override
            public void onDelete(WatchEvent<?> watchEvent, Path path) {
                dealWith(watchEvent, path);
            }

            @Override
            public void onOverflow(WatchEvent<?> watchEvent, Path path) {
                dealWith(watchEvent, path);
            }
        });
    }

    public void start() {
        monitor.start();
    }

    public void stop() {
        monitor.interrupt();
    }
}
 
