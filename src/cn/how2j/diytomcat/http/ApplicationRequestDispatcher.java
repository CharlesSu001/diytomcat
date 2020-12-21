/**
 * Copyright (C), 2015-2020, XXX有限公司
 * FileName: ApplicationRequestDispatcher
 * Author:   苏晨宇
 * Date:     2020/12/18 16:47
 * Description: 服务端跳转 修改uri 内部再次访问
 * History:
 * <author>          <time>          <version>          <desc>
 * 作者姓名           修改时间           版本号              描述
 */
package cn.how2j.diytomcat.http;

import cn.how2j.diytomcat.catalina.HttpProcessor;
import org.apache.tools.ant.taskdefs.condition.Http;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import java.io.IOException;

/**
 * 〈一句话功能简述〉<br> 
 * 〈服务端跳转 修改uri 内部再次访问〉
 *
 * @author 苏晨宇
 * @create 2020/12/18
 * @since 1.0.0
 */
public class ApplicationRequestDispatcher implements RequestDispatcher {
    private String uri;

    public ApplicationRequestDispatcher(String uri){
        if(!uri.startsWith("/"))
            uri="/"+uri;
        this.uri=uri;
    }

    @Override
    public void forward(ServletRequest servletRequest, ServletResponse servletResponse) throws ServletException, IOException {
        Request request=(Request) servletRequest;
        Response response=(Response) servletResponse;
        request.setUri(uri);
        HttpProcessor processor=new HttpProcessor();
        processor.execute(request.getSocket(),request,response);
        request.setForwarded(true);
    }

    @Override
    public void include(ServletRequest servletRequest, ServletResponse servletResponse) throws ServletException, IOException {

    }
}
 
