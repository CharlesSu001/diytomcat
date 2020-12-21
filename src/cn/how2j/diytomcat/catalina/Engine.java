/**
 * Copyright (C), 2015-2020, XXX有限公司
 * FileName: Engine
 * Author:   苏晨宇
 * Date:     2020/12/14 15:57
 * Description: Engine类
 * History:
 * <author>          <time>          <version>          <desc>
 * 作者姓名           修改时间           版本号              描述
 */
package cn.how2j.diytomcat.catalina;

import cn.how2j.diytomcat.util.ServerXMLUtil;

import java.util.List;

/**
 * 〈一句话功能简述〉<br> 
 * 〈Engine类〉
 *
 * @author 苏晨宇
 * @create 2020/12/14
 * @since 1.0.0
 */
public class Engine {
    private String defaultHost;
    private List<Host> hosts;
    private Service service;

    public Engine(Service service){
        this.service=service;
        this.defaultHost= ServerXMLUtil.getEngineDefaultHost();
        this.hosts=ServerXMLUtil.getHosts(this);
        checkDefault();
    }

    public Service getService(){
        return service;
    }
    private void checkDefault(){
        if(getDefaultHost()==null){
            throw new RuntimeException("the defaultHost"+defaultHost+"does not exist!");
        }
    }
    public Host getDefaultHost(){
        for(Host host:hosts){
            if(host.getName().equals(defaultHost))
                return host;
        }
        return null;
    }
}
 
