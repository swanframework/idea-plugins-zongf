package org.zongf.plugins.idea.action;

import com.intellij.codeInsight.daemon.impl.DaemonCodeAnalyzerEx;
import com.intellij.codeInsight.daemon.impl.HighlightInfo;
import com.intellij.codeInsight.daemon.impl.VisibleHighlightingPassFactory;
import com.intellij.codeInsight.daemon.impl.actions.AddImportAction;
import com.intellij.codeInsight.daemon.impl.quickfix.ImportClassFix;
import com.intellij.codeInsight.intention.IntentionAction;
import com.intellij.codeInspection.HintAction;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.PlatformDataKeys;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.Pair;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiJavaFile;
import com.intellij.psi.PsiJavaReference;
import com.intellij.psi.PsiReference;
import com.intellij.psi.codeStyle.JavaCodeStyleManager;

import java.util.*;

/** 优化导包
 * @since 1.0
 * @author zongf
 * @created 2019-07-09
 */
public class OptimizeImportAction extends AnAction {

    @Override
    public void actionPerformed(AnActionEvent event) {

        Project project = event.getData(PlatformDataKeys.PROJECT);
        Editor editor = event.getData(PlatformDataKeys.EDITOR);

        // 获取当前类信息
        PsiJavaFile psiJavaFile = (PsiJavaFile)event.getData(PlatformDataKeys.PSI_FILE);

        WriteCommandAction.runWriteCommandAction(project, ()->{

            // 获取代码中可以自动导入的类映射
            Map<PsiJavaReference, PsiClass> imprtClassMap = getCanAutoImportClassess(editor, psiJavaFile);

            // 遍历依次执行导入
            for (Map.Entry<PsiJavaReference, PsiClass> entry : imprtClassMap.entrySet()) {
                new AddImportAction(project, entry.getKey(), editor, entry.getValue()) {
                    protected void bindReference(PsiReference ref, PsiClass psiClass) {
                        ref.bindToElement(psiClass);
                    }
                }.execute();
            }

            // 移除多余的import语句
            JavaCodeStyleManager.getInstance(project).removeRedundantImports(psiJavaFile);

        });
    }

    /** 获取能够自动导入的类信息
     * @param editor 编辑器
     * @param psiJavaFile java 文件
     * @return Map<PsiJavaReference, PsiClass> 键值对
     * @since 1.0
     * @author zongf
     * @created 2019-07-10 
     */
    private Map<PsiJavaReference, PsiClass> getCanAutoImportClassess(Editor editor, PsiJavaFile psiJavaFile) {

        Map<PsiJavaReference, PsiClass> map = new LinkedHashMap<>();

        // 获取代码中高亮列表(并不全是因为导包问题)
        List<HighlightInfo> highlights = getHighLightInfo(editor);

        for (HighlightInfo highlight : highlights) {

            ImportClassFix importClassFix = getImportClassFix(highlight);

            if (importClassFix != null) {
                // 获取匹配到的类列表
                List<PsiClass> matchClassList = importClassFix.getClassesToImport();

                // 如果匹配到的类唯一, 那么说明可以自动导入. 有些类是不唯一的,比如说(Date), 对于不唯一的类不能进行自动导入.
                if(matchClassList != null && matchClassList.size() == 1){
                    PsiJavaReference psiJavaReference = (PsiJavaReference) psiJavaFile.findReferenceAt(highlight.startOffset);
                    map.put(psiJavaReference, matchClassList.get(0));
                }
            }
        }

        return map;
    }

    /**获取导入包修复对象
     * @param highlight
     * @return ImportClassFix
     * @since 1.0
     * @author zongf
     * @created 2019-07-10 
     */
    private ImportClassFix getImportClassFix(HighlightInfo highlight) {
        List<Pair<HighlightInfo.IntentionActionDescriptor, TextRange>> list = highlight.quickFixActionRanges;

        Iterator pairIterator = list.iterator();

        IntentionAction action = null;
        do {
            if (!pairIterator.hasNext()) {
                break;
            }

            Pair<HighlightInfo.IntentionActionDescriptor, TextRange> pair = (Pair)pairIterator.next();
            action = ((HighlightInfo.IntentionActionDescriptor)pair.getFirst()).getAction();
        } while(!(action instanceof HintAction));

        return (ImportClassFix) action;
    }

    /** 获取代码中高亮的位置信息, 代码中有问题的部分会进行高亮显示
     * @param editor 编译器
     * @return 代码中高亮信息列表
     * @since 1.0
     * @author zongf
     * @created 2019-07-10
     */
    private List<HighlightInfo> getHighLightInfo(Editor editor){

        List<HighlightInfo> highlights = new ArrayList();

        // 计算文本编辑器文本范围值
        TextRange range = VisibleHighlightingPassFactory.calculateVisibleRange(editor);
        int startOffset = range.getStartOffset();
        int endOffset = range.getEndOffset();

        // 解析代码高亮
        DaemonCodeAnalyzerEx.processHighlights(editor.getDocument(), editor.getProject(), null, startOffset, endOffset, (hightLightInfo) -> {
            if (hightLightInfo.hasHint() && !editor.getFoldingModel().isOffsetCollapsed(hightLightInfo.startOffset)) {
                highlights.add(hightLightInfo);
            }
            return true;
        });
        return highlights;
    }

}
