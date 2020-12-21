/**
 * Copyright (C), 2015-2020, XXX有限公司
 * FileName: JspClassLoader
 * Author:   苏晨宇
 * Date:     2020/12/18 15:51
 * Description: jsp类加载器
 * History:
 * <author>          <time>          <version>          <desc>
 * 作者姓名           修改时间           版本号              描述
 */
package cn.how2j.diytomcat.classloader;

import cn.how2j.diytomcat.catalina.Context;
import cn.how2j.diytomcat.util.Constant;
import cn.hutool.core.util.StrUtil;

import java.io.File;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.HashMap;
import java.util.Map;

/**
 * 〈一句话功能简述〉<br> 
 * 〈jsp类加载器〉
 *
 * @author 苏晨宇
 * @create 2020/12/18
 * @since 1.0.0
 */
public class JspClassLoader extends URLClassLoader {

    private static Map<String,JspClassLoader> map=new HashMap<>();

    public static void invalidJspClassLoader(String uri, Context context){
        String key=context.getPath()+"/"+uri;
        map.remove(key);
    }

    public static JspClassLoader getJspClassLoader(String uri,Context context){
        String key=context.getPath()+"/"+uri;
        JspClassLoader loader=map.get(key);
        if(loader==null){
            loader=new JspClassLoader(context);
            map.put(key,loader);
        }
        return loader;
    }

    private JspClassLoader(Context context){
        super(new URL[]{},context.getWebappClassLoader());
        try{
            String subFolder;
            String path=context.getPath();
            if(path.equals("/"))
                subFolder="_";
            else
                subFolder= StrUtil.subAfter(path,"/",false);
            File classFolder=new File(Constant.workFolder,subFolder);
            URL url=new URL("file:"+classFolder.getAbsolutePath()+"/");
            this.addURL(url);
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
 
