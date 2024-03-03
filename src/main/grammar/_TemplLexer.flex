package com.templ.templ.parsing;

import com.intellij.psi.tree.IElementType;
import com.intellij.lexer.FlexLexer;
import org.bouncycastle.util.Arrays;

import static com.intellij.psi.TokenType.*;
import static com.templ.templ.psi.TemplTypes.*;

%%

%{
    private int braceNestingLevel = 0;
    private int parensNestingLevel = 0;
    private int previousState = -1;

    public void yyPushState(int newState) {
        previousState = yystate();
        yybegin(newState);
    }

    public void yyPopState() { // We only remember one level of state.
        if (previousState == -1) {
          throw new IllegalStateException("No previous state to pop");
        }
        yybegin(previousState);
        previousState = -1;
    }

    public boolean isCommentEnabled() {
        return !Arrays.contains(new int[]{IN_BLOCK_COMMENT, IN_GO_STRING, IN_GO_RAW_STRING}, yystate());
    }
%}

%public
%class _TemplLexer
%implements FlexLexer
%function advance
%type IElementType
%unicode

WHITE_SPACE=\s+
OPTIONAL_WHITE_SPACE=\s*

%state IN_TEMPL_DECLARATION_START
%state IN_TEMPL_DECLARATION_BODY
%state IN_CSS_DECLARATION_START
%state IN_CSS_DECLARATION_PARAMS
%state IN_CSS_DECLARATION_BODY
%state IN_SCRIPT_DECLARATION_START
%state IN_SCRIPT_DECLARATION_BODY
%state IN_BOOL_EXPR
%state IN_EXPR
%state IN_COMPONENT_IMPORT
%state IN_COMPONENT_IMPORT_PARAMS
%state IN_COMPONENT_IMPORT_PARAMS_END_WITHOUT_CHILDREN
%state IN_COMPONENT_IMPORT_CHILDREN_BLOCK_START
%state IN_BLOCK_COMMENT
%state IN_GO_STRING
%state IN_GO_RAW_STRING
%state IN_HTML_COMMENT

%%

"//" .* {
    if (isCommentEnabled()) {
        return LINE_COMMENT;
    }
    yypushback(yylength() - 1);
}

"/\*" {
    if (isCommentEnabled()) {
        yyPushState(IN_BLOCK_COMMENT);
        return BLOCK_COMMENT;
    }
    yypushback(yylength() - 1);
}

<IN_BLOCK_COMMENT> {
    "\*/" {
        yyPopState();
        return BLOCK_COMMENT;
    }

    [^] {
        return BLOCK_COMMENT;
    }
}

// Detect Go strings because comments aren't allowed inside them.
<YYINITIAL, IN_EXPR, IN_COMPONENT_IMPORT_PARAMS, IN_TEMPL_DECLARATION_START> {
    "\"" {
        yyPushState(IN_GO_STRING);
    }

    "`" {
        yyPushState(IN_GO_RAW_STRING);
    }
}

<IN_GO_STRING> {
    "\\\"" { /* ignore escaped quotes */ }

    "\"" {
        yyPopState();
    }

   . { /* consume characters until string is terminated */ }
}

<IN_GO_RAW_STRING> {
    "`" {
        yyPopState();
    }

   [^] { /* consume characters until string is terminated */ }
}

<YYINITIAL> {
    ^ "templ" {
        yyPushState(IN_TEMPL_DECLARATION_START);
        return HTML_DECL_START;
    }

    ^ "css" {
        yyPushState(IN_CSS_DECLARATION_START);
        return CSS_DECL_START;
    }

    ^ "script" {
        yyPushState(IN_SCRIPT_DECLARATION_START);
        return SCRIPT_DECL_START;
    }

    [^] {
        return GO_ROOT_FRAGMENT;
    }
}

<IN_TEMPL_DECLARATION_START> {
    "{" $ {
        yyPushState(IN_TEMPL_DECLARATION_BODY);
        return LBRACE;
    }

    [^] {
        return DECL_GO_TOKEN;
    }
}

<IN_TEMPL_DECLARATION_BODY> {
    "<!--" {
        yyPushState(IN_HTML_COMMENT);
        return HTML_FRAGMENT;
    }

    ^ {OPTIONAL_WHITE_SPACE} "if" ~"{" $ {
        return GO_IF_START_FRAGMENT;
    }

    "}" {OPTIONAL_WHITE_SPACE} "else if" ~"{" $ {
        return GO_ELSE_IF_START_FRAGMENT;
    }

    "}" {OPTIONAL_WHITE_SPACE} "else" {OPTIONAL_WHITE_SPACE} "{" {OPTIONAL_WHITE_SPACE} $ {
        return GO_ELSE_START_FRAGMENT;
    }

    ^ {OPTIONAL_WHITE_SPACE} "switch" {WHITE_SPACE} ~"{\n" {
        return GO_SWITCH_START_FRAGMENT;
    }

    ^ {OPTIONAL_WHITE_SPACE} "case" ~":" {OPTIONAL_WHITE_SPACE} $ {
        return GO_CASE_FRAGMENT;
    }

    ^ {OPTIONAL_WHITE_SPACE} "default:" {OPTIONAL_WHITE_SPACE} $ {
        return GO_DEFAULT_FRAGMENT;
    }

    ^ {OPTIONAL_WHITE_SPACE} "for" {WHITE_SPACE} ~"{" $ {
        return GO_FOR_START_FRAGMENT;
    }

    ^ {OPTIONAL_WHITE_SPACE} "@" {
        yypushback(1);
        yyPushState(IN_COMPONENT_IMPORT);
        return HTML_FRAGMENT;
    }

    ">" {OPTIONAL_WHITE_SPACE} "@" {
        yypushback(1);
        yyPushState(IN_COMPONENT_IMPORT);
        return HTML_FRAGMENT;
    }

    "?={" {
        yypushback(1); // IN_EXPR handles brace nesting
        yyPushState(IN_BOOL_EXPR);
        return BOOL_EXPR_START;
    }

    "{" {
        yypushback(1); // IN_EXPR handles brace nesting
        yyPushState(IN_EXPR);
    }

    ^ "}" {
        yyPushState(YYINITIAL);
        return RBRACE;
    }


    "}" {
        return RBRACE;
    }

    [^] {
        return HTML_FRAGMENT;
    }
}

<IN_HTML_COMMENT> {
    "-->" {
        yyPopState();
        return HTML_FRAGMENT;
    }

    [^] {
        return HTML_FRAGMENT;
    }
}

<IN_COMPONENT_IMPORT> {
    "@" {
        return COMPONENT_IMPORT_START;
    }

    [\w\.]+ "(" {
        yypushback(1);
        yyPushState(IN_COMPONENT_IMPORT_PARAMS);
        return COMPONENT_REFERENCE;
    }

    [\w\.]+ {
        yyPushState(IN_TEMPL_DECLARATION_BODY);
        return COMPONENT_REFERENCE;
    }
}

<IN_COMPONENT_IMPORT_PARAMS> {
    "(" {
        parensNestingLevel++;
        if (parensNestingLevel == 1) {
            return LPARENTH;
        }
    }

    ")" {OPTIONAL_WHITE_SPACE} "{" {
        parensNestingLevel--;
        if (parensNestingLevel == 0) {
            yypushback(yylength());
            yyPushState(IN_COMPONENT_IMPORT_CHILDREN_BLOCK_START);
        }
    }

    ")" {
        parensNestingLevel--;
        if (parensNestingLevel == 0) {
            yypushback(1);
            yyPushState(IN_COMPONENT_IMPORT_PARAMS_END_WITHOUT_CHILDREN);
        }
    }

    [^] {
        return GO_COMPONENT_IMPORT_PARAMS;
    }
}

<IN_COMPONENT_IMPORT_PARAMS_END_WITHOUT_CHILDREN> {
    ")" {
        yyPushState(IN_TEMPL_DECLARATION_BODY);
        return RPARENTH;
    }
}

<IN_COMPONENT_IMPORT_CHILDREN_BLOCK_START> {
    ")" {
        return RPARENTH;
    }

    {WHITE_SPACE} {
        return WHITE_SPACE;
    }

    "{" {
        yyPushState(IN_TEMPL_DECLARATION_BODY);
        return LBRACE;
    }
}

<IN_EXPR> {
    "{" {
        braceNestingLevel++;
        if (braceNestingLevel == 1) {
            return LBRACE;
        }
    }

    "}" {
        braceNestingLevel--;
        if (braceNestingLevel == 0) {
            yyPushState(IN_TEMPL_DECLARATION_BODY);
            return RBRACE;
        }
    }

    [^] {
          return GO_EXPR;
     }
}

<IN_CSS_DECLARATION_START> {
    "(" {
        yypushback(1); // IN_CSS_DECLARATION_PARAMS handles parens nesting
        yyPushState(IN_CSS_DECLARATION_PARAMS);
    }

    \w+ { return CSS_CLASS_ID; }

    "{" $ {
        yyPushState(IN_CSS_DECLARATION_BODY);
        return LBRACE;
    }

    {WHITE_SPACE} {
        return WHITE_SPACE;
    }
}

<IN_CSS_DECLARATION_PARAMS> {
    "(" {
        parensNestingLevel++;
        if (parensNestingLevel == 1) {
            return LPARENTH;
        }
    }

    ")" {
        parensNestingLevel--;
        if (parensNestingLevel == 0) {
            yyPushState(IN_CSS_DECLARATION_START);
            return RPARENTH;
        }
    }

    [^] {
        return GO_CSS_DECL_PARAMS;
    }
}

<IN_CSS_DECLARATION_BODY> {
    ^ "}" {
        yyPushState(YYINITIAL);
        return RBRACE;
    }

    [^] {
        return CSS_PROPERTIES;
    }
}

<IN_SCRIPT_DECLARATION_START> {
    "{" $ {
        yyPushState(IN_SCRIPT_DECLARATION_BODY);
        return SCRIPT_FUNCTION_DECL;
    }

    [^] {
        return SCRIPT_FUNCTION_DECL;
    }
}

<IN_SCRIPT_DECLARATION_BODY> {
    ^ "}" {
        yypushback(1);
        yyPushState(YYINITIAL);
        return RBRACE;
    }

    [^] {
        return SCRIPT_BODY;
    }
}

[^] {
    yybegin(YYINITIAL);
    return BAD_CHARACTER;
}

