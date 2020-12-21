/**
 * Copyright (C), 2015-2020, XXX有限公司
 * FileName: CommonClassLoader
 * Author:   苏晨宇
 * Date:     2020/12/15 18:45
 * Description: 公共类加载器
 * History:
 * <author>          <time>          <version>          <desc>
 * 作者姓名           修改时间           版本号              描述
 */
package cn.how2j.diytomcat.classloader;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;

/**
 * 〈一句话功能简述〉<br>
 * 〈公共类加载器〉
 *
 * @author 苏晨宇
 * @create 2020/12/15
 * @since 1.0.0
 */
public class CommonClassLoader extends URLClassLoader {
    public CommonClassLoader() {
        super(new URL[]{});

        try {
            File workingFolder = new File(System.getProperty("user.dir"));
            File libFolder = new File(workingFolder, "lib");
            File[] jarFiles = libFolder.listFiles();
            for (File file : jarFiles) {
                if (file.getName().endsWith("jar")) {
                    URL url = new URL("file:" + file.getAbsolutePath());
                    this.addURL(url);
                }
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
    }
}
 
