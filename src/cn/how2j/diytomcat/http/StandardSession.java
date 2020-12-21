/**
 * Copyright (C), 2015-2020, XXX有限公司
 * FileName: StandardSession
 * Author:   苏晨宇
 * Date:     2020/12/17 18:17
 * Description: 实现HttpSession
 * History:
 * <author>          <time>          <version>          <desc>
 * 作者姓名           修改时间           版本号              描述
 */
package cn.how2j.diytomcat.http;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpSessionContext;
import java.util.*;

/**
 * 〈一句话功能简述〉<br>
 * 〈实现HttpSession〉
 *
 * @author 苏晨宇
 * @create 2020/12/17
 * @since 1.0.0
 */
public class StandardSession implements HttpSession {
    private Map<String, Object> attributesMap;
    private String id;
    private long creationTime;
    private long lastAccessedTime;
    private ServletContext servletContext;
    private int maxInactiveInterval;


    public StandardSession(String jsessionid, ServletContext servletContext) {
        this.attributesMap = new HashMap<>();
        this.id = jsessionid;
        this.creationTime = System.currentTimeMillis();
        this.servletContext = servletContext;
    }


    @Override
    public long getCreationTime() {
        return this.creationTime;
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public long getLastAccessedTime() {
        return lastAccessedTime;
    }

    public void setLastAccessedTime(long lastAccessedTime){
        this.lastAccessedTime=lastAccessedTime;
    }


    @Override
    public ServletContext getServletContext() {
        return servletContext;
    }

    @Override
    public void setMaxInactiveInterval(int i) {
        this.maxInactiveInterval = i;
    }

    @Override
    public int getMaxInactiveInterval() {
        return this.maxInactiveInterval;
    }

    @Override
    public HttpSessionContext getSessionContext() {
        return null;
    }

    @Override
    public Object getAttribute(String s) {
        return attributesMap.get(s);
    }

    @Override
    public Object getValue(String s) {
        return null;
    }

    @Override
    public Enumeration<String> getAttributeNames() {
        Set<String> keys = attributesMap.keySet();
        return Collections.enumeration(keys);
    }

    @Override
    public String[] getValueNames() {
        return new String[0];
    }

    @Override
    public void setAttribute(String s, Object o) {
        attributesMap.put(s, o);
    }

    @Override
    public void putValue(String s, Object o) {

    }

    @Override
    public void removeAttribute(String s) {
        attributesMap.remove(s);

    }

    @Override
    public void removeValue(String s) {

    }

    @Override
    public void invalidate() {
        attributesMap.clear();
    }

    @Override
    public boolean isNew() {
        return creationTime == lastAccessedTime;
    }
}
 
