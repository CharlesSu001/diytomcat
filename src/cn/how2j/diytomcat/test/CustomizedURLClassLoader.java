/**
 * Copyright (C), 2015-2020, XXX有限公司
 * FileName: CustomizedURLClassLoader
 * Author:   苏晨宇
 * Date:     2020/12/15 18:10
 * Description: 自定义类加载器 加载jar
 * History:
 * <author>          <time>          <version>          <desc>
 * 作者姓名           修改时间           版本号              描述
 */
package cn.how2j.diytomcat.test;

import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;

/**
 * 〈一句话功能简述〉<br> 
 * 〈自定义类加载器 加载jar〉
 *
 * @author 苏晨宇
 * @create 2020/12/15
 * @since 1.0.0
 */
public class CustomizedURLClassLoader extends URLClassLoader {
    public CustomizedURLClassLoader(URL[] urls){
        super(urls);
    }

    public static void main(String[] args) throws Exception{
        URL url=new URL("file:f:/project6/diytomcat/jar_4_test/test.jar");
        URL[] urls=new URL[]{url};
        CustomizedURLClassLoader loader=new CustomizedURLClassLoader(urls);
        Class<?> how2jClass=loader.loadClass("cn.how2j.diytomcat.test.HOW2J");
        Object o=how2jClass.newInstance();
        Method m=how2jClass.getMethod("hello");
        m.invoke(o);

        System.out.println(how2jClass.getClassLoader());
        CustomizedURLClassLoader loader1=new CustomizedURLClassLoader(urls);
        Class<?> how2jClass1=loader1.loadClass("cn.how2j.diytomcat.test.HOW2J");

        CustomizedURLClassLoader loader2=new CustomizedURLClassLoader(urls);
        Class<?> how2jClass2=loader2.loadClass("cn.how2j.diytomcat.test.HOW2J");
        System.out.println(how2jClass1==how2jClass2);
    }

}
 
