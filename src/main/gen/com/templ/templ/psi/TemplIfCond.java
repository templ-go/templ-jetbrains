// This is a generated file. Not intended for manual editing.
package com.templ.templ.psi;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.psi.PsiElement;

public interface TemplIfCond extends PsiElement {

  @Nullable
  TemplElse getElse();

  @NotNull
  List<TemplElseIf> getElseIfList();

  @Nullable
  TemplHtmlDeclBody getHtmlDeclBody();

}
