/**
 * Copyright (C), 2015-2020, XXX有限公司
 * FileName: Host
 * Author:   苏晨宇
 * Date:     2020/12/14 9:12
 * Description: host类
 * History:
 * <author>          <time>          <version>          <desc>
 * 作者姓名           修改时间           版本号              描述
 */
package cn.how2j.diytomcat.catalina;

import cn.how2j.diytomcat.util.Constant;
import cn.how2j.diytomcat.util.ServerXMLUtil;
import cn.how2j.diytomcat.watcher.WarFileWatcher;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.RuntimeUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.log.LogFactory;
import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 〈一句话功能简述〉<br>
 * 〈host类〉
 *
 * @author 苏晨宇
 * @create 2020/12/14
 * @since 1.0.0
 */
public class Host {
    private String name;
    private Map<String, Context> contextMap;
    private Engine engine;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Host(String name, Engine engine) {
        this.contextMap = new HashMap<>();
        this.name = name;
        this.engine = engine;

        scanContextsOnWebAppsFolder();
        scanContextsInServerXML();
        scanWarOnWebAppsFolder();

        new WarFileWatcher(this).start();
    }


    public void load(File folder){
        String path=folder.getName();
        if(path.equals("ROOT"))
            path="/";
        else
            path="/"+path;
        String docBase=folder.getAbsolutePath();
        Context context=new Context(path,docBase,this,false);
        contextMap.put(context.getPath(),context);

    }

    public void reload(Context context) {
        LogFactory.get().info("Reloading Context with name [{}] has started",context.getPath());
        String path=context.getPath();
        String docBase=context.getDocBase();
        boolean reloadable=context.isReloadable();
        //stop
        context.stop();

        //remove
        contextMap.remove(path);

        //allocate new context
        Context newContext=new Context(path,docBase,this,reloadable);
        //assign it to map
        contextMap.put(newContext.getPath(),newContext);
        LogFactory.get().info("Reloading Context with name [{}] has completed",context.getPath());
    }

    public void loadWar(File warFile){
        String fileName=warFile.getName();
        String folderName= StrUtil.subBefore(fileName,".",true);
        //查看是否有对应的Context
        Context context=getContext("/"+folderName);
        if(context!=null)
            return;
        //查看是否有对应的文件夹
        File folder=new File(Constant.webappsFolder,folderName);
        if(folder.exists())
            return;
        //移动war文件，jar命令只支持解压到当前目录
        File tempWarFile= FileUtil.file(Constant.webappsFolder,folderName,fileName);
        File contextFolder=tempWarFile.getParentFile();
        contextFolder.mkdir();
        FileUtil.copyFile(warFile,tempWarFile);
        //解压
        String command="jar xvf "+fileName;
        Process p= RuntimeUtil.exec(null,contextFolder,command);
        try{
            p.waitFor();
        }catch (InterruptedException e){
            e.printStackTrace();
        }

        //解压之后删除临时war
        tempWarFile.delete();
        //创建新的Context
        load(contextFolder);

    }

    private void scanContextsInServerXML() {
        List<Context> contexts = ServerXMLUtil.getContexts(this);
        for (Context context : contexts) {
            contextMap.put(context.getPath(), context);
        }
    }

    private void scanContextsOnWebAppsFolder() {
        File[] folders = Constant.webappsFolder.listFiles();
        for (File folder : folders) {
            if (!folder.isDirectory())
                continue;
            loadContext(folder);
        }
    }

    private void scanWarOnWebAppsFolder(){
        File folder=FileUtil.file(Constant.webappsFolder);
        File[] files=folder.listFiles();
        for(File file:files){
            if(!file.getName().toLowerCase().endsWith(".war"))
                continue;
            loadWar(file);
        }
    }

    private void loadContext(File folder)  {
        String path = folder.getName();
        if (path.equals("ROOT"))
            path = "/";
        else
            path = "/" + path;

        String docBase = folder.getAbsolutePath();
        Context context = new Context(path, docBase,this,true);

        contextMap.put(context.getPath(), context);
    }

    public Context getContext(String path) {
        return contextMap.get(path);
    }
}
 
