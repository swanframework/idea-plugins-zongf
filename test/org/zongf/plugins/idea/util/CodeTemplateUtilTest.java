package org.zongf.plugins.idea.util;

import org.zongf.plugins.idea.util.CodeTemplateUtil;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @author: zongf
 * @created: 2019-07-09
 * @since 1.0
 */
public class CodeTemplateUtilTest {

    public static void test_getBasicCode(){
        String className = "StudentPO";

        Map<String, String> fieldMap = new LinkedHashMap<>();
        fieldMap.put("success", "Boolean");
        fieldMap.put("failed", "boolean");
        fieldMap.put("name", "String");
        fieldMap.put("sex", "String");

        String basicCode = CodeTemplateUtil.getBasicCode(className, fieldMap);
        System.out.println(basicCode);
    }

    public static void main(String[] args) {
        test_getBasicCode();
    }
}
