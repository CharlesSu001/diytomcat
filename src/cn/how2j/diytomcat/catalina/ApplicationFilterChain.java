/**
 * Copyright (C), 2015-2020, XXX有限公司
 * FileName: ApplicationFilterChain
 * Author:   苏晨宇
 * Date:     2020/12/20 16:50
 * Description: 实现责任链
 * History:
 * <author>          <time>          <version>          <desc>
 * 作者姓名           修改时间           版本号              描述
 */
package cn.how2j.diytomcat.catalina;

import cn.hutool.core.util.ArrayUtil;

import javax.servlet.*;
import java.io.IOException;
import java.util.List;

/**
 * 〈一句话功能简述〉<br> 
 * 〈实现责任链〉
 *
 * @author 苏晨宇
 * @create 2020/12/20
 * @since 1.0.0
 */
public class ApplicationFilterChain implements FilterChain {
    private Filter[] filters;
    private Servlet servlet;
    int pos;

    public ApplicationFilterChain(List<Filter> filters,Servlet servlet){
        this.filters= ArrayUtil.toArray(filters,Filter.class);
        this.servlet=servlet;
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse) throws IOException, ServletException {
        if(pos<filters.length){
            Filter filter=filters[pos++];
            filter.doFilter(servletRequest,servletResponse,this);
        }else {
            servlet.service(servletRequest,servletResponse);
        }
    }
}
 
