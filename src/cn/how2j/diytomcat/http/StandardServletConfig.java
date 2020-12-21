/**
 * Copyright (C), 2015-2020, XXX有限公司
 * FileName: StandardServletConfig
 * Author:   苏晨宇
 * Date:     2020/12/16 15:03
 * Description: 实现ServletConfig接口 提供对应的方法
 * History:
 * <author>          <time>          <version>          <desc>
 * 作者姓名           修改时间           版本号              描述
 */
package cn.how2j.diytomcat.http;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import java.util.Collections;
import java.util.Enumeration;
import java.util.Map;

/**
 * 〈一句话功能简述〉<br>
 * 〈实现ServletConfig接口 提供对应的方法〉
 *
 * @author 苏晨宇
 * @create 2020/12/16
 * @since 1.0.0
 */
public class StandardServletConfig implements ServletConfig {
    private ServletContext servletContext;
    private Map<String, String> initParameters;
    private String servletName;


    public StandardServletConfig(ServletContext servletContext, String servletName, Map<String, String> initParameters) {
        this.servletContext = servletContext;
        this.initParameters = initParameters;
        this.servletName = servletName;
    }

    @Override
    public String getServletName() {
        return servletName;
    }

    @Override
    public ServletContext getServletContext() {
        return servletContext;
    }

    @Override
    public String getInitParameter(String s) {
        return initParameters.get(s);
    }

    @Override
    public Enumeration<String> getInitParameterNames() {
        return Collections.enumeration(initParameters.keySet());
    }
}
 
