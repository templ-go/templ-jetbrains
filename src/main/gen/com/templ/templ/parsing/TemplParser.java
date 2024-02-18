// This is a generated file. Not intended for manual editing.
package com.templ.templ.parsing;

import com.intellij.lang.PsiBuilder;
import com.intellij.lang.PsiBuilder.Marker;
import static com.templ.templ.psi.TemplTypes.*;
import static com.intellij.lang.parser.GeneratedParserUtilBase.*;
import com.intellij.psi.tree.IElementType;
import com.intellij.lang.ASTNode;
import com.intellij.psi.tree.TokenSet;
import com.intellij.lang.PsiParser;
import com.intellij.lang.LightPsiParser;

@SuppressWarnings({"SimplifiableIfStatement", "UnusedAssignment"})
public class TemplParser implements PsiParser, LightPsiParser {

  public ASTNode parse(IElementType t, PsiBuilder b) {
    parseLight(t, b);
    return b.getTreeBuilt();
  }

  public void parseLight(IElementType t, PsiBuilder b) {
    boolean r;
    b = adapt_builder_(t, b, this, null);
    Marker m = enter_section_(b, 0, _COLLAPSE_, null);
    r = parse_root_(t, b);
    exit_section_(b, 0, m, t, r, true, TRUE_CONDITION);
  }

  protected boolean parse_root_(IElementType t, PsiBuilder b) {
    return parse_root_(t, b, 0);
  }

  static boolean parse_root_(IElementType t, PsiBuilder b, int l) {
    return root(b, l + 1);
  }

  /* ********************************************************** */
  // INLINE_COMPONENT_START GO_INLINE_COMPONENT component_children?
  public static boolean component(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "component")) return false;
    if (!nextTokenIs(b, INLINE_COMPONENT_START)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeTokens(b, 0, INLINE_COMPONENT_START, GO_INLINE_COMPONENT);
    r = r && component_2(b, l + 1);
    exit_section_(b, m, COMPONENT, r);
    return r;
  }

  // component_children?
  private static boolean component_2(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "component_2")) return false;
    component_children(b, l + 1);
    return true;
  }

  /* ********************************************************** */
  // LBRACE html_decl_body RBRACE
  public static boolean component_children(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "component_children")) return false;
    if (!nextTokenIs(b, LBRACE)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, LBRACE);
    r = r && html_decl_body(b, l + 1);
    r = r && consumeToken(b, RBRACE);
    exit_section_(b, m, COMPONENT_CHILDREN, r);
    return r;
  }

  /* ********************************************************** */
  // GO_ELSE_START_FRAGMENT html_decl_body
  public static boolean else_$(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "else_$")) return false;
    if (!nextTokenIs(b, GO_ELSE_START_FRAGMENT)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, GO_ELSE_START_FRAGMENT);
    r = r && html_decl_body(b, l + 1);
    exit_section_(b, m, ELSE, r);
    return r;
  }

  /* ********************************************************** */
  // GO_ELSE_IF_START_FRAGMENT html_decl_body
  public static boolean else_if(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "else_if")) return false;
    if (!nextTokenIs(b, GO_ELSE_IF_START_FRAGMENT)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, GO_ELSE_IF_START_FRAGMENT);
    r = r && html_decl_body(b, l + 1);
    exit_section_(b, m, ELSE_IF, r);
    return r;
  }

  /* ********************************************************** */
  // LBRACE GO_EXPR RBRACE
  public static boolean expr(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "expr")) return false;
    if (!nextTokenIs(b, LBRACE)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeTokens(b, 0, LBRACE, GO_EXPR, RBRACE);
    exit_section_(b, m, EXPR, r);
    return r;
  }

  /* ********************************************************** */
  // GO_FOR_START_FRAGMENT html_decl_body RBRACE
  public static boolean for_loop(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "for_loop")) return false;
    if (!nextTokenIs(b, GO_FOR_START_FRAGMENT)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, GO_FOR_START_FRAGMENT);
    r = r && html_decl_body(b, l + 1);
    r = r && consumeToken(b, RBRACE);
    exit_section_(b, m, FOR_LOOP, r);
    return r;
  }

  /* ********************************************************** */
  // GO_ROOT_FRAGMENT
  public static boolean go_root(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "go_root")) return false;
    if (!nextTokenIs(b, GO_ROOT_FRAGMENT)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, GO_ROOT_FRAGMENT);
    exit_section_(b, m, GO_ROOT, r);
    return r;
  }

  /* ********************************************************** */
  // HTML_DECL_START DECL_GO_TOKEN html_decl_body RBRACE
  public static boolean html_decl(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "html_decl")) return false;
    if (!nextTokenIs(b, HTML_DECL_START)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeTokens(b, 0, HTML_DECL_START, DECL_GO_TOKEN);
    r = r && html_decl_body(b, l + 1);
    r = r && consumeToken(b, RBRACE);
    exit_section_(b, m, HTML_DECL, r);
    return r;
  }

  /* ********************************************************** */
  // (HTML_FRAGMENT | expr | if_cond | switch_stmt | for_loop | component)*
  public static boolean html_decl_body(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "html_decl_body")) return false;
    Marker m = enter_section_(b, l, _NONE_, HTML_DECL_BODY, "<html decl body>");
    while (true) {
      int c = current_position_(b);
      if (!html_decl_body_0(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "html_decl_body", c)) break;
    }
    exit_section_(b, l, m, true, false, null);
    return true;
  }

  // HTML_FRAGMENT | expr | if_cond | switch_stmt | for_loop | component
  private static boolean html_decl_body_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "html_decl_body_0")) return false;
    boolean r;
    r = consumeToken(b, HTML_FRAGMENT);
    if (!r) r = expr(b, l + 1);
    if (!r) r = if_cond(b, l + 1);
    if (!r) r = switch_stmt(b, l + 1);
    if (!r) r = for_loop(b, l + 1);
    if (!r) r = component(b, l + 1);
    return r;
  }

  /* ********************************************************** */
  // GO_IF_START_FRAGMENT html_decl_body else_if* else? RBRACE
  public static boolean if_cond(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "if_cond")) return false;
    if (!nextTokenIs(b, GO_IF_START_FRAGMENT)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, GO_IF_START_FRAGMENT);
    r = r && html_decl_body(b, l + 1);
    r = r && if_cond_2(b, l + 1);
    r = r && if_cond_3(b, l + 1);
    r = r && consumeToken(b, RBRACE);
    exit_section_(b, m, IF_COND, r);
    return r;
  }

  // else_if*
  private static boolean if_cond_2(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "if_cond_2")) return false;
    while (true) {
      int c = current_position_(b);
      if (!else_if(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "if_cond_2", c)) break;
    }
    return true;
  }

  // else?
  private static boolean if_cond_3(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "if_cond_3")) return false;
    else_$(b, l + 1);
    return true;
  }

  /* ********************************************************** */
  // go_root (go_root | html_decl)*
  static boolean root(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "root")) return false;
    if (!nextTokenIs(b, GO_ROOT_FRAGMENT)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = go_root(b, l + 1);
    r = r && root_1(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // (go_root | html_decl)*
  private static boolean root_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "root_1")) return false;
    while (true) {
      int c = current_position_(b);
      if (!root_1_0(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "root_1", c)) break;
    }
    return true;
  }

  // go_root | html_decl
  private static boolean root_1_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "root_1_0")) return false;
    boolean r;
    r = go_root(b, l + 1);
    if (!r) r = html_decl(b, l + 1);
    return r;
  }

  /* ********************************************************** */
  // GO_CASE_FRAGMENT html_decl_body
  public static boolean switch_case(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "switch_case")) return false;
    if (!nextTokenIs(b, GO_CASE_FRAGMENT)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, GO_CASE_FRAGMENT);
    r = r && html_decl_body(b, l + 1);
    exit_section_(b, m, SWITCH_CASE, r);
    return r;
  }

  /* ********************************************************** */
  // GO_DEFAULT_FRAGMENT html_decl_body
  public static boolean switch_default(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "switch_default")) return false;
    if (!nextTokenIs(b, GO_DEFAULT_FRAGMENT)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, GO_DEFAULT_FRAGMENT);
    r = r && html_decl_body(b, l + 1);
    exit_section_(b, m, SWITCH_DEFAULT, r);
    return r;
  }

  /* ********************************************************** */
  // GO_SWITCH_START_FRAGMENT switch_case* switch_default? RBRACE
  public static boolean switch_stmt(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "switch_stmt")) return false;
    if (!nextTokenIs(b, GO_SWITCH_START_FRAGMENT)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, GO_SWITCH_START_FRAGMENT);
    r = r && switch_stmt_1(b, l + 1);
    r = r && switch_stmt_2(b, l + 1);
    r = r && consumeToken(b, RBRACE);
    exit_section_(b, m, SWITCH_STMT, r);
    return r;
  }

  // switch_case*
  private static boolean switch_stmt_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "switch_stmt_1")) return false;
    while (true) {
      int c = current_position_(b);
      if (!switch_case(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "switch_stmt_1", c)) break;
    }
    return true;
  }

  // switch_default?
  private static boolean switch_stmt_2(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "switch_stmt_2")) return false;
    switch_default(b, l + 1);
    return true;
  }

}
