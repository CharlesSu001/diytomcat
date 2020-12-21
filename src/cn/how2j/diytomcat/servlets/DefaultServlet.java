/**
 * Copyright (C), 2015-2020, XXX有限公司
 * FileName: DefaultServlet
 * Author:   苏晨宇
 * Date:     2020/12/15 16:33
 * Description: 处理静态资源
 * History:
 * <author>          <time>          <version>          <desc>
 * 作者姓名           修改时间           版本号              描述
 */
package cn.how2j.diytomcat.servlets;

import cn.how2j.diytomcat.catalina.Context;
import cn.how2j.diytomcat.http.Request;
import cn.how2j.diytomcat.http.Response;
import cn.how2j.diytomcat.util.Constant;
import cn.how2j.diytomcat.util.WebXMLUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.thread.ThreadUtil;
import cn.hutool.core.util.StrUtil;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;

/**
 * 〈一句话功能简述〉<br>
 * 〈处理静态资源〉
 *
 * @author 苏晨宇
 * @create 2020/12/15
 * @since 1.0.0
 */
public class DefaultServlet extends HttpServlet {
    private static DefaultServlet instance = new DefaultServlet();

    public static synchronized DefaultServlet getInstance() {
        return instance;
    }

    private DefaultServlet() {

    }

    public void service(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) {
        Request request = (Request) httpServletRequest;
        Response response = (Response) httpServletResponse;

        Context context = request.getContext();
        String uri = request.getUri();

        if (uri.equals("/500.html"))
            throw new RuntimeException("this is a deliberately created exception");

        if (uri.equals("/"))
            uri = WebXMLUtil.getWelcomeFile(request.getContext());

        if(uri.endsWith(".jsp")){
            JspServlet.getInstance().service(request,response);
            return;
        }

        String fileName = StrUtil.removePrefix(uri, "/");
        File file = FileUtil.file(request.getRealPath(fileName));

        if (file.exists()) {
            String extName = FileUtil.extName(file);
            String mimeType = WebXMLUtil.getMimeType(extName);
            response.setContentType(mimeType);

            byte[] body = FileUtil.readBytes(file);
            response.setBody(body);
            if (fileName.equals("timeConsume.html"))
                ThreadUtil.sleep(1000);

            response.setStatus(Constant.CODE_200);
        } else {
            response.setStatus(Constant.CODE_404);
        }
    }
}
 
