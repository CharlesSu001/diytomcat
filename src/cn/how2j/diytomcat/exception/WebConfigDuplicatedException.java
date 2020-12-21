/**
 * Copyright (C), 2015-2020, XXX有限公司
 * FileName: WebConfigDuplicatedException
 * Author:   苏晨宇
 * Date:     2020/12/15 10:11
 * Description: web.xml重复配置servlet抛出异常
 * History:
 * <author>          <time>          <version>          <desc>
 * 作者姓名           修改时间           版本号              描述
 */
package cn.how2j.diytomcat.exception;

/**
 * 〈一句话功能简述〉<br>
 * 〈web.xml重复配置servlet抛出异常〉
 *
 * @author 苏晨宇
 * @create 2020/12/15
 * @since 1.0.0
 */
public class WebConfigDuplicatedException extends Exception {
    public WebConfigDuplicatedException(String msg) {
        super(msg);
    }
}
 
