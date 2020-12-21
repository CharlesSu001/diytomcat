/**
 * Copyright (C), 2015-2020, XXX有限公司
 * FileName: ApplicationContext
 * Author:   苏晨宇
 * Date:     2020/12/15 20:47
 * Description: 继承BaseServletContext
 * History:
 * <author>          <time>          <version>          <desc>
 * 作者姓名           修改时间           版本号              描述
 */
package cn.how2j.diytomcat.http;

import cn.how2j.diytomcat.catalina.Context;

import java.io.File;
import java.util.*;

/**
 * 〈一句话功能简述〉<br>
 * 〈继承BaseServletContext〉
 *
 * @author 苏晨宇
 * @create 2020/12/15
 * @since 1.0.0
 */
public class ApplicationContext extends BaseServletContext {
    private Map<String, Object> attributesMap;
    private Context context;

    public ApplicationContext(Context context){
        this.attributesMap=new HashMap<>();
        this.context=context;
    }


    public void removeAttribute(String name) {
        attributesMap.remove(name);
    }

    public void setAttribute(String name, Object value) {
        attributesMap.put(name, value);
    }

    public Object getAttribute(String name) {
        return attributesMap.get(name);
    }

    public Enumeration<String> getAttributeNames() {
        Set<String> keys = attributesMap.keySet();
        return Collections.enumeration(keys);
    }

    public String getRealPath(String path) {
        return new File(context.getDocBase(), path).getAbsolutePath();
    }


}
 
