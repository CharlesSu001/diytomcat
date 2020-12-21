/**
 * Copyright (C), 2015-2020, XXX有限公司
 * FileName: TestClassLoader
 * Author:   苏晨宇
 * Date:     2020/12/15 16:51
 * Description: 测试类对象
 * History:
 * <author>          <time>          <version>          <desc>
 * 作者姓名           修改时间           版本号              描述
 */
package cn.how2j.diytomcat.test;

/**
 * 〈一句话功能简述〉<br> 
 * 〈测试类对象〉
 *
 * @author 苏晨宇
 * @create 2020/12/15
 * @since 1.0.0
 */
public class TestClassLoader {
    public static void main(String[] args){
        Object o=new Object();
        System.out.println(o);
        Class<?> clazz=o.getClass();
        System.out.println(clazz);
        System.out.println(Object.class.getClassLoader());
        System.out.println(TestClassLoader.class.getClassLoader());
    }
}
 
