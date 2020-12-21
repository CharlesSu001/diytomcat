/**
 * Copyright (C), 2015-2020, XXX有限公司
 * FileName: ContextXMLUtil
 * Author:   苏晨宇
 * Date:     2020/12/15 10:05
 * Description: 读取context.xml信息
 * History:
 * <author>          <time>          <version>          <desc>
 * 作者姓名           修改时间           版本号              描述
 */
package cn.how2j.diytomcat.util;

import cn.hutool.core.io.FileUtil;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

/**
 * 〈一句话功能简述〉<br>
 * 〈读取context.xml信息〉
 *
 * @author 苏晨宇
 * @create 2020/12/15
 * @since 1.0.0
 */
public class ContextXMLUtil {
    public static String getWatchedResource() {
        try {
            String xml = FileUtil.readUtf8String(Constant.contextXmlFile);
            Document d = Jsoup.parse(xml);
            Element e = d.select("WatchedResource").first();
            return e.text();
        } catch (Exception e) {
            e.printStackTrace();
            return "WEB-INF/web.xml";
        }
    }
}
 
