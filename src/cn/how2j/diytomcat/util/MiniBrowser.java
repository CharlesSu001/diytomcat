/**
 * Copyright (C), 2015-2020, XXX有限公司
 * FileName: MiniBrowser
 * Author:   苏晨宇
 * Date:     2020/12/13 15:19
 * Description: 模仿浏览器获取http响应内容
 * History:
 * <author>          <time>          <version>          <desc>
 * 作者姓名           修改时间           版本号              描述
 */
package cn.how2j.diytomcat.util;

import cn.hutool.http.HttpUtil;

import java.io.*;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.URL;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * 〈一句话功能简述〉<br>
 * 〈模仿浏览器获取http响应内容〉
 *
 * @author 苏晨宇
 * @create 2020/12/13
 * @since 1.0.0
 */
public class MiniBrowser {
    public static void main(String[] args) {
        String url = "http://static.how2j.cn/diytomcat.html";
        String contentString = getContentString(url, false);
        System.out.println(contentString);
        String httpString = getHttpString(url, false);
        System.out.println(httpString);
    }

    public static byte[] getContentBytes(String url, Map<String, Object> params, boolean isGet) {
        return getContentBytes(url, false, params, isGet);
    }

    public static byte[] getContentBytes(String url, boolean gzip) {
        return getContentBytes(url, gzip, null, true);
    }

    public static byte[] getContentBytes(String url) {
        return getContentBytes(url, false, null, true);
    }

    public static String getContentString(String url, Map<String, Object> params, boolean isGet) {
        return getContentString(url, false, params, isGet);
    }

    public static String getContentString(String url, boolean gzip) {
        return getContentString(url, gzip, null, true);
    }

    public static String getContentString(String url) {
        return getContentString(url, false, null, true);
    }


    public static String getContentString(String url, boolean gzip, Map<String, Object> params, boolean isGet) {
        byte[] result = getContentBytes(url, gzip, params, isGet);
        if (result == null)
            return null;
        try {
            return new String(result, "utf-8").trim();
        } catch (UnsupportedEncodingException e) {
            return null;
        }
    }

    public static byte[] getContentBytes(String url, boolean gzip, Map<String, Object> params, boolean isGet) {
        byte[] response = getHttpBytes(url, gzip, params, isGet);
        byte[] doubleReturn = "\r\n\r\n".getBytes();

        int pos = -1;
        for (int i = 0; i < response.length - doubleReturn.length; i++) {
            byte[] temp = Arrays.copyOfRange(response, i, i + doubleReturn.length);
            if (Arrays.equals(temp, doubleReturn)) {
                pos = i;
                break;
            }
        }
        if (pos == -1)
            return null;

        pos += doubleReturn.length;
        byte[] result = Arrays.copyOfRange(response, pos, response.length);
        return result;
    }

    public static String getHttpString(String url, boolean gzip) {
        byte[] bytes = getHttpBytes(url, gzip, null, true);
        return new String(bytes).trim();
    }

    public static String getHttpString(String url) {
        return getHttpString(url, false, null, true);
    }

    public static String getHttpString(String url, boolean gzip, Map<String, Object> params, boolean isGet) {
        byte[] bytes = getHttpBytes(url, gzip, params, isGet);
        return new String(bytes).trim();
    }

    public static byte[] getHttpBytes(String url, boolean gzip, Map<String, Object> params, boolean isGet) {
        String method = isGet ? "GET" : "POST";
        byte[] result = null;
        try {
            URL u = new URL(url);
            Socket client = new Socket();
            int port = u.getPort();
            if (port == -1)
                port = 80;
            InetSocketAddress inetSocketAddress = new InetSocketAddress(u.getHost(), port);
            client.connect(inetSocketAddress, 1000);
            Map<String, String> requestHeaders = new HashMap<>();

            requestHeaders.put("Host", u.getHost() + ":" + port);
            requestHeaders.put("Accept", "text/html");
            requestHeaders.put("Connection", "close");
            requestHeaders.put("User-Agent", "how2j mini browser / java 1.8");

            if (gzip)
                requestHeaders.put("Accept-Encoding", "gzip");

            String path = u.getPath();
            if (path.length() == 0)
                path = "/";

            if (params != null && isGet) {
                String paramsString = HttpUtil.toParams(params);
                path = path + "?" + paramsString;
            }

            String firstLine = method + " " + path + " HTTP/1.1\r\n";

            StringBuffer httpRequestString = new StringBuffer();
            httpRequestString.append(firstLine);
            Set<String> headers = requestHeaders.keySet();
            for (String header : headers) {
                String headerLine = header + ":" + requestHeaders.get(header) + "\r\n";
                httpRequestString.append(headerLine);
            }

            if (params != null && !isGet) {
                String paramsString = HttpUtil.toParams(params);
                httpRequestString.append("\r\n");
                httpRequestString.append(paramsString);
            }

            PrintWriter pWriter = new PrintWriter(client.getOutputStream(), true);
            pWriter.println(httpRequestString);
            InputStream is = client.getInputStream();

//            int buffer_size = 1024;
//
//            ByteArrayOutputStream baos = new ByteArrayOutputStream();
//            byte buffer[] = new byte[buffer_size];
//            while (true) {
//                int length = is.read(buffer);
//                if (length == -1)
//                    break;
//                baos.write(buffer, 0, length);
//                if (length != buffer_size)
//                    break;
//            }
//            result = baos.toByteArray();

            result = readBytes(is, true);
            client.close();
        } catch (Exception e) {
            e.printStackTrace();
            try {
                result = e.toString().getBytes("utf-8");
            } catch (UnsupportedEncodingException e1) {
                e1.printStackTrace();
            }
        }
        return result;
    }


    public static byte[] readBytes(InputStream is, boolean fully) throws IOException {
        int buffer_size = 1024;
        byte buffer[] = new byte[buffer_size];
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        while (true) {
            int length = is.read(buffer);
            if (length == -1)
                break;
            baos.write(buffer, 0, length);
            if (!fully && length != buffer_size)
                break;
        }

        byte[] result = baos.toByteArray();
        return result;
    }
}
 
