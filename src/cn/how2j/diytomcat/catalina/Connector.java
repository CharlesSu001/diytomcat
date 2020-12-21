/**
 * Copyright (C), 2015-2020, XXX有限公司
 * FileName: Connector
 * Author:   苏晨宇
 * Date:     2020/12/14 20:27
 * Description: 映射Connector节点
 * History:
 * <author>          <time>          <version>          <desc>
 * 作者姓名           修改时间           版本号              描述
 */
package cn.how2j.diytomcat.catalina;

import cn.how2j.diytomcat.http.Request;
import cn.how2j.diytomcat.http.Response;
import cn.how2j.diytomcat.util.Constant;
import cn.how2j.diytomcat.util.ThreadPoolUtil;
import cn.how2j.diytomcat.util.WebXMLUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.thread.ThreadUtil;
import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.log.LogFactory;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * 〈一句话功能简述〉<br>
 * 〈映射Connector节点〉
 *
 * @author 苏晨宇
 * @create 2020/12/14
 * @since 1.0.0
 */
public class Connector implements Runnable {

    int port;
    private Service service;
    private String compression;
    private int compressionMinSize;
    private String noCompressionUserAgents;
    private String compressableMimeType;

    public String getCompression() {
        return compression;
    }

    public void setCompression(String compression) {
        this.compression = compression;
    }

    public int getCompressionMinSize() {
        return compressionMinSize;
    }

    public void setCompressionMinSize(int compressionMinSize) {
        this.compressionMinSize = compressionMinSize;
    }

    public String getNoCompressionUserAgents() {
        return noCompressionUserAgents;
    }

    public void setNoCompressionUserAgents(String noCompressionUserAgents) {
        this.noCompressionUserAgents = noCompressionUserAgents;
    }

    public String getCompressableMimeType() {
        return compressableMimeType;
    }

    public void setCompressableMimeType(String compressableMimeType) {
        this.compressableMimeType = compressableMimeType;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public Service getService() {
        return service;
    }

    public Connector(Service service) {
        this.service = service;
    }


    public void init() {
        LogFactory.get().info("Initializing ProtocolHandler [http-bio-{}]", port);
    }

    public void start() {
        LogFactory.get().info("Starting ProtocolHandler [http-bio-{}]", port);
        new Thread(this).start();
    }

    @Override
    public void run() {
        try {

            ServerSocket ss = new ServerSocket(port);

            while (true) {
                Socket s = ss.accept();
                Runnable r = () -> {
                    try {
                        Request request=new Request(s,Connector.this);
                        Response response=new Response();
                        HttpProcessor processor=new HttpProcessor();
                        processor.execute(s,request,response);
                    } catch (IOException e) {
                       e.printStackTrace();
                    }
                };
                ThreadPoolUtil.run(r);

            }
        } catch (IOException e) {
            LogFactory.get().error(e);
            e.printStackTrace();
        }
    }



}
 
