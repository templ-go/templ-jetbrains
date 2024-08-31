// This is a generated file. Not intended for manual editing.
package com.templ.templ.psi.impl;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.util.PsiTreeUtil;
import static com.templ.templ.psi.TemplTypes.*;
import com.intellij.extapi.psi.ASTWrapperPsiElement;
import com.templ.templ.psi.*;

public class TemplHtmlDeclBodyImpl extends ASTWrapperPsiElement implements TemplHtmlDeclBody {

  public TemplHtmlDeclBodyImpl(@NotNull ASTNode node) {
    super(node);
  }

  public void accept(@NotNull TemplVisitor visitor) {
    visitor.visitHtmlDeclBody(this);
  }

  @Override
  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof TemplVisitor) accept((TemplVisitor)visitor);
    else super.accept(visitor);
  }

  @Override
  @NotNull
  public List<TemplComponent> getComponentList() {
    return PsiTreeUtil.getChildrenOfTypeAsList(this, TemplComponent.class);
  }

  @Override
  @NotNull
  public List<TemplExpr> getExprList() {
    return PsiTreeUtil.getChildrenOfTypeAsList(this, TemplExpr.class);
  }

  @Override
  @NotNull
  public List<TemplForLoop> getForLoopList() {
    return PsiTreeUtil.getChildrenOfTypeAsList(this, TemplForLoop.class);
  }

  @Override
  @NotNull
  public List<TemplIfCond> getIfCondList() {
    return PsiTreeUtil.getChildrenOfTypeAsList(this, TemplIfCond.class);
  }

  @Override
  @NotNull
  public List<TemplRawGo> getRawGoList() {
    return PsiTreeUtil.getChildrenOfTypeAsList(this, TemplRawGo.class);
  }

  @Override
  @NotNull
  public List<TemplSwitchStmt> getSwitchStmtList() {
    return PsiTreeUtil.getChildrenOfTypeAsList(this, TemplSwitchStmt.class);
  }

}
