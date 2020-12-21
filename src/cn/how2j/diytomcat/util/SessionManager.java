/**
 * Copyright (C), 2015-2020, XXX有限公司
 * FileName: SessionManager
 * Author:   苏晨宇
 * Date:     2020/12/17 18:32
 * Description: 用于管理session
 * History:
 * <author>          <time>          <version>          <desc>
 * 作者姓名           修改时间           版本号              描述
 */
package cn.how2j.diytomcat.util;

import cn.how2j.diytomcat.http.Request;
import cn.how2j.diytomcat.http.Response;
import cn.how2j.diytomcat.http.StandardSession;
import cn.hutool.core.convert.Convert;
import cn.hutool.core.thread.ThreadUtil;
import cn.hutool.core.util.RandomUtil;
import cn.hutool.crypto.SecureUtil;
import com.sun.org.apache.bcel.internal.generic.NEW;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import javax.servlet.ServletContext;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.*;

/**
 * 〈一句话功能简述〉<br>
 * 〈用于管理session〉
 *
 * @author 苏晨宇
 * @create 2020/12/17
 * @since 1.0.0
 */
public class SessionManager {
    private static Map<String, StandardSession> sessionMap = new HashMap<>();
    private static int defaultTimeout = getTimeout();
    static{
        startSessionOutdateCheckThread();
    }

    private static int getTimeout() {
        int defaultResult = 30;
        try {
            Document d = Jsoup.parse(Constant.webXmlFile, "utf-8");
            Elements es = d.select("session-config session-timeout");
            if (es.isEmpty()) {
                return defaultResult;
            }
            return Convert.toInt(es.get(0).text());
        } catch (IOException e) {
            return defaultResult;
        }
    }

    private static void startSessionOutdateCheckThread(){
        new Thread(){
            public void run(){
                while(true){
                    checkOutDateSession();
                    ThreadUtil.sleep(1000*30);
                }
            }
        }.start();
    }

    private static void checkOutDateSession(){
        Set<String> jsessionids=sessionMap.keySet();
        List<String> outdateJessionIds=new ArrayList<>();
        for(String jsessionid:jsessionids){
            StandardSession session=sessionMap.get(jsessionid);
            long interval=System.currentTimeMillis()-session.getLastAccessedTime();
            if(interval>session.getMaxInactiveInterval()*1000)
                outdateJessionIds.add(jsessionid);
        }

        for(String jsessionid:outdateJessionIds){
            sessionMap.remove(jsessionid);
        }
    }

    public static synchronized String generateSessionId(){
        String result=null;
        byte[] bytes= RandomUtil.randomBytes(16);
        result=new String(bytes);
        result= SecureUtil.md5(result);
        result=result.toUpperCase();
        return result;
    }

    private static void createCookieBySession(HttpSession session,Request request,Response response){
        Cookie cookie=new Cookie("JSESSIONID",session.getId());
        cookie.setMaxAge(session.getMaxInactiveInterval());
        cookie.setPath(request.getContext().getPath());
        response.addCookie(cookie);
    }

    private static HttpSession newSession(Request request, Response response){
        ServletContext servletContext=request.getServletContext();
        String sid=generateSessionId();
        StandardSession session=new StandardSession(sid,servletContext);
        session.setMaxInactiveInterval(defaultTimeout);
        sessionMap.put(sid,session);
        createCookieBySession(session,request,response);
        return session;
    }

    public static HttpSession getSession(String jsessionid,Request request,Response response){
        if(jsessionid==null){
            return newSession(request,response);
        }else {
            StandardSession currentSession=sessionMap.get(jsessionid);
            if(currentSession==null){
                return newSession(request,response);
            }else{
                currentSession.setLastAccessedTime(System.currentTimeMillis());
                createCookieBySession(currentSession,request,response);
                return currentSession;
            }
        }
    }





}
 
