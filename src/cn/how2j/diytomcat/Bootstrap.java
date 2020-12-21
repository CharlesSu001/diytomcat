/**
 * Copyright (C), 2015-2020, XXX有限公司
 * FileName: Bootstrap
 * Author:   苏晨宇
 * Date:     2020/12/13 14:53
 * Description: 简单web服务器
 * History:
 * <author>          <time>          <version>          <desc>
 * 作者姓名           修改时间           版本号              描述
 */
package cn.how2j.diytomcat;

import cn.how2j.diytomcat.catalina.*;
import cn.how2j.diytomcat.classloader.CommonClassLoader;
import cn.how2j.diytomcat.http.Request;
import cn.how2j.diytomcat.http.Response;
import cn.how2j.diytomcat.util.Constant;
import cn.how2j.diytomcat.util.ServerXMLUtil;
import cn.how2j.diytomcat.util.ThreadPoolUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.thread.ThreadUtil;
import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.NetUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.log.LogFactory;
import cn.hutool.system.SystemUtil;


import java.io.*;
import java.lang.reflect.Method;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;

/**
 * 〈一句话功能简述〉<br>
 * 〈简单web服务器〉
 *
 * @author 苏晨宇
 * @create 2020/12/13
 * @since 1.0.0
 */
public class Bootstrap {

    public static void main(String[] args) throws Exception {
        CommonClassLoader commonClassLoader = new CommonClassLoader();
        Thread.currentThread().setContextClassLoader(commonClassLoader);
        String serverClassName = "cn.how2j.diytomcat.catalina.Server";
        Class<?> serverClazz = commonClassLoader.loadClass(serverClassName);
        Object serverObject = serverClazz.newInstance();
        Method m = serverClazz.getMethod("start");
        m.invoke(serverObject);
        System.out.println(serverClazz.getClassLoader());

    }

}
 
