package org.zongf.plugins.idea.util;

import freemarker.template.Configuration;
import freemarker.template.Template;
import org.zongf.plugins.idea.util.common.ClassUtil;

import java.io.StringWriter;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/** 代码模板工具类
 * @author: zongf
 * @created: 2019-07-09
 * @since 1.0
 */
public class CodeTemplateUtil {

    // ftl模板根目录
    private static final String DIR_TEMPLATE = "/ftls";

    // freemarker 配置类
    private static final Configuration cfg;

    static {
        try {
            // 1. 创建Freemarker 配置类, 并指定Freemarker 版本号
            cfg = new Configuration(Configuration.VERSION_2_3_23);
            // 2. 设置模板加载目录
            cfg.setClassForTemplateLoading(cfg.getClass(), DIR_TEMPLATE);
            // 3. 设置编码
            cfg.setDefaultEncoding("UTF-8");
            // 4. 设置模板更新延迟时间
            cfg.setTemplateUpdateDelayMilliseconds(0);
        } catch (Exception e) {
            throw new RuntimeException("Freemarker 初始化失败!", e);
        }
    }

    /** 获取模板内容
     * @param ftlName 模板文件名
     * @param rootMap 模板中的键值对
     * @return String 模板文件内容
     * @since 1.0
     * @author zongf
     * @created 2019-07-09 
     */
    public static String getTemplate(String ftlName, Map rootMap) {
        try {

            // 获取模板
            Template template = cfg.getTemplate(ftlName);

            // 解析模板, 替换模板中的变量
            StringWriter writer = new StringWriter();
            template.process(rootMap, writer);

            // 返回模板内容
            return writer.getBuffer().toString();
        } catch (Exception e) {
            throw new RuntimeException("获取模板内容失败!", e);
        }
    }

    /** 获取基础类代码模板: setter, getter, toString, constructor 方法
     * @param className 类名称
     * @param fieldMap 字段名与字段类型组成的key-value键值对
     * @return String
     * @since 1.0
     * @author zongf
     * @created 2019-07-09
     */
    public static String getBasicCode(String className, Map<String, String> fieldMap) {
        Map<String,Object> root = new HashMap<>();
        root.put("className", className);
        root.put("fieldMap", fieldMap);
        return getTemplate("basic.ftl", root);
    }

    /** 获取方法注释模板
     * @param paramNames 方法形参列表
     * @param result 方法返回值
     * @param indent 方法前缩进
     * @return: null
     * @author: zongf
     * @time: 2019-08-05 19:41:32
     */
    public static String getAddMethodComment(List<String> paramNames, String result, String indent) {
        Map<String,Object> root = new HashMap<>();
        root.put("return", ClassUtil.simpleClassName(result));
        root.put("paramNames", paramNames);
        root.put("indent", indent);
        root.put("date", DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss").format(LocalDateTime.now()));
        return getTemplate("work/addMethodComments.ftl", root);
    }
}
