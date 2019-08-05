package org.zongf.plugins.idea.util;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @author: zongf
 * @created: 2019-07-09
 * @since 1.0
 */
public class CodeTemplateUtilTest {

    // 测试生成基础方法
    public static void test_getBasicCode(){
        String className = "StudentPO";

        Map<String, String> fieldMap = new LinkedHashMap<>();
//        fieldMap.put("id", "Integer");
//        fieldMap.put("name", "String");

        String basicCode = CodeTemplateUtil.getBasicCode(className, fieldMap);
        System.out.println(basicCode);
    }

    public static void test_addMethodComments(){

        String templatePath = "work/addMethodComments.ftl";
//        String templatePath = "generateMethodComments.ftl";
        String content = CodeTemplateUtil.getAddMethodComment(Arrays.asList("name", "id", "age"), "List<java.lang.String>", "    ", templatePath);

        System.out.println(content);

    }
    public static void main(String[] args) {
//        test_getBasicCode();
        test_addMethodComments();

        System.out.println("public String sayHellO(String name, String wel) {".length());

    }
}
