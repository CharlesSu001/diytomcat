/**
 * Copyright (C), 2015-2020, XXX有限公司
 * FileName: CusomizedClassLoader
 * Author:   苏晨宇
 * Date:     2020/12/15 17:04
 * Description: 自定义类加载器
 * History:
 * <author>          <time>          <version>          <desc>
 * 作者姓名           修改时间           版本号              描述
 */
package cn.how2j.diytomcat.test;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.StrUtil;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * 〈一句话功能简述〉<br>
 * 〈自定义类加载器〉
 *
 * @author 苏晨宇
 * @create 2020/12/15
 * @since 1.0.0
 */
public class CustomizedClassLoader extends ClassLoader {
    private File classesFolder = new File(System.getProperty("user.dir"), "classes_4_test");

    protected Class<?> findClass(String qualifiedName) throws ClassNotFoundException {
        byte[] data = loadClassData(qualifiedName);
        return defineClass(qualifiedName, data, 0, data.length);
    }

    private byte[] loadClassData(String fullQualifiedName) throws ClassNotFoundException {
        String fileName = StrUtil.replace(fullQualifiedName, ".", "/") + ".class";
        File classFile = new File(classesFolder, fileName);
        if (!classFile.exists())
            throw new ClassNotFoundException(fullQualifiedName);
        return FileUtil.readBytes(classFile);
    }

    public static void main(String[] args) throws Exception {

        CustomizedClassLoader loader = new CustomizedClassLoader();
        Class<?> how2jClass = loader.loadClass("cn.how2j.diytomcat.test.HOW2J");
        Object o = how2jClass.newInstance();
        Method m = how2jClass.getMethod("hello");

        m.invoke(o);
        System.out.println(how2jClass.getClassLoader());

    }

}
 
