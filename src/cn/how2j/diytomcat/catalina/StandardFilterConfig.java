/**
 * Copyright (C), 2015-2020, XXX有限公司
 * FileName: StandardFilterConfig
 * Author:   苏晨宇
 * Date:     2020/12/20 15:46
 * Description: 实现FilterConfig
 * History:
 * <author>          <time>          <version>          <desc>
 * 作者姓名           修改时间           版本号              描述
 */
package cn.how2j.diytomcat.catalina;

import cn.how2j.diytomcat.http.StandardSession;

import javax.servlet.FilterConfig;
import javax.servlet.ServletContext;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

/**
 * 〈一句话功能简述〉<br>
 * 〈实现FilterConfig〉
 *
 * @author 苏晨宇
 * @create 2020/12/20
 * @since 1.0.0
 */
public class StandardFilterConfig implements FilterConfig {
    private ServletContext servletContext;
    private Map<String, String> initParameters;
    private String filterName;


    public StandardFilterConfig(ServletContext servletContext, String filterName, Map<String, String> initParameters) {
        this.servletContext = servletContext;
        this.filterName = filterName;
        this.initParameters = initParameters;
        if (this.initParameters == null)
            this.initParameters = new HashMap<>();
    }

    @Override
    public String getFilterName() {
        return filterName;
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
 
