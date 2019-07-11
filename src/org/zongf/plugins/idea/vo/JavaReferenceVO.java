package org.zongf.plugins.idea.vo;

import java.util.ArrayList;
import java.util.List;

/**
 * @author: zongf
 * @created: 2019-07-11
 * @since 1.0
 */
public class JavaReferenceVO {

    private String className;

    private String methodName;

    private List<String> paramList = new ArrayList<>();

    private String codeLine;

    private int lineNo;

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public String getMethodName() {
        return methodName;
    }

    public void setMethodName(String methodName) {
        this.methodName = methodName;
    }

    public List<String> getParamList() {
        return paramList;
    }

    public void setParamList(List<String> paramList) {
        this.paramList = paramList;
    }

    public String getCodeLine() {
        return codeLine;
    }

    public void setCodeLine(String codeLine) {
        this.codeLine = codeLine;
    }

    public int getLineNo() {
        return lineNo;
    }

    public void setLineNo(int lineNo) {
        this.lineNo = lineNo;
    }

}
