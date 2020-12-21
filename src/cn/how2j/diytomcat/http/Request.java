/**
 * Copyright (C), 2015-2020, XXX有限公司
 * FileName: Request
 * Author:   苏晨宇
 * Date:     2020/12/13 16:45
 * Description: 请求对象
 * History:
 * <author>          <time>          <version>          <desc>
 * 作者姓名           修改时间           版本号              描述
 */
package cn.how2j.diytomcat.http;

import cn.how2j.diytomcat.Bootstrap;
import cn.how2j.diytomcat.catalina.*;
import cn.how2j.diytomcat.util.MiniBrowser;
import cn.hutool.core.convert.Convert;
import cn.hutool.core.io.IoUtil;
import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.core.util.URLUtil;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletContext;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.*;

/**
 * 〈一句话功能简述〉<br>
 * 〈请求对象〉
 *
 * @author 苏晨宇
 * @create 2020/12/13
 * @since 1.0.0
 */
public class Request extends BaseRequest {
    private String requestString;
    private String uri;
    private Socket socket;
    private Context context;
    private Connector connector;
    private String method;
    private String queryString;
    private Map<String, String[]> parameterMap;
    private Map<String, String> headerMap;
    private Cookie[] cookies;
    private HttpSession session;
    private boolean forwarded;
    private Map<String,Object> attributesMap;


    public Request(Socket socket, Connector connector) throws IOException {
        this.socket = socket;
        this.connector=connector;
        this.parameterMap = new HashMap<>();
        this.headerMap = new HashMap<>();
        this.attributesMap=new HashMap<>();
        parseHttpRequest();
        if (StrUtil.isEmpty(requestString))
            return;
        parseUri();
        parseContext();
        parseMethod();
        if (!(context.getPath().equals("/"))) {
            uri = StrUtil.removePrefix(uri, context.getPath());
            if (StrUtil.isEmpty(uri))
                uri = "/";
        }

        parseParameters();
        parseHeaders();
        parseCookies();
        System.out.println(headerMap);
    }


    private void parseMethod() {
        method = StrUtil.subBefore(requestString, " ", false);
    }

    private void parseHttpRequest() throws IOException {
        InputStream is = this.socket.getInputStream();
        byte[] bytes = MiniBrowser.readBytes(is, false);
        requestString = new String(bytes, "utf-8");
    }

    private void parseUri() {
        String temp;
        temp = StrUtil.subBetween(requestString, " ", " ");
        if (!StrUtil.contains(temp, '?')) {
            uri = temp;
            return;
        }

        temp = StrUtil.subBefore(temp, '?', false);
        uri = temp;
    }

    private void parseContext() {
        Service service=connector.getService();
        Engine engine = service.getEngine();
        context = engine.getDefaultHost().getContext(uri);
        if (context != null)
            return;
        String path = StrUtil.subBetween(uri, "/", "/");
        if (path == null)
            path = "/";
        else
            path = "/" + path;
        context = engine.getDefaultHost().getContext(path);
        if (context == null)
            context = engine.getDefaultHost().getContext("/");
    }

    private void parseParameters() {
        if (this.getMethod().equals("GET")) {
            String url = StrUtil.subBetween(requestString, " ", " ");
            if (StrUtil.contains(url, '?')) {
                queryString = StrUtil.subAfter(url, '?', false);
            }
        }

        if (this.getMethod().equals("POST")) {
            queryString = StrUtil.subAfter(requestString, "\r\n\r\n", false);
        }

        if (queryString == null || queryString.length() == 0)
            return;

        queryString = URLUtil.decode(queryString);
        String[] parameterValues = queryString.split("&");
        if (parameterValues != null) {
            for (String parameterValue : parameterValues) {
                String[] nameValues = parameterValue.split("=");
                String name = nameValues[0];
                String value = nameValues[1];
                String[] values = parameterMap.get(name);
                if (values == null) {
                    values = new String[]{value};
                    parameterMap.put(name, values);
                } else {
                    values = ArrayUtil.append(values, value);
                    parameterMap.put(name, values);
                }
            }
        }
    }

    private void parseHeaders() {
        StringReader stringReader = new StringReader(requestString);
        List<String> lines = new ArrayList<>();
        IoUtil.readLines(stringReader, lines);
        for (int i = 1; i < lines.size(); i++) {
            String line = lines.get(i);
            if (line.length() == 0)
                break;
            String[] segs = line.split(":");
            String headerName = segs[0].toLowerCase();
            String headerValue = segs[1];
            headerMap.put(headerName, headerValue);

        }
    }

    public boolean isForwarded(){
        return forwarded;
    }

    public void setForwarded(boolean forwarded){
        this.forwarded=forwarded;
    }

    public void setUri(String uri){
        this.uri=uri;
    }

    public Socket getSocket(){
        return socket;
    }

    public String getUri() {
        return uri;
    }

    public String getRequestString() {
        return requestString;
    }


    public Context getContext() {
        return context;
    }

    public Connector getConnector(){
        return connector;
    }

    public String getMethod() {
        return method;
    }

    public Cookie[] getCookies() {
        return cookies;
    }

    public RequestDispatcher getRequestDispatcher(String uri){
        return new ApplicationRequestDispatcher(uri);
    }

    public HttpSession getSession(){
        return session;
    }

    public void setSession(HttpSession session){
        this.session=session;
    }

    public String getJSessionIdFromCookie(){
        if(cookies==null)
            return null;
        for(Cookie cookie:cookies){
            if(cookie.getName().equals("JSESSIONID")){
                return cookie.getValue();
            }
        }
        return null;
    }

    private void parseCookies(){
        List<Cookie> cookieList=new ArrayList<>();
        String cookies=headerMap.get("cookie");
        if(cookies!=null){
            String[] pairs=StrUtil.split(cookies,",");
            for(String pair:pairs){
                if(StrUtil.isBlank(pair))
                    continue;
                String[] segs=StrUtil.split(pair,"=");
                String name=segs[0].trim();
                String value=segs[1].trim();
                Cookie cookie=new Cookie(name,value);
                cookieList.add(cookie);
            }
        }

        this.cookies=ArrayUtil.toArray(cookieList,Cookie.class);
    }

    public ServletContext getServletContext() {
        return context.getServletContext();
    }

    public String getRealPath(String path) {
        return getServletContext().getRealPath(path);
    }

    public String getParameter(String name) {
        String[] values = parameterMap.get(name);
        if (values != null && values.length != 0)
            return values[0];
        return null;
    }

    public Map getParameterMap() {
        return parameterMap;
    }

    public Enumeration getParameterNames() {
        return Collections.enumeration(parameterMap.keySet());
    }

    public String[] getParameterValues(String name) {
        return parameterMap.get(name);
    }

    public String getHeader(String name) {
        if (name == null)
            return null;
        name = name.toLowerCase();
        return headerMap.get(name);
    }

    public Enumeration getHeaderNames() {
        Set keys = headerMap.keySet();
        return Collections.enumeration(keys);
    }

    public int getIntHeader(String name) {
        String value = headerMap.get(name);
        return Convert.toInt(value, 0);
    }

    public String getLocalAddr() {
        return socket.getLocalAddress().getHostAddress();
    }

    public String getLocalName() {
        return socket.getLocalAddress().getHostName();
    }

    public int getLocalPort() {
        return socket.getLocalPort();
    }

    public String getProtocol() {
        return "HTTP:/1.1";
    }

    public String getRemoteAddr() {
        InetSocketAddress isa = (InetSocketAddress) socket.getRemoteSocketAddress();
        String temp = isa.getAddress().toString();
        return StrUtil.subAfter(temp, "/", false);
    }

    public String getRemoteHost() {
        InetSocketAddress isa = (InetSocketAddress) socket.getRemoteSocketAddress();
        return isa.getHostName();
    }

    public int getRemotePort() {
        return socket.getPort();
    }

    public String getScheme() {
        return "http";
    }

    public String getServerName() {
        return getHeader("host").trim();
    }


    public void removeAttribute(String name){
        attributesMap.remove(name);
    }

    public void setAttribute(String name,Object value){
        attributesMap.put(name,value);
    }

    public Object getAttribute(String name){
        return attributesMap.get(name);
    }

    public Enumeration<String> getAttributeNames(){
        Set<String> keys=attributesMap.keySet();
        return Collections.enumeration(keys);
    }


    public int getServerPort() {
        return getLocalPort();
    }

    public String getContextPath() {
        String result = this.context.getPath();
        if (result.equals("/"))
            return "";
        return result;
    }

    public String getRequestURI() {
        return uri;
    }

    public StringBuffer getRequestURL() {
        StringBuffer url = new StringBuffer();
        String scheme = getScheme();
        int port = getServerPort();
        if (port < 0) {
            port = 80;//Work around java.net.URL bug
        }

        url.append(scheme);
        url.append("://");
        url.append(getServerName());
        if ((scheme.equals("http") && (port != 80)) || (scheme.equals("https") && (port != 443))) {
            url.append(":");
            url.append(port);
        }
        url.append(getRequestURI());
        return url;
    }

    public String getServletPath() {
        return uri;
    }


}
 
