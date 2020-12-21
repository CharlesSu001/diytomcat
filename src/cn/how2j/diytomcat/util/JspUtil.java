/**
 * Copyright (C), 2015-2020, XXX有限公司
 * FileName: JspUtil
 * Author:   苏晨宇
 * Date:     2020/12/18 14:54
 * Description: Jsp转译和编译类
 * History:
 * <author>          <time>          <version>          <desc>
 * 作者姓名           修改时间           版本号              描述
 */
package cn.how2j.diytomcat.util;

import cn.how2j.diytomcat.catalina.Context;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.StrUtil;
import org.apache.jasper.JasperException;
import org.apache.jasper.JspC;

import java.io.File;
import java.io.IOException;

/**
 * 〈一句话功能简述〉<br>
 * 〈Jsp转译和编译类〉
 *
 * @author 苏晨宇
 * @create 2020/12/18
 * @since 1.0.0
 */
public class JspUtil {

    private static final String javaKeywords[] = {"abstract", "assert", "boolean", "break", "byte", "case", "catch",
            "char", "class", "const", "continue", "default", "do", "double", "else", "enum", "extends", "final",
            "finally", "float", "for", "goto", "if", "implements", "import", "instanceof", "int", "interface", "long",
            "native", "new", "package", "private", "protected", "public", "return", "short", "static", "strictfp",
            "super", "switch", "synchronized", "this", "throw", "throws", "transient", "try", "void", "volatile",
            "while"};

    public static void compileJsp(Context context, File file) throws JasperException {
        String subFolder;
        String path = context.getPath();
        if (path.equals("/"))
            subFolder = "_";
        else
            subFolder = StrUtil.subAfter(path, "/", false);

        String workPath = new File(Constant.workFolder, subFolder).getAbsolutePath() + File.separator;
        String[] args = new String[]{"-webapp", context.getDocBase().toLowerCase(), "-d", workPath.toLowerCase(), "-compile",};
        JspC jspc = new JspC();
        jspc.setArgs(args);
        jspc.execute(file);
    }

    public static final String makeJavaIdentifier(String identifier) {
        return makeJavaIdentifier(identifier, true);
    }

    public static final String makeJavaIdentifier(String identifier, boolean periodToUnderScore) {
        StringBuilder modifiedIndentifier = new StringBuilder(identifier.length());
        if (!Character.isJavaIdentifierPart(identifier.charAt(0))) {
            modifiedIndentifier.append('_');
        }

        for (int i = 0; i < identifier.length(); i++) {
            char ch = identifier.charAt(i);
            if (Character.isJavaIdentifierPart(ch) && (ch != '_' || !periodToUnderScore)) {
                modifiedIndentifier.append(ch);
            } else if (ch == '.' && periodToUnderScore) {
                modifiedIndentifier.append('_');
            } else {
                modifiedIndentifier.append(mangleChar(ch));
            }
        }

        if (isJavaKeyword(modifiedIndentifier.toString())) {
            modifiedIndentifier.append('_');
        }
        return modifiedIndentifier.toString();
    }

    public static final String mangleChar(char ch) {
        char[] result = new char[5];
        result[0] = '_';
        ;
        result[1] = Character.forDigit((ch >> 12) & 0xf, 16);
        result[2] = Character.forDigit((ch >> 8) & 0xf, 16);
        result[3] = Character.forDigit((ch >> 4) & 0xf, 16);
        result[4] = Character.forDigit((ch) & 0xf, 16);
        return new String(result);
    }


    public static boolean isJavaKeyword(String key) {
        int i = 0;
        int j = javaKeywords.length;
        while (i < j) {
            int k = (i + j) / 2;
            int result = javaKeywords[k].compareTo(key);
            if (result == 0)
                return true;
            if (result < 0) {
                i = k + 1;
            } else {
                j = k;
            }
        }
        return false;
    }


    public static String getServletPath(String uri, String subFolder) {
        String tempPath = "org/apache/jsp/" + uri;
        File tempFile = FileUtil.file(Constant.workFolder, subFolder, tempPath);
        String fileNameOnly = tempFile.getName();
        String classFileName = JspUtil.makeJavaIdentifier(fileNameOnly);
        File servletFile = new File(tempFile.getParent(), classFileName);
        return servletFile.getAbsolutePath();
    }

    public static String getServletClassPath(String uri, String subFolder) {
        return getServletPath(uri, subFolder) + ".class";
    }

    public static String getServletJavaPath(String uri, String subFolder) {
        return getServletPath(uri, subFolder) + ".java";
    }

    public static String getJspServletClassName(String uri, String subFolder) {
        File tempFile = FileUtil.file(Constant.workFolder, subFolder);
        String tempPath = tempFile.getAbsolutePath() + File.separator;
        String servletPath = getServletPath(uri, subFolder);

        String jspServletClassPath = StrUtil.subAfter(servletPath, tempPath, false);
        String jspServletClassName = StrUtil.replace(jspServletClassPath, File.separator, ".");
        return jspServletClassName;
    }


    public static void main(String[] args) {
        try {
            Context context = new Context("/javaweb", "f:/project6/javaweb/web", null, true);
            File file = new File("f:/project6/javaweb/web/index.jsp");
            compileJsp(context, file);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}
 
