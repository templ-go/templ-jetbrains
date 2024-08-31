// This is a generated file. Not intended for manual editing.
package com.templ.templ.psi;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;

public interface TemplHtmlDeclBody extends PsiElement {

  @NotNull
  List<TemplComponent> getComponentList();

  @NotNull
  List<TemplExpr> getExprList();

  @NotNull
  List<TemplForLoop> getForLoopList();

  @NotNull
  List<TemplIfCond> getIfCondList();

  @NotNull
  List<TemplRawGo> getRawGoList();

  @NotNull
  List<TemplSwitchStmt> getSwitchStmtList();

}
