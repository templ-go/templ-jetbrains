// This is a generated file. Not intended for manual editing.
package com.templ.templ.psi;

import com.intellij.psi.tree.IElementType;
import com.intellij.psi.PsiElement;
import com.intellij.lang.ASTNode;
import com.templ.templ.psi.impl.*;

public interface TemplTypes {

  IElementType COMPONENT = new TemplElementType("COMPONENT");
  IElementType COMPONENT_CHILDREN = new TemplElementType("COMPONENT_CHILDREN");
  IElementType CSS_DECL = new TemplElementType("CSS_DECL");
  IElementType ELSE = new TemplElementType("ELSE");
  IElementType ELSE_IF = new TemplElementType("ELSE_IF");
  IElementType EXPR = new TemplElementType("EXPR");
  IElementType FOR_LOOP = new TemplElementType("FOR_LOOP");
  IElementType GO_ROOT = new TemplElementType("GO_ROOT");
  IElementType HTML_DECL = new TemplElementType("HTML_DECL");
  IElementType HTML_DECL_BODY = new TemplElementType("HTML_DECL_BODY");
  IElementType IF_COND = new TemplElementType("IF_COND");
  IElementType RAW_GO = new TemplElementType("RAW_GO");
  IElementType SCRIPT_DECL = new TemplElementType("SCRIPT_DECL");
  IElementType SWITCH_CASE = new TemplElementType("SWITCH_CASE");
  IElementType SWITCH_DEFAULT = new TemplElementType("SWITCH_DEFAULT");
  IElementType SWITCH_STMT = new TemplElementType("SWITCH_STMT");

  IElementType BLOCK_COMMENT = new TemplTokenType("BLOCK_COMMENT");
  IElementType BOOL_EXPR_START = new TemplTokenType("BOOL_EXPR_START");
  IElementType COMPONENT_IMPORT_START = new TemplTokenType("@");
  IElementType COMPONENT_REFERENCE = new TemplTokenType("COMPONENT_REFERENCE");
  IElementType CSS_CLASS_ID = new TemplTokenType("CSS_CLASS_ID");
  IElementType CSS_DECL_START = new TemplTokenType("css");
  IElementType CSS_PROPERTIES = new TemplTokenType("CSS_PROPERTIES");
  IElementType DECL_GO_TOKEN = new TemplTokenType("DECL_GO_TOKEN");
  IElementType DOUBLE_LBRACE = new TemplTokenType("{{");
  IElementType DOUBLE_RBRACE = new TemplTokenType("}}");
  IElementType GO_CASE_FRAGMENT = new TemplTokenType("GO_CASE_FRAGMENT");
  IElementType GO_CSS_DECL_PARAMS = new TemplTokenType("GO_CSS_DECL_PARAMS");
  IElementType GO_DEFAULT_FRAGMENT = new TemplTokenType("GO_DEFAULT_FRAGMENT");
  IElementType GO_ELSE_IF_START_FRAGMENT = new TemplTokenType("GO_ELSE_IF_START_FRAGMENT");
  IElementType GO_ELSE_START_FRAGMENT = new TemplTokenType("GO_ELSE_START_FRAGMENT");
  IElementType GO_EXPR = new TemplTokenType("GO_EXPR");
  IElementType GO_FOR_START_FRAGMENT = new TemplTokenType("GO_FOR_START_FRAGMENT");
  IElementType GO_FRAGMENT = new TemplTokenType("GO_FRAGMENT");
  IElementType GO_IF_START_FRAGMENT = new TemplTokenType("GO_IF_START_FRAGMENT");
  IElementType GO_ROOT_FRAGMENT = new TemplTokenType("GO_ROOT_FRAGMENT");
  IElementType GO_SWITCH_START_FRAGMENT = new TemplTokenType("GO_SWITCH_START_FRAGMENT");
  IElementType HTML_DECL_START = new TemplTokenType("templ");
  IElementType HTML_FRAGMENT = new TemplTokenType("HTML_FRAGMENT");
  IElementType LBRACE = new TemplTokenType("{");
  IElementType LINE_COMMENT = new TemplTokenType("LINE_COMMENT");
  IElementType LPARENTH = new TemplTokenType("(");
  IElementType RBRACE = new TemplTokenType("}");
  IElementType RPARENTH = new TemplTokenType(")");
  IElementType SCRIPT_BODY = new TemplTokenType("SCRIPT_BODY");
  IElementType SCRIPT_DECL_START = new TemplTokenType("script");
  IElementType SCRIPT_FUNCTION_DECL = new TemplTokenType("SCRIPT_FUNCTION_DECL");

  class Factory {
    public static PsiElement createElement(ASTNode node) {
      IElementType type = node.getElementType();
      if (type == COMPONENT) {
        return new TemplComponentImpl(node);
      }
      else if (type == COMPONENT_CHILDREN) {
        return new TemplComponentChildrenImpl(node);
      }
      else if (type == CSS_DECL) {
        return new TemplCssDeclImpl(node);
      }
      else if (type == ELSE) {
        return new TemplElseImpl(node);
      }
      else if (type == ELSE_IF) {
        return new TemplElseIfImpl(node);
      }
      else if (type == EXPR) {
        return new TemplExprImpl(node);
      }
      else if (type == FOR_LOOP) {
        return new TemplForLoopImpl(node);
      }
      else if (type == GO_ROOT) {
        return new TemplGoRootImpl(node);
      }
      else if (type == HTML_DECL) {
        return new TemplHtmlDeclImpl(node);
      }
      else if (type == HTML_DECL_BODY) {
        return new TemplHtmlDeclBodyImpl(node);
      }
      else if (type == IF_COND) {
        return new TemplIfCondImpl(node);
      }
      else if (type == RAW_GO) {
        return new TemplRawGoImpl(node);
      }
      else if (type == SCRIPT_DECL) {
        return new TemplScriptDeclImpl(node);
      }
      else if (type == SWITCH_CASE) {
        return new TemplSwitchCaseImpl(node);
      }
      else if (type == SWITCH_DEFAULT) {
        return new TemplSwitchDefaultImpl(node);
      }
      else if (type == SWITCH_STMT) {
        return new TemplSwitchStmtImpl(node);
      }
      throw new AssertionError("Unknown element type: " + type);
    }
  }
}
