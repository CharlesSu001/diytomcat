/**
 * Copyright (C), 2015-2020, XXX有限公司
 * FileName: HelloServlet
 * Author:   苏晨宇
 * Date:     2020/12/15 9:34
 * Description: 简单的HelloServlet
 * History:
 * <author>          <time>          <version>          <desc>
 * 作者姓名           修改时间           版本号              描述
 */
package cn.how2j.diytomcat.webappservlet;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * 〈一句话功能简述〉<br>
 * 〈简单的HelloServlet〉
 *
 * @author 苏晨宇
 * @create 2020/12/15
 * @since 1.0.0
 */
public class HelloServlet extends HttpServlet {
    public void doGet(HttpServletRequest request, HttpServletResponse response) {
        try {
            response.getWriter().println("Hello DIY Tomcat from HelloServlet");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
 
