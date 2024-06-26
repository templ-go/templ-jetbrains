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
import static com.intellij.psi.TokenType.WHITE_SPACE;

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
  // COMPONENT_IMPORT_START COMPONENT_REFERENCE component_children?
  public static boolean component(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "component")) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_, COMPONENT, "<component>");
    r = consumeTokens(b, 1, COMPONENT_IMPORT_START, COMPONENT_REFERENCE);
    p = r; // pin = 1
    r = r && component_2(b, l + 1);
    exit_section_(b, l, m, r, p, TemplParser::recover_component);
    return r || p;
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
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_, COMPONENT_CHILDREN, null);
    r = consumeToken(b, LBRACE);
    p = r; // pin = 1
    r = r && report_error_(b, html_decl_body(b, l + 1));
    r = p && consumeToken(b, RBRACE) && r;
    exit_section_(b, l, m, r, p, null);
    return r || p;
  }

  /* ********************************************************** */
  // CSS_DECL_START CSS_CLASS_ID css_params LBRACE CSS_PROPERTIES* RBRACE
  public static boolean css_decl(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "css_decl")) return false;
    if (!nextTokenIs(b, CSS_DECL_START)) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_, CSS_DECL, null);
    r = consumeTokens(b, 1, CSS_DECL_START, CSS_CLASS_ID);
    p = r; // pin = 1
    r = r && report_error_(b, css_params(b, l + 1));
    r = p && report_error_(b, consumeToken(b, LBRACE)) && r;
    r = p && report_error_(b, css_decl_4(b, l + 1)) && r;
    r = p && consumeToken(b, RBRACE) && r;
    exit_section_(b, l, m, r, p, null);
    return r || p;
  }

  // CSS_PROPERTIES*
  private static boolean css_decl_4(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "css_decl_4")) return false;
    while (true) {
      int c = current_position_(b);
      if (!consumeToken(b, CSS_PROPERTIES)) break;
      if (!empty_element_parsed_guard_(b, "css_decl_4", c)) break;
    }
    return true;
  }

  /* ********************************************************** */
  // LPARENTH GO_CSS_DECL_PARAMS* RPARENTH
  static boolean css_params(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "css_params")) return false;
    if (!nextTokenIs(b, LPARENTH)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, LPARENTH);
    r = r && css_params_1(b, l + 1);
    r = r && consumeToken(b, RPARENTH);
    exit_section_(b, m, null, r);
    return r;
  }

  // GO_CSS_DECL_PARAMS*
  private static boolean css_params_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "css_params_1")) return false;
    while (true) {
      int c = current_position_(b);
      if (!consumeToken(b, GO_CSS_DECL_PARAMS)) break;
      if (!empty_element_parsed_guard_(b, "css_params_1", c)) break;
    }
    return true;
  }

  /* ********************************************************** */
  // GO_ELSE_START_FRAGMENT html_decl_body
  public static boolean else_$(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "else_$")) return false;
    if (!nextTokenIs(b, GO_ELSE_START_FRAGMENT)) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_, ELSE, null);
    r = consumeToken(b, GO_ELSE_START_FRAGMENT);
    p = r; // pin = 1
    r = r && html_decl_body(b, l + 1);
    exit_section_(b, l, m, r, p, null);
    return r || p;
  }

  /* ********************************************************** */
  // GO_ELSE_IF_START_FRAGMENT html_decl_body
  public static boolean else_if(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "else_if")) return false;
    if (!nextTokenIs(b, GO_ELSE_IF_START_FRAGMENT)) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_, ELSE_IF, null);
    r = consumeToken(b, GO_ELSE_IF_START_FRAGMENT);
    p = r; // pin = 1
    r = r && html_decl_body(b, l + 1);
    exit_section_(b, l, m, r, p, null);
    return r || p;
  }

  /* ********************************************************** */
  // BOOL_EXPR_START? LBRACE GO_EXPR* RBRACE
  public static boolean expr(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "expr")) return false;
    if (!nextTokenIs(b, "<expr>", BOOL_EXPR_START, LBRACE)) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_, EXPR, "<expr>");
    r = expr_0(b, l + 1);
    r = r && consumeToken(b, LBRACE);
    p = r; // pin = 2
    r = r && report_error_(b, expr_2(b, l + 1));
    r = p && consumeToken(b, RBRACE) && r;
    exit_section_(b, l, m, r, p, null);
    return r || p;
  }

  // BOOL_EXPR_START?
  private static boolean expr_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "expr_0")) return false;
    consumeToken(b, BOOL_EXPR_START);
    return true;
  }

  // GO_EXPR*
  private static boolean expr_2(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "expr_2")) return false;
    while (true) {
      int c = current_position_(b);
      if (!consumeToken(b, GO_EXPR)) break;
      if (!empty_element_parsed_guard_(b, "expr_2", c)) break;
    }
    return true;
  }

  /* ********************************************************** */
  // GO_FOR_START_FRAGMENT html_decl_body RBRACE
  public static boolean for_loop(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "for_loop")) return false;
    if (!nextTokenIs(b, GO_FOR_START_FRAGMENT)) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_, FOR_LOOP, null);
    r = consumeToken(b, GO_FOR_START_FRAGMENT);
    p = r; // pin = 1
    r = r && report_error_(b, html_decl_body(b, l + 1));
    r = p && consumeToken(b, RBRACE) && r;
    exit_section_(b, l, m, r, p, null);
    return r || p;
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
  // HTML_DECL_START DECL_GO_TOKEN* LBRACE html_decl_body RBRACE
  public static boolean html_decl(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "html_decl")) return false;
    if (!nextTokenIs(b, HTML_DECL_START)) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_, HTML_DECL, null);
    r = consumeToken(b, HTML_DECL_START);
    p = r; // pin = 1
    r = r && report_error_(b, html_decl_1(b, l + 1));
    r = p && report_error_(b, consumeToken(b, LBRACE)) && r;
    r = p && report_error_(b, html_decl_body(b, l + 1)) && r;
    r = p && consumeToken(b, RBRACE) && r;
    exit_section_(b, l, m, r, p, null);
    return r || p;
  }

  // DECL_GO_TOKEN*
  private static boolean html_decl_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "html_decl_1")) return false;
    while (true) {
      int c = current_position_(b);
      if (!consumeToken(b, DECL_GO_TOKEN)) break;
      if (!empty_element_parsed_guard_(b, "html_decl_1", c)) break;
    }
    return true;
  }

  /* ********************************************************** */
  // (HTML_FRAGMENT | TEMPL_FRAGMENT | raw_go | expr | if_cond | switch_stmt | for_loop | component)*
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

  // HTML_FRAGMENT | TEMPL_FRAGMENT | raw_go | expr | if_cond | switch_stmt | for_loop | component
  private static boolean html_decl_body_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "html_decl_body_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, HTML_FRAGMENT);
    if (!r) r = consumeToken(b, TEMPL_FRAGMENT);
    if (!r) r = raw_go(b, l + 1);
    if (!r) r = expr(b, l + 1);
    if (!r) r = if_cond(b, l + 1);
    if (!r) r = switch_stmt(b, l + 1);
    if (!r) r = for_loop(b, l + 1);
    if (!r) r = component(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  /* ********************************************************** */
  // GO_IF_START_FRAGMENT html_decl_body else_if* else? RBRACE
  public static boolean if_cond(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "if_cond")) return false;
    if (!nextTokenIs(b, GO_IF_START_FRAGMENT)) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_, IF_COND, null);
    r = consumeToken(b, GO_IF_START_FRAGMENT);
    p = r; // pin = 1
    r = r && report_error_(b, html_decl_body(b, l + 1));
    r = p && report_error_(b, if_cond_2(b, l + 1)) && r;
    r = p && report_error_(b, if_cond_3(b, l + 1)) && r;
    r = p && consumeToken(b, RBRACE) && r;
    exit_section_(b, l, m, r, p, null);
    return r || p;
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
  // DOUBLE_LBRACE GO_FRAGMENT* DOUBLE_RBRACE
  public static boolean raw_go(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "raw_go")) return false;
    if (!nextTokenIs(b, DOUBLE_LBRACE)) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_, RAW_GO, null);
    r = consumeToken(b, DOUBLE_LBRACE);
    p = r; // pin = 1
    r = r && report_error_(b, raw_go_1(b, l + 1));
    r = p && consumeToken(b, DOUBLE_RBRACE) && r;
    exit_section_(b, l, m, r, p, null);
    return r || p;
  }

  // GO_FRAGMENT*
  private static boolean raw_go_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "raw_go_1")) return false;
    while (true) {
      int c = current_position_(b);
      if (!consumeToken(b, GO_FRAGMENT)) break;
      if (!empty_element_parsed_guard_(b, "raw_go_1", c)) break;
    }
    return true;
  }

  /* ********************************************************** */
  // !(HTML_DECL_START | CSS_DECL_START | SCRIPT_DECL_START | GO_ROOT_FRAGMENT)
  static boolean recover(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "recover")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NOT_);
    r = !recover_0(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  // HTML_DECL_START | CSS_DECL_START | SCRIPT_DECL_START | GO_ROOT_FRAGMENT
  private static boolean recover_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "recover_0")) return false;
    boolean r;
    r = consumeToken(b, HTML_DECL_START);
    if (!r) r = consumeToken(b, CSS_DECL_START);
    if (!r) r = consumeToken(b, SCRIPT_DECL_START);
    if (!r) r = consumeToken(b, GO_ROOT_FRAGMENT);
    return r;
  }

  /* ********************************************************** */
  // !(COMPONENT_IMPORT_START | HTML_FRAGMENT | RBRACE | RPARENTH)
  static boolean recover_component(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "recover_component")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NOT_);
    r = !recover_component_0(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  // COMPONENT_IMPORT_START | HTML_FRAGMENT | RBRACE | RPARENTH
  private static boolean recover_component_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "recover_component_0")) return false;
    boolean r;
    r = consumeToken(b, COMPONENT_IMPORT_START);
    if (!r) r = consumeToken(b, HTML_FRAGMENT);
    if (!r) r = consumeToken(b, RBRACE);
    if (!r) r = consumeToken(b, RPARENTH);
    return r;
  }

  /* ********************************************************** */
  // go_root (root_item)*
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

  // (root_item)*
  private static boolean root_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "root_1")) return false;
    while (true) {
      int c = current_position_(b);
      if (!root_1_0(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "root_1", c)) break;
    }
    return true;
  }

  // (root_item)
  private static boolean root_1_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "root_1_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = root_item(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  /* ********************************************************** */
  // go_root | html_decl | css_decl | script_decl
  static boolean root_item(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "root_item")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_);
    r = go_root(b, l + 1);
    if (!r) r = html_decl(b, l + 1);
    if (!r) r = css_decl(b, l + 1);
    if (!r) r = script_decl(b, l + 1);
    exit_section_(b, l, m, r, false, TemplParser::recover);
    return r;
  }

  /* ********************************************************** */
  // SCRIPT_DECL_START SCRIPT_FUNCTION_DECL* LBRACE SCRIPT_BODY* RBRACE
  public static boolean script_decl(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "script_decl")) return false;
    if (!nextTokenIs(b, SCRIPT_DECL_START)) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_, SCRIPT_DECL, null);
    r = consumeToken(b, SCRIPT_DECL_START);
    p = r; // pin = 1
    r = r && report_error_(b, script_decl_1(b, l + 1));
    r = p && report_error_(b, consumeToken(b, LBRACE)) && r;
    r = p && report_error_(b, script_decl_3(b, l + 1)) && r;
    r = p && consumeToken(b, RBRACE) && r;
    exit_section_(b, l, m, r, p, null);
    return r || p;
  }

  // SCRIPT_FUNCTION_DECL*
  private static boolean script_decl_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "script_decl_1")) return false;
    while (true) {
      int c = current_position_(b);
      if (!consumeToken(b, SCRIPT_FUNCTION_DECL)) break;
      if (!empty_element_parsed_guard_(b, "script_decl_1", c)) break;
    }
    return true;
  }

  // SCRIPT_BODY*
  private static boolean script_decl_3(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "script_decl_3")) return false;
    while (true) {
      int c = current_position_(b);
      if (!consumeToken(b, SCRIPT_BODY)) break;
      if (!empty_element_parsed_guard_(b, "script_decl_3", c)) break;
    }
    return true;
  }

  /* ********************************************************** */
  // GO_CASE_FRAGMENT html_decl_body
  public static boolean switch_case(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "switch_case")) return false;
    if (!nextTokenIs(b, GO_CASE_FRAGMENT)) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_, SWITCH_CASE, null);
    r = consumeToken(b, GO_CASE_FRAGMENT);
    p = r; // pin = 1
    r = r && html_decl_body(b, l + 1);
    exit_section_(b, l, m, r, p, null);
    return r || p;
  }

  /* ********************************************************** */
  // GO_DEFAULT_FRAGMENT html_decl_body
  public static boolean switch_default(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "switch_default")) return false;
    if (!nextTokenIs(b, GO_DEFAULT_FRAGMENT)) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_, SWITCH_DEFAULT, null);
    r = consumeToken(b, GO_DEFAULT_FRAGMENT);
    p = r; // pin = 1
    r = r && html_decl_body(b, l + 1);
    exit_section_(b, l, m, r, p, null);
    return r || p;
  }

  /* ********************************************************** */
  // GO_SWITCH_START_FRAGMENT switch_case* switch_default? RBRACE
  public static boolean switch_stmt(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "switch_stmt")) return false;
    if (!nextTokenIs(b, GO_SWITCH_START_FRAGMENT)) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_, SWITCH_STMT, null);
    r = consumeToken(b, GO_SWITCH_START_FRAGMENT);
    p = r; // pin = 1
    r = r && report_error_(b, switch_stmt_1(b, l + 1));
    r = p && report_error_(b, switch_stmt_2(b, l + 1)) && r;
    r = p && consumeToken(b, RBRACE) && r;
    exit_section_(b, l, m, r, p, null);
    return r || p;
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
