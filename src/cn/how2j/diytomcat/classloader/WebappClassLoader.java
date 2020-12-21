/**
 * Copyright (C), 2015-2020, XXX有限公司
 * FileName: WebappClassLoader
 * Author:   苏晨宇
 * Date:     2020/12/15 19:20
 * Description: 加载web应用
 * History:
 * <author>          <time>          <version>          <desc>
 * 作者姓名           修改时间           版本号              描述
 */
package cn.how2j.diytomcat.classloader;

import cn.hutool.core.io.FileUtil;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.List;

/**
 * 〈一句话功能简述〉<br> 
 * 〈加载web应用〉
 *
 * @author 苏晨宇
 * @create 2020/12/15
 * @since 1.0.0
 */
public class WebappClassLoader extends URLClassLoader {
    public WebappClassLoader(String docBase,ClassLoader commonClassLoader){
        super(new URL[]{},commonClassLoader);

        try{
            File webinfFolder=new File(docBase,"WEB-INF");
            File classesFolder=new File(webinfFolder,"classes");
            File libFolder=new File(webinfFolder,"lib");

            URL url;
            url=new URL("file:"+classesFolder.getAbsolutePath()+"/");
            this.addURL(url);
            List<File> jarFiles= FileUtil.loopFiles(libFolder);
            for(File file:jarFiles){
                url=new URL("file:"+file.getAbsolutePath());
                this.addURL(url);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public void stop(){
        try{
            close();
        }catch (IOException e){
            e.printStackTrace();
        }
    }
}
 
