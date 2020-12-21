/**
 * Copyright (C), 2015-2020, XXX有限公司
 * FileName: Service
 * Author:   苏晨宇
 * Date:     2020/12/14 16:12
 * Description: Service下有name和Engine属性
 * History:
 * <author>          <time>          <version>          <desc>
 * 作者姓名           修改时间           版本号              描述
 */
package cn.how2j.diytomcat.catalina;

import cn.how2j.diytomcat.util.ServerXMLUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.date.TimeInterval;
import cn.hutool.log.LogFactory;

import java.sql.Time;
import java.util.List;

/**
 * 〈一句话功能简述〉<br>
 * 〈Service下有name和Engine属性〉
 *
 * @author 苏晨宇
 * @create 2020/12/14
 * @since 1.0.0
 */
public class Service {
    private String name;
    private Engine engine;
    private Server server;
    private List<Connector> connectors;

    public Service(Server server) {
        this.server = server;
        this.name = ServerXMLUtil.getServiceName();
        this.engine = new Engine(this);
        this.connectors = ServerXMLUtil.getConnectors(this);
    }

    public Engine getEngine() {
        return engine;
    }

    public Server getServer() {
        return server;
    }

    public void start() {
        init();
    }

    private void init() {
        TimeInterval timeInterval = DateUtil.timer();
        for (Connector c : connectors)
            c.init();
        LogFactory.get().info("Initialization processed in {} ms", timeInterval.intervalMs());
        for (Connector c : connectors)
            c.start();

    }
}

 
