/**
 * Copyright (C), 2015-2020, XXX有限公司
 * FileName: WebXMLUtil
 * Author:   苏晨宇
 * Date:     2020/12/14 18:36
 * Description: 获取Context下的欢迎文件名称
 * History:
 * <author>          <time>          <version>          <desc>
 * 作者姓名           修改时间           版本号              描述
 */
package cn.how2j.diytomcat.util;

import cn.how2j.diytomcat.catalina.Context;
import cn.hutool.core.io.FileUtil;
import cn.hutool.json.JSON;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;


import java.io.File;
import java.util.HashMap;
import java.util.Map;

/**
 * 〈一句话功能简述〉<br>
 * 〈获取Context下的欢迎文件名称〉
 *
 * @author 苏晨宇
 * @create 2020/12/14
 * @since 1.0.0
 */
public class WebXMLUtil {
    private static Map<String, String> mimeTypeMapping = new HashMap<>();

    public static String getWelcomeFile(Context context) {
        String xml = FileUtil.readUtf8String(Constant.webXmlFile);
        Document d = Jsoup.parse(xml);
        Elements es = d.select("welcome-file");
        for (Element e : es) {
            String welcomeFileName = e.text();
            File f = new File(context.getDocBase(), welcomeFileName);
            if (f.exists())
                return f.getName();
        }

        return "index.html";
    }

    private static void initMimeType() {
        String xml = FileUtil.readUtf8String(Constant.webXmlFile);
        Document d = Jsoup.parse(xml);
        Elements es = d.select("mime-mapping");
        for (Element e : es) {
            String extName = e.select("extension").first().text();
            String mimeType = e.select("mime-type").first().text();
            mimeTypeMapping.put(extName, mimeType);
        }
    }

    public static synchronized String getMimeType(String extName) {
        if (mimeTypeMapping.isEmpty())
            initMimeType();

        String mimeType = mimeTypeMapping.get(extName);
        if (mimeType == null)
            return "text/html";
        return mimeType;
    }
}
 
